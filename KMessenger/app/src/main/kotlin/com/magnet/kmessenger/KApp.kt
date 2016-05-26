package com.magnet.kmessenger

import android.app.Application
import com.magnet.kmessenger.R
import com.magnet.magnetchat.ChatSDK
import com.magnet.max.android.Max
import com.magnet.max.android.config.MaxAndroidPropertiesConfig

/**
 * Created by aorehov on 26.05.16.
 */
class KApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Max.init(this, MaxAndroidPropertiesConfig(this, R.raw.magnetmax))
        ChatSDK.init(this)
    }

}