package com.ishare.bdtts;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.ishare.ttsengine.MoxiangApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BaiduTtsActivity extends Activity {

    private final static String TAG = "HYY";
    protected SpeechSynthesizer mSpeechSynthesizer;
    protected String appId = "11339376";
    protected String appKey = "pDNxhNXvFWAnOBGgqLoBcGsD";
    protected String secretKey = "kj8HKzVHqst0eRIkfsGapPKabK4wxBB1";
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = TtsMode.MIX;

    private MoxiangApplication mApp;
    private Button btnSpeak;
    private Button btnStop;
    private Button btnSave;
    private boolean isNeedSaveTTS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_tts);

        mApp = (MoxiangApplication) getApplication();
        initEngine();
        initView();
        initListener();
    }

    private void initView() {

        btnSpeak = findViewById(R.id.btn_speak);
        btnStop = findViewById(R.id.btn_stop);
        btnSave = findViewById(R.id.btn_save);
    }

    private void initListener() {

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNeedSaveTTS = false;
                String text = "青青子衿，悠悠我心。";
                mSpeechSynthesizer.speak(text);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpeechSynthesizer.stop();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNeedSaveTTS = true;
                String text = "青青子衿，悠悠我心。";
                mSpeechSynthesizer.synthesize(text);
            }
        });
    }

    private void initEngine() {

        // 1. 获取实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);

        // 2. 设置listener
        mSpeechSynthesizer.setSpeechSynthesizerListener(speechSynthesizerListener);

        // 3. 设置appId，appKey.secretKey
        mSpeechSynthesizer.setAppId(appId);
        mSpeechSynthesizer.setApiKey(appKey, secretKey);

        // 4. 支持离线的话，需要设置离线模型
        if (ttsMode.equals(TtsMode.MIX)) {
            // 检查离线授权文件是否下载成功，离线授权文件联网时SDK自动下载管理，有效期3年，3年后的最后一个月自动更新。
            if (!checkAuth()) {
                return;
            }
            // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mApp.getTextModeFile());
            // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mApp.getSpeechModeFile());
        }

        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "4");
        // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI);
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // 设置播放器的音频流类型
        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_NORMAL);
        // 不使用压缩传输
        // mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE, SpeechSynthesizer.AUDIO_ENCODE_PCM);
        // mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE, SpeechSynthesizer.AUDIO_BITRATE_PCM);

        // 6. 初始化
        mSpeechSynthesizer.initTts(ttsMode);
    }

    /**
     * 检查appId ak sk 是否填写正确，另外检查官网应用内设置的包名是否与运行时的包名一致。本demo的包名定义在build.gradle文件中
     *
     * @return
     */
    private boolean checkAuth() {
        AuthInfo authInfo = mSpeechSynthesizer.auth(ttsMode);
        if (!authInfo.isSuccess()) {
            // 离线授权需要网站上的应用填写包名
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            return false;
        } else {
            return true;
        }
    }

    private String destDir= mApp.getOutputDir().getPath();
    private File ttsFile;
    private FileOutputStream ttsFileOutputStream;
    private BufferedOutputStream ttsFileBufferedOutputStream;

    SpeechSynthesizerListener speechSynthesizerListener = new SpeechSynthesizerListener() {

        @Override
        public void onSynthesizeStart(String s) {
            //合成开始
            Log.i(TAG, "speechSynthesizerListener onSynthesizeStart");

            if (isNeedSaveTTS) {
                String filename = TimeUtil.getTimeStampLocal() + ".pcm";
                // 保存的语音文件是 16K采样率 16bits编码 单声道 pcm文件。
                ttsFile = new File(destDir, filename);
                try {
                    if (ttsFile.exists()) {
                        ttsFile.delete();
                    }
                    ttsFile.createNewFile();
                    FileOutputStream ttsFileOutputStream = new FileOutputStream(ttsFile);
                    ttsFileBufferedOutputStream = new BufferedOutputStream(ttsFileOutputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSynthesizeDataArrived(String s, byte[] data, int i) {
            // 合成过程中的数据回调接口
            Log.i(TAG, "speechSynthesizerListener onSynthesizeDataArrived s=" + s);

            if(isNeedSaveTTS){
                try {
                    ttsFileBufferedOutputStream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSynthesizeFinish(String s) {
            // 合成结束
            Log.i(TAG, "speechSynthesizerListener onSynthesizeFinish s=" + s);

            if (isNeedSaveTTS)
                close();
        }

        @Override
        public void onSpeechStart(String s) {
            // 播放开始
            Log.i(TAG, "speechSynthesizerListener onSpeechStart s=" + s);
        }

        @Override
        public void onSpeechProgressChanged(String s, int i) {
            // 播放过程中的回调
            Log.i(TAG, "onSpeechProgressChanged onSpeechProgressChanged s=" + s);
        }

        @Override
        public void onSpeechFinish(String s) {
            // 播放结束
            Log.i(TAG, "onSpeechProgressChanged onSpeechFinish s=" + s);
        }

        @Override
        public void onError(String s, SpeechError speechError) {
            // 合成和播放过程中出错时的回调
            Log.e(TAG, "onSpeechProgressChanged onError s=" + s+" error="+speechError.toString());
            if (isNeedSaveTTS)
                close();
        }
    };

    private void close() {
        if (ttsFileBufferedOutputStream != null) {
            try {
                ttsFileBufferedOutputStream.flush();
                ttsFileBufferedOutputStream.close();
                ttsFileBufferedOutputStream = null;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (ttsFileOutputStream != null) {
            try {
                ttsFileOutputStream.close();
                ttsFileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
