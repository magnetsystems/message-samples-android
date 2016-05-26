package com.magnet.kmessenger.ui

import android.os.Bundle
import com.magnet.kmessenger.R
import com.magnet.magnetchat.presenters.RegisterContract
import com.magnet.magnetchat.ui.activities.MMXBaseActivity
import com.magnet.magnetchat.ui.views.register.AbstractRegisterView

/**
 * Created by aorehov on 26.05.16.
 */
class RegisterActivity : MMXBaseActivity() {
    val uiRegister: AbstractRegisterView by lazy { findViewById(R.id.register) as AbstractRegisterView }

    override fun getLayoutResource(): Int = R.layout.activity_register

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiRegister.onCreateActivity()
        uiRegister.setRegisterActionCallback(regCallback)
    }

    override fun onResume() {
        super.onResume()
        uiRegister.onResumeActivity()
    }

    override fun onPause() {
        uiRegister.onPauseActivity()
        super.onPause()
    }

    override fun onDestroy() {
        uiRegister.onDestroyActivity()
        super.onDestroy()
    }

    val regCallback = object : RegisterContract.OnRegisterActionCallback {

        override fun onRegisterSuccess() = startActivity(HomeActivity::class.java, true)

        override fun onRegisterError(message: String) = showMessage(message)

    }
}