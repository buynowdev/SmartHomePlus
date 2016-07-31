package cn.zhaoyuening.smarthomeplus.utils_old;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Zhao on 2016/7/15.
 *
 */
public class UDPRunnable implements Runnable {
    private String mIP;
    private int mPort;
    String str;
    private static DatagramSocket mClient;

    public UDPRunnable(String mIP, int mPort, String str) {
        this.mIP = mIP;
        this.mPort = mPort;
        this.str = str;
    }

    @Override
    public void run() {
        try {
            if (mClient==null){
                mClient = new DatagramSocket(8999);
            }
            DatagramPacket packet = new DatagramPacket(str.getBytes(),str.getBytes().length, InetAddress.getByName(mIP),8002);
            mClient.send(packet);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
