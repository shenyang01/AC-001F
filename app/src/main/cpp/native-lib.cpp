#include <stdio.h>   /* Standard input/output definitions */
#include <string.h>  /* String function definitions */
#include <unistd.h>  /* UNIX standard function definitions */
#include <fcntl.h>   /* File control definitions */
#include <errno.h>   /* Error number definitions */
#include <termios.h> /* POSIX terminal control definitions */
#include <time.h>
#include <pthread.h>
#include <stdlib.h>
#include <signal.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <sys/epoll.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <ctype.h>
#include <dirent.h>
#include <pthread.h>

#include <android/log.h>

#include <jni.h>
#include <string>
#include <fcntl.h>
#include <unistd.h>
#include <android/log.h>
#include <termios.h>


#define LOG_TAG "JNI_LOG_TAG"
#define LOGD(...)__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGI(...)__android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGV(...)__android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)


static int serial_fd;

//#define SERIAL_DEV     	"/dev/ttymxc2"
#define SERIAL_DEV        "/dev/ttyMT2"
#define WRITE_BUFF_SIZE        32
#define READ_BUFF_SIZE        32

static int write_int(char const *path, int value) {
    int fd;

    if (path == NULL)
        return -1;

    fd = open(path, O_RDWR);
    if (fd >= 0) {
        char buffer[20];
        int bytes = sprintf(buffer, "%d\n", value);
        int amt = write(fd, buffer, bytes);
        close(fd);
        return amt == -1 ? -errno : 0;
    }

    LOGI("write_int failed to open %s\n", path);
    return -errno;
}


typedef struct __baudrate_mpping {
    unsigned int ul_baud_rate;
    speed_t linux_baud_rate;
} BAUD_RATE_SETTING;

static BAUD_RATE_SETTING speeds_mapping[] = {
        {0,       B0},
        {50,      B50},
        {75,      B75},
        {110,     B110},
        {134,     B134,},
        {150,     B150},
        {200,     B200},
        {300,     B300},
        {600,     B600},
        {1200,    B1200},
        {1800,    B1800},
        {2400,    B2400},
        {4800,    B4800},
        {9600,    B9600},
        {19200,   B19200},
        {38400,   B38400},
        {57600,   B57600},
        {115200,  B115200},
        {230400,  B230400},
        {460800,  B460800},
        {500000,  B500000},
        {576000,  B576000},
        {921600,  B921600},
        {1000000, B1000000},
        {1152000, B1152000},
        {1500000, B1500000},
        {2000000, B2000000},
        {2500000, B2500000},
        {3000000, B3000000},
        {3500000, B3500000},
        {4000000, B4000000},
};


static speed_t get_speed(unsigned int baudrate) {
    unsigned int idx;
    for (idx = 0; idx < sizeof(speeds_mapping) / sizeof(speeds_mapping[0]); idx++) {
        if (baudrate == (unsigned int) speeds_mapping[idx].ul_baud_rate) {
            return speeds_mapping[idx].linux_baud_rate;
        }
    }
    return CBAUDEX;
}

int SERIAL_Close(void) {
    int err;

    //SERIAL_PowerOn(0);
    err = close(serial_fd);
    LOGI("SERIAL_Close() re=%d\n", err);
    serial_fd = NULL;
    return err;
}

int SERIAL_Open(unsigned int new_baudrate, int length, char parity_c, int stopbits) {
    short err;
    pid_t pid;
    int portno;
    //struct sockaddr_in serv_addr;
    //struct hostent *server;

    struct termios termOptions;

    unsigned char buf[READ_BUFF_SIZE] = {0};
    int nRead = 0, nWrite = 0;
    speed_t speed;


    LOGI("SERIAL_Open() start\n");
    LOGI("SERIAL_Open: new_baudrate=%d,\n", new_baudrate);
    LOGI("SERIAL_Open: length=%d,\n", length);
    LOGI("SERIAL_Open: parity_c=%c,\n", parity_c);
    LOGI("SERIAL_Open: stopbits=%d,\n", stopbits);

    //err=SERIAL_Close();

    // power on SERIAL chip
    //SERIAL_PowerOn(1);

    // check whether SERIAL chip is alive or not
    serial_fd = open(SERIAL_DEV, O_RDWR | O_NOCTTY | O_NONBLOCK);
    if (serial_fd < 0) {
        LOGI("SERIAL_Open: Unable to open - %s, serial_fd=%d\n", SERIAL_DEV, serial_fd);
        /*the process should exit if fail to open UART device*/
        return (-7);
    } else {
        LOGI("SERIAL_Open: open - %s, serial_fd=%d\n", SERIAL_DEV, serial_fd);


#if 1

        fcntl(serial_fd, F_SETFL, 0);
        speed = get_speed(new_baudrate);

        // Get the current options:
        err = tcgetattr(serial_fd, &termOptions);

        // Set 8bit data, No parity, stop 1 bit (8N1):
        termOptions.c_cflag &= ~PARENB;
        termOptions.c_cflag &= ~CSTOPB;
        termOptions.c_cflag &= ~CSIZE;
        termOptions.c_cflag |= CS8 | CLOCAL | CREAD;

        LOGI("SERIAL_Open: c_lflag=%x,c_iflag=%x,c_oflag=%x,err:%d\n", termOptions.c_lflag,
             termOptions.c_iflag,
             termOptions.c_oflag, err);
        //termOptions.c_lflag



        // Raw mode
        termOptions.c_iflag &= ~(INLCR | ICRNL | IXON | IXOFF | IXANY);
        termOptions.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);  /*raw input*/
        termOptions.c_oflag &= ~OPOST;  /*raw output*/

        tcflush(serial_fd, TCIFLUSH);//clear input buffer
        termOptions.c_cc[VTIME] = 0; /* inter-character timer unused */
        termOptions.c_cc[VMIN] = 0; /* blocking read until 0 character arrives */

        // Set baudrate
        cfsetispeed(&termOptions, speed);
        cfsetospeed(&termOptions, speed);

        /*
        * Set the new options for the port...
        */


        switch (length) /*设置数据位数*/ {
            case 5:
                termOptions.c_cflag |= CS5;
                break;
            case 6:
                termOptions.c_cflag |= CS6;
                break;
            case 7:
                termOptions.c_cflag |= CS7;
                break;
            case 8:
                termOptions.c_cflag |= CS8;
                break;
        }

        switch (parity_c) {
            case 'n':
            case 'N':
                termOptions.c_cflag &= ~PARENB;   /* Clear parity enable */
                termOptions.c_iflag &= ~INPCK;     /* Enable parity checking */
                break;
            case 'o':
            case 'O':
                termOptions.c_cflag |= PARENB;     /* Enable parity */
                termOptions.c_cflag |= PARODD;     /* 设置为奇效验*/
                termOptions.c_iflag |= INPCK;             /* Disnable parity checking */
                break;
            case 'e':
            case 'E':
                termOptions.c_cflag |= PARENB;     /* Enable parity */
                termOptions.c_cflag &= ~PARODD;   /* 转换为偶效验*/
                termOptions.c_iflag |= INPCK;       /* Disnable parity checking */
                break;
        }

        /* 设置停止位*/
        switch (stopbits) {
            case 1:
                termOptions.c_cflag &= ~CSTOPB;
                break;
            case 2:
                termOptions.c_cflag |= CSTOPB;
                break;
        }


        err = tcsetattr(serial_fd, TCSANOW, &termOptions);

        LOGI("tcsetattr err:%d", err);

#endif
    }
    usleep(5000);

    //nRead = read(serial_fd, buf, sizeof(buf));

    LOGI("nRead = %d\n", nRead);

    LOGI("SERIAL_Open() end\n");

    return serial_fd;
}

/*
void *SERIAL_read_thread(void *data)
{
    int nRead = 0, nWrite = 0;
    unsigned char tempbuf[READ_BUFF_SIZE] = {0};
    extern int thd_status ;


    if(serial_fd>0)
    {

        while(thd_status)
        {
            memset(tempbuf,0,sizeof(tempbuf));
            nRead = read(serial_fd, tempbuf, sizeof(tempbuf));

            LOGI("Thread nRead = %d\n", nRead);
            if(nRead > 0)
            {
                int i;

                if(nRead > 0)
                {
                    int i;

                    for(i = 0; i < nRead; )
                    {
                        LOGI("read data :%02x %02x %02x %02x %02x %02x %02x %02x", tempbuf[i], tempbuf[i+1], tempbuf[i+2], tempbuf[i+3], tempbuf[i+4], tempbuf[i+5], tempbuf[i+6],tempbuf[i+7]);
                        i+=8;
                    }
                }
            }
            usleep(1000000);
        }

    }
    else
    {
        LOGI("SERIAL_read_thread: illegal serial_fd=%d\n", serial_fd);

    }
    pthread_exit(NULL);

    return NULL;
}
*/
int SERIAL_read(unsigned char *buf) {
    int nRead = 0;
    int validLen = 0;
    unsigned char tempbuf[READ_BUFF_SIZE] = {0};

//char strInfo[ECHO_SENSOR_INFO_MAX_SIZE]= {0};

    LOGI("SERIAL_read\n");

    serial_fd = open(SERIAL_DEV, O_RDWR | O_NOCTTY | O_NONBLOCK);
    if (serial_fd < 0) {
        LOGI("SERIAL_Open: Unable to open - %s, serial_fd=%d\n", SERIAL_DEV, serial_fd);
        /*the process should exit if fail to open UART device*/
        return (-7);
    } else {
        LOGI("SERIAL_Open: open - %s, serial_fd=%d\n", SERIAL_DEV, serial_fd);
    }

    if (serial_fd > 0) {
        memset(tempbuf, 0, sizeof(tempbuf));

        LOGI("SERIAL_read1\n");
        nRead = read(serial_fd, tempbuf, sizeof(tempbuf));
        LOGI("SERIAL_read2 nRead：%d\n", nRead);

        memcpy(buf, tempbuf, READ_BUFF_SIZE);

        LOGI("SERIAL_read3\n");

        LOGI("nRead = %d\n", nRead);

        if (nRead > 0) {
            int i;

            for (i = 0; i < nRead; i++) {
                LOGI("nRead buf=%02x ", buf[i]);
            }

            LOGI("\n");
        }

    } else {
        LOGI("SERIAL_read: illegal serial_fd=%d\n", serial_fd);

    }

    if (serial_fd > 0)close(serial_fd);

    return nRead;

}


int SERIAL_write(unsigned char *buf, int len) {
    int nWrite = 0;

    serial_fd = open(SERIAL_DEV, O_RDWR | O_NOCTTY | O_NONBLOCK);
    if (serial_fd < 0) {
        LOGI("SERIAL_Open: Unable to open - %s, serial_fd=%d\n", SERIAL_DEV, serial_fd);
        /*the process should exit if fail to open UART device*/
        return (-7);
    } else {
        LOGI("SERIAL_Open: open - %s, serial_fd=%d\n", SERIAL_DEV, serial_fd);
    }

    if (serial_fd > 0) {

        nWrite = write(serial_fd, buf, len);

        LOGI("nWrite = %d\n", nWrite);
        if (nWrite > 0) {
            int i;

            for (i = 0; i < nWrite; i++) {
                LOGI("nWrite buf=%02x ", buf[i]);
            }

            LOGI("\n");
        }

        if (serial_fd > 0)close(serial_fd);

    } else {
        LOGI("SERIAL_write: illegal serial_fd=%d\n", serial_fd);

    }
    return nWrite;
}




/*
 * Class:     com_topeet_ledtest_led
 * Method:    Open
 * Signature: ()I
 */


extern "C"
//JNIEXPORT jint JNICALL
//Java_com_zxcn_hello_PressureManager_open
//        (JNIEnv *env, jobject /* this */
//) {
//  if (fd <= 0)fd = open("/dev/ttymxc2", O_RDWR | O_NDELAY | O_NOCTTY);

//   LOGV("Pressure device is opening");
//   if (fd <= 0) {
//       LOGV("Pressure device open error");
//       return JNI_FALSE;

//  } else {
//       LOGV("Pressure device open /dev/ttymxc2 Sucess fd = %d", fd);
//       set_opt(fd, 115200, 8, 'N', 1);
//      return JNI_TRUE;
//  }
//}
//

//extern "C"
JNIEXPORT jint JNICALL
Java_com_zxcn_imai_smart_core_pressure_PressureManager_open
        (JNIEnv *env, jobject /* this */) {

    LOGV("device is opening111 fd:%d");

    int temp = 1;

    return SERIAL_Open(115200, 8, 'N', 1); //9.25



    // return temp;  //9.25
}

/*
* Class:     com_topeet_ledtest_led
* Method:    Close
* Signature: ()I
*/
extern "C"
JNIEXPORT int JNICALL Java_com_zxcn_imai_smart_core_pressure_PressureManager_close
        (JNIEnv *env, jclass thiz) {
    LOGV("Pressure device is closing");
    if (serial_fd > 0) {
        LOGV("Pressure device close success:%d\n", serial_fd);
        return close(serial_fd);
    } else {
        LOGV("Pressure device close fail");
        return -1;
    }
}



extern "C"

#if 0

JNIEXPORT jint JNICALL Java_com_example_administrator_ndk_PressureManager_readCmd
        (JNIEnv *env, jobject param)
{
    int ret = 0;
    unsigned char buf[READ_BUFF_SIZE]={0};
    int len = 0;

    LOGV("SERIAL_read_Native ENTRY.");

    jclass cls=env->GetObjectClass(param);

    if (cls == NULL) {
        LOGV("cls == NULL.");
    }

    jfieldID fid = env->GetFieldID(cls, "rd_data", "[I");

    if (fid == NULL) {
        LOGV("fid == NULL.");
    }

    len=SERIAL_read(buf);
    if(len > 0 && len <= READ_BUFF_SIZE)
    {
        jintArray tempArray = env->NewIntArray(READ_BUFF_SIZE); //新建一个本地的 tempArray
        jint jdata ;
        for(int i=0;i<READ_BUFF_SIZE;i++)
        {
            if(i <= len )
                jdata = buf[i];
            else
                jdata = 0 ;

            env->SetIntArrayRegion(tempArray, i, 1, &jdata);
        }



        env->SetObjectField(param, fid, tempArray);//把这个 tempArray 设置为 fid 的值
        env->DeleteLocalRef(tempArray);//删除本地引用tempArray
        tempArray = NULL;
    }

    LOGV("SERIAL_read_Native SUCCESS.");
    return len;
}

#else

JNIEXPORT jintArray JNICALL Java_com_zxcn_imai_smart_core_pressure_PressureManager_readCmd
        (JNIEnv *env, jclass object, jintArray j_array) {
    //1. 获取数组指针和长度
    jint *c_array = env->GetIntArrayElements(j_array, 0);
    int len_arr = env->GetArrayLength(j_array);

    LOGI("Java_com_example_administrator_ndk_PressureManager_readCmd length:%d", len_arr);

    len_arr = 20;

    //2. 具体处理
    jintArray c_result = env->NewIntArray(len_arr);
    jint buf[len_arr];

    unsigned char read_data[READ_BUFF_SIZE] = {0};

    int rd_length = -1;

    rd_length = SERIAL_read(read_data);

    LOGI("serial read rd_length:%d\n", rd_length);

    LOGI("serial read rd_length  read_data[0]:0x%x,read_data[1]:0x%x,read_data[2]:0x%x,read_data[3]:0x%x,read_data[4]:0x%x,read_data[5]:0x%x,read_data[6]:0x%x,read_data[7]:0x%x\n", read_data[0],
         read_data[1],read_data[2],read_data[3],read_data[4],read_data[5],read_data[6],read_data[7]);




    for (int i = 0; i < rd_length; i++) {
        //buf[i] = c_array[i] + 1;
        //buf[i]=read_data[i];
        buf[i]=read_data[i];
        LOGI("zyp_buf[%d]: %x", i, buf[i]);
    }

    //3. 释放内存
    env->ReleaseIntArrayElements(j_array, c_array, 0);

    //4. 赋值
    env->SetIntArrayRegion(c_result, 0, len_arr, buf);
    return c_result;
}

#endif

extern "C"
JNIEXPORT jintArray JNICALL Java_com_zxcn_imai_smart_core_pressure_PressureManager_writeCmd
        (JNIEnv *env, jclass object, jint len,jintArray j_array) {
        //(JNIEnv *env, jobject param, jint len) {
    int i, ret = 0;

    jintArray c_result = env->NewIntArray(len);

    jint buf[len];

    unsigned char write_data[WRITE_BUFF_SIZE] = {0};

    env->GetIntArrayRegion(j_array, 0, len, buf);

    for (i = 0; i < len; i++) {
        LOGI("Java_com_zxcn_bpressure_PressureManager_writeCmd buf=%02x ", buf[i]);
        write_data[i] = buf[i];
    }

    ret = SERIAL_write(write_data, len);

    LOGV("SERIAL_write_Native SUCCESS. ret:%d", ret);

    return c_result;
}


//extern "C"
//JNIEXPORT jint JNICALL Java_com_zxcn_bpressure_PressureManager_writeCmd
//        (JNIEnv *env, jobject param, jint len) {
//    int i, ret = 0;
//    unsigned char buf[WRITE_BUFF_SIZE] = {0};
//    jint jDATA[WRITE_BUFF_SIZE];
//
//    jclass cls = env->GetObjectClass(param);
//
//    jfieldID fid = env->GetFieldID(cls, "wr_data", "I");
//
//    memset(buf, 0, sizeof(buf));
//    if (len > 0 && len <= WRITE_BUFF_SIZE) {
//        jintArray tempArray = (jintArray) env->GetObjectField(param, fid);
//        env->GetIntArrayRegion(tempArray, 0, len, jDATA);
//
//        LOGV("Java_com_example_administrator_ndk_PressureManager_writeCmd buf:");
//
//        for (i = 0; i < len; i++) {
//            buf[i] = jDATA[i];
//            LOGV("writeCmd : 0x%x ", buf[i]);
//        }
//    } else {
//        LOGV("Java_com_zxcn_ndk_PressureManager_write ERROR!!.");
//        return 0;
//    }
//
//    LOGV("\n");
//
//    ret = SERIAL_write(buf, len);
//
//    LOGV("SERIAL_write_Native SUCCESS. ret:%d", ret);
//
//    return ret;
//}
