/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ishare.ttsengine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by huangyouyang on 2018/6/6.
 */

public class CheckVoiceData extends Activity {

    private final static String[] supportedLanguages = {
            "zho-CHN", "eng-USA"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int result = TextToSpeech.Engine.CHECK_VOICE_DATA_PASS;

        ArrayList<String> available = new ArrayList<>();
        ArrayList<String> unavailable = new ArrayList<>();

        HashMap<String, Boolean> languageCountry = new HashMap<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            ArrayList<String> langCountryVars = bundle.getStringArrayList(
                    TextToSpeech.Engine.EXTRA_CHECK_VOICE_DATA_FOR);
            if (langCountryVars != null){
                for (int i = 0; i < langCountryVars.size(); i++){
                    if (langCountryVars.get(i).length() > 0){
                        languageCountry.put(langCountryVars.get(i), true);
                    }
                }
            }
        }

        // Check for files
        for (int i = 0; i < supportedLanguages.length; i++){
            if ((languageCountry.size() < 1) ||
                    (languageCountry.containsKey(supportedLanguages[i]))){
                available.add(supportedLanguages[i]);
            }
        }

        if (languageCountry.size() > 0) {
            result = TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL;
        }

        Intent returnData = new Intent();
        returnData.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES, available);
        returnData.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES, unavailable);
        setResult(result, returnData);
        finish();
    }
}
