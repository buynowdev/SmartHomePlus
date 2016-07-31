package cn.zhaoyuening.smarthomeplus.utils_old;

/**
 * Created by Zhao on 2016/7/15.
 * byte数组合成
 */
public class ByteArrayGroup {
    public static byte[] group(byte[] bytes1,byte[] bytes2){
        byte[] bytes = new byte[bytes1.length + bytes2.length];
        int i=0;
        for(;i<bytes1.length;i++){
            bytes[i]=bytes1[i];
        }
        int d=i;
        for (;i<bytes.length;i++){
            bytes[i]=bytes2[i-d];
        }
        return bytes;
    }
}
