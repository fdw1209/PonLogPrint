package com.changhong.pontest.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.pontest.R;
import com.changhong.pontest.activity.TelnetOutputActivity;
import com.changhong.pontest.adapter.OnResponseListener;
import com.changhong.pontest.utils.HttpUtils;
import com.changhong.pontest.utils.TelnetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Telnet连接模块
 *
 * @author fdw
 * Created by fdw on 2018/8/3
 */
public class TelnetFragment extends Fragment {

    private TextView text_connect;
    private Context mContext;
    private LinearLayout lay_olt, lay_rms, lay_plugin, lay_web;
    private boolean ENABLE_TELNET = false;//Telnet是否启用标识
    private TelnetUtils telnetUtils;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_telnet, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * 直接在Main Thread 进行网络操作的方法
         */
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        //初始化控件
        initView();
        //打开Telnet
        OpenTelnet();
        //Log问题调试命令选项
        LogDebug();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        text_connect = getActivity().findViewById(R.id.telnet_connect);
        lay_olt = getActivity().findViewById(R.id.olt_register);
        lay_rms = getActivity().findViewById(R.id.rms_platform);
        lay_plugin = getActivity().findViewById(R.id.plugin_platform);
        lay_web = getActivity().findViewById(R.id.web_config);
        telnetUtils = new TelnetUtils();
        mContext = getContext();
    }

    /**
     * 打开Telnet开关
     */
    private void OpenTelnet() {

        text_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWifiOpened() || !isWifiConnected()) {
                    //网络提示对话框
                    showNetDialog();
                } else {
                    //显示Telnet对话框
                    showTelnetDialog();
                    //showTelnetLoginDialog();
                }
            }
        });
    }

    /**
     * Log问题调试命令选项
     */
    private void LogDebug() {
        /**
         * OLT注册
         */
        lay_olt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ENABLE_TELNET) {
                    loadingDialog();
                    Intent intent = new Intent(mContext, TelnetOutputActivity.class);
                    intent.putExtra("LOG_TYPE", "OLTRegister");
                    startActivity(intent);//startActivity(intent)实现跳转
                } else {
                    showNetDialog();
                }
            }
        });
        /**
         * RMS平台
         */
        lay_rms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ENABLE_TELNET) {
                    loadingDialog();
                    Intent intent = new Intent(mContext, TelnetOutputActivity.class);
                    intent.putExtra("LOG_TYPE", "RMSPlatForm");
                    startActivity(intent);//startActivity(intent)实现跳转
                } else {
                    showNetDialog();
                }
            }
        });
        /**
         * 插件平台
         */
        lay_plugin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ENABLE_TELNET) {
                    loadingDialog();
                    Intent intent = new Intent(mContext, TelnetOutputActivity.class);
                    intent.putExtra("LOG_TYPE", "PluginPlatForm");
                    startActivity(intent);//startActivity(intent)实现跳转
                } else {
                    showNetDialog();
                }
            }
        });
        /**
         * WEB管理
         */
        lay_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ENABLE_TELNET) {
                    loadingDialog();
                    Intent intent = new Intent(mContext, TelnetOutputActivity.class);
                    intent.putExtra("LOG_TYPE", "WebConfig");
                    startActivity(intent);//startActivity(intent)实现跳转
                } else {
                    showNetDialog();
                }
            }
        });
    }

    /**
     * 加载等待弹窗
     */
    private void loadingDialog() {
        LoadingDialog.Builder builder1 = new LoadingDialog.Builder(mContext)
                .setMessage("加载中...")
                .setCancelable(false);
        final LoadingDialog dialog1 = builder1.create();
        dialog1.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog1.dismiss();
            }
        }, 2000);
    }

    /**
     * 显示连接Telnet对话框
     */
    private void showTelnetDialog() {
        //初始化布局文件
        View dialogView = View.inflate(mContext, R.layout.dialog_telnet_session, null);
        AlertDialog.Builder mMessageBuilder = new AlertDialog.Builder(mContext);
        final AlertDialog mDialog = mMessageBuilder.create();
        mDialog.setView(dialogView);
        mDialog.setCanceledOnTouchOutside(false);//点击屏幕不消失
        mDialog.show();
        //确定按钮
        TextView tvConfirm = dialogView.findViewById(R.id.telnet_add_ok);
        //取消按钮
        TextView tvCancel = dialogView.findViewById(R.id.telnet_add_cancle);

        //确定和取消按钮监听事件
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启用Telnet
                EnableTelnet();
                loadingDialog();
                mDialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    /**
     * 显示连接Telnet登录框
     */
    private void showTelnetLoginDialog() {
        //初始化布局文件
        View dialogView = View.inflate(mContext, R.layout.dialog_telnet_login, null);
        AlertDialog.Builder mMessageBuilder = new AlertDialog.Builder(mContext);
        final AlertDialog mDialog = mMessageBuilder.create();
        mDialog.setView(dialogView);
        mDialog.setCanceledOnTouchOutside(false);//点击屏幕不消失
        mDialog.show();
        //确定按钮
        TextView tvConfirm = dialogView.findViewById(R.id.telnet_add_ok);
        //取消按钮
        TextView tvCancel = dialogView.findViewById(R.id.telnet_add_cancle);
        final EditText etUsername = dialogView.findViewById(R.id.telnet_username);
        final EditText etPassword = dialogView.findViewById(R.id.telnet_password);

        //确定和取消按钮监听事件
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启用Telnet
                EnableTelnet();
                loadingDialog();
                boolean flag = telnetUtils.connect("192.168.1.1", 23, etUsername.getText().toString(), etPassword.getText().toString());
//              Toast.makeText(mContext, etUsername.getText().toString() + etPassword.getText().toString() + "登录" + flag, Toast.LENGTH_LONG).show();
                if (flag) {
                    telnetUtils.sendCommand("sidbg 1 DB set CltLmt 8 Enable 0");
                    telnetUtils.sendCommand("sidbg 1 DB save");
                    telnetUtils.sendCommand("reboot");
                    mDialog.dismiss();
                    Toast.makeText(mContext, "修改成功！", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "用户名或密码错误！", Toast.LENGTH_LONG).show();
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    /**
     * 启用Telnet
     */
    private void EnableTelnet() {
        /**
         * 通过接口地址：http://192.168.1.1/app_telnet_info.gch打开Telnet开关
         */
        String url = "http://192.168.1.1/app_telnet_info.gch";
        Map<String, String> params = new HashMap<>();
        params.put("Username", "CMCCAdmin");
        params.put("Password", "aDm8H%25MdA");
        params.put("ENABLE_FLAG", "1");
        try {
            HttpUtils.postRequest(url, params, "utf-8", new OnResponseListener() {
                @Override
                public void onSucess(String response) {
                    System.out.println(response);
                    //Toast.makeText(mContext, "Response Success:" + response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonObject = new JSONObject(response.replace(";", ""));
                        String resultCode = jsonObject.getString("ResultCode");
                        if (resultCode.equals("200")) {
                            ENABLE_TELNET = true;
                            Toast.makeText(mContext, "Telnet已经启用", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    System.out.println(error);
                    Toast.makeText(mContext, "Response Error:" + error, Toast.LENGTH_SHORT).show();
                    ENABLE_TELNET = false;
                    Toast.makeText(mContext, "Telnet启用失败,请连接wifi", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 网络提示对话框
     */
    private void showNetDialog() {
        //初始化布局文件
        View dialogView = View.inflate(mContext, R.layout.view_alert_dialog_confirm, null);
        AlertDialog.Builder mMessageBuilder = new AlertDialog.Builder(mContext);
        final AlertDialog mDialog = mMessageBuilder.create();
        mDialog.setView(dialogView);
        mDialog.setCanceledOnTouchOutside(false);//点击屏幕不消失
        mDialog.show();

        //确定按钮
        TextView tvConfirm = dialogView.findViewById(R.id.tv_confirm_dialog);
        //取消按钮
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel_dialog);
        //提示信息
        TextView tvMessage = dialogView.findViewById(R.id.tv_message_dialog);
        if (!isWifiOpened()) {
            tvMessage.setText(R.string.wifidisable);
        } else {
            tvMessage.setText(R.string.wifi_connect);
        }
        //确定和取消按钮监听事件
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    /**
     * 判断WLAN是否打开
     *
     * @return
     */
    private boolean isWifiOpened() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 判断WIFI是否连接
     *
     * @return
     */
    private boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnected();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
