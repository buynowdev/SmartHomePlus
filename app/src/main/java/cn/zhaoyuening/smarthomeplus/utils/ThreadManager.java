package cn.zhaoyuening.smarthomeplus.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Zhao on 2016/7/31.
 * 线程管理
 */
public class ThreadManager  {
    private static Executor sExecutor = Executors.newCachedThreadPool();
    public static void excute(Runnable runnable){
        sExecutor.execute(runnable);
    }

}
