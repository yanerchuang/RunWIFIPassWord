package com.wifi.android.runwifipassword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * 一款快速连接WiFi密码的软件，其中有四种连接方式可供选择，连接成功率依次增强。
 * 对已经连接的WiFi可以进行保存，方便以后获取和查看。
 * 还可以查看你手机已连接过的WiFi密码，解决已经连接的WiFi不知道密码的烦恼，轻松又快捷。
 * 还可以查询在公共场合一些未加密的WiFi，进行一键快速连接。
 * 还内置了WiFi分享连接，只要别人分享过得WiFi可以进行一键连接，方便又快捷。
 * 对于连已经获取过得密码可以进行自动连接，免去了每次都输入密码的痛苦。
 * WiFi已经在我们的世界中成为了必须品，面对流量的高额费用这款软件将会是你不错的选择。
 * 本app操作简单，适合所有人群使用。
 * <p>
 * <p>
 * 一款快速连接WiFi密码的软件，其中有四种连接方式可供选择，连接成功率依次增强，对连接的WiFi密码进行保存方便以后查看当前版本为1.0，可能存在一些bug,但是会慢慢的进行修复，
 */

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);
//        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1, false);
//        StatService.start(this);
        ImageView iv = (ImageView) findViewById(R.id.iv);
        iv.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);
        //
        //
    }
}
