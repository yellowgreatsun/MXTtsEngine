package com.ishare.bdtts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ishare.ui.PreferenceFragmentDemo;

/**
 * Created by huangyouyang on 2018/6/20.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        startActivity(new Intent(this,BaiduTtsActivity.class));
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragmentDemo()).commit();
    }
}
