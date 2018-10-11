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

package com.zxcn.imai.smart.core.bglucose;

import android.serialport.SerialPort;
import android.util.Log;


import com.zxcn.imai.smart.SmartApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

public abstract class BGlucoseService {

    private static final String TAG = "8888_BGlucoseService";

    protected SmartApplication mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();

            Log.v(TAG, " BGlucoseService ReadThread run");

            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[16];
                    if (mInputStream == null) return;

                    Log.v(TAG, " BGlucoseService ReadThread read buffer");
                    size = mInputStream.read(buffer);  //读取数据
                    if (size > 0) {
                        Log.v(TAG, " BGlucoseService ReadThread onDataReceived");
                        onDataReceived(buffer, size);  //处理数据
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

        Log.v(TAG, " BGlucoseService start");

        try {
            mSerialPort = mApplication.getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            Log.v(TAG,"You do not have read/write permission to the serial port.");
        } catch (IOException e) {
            Log.v(TAG,"he serial port can not be opened for an unknown reason.");
        } catch (InvalidParameterException e) {
            Log.v(TAG,"Please configure your serial port first.");

        }
    }

    protected abstract void onDataReceived(final byte[] buffer, final int size);

    public void stop() {
        if (mReadThread != null) mReadThread.interrupt();
        mApplication.closeSerialPort();
        mSerialPort = null;

    }

}
