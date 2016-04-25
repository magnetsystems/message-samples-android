/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ResizedListView extends ListView {
  public ResizedListView(Context context) {
    super(context);
  }

  public ResizedListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ResizedListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 4, MeasureSpec.AT_MOST));
  }
}
