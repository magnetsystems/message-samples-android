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
import com.magnet.magnetchat.ui.views.section.chat.CircleNameView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aorehov on 12.05.16.
 */
public class DefaultMMXUserItemView extends MMXUserItemView<MMXUserItemProperty> {

    TextView uiLetter;
    TextView uiUserName;
    CircleImageView uiUserPic;
    CircleNameView uiUserLetters;
    private View uiBase;

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
        uiBase = baseView;
        uiLetter = findView(baseView, R.id.mmx_character);
        uiUserName = findView(baseView, R.id.mmx_name);
        uiUserPic = findView(baseView, R.id.mmx_pic_origin);
        uiUserLetters = findView(baseView, R.id.mmx_pic_letters);
    }

    @Override
    protected void onShowLetter(boolean isShowLetter, String firstLetter) {
        uiLetter.setText(firstLetter);
        uiLetter.setVisibility(isShowLetter ? VISIBLE : GONE);
    }

    @Override
    protected void onUserPic(String url, final String displayName) {
        if (url == null) {
            uiUserPic.setVisibility(INVISIBLE);
            uiUserLetters.setVisibility(VISIBLE);
            uiUserLetters.setUserName(displayName);
        } else {
            uiUserPic.setVisibility(VISIBLE);
            uiUserLetters.setVisibility(INVISIBLE);
            Glide.with(getContext())
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            onUserPic(null, displayName);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    }).into(uiUserPic);
        }
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
    public void setProperties(MMXUserItemProperty property) {
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        uiBase.setOnClickListener(l);
    }
}
