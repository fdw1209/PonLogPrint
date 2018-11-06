package com.changhong.pontest.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.pontest.R;
import com.changhong.pontest.adapter.MyRecyclerViewAdapter;
import com.changhong.pontest.adapter.OnItemClickListener;
import com.changhong.pontest.utils.DeleteFileUtil;
import com.changhong.pontest.utils.FileService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class LogItemActivity extends Activity {

    private RecyclerView MyRecyclerView;
    private MyRecyclerViewAdapter mMyAdapter;
    private String logType;
    private List<String> logListNames = new ArrayList<>();
    private LinearLayout linear_back;
    private TextView text_log_title;
    private ImageView img_delete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list);

        //初始化控件
        initView();
        //获取Log文件名列表
        getLogFileName();

        //调用Adapter,显示Log列表
        updateAdapter();

        //返回上一级页面
        linear_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogItemActivity.this.finish();
            }
        });

        //清空Log列表并删除文件
        cleanLog(Environment.getExternalStorageDirectory().getPath(),logType);

    }

    /**
     * 获取Log文件名列表
     */
    private void getLogFileName() {
        File path = new File(Environment.getExternalStorageDirectory().getPath());// 获得SD卡路径
        File[] files = path.listFiles();// 读取
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.endsWith(".txt") && fileName.contains(logType)) {
                    logListNames.add(fileName);
                }
            }
        }
//        SharedPreferences config = getSharedPreferences("FileName" + logType, 0);  //注意这里的文件名要和创建时的一样才行，否则就是新建了
//        fileNames = config.getStringSet("fileNames", new HashSet<String>());  //这里第二个参数是default_values，这里直接设为了""空串
//        List<String> list = new ArrayList<>();
//        if (!fileNames.isEmpty()) {
//            for (String str : fileNames) {
//                System.out.println(str);
//                Log.i("LogFileName", str);
//                list.add(str + ".txt");
//            }
//            return list;
//        } else {
//            return new ArrayList<>();
//        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        MyRecyclerView = findViewById(R.id.log_list);
        linear_back = findViewById(R.id.activity_title_back);
        text_log_title = findViewById(R.id.activity_title_title);
        img_delete = findViewById(R.id.activity_title_right_im);

        MyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        text_log_title.setText("日志列表");
        img_delete.setImageResource(R.mipmap.icon_history_list_delete);
        logType = getLogType();
    }

    /**
     * 新建调用Adapter
     */
    private void updateAdapter() {
        mMyAdapter = new MyRecyclerViewAdapter(this, logListNames);
        MyRecyclerView.setAdapter(mMyAdapter);
        mMyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                String fileName = logListNames.get(postion);
                //如果文件名为空，则提示用户输入文件名，并记录日志
                if (isEmpty(fileName)) {
                    Toast.makeText(LogItemActivity.this, R.string.file_empty, Toast.LENGTH_LONG).show();
                    Log.w(FileService.TAG, "The file name is empty");
                    return;
                }
                //调用fileService的read方法，并将读取出来的内容放入到文本内容输入框里面
                //如果成功执行，提示用户读取成功，并记录日志。
                //如果出现异常信息（例：文件不存在），提示用户读取失败，并记录日志。
                try {
                    String path = FileService.readPath(fileName);
                    startActivity(getTextFileIntent(path));
                    //Toast.makeText(LogItemActivity.this, R.string.read_success, Toast.LENGTH_SHORT).show();
                    Log.i(FileService.TAG, "The file read successful");
                } catch (Exception e) {
                    Toast.makeText(LogItemActivity.this, R.string.read_fail, Toast.LENGTH_SHORT).show();
                    Log.e(FileService.TAG, "The file read failed");
                }
            }
        });
    }

    /**
     * 清空Log列表并删除文件
     */
    private void cleanLog(final String dir, final String logType) {
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = DeleteFileUtil.deleteDirectory(dir, logType);
                if (flag) {
                    Toast.makeText(getApplicationContext(), "清空Log成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "清空Log失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
     * android获取一个用于打开文本文件的intent(第三方应用打开)
     */
    private static Intent getTextFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

}
