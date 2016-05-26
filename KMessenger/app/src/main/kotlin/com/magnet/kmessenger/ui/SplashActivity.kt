package com.magnet.kmessenger.ui

import android.os.Bundle
import com.magnet.kmessenger.R
import com.magnet.magnetchat.presenters.SplashContract
import com.magnet.magnetchat.presenters.impl.DefaultSplashPresenter
import com.magnet.magnetchat.ui.activities.MMXBaseActivity

/**
 * Created by aorehov on 26.05.16.
 */
class SplashActivity : MMXBaseActivity(), SplashContract.View {

    val presenter: SplashContract.Presenter by lazy { DefaultSplashPresenter(this) }

    override fun getLayoutResource(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        presenter.onSplashAction()
    }

    override fun navigate(navigationType: SplashContract.NavigationType?) {
        when (navigationType) {
            SplashContract.NavigationType.HOME -> openHomeScreen()
            SplashContract.NavigationType.LOGIN -> openLoginScreen()
        }
    }

    private fun openHomeScreen() = startActivity(HomeActivity::class.java)

    private fun openLoginScreen() = startActivity(LoginActivity::class.java)


}
