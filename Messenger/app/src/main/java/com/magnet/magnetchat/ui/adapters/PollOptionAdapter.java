/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.magnet.magnetchat.R;
import com.magnet.mmx.client.ext.poll.MMXPollOption;
import java.util.ArrayList;
import java.util.List;

public class PollOptionAdapter extends ArrayAdapter<MMXPollOption> {

  boolean showCount;

  public PollOptionAdapter(Context context, List<MMXPollOption> objects, boolean showCount) {
    super(context, R.layout.item_poll_option, new ArrayList<MMXPollOption>(objects));
    this.showCount = showCount;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // Get the data item for this position
    MMXPollOption option = getItem(position);
    // Check if an existing view is being reused, otherwise inflate the view
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_poll_option, parent, false);
    }
    // Lookup view for data population
    TextView tvText = (TextView) convertView.findViewById(R.id.tvText);
    tvText.setText(option.getText());

    if(showCount) {
      TextView tvCount = (TextView) convertView.findViewById(R.id.tvCount);
      tvCount.setText(String.valueOf(option.getCount()));
    }

    return convertView;
  }

  public void resetData(List<MMXPollOption> data) {
    Log.d("PollOptionAdapter", "---------reseting data to " + data);
    clear();
    addAll(data);
    notifyDataSetChanged();
  }

  public void setShowCount(boolean value) {
    this.showCount = value;
  }
}
