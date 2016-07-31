package cn.zhaoyuening.smarthomeplus.utils_old;

/**
 * Created by Zhao on 2016/7/17.
 */
public class CmdSender {
    public static void sendCmd(String str){
        String cmd = "yaokong_key="+str+"&";
        new Thread(new UDPRunnable("255.255.255.255",8002,cmd)).start();
    }
    public static String fromStringCmd(String str){
        if (str.length()==2){
            return str;
        }else if (str.length()==1){
            return "0"+str;
        }else if (str.length()==0){
            return "00";
        }
        return str.substring(0,2);
    }
    public static void sendCustomCmd(String cmd){
        new Thread(new UDPRunnable("255.255.255.255",8002,cmd)).start();
    }
}
