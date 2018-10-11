package com.zxcn.imai.smart.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/9/14.
 */
public class ShellExe {
    private static StringBuilder sb = new StringBuilder("");
    public static String getOutput()
    {
        return sb.toString();
    }
    public static void execCommand(String[] command) throws IOException {

        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

        // read the ls output
        //sb = new StringBuilder("");
        sb.delete(0, sb.length());
        String ERROR = "ERROR";
        try {
            if (proc.waitFor() != 0) {
                Log.i("MTK","exit value = " + proc.exitValue());
                sb.append(ERROR).append(proc.exitValue());
            }
            else
            {
                String line;
                //one line has not CR, or  "line1 CR line2 CR line3..."
                line = bufferedreader.readLine();
                if(line != null)
                {
                    sb.append(line);
                }
                else
                {
                    return;
                }
                while(true)
                {
                    line = bufferedreader.readLine();
                    if(line == null)
                    {
                        break;
                    }
                    else
                    {
                        sb.append('\n');
                        sb.append(line);
                    }
                }
            }
        } catch (InterruptedException e) {
            Log.i("MTK","exe fail " + e.toString());
            sb.append(ERROR).append(e.toString());
        }
    }
}


