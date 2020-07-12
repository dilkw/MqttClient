package com.example.mqttclient.dele_add;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mqttclient.PubSubTestActivity;
import com.example.mqttclient.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ChooseDeviceActivity extends AppCompatActivity {

    private TextView titleText;

    private ListView listView;

    private Button backButton;

    private ArrayAdapter<String> adapter;

    private List<Device> deviceList;

    private List<String> dataList = new ArrayList<>();

    private Boolean isAdd = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_device);
        titleText = (TextView) findViewById(R.id.title_text);

//        DataSupport.deleteAll(UserDevices.class);
//        DataSupport.deleteAll(Device.class);

        deviceList = DataSupport.findAll(Device.class);

        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });


        adapter = new ArrayAdapter<String>(ChooseDeviceActivity.this, android.R.layout.simple_expandable_list_item_1, dataList);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        queryDevice();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int type = deviceList.get(position).getType();
                    String userDevice_name = deviceList.get(position).getDeviceName();
                    String theme = deviceList.get(position).getTheme();
                    Log.d("ddddddd主题dddddd",theme);
                    int deviceId = deviceList.get(position).getId();

                    UserDevices userDevices = new UserDevices();
                    userDevices.setDeviceName(userDevice_name);
                    userDevices.setType(type);
                    userDevices.setId(deviceId);
                    userDevices.setTheme(theme);
                    userDevices.save();

                    DeviceActivity.userDeviceList.add(userDevices);
                    //Log.d("add_city_name",userCity_name);
                    isAdd = true;
                    Intent intent = new Intent();
                    intent.putExtra("data_return",isAdd);
                    setResult(RESULT_OK,intent);
                    Toast.makeText(ChooseDeviceActivity.this,"添加设备成功，可点击“查看设备”查看", Toast.LENGTH_SHORT).show();
                    finish();
                }
        });
    }


    private void queryDevice() {
        titleText.setText("设备列表");
        backButton.setVisibility(View.VISIBLE);
        deviceList = DataSupport.findAll(Device.class);
        if (dataList.size() > 0) {
            dataList.clear();
            for (Device device : deviceList) {
                dataList.add(device.getDeviceName());
            }
        } else {
            //SQLiteDatabase db = dbHelper.getWritableDatabase();
            deviceList.add(new Device(1,"tem温度",1,"/test/temp"));
            deviceList.add(new Device(2,"hum湿度",2,"/test/hum"));
            deviceList.add(new Device(3,"PM2.5",3,"/test/pm"));
            deviceList.add(new Device(4,"co2二氧化碳浓度",4,"/test/co2"));
            deviceList.add(new Device(5,"gas可燃气体浓度",5,"/test/gas"));
            deviceList.add(new Device(6,"waterTower水塔水位",6,"/test/waterTower"));
            deviceList.add(new Device(7,"illuminance光照度",7,"/test/illuminance"));
            deviceList.add(new Device(8,"human人",8,"/test/human"));
            deviceList.add(new Device(9,"door门",9,"/test/door"));
            deviceList.add(new Device(10,"window窗",10,"/test/window"));
            deviceList.add(new Device(11,"light1",11,"/test/light1"));
            deviceList.add(new Device(12,"light2",12,"/test/light2"));
            deviceList.add(new Device(13,"窗帘1",13,"/test/curtain1"));
            deviceList.add(new Device(14,"窗帘2",14,"/test/curtain2"));
            deviceList.add(new Device(15,"风扇1",15,"/test/fan1"));
            deviceList.add(new Device(16,"风扇2",16,"/test/fan2"));
            deviceList.add(new Device(17,"插座1",17,"/test/socket1"));
            deviceList.add(new Device(18,"插座2",18,"/test/socket2"));
            deviceList.add(new Device(19,"抽水机",19,"/test/waterPump"));
            deviceList.add(new Device(20,"空调",20,"/test/airConditioning"));
            for(Device device:deviceList){
                dataList.add(device.getDeviceName());
                device.save();
            }
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
    }
}
