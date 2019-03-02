package com.ishare.ui;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.ishare.bdtts.R;


/**
 * Created by huangyouyang on 2018/6/20.
 */

public class PreferenceFragmentDemo extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_demo);
    }

    //重写的以下方法请看后面讲解
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if ("select_linkage".equals(preference.getKey())) {
            CheckBoxPreference checkBox = (CheckBoxPreference) findPreference("select_linkage");
            ListPreference editBox = (ListPreference) findPreference("select_city");
            editBox.setEnabled(checkBox.isChecked());
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
