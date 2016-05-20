package com.magnet.magnetchat.ui.views.users;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
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

    TextView uiLetter;
    TextView uiUserName;
    CircleImageView uiUserPic;
    TextView uiLetters;
    private View uiBase;

    private int colorRes;

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
        colorRes = R.color.accent;
        uiBase = baseView;
        uiLetter = findView(baseView, R.id.mmx_character);
        uiUserName = findView(baseView, R.id.mmx_name);
        uiUserPic = findView(baseView, R.id.mmx_msg_pic_origin);
        uiLetters = findView(baseView, R.id.mmx_user_name_letters);
    }

    @Override
    protected void onShowLetter(boolean isShowLetter, String firstLetter) {
        uiLetter.setText(firstLetter);
        uiLetter.setVisibility(isShowLetter ? VISIBLE : GONE);
    }

    @Override
    protected void onUserPic(String url, final String displayName) {
        setUserLetters(displayName);
        if (url != null) {
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
                            uiLetters.setVisibility(GONE);
                            return false;
                        }
                    }).into(uiUserPic);
        }
    }

    private void setUserLetters(String userName) {
        uiUserPic.setImageResource(colorRes);
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
    public void setOnClickListener(OnClickListener l) {
        if (uiBase != this) {
            uiBase.setOnClickListener(l);
        } else {
            super.setOnClickListener(l);
        }
    }

    @Override
    protected void onSelected(boolean selected) {
        uiBase.setBackgroundResource(!selected ? android.R.color.white : android.R.color.darker_gray);
    }
}
