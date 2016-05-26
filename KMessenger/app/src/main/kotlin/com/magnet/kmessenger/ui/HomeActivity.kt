package com.magnet.kmessenger.ui

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.magnet.kmessenger.R
import com.magnet.magnetchat.layers.ChannelsListContractLayer
import com.magnet.magnetchat.ui.activities.MMXBaseActivity
import com.magnet.magnetchat.ui.activities.MMXChatActivity
import com.magnet.magnetchat.ui.activities.MMXUsersActivity
import com.magnet.magnetchat.ui.custom.AdapteredRecyclerView
import com.magnet.magnetchat.ui.views.channels.AbstractChannelsView

/**
 * Created by aorehov on 26.05.16.
 */
class HomeActivity : MMXBaseActivity() {

    val uiToolbar: Toolbar by lazy { findView<Toolbar>(R.id.toolbar) }
    val uiChannelsList: AbstractChannelsView by lazy { findViewById(R.id.channels) as AbstractChannelsView }

    override fun getLayoutResource(): Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(uiToolbar)
        uiToolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        uiChannelsList.onCreateActivity()
        uiChannelsList.setChannelListCallback(object : ChannelsListContractLayer.OnChannelsListCallback {
            override fun onItemClick(index: Int, `object`: ChannelsListContractLayer.ChannelObject) {
                val channel = `object`.channelDetail.channel
                val intent = MMXChatActivity.createIntent(this@HomeActivity, channel)
                intent?.let { startActivity(it) }
            }

            override fun onActionPerformed(recycleEvent: AdapteredRecyclerView.BaseRecyclerCallback.RecycleEvent?, index: Int, `object`: ChannelsListContractLayer.ChannelObject) {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.create) {
            val intent = MMXUsersActivity.createActivityIntent(this)
            intent?.let { startActivity(intent) }
            return true
        } else
            return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        uiChannelsList.onResumeActivity()
    }


    override fun onPause() {
        uiChannelsList.onPauseActivity()
        super.onPause()
    }

    override fun onDestroy() {
        uiChannelsList.onDestroyActivity()
        super.onDestroy()
    }
}