package com.magnet.kmessenger.ui

import android.os.Bundle
import com.magnet.kmessenger.R
import com.magnet.magnetchat.presenters.LoginContract
import com.magnet.magnetchat.ui.activities.MMXBaseActivity
import com.magnet.magnetchat.ui.views.login.AbstractLoginView

/**
 * Created by aorehov on 26.05.16.
 */

class LoginActivity : MMXBaseActivity() {

    val uiLogin: AbstractLoginView by lazy { findViewById(R.id.login) as AbstractLoginView }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiLogin.loginActionCallback = loginCallback
        uiLogin.onCreateActivity()
    }

    override fun onPause() {
        uiLogin.onPauseActivity()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        uiLogin.onResumeActivity()
    }

    override fun onDestroy() {
        uiLogin.onDestroyActivity()
        super.onDestroy()
    }

    override fun getLayoutResource(): Int = R.layout.activity_login

    val loginCallback = object : LoginContract.OnLoginActionCallback {
        override fun onLoginSuccess() = startActivity(HomeActivity::class.java)

        override fun onLoginError(errorMessage: String) = showMessage(errorMessage)

        override fun onRegisterPressed() = startActivity(RegisterActivity::class.java)
    }

}