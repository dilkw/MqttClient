package com.example.mqttclient.dele_add;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mqttclient.R;
import java.util.ArrayList;
import java.util.List;

import static android.view.accessibility.AccessibilityEvent.INVALID_POSITION;
import static org.litepal.LitePalApplication.getContext;

public class DeviceAdapter extends BaseAdapter {

    private View.OnClickListener mDelClickListener;

    private MyLinearLayout.OnScrollListener mScrollListener;

    public static interface OnScrollListener{
        public void OnScroll(MyLinearLayout view);
    }


    private Context mContext;

    private ArrayList<String> user_cities;

    private List<DataHolder> mDataList = new ArrayList<DataHolder>();

    private Button curDel_btn;

    private LayoutInflater mInflater;

    FlingListeber listener;

    private View mView;

    private GestureDetector detector;


    private Scroller mScroller;

    private int mlastX = 0;

    private final int MAX_WIDTH = 60;

    private Scroller mPreScroller;

    private MyListView myListView;

    private float x, ux;

    public DeviceAdapter(Context context, List<DataHolder> mDataList, View.OnClickListener delClickListener, MyLinearLayout.OnScrollListener listener) {
        for(int i = 0 ; i < mDataList.size() ; i++){
            Log.d("yyy",mDataList.get(i).title);
        }
        this.mDataList.clear();
        this.mDataList = mDataList;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mDelClickListener = delClickListener;
        mScrollListener = listener;
        this.listener = new FlingListeber();
        detector = new GestureDetector(this.listener);
    }


    public int getCount() {
        return this.mDataList.size();
    }

    public Object getItem(int position) {
        return mDataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = this.mInflater.inflate(R.layout.device_item,parent,false);
            holder = new ViewHolder();
            holder.tvTitle = (TextView)convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DataHolder item = mDataList.get(position);

        holder.tvTitle.setText(item.title);

        item.rootView = (LinearLayout)convertView.findViewById(R.id.lin_root);
        item.rootView.scrollTo(0,0);


        Button delTv = (Button) convertView.findViewById(R.id.del);
        delTv.setOnClickListener(mDelClickListener);

        this.mView = convertView;

//        final DataHolder item2 = mDataList.get(position);
        if(item.equals(null)){
            Log.d("ddd",item.toString());
        }
        listener.setItem(item);
        holder.tvTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return detector.onTouchEvent(event);
            }
        });

        return convertView;
    }


    public void removeItem(int position){
        mDataList.remove(position);
        this.notifyDataSetChanged();
        //更新信息
        DeviceActivity.userDeviceList.remove(position);
//        WeatherActivity.weatherIdList.remove(position);
//        WeatherActivity.user_cityList.remove(position);

    }



    final static class ViewHolder {//删除按键视图
        TextView tvTitle;
        Button btnDel;
    }

    public static class DataHolder {//添加过的城市的视图
        public int type;
        public String title;
        public LinearLayout rootView;
    }



    class FlingListeber implements GestureDetector.OnGestureListener{

        DataHolder item;
        ViewHolder holder;

        public DataHolder getItem() {
            return item;
        }

        public void setItem(DataHolder item) {
            this.item = item;
        }



        public ViewHolder getHolder() {
            return holder;
        }

        public void setHolder(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Log.d("lll","onFling");
//            // TODO Auto-generated method stub
//            if(e2.getX()-e1.getX()>20){
//                Toast.makeText(ctx, "左滑"+item.areaName, 3000).show();
//
//            }else if(e1.getX()-e2.getX()>20){
//                Toast.makeText(ctx, "右滑"+item.areaName, 3000).show();
//            }

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            Log.d("lll","点击");
            return false;
        }

    }

}