package com.example.bigwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.bigwork.adapter.PhotoAdapter;
import com.example.bigwork.beans.Diary;
import com.example.bigwork.beans.Weatherdata;
import com.example.bigwork.database.DairySave;
import com.example.bigwork.okhttp.OkHttpUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditActivity extends CheckPermissionsActivity{
    private double longitude;
    private double latitude;
    private String jsonStr;
    private Diary diary;
    private String TAG="Diary";
    private String address;
    TextView address_text;
    TextView weather_text;
    List<Uri> mSelected;
    private EditText editText;
    private static final int REQUEST_CODE_CHOOSE = 23;//定义请求码常量
    private Weatherdata weatherdata;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            latitude=amapLocation.getLatitude();//获取纬度
            longitude=amapLocation.getLongitude();//获取经度
            address=amapLocation.getAddress();//地址
            Log.d(TAG, address);
            initweather();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        recyclerView=(RecyclerView)findViewById(R.id.photo_list);
        // 关联toolbar和menu，只需这一句代码菜单就可以正常显示了
        toolbar.inflateMenu(R.menu.toolbar_menu);
        editText = findViewById(R.id.edit_view);
        SharedPreferences prefs=getSharedPreferences("setting", Context.MODE_PRIVATE);
        editText.setTextSize(prefs.getInt("edit_font_size",16));
        editText.requestFocus();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(EditActivity.this,MainActivity.class);
                startActivity(it);
                finish();
            }
        });
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setOnceLocation(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.edit_floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                if(editText.getText()!=null){
                    diary =new Diary(address,weatherdata.getResult().getRealtime().getSkycon().getDec()+" "+weatherdata.getResult().getRealtime().getTemperature()+"℃",editText.getText().toString(),mSelected,simpleDateFormat.format(date));
                }
                Log.d(TAG, diary.getAddress()+ diary.getWeather()+ diary.getText()+ diary.getImg());
                DairySave dairySave =new DairySave(EditActivity.this,"Dairy.db",null,2);
                SQLiteDatabase db=dairySave.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("address", diary.getAddress());
                values.put("weather", diary.getWeather());
                values.put("word", diary.getText());
                values.put("date", diary.getDate());
                if(diary.getImg()!=null) {
                    values.put("img", diary.getImg().toString());
                }
                db.insert("Dairy",null,values);
                values.clear();
                Intent it=new Intent(EditActivity.this,MainActivity.class);
                startActivity(it);
                finish();
            }
        });

    }



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            address_text=(TextView)findViewById(R.id.address_text);
            address_text.setText(address);
            weather_text=(TextView)findViewById(R.id.weather_text);
            weather_text.setText(weatherdata.getResult().getRealtime().getSkycon().getDec()+"  "+weatherdata.getResult().getRealtime().getTemperature()+"℃");
            switch (msg.what) {
                case 1:
                    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.item2:
                                Matisse.from(EditActivity.this)
                                        .choose(MimeType.ofAll())
                                        .countable(true)
                                        .maxSelectable(9)
                                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                        .thumbnailScale(0.85f)
                                        .imageEngine(new GlideEngine())
                                        .showPreview(false) // Default is `true`
                                        .forResult(REQUEST_CODE_CHOOSE);
                                break;
                        }
                        return false;
                    }
                });

            }

        }
    };
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("未保存就退出吗吗？")
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            })
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Intent it=new Intent(EditActivity.this,MainActivity.class);
                                    startActivity(it);
                                    finish();
                                }
                            }).show();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    private void initweather(){
        new Thread(){
            public void run() {
                jsonStr= OkHttpUtils.OkGetArt("https://api.caiyunapp.com/v2.5/4kenoiQ8iVGbEn8N/"+longitude+","+latitude+"/realtime.json",EditActivity.this);
                Log.d(TAG, jsonStr);
                Gson gson=new Gson();
                weatherdata=gson.fromJson(jsonStr,Weatherdata.class);
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }.start();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            PhotoAdapter photoAdapter=new PhotoAdapter(mSelected);
            GridLayoutManager gridLayoutManager=new GridLayoutManager(EditActivity.this,3);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(photoAdapter);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}