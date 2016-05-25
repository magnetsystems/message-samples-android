package com.magnet.magnetchat.ui.views.users;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.MMXObjectsHelper;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aorehov on 12.05.16.
 */
public class DefaultMMXUserItemView extends MMXUserItemView<MMXUserItemProperty> {

    LinearLayout uiUserView;
    TextView uiLetter;
    TextView uiUserName;
    CircleImageView uiUserPic;
    TextView uiLetters;
    View uiUserPicView;
    View uiBase;
    View uiDivider;

    //    private int colorRes;
    private Drawable colorUserCircle;
    private Drawable colorDefault;
    private Drawable colorSelected;
    private boolean isLoading;

    public DefaultMMXUserItemView(Context context) {
        super(context);
    }

    public DefaultMMXUserItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXUserItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLinkingViews(View baseView) {
        colorUserCircle = new ColorDrawable(getColor(R.color.accent));
        colorDefault = new ColorDrawable(getColor(android.R.color.white));
        colorSelected = new ColorDrawable(getColor(android.R.color.darker_gray));
        uiBase = baseView;
        uiLetter = findView(baseView, R.id.mmx_character);
        uiUserName = findView(baseView, R.id.mmx_name);
        uiUserPic = findView(baseView, R.id.mmx_msg_pic_origin);
        uiUserPicView = findView(baseView, R.id.mmx_user_pic);
        uiLetters = findView(baseView, R.id.mmx_user_name_letters);
        uiUserView = findView(baseView, R.id.mmx_user_view);
        uiDivider = findView(baseView, R.id.mmx_divider);
    }

    @Override
    protected void onShowLetter(boolean isShowLetter, String firstLetter) {
        uiLetter.setText(firstLetter);
        uiLetter.setVisibility(isShowLetter ? VISIBLE : GONE);
    }

    @Override
    protected void onUserPic(String url, final String displayName) {
        isLoading = false;
        setUserLetters(displayName);
        if (url != null) {
            isLoading = true;
            Glide.with(getContext())
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            setUserLetters(displayName);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (isLoading) {
                                uiLetters.setVisibility(GONE);
                                uiUserPic.setImageDrawable(resource);
                                isLoading = false;
                            }
                            return true;
                        }
                    }).preload();
        }
    }

    private void setUserLetters(String userName) {
        uiUserPic.setImageDrawable(colorUserCircle);
        uiLetters.setText(MMXObjectsHelper.getLettersFromName(userName));
        uiLetters.setVisibility(VISIBLE);
    }


    @Override
    protected void onDisplayName(String displayName) {
        SpannableStringBuilder builder = new SpannableStringBuilder(displayName);
        int indexOf = displayName.indexOf(' ');
        if (indexOf != -1) {
            builder.setSpan(new StyleSpan(Typeface.BOLD), indexOf, displayName.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        uiUserName.setText(displayName);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_user_item;
    }

    @Override
    protected void onCreateView() {

    }

    @Override
    protected MMXUserItemProperty onReadAttributes(AttributeSet attrs) {
        TypedArray arr = readTypedArray(attrs, R.styleable.DefaultMMXUserItemView);
        try {
            MMXUserItemProperty prop = new MMXUserItemProperty();

            prop.header_letter_background = arr.getColor(R.styleable.DefaultMMXUserItemView_header_letter_background, -1);
            prop.header_letter_textColor = arr.getColor(R.styleable.DefaultMMXUserItemView_header_letter_textColor, -1);
            prop.header_letter_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_header_letter_textSize, -1);
            prop.header_letter_padding = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_header_letter_padding, -1);

            prop.letters_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_letters_textSize, -1);
            prop.letters_textColor = arr.getColor(R.styleable.DefaultMMXUserItemView_letters_textColor, -1);

            prop.upic_src = arr.getDrawable(R.styleable.DefaultMMXUserItemView_upic_src);
            prop.upic_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_upic_height, -1);
            prop.upic_width = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_upic_width, -1);
            prop.upic_borderSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_upic_borderSize, -1);
            prop.upic_borderColor = arr.getColor(R.styleable.DefaultMMXUserItemView_upic_borderColor, -1);
            prop.upic_marginLeft = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_upic_marginLeft, getDimensAsPixel(R.dimen.dimen_15));
            prop.upic_marginRight = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_upic_marginRight, getDimensAsPixel(R.dimen.dimen_15));
            prop.upic_marginTop = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_upic_marginTop, getDimensAsPixel(R.dimen.dimen_7));
            prop.upic_marginBottom = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_upic_marginBottom, getDimensAsPixel(R.dimen.dimen_7));

            prop.common_background = arr.getDrawable(R.styleable.DefaultMMXUserItemView_common_background);
            prop.common_backgroundSelected = arr.getDrawable(R.styleable.DefaultMMXUserItemView_common_backgroundSelected);
            prop.common_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_common_height, -1);

            prop.uname_textColor = arr.getColor(R.styleable.DefaultMMXUserItemView_uname_textColor, -1);
            prop.uname_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_uname_textSize, -1);
            prop.uname_padding = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_uname_padding, -1);

            prop.divider_color = arr.getColor(R.styleable.DefaultMMXUserItemView_divider_color, -1);
            prop.divider_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXUserItemView_divider_height, -1);

            return prop;
        } finally {
            arr.recycle();
        }
    }

    @Override
    protected void onApplyAttributes(MMXUserItemProperty prop) {
        if (prop.header_letter_background != -1)
            uiLetter.setBackgroundColor(prop.header_letter_background);
        if (prop.header_letter_textColor != -1)
            uiLetter.setTextColor(prop.header_letter_textColor);
        if (prop.header_letter_textSize != -1)
            uiLetter.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.header_letter_textSize);
        if (prop.header_letter_padding != -1)
            uiLetter.setPadding(prop.header_letter_padding, prop.header_letter_padding, prop.header_letter_padding, prop.header_letter_padding);

        if (prop.letters_textSize != -1)
            uiLetters.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.letters_textSize);
        if (prop.letters_textColor != -1) uiLetters.setTextColor(prop.letters_textColor);

        if (prop.upic_src != null) {
            uiUserPic.setImageDrawable(prop.upic_src);
            colorUserCircle = prop.upic_src;
        }
        ViewGroup.LayoutParams upicParams = uiUserPicView.getLayoutParams();
        if (prop.upic_height != -1) upicParams.height = prop.upic_height;
        if (prop.upic_width != -1) upicParams.width = prop.upic_width;
        if (prop.upic_borderSize != -1) uiUserPic.setBorderWidth(prop.upic_borderSize);
        if (prop.upic_borderColor != -1) uiUserPic.setBorderColor(prop.upic_borderColor);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) uiUserPicView.getLayoutParams();
        params.setMargins(prop.upic_marginLeft, prop.upic_marginTop, prop.upic_marginRight, prop.upic_marginBottom);

        if (prop.common_background != null) {
            uiBase.setBackground(prop.common_background);
            colorDefault = prop.common_background;
        }
        if (prop.common_backgroundSelected != null) {
            colorSelected = prop.common_backgroundSelected;
        }
        if (prop.common_height != -1) uiUserView.getLayoutParams().height = prop.common_height;

        if (prop.uname_textColor != -1) uiUserName.setTextColor(prop.uname_textColor);
        if (prop.uname_textSize != -1)
            uiUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.uname_textSize);
        if (prop.uname_padding != -1)
            uiUserName.setPadding(prop.uname_padding, prop.uname_padding, prop.uname_padding, prop.uname_padding);

        if (prop.divider_color != -1) uiDivider.setBackgroundColor(prop.divider_color);
        if (prop.divider_height != -1) uiDivider.getLayoutParams().height = prop.divider_height;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (uiBase != this) {
            uiBase.setOnClickListener(l);
        } else {
            super.setOnClickListener(l);
        }
    }

    @Override
    protected void onSelected(boolean selected) {
        uiBase.setBackground(!selected ? colorDefault : colorSelected);
    }
}
