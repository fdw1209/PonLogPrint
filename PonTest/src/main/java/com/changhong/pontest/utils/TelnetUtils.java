package com.changhong.pontest.utils;

import android.util.Log;
import android.widget.TextView;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedInputStream;
import java.io.PrintStream;

/**
 * Telnet操作类
 * Created by fdw on 2018/7/31.
 */

public class TelnetUtils {
    private TelnetClient client;
    private BufferedInputStream bis;
    private PrintStream ps;
    private char prompt = '#';
    private GetMessageListener mListener;
    private TextView textView;
    private StringBuffer stringBuffer;

    public TelnetUtils() {
        client = new TelnetClient();
        stringBuffer = new StringBuffer();
    }

    public TelnetUtils(TextView text_log) {
        stringBuffer = new StringBuffer();
        client = new TelnetClient();
        this.textView = text_log;
    }

    /**
     * 连接及登录
     *
     * @param ip       目标主机IP
     * @param port     端口号（Telnet 默认 23）
     * @param user     登录用户名
     * @param password 登录密码
     */
    public boolean connect(String ip, int port, String user, String password) {
        try {
            client.connect(ip, port);
            bis = new BufferedInputStream(client.getInputStream());
            ps = new PrintStream(client.getOutputStream());
            this.prompt = user.equals("root") ? '#' : '#';
            return login(user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 登录
     *
     * @param user
     * @param password
     */
    public boolean login(String user, String password) {
        read("Login:");
        write(user);
        String str1 = read("Password:");
        if (str1.equals("")) {
            return false;
        } else {
            write(password);
            String str = read(prompt + " ");
            if (str.contains("#")) {
                return true;
            }
            return false;
        }
    }

    /**
     * 读取返回来的数据
     *
     * @param info
     * @return
     */
    public String read(String info) {
        long time = System.currentTimeMillis();
        try {
            char lastChar = info.charAt(info.length() - 1);
            StringBuffer sb = new StringBuffer();
            char ch = (char) bis.read();
            while (true) {
                sb.append(ch);
                //User name is incorrect
                if (ch == lastChar) {
                    if (sb.toString().endsWith(info)) {
                        if (mListener != null) {
                            mListener.onMessage(sb.toString());
                        }
                        Log.i("ReturnStr:", sb.toString());
                        if (sb.toString().contains("#") || sb.toString().contains("Login:")) {
                            stringBuffer.append(sb.toString());
                        } else {
                            stringBuffer.append(sb.toString() + "\n");
                        }
                        textView.setText(stringBuffer);
                        return sb.toString();
                    }
                }
                if (sb.toString().contains("User name is incorrect") || sb.toString().contains("Password is incorrect")) {
                    return "";
                }
                ch = (char) bis.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 写指令
     *
     * @param instruction
     */
    public void write(final String instruction) {
        try {
            ps.println(instruction);
            ps.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向目标发送命令字符串
     *
     * @param command
     * @return
     */
    public String sendCommand(String command) {
        try {
            write(command);
            return read(prompt + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        try {
            if (client != null)
                client.disconnect();
//            if (in != null)
//                in.close();
            if (ps != null)
                ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface GetMessageListener {
        void onMessage(String info);
    }

    public void setListener(GetMessageListener mListener) {
        this.mListener = mListener;
    }
}
