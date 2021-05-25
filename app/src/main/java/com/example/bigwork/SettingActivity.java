package com.example.bigwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.xw.repo.BubbleSeekBar;


public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        BubbleSeekBar editSeekBar=(BubbleSeekBar)findViewById(R.id.edit_seekBar);
        BubbleSeekBar readSeekBar=(BubbleSeekBar)findViewById(R.id.read_seekBar);
        SharedPreferences prefs=getSharedPreferences("setting",Context.MODE_PRIVATE);
        final TextView textView=(TextView)findViewById(R.id.show_font);
        textView.setTextSize(prefs.getInt("read_font_size",16));
        int edit_font_size=prefs.getInt("edit_font_size",16);
        int read_font_size=prefs.getInt("read_font_size",16);
        editSeekBar.setProgress(edit_font_size);
        readSeekBar.setProgress(read_font_size);
        editSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter(){
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                textView.setTextSize(progress);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                SharedPreferences.Editor editor=getSharedPreferences("setting", Context.MODE_PRIVATE).edit();
                editor.putInt("edit_font_size",progress);
                editor.apply();
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        readSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                textView.setTextSize(progress);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                SharedPreferences.Editor editor=getSharedPreferences("setting", Context.MODE_PRIVATE).edit();
                editor.putInt("read_font_size",progress);
                editor.apply();
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent it=new Intent(SettingActivity.this,MainActivity.class);
            startActivity(it);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}