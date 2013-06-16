package com.lukevalenty.rpgforge;

import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;

import roboguice.activity.RoboActivity;

public class BaseActivity extends RoboActivity {
    protected final String TAG = this.getClass().getCanonicalName();
    
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

    @Override 
    public void onPause() {
        super.onPause();
        Log.d(TAG, "SAVING FILE: " + RpgForgeApplication.getDbFile());
        RpgForgeApplication.save(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        return;
    }

    protected int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }
}

