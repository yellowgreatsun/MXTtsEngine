package com.ishare.ttsengine;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.ishare.MoxiangApplication;

import java.util.Locale;

/**
 * Created by huangyouyang on 2018/6/6.
 */

public class MoxiangTtsService extends TextToSpeechService {

    private static final String TAG = "MoxiangTtsService";
    private MoxiangApplication mApp;
    private volatile String[] mCurrentLanguage = null;
    private static final int SAMPLING_RATE_HZ = 16000;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (MoxiangApplication) getApplication();
        initEngine();
    }

    @Override
    public void onDestroy() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
        }
        super.onDestroy();
    }

    // 过时
    @Override
    protected String[] onGetLanguage() {
        return mCurrentLanguage;
    }

    @Override
    protected int onIsLanguageAvailable(String lang, String country, String variant) {

//        Log.i(TAG, "onIsLanguageAvailable lang=" + lang + " country=" + country + " variant=" + variant);

        if ((Locale.SIMPLIFIED_CHINESE.getISO3Language().equals(lang)) || (Locale.US.getISO3Language().equals(lang))) {
            if ((Locale.SIMPLIFIED_CHINESE.getISO3Country().equals(country)) || (Locale.US.getISO3Country().equals(country)))
                return TextToSpeech.LANG_COUNTRY_AVAILABLE;
            return TextToSpeech.LANG_AVAILABLE;
        }
        return TextToSpeech.LANG_NOT_SUPPORTED;
    }

    @Override
    protected synchronized int onLoadLanguage(String lang, String country, String variant) {
        mCurrentLanguage = new String[] { lang, country, ""};
        return onIsLanguageAvailable(lang, country, variant);
    }

    @Override
    protected void onStop() {
        if(mSpeechSynthesizer!=null){
            mSpeechSynthesizer.stop();
        }
    }

    private SynthesisCallback mCallback;
    @Override
    protected synchronized void onSynthesizeText(SynthesisRequest request, SynthesisCallback callback) {

        Log.i(TAG, "onSynthesizeText lang=" + request.getLanguage() + " country=" + request.getCountry()
                + " variant=" + request.getVariant() + " name=" + request.getVoiceName()
                + " text=" + request.getCharSequenceText());

        this.mCallback = callback;
        int load = onLoadLanguage(request.getLanguage(), request.getCountry(), request.getVariant());
        if (load == TextToSpeech.LANG_NOT_SUPPORTED) {
            this.mCallback.error();
            return;
        }
        this.mCallback.start(SAMPLING_RATE_HZ, AudioFormat.ENCODING_PCM_16BIT, 1);

        final String text = request.getCharSequenceText().toString();
        mSpeechSynthesizer.synthesize(text);
        isSynthesizing = true;
        while (isSynthesizing) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        mCallback.done();
    }

    protected SpeechSynthesizer mSpeechSynthesizer;
    protected String appId = "11339376";
    protected String appKey = "pDNxhNXvFWAnOBGgqLoBcGsD";
    protected String secretKey = "kj8HKzVHqst0eRIkfsGapPKabK4wxBB1";
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = TtsMode.MIX;
    private boolean isSynthesizing = true;

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
            // 离线授权需要网站上的应用填写包名。本demo的包名是com.baidu.tts.sample，定义在build.gradle中
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            return false;
        } else {
            return true;
        }
    }


    SpeechSynthesizerListener speechSynthesizerListener = new SpeechSynthesizerListener() {

        @Override
        public void onSynthesizeStart(String s) {
            //合成开始
//            Log.i(TAG, "speechSynthesizerListener onSynthesizeStart");
        }

        @Override
        public void onSynthesizeDataArrived(String s, byte[] data, int i) {
            // 合成过程中的数据回调接口
//            Log.i(TAG, "speechSynthesizerListener onSynthesizeDataArrived s=" + s+" data="+data.length);

            final int maxBufferSize = mCallback.getMaxBufferSize();
            int offset = 0;
            while (offset < data.length) {
                int bytesToWrite = Math.min(maxBufferSize, data.length - offset);
                mCallback.audioAvailable(data, offset, bytesToWrite);
//                Log.i(TAG, "speechSynthesizerListener audioAvailable bytesToWrite=" + bytesToWrite);
                offset += bytesToWrite;
            }
        }

        @Override
        public void onSynthesizeFinish(String s) {
            // 合成结束
//            Log.i(TAG, "speechSynthesizerListener onSynthesizeFinish s=" + s);

            isSynthesizing = false;
            if (mCallback!=null) {
                mCallback.done();
            }
        }

        @Override
        public void onSpeechStart(String s) {
            // 播放开始
//            Log.i(TAG, "speechSynthesizerListener onSpeechStart s=" + s);
        }

        @Override
        public void onSpeechProgressChanged(String s, int i) {
            // 播放过程中的回调
//            Log.i(TAG, "speechSynthesizerListener onSpeechProgressChanged s=" + s);
        }

        @Override
        public void onSpeechFinish(String s) {
            // 播放结束
//            Log.i(TAG, "speechSynthesizerListener onSpeechFinish s=" + s);
            isSynthesizing = false;
        }

        @Override
        public void onError(String s, SpeechError speechError) {
            // 合成和播放过程中出错时的回调
            Log.e(TAG, "speechSynthesizerListener onError speechError=" + speechError.toString());
            isSynthesizing = false;
            if (mCallback != null)
                mCallback.error();
        }
    };
}
