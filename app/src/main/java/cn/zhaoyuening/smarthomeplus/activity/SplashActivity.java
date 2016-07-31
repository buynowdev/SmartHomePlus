package cn.zhaoyuening.smarthomeplus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.zhaoyuening.smarthomeplus.R;
import cn.zhaoyuening.smarthomeplus.utils_old.HttpTools;
import cn.zhaoyuening.smarthomeplus.utils_old.MyConstants;
import cn.zhaoyuening.smarthomeplus.utils_old.SPUtils;
import cn.zhaoyuening.smarthomeplus.utils_old.StreamToools;


public class SplashActivity extends Activity {
    //tag
    private static final String TAG = "TAG_SPLASHACTIVITY";
    private static final int START_HOME_ACTIVITY = 1;
    private static final int NEED_UPDATE = 2;
    private static final int SHOW_TOAST = 3;
    private static final int INSTALL_NEW_VERSION = 4;

    private TextView tv_versionName;
    private PackageManager mPackageManager;
    private View rl_splashRoot;
    private AnimationSet mAs;
    private int mVersionCode;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_HOME_ACTIVITY:
                    startHomeActivity();
                    break;
                case NEED_UPDATE:
                    isUpdateDialog((VersionInfo) msg.obj);
                    break;
                case SHOW_TOAST:
                    Toast.makeText(SplashActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case INSTALL_NEW_VERSION:
                    installNewVersion((File)msg.obj);
                    break;
                default:
                    Toast.makeText(SplashActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    startHomeActivity();
            }
        }
    };
    private ExecutorService mEs;

    private void updateAPP() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //初始化视图
        initView();
        //初始化数据
        initData();
        //初始化动画
        initAnimation();
        //初始化事件
        initEvent();

    }

    private void initAnimation() {
        int duration = 2000;
        //旋转动画
        RotateAnimation ra = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        //设置动画持续时间

        //比例动画
        ScaleAnimation sa = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //alpha动画
        AlphaAnimation aa = new AlphaAnimation(0f, 1f);

        mAs = new AnimationSet(false);

        sa.setDuration(duration);
        ra.setDuration(duration);
        aa.setDuration(duration);

        mAs.addAnimation(ra);
        mAs.addAnimation(sa);
        mAs.addAnimation(aa);
        rl_splashRoot.startAnimation(mAs);
    }


    private void initView() {

        rl_splashRoot = findViewById(R.id.rl_splahRoot);
        tv_versionName = (TextView) findViewById(R.id.tv_versionName);


    }

    private void initData() {
        mPackageManager = getPackageManager();
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(getPackageName(),0);
            mVersionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;

            tv_versionName.setText(versionName);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        //监听动画事件
        //检查版本更新
        mAs.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                /*
                //动画开始
                boolean isUpdate= SPUtils.getBoolean(SplashActivity.this, MyConstants.KEY_AUTO_UPDATE,true);
                if (isUpdate) {
                    //更新
                    checkUpdate();
                }
                */
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束
                //是否更新
                boolean isUpdate=SPUtils.getBoolean(SplashActivity.this,MyConstants.KEY_AUTO_UPDATE,true);
//                if (isUpdate){

//                }else{
                    //进入主界面
                    Log.d(TAG, "进入主界面");
                    startHomeActivity();
//                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void startHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //检测版本更新
    //安装
    //跳转
    private void checkUpdate() {
        final long startTime = System.currentTimeMillis();
        Log.d(TAG, "更新开始");
        mEs = Executors.newCachedThreadPool();
        mEs.execute(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = START_HOME_ACTIVITY;
                Log.d(TAG,"新线程 检测更新 id:"+ Thread.currentThread().getId());
                try {
                    //得到目标的输入流
                    String updateUrl = getString(R.string.updateUrl);
                    Log.d(TAG, "检测更新地址：" + updateUrl);
                    InputStream in = HttpTools.openGetConnect(updateUrl,5000);
                    //流转字符串 得到json格式字符串
                    String versionInfo = StreamToools.getString(in);
                    //关闭流
                    in.close();
                    //解析Json 得到版本信息
                    VersionInfo info = parseJSON(versionInfo);
                    Log.d(TAG, info.toString());

                    if (mVersionCode == info.versioncode) {
                        Log.d(TAG,"版本不需要更新");

                    }
                    if (mVersionCode < info.versioncode) {
                        Log.d(TAG, "版本需要更新");
                        msg.what = NEED_UPDATE;
                        msg.obj = info;
                        //询问用户是否需要更新
                        //是->下载新客户端 安装
                        //否->进入主界面
                    }
                    Log.d(TAG,versionInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    //延时操作
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 3000) {
                        //动画没播完
                        SystemClock.sleep(3000 - (endTime - startTime));
                    }
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    private void isUpdateDialog(final VersionInfo info) {
        //是否更新对话框
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("是否更新");
        ab.setMessage("有新版本\n" + info.desc);
        ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk
                downloadNewApk(info.url);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //跳转到主界面
                startHomeActivity();
            }
        }).setCancelable(false).show();

    }

    private void downloadNewApk(final String downloadUrl){
        //TODO 下载中网络中断异常 不进入主界面
        //下载apk
        mEs.execute(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = SHOW_TOAST;
                msg.obj = "新版本开始下载 请稍后";
                mHandler.sendMessage(msg);
                Log.d(TAG,"下载新版本 APP开始");
                try {
                    File savePath = HttpTools.downloadFile(downloadUrl, SplashActivity.this.getExternalCacheDir().getPath()  ,"new.apk",5000);
                    msg = Message.obtain();
                    msg.what = SHOW_TOAST;
                    msg.obj = "下载完成 即将为您安装";
                    mHandler.sendMessage(msg);
                    Log.d(TAG,"下载新版本成功");
                    msg= Message.obtain();
                    msg.what = INSTALL_NEW_VERSION;
                    msg.obj = savePath;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    msg = Message.obtain();
                    msg.obj = "下载最新版本失败 请检查网络";
                    mHandler.sendMessage(msg);
                }

            }
        });

    }

    private void installNewVersion(File savePath) {
        //安装下载好的新版本apk
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(savePath), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //版本信息
    private class VersionInfo{
        String desc;
        int versioncode;
        String versionname;
        String url;

        @Override
        public String toString() {
            return desc+" "+versionname+" "+versioncode+" "+url;
        }
    }

    private VersionInfo parseJSON(String versionInfo) throws JSONException {
        JSONObject json = new JSONObject(versionInfo);
        String desc = json.getString("desc");
        String versionname = json.getString("versionname");
        int versioncode = json.getInt("versioncode");
        String url = json.getString("url");

        VersionInfo  info = new VersionInfo();
        info.desc=desc;
        info.url=url;
        info.versioncode = versioncode;
        info.versionname=versionname;

        return info;
    }
}