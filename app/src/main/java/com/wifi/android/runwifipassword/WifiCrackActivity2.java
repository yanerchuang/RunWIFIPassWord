package com.wifi.android.runwifipassword;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wifi.android.runwifipassword.util.AccessPoint;
import com.wifi.android.runwifipassword.util.CopyData_File;
import com.wifi.android.runwifipassword.util.LogUtil;
import com.wifi.android.runwifipassword.util.PasswordGetter;
import com.wifi.android.runwifipassword.util.SharedPrefsUtil;
import com.wifi.android.runwifipassword.view.PinnedSectionListView;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WifiCrackActivity2 extends BaseActivity implements View.OnClickListener {

    PinnedSectionListView listView;
    private List<WifiPo> listData=new ArrayList<>();
    private WifiManager wifimanager;
    private List<ScanResult> scanResults;
    private List<WifiConfiguration> configuredNetworks;
    private List<WifiPo> myWifi;
    private List<WifiPo> crackWifi;
    private List<WifiPo> openWifi;
    public static final String WIFI_AUTH_OPEN = "";
    public static final String WIFI_AUTH_ROAM = "[ESS]";
    public static final String WIFI_AUTH_WPS = "[WPS][ESS]";
    private Comparator<WifiPo> comparator;
    private WifiConfiguration mConfig;
    private LinearLayout linearLayout01;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout02;
    private TextView forget;
    private listViewAdaper adapter;
    private WifiReceiver wifiReceiver;
    private AccessPoint ap;
    private AccessPoint tmpap;
    private String password="";
    private boolean cracking;
    private int netid;
    private int netids;
    private static final String TAG = "chuangguo.qi";
    //List<ScanResult> results;
    ScanResult result;
    private int nowid = 0;
    private PasswordGetter passwordGetter;
    ScanResult scanResult = null;
    private String crackWifiSSID;
    private int posint = 0;
    private TextView strengt_tv;
    private TextView tvName;
    private TextView classify_tv;
    private ProgressBar progressBar1;
    private View dialogView;
    private TextView tv_title;
    private ProgressBar progressBar;
    private TextView tv_present;
    private TextView tv_progress;
    private Button button;
    private AlertDialog.Builder crackBuilder;
    private AlertDialog dialog;
    private int currentProgress = 0;
    private boolean isOnClick = false;
    private AlertDialog failDialog;
    private CopyData_File co;
    private String ASSET_NAME_1 = "adfaf.lar";
    private String ASSET_NAME_4 = "adfdfvv.lar";
    private String ASSET_NAME_2 = "adaegg.lar";
    private String ASSET_NAME_3 = "dafawef.lar";

    private int ASSET_NAME_1_LENGHT = 757;
    private int ASSET_NAME_2_LENGHT = 1649;
    private int ASSET_NAME_3_LENGHT = 2641;
    private int ASSET_NAME_4_LENGHT = 1287;
    private int ASSET_NAME_LENGHT = 0;
    private View select_dialog_view;
    private Button bt_base;
    private Button bt_standard;
    private Button bt_advanced;
    private Button bt_king;
    private AlertDialog selectDialogShow;
    private AlertDialog.Builder select_dialog;
    private AlertDialog.Builder integralDialog;
    private AlertDialog integralDialogshow;
    private DatabaseHelper helper;
    private WifiOpenOrClose wifiOpenOrClose;
    private boolean isOne=true;
    private ProgressDialog progressDialog;
    private boolean isCrackOk = true;
    private Dialog dialogFail;
    private AlertDialog.Builder builderFail;
    private boolean isbuilderFail=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        co = new CopyData_File(this);
        co.DoCopy(co.getSDPath() + "/", co.getSDPath() + "/" + ASSET_NAME_1, R.raw.simpleness);
        co.DoCopy(co.getSDPath() + "/", co.getSDPath() + "/" + ASSET_NAME_2, R.raw.complex);
        co.DoCopy(co.getSDPath() + "/", co.getSDPath() + "/" + ASSET_NAME_3, R.raw.password);
        co.DoCopy(co.getSDPath() + "/", co.getSDPath() + "/" + ASSET_NAME_4, R.raw.superpassword);
        setContentView(R.layout.activity_wifi_crack);
        cracking = false;
        netid = -1;
        initView();
        //初始化wifi管理器
        wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        LogUtil.i("WiFi未打开"+wifimanager.isWifiEnabled());

        if (!wifimanager.isWifiEnabled()){
            LogUtil.i("WiFi未打开");
            showDialogTip("当前WiFi未打开是否打开？", "确定", "取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
                    wifiOpenOrClose = new WifiOpenOrClose();
                    registerReceiver(wifiOpenOrClose,intentFilter);
                    progressDialog.show();
                    wifimanager.setWifiEnabled(true);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WifiCrackActivity2.this.finish();
                }
            });

        }else {

            getwifiData();
            if (scanResults.size()>0) {
                initDialogView();
                initData();
                getData();
            }else {

                showDialogTip("当前范围内没有可用WiFi", "我知道了", null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },null);
            }

        }

        wifiReceiver = new WifiReceiver();
        IntentFilter intentFilter02 = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter02.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter02.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter02.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter02.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, intentFilter02);


    }

    private void initDialogView() {

        dialogView = LayoutInflater.from(WifiCrackActivity2.this).inflate(R.layout.dialog_view, null, false);
        tv_title = (TextView) dialogView.findViewById(R.id.tv_title);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.progressBar);
        progressBar.setMax(4685);
        tv_present = (TextView) dialogView.findViewById(R.id.tv_present);
        tv_progress = (TextView) dialogView.findViewById(R.id.tv_progress);
        button = (Button) dialogView.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                wifimanager.removeNetwork(netid);
                wifimanager.saveConfiguration();
            }
        });

    }

    /**
     * 获取wifi列表
     */
    public void getwifiData() {
        wifimanager.startScan();
        scanResults = wifimanager.getScanResults();
        configuredNetworks = wifimanager.getConfiguredNetworks();
      //  LogUtil.i("-----scanResults--" + scanResults);
        //LogUtil.i("-----configuredNetworks---"+configuredNetworks);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        listData = new ArrayList<WifiPo>();
        myWifi = new ArrayList<WifiPo>();
        crackWifi = new ArrayList<WifiPo>();
        openWifi = new ArrayList<WifiPo>();

        comparator = new Comparator<WifiPo>() {
            @Override
            public int compare(WifiPo wifiPo, WifiPo t1) {
                int leve1 = 0;
                int leve2 = 0;
                try {
                    if (wifiPo.getStrength() == null) {

                        leve1 = Integer.parseInt("0");
                    } else {

                        leve1 = Integer.parseInt(wifiPo.getStrength());
                    }

                    if (t1.getStrength() == null) {
                        leve2 = Integer.parseInt("0");
                    } else {

                        leve2 = Integer.parseInt(t1.getStrength());
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }

                if (leve1 > leve2) {
                    return -1;
                } else if (leve1 < leve2) {

                    return 1;
                } else {
                    return 0;
                }
            }
        };


    }


    public void getData() {

        myWifi.clear();
        openWifi.clear();
        crackWifi.clear();
        listData.clear();
        if (scanResults == null) {
            return;
        }

        FIFL:
        for (int i = 0; i < scanResults.size(); i++) {

            WifiPo wifipo = new WifiPo();

            String scanSSID = scanResults.get(i).SSID;
            if (scanSSID.length() < 1) {
                continue;
            }

            int level = WifiManager.calculateSignalLevel(scanResults.get(i).level,
                    100);
            String capabilities = scanResults.get(i).capabilities.trim();

            if (capabilities != null && (capabilities.equals(WIFI_AUTH_OPEN) || capabilities.equals(WIFI_AUTH_ROAM) || capabilities.equals(WIFI_AUTH_WPS))) {
                wifipo.setState(2);
                wifipo.setName(scanResults.get(i).SSID);
                wifipo.setType(1);
                wifipo.setStrength(String.valueOf(Math.abs(level)));
                openWifi.add(wifipo);

                continue FIFL;
            }

            for (int j = 0; j < configuredNetworks.size(); j++) {
                String confguredSSID = configuredNetworks.get(j).SSID;
                if (confguredSSID.length() < 1) {
                    break;
                }
                confguredSSID = confguredSSID.substring(1, confguredSSID.length() - 1);
                if (scanSSID.equals(confguredSSID)) {
                    wifipo.setState(1);
                    wifipo.setName(scanResults.get(i).SSID);
                    wifipo.setType(1);
                    wifipo.setNetid(configuredNetworks.get(j).networkId);
                    wifipo.setStrength(String.valueOf(Math.abs(level)));
                    if (!scanResults.get(i).SSID.equals(wifimanager.getConnectionInfo().getSSID().substring(1, wifimanager.getConnectionInfo().getSSID().length() - 1))) {

                        myWifi.add(wifipo);

                    }

                    continue FIFL;
                } else {
                    wifipo.setState(0);
                }
            }
            wifipo.setName(scanResults.get(i).SSID);
            wifipo.setType(1);
            wifipo.setStrength(String.valueOf(Math.abs(level)));
            crackWifi.add(wifipo);
        }

        if (myWifi.size() > 0) {
            Collections.sort(myWifi, comparator);
        }
        if (crackWifi.size() > 0) {
            Collections.sort(crackWifi, comparator);
        }
        if (openWifi.size() > 0) {
            Collections.sort(openWifi, comparator);
        }

        WifiPo wifi = new WifiPo();
        wifi.setName("我的wifi");
        wifi.setType(0);
        myWifi.add(0, wifi);
        if (myWifi.size() >= 2) {
            listData.addAll(myWifi);
        }

        WifiPo wifi3 = new WifiPo();
        wifi3.setName("开放wifi");
        wifi3.setType(0);
        openWifi.add(0, wifi3);
        if (openWifi.size() >= 2) {
            listData.addAll(openWifi);
        }

        WifiPo wifi2 = new WifiPo();
        wifi2.setName("未破解wifi");
        wifi2.setType(0);
        crackWifi.add(0, wifi2);
        if (crackWifi.size() >= 2) {
            listData.addAll(crackWifi);
        }

        adapter.notifyDataSetChanged();

    }

    /***
     * 初始化View
     */
    private void initView() {

        progressDialog = new ProgressDialog(WifiCrackActivity2.this);
        progressDialog.setMessage("正在打开WiFi请稍等！");

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        listView = (PinnedSectionListView) findViewById(R.id.listView);
        adapter = new listViewAdaper();
        listView.setAdapter(adapter);
        linearLayout01 = (LinearLayout) findViewById(R.id.linear01);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        linearLayout02 = (LinearLayout) findViewById(R.id.linear02);

        strengt_tv = (TextView) findViewById(R.id.strengt_tv);
        tvName = (TextView) findViewById(R.id.tv);
        classify_tv = (TextView) findViewById(R.id.classify_tv);
        forget = (TextView) findViewById(R.id.forget);

        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);

        select_dialog_view = LayoutInflater.from(WifiCrackActivity2.this).inflate(R.layout.select_dialog_view, null, false);

        bt_base = (Button) select_dialog_view.findViewById(R.id.bt_base);
        bt_standard = (Button) select_dialog_view.findViewById(R.id.bt_standard);
        bt_advanced = (Button) select_dialog_view.findViewById(R.id.bt_advanced);
        bt_king = (Button) select_dialog_view.findViewById(R.id.bt_king);

        bt_base.setOnClickListener(this);
        bt_standard.setOnClickListener(this);
        bt_advanced.setOnClickListener(this);
        bt_king.setOnClickListener(this);

        layoutvisibility(View.GONE);

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifimanager.removeNetwork(wifimanager.getConnectionInfo().getNetworkId());
                wifimanager.saveConfiguration();
                layoutvisibility(View.GONE);
                getwifiData();
                getData();
            }
        });

        listView.setShadowVisible(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtil.i("onItemClick: " + listData.get(i).getName());
                posint = i;
                int state = listData.get(i).getState();
                mConfig = new WifiConfiguration();
                if (state == 0) {//破解
                    isOnClick = true;
                    update();

                } else if (state == 1) {//我的
                    if (wifimanager.getConnectionInfo().getSSID().equals("\"" + listData.get(i).getName() + "\"")) {
                        return;
                    } else {
                        mConfig = isExsits(listData.get(i).getName());
                        if (mConfig == null) {
                            return;
                        }
                        netids = mConfig.networkId;
                        layoutvisibility(View.VISIBLE);
                        progressBar1.setVisibility(View.VISIBLE);
                        forget.setVisibility(View.GONE);
                        tvName.setText(listData.get(i).getName());
                        classify_tv.setText("正在连接");
                        strengt_tv.setText(WifiManager.calculateSignalLevel(Integer.parseInt(listData.get(i).getStrength()), 100) + "%");
                        wifimanager.enableNetwork(mConfig.networkId, true);
                        wifimanager.saveConfiguration();
                    }

                } else if (state == 2) {//无需

                    mConfig.SSID = AccessPoint.convertToQuotedString(listData.get(i).getName());
                    mConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifimanager.enableNetwork(wifimanager.addNetwork(mConfig), true);
                    wifimanager.saveConfiguration();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                final int clickID = i;
                if (listData.get(i).getState() == 1) {
                    return true;
                }
                AlertDialog.Builder removeSSID = new AlertDialog.Builder(WifiCrackActivity2.this);
                removeSSID.setTitle("提示");
                removeSSID.setMessage("你是否要忘记密码");
                removeSSID.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        LogUtil.i("onClick: " + listData.get(clickID).getNetid());
                        wifimanager.removeNetwork(listData.get(clickID).getNetid());
                        wifimanager.saveConfiguration();
                        getwifiData();
                        getData();
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", null).create().show();

                return true;
            }
        });

        helper = DatabaseHelper.getHelper(WifiCrackActivity2.this);
    }

    @Override
    public void onClick(View view) {
        isCrackOk=true;
        int myIntegral1 = SharedPrefsUtil.getValue(this, "myIntegral", 0);
        if (view.getId() == R.id.bt_base) {
            getPassWorld(ASSET_NAME_1);
            isIntegralOK();
        } else if (view.getId() == R.id.bt_standard) {

            if (myIntegral1 >= 10) {
                getPassWorld(ASSET_NAME_2);
                isIntegralOK();
            } else {

                getIntegral(10);
            }
        } else if (view.getId() == R.id.bt_advanced) {

            if (myIntegral1 >= 20) {
                getPassWorld(ASSET_NAME_3);
                isIntegralOK();

            } else {

                getIntegral(20);
            }

        } else if (view.getId() == R.id.bt_king) {
            if (myIntegral1 >= 30) {
                getPassWorld(ASSET_NAME_4);
                isIntegralOK();
            } else {

                getIntegral(30);
            }
        }
        netid = -1;
        WifiInfo connectionInfo = wifimanager.getConnectionInfo();
        int netid = connectionInfo.getNetworkId();
        wifimanager.disableNetwork(netid);
        wifimanager.disconnect();
        connectionInfo=null;

        if (selectDialogShow != null && selectDialogShow.isShowing()) {
            selectDialogShow.dismiss();
        }

    }

    public class listViewAdaper extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {


        @Override
        public int getCount() {
            if (listData != null) {
                return listData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return listData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHold hold = null;
            if (view == null) {
                hold = new ViewHold();
                if (listData.get(i).getType() == 0) {
                    view = LayoutInflater.from(WifiCrackActivity2.this).inflate(R.layout.listview_item_title, null, false);
                    hold.title_tv = (TextView) view.findViewById(R.id.titile_tv);
                    view.setTag(hold);
                } else {
                    view = LayoutInflater.from(WifiCrackActivity2.this).inflate(R.layout.listview_item, null, false);
                    hold.textView = (TextView) view.findViewById(R.id.tv);
                    hold.strengt_tv = (TextView) view.findViewById(R.id.strengt_tv);
                    hold.classify_tv = (TextView) view.findViewById(R.id.classify_tv);
                    view.setTag(hold);
                }
            } else {

                hold = (ViewHold) view.getTag();
            }

            WifiPo wifipo = listData.get(i);
            if (listData.get(i).getType() == 0) {
                hold.title_tv.setText(wifipo.getName());
            } else {
                hold.textView.setText(wifipo.getName());
                hold.strengt_tv.setText(wifipo.getStrength() + "%");
                if (listData.get(i).getState() == 1) {
                    hold.classify_tv.setText("已保存");

                } else if (listData.get(i).getState() == 0) {
                    hold.classify_tv.setText("需密码");
                } else if (listData.get(i).getState() == 2) {
                    hold.classify_tv.setText("开放wifi,可直接连接");

                }
            }

            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return listData.get(position).getType();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return listData.get(viewType).getType() == 0 ? true : false;
        }

        class ViewHold {

            TextView textView, title_tv, strengt_tv, classify_tv;
        }
    }

    private WifiConfiguration isExsits(String ssid) {

        List<WifiConfiguration> configuredNetworks = wifimanager.getConfiguredNetworks();
        for (WifiConfiguration configure : configuredNetworks) {

            if (configure.SSID.equals("\"" + ssid + "\"")) {

                return configure;
            }
        }
        LogUtil.i("isExsits: null");
        return null;
    }

    public void layoutvisibility(int visibility) {

        linearLayout01.setVisibility(visibility);
        linearLayout02.setVisibility(visibility);
        relativeLayout.setVisibility(visibility);
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                if (!cracking) {
                    LogUtil.i("onReceive: " + "不更新界面");
                    //update();
                }

            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION
                    .equals(action)) {
                WifiInfo info = wifimanager.getConnectionInfo();
                SupplicantState state = info.getSupplicantState();
                String str = null;
                if (state == SupplicantState.ASSOCIATED) {
                    nowid++;
                    str = "关联AP完成";
                } else if (state.toString().equals("AUTHENTICATING")) {
                    if (password != null && password.length() > 0) {
                        str = "正在验证密码" + AccessPoint.removeDoubleQuotes(password);
                    }
                } else if (state == SupplicantState.ASSOCIATING) {
                    str = "正在关联AP...";
                } else if (state == SupplicantState.COMPLETED) {
                    if (cracking) {
                        cracking = false;
                        return;
                    } else {
                        str = "已连接";
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (listData != null && listData.size() > 0) {
                            layoutvisibility(View.VISIBLE);
                            progressBar1.setVisibility(View.GONE);
                            forget.setVisibility(View.VISIBLE);
                            tvName.setText(wifimanager.getConnectionInfo().getSSID().substring(1, wifimanager.getConnectionInfo().getSSID().length() - 1));
                            classify_tv.setText(str);
                            strengt_tv.setText(listData.get(posint).getStrength());
                        }
                        if (passwordGetter != null) {
                            passwordGetter.reSet();
                        }

                        if (isOnClick && listData.get(posint).getState() == 0) {

                            if (isCrackOk) {
                                isCrackOk=false;
                                showDialogTip("恭喜你！密码破解成功！密码：" + password + "是否保存?", "保存", "取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        boolean isSever = false;
                                        CrackWifiPo crackWifiPo = new CrackWifiPo();
                                        crackWifiPo.setName(wifimanager.getConnectionInfo().getSSID());
                                        crackWifiPo.setBssid(wifimanager.getConnectionInfo().getBSSID());
                                        crackWifiPo.setPassword(password);

                                        try {
                                            List<CrackWifiPo> users = helper.getUserDao().queryForAll();
                                            for (int j = 0; j < users.size(); j++) {

                                                String bssid = users.get(j).getBssid();
                                                if (bssid.equals(wifimanager.getConnectionInfo().getBSSID())) {
                                                    isSever = true;

                                                }
                                            }

                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            if (!isSever) {
                                                helper.getUserDao().create(crackWifiPo);
                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, null);
                            }
                        }
                    }

                } else if (state == SupplicantState.DISCONNECTED) {
                    str = "已断开";
                    if (listData.get(posint).getState() == 1) {
                        if (isbuilderFail) {
                            isbuilderFail=false;
                            adapter.notifyDataSetChanged();
                            builderFail = new AlertDialog.Builder(WifiCrackActivity2.this);
                            builderFail.setTitle("提示");
                            builderFail.setMessage("WIFI验证失败！你可以在列表中进行破解。");
                            builderFail.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
                                @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        layoutvisibility(View.GONE);
                                        wifimanager.removeNetwork(mConfig.networkId);
                                        wifimanager.saveConfiguration();
                                        getwifiData();
                                        getData();
                                        isbuilderFail=true;

                                }
                            });

                            dialogFail = builderFail.show();
                        }
                    }

                } else if (state == SupplicantState.DORMANT) {
                    str = "暂停活动";
                } else if (state == SupplicantState.FOUR_WAY_HANDSHAKE) {
                    tv_title.setText("破解密码中..");

                    if (password != null && password.length() > 0) {
                        progressBar.setProgress(currentProgress);
                        tv_present.setText(currentProgress + "");
                        tv_progress.setText("" + currentProgress + "/" + ASSET_NAME_LENGHT);
                        str = "破解密码中.." + AccessPoint.removeDoubleQuotes(password)
                                + "  破解进行到第" + currentProgress + "个";
                    }
                } else if (state == SupplicantState.GROUP_HANDSHAKE) {
                    str = "组握手";
                } else if (state == SupplicantState.INACTIVE) {
                    str = "休眠中...";
                    if (cracking) {
                        //connectNetwork();
                    }
                    // 连接网络
                } else if (state == SupplicantState.INVALID) {
                    str = "无效";
                } else if (state == SupplicantState.SCANNING) {
                    str = "扫描中...";
                } else if (state == SupplicantState.UNINITIALIZED) {
                    str = "未初始化";
                }
                LogUtil.i("onReceive: " + str);
                final int errorCode = intent.getIntExtra(
                        WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                if (errorCode == WifiManager.ERROR_AUTHENTICATING) {
                    LogUtil.i("WIFI验证失败！");
                    if (listData.get(posint).getState() == 1) {
                        layoutvisibility(View.GONE);
                        wifimanager.removeNetwork(mConfig.networkId);
                        wifimanager.saveConfiguration();
                        getwifiData();
                        getData();
                        adapter.notifyDataSetChanged();
                        if (failDialog !=null && !failDialog.isShowing()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(WifiCrackActivity2.this);
                            builder.setTitle("提示");
                            builder.setMessage("WIFI验证失败！你可以在列表中进行破解。");
                            builder.setNegativeButton("我知道了", null).create().show();
                            failDialog = builder.show();
                        }

                    }
                    if (cracking) {
                        connectNetwork();
                    }

                }
            }

        }

    }

    private void connectNetwork() {

        if (cracking) {
            ap.mConfig.priority = 1;
            ap.mConfig.status = WifiConfiguration.Status.ENABLED;
            password = passwordGetter.getPassword(); // 从外部字典加载密码
            LogUtil.i("password: --------"+password);
            if (password == null || password.length() == 0) {
                cracking = false;
                showDialogTip("当前模式已破解完成,请更换模式后重新破解", "我知道了",null,null,null);
                return;
            }
            password = "\"" + password + "\"";
            ap.mConfig.preSharedKey = password; // 设置密码
            LogUtil.i("ap信息："+ap.toString());
            if (netid == -1) {
                netid = wifimanager.addNetwork(ap.mConfig);
                ap.mConfig.networkId = netid;
                LogUtil.i( "添加AP失败");
            } else
                wifimanager.updateNetwork(ap.mConfig);
            if (wifimanager.enableNetwork(netid, false)) {
                LogUtil.i("connectNetwork: 启用网络失败");
            }
            currentProgress++;
            wifimanager.saveConfiguration();
            wifimanager.reconnect(); // 连接AP
            LogUtil.i("connectNetwork: " + "连接中。。。。");
        }
    }

    public void update() {

        if (scanResults == null) {

            return;
        }
        if (scanResult == null) {
            for (int i = 0; i < scanResults.size(); i++) {
                scanResult = scanResults.get(i);
                if (scanResult.SSID.equals(listData.get(posint).getName())) {
                    break;
                }
            }
        } else {

            tmpap = new AccessPoint(this, scanResult);
            checkAP();
            return;
        }
        if (scanResult != null) {
            tmpap = new AccessPoint(this, scanResult);
            LogUtil.i("update: " + scanResult.SSID);
            checkAP();
        }
    }

    private void checkAP() {

        if (tmpap.security == AccessPoint.SECURITY_NONE) {
            LogUtil.i("SECURITY_NONE");
            return;
        } else if ((tmpap.security == AccessPoint.SECURITY_EAP)
                || (tmpap.security == AccessPoint.SECURITY_WEP)) {
            LogUtil.i("SECURITY_WEP");
            return;
        }

        showMessageDialog("WIFI热点信息", tmpap.toString(), "破解", true,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogs, int which) {
                        if (select_dialog == null) {
                            select_dialog = new AlertDialog.Builder(WifiCrackActivity2.this);
                            select_dialog.setView(select_dialog_view);
                            selectDialogShow = select_dialog.show();
                        } else {

                            if (selectDialogShow != null && !selectDialogShow.isShowing()) {
                                selectDialogShow.show();

                            }
                        }


                    }
                });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiReceiver!=null) {
            unregisterReceiver(wifiReceiver);
        }
        if (wifiOpenOrClose!=null){
            unregisterReceiver(wifiOpenOrClose);

        }
    }

    private void showMessageDialog(String title, String message,
                                   String positiveButtonText, boolean bShowCancel,
                                   DialogInterface.OnClickListener positiveButtonlistener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, positiveButtonlistener);
        if (bShowCancel) {
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        builder.create().show();
    }

    public void getPassWorld(String ASSET_NAME) {

        if (ASSET_NAME.equals(ASSET_NAME_1)) {

            ASSET_NAME_LENGHT = ASSET_NAME_1_LENGHT;
        } else if (ASSET_NAME.equals(ASSET_NAME_2)) {

            ASSET_NAME_LENGHT = ASSET_NAME_2_LENGHT;
        } else if (ASSET_NAME.equals(ASSET_NAME_3)) {

            ASSET_NAME_LENGHT = ASSET_NAME_3_LENGHT;
        } else if (ASSET_NAME.equals(ASSET_NAME_4)) {
            ASSET_NAME_LENGHT = ASSET_NAME_4_LENGHT;
        }

        try {
            passwordGetter = new PasswordGetter("/sdcard/reality/" + ASSET_NAME);
        } catch (FileNotFoundException e) {
            showMessageDialog("程序初始化失败", "sd卡错误无法初始化密码字典，请检查sd卡", "确定", false,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //WIFICracker.this.finish();
                        }

                    });
        }
    }

    //获取积分
    private void getIntegral(int number) {

        if (integralDialog == null) {
            integralDialog = new AlertDialog.Builder(WifiCrackActivity2.this);
            integralDialog.setTitle("提示");
            integralDialog.setMessage("解锁此功能需要" + number + "积分，当前积分不够,请先获取足够的积分在试");
            integralDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(WifiCrackActivity2.this, IntegralMainActivity.class);
                    startActivity(intent);
                    //前往获取积分
                }
            }).setNegativeButton("取消", null);

            integralDialogshow = integralDialog.show();
        } else if (integralDialogshow != null && !integralDialogshow.isShowing()) {

            integralDialogshow.show();

        }
    }

    private void isIntegralOK() {

        netid = -1;
        currentProgress = 0;
        scanResult = null;
        cracking = true;
        if (crackBuilder == null) {
            crackBuilder = new AlertDialog.Builder(WifiCrackActivity2.this);
            crackBuilder.setView(dialogView);
            tv_title.setText("正在准备");
            crackWifiSSID = listData.get(posint).getName();
            dialog = crackBuilder.show();
            dialog.setCancelable(false);
        } else {

            if (dialog != null && !dialog.isShowing()) {

                dialog.show();
            }
        }
        tv_progress.setText("" + currentProgress + "/" + ASSET_NAME_LENGHT);
        try {
            ap = tmpap;
            connectNetwork(); // 连接网络
            //enablePreferenceScreens(false);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    class WifiOpenOrClose extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {

            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {

                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:

                        if (!isOne) {
                            showDialogTip("当前WiFi已关闭是否重新打开", "打开", "取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    wifimanager.setWifiEnabled(true);
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                        }

                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        LogUtil.i("onReceive: WIFI_STATE_ENABLED");
                        if (isOne) {
                            listView.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    getwifiData();
                                    if (scanResults.size()>0) {
                                        initDialogView();
                                        initData();
                                        getData();
                                    }else {

                                        showDialogTip("当前范围内没有可用WiFi", "我知道了", null, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();
                                            }
                                        },null);
                                    }

                                    progressDialog.dismiss();

                                }
                            }, 2000);

                            isOne=false;
                        }
                        break;
                }
            }
        }
    }



}
