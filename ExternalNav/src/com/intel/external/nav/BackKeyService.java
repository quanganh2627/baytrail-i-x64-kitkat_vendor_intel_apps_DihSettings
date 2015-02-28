/*
 * Copyright (C) 2007 The Android Open Source Project
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
 *
 * author: jianpingx.li@intel.com
 * A service to handle the back key view
 *
 */

package com.intel.external.nav;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.view.WindowManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.graphics.PixelFormat;
import android.content.Context;
import android.hardware.display.DisplayManagerGlobal;
import android.hardware.display.DisplayManager;
import android.view.Display;

import android.util.Log;

public class BackKeyService extends Service {
    private static final String TAG = "BackKeyService";
    private BackKeyView mBackKeyView = null;
    private WindowManager mWindowManager;
    static int DisplayId = 0;

    private WindowManager.LayoutParams getBackKeyLayoutParams() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_NAVIGATION_BAR_EX,
                    0
                    | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSLUCENT);
        // this will allow the navbar to run in an overlay on devices that support this
        if (ActivityManager.isHighEndGfx()) {
            lp.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        }

        lp.setTitle("BackKey");
        lp.windowAnimations = 0;
        return lp;
    }

    protected void makeBackKeyView() {
        mBackKeyView = (BackKeyView) View.inflate(this, R.layout.back_key, null);
        mBackKeyView.setDisabledFlags(0);
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mBackKeyView, getBackKeyLayoutParams());
    }

     @Override
    public void onCreate() {
        makeBackKeyView();
    }

    /**
     * Nobody binds to us.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
	 if(mWindowManager != null){
           mWindowManager.removeViewImmediate(mBackKeyView);
	 }
    }

}

