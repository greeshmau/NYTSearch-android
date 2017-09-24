package com.codepath.gumapathi.nytsearch;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by gumapathi on 9/23/2017.
 */

public class NYTApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // This instantiates DBFlow
        FlowManager.init(new FlowConfig.Builder(this).build());
        // add for verbose logging
        // FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);

        //custom font init
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cheltenham_normal_200.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
