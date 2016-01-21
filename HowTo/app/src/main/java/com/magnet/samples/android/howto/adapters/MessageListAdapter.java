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
import com.magnet.max.android.Attachment;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.samples.android.howto.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MessageListAdapter extends ArrayAdapter<MMXMessage> {

    private final LayoutInflater inflater;

    private class ViewHolder {
        TextView message;
        ImageView imageView;
    }

    public MessageListAdapter(Context context, List<MMXMessage> objects) {
        super(context, R.layout.item_message, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_message, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.message = (TextView) convertView.findViewById(R.id.msgText);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.msgImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MMXMessage message = getItem(position);
        List<Attachment> attachments = message.getAttachments();
        if (attachments.size() > 0) {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            Attachment att = attachments.get(0);

            //Load image by Picasso
            if(null != att.getAttachmentId()) {
                Picasso.with(getContext()).load(att.getDownloadUrl()).into(viewHolder.imageView);
            }

            // Or download it to file
            //try {
            //    File attFile = new File(getContext().getFilesDir(), att.getName());
            //    if (!attFile.exists()) {
            //        att.download(attFile, new Attachment.DownloadAsFileListener() {
            //            @Override
            //            public void onComplete(File file) {
            //                Logger.debug("download image", file.getAbsolutePath());
            //                viewHolder.imageView.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
            //            }
            //
            //            @Override
            //            public void onError(Throwable throwable) {
            //                Logger.error("download image", throwable);
            //            }
            //        });
            //    } else {
            //        viewHolder.imageView.setImageDrawable(Drawable.createFromPath(attFile.getAbsolutePath()));
            //    }
            //} catch (Exception e) {
            //    Logger.error("download image exception", e);
            //}
        } else {
            viewHolder.imageView.setVisibility(View.GONE);
        }
        String messageText = message.getContent().get("content");
        viewHolder.message.setText(messageText);
        return convertView;
    }

}
