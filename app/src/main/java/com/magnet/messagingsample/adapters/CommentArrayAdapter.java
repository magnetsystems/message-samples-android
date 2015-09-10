package com.magnet.messagingsample.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magnet.messagingsample.R;
import com.magnet.messagingsample.models.Comment;

public class CommentArrayAdapter extends ArrayAdapter<Comment> {

	private TextView commentView;
	private List<Comment> comments = new ArrayList<Comment>();
	private LinearLayout wrapper;

	@Override
	public void add(Comment object) {
		comments.add(object);
		super.add(object);
	}

	public CommentArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.comments.size();
	}

	public Comment getItem(int index) {
		return this.comments.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.item_chat, parent, false);
		}

		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

		Comment comment = getItem(position);

		commentView = (TextView) row.findViewById(R.id.comment);
		commentView.setText(comment.comment);

		commentView.setBackgroundResource(comment.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
		wrapper.setGravity(comment.left ? Gravity.LEFT : Gravity.RIGHT);

		return row;
	}

}