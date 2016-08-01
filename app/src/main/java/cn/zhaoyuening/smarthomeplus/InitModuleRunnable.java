package cn.zhaoyuening.smarthomeplus;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cn.zhaoyuening.smarthomeplus.utils_old.TCPConnect;

/**
 * Created by Zhao on 2016/7/31.
 * 初始化模块
 * 通过tcp传递模块需要连接wifi的wifi名与密码
 */
public class InitModuleRunnable implements Runnable {
    private String mHost;
    private int mPort;
    private String mSn;
    private String mWifiName;
    private String mWifiPassword;

    public InitModuleRunnable(String host, int port, String sn, String wifiName, String wifiPassword) {
        mHost = host;
        mPort = port;
        mSn = sn;
        mWifiName = wifiName;
        mWifiPassword = wifiPassword;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(mHost,mPort);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            //发送给模块的命令
            //TODO 初始化命令格式
            String initCmd = "wifi_set=AT+CWJAP=\""+mWifiName+"\",\""+mWifiPassword+"\"&";
            //发送
            writer.print(initCmd);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
