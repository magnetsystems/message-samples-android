/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import com.magnet.magnetchat.R;

public class RoundedTextView extends TextView {
  private float strokeWidth;
  private int solidColor;
  private int strokeColor;

  public RoundedTextView(Context context) {
    super(context);
  }

  public RoundedTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public RoundedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.RoundedTextView,
        0, 0);

    solidColor = a.getColor(R.styleable.RoundedTextView_solidColor, Color.WHITE);
    strokeColor = a.getColor(R.styleable.RoundedTextView_strokeColor, Color.WHITE);
    strokeWidth = a.getFloat(R.styleable.RoundedTextView_strokeWidth, 0);
    setStrokeWidth(strokeWidth);

    a.recycle();

    setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
  }

  @Override
  public void onDraw(Canvas canvas) {

    Paint circlePaint = new Paint();
    circlePaint.setColor(solidColor);
    circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    Paint strokePaint = new Paint();
    strokePaint.setColor(strokeColor);
    strokePaint.setStrokeWidth(strokeWidth);
    strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    int  h = this.getHeight();
    int  w = this.getWidth();

    int diameter = ((h > w) ? h : w);
    int radius = diameter/2;

    this.setHeight(diameter);
    this.setWidth(diameter);

    canvas.drawCircle(diameter / 2 , diameter / 2, radius, strokePaint);

    canvas.drawCircle(diameter / 2, diameter / 2, radius - strokeWidth, circlePaint);

    super.onDraw(canvas);
  }


  private int getColor(int colorValue) {
    int color;

    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      color = getContext().getResources().getColor(colorValue, null);
    }
    else {
      color =  getContext().getResources().getColor(colorValue);
    }

    return color;
  }

  public void setStrokeWidth(float dp) {
    float scale = getContext().getResources().getDisplayMetrics().density;
    this.strokeWidth = dp*scale;

  }
}
