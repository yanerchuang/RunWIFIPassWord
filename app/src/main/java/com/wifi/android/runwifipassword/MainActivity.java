package com.wifi.android.runwifipassword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wifi.android.runwifipassword.util.SharedPrefsUtil;

import www.yiba.com.wifisdk.activity.YIbaWifiActivity;
import www.yiba.com.wifisdk.manager.WiFiSDKManager;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "chuangguo.qi";
    private TextView tv_integral;
    private boolean isNetwork = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myNetReceiver, mFilter);


        initView();
    }

    private void initView() {
        SharedPrefsUtil.putValue(this, "myIntegral", 1000);
        findViewById(R.id.bt_start_crack).setOnClickListener(this);
        findViewById(R.id.bt_look_password).setOnClickListener(this);
        findViewById(R.id.bt_integral).setOnClickListener(this);
        findViewById(R.id.bt_commonality).setOnClickListener(this);
        findViewById(R.id.bt_user).setOnClickListener(this);
        findViewById(R.id.bt_exit).setOnClickListener(this);

        tv_integral = (TextView) findViewById(R.id.tv_integral);
        tv_integral.setText(SharedPrefsUtil.getValue(MainActivity.this, "myIntegral", 0) + "");

    }

    @Override
    public void onClick(View view) {
        int myIntegral1 = SharedPrefsUtil.getValue(this, "myIntegral", 0);
        int id = view.getId();
        Intent intent = null;

        if (id == R.id.bt_start_crack) {
            intent = new Intent(this, WifiCrackActivitySimple.class);
            startActivity(intent);


        } else if (id == R.id.bt_look_password) {

            //Toast.makeText(MainActivity.this,"暂未开放",Toast.LENGTH_LONG).show();
            intent = new Intent(this, WifiPasswordLookActivity.class);
            startActivity(intent);

        } else if (id == R.id.bt_integral) {

            intent = new Intent(MainActivity.this, IntegralMainActivity.class);
            startActivity(intent);

        } else if (id == R.id.bt_commonality) {
//            WiFiSDKManager.getInstance().setFreeWifiToggle( this , false );
            WiFiSDKManager.getInstance().setOpenWifiToggle(this, false);
            WiFiSDKManager.getInstance().setNotificationToggle(this, false);
            intent = new Intent(MainActivity.this, YIbaWifiActivity.class);
            startActivity(intent);

        } else if (id == R.id.bt_user) {

            intent = new Intent(this, CrackPasswordLookActivity.class);
            startActivity(intent);

        } else if (id == R.id.bt_exit) {

            showDialogTip("是否退出当前应用", "退出", "取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }, null);


        }

    }

    private BroadcastReceiver myNetReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {

                    /////////////网络连接
                    String name = netInfo.getTypeName();

                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        /////WiFi网络

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                        /////有线网络

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        /////////3g网络

                    }
                } else {
                    ////////网络断开
                    isNetwork = false;
                    Toast.makeText(MainActivity.this, "当前无可用网络", Toast.LENGTH_LONG).show();
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myNetReceiver != null) {
            unregisterReceiver(myNetReceiver);
        }
    }
}
