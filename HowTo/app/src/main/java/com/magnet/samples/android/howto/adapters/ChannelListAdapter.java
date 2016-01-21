/*
 *  Copyright (c) 2016 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magnet.samples.android.howto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.samples.android.howto.R;
import com.magnet.samples.android.howto.util.Logger;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ChannelListAdapter extends ArrayAdapter<MMXChannel> {

    private final LayoutInflater inflater;

    private class ViewHolder {
        TextView name;
        TextView tags;
        TextView time;
        ImageView locking;
    }

    public ChannelListAdapter(Context context, List<MMXChannel> objects) {
        super(context, R.layout.item_channel, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_channel, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.channelItemName);
            viewHolder.tags = (TextView) convertView.findViewById(R.id.channelItemTags);
            viewHolder.time = (TextView) convertView.findViewById(R.id.channelItemTime);
            viewHolder.locking = (ImageView) convertView.findViewById(R.id.channelItemLocking);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MMXChannel channel = getItem(position);
        viewHolder.name.setText(channel.getName());
        viewHolder.time.setText(getDateString(channel.getLastTimeActive()));
        setTagsToLabel(channel, viewHolder.tags);
        if (channel.isPublic()) {
            viewHolder.locking.setImageResource(R.mipmap.ic_unlock);
        } else {
            viewHolder.locking.setImageResource(R.mipmap.ic_lock);
        }
        return convertView;
    }

    private String getDateString(Date date) {
        return DateFormat.getDateTimeInstance().format(date);
    }

    private void setTagsToLabel(MMXChannel channel, final TextView tagsLabel) {
        channel.getTags(new MMXChannel.OnFinishedListener<HashSet<String>>() {
            @Override
            public void onSuccess(HashSet<String> strings) {
                Logger.debug("get tags", "success");
                String text = "Tags : ";
                Iterator<String> iterator = strings.iterator();
                while (iterator.hasNext()) {
                    text += iterator.next();
                    if (iterator.hasNext()) {
                        text += ", ";
                    }
                }
                tagsLabel.setText(text);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Logger.error("set gags", throwable, "error : ", failureCode);
            }
        });
    }

}
