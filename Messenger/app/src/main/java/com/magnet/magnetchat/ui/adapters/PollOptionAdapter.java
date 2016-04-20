/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

  public PollOptionAdapter(Context context, List<MMXPollOption> objects) {
    super(context, R.layout.item_poll_option, objects);
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

    return convertView;
  }

  public void resetData(List<MMXPollOption> data) {

  }
}
