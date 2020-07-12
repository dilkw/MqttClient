package com.example.mqttclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mqttclient.mqtt.MqttService;
import com.example.mqttclient.util.HttpUtil;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PubSubTestActivity extends AppCompatActivity implements MqttService.MqttEventCallBack {

    private EditText topicPublish, topicSubscribe, messagePublish;
    private TextView connectState, messaageRecv;
    private MqttService.MqttBinder mqttBinder;
    private String TAG = "PubSubTestActivity";
    private String lastSubscribeTopic = null;

    private ImageView imageView;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttBinder = (MqttService.MqttBinder)iBinder;
            mqttBinder.setMqttEventCallback(PubSubTestActivity.this);
            if(mqttBinder.isConnected()){
                connectState.setText("已连接");
            } else {
                connectState.setText("未连接");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_sub_test);
        topicPublish = findViewById(R.id.topic_publish_et);
        topicSubscribe = findViewById(R.id.topic_sub_et);
        messagePublish = findViewById(R.id.message_publish_et);
        messaageRecv = findViewById(R.id.message_recv_tv);
        connectState = findViewById(R.id.pubsub_connect_state);
        imageView = (ImageView) findViewById(R.id.bing_pic_img);//背景图片
        loadBingPic();

        Intent mqttServiceIntent = new Intent(this, MqttService.class);
        bindService(mqttServiceIntent, connection, Context.BIND_AUTO_CREATE);

        findViewById(R.id.pubsub_publish_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = topicPublish.getText().toString();
                if("".equals(topic)){
                    Toast.makeText(PubSubTestActivity.this, "主题不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    mqttBinder.publishMessage(topic, messagePublish.getText().toString());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.pubsub_ubscribe_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribeNewTopic();
            }
        });
    }

    void subscribeNewTopic(){
        String topic = topicSubscribe.getText().toString();
        if("".equals(topic)){
            Toast.makeText(PubSubTestActivity.this, "主题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(lastSubscribeTopic!=null){
            try {
                mqttBinder.unSubscribe(lastSubscribeTopic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        lastSubscribeTopic = null;
        try {
            mqttBinder.subscribe(topic);
            lastSubscribeTopic = topic;
            Toast.makeText(PubSubTestActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectSuccess() {
        connectState.setText("已连接");
    }

    @Override
    public void onConnectError(String error) {
        Log.d(TAG, "onConnectError: "+error);
        connectState.setText("未连接");
        lastSubscribeTopic = null;
    }

    @Override
    public void onDeliveryComplete() {
        Log.d(TAG, "publish ok");
    }

    @Override
    public void onMqttMessage(String topic, String message) {
        messaageRecv.setText("topic:"+topic+", message:"+message);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
    private void loadBingPic() {
//        String requestBingPic = "http://guolin.tech/api/bing_pic";
        String requestBingPic = "https://img.xjh.me/random_img.php?type=bg&ctype=nature&return=302&device=mobile";
//        String requestBingPic = "http://pic.tsmp4.net/api/fengjing/img.php";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.toString();
                int L = bingPic.length();
                final String temp_bingPic = bingPic.substring(bingPic.indexOf("url=")+4,L-1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(PubSubTestActivity.this).load(temp_bingPic).into(imageView);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

}
