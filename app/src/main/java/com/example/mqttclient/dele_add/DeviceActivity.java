package com.example.mqttclient.dele_add;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mqttclient.DevicesDemoActivity;
import com.example.mqttclient.R;
import com.example.mqttclient.mqtt.MqttService;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceActivity extends AppCompatActivity
        implements View.OnClickListener,MyLinearLayout.OnScrollListener,MqttService.MqttEventCallBack{
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_device);
//        // 先拿到数据并放在适配器上
//        initFruits(); //初始化水果数据
//        DeviceAdapter adapter=new DeviceAdapter(DeviceActivity.this,R.layout.device_item,deviceList);
//
//        // 将适配器上的数据传递给listView
//        ListView listView=findViewById(R.id.list_view);
//        listView.setAdapter(adapter);
//
//        // 为ListView注册一个监听器，当用户点击了ListView中的任何一个子项时，就会回调onItemClick()方法
//        // 在这个方法中可以通过position参数判断出用户点击的是那一个子项
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Device device=deviceList.get(position);
//                Toast.makeText(DeviceActivity.this,device.getName(),Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // 初始化数据
//    private void initFruits(){
//        for(int i=0;i<10;i++){
//            Device tem=new Device("室内温度",R.drawable.tem,1);
//            deviceList.add(tem);
//            Device hum=new Device("室内湿度",R.drawable.hum,2);
//            deviceList.add(hum);
//            Device pm=new Device("PM2.5",R.drawable.pm,3);
//            deviceList.add(pm);
//            Device gas=new Device("可燃气体浓度",R.drawable.gas,4);
//            deviceList.add(gas);
//            Device waterTower=new Device("大门状态",R.drawable.water,5);
//            deviceList.add(waterTower);
//            Device illuminance=new Device("客厅灯",R.drawable.illuminance,6);
//            deviceList.add(illuminance);
//            Device door=new Device("窗帘",R.drawable.door,7);
//            deviceList.add(door);
//            Device window=new Device("风扇",R.drawable.window,8);
//            deviceList.add(window);
//            Device water=new Device("水位",R.drawable.window,9);
//            deviceList.add(water);
//            Device condition=new Device("空调",R.drawable.window,9);
//            deviceList.add(condition);
//
//            Device d=new Device("灯2",R.drawable.window,10);
//            deviceList.add(d);
//        }
//    }


    public static List<UserDevices> userDeviceList=new ArrayList<>();

    private List<DeviceAdapter.DataHolder> items = new ArrayList<DeviceAdapter.DataHolder>();


    private Button addButton;

    private int delPosition[] = new int[50];//被删除的位置

    private Button cbackButton;

    private MyListView myListView;

    private int array_pos = -1;

    private MyLinearLayout mLastScrollView;

    private DeviceAdapter deviceAdapter;

    private MqttService.MqttBinder mqttBinder;

    private TextView connectState;

    private Map<String, Integer> subscribeTopics = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device);
        //DataSupport.deleteAll(UserDevices.class);
        userDeviceList = DataSupport.findAll(UserDevices.class);
        connectState = findViewById(R.id.connectState);

        items.clear();

        for(int i = 0 ; i < DeviceActivity.userDeviceList.size() ; i++){
            DeviceAdapter.DataHolder item = new DeviceAdapter.DataHolder();
            item.title = DeviceActivity.userDeviceList.get(i).getDeviceName();
            item.type = DeviceActivity.userDeviceList.get(i).getType();
            this.items.add(item);
        }

        //添加设备
        addButton = findViewById(R.id.add_city2);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Intent intent1 = new Intent(DeviceActivity.this,ChooseDeviceActivity.class);
                intent1.putExtra("data_return",delPosition);
                setResult(RESULT_OK,intent1);
                startActivityForResult(intent1,1);

            }
        });

        cbackButton = (Button) findViewById(R.id.city_back_button2);
        cbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        myListView = (MyListView) findViewById(R.id.device_list_view);
        DeviceAdapter deviceAdapter = new DeviceAdapter(DeviceActivity.this,this.items,this,this);
        myListView.setAdapter(deviceAdapter);
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttBinder = (MqttService.MqttBinder) iBinder;
            mqttBinder.setMqttEventCallback(DeviceActivity.this);
            if (mqttBinder.isConnected()) {
                connectState.setText("已连接");
                subscribeTopics();
            } else {
                connectState.setText("未连接");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };


    void subscribeTopics() {
        try {
            subscribeTopics.put("/test/temp",1);
            subscribeTopics.put("/test/hum", 2);
            subscribeTopics.put("/test/pm",3);
            subscribeTopics.put("/test/gas",4);
            subscribeTopics.put("/test/door",5);
            for(Map.Entry<String, Integer> entry : subscribeTopics.entrySet()){
                mqttBinder.subscribe(entry.getKey());
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void unSubscribeTopics() {
        try {
            for(Map.Entry<String, Integer> entry : subscribeTopics.entrySet()){
                mqttBinder.unSubscribe(entry.getKey());
            }
            subscribeTopics.clear();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnScroll(MyLinearLayout view) {
        if (mLastScrollView != null){
            mLastScrollView.smoothScrollTo(0,0);
        }
        mLastScrollView = view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.del){
            array_pos++;
            int position = myListView.getPositionForView(v);
            Log.d("uuu","被删除的位置是："+delPosition[array_pos]);
            DataSupport.deleteAll(UserDevices.class,"id = ?", String.valueOf(DeviceActivity.userDeviceList.get(position).getId()));
            deviceAdapter.removeItem(position);
            deviceAdapter.notifyDataSetChanged();
            delPosition[array_pos] = position+1;
            Log.d("uuu","被删除的位置是："+delPosition[array_pos]);

        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        Log.d("111",intent.toString());
        intent.putExtra("data_return",delPosition);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onConnectSuccess() {

    }

    @Override
    public void onConnectError(String error) {

    }

    @Override
    public void onDeliveryComplete() {

    }

    @Override
    public void onMqttMessage(String topic, String message) {

    }
}
