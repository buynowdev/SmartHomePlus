package cn.zhaoyuening.smarthomeplus.utils_old;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Zhao on 2016/7/8.
 */
public class TCPRunnable implements Runnable {
    private static String TAG = "TAG_TCPRunnable";

    private String wifiName;
    private String wifiPassword;
    private String moduleSN;
    private String MODULE_IP;
    private int MODULE_PORT;

    private static Socket mSocket;

    public TCPRunnable(String wifiName, String wifiPassword, String moduleSN, String MODULE_IP, int MODULE_PORT) {
        this.wifiName = wifiName;
        this.wifiPassword = wifiPassword;
        this.moduleSN = moduleSN;
        this.MODULE_IP = MODULE_IP;
        this.MODULE_PORT = MODULE_PORT;
    }

    @Override
    public void run() {
        //通过tcp连接模块
        try {
            //通过tcp 传递给模块
            if (mSocket==null){
                mSocket = new Socket(MODULE_IP, MODULE_PORT);
            }
            if (mSocket.isClosed()){
                mSocket.connect(new InetSocketAddress(MODULE_IP,MODULE_PORT));
            }
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            mSocket.getOutputStream())), true);
            InputStream in = mSocket.getInputStream();
            //从模块读取的初始化反馈信息 OK OR FAIL
            //TODO wifi名 wifi密码 sn码传递格式
            out.print("wifi_set=AT+CWJAP=\""+wifiName+"\",\""+wifiPassword+"\"&");
//            out.print(moduleSN);
            out.flush();
            byte[] buffer = new  byte[1024];
            int len;
            while((len=in.read(buffer))>0){
                String str = new String(buffer,0,len);
                Log.d(TAG,"msg:"+str);
                if (str.contains("ok")){
                    //初始化成功
                    Log.d(TAG,"初始化模块成功");
                    mSocket.close();
                    mSocket = null;
                    in.close();
                    out.close();
                    return;
                }else{
                    //初始化失败
                    Log.d(TAG,"初始化模块失败");
                    mSocket.close();
                    in.close();
                    out.close();
                    return ;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
