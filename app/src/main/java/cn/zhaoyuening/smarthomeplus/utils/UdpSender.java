package cn.zhaoyuening.smarthomeplus.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Zhao on 2016/7/31.
 */
public class UdpSender {
    private static DatagramSocket mClient;
    static {
        try {
            mClient = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送udp广播
     * @param host 需要传输的主机地址
     * @param port 需要传输的端口号
     * @param data 需要传输的数据
     */
    public static void send(String host,int port,String data){
        try {
            byte[] bytes = data.getBytes();
            DatagramPacket packet = new DatagramPacket(
                    bytes, bytes.length, InetAddress.getByName(host),port);
            //发送
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
