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
import android.widget.TextView;
import com.magnet.max.android.User;
import com.magnet.samples.android.howto.R;
import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {

    private final LayoutInflater inflater;

    private class ViewHolder {
        TextView username;
    }

    public UserListAdapter(Context context, List<User> objects) {
        super(context, R.layout.item_user, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.username = (TextView) convertView.findViewById(R.id.userName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        User message = getItem(position);
        viewHolder.username.setText(message.getUserName());
        return convertView;
    }

}
