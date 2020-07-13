package com.example.mqttclient.dele_add;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mqttclient.R;
import com.example.mqttclient.mqtt.MqttService;
import com.example.mqttclient.protocol.AirConditioningMessage;
import com.example.mqttclient.protocol.BoolMessage;
import com.example.mqttclient.protocol.FloatMessage;
import com.example.mqttclient.protocol.IntMessage;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.Map;

public class DeviceDetailActivity extends AppCompatActivity implements MqttService.MqttEventCallBack, CompoundButton.OnCheckedChangeListener {

    private TextView title;
    private TextView values;
    private Switch aSwitch;
    private ImageView imageView;
    private EditText editText;
    private TextView connectState;
 //   private TextView valuesName;
    private AnimationDrawable animation;

    private MqttService.MqttBinder mqttBinder;

    private Map<String, Integer> subscribeTopics = new HashMap<>();
    UserDevices userDevices;
    String deviceName;
    int type;

    Statuss statuss = new Statuss();


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttBinder = (MqttService.MqttBinder) iBinder;
            mqttBinder.setMqttEventCallback(DeviceDetailActivity.this);
            if (mqttBinder.isConnected()) {
                connectState.setText("已连接");
                subscribeTopics();
                Log.d("-------------","连接成功");
            } else {
                connectState.setText("未连接");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    public void onMqttMessage(String topic, String message) {
        Log.d("onMqttMessage", "topic:"+topic+ "message length:"+ message.length() + ", message:"+message);
        Gson gson = new Gson();
        switch (subscribeTopics.get(topic)){
            case 1:
                values.setText(deviceName+String.valueOf(gson.fromJson(message.trim(), FloatMessage.class).value));
                break;

            default:
                values.setText(deviceName+String.valueOf(gson.fromJson(message.trim(), IntMessage.class).value));
                break;

            case 5:
                String status = gson.fromJson(message.trim(), BoolMessage.class).value ?"开":"关";
                values.setText(deviceName+status);
               // statuss.setStatus(status);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        title = findViewById(R.id.title);
        values = findViewById(R.id.values);
        aSwitch = findViewById(R.id.switch1);
        editText = findViewById(R.id.editText);
        connectState = findViewById(R.id.connect_state2);
       // valuesName = findViewById(R.id.name);
        imageView = findViewById(R.id.imageView);













        Intent mqttServiceIntent = new Intent(this, MqttService.class);
        bindService(mqttServiceIntent, connection, Context.BIND_AUTO_CREATE);


        userDevices = (UserDevices) getIntent().getSerializableExtra("device");
//        int m = (int) getIntent().getSerializableExtra("device");
        Toast.makeText(DeviceDetailActivity.this,"点击item",Toast.LENGTH_LONG).show();
        type = userDevices.getType();
        deviceName = userDevices.getDeviceName();
       // String imgUrl = ""+type+".jpg";
        title.setText(deviceName);
      //  imageVie
        if(type > 7) {
            if(type == 20){
                editText.setVisibility(View.VISIBLE);
            }
            aSwitch.setVisibility(View.VISIBLE);
        }else{
            values.setVisibility(View.VISIBLE);
       //     valuesName.setVisibility(View.VISIBLE);
        }
        aSwitch.setOnCheckedChangeListener(this);


        switch (type){
            case 15:
            case 16:
                imageView.setBackgroundResource(R.drawable.animation1415);animation = (AnimationDrawable)imageView.getBackground();animation.setOneShot(false);break;
            case 1: imageView.setImageResource(R.drawable.tem);break;
            case 2: imageView.setImageResource(R.drawable.hum);break;
            case 3: imageView.setImageResource(R.drawable.pm);break;
            case 4: imageView.setImageResource(R.drawable.co2);break;
            case 5: imageView.setImageResource(R.drawable.gas);break;
            case 6: imageView.setImageResource(R.drawable.water);break;
            case 7: imageView.setImageResource(R.drawable.illuminance);break;
            case 8: imageView.setImageResource(R.drawable.human);break;
            case 9: imageView.setImageResource(R.drawable.door_off);break;
            case 10:imageView.setImageResource(R.drawable.window_out);break;
            case 11:
            case 12:
                imageView.setBackgroundResource(R.drawable.animation1112);animation = (AnimationDrawable)imageView.getBackground();animation.setOneShot(false);break;
            case 13:
            case 14:
                imageView.setBackgroundResource(R.drawable.animation1314);animation = (AnimationDrawable)imageView.getBackground();animation.setOneShot(true);break;
            case 17:
            case 18:
                imageView.setBackgroundResource(R.drawable.animation1718);animation = (AnimationDrawable)imageView.getBackground();animation.setOneShot(false);break;
            case 19: imageView.setBackgroundResource(R.drawable.animation19);animation = (AnimationDrawable)imageView.getBackground();animation.setOneShot(false);break;
            case 20:imageView.setBackgroundResource(R.drawable.animation20);animation = (AnimationDrawable)imageView.getBackground();animation.setOneShot(false);break;
        }
    }




    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch (type) {
            case 20:
                try {
                    if (compoundButton.isChecked()) {
                        if ((animation.isRunning())){
                            animation.stop();
                        }
                        animation.start();
                        Log.d("-------------","订阅主题："+userDevices.getTheme());
                        String json = new Gson().toJson(new AirConditioningMessage(true,
                                Float.parseFloat(editText.getText().toString())));
                        Log.d("-------------","点击开关");
                        mqttBinder.publishMessage(userDevices.getTheme(),json);
                    } else {
                        animation.stop();
                        String json = new Gson().toJson(new AirConditioningMessage(false,
                                Float.parseFloat(editText.getText().toString())));
                        Log.d("json",json);
                        mqttBinder.publishMessage(userDevices.getTheme(),json);
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    if (compoundButton.isChecked()) {
                        Log.d("-------------","订阅主题："+userDevices.getTheme());
                        Log.d("-------------","点击开关");
                        //animation.setOneShot(false);
                        if ((animation.isRunning())){
                            animation.stop();
                        }
                            animation.start();

                        mqttBinder.publishMessage(userDevices.getTheme(),
                                new Gson().toJson(new BoolMessage(true)));
                    } else {
                        animation.stop();
                        mqttBinder.publishMessage(userDevices.getTheme(),
                                new Gson().toJson(new BoolMessage(false)));
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    @Override
    public void onConnectSuccess() {
        subscribeTopics();
        connectState.setText("已连接");
    }

    @Override
    public void onConnectError(String error) {
        Log.d("DD", "onConnectError: " + error);
        connectState.setText("未连接");
        subscribeTopics.clear();
    }

    @Override
    public void onDeliveryComplete() {
        Log.d("DDD", "publish ok");
    }

//    @Override
//    public void onMqttMessage(String topic, String message) {
//        Log.d("onMqttMessage", "topic:"+topic+ "message length:"+ message.length() + ", message:"+message);
//        Gson gson = new Gson();
//        switch (subscribeTopics.get(topic)){
//            case 1:
//                values.setText(deviceName+String.valueOf(gson.fromJson(message.trim(), FloatMessage.class).value));
//                break;
//
//            default:
//                values.setText(deviceName+String.valueOf(gson.fromJson(message.trim(), IntMessage.class).value));
//                break;
//
//            case 5:
//                String status = gson.fromJson(message.trim(), BoolMessage.class).value ?"开":"关";
//                values.setText(deviceName+status);
//
//                break;
//        }
//    }



    void subscribeTopics() {
        try {
            subscribeTopics.put(userDevices.getTheme(),1);
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

//    @Override
//    public void onMqttMessage(String topic, String message) {
//        Log.d("onMqttMessage", "topic:"+topic+ "message length:"+ message.length() + ", message:"+message);
//        Gson gson = new Gson();
//        switch (subscribeTopics.get(topic)){
//            case 1:
//                temperatureValue.setText(String.valueOf(gson.fromJson(message.trim(), FloatMessage.class).value));
//                break;
//
//            case 2:
//                humidityValue.setText(String.valueOf(gson.fromJson(message.trim(), IntMessage.class).value));
//                break;
//
//            case 3:
//                pmValue.setText(String.valueOf(gson.fromJson(message.trim(), IntMessage.class).value));
//                break;
//
//            case 4:
//                gasValue.setText(String.valueOf(gson.fromJson(message.trim(), IntMessage.class).value));
//                break;
//
//            case 5:
//                String status = gson.fromJson(message.trim(), BoolMessage.class).value ?"开":"关";
//                doorStatus.setText(status);
//                break;
//        }
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mqttBinder.isConnected()) {
            connectState.setText("已连接");
            subscribeTopics();
        } else {
            connectState.setText("未连接");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unSubscribeTopics();
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
