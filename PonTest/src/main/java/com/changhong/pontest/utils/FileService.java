package com.changhong.pontest.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 文件保存与读取功能实现类
 *
 * @author fdw
 * <p>
 * 2018-8-10 上午09:03:18
 */
public class FileService {
    public static final String TAG = "FileService";
    private static Context context;

    //得到传入的上下文对象的引用
    public FileService(Context context) {
        this.context = context;
    }

//    public void save(String fileName, String content) throws Exception {
//        // 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
//        if (!fileName.endsWith(".txt")) {
//            fileName = fileName + ".txt";
//        }
//        byte[] buf = fileName.getBytes("iso8859-1");
//        Log.e(TAG, new String(buf, "utf-8"));
//        fileName = new String(buf, "utf-8");
//        Log.e(TAG, fileName);
//        // Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
//        // Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
//        // Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
//        // MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
//        // 如果希望文件被其他应用读和写，可以传入：
//        // openFileOutput("output.txt", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
//        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//        fos.write(content.getBytes());
//        //fos.write("\r\n".getBytes());//写入换行
//        fos.close();
//    }

    /**
     * 定义文件保存的方法
     *
     * @param fileName 文件名
     * @param content  文件内容
     * @throws Exception
     */
    public static void save(String fileName, String content) {
        // 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
        if (!fileName.endsWith(".txt")) {
            fileName = fileName + ".txt";
        }
        FileOutputStream fos = null;
        try {
            /* 判断sd的外部设置状态是否可以读写 */
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File file = new File(Environment.getExternalStorageDirectory(), fileName);
                // 先清空内容再写入
                fos = new FileOutputStream(file);
                byte[] buffer = content.getBytes();
                fos.write(buffer);
                fos.close();
            }
        } catch (Exception ex) {
            Toast.makeText(context,ex.toString()+"????",Toast.LENGTH_LONG).show();
            ex.printStackTrace();
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件路径
     *
     * @param fileName 文件名
     * @return 文件路径
     */
    public static String readPath(String fileName) {
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + fileName;
        return path;
    }

//    public String read(String fileName) throws Exception {
//        // 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
//        if (!fileName.endsWith(".txt")) {
//            fileName = fileName + ".txt";
//        }
//        FileInputStream fis = context.openFileInput(fileName);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        byte[] buf = new byte[1024];
//        int len = 0;
//        //将读取后的数据放置在内存中---ByteArrayOutputStream
//        while ((len = fis.read(buf)) != -1) {
//            baos.write(buf, 0, len);
//        }
//        fis.close();
//        baos.close();
//        //返回内存中存储的数据
//        return baos.toString();
//    }
    /**
     * 读取文件内容
     *
     * @param fileName 文件名
     * @return 文件内容
     * @throws Exception
     */
    public static String read(String fileName) {
        // 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
        if (!fileName.endsWith(".txt")) {
            fileName = fileName + ".txt";
        }
        FileInputStream fis = null;
        String result = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            result = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
