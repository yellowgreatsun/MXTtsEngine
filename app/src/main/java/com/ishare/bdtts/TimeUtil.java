/**
 * Creation Date:2015-2-12
 * <p>
 * Copyright
 */
package com.ishare.bdtts;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Description Of The Class<br>
 *
 * @author huangyouyang
 */
public class TimeUtil {

    public static String getTimeStampLocal() {
        Date d = new Date();
        @SuppressLint("SimpleDateFormat")
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return format.format(d);
    }
}
