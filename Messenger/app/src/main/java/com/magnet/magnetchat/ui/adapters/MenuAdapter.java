package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.ChannelCacheManager;

public class MenuAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;

    private class ViewHolder {
        TextView title;
        TextView countNew;
        LinearLayout llNew;
    }

    public MenuAdapter(Context context, String[] items) {
        super(context, R.layout.item_home_menu, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null || null == convertView.getTag()) {
            convertView = inflater.inflate(R.layout.item_home_menu, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.tvMenuItemTitle);
            viewHolder.countNew = (TextView) convertView.findViewById(R.id.tvMenuItemCountNew);
            viewHolder.llNew = (LinearLayout) convertView.findViewById(R.id.llMenuItemNew);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String item = getItem(position);
        viewHolder.title.setText(item);
        int newSupportMessages = ChannelCacheManager.getInstance().getSupportUnreadCount();
        if (item.equals("Support") && newSupportMessages > 0) {
            viewHolder.llNew.setVisibility(View.VISIBLE);
            viewHolder.countNew.setText(String.valueOf(newSupportMessages));
        } else {
            viewHolder.llNew.setVisibility(View.GONE);
        }
        return convertView;
    }
}
