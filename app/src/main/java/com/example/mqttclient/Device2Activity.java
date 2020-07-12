package com.example.mqttclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mqttclient.dele_add.DeviceActivity;
import com.example.mqttclient.dele_add.DeviceAdapter;
import com.example.mqttclient.dele_add.DeviceDetailActivity;
import com.example.mqttclient.dele_add.UserDevices;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class Device2Activity extends AppCompatActivity {

    private List<UserDevices> items = new ArrayList<UserDevices>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device2);
        //connectState = findViewById(R.id.connectState);

        //items.clear();
        items = DataSupport.findAll(UserDevices.class);

        Device2Adapter adapter=new Device2Adapter(Device2Activity.this,R.layout.device_item2,items);
        ListView listView=findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        // 为ListView注册一个监听器，当用户点击了ListView中的任何一个子项时，就会回调onItemClick()方法
        // 在这个方法中可以通过position参数判断出用户点击的是那一个子项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDevices userDevice = items.get(position);
                Intent intent = new Intent();
                intent.setClass(Device2Activity.this, DeviceDetailActivity.class);
                // 新建Bundle对象
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("device", userDevice);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
    }
}
