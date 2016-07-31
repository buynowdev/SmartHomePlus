package cn.zhaoyuening.smarthomeplus.utils_old;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Zhao on 2016/7/18.
 *
 */
public class GetMyDatagramSocket {
    private static DatagramSocket socket ;
    public static DatagramSocket getSocket(){
        if (socket == null) {
            try {
                socket = new DatagramSocket(8001);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }
}
