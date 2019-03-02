package com.ishare.speech;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ishare.bdtts.R;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Created by huangyouyang on 2018/5/8.
 */

public class TestSpeechActivity extends AppCompatActivity {

    private final static String TAG = "HYY ";
    private Button btnTts;
    private Button btnSave;

    private static TextToSpeech textToSpeech;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_speech_test);
        initView();
        initSpeech();
        initListener();
    }

    private void initView() {
        btnTts = findViewById(R.id.btn_tts);
        btnSave = findViewById(R.id.btn_save);
    }

    private void initSpeech() {

        // 1.创建TextToSpeech对象，创建时传入OnInitListener监听器监听创建是否成功
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // status : TextToSpeech.SUCCESS=0 , TextToSpeech.ERROR=-1
                Log.i(TAG, "TextToSpeech onInit status = " + status);
                if (status==TextToSpeech.SUCCESS){
                    getSpeechParams();
                }
            }
        });
    }

    public void getSpeechParams() {

        List<TextToSpeech.EngineInfo> engineInfoList = textToSpeech.getEngines();
        StringBuilder builderEngineInfo = new StringBuilder("");
        for (TextToSpeech.EngineInfo engineInfo : engineInfoList) {
            builderEngineInfo.append(" ").append(engineInfo.name).append("&").append(engineInfo.label).append(" |");
        }
        Log.i(TAG, " engineInfoList = " + builderEngineInfo.toString());

        String defaultEngine = textToSpeech.getDefaultEngine();
        Log.i(TAG, " defaultEngine = " + defaultEngine);

        StringBuilder builderVoice = new StringBuilder("");
        Set<Voice> voiceList = textToSpeech.getVoices();
        for (Voice vioce:voiceList){
            builderVoice.append(" ").append(vioce.getName()).append(" |");
        }
        Log.i(TAG, " voiceList = " + builderVoice.toString());

        Voice defaultVoice = textToSpeech.getDefaultVoice();
        if (defaultVoice != null)
            Log.i(TAG, " defaultVoice = " + defaultVoice.toString());
    }

    private void initListener() {

        btnTts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 2.设置TextToSpeech所使用语言、国家选项
                int result = textToSpeech.setLanguage(Locale.CHINESE);
                Log.i(TAG, " textToSpeech.setLanguage result = " + result);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                } else {
                    String text = "蒹葭苍苍 白露为霜";
                    // 3.调用speak
                    int state = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, Integer.toString(new Random().nextInt()));
                    Log.i(TAG, "speak state : " + state);
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.i(TAG, " UtteranceProgressListener onStart " + utteranceId);
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.i(TAG, " UtteranceProgressListener onDone " + utteranceId);
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.i(TAG, " UtteranceProgressListener onError " + utteranceId);
                        }
                    });
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 2.设置TextToSpeech所使用语言、国家选项
                int result = textToSpeech.setLanguage(Locale.CHINESE);
                Log.i(TAG, " textToSpeech.setLanguage result = " + result);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                } else {
                    String text="青青子衿 悠悠我心";
                    String filename = "tts.mp3";
                    String destDir = "/sdcard/Documents/MXTts/output";
                    File ttsFile = new File(destDir, filename);
                    // 3.调用synthesizeToFile
                    int state = textToSpeech.synthesizeToFile(text, null, ttsFile, Integer.toString(new Random().nextInt()));
                    Log.i(TAG, "synthesizeToFile state : " + state);

                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.i(TAG, " UtteranceProgressListener onStart " + utteranceId);
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.i(TAG, " UtteranceProgressListener onDone " + utteranceId);
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.i(TAG, " UtteranceProgressListener onError " + utteranceId);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // 4.关闭TTS，回收资源
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
