package com.example.bigwork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.example.bigwork.adapter.ImageAdapter;
import com.example.bigwork.beans.Diary;
import com.example.bigwork.inter.IOfflineResourceConst;
import com.example.bigwork.util.Auth;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ReadActivity extends AppCompatActivity implements IOfflineResourceConst{
    private List<Diary> diaryList =new ArrayList<>();
    Banner banner;
    TextView textView;
    String dairy_word;
    protected String appId;

    protected String appKey;

    protected String secretKey;
    protected SpeechSynthesizer mSpeechSynthesizer;
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = DEFAULT_SDK_TTS_MODE;
    protected String sn; // 纯离线合成SDK授权码；离在线合成SDK没有此参数

    private boolean isOnlineSDK = TtsMode.ONLINE.equals(DEFAULT_SDK_TTS_MODE);
    // ================ 纯离线sdk或者选择TtsMode.ONLINE  以下参数无用;
    private static final String TEMP_DIR = "/sdcard/baiduTTS"; // 重要！请手动将assets目录下的3个dat 文件复制到该目录

    // 请确保该PATH下有这个文件
    private static final String TEXT_FILENAME = TEMP_DIR + "/" + TEXT_MODEL;

    // 请确保该PATH下有这个文件 ，m15是离线男声
    private static final String MODEL_FILENAME = TEMP_DIR + "/" + VOICE_MALE_MODEL;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    textView.setText(dairy_word);
                    break;
                case 2:
                    GridLayoutManager gridLayoutManager=new GridLayoutManager(ReadActivity.this,1);

            }


        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        appId = Auth.getInstance(this).getAppId();
        appKey = Auth.getInstance(this).getAppKey();
        secretKey = Auth.getInstance(this).getSecretKey();
        textView=(TextView)findViewById(R.id.tv_scrolling);
        SharedPreferences prefs=getSharedPreferences("setting", Context.MODE_PRIVATE);
        textView.setTextSize(prefs.getInt("read_font_size",16));
        banner=(Banner)findViewById(R.id.banner);
        final TextView textView=(TextView)findViewById(R.id.toolbar_title);
        AppBarLayout appBarLayout=(AppBarLayout)findViewById(R.id.app_bar_scrolling);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                Log.d("onOffsetChanged: ", "偏移"+i);
                if(i<-326){
                    textView.setVisibility(VISIBLE);
                }else textView.setVisibility(INVISIBLE);

            }
        });
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object data, int position) {
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setDataAndType(Uri.parse(data.toString()), "image/*");
                startActivity(it);
                Log.d("BannerListener", "已点击");
            }
        });
        initTTs();
        FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.fab_reading);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        List<Uri> diary_img=(List<Uri>) getIntent().getSerializableExtra("img_list");
        if(diary_img!=null){
            for(Uri uri:diary_img) {
                Diary diary = new Diary(uri);
                diaryList.add(diary);
            }
        }
            //自定义的图片适配器，也可以使用默认的BannerImageAdapter
            ImageAdapter adapter = new ImageAdapter(diaryList);
            banner.setAdapter(adapter);
        initContext();
    }
    private void initContext(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent=getIntent();
                dairy_word=intent.getStringExtra("dairy");
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
            }
        }).start();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    private void initTTs() {
        LoggerProxy.printable(true); // 日志打印在logcat中
        boolean isSuccess;
        // 日志更新在UI中，可以换成MessageListener，在logcat中查看日志


        // 1. 获取实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);



        // 3. 设置appId，appKey.secretKey
        mSpeechSynthesizer.setAppId(appId);
        mSpeechSynthesizer.setApiKey(appKey, secretKey);

        // 4. 如果是纯离线SDK需要离线功能的话
        if (!isOnlineSDK) {
            // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);

            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
            // 该参数设置为TtsMode.MIX生效。
            // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线



        }

        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");

        // mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL); // 调整音频输出

        if (sn != null) {
            // 纯离线sdk这个参数必填；离在线sdk没有此参数
            mSpeechSynthesizer.setParam(PARAM_SN_NAME, sn);
        }

        // x. 额外 ： 自动so文件是否复制正确及上面设置的参数
        Map<String, String> params = new HashMap<>();
        // 复制下上面的 mSpeechSynthesizer.setParam参数
        // 上线时请删除AutoCheck的调用
        if (!isOnlineSDK) {
            params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
        }

        // 检测参数，通过一次后可以去除，出问题再打开debug


        // 6. 初始化
        mSpeechSynthesizer.initTts(ttsMode);

    }
    private void speak() {
        /* 以下参数每次合成时都可以修改
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
         *  设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); 设置合成的音量，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5"); 设置合成的语速，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); 设置合成的语调，0-9 ，默认 5
         *
         */

        if (mSpeechSynthesizer == null) {
            return;
        }
        if(dairy_word.length()>256){
            String[] new_dairy_word=dairy_word.split("。");
            for (String page:new_dairy_word){
                mSpeechSynthesizer.speak(page);
            }

        }else mSpeechSynthesizer.speak(dairy_word);


    }
    @Override
    protected void onDestroy() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;

        }
        super.onDestroy();
    }

}