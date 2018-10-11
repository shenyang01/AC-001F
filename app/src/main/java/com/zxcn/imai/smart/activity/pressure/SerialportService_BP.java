/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxcn.imai.smart.activity.pressure;

import android.serialport.SerialPort;
import android.util.Log;

import com.zxcn.imai.smart.base.SmartApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

/**
 * SerialportService class
 *
 * @author huangyuan
 * @data 2017/12/20
 */
public abstract class SerialportService_BP {

    /**
     * log打印开关
     */
    private final boolean DEBUG = false;

    private static final String TAG = "8888_BGlucoseService";

    public SmartApplication mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    private int bufferlength = 60;

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (DEBUG) {
                Log.v(TAG, " SerialportService ReadThread run");
            }

            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[bufferlength];
                    if (mInputStream == null) {
                        return;
                    }

                    if (DEBUG) {
                        Log.v(TAG, " SerialportService ReadThread read buffer");
                    }
                    //读取数据
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        if (DEBUG) {
                            Log.v(TAG, " SerialportService ReadThread onDataReceived");
                        }
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public void start(SmartApplication application) {
        mApplication = application;
        if (DEBUG) {
            Log.v(TAG, " SerialportService start");
        }

        try {
            mSerialPort = mApplication.getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            /* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            Log.v(TAG, "You do not have read/write permission to the serial port.");
        } catch (IOException e) {
            Log.v(TAG, "he serial port can not be opened for an unknown reason.");
        } catch (InvalidParameterException e) {
            Log.v(TAG, "Please configure your serial port first.");

        }
    }

    protected abstract void onDataReceived(final byte[] buffer, final int size);

    public void stop() {
        if (mApplication == null)
            return;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        mApplication.closeSerialPort();
        mSerialPort = null;

    }

    /**
     * 设置读串口数据长度
     */
    public void setBufferLength(int mLength) {
        bufferlength = mLength;
    }
}
