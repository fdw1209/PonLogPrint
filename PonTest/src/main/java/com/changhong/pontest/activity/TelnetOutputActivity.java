package com.changhong.pontest.activity;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.pontest.R;
import com.changhong.pontest.fragment.LoadingDialog;
import com.changhong.pontest.utils.FileService;
import com.changhong.pontest.utils.TelnetUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.text.TextUtils.isEmpty;

public class TelnetOutputActivity extends Activity {

    private TelnetUtils telnetUtils;
    private TextView text_IpInfo, text_log, text_cmd_ok;
    private TextView log_open, log_close, log_query;
    private EditText et_cmd;
    private ImageView img_save, img_share, img_cmd_delete;
    private LinearLayout linear_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telnet_output);
        //初始化控件
        initView();
        //获取Log问题调试选项
        final String Log_Type = getLogType();
        //实现Telnet登录及打印重定向
        initLog();
        //Log命令处理操作
        LogOperation(Log_Type);
        //命令输入操作
        cmdInput();
        //保存Log
        SaveLog(Log_Type);

        //返回上一级页面
        linear_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelnetOutputActivity.this.finish();
                //telnetUtils.disconnect();
            }
        });
    }

    /**
     * 命令输入操作
     */
    private void cmdInput() {
        //删除输入命令
        img_cmd_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_cmd.setText("");
            }
        });
        //发送命令按钮
        text_cmd_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmd = et_cmd.getText().toString();
                if (!cmd.equals("")) {
                    et_cmd.setText("");
                    telnetUtils.sendCommand(cmd);
                }
            }
        });
    }

    /**
     * 保存Log
     *
     * @param log_Type
     */
    private void SaveLog(final String log_Type) {
        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fileName = log_Type + "-Log-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String content = text_log.getText().toString();
                //当文件内容为空的时候，提示用户文件内容为空，并记录日志。
                if (isEmpty(content)) {
                    Toast.makeText(TelnetOutputActivity.this, R.string.empty_content, Toast.LENGTH_SHORT).show();
                    Log.i(FileService.TAG, "The log content is empty");
                    return;
                }
                //当文件名和内容都不为空的时候，调用fileService的save方法
                //当成功执行的时候，提示用户保存成功，并记录日志
                //当出现异常的时候，提示用户保存失败，并记录日志
                try {
                    FileService.save(fileName, content);
                    Toast.makeText(TelnetOutputActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                    Log.i(FileService.TAG, "The log icon_save successful");
                    img_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //分享文件
                            shareFile(fileName + ".txt");
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(TelnetOutputActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(TelnetOutputActivity.this, R.string.fail, Toast.LENGTH_SHORT).show();
                    Log.e(FileService.TAG, "The log icon_save failed");
                }
            }
        });
    }

    /**
     * 调用系统方法分享文件
     */
    public void shareFile(final String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        //Toast.makeText(getApplicationContext(), file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, "分享文件"));
        } else {
            Toast.makeText(TelnetOutputActivity.this, "分享文件不存在", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型
     */
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    /**
     * Log命令处理操作
     *
     * @param log_Type
     */
    private void LogOperation(final String log_Type) {
        /**
         * 打开Log
         */
        log_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog();
                switch (log_Type) {
                    case "OLTRegister"://OLT注册
                        //开启ploam消息打印开关
                        telnetUtils.sendCommand("gponsdk_test -s_print_dbg 2");
                        DelayTime(1);
                        //OMCI 交互日志
                        telnetUtils.sendCommand("sidbg 132 omcidebug setprintlevel 5 0 0");
                        break;
                    case "RMSPlatForm"://RMS平台
                        telnetUtils.sendCommand("sidbg 3 tr069 -l 8");
                        DelayTime(1);
                        telnetUtils.sendCommand("sidbg 3 tr069 showsoap 1");
                        break;
                    case "PluginPlatForm"://插件平台
                        telnetUtils.sendCommand("sidbg 73 -l 8");
                        DelayTime(1);
                        telnetUtils.sendCommand("sidbg 73 plugm_cmdtype dbgall 1");
                        DelayTime(1);
                        telnetUtils.sendCommand("sidbg 73 plugm_cmdtype dbgjson 1");
                        break;
                    case "WebConfig"://WEB配置
                        //查看环境变量
                        telnetUtils.sendCommand("sidbg 3 webd printenv");
                        DelayTime(1);
                        //开启Debug级别日志
                        telnetUtils.sendCommand("idbg 3 webd -l 8");
                        break;
                    default:
                        break;
                }
            }
        });
        /**
         * 关闭Log
         */
        log_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog();
                switch (log_Type) {
                    case "OLTRegister"://OLT注册
                        //关闭ploam消息打印开关
                        telnetUtils.sendCommand("gponsdk_test -s_print_dbg 0");
                        DelayTime(1);
                        //关闭OMCI 交互日志
                        telnetUtils.sendCommand("sidbg 132 omcidebug setprintlevel 0 0 0");
                        break;
                    case "RMSPlatForm"://RMS平台
                        telnetUtils.sendCommand("sidbg 3 tr069 -l 5");
                        DelayTime(1);
                        telnetUtils.sendCommand("sidbg 3 tr069 showsoap 0");
                        break;
                    case "PluginPlatForm"://插件平台
                        telnetUtils.sendCommand("sidbg 73 -l 5");
                        DelayTime(1);
                        telnetUtils.sendCommand("sidbg 73 plugm_cmdtype dbgall 0");
                        DelayTime(1);
                        telnetUtils.sendCommand("sidbg 73 plugm_cmdtype dbgjson 0");
                        break;
                    case "WebConfig"://WEB配置
                        //关闭日志
                        telnetUtils.sendCommand("sidbg 3 webd -l 0");
                        break;
                    default:
                        break;
                }
            }
        });
        /**
         * 关闭LOG后，查询命令
         */
        log_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog();
                switch (log_Type) {
                    case "OLTRegister"://OLT注册
                        //获取光功率
                        telnetUtils.sendCommand("opticaltst -getpara");
                        DelayTime(2);
                        //获取OLT下发参数
                        telnetUtils.sendCommand("echo 0 128 >/sys/devices/platform/ponmac/gmac/bwMap");
                        DelayTime(2);
                        //获取OLT下发参数
                        telnetUtils.sendCommand("cat /sys/devices/platform/ponmac/gmac/allocTab");
                        break;
                    case "RMSPlatForm"://RMS平台
                        telnetUtils.sendCommand("ifconfig");//查询WAN口信息
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 实现Telnet登录及打印重定向
     */
    private void initLog() {
        telnetUtils.connect("192.168.1.1", 23, "root", "Pon521");
        telnetUtils.sendCommand("cat /proc/zxic/verdate");//查看版本
        telnetUtils.sendCommand("redir add");
        telnetUtils.sendCommand("redir printf");//打印重定向
    }

    /**
     * 获取Log问题调试选项
     *
     * @return
     */
    private String getLogType() {
        Intent intent = getIntent();
        String Log_Type = intent.getStringExtra("LOG_TYPE");
        return Log_Type;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        text_IpInfo = findViewById(R.id.activity_title_title);
        img_save = findViewById(R.id.activity_title_right_im);
        img_share = findViewById(R.id.activity_title_right_2_im);
        linear_back = findViewById(R.id.activity_title_back);
        text_log = findViewById(R.id.telnet_output);
        et_cmd = findViewById(R.id.telnet_cmd_input);
        text_cmd_ok = findViewById(R.id.telnet_cmd_ok);
        img_cmd_delete = findViewById(R.id.telnet_cmd_delete);
        log_open = findViewById(R.id.telnet_log_open);
        log_close = findViewById(R.id.telnet_log_close);
        log_query = findViewById(R.id.telnet_log_query);

        text_IpInfo.setText("Telnet-192.168.1.1:23");
        img_save.setImageResource(R.mipmap.icon_save);
        img_share.setImageResource(R.mipmap.icon_details_share);
        telnetUtils = new TelnetUtils(text_log);
    }

    /**
     * 加载等待弹窗
     */
    private void loadingDialog() {
        LoadingDialog.Builder builder1 = new LoadingDialog.Builder(TelnetOutputActivity.this)
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
     * Handler消息处理
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 延时
     *
     * @param time
     */
    private void DelayTime(int time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        telnetUtils.disconnect();
    }

//    /**
//     * Log文件名存储
//     *
//     * @param log_Type
//     * @param fileNames
//     */
//    private void FileNameSave(String log_Type, Set<String> fileNames) {
//        SharedPreferences config = getSharedPreferences("FileName" + log_Type, 0);
//        SharedPreferences.Editor edit = config.edit();
//        edit.putStringSet("fileNames", fileNames);
//        edit.commit();
//    }
}
