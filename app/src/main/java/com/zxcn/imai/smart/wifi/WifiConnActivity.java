package com.zxcn.imai.smart.wifi;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zxcn.imai.smart.R;

import java.util.ArrayList;
import java.util.List;

public class WifiConnActivity extends MPermissionsActivity implements OnItemClickListener, OnClickListener {

    private ListView wifiList;
    private Button wifi_conn_refresh_btn;
    private TextView wifi_conn_scan_btn;

    private Switch switch_btn;
    private List<ScanResult> list;
    private ScanResult mScanResult;
    private WifiAdmin mWifiAdmin;
    private WifiConnListAdapter mConnListAdapter;
    private TextView showConn,tv_back;
    private ArrayList<WifiElement> wifi_list = new ArrayList<WifiElement>();
    private boolean isOpen = false;
    private WifiConfiguration wifiConfiguration;
    private String connected_ssid;
    private boolean flag;
    private String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.bg_title_bar));
        setContentView(R.layout.activity_wificonn);
        mWifiAdmin = new WifiAdmin(WifiConnActivity.this);
        initView();
        auto_scan_list();

    }

    private void auto_connect_wifi() {
        List<WifiElement> list = getAllNetWorkList();
        if (null != list) {
            for (WifiElement ls : list) {
                WifiConfiguration configuration = mWifiAdmin.IsExsits(ls.getSsid());
                if (null != configuration) {
                    mWifiAdmin.Connect(configuration);
                    setTag(ls.getSsid());
                    Toast.makeText(WifiConnActivity.this, "自动连接成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void auto_scan_list() {
        if (mWifiAdmin.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            //如果wifi功能打开  自动扫描
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission(new String[]{Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0x0003);
            }
            auto_connect_wifi();
        }
    }

    private void initView() {
        wifiList = (ListView) this.findViewById(R.id.wifi_conn_lv);
        wifi_conn_refresh_btn = (Button) this.findViewById(R.id.wifi_conn_refresh_btn);
        switch_btn = (Switch) this.findViewById(R.id.wifi_conn_cancle_btn);
        wifi_conn_scan_btn = (TextView) this.findViewById(R.id.wifi_conn_scan_btn);
        showConn = (TextView) this.findViewById(R.id.wifi_show_conn);
        tv_back = (TextView) this.findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        showConn.setVisibility(View.INVISIBLE);

        if (mWifiAdmin.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            switch_btn.setChecked(false);
            wifi_conn_scan_btn.setText("开启");
        } else {
            isOpen = true;
            switch_btn.setChecked(true);
            wifi_conn_scan_btn.setText("关闭");
        }

        switch_btn.setOnCheckedChangeListener(new MyOncheckedListener());
        wifi_conn_refresh_btn.setOnClickListener(this);
        wifiList.setOnItemClickListener(this);
        wifi_conn_scan_btn.setOnClickListener(this);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    showConn.setText("已连接:" + initShowConn());
                    Log.e("hhh", "handler执行一次");
                    break;
                case 122:
                   auto_scan_list();
                    break;

            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wifi_conn_refresh_btn:
            if (isOpen){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0x0003);
                }
            }else {
                Toast.makeText(WifiConnActivity.this,"请先开启wifi", Toast.LENGTH_SHORT).show();
            }
                break;
            case R.id.wifi_conn_scan_btn:
                   if (isOpen){//开启
                       wifi_conn_scan_btn.setText("关闭");
                       switch_btn.setChecked(true);
                       isOpen=true;

                   } else {
                       wifi_conn_scan_btn.setText("开启");
                       switch_btn.setChecked(false);
                       isOpen=false;
                   }
                break;

        }
    }

    /**
     * wifi开关监听
     */
    public class MyOncheckedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            //wifi关闭
            if (!b) {
                isOpen = false;
                if (mWifiAdmin.closeWifi()) {
                    Toast.makeText(getApplicationContext(), "wifi关闭成功", Toast.LENGTH_SHORT).show();
                    setTag("");
                    isOpen = false;
                   wifiList.setVisibility(View.INVISIBLE);

                } else {
                    Toast.makeText(getApplicationContext(), "wifi关闭失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                isOpen = true;

                if (mWifiAdmin.OpenWifi()) {
                    Toast.makeText(getApplicationContext(), "wifi打开成功", Toast.LENGTH_SHORT).show();
                    isOpen = true;
                    //自动连接
                    wifiList.setVisibility(View.VISIBLE);
                  handler.sendEmptyMessage(122);
                } else {
                    Toast.makeText(getApplicationContext(), "wifi打开失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String initShowConn() {
        WifiManager wifiManager = (WifiManager)getApplicationContext(). getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        connected_ssid = wifiInfo.getBSSID();
        String s = wifiInfo.getSSID() + "" +
                "IP地址:" + mWifiAdmin.ipIntToString(wifiInfo.getIpAddress()) + "" +
                "Mac地址：" + wifiInfo.getMacAddress();
        return s;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
        // TODO Auto-generated method stub
        final WifiElement element = wifi_list.get(position);
        ssid = element.getSsid();
        final Builder dialog = new AlertDialog.Builder(WifiConnActivity.this);
        wifiConfiguration = mWifiAdmin.IsExsits(ssid);
        dialog.setTitle("是否连接");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dial, int which) {

                        if (element.getCapabilities().equals("[WPS][ESS]")&&wifiConfiguration==null) {//无密码连接
                                //没有密码到时候
                                flag = mWifiAdmin.Connect(ssid, "", WifiAdmin.WifiCipherType.WIFICIPHER_NOPASS);
                                if (flag) {
                                    setTag(ssid);
                                    Toast.makeText(getApplicationContext(), "无密码连接成功", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), "无密码连接失败", Toast.LENGTH_SHORT).show();
                                }

                        } else if (element.getCapabilities().equals("[WPS][ESS]")&&wifiConfiguration!=null){
                                wifiConfiguration=null;
                                flag = mWifiAdmin.Connect(wifiConfiguration);
                                if (flag ){
                                    setTag(ssid);
                                    Toast.makeText(getApplicationContext(), "无密成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    setTag("");
                                    Toast.makeText(getApplicationContext(), "无密失败", Toast.LENGTH_SHORT).show();
                                }
                        }
                        else {      //有密码
                            if (null == wifiConfiguration) {    //无历史记录
                                setMessage(ssid);
                            } else {
                                flag = mWifiAdmin.Connect(wifiConfiguration);
                                if (flag) {
                                    setTag(ssid);
                                } else {
                                    setMessage(ssid);
                                }
                            }
                        }
                        // // TODO: 2017/10/13 handler 消息 改变当前连接
                        handler.sendEmptyMessage(1);
                    }
                }
        ).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).setNeutralButton("移除", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (null != wifiConfiguration) {
                    int id = wifiConfiguration.networkId;
                    System.out.println("id" + id + "SSID" + wifiConfiguration.SSID);
                    mWifiAdmin.removeNetworkLink(id);
                    mConnListAdapter.notifyDataSetChanged();
                    SharedPreferencesUtils.setParam(WifiConnActivity.this, "is_conn", "");
                    handler.sendEmptyMessage(1);
                }
            }
        }).create();
        dialog.show();
    }

    private void setTag(String ssid) {
        SharedPreferencesUtils.setParam(WifiConnActivity.this, "is_conn", "");
        SharedPreferencesUtils.setParam(WifiConnActivity.this, "is_conn", ssid);
       if (null!=mConnListAdapter){
            mConnListAdapter.notifyDataSetChanged();
        }
    }

    private void setMessage(final String ssid) {
        Builder dialog = new AlertDialog.Builder(WifiConnActivity.this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout lay = (LinearLayout) inflater.inflate(R.layout.widget_wifi_pwd, null);
        dialog.setView(lay);
        final EditText pwd = (EditText) lay.findViewById(R.id.wifi_pwd_edit);
        dialog.setTitle(ssid);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String pwdStr = pwd.getText().toString();
                flag = mWifiAdmin.Connect(ssid, pwdStr, WifiAdmin.WifiCipherType.WIFICIPHER_WPA);

                if (flag) {
                    Toast.makeText(getApplicationContext(), "正在连接，请稍后", Toast.LENGTH_SHORT).show();
                    setTag(ssid);
                } else {
                    showLog("密码错误");
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        }).create();
        dialog.show();
    }

    private ArrayList<WifiElement> getAllNetWorkList() {
        // 每次点击扫描之前清空上一次的扫描结果
        wifi_list.clear();
        // 开始扫描网络
        mWifiAdmin.startScan();
        list = mWifiAdmin.getWifiList();

        WifiElement element;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                // 得到扫描结果
                mScanResult = list.get(i);
                element = new WifiElement();
                element.setSsid(mScanResult.SSID);
                element.setBssid(mScanResult.BSSID);
                element.setCapabilities(mScanResult.capabilities);
                element.setFrequency(mScanResult.frequency);
                element.setLevel(mScanResult.level);
                wifi_list.add(element);
            }
        }
        return wifi_list;
    }

    /**
     * 提示信息对话框
     *
     * @param msg
     */
    private void showLog(final String msg) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                Dialog dialog = new AlertDialog.Builder(WifiConnActivity.this).setTitle("提示").setMessage(msg).setNegativeButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                }).create();// 创建
                // 显示对话框
                dialog.show();
            }

        }.execute();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter ins = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        WifiConnActivity.this.registerReceiver(netConnReceiver, ins);

    }

    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        switch (requestCode) {
            case 0x0003:
                mConnListAdapter = new WifiConnListAdapter(WifiConnActivity.this, getAllNetWorkList());
                wifiList.setAdapter(mConnListAdapter);
                break;
        }

    }

    private BroadcastReceiver netConnReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {

                if (checknet()) {
                    Log.d("111111>>>>>>>>>>", "成功");
                    showConn.setText("已连接：   " + initShowConn());
                } else {
                    Log.d("22222222>>>>>>>>>>", "失败");
                    showConn.setText("正在尝试连接：     " + initShowConn());

                }
            }
        }

    };

    /**
     * 获取网络
     */
    private NetworkInfo networkInfo;

    /**
     * 监测网络链接
     *
     * @return true 链接正常 false 链接断开
     */
    private boolean checknet() {
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象
        networkInfo = connManager.getActiveNetworkInfo();
        if (null != networkInfo) {
            return networkInfo.isAvailable();
        }
        return false;
    }

}
