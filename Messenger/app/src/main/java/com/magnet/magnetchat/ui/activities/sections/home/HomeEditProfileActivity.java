package com.magnet.magnetchat.ui.activities.sections.home;

import com.magnet.magnetchat.ui.activities.sections.register.EditProfileActivity;

public class HomeEditProfileActivity extends EditProfileActivity {

    @Override
    public void onBackPressed() {
        showProgress(false);
        finish();
    }

}
