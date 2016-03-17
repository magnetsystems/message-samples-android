package com.magnet.magntetchatapp.ui.activities.section.edit;

import android.view.MenuItem;

import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.ui.activities.section.home.HomeActivity;

public class FirstSetupActivity extends EditProfileActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_first_setup;
    }

    @Override
    protected int getMenuId() {
        return R.menu.menu_first_setup;
    }

    protected void onCreateActivity() {
        editProfileView.onCreateActivity();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_close) {
            startActivityWithClearTop(HomeActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivityWithClearTop(HomeActivity.class);
    }
}
