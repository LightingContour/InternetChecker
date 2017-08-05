package com.lightingcontour.internetchecker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn1,btn2;
    ToggleButton btn3;
    Button btn4;
    TextView tv;

    ConnectivityManager manager;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (ToggleButton) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        tv = (TextView) findViewById(R.id.tv);

        manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetWorkSpeedUtils netWorkSpeedUtils = new NetWorkSpeedUtils(MainActivity.this,mhandler);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNetWorkState(checkNetWorkAvailable());
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNetWorkAvailable() == true)
                {
                    checkNetWorkState();
                }else
                {
                    tv.setText("网络未连接！测试网络连接方式已禁用");
                }
            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View v) {
                if (flag == false)
                {
                    btn3.setChecked(true);
                    netWorkSpeedUtils.startShowNetSpeed();
                    flag = true;
                    Toast.makeText(MainActivity.this,"已开启测试网速",Toast.LENGTH_SHORT).show();
                }else
                {
                    btn3.setChecked(false);
                    netWorkSpeedUtils.endShowNetSpeed();
                    flag = false;
                    Toast.makeText(MainActivity.this,"已取消测试网速",Toast.LENGTH_SHORT).show();
                    tv.setText("实时网速测试已关闭");
                }
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNetWorkAvailable() == true)
                {
                    if (getNetWorkState() != 1)
                    {
                        tv.setText("目前不是WiFi连接，功能不可使用");
                    }else
                    {
                        Log.i(TAG,"已点击btn4");
                        int channel = getCurrentChannel(MainActivity.this);
                        tv.setText("目前WiFi信道为：" + channel);
                    }
                }else
                {
                    tv.setText("目前没有网络连接！");
                }
            }
        });
    }


    private Handler mhandler = new Handler()
    {
        @Override
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case 100:
                    tv.setText("当前网速：" +message.obj.toString());
                    break;
            }
            super.handleMessage(message);
        }
    };

    private boolean checkNetWorkAvailable()
    {
        boolean flag = false;

        if (manager.getActiveNetworkInfo() != null)
        {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }

    private void showNetWorkState(boolean flag)
    {
        if (flag == true)
        {
            tv.setText("测试网络状态：网络已连接");
        }else
        {
            tv.setText("测试网络状态：网络未连接");
        }
    }

    private void checkNetWorkState()
    {
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        NetworkInfo.State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (wifi == NetworkInfo.State.CONNECTED)
        {
            tv.setText("测试网络连接方式：连接方式为Wifi");
        }else if (mobile == NetworkInfo.State.CONNECTED)
        {
            tv.setText("测试网络连接方式：连接方式为移动数据");
        }
    }

    private int getNetWorkState()
    {
        int state = -1;
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        NetworkInfo.State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (wifi == NetworkInfo.State.CONNECTED)
        {
            state = 1;
        }else if (mobile == NetworkInfo.State.CONNECTED)
        {
            state = 2;
        }
        return state;
    }

    public static int getCurrentChannel(Context context)
    {
        Log.i(TAG,"使用了getCurrentChannel");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();//当前WiFi链接信息.
        Log.i(TAG,"BSSID:" + wifiInfo.getBSSID() + " SSID:" + wifiInfo.getSSID() + " Speed:" + wifiInfo.getLinkSpeed());
        List<ScanResult> scanResults = wifiManager.getScanResults();
        Log.i(TAG,scanResults.toString());

        for (ScanResult result :scanResults)
        {
            if (result.BSSID.equalsIgnoreCase(wifiInfo.getBSSID()) &&
                    result.SSID.equalsIgnoreCase(wifiInfo.getSSID().substring(
                            1,wifiInfo.getSSID().length() - 1)))
            {
                Log.d(TAG,"将使用getChannelByFrequency");
                return getChannelByFrequency(result.frequency);
            }
        }
        Log.d(TAG,"未使用getChannelByFrequency");
        return -1;
    }

    /*
    * 根据频率获取信道
    *
    * @param frequency
    * @return
    * */
    public static int getChannelByFrequency(int frequency) {
        int channel = -1;
        switch (frequency) {
            case 2412:
                channel = 1;
                break;
            case 2417:
                channel = 2;
                break;
            case 2422:
                channel = 3;
                break;
            case 2427:
                channel = 4;
                break;
            case 2432:
                channel = 5;
                break;
            case 2437:
                channel = 6;
                break;
            case 2442:
                channel = 7;
                break;
            case 2447:
                channel = 8;
                break;
            case 2452:
                channel = 9;
                break;
            case 2457:
                channel = 10;
                break;
            case 2462:
                channel = 11;
                break;
            case 2467:
                channel = 12;
                break;
            case 2472:
                channel = 13;
                break;
            case 2484:
                channel = 14;
                break;
            case 5745:
                channel = 149;
                break;
            case 5765:
                channel = 153;
                break;
            case 5785:
                channel = 157;
                break;
            case 5805:
                channel = 161;
                break;
            case 5825:
                channel = 165;
                break;
        }
        return channel;
    }
}
