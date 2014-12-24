package com.intel.DualDisplay;

import android.app.Activity;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.app.AlertActivity;


public class ExternalDisplayRotationSettings extends AlertActivity implements OnCheckedChangeListener {
	
    protected TextView mTitleView;
    protected FrameLayout mContentFrame;
    public RadioButton mPortrait;
    public RadioButton mLandscape;

    public RadioButton mMainPortrait;
    public RadioButton mMainLandscape;

    public String mSelectedRotation;
    public String mOldSelectedRotation;

    public String mOldMainSelectedRotation;
    public String mMainSelectedRotation;

    public String ROTATION_PORTRAIT = "portrait";
    public String ROTATION_LANDSCAPE = "landscape";
    public boolean mSettingChange = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_activity);
        mTitleView = (TextView) findViewById(R.id.alertTitle);
        mContentFrame = (FrameLayout) findViewById(R.id.content);
        setDialogContent(R.layout.dialog);
        setHeading(R.string.external_display_rotation_settings);
        mPortrait = (RadioButton) findViewById(R.id.radioButtonPortrait);
        mLandscape = (RadioButton) findViewById(R.id.radioButtonLandscape);

        mMainPortrait = (RadioButton) findViewById(R.id.radioButtonMainPortrait);
        mMainLandscape = (RadioButton) findViewById(R.id.radioButtonMainLandscape);

        mPortrait.setOnCheckedChangeListener(this);
        mLandscape.setOnCheckedChangeListener(this);

	 mMainPortrait.setOnCheckedChangeListener(this);
        mMainLandscape.setOnCheckedChangeListener(this);

        mOldSelectedRotation = Settings.System.getStringForUser(getContentResolver(),
                Settings.System.DISPLAY_ROTATION_ON_EXTERNAL,UserHandle.USER_CURRENT);
	    if(mOldSelectedRotation.equals(ROTATION_PORTRAIT)){
                mPortrait.setChecked(true);
                mLandscape.setChecked(false);
	    }else{
            mPortrait.setChecked(false);
            mLandscape.setChecked(true);
	    }

        mOldMainSelectedRotation = Settings.System.getStringForUser(getContentResolver(),
                Settings.System.DISPLAY_ROTATION_ON_MAIN,UserHandle.USER_CURRENT);
	    if(mOldMainSelectedRotation.equals(ROTATION_PORTRAIT)){
                mMainPortrait.setChecked(true);
                mMainLandscape.setChecked(false);
	    }else{
            mMainPortrait.setChecked(false);
            mMainLandscape.setChecked(true);
	    }
    }

    public void setHeading(int titleRes) {
        mTitleView.setText(titleRes);
    }

    public void setHeading(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setDialogContent(int layoutRes) {
        mContentFrame.removeAllViews();
        getLayoutInflater().inflate(layoutRes, mContentFrame);
    }

      @Override
    protected void onStop() {
        super.onStop();
        if(mSelectedRotation==null||mSelectedRotation.isEmpty()
			|| mMainSelectedRotation ==null||mMainSelectedRotation.isEmpty())
        	return;
        mSettingChange = !mOldSelectedRotation.equals(mSelectedRotation);
        if(mSettingChange){
            Settings.System.putString(getContentResolver(),
                Settings.System.DISPLAY_ROTATION_ON_EXTERNAL,mSelectedRotation);
//	         Toast.makeText(this,"You must reboot devices to apply the change",Toast.LENGTH_SHORT).show();
        }
	 mSettingChange = !mOldMainSelectedRotation.equals(mMainSelectedRotation);
        if(mSettingChange){
            Settings.System.putString(getContentResolver(),
                Settings.System.DISPLAY_ROTATION_ON_MAIN,mMainSelectedRotation);
//	         Toast.makeText(this,"You must reboot devices to apply the change",Toast.LENGTH_SHORT).show();
        }
    }

    public View getDialogContent() {
        if (mContentFrame.getChildCount() > 0) {
            return mContentFrame.getChildAt(0);
        } else {
            return null;
        }
    }

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		if(arg0 == mPortrait){
			if(arg1){
				mLandscape.setChecked(false);
				mSelectedRotation = ROTATION_PORTRAIT;
			}
		}
		if(arg0 ==mLandscape ){
			if(arg1){
				mPortrait.setChecked(false);
				mSelectedRotation = ROTATION_LANDSCAPE;
			}
		}

		if(arg0 == mMainPortrait){
		    if(arg1){
                     mMainLandscape.setChecked(false);
                     mMainSelectedRotation = ROTATION_PORTRAIT;
                  }
             }
             if(arg0 ==mMainLandscape ){
                  if(arg1){
                      mMainPortrait.setChecked(false);
                      mMainSelectedRotation = ROTATION_LANDSCAPE;
                  }
              }

         }

}
