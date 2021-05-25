package com.example.bigwork;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.bigwork.adapter.DairyAdapter;
import com.example.bigwork.beans.Diary;
import com.example.bigwork.beans.Weatherdata;
import com.example.bigwork.database.DairySave;
import com.example.bigwork.view.ItemTouchHelperCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends CheckPermissionsActivity {
    private DrawerLayout drawerLayout;
    private List<Uri> uriList;
    String TAG="初始化";
    private  List<Diary> diaryList;
    private RecyclerView recyclerView;
    private int lastOffset;
    private int lastPosition;
    DairyAdapter dairyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        recyclerView=(RecyclerView)findViewById(R.id.diary);
        //监听RecyclerView滚动状态
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(recyclerView.getLayoutManager() != null) {
                    getPositionAndOffset();
                }
            }
        });
        NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.sort:
                        if(diaryList.size()!=0){
                            Collections.reverse(diaryList);
                            dairyAdapter.notifyDataSetChanged();
                        }
                        break;
                    case R.id.setting:
                        Intent it=new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(it);
                        finish();
                        break;
                    case R.id.about:
                        Intent it2=new Intent(MainActivity.this,AboutActivity.class);
                        startActivity(it2);
                        finish();
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                startActivity(intent);
                finish();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        initDairy();
        showResult(diaryList);
    }
    @Override
    protected void onStart() {
        super.onStart();
        scrollToPosition();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确定退出[海豚记事本]吗？")
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
                                    finish();
                                }
                            }).show();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    public void  initDairy(){
                DairySave dairySave =new DairySave(MainActivity.this,"Dairy.db",null,2);
                SQLiteDatabase db=dairySave.getWritableDatabase();
                diaryList =new ArrayList<>();
                Cursor cursor=db.query("Dairy",null,null,null,null,null,null);
                while (cursor.moveToNext()) {
                        String address = cursor.getString(cursor.getColumnIndex("address"));
                        String weather = cursor.getString(cursor.getColumnIndex("weather"));
                        String word = cursor.getString(cursor.getColumnIndex("word"));
                        String photos = cursor.getString(cursor.getColumnIndex("img"));
                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        if (photos != null) {
                            uriList=new ArrayList<>();
                            photos = photos.substring(1, photos.length() - 1);
                            List<String> stringList = Arrays.asList(photos.split(", "));
                            for (String str : stringList) {
                                Uri uri = Uri.parse(str);
                                uriList.add(uri);
                            }
                            Log.d("uriList", uriList.toString());
                        }else uriList=null;
                        Diary diary = new Diary(address, weather, word, uriList, date);
                        diaryList.add(diary);
                }
                Collections.reverse(diaryList);
                cursor.close();


    }
    public void showResult(List<Diary> dairies) {
//        initAinm();
        //调用动画方法   一定要在设置数据之前
        if (dairies != null && dairies.size() > 0) {
            dairyAdapter = new DairyAdapter(dairies);
            recyclerView.setAdapter(dairyAdapter);
            //先实例化Callback
            ItemTouchHelperCallback callback = new ItemTouchHelperCallback(dairyAdapter);
            //用Callback构造ItemtouchHelper
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            //调用ItemTouchHelper的attachToRecyclerView方法建立联系
            touchHelper.attachToRecyclerView(recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }

    /** * 记录RecyclerView当前位置 */
    private void getPositionAndOffset() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //获取可视的第一个view
        View topView = layoutManager.getChildAt(0);
        if(topView != null) {
            //获取与该view的顶部的偏移量
            lastOffset = topView.getTop();
            //得到该View的数组位置
            lastPosition = layoutManager.getPosition(topView);
        }
    }

    /** * 让RecyclerView滚动到指定位置 */
    private void scrollToPosition() {
        if(recyclerView.getLayoutManager() != null && lastPosition >= 0) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
        }
    }

}