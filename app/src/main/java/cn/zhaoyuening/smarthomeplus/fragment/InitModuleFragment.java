package cn.zhaoyuening.smarthomeplus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import cn.zhaoyuening.smarthomeplus.InitModuleRunnable;
import cn.zhaoyuening.smarthomeplus.R;
import cn.zhaoyuening.smarthomeplus.utils.MyInfoSet;
import cn.zhaoyuening.smarthomeplus.utils.ThreadManager;

/**
 * Created by Zhao on 2016/7/31.\
 * 初始化模块
 */
public class InitModuleFragment extends Fragment {
    private EditText et_wifiName;
    private EditText et_wifiPassword;
    private EditText et_sn;
    private CircularProgressButton bt_initModule;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragm_init_module, null);

        //初始化数据
        initData();
        //初始化事件
        initEvent();
        return mView;
    }

    private void initEvent() {
        //点击初始化按钮
        //初始化开始
        bt_initModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "正在初始化，请稍后", Toast.LENGTH_SHORT).show();
                Log.d(InitModuleFragment.this.getClass().getSimpleName(),"点击初始化按钮——初始化开始");

                //获取编辑框的信息
                final String wifiName = et_wifiName.getText().toString().trim();
                final String wifiPassword = et_wifiPassword.getText().toString();
                final String sn = et_sn.getText().toString();
                Runnable runnable = new InitModuleRunnable(
                        MyInfoSet.INITMODULE_IP,MyInfoSet.INITMODULE_PORT,sn,wifiName,wifiPassword);
                ThreadManager.excute(runnable);
                //模块初始化成功
                InitModuleSuccess();
            }
        });
    }

    private void InitModuleSuccess() {
        ThreadManager.excute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    bt_initModule.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "初始化模块成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData() {
        et_wifiName = (EditText) mView.findViewById(R.id.et_wifiName);
        et_wifiPassword = (EditText) mView.findViewById(R.id.et_wifiPassword);
        et_sn = (EditText) mView.findViewById(R.id.et_sn);
        bt_initModule = (CircularProgressButton) mView.findViewById(R.id.bt_initDevice);
    }
}
