/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.magnet.magnetchat.R;
import java.util.ArrayList;
import java.util.List;

public class PollOptionItemAdapter extends RecyclerView.Adapter<PollOptionItemAdapter.PollOptionItemViewHolder> {

  private Context context;
  private List<String> options;

  public PollOptionItemAdapter(Context context, List<String> options) {
    this.context = context;
    this.options = null != options ? options : new ArrayList<String>();
  }

  @Override public PollOptionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.item_poll_option, parent, false);
    return new PollOptionItemViewHolder(view);
  }

  @Override public void onBindViewHolder(PollOptionItemViewHolder holder, int position) {
    if (holder != null) {
      holder.tvText.setText(options.get(position));
    }
  }

  @Override public int getItemCount() {
    return null != options ? options.size() : 0;
  }

  public void addOption(String option) {
    options.add(option);
    notifyItemInserted(options.size() - 1);
  }

  public List<String> getOptions() {
    return options;
  }

  public class PollOptionItemViewHolder extends RecyclerView.ViewHolder {

    TextView tvText;

    public PollOptionItemViewHolder(View itemView) {
      super(itemView);

      tvText = (TextView) itemView.findViewById(R.id.tvText);
    }
  }
}
