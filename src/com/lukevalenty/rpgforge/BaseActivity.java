package com.lukevalenty.rpgforge;

import com.google.analytics.tracking.android.EasyTracker;

import roboguice.activity.RoboActivity;

public class BaseActivity extends RoboActivity {
    @Override
    public void onStart() {
      super.onStart();
      EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
      super.onStop();
      EasyTracker.getInstance().activityStop(this);
    }
}
