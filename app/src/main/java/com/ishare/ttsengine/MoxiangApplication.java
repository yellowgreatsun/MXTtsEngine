package com.ishare.ttsengine;

import android.app.Application;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by huangyouyang on 2018/6/8.
 */

public class MoxiangApplication extends Application {

    static File baseDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), Const.MY_BASE_DIR);
    static File offlineDir = new File(baseDir, Const.OFFLINE_VOICE_DIR);
    static File outputDir = new File(baseDir, Const.OUTPUT_DIR);

    private AssetManager assets;

    @Override
    public void onCreate() {
        super.onCreate();

        this.assets = getAssets();
        copyAssetsFile("bd_etts_text.dat");
        copyAssetsFile("bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat");
        copyAssetsFile("bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat");
        copyAssetsFile("bd_etts_common_speech_yyjw_mand_eng_high_am-mix_v3.0.0_20170512.dat");
        copyAssetsFile("bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat");
    }

    public String getTextModeFile(){
       return getOfflineDir().getPath() + "/" + "bd_etts_text.dat";
    }

    public String getSpeechModeFile(){
        return getOfflineDir().getPath() + "/" + "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat";
    }

    public static File getOfflineDir() {

        if (!offlineDir.isDirectory()) {
            offlineDir.delete();
            offlineDir.mkdirs();
        }
        return offlineDir;
    }

    public static File getOutputDir() {

        if (!outputDir.isDirectory()) {
            outputDir.delete();
            outputDir.mkdirs();
        }
        return outputDir;
    }

    private  void copyAssetsFile(String sourceFilename){

        String destFilename = getOfflineDir().getAbsolutePath() + "/" + sourceFilename;
        try {
            copyFromAssets(assets, sourceFilename, destFilename);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void copyFromAssets(AssetManager assets, String source, String dest) throws IOException {

        File file = new File(dest);
        if (!file.exists()) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = assets.open(source);
                fos = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }
        }
    }
}
