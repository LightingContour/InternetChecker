package com.lightingcontour.internetchecker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn1,btn2,btn3;
    TextView tv;

    ConnectivityManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
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
                    netWorkSpeedUtils.startShowNetSpeed();
                    flag = true;
                    Toast.makeText(MainActivity.this,"已开启测试网速",Toast.LENGTH_SHORT).show();
                }else
                {
                    netWorkSpeedUtils.endShowNetSpeed();
                    flag = false;
                    Toast.makeText(MainActivity.this,"已取消测试网速",Toast.LENGTH_SHORT).show();
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
            tv.setText("测试网络连接方式：连接方式为WIFI");
        }else if (mobile == NetworkInfo.State.CONNECTED)
        {
            tv.setText("测试网络连接方式：连接方式为移动数据");
        }
    }
}
