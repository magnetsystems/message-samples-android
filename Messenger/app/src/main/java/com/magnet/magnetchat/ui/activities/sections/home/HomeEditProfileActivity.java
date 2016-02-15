package com.magnet.magnetchat.ui.activities.sections.home;

import com.magnet.magnetchat.ui.activities.sections.register.EditProfileActivity;

/**
 * Created by Artli_000 on 11.02.2016.
 */
public class HomeEditProfileActivity extends EditProfileActivity {

    @Override
    public void onBackPressed() {
        showProgress(false);
        super.onBackPressed();
    }

}
