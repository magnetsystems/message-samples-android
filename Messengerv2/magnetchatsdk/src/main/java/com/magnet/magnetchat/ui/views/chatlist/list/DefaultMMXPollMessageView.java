package com.magnet.magnetchat.ui.views.chatlist.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.magnetchat.presenters.chatlist.MMXMessagePresenterFactory;
import com.magnet.magnetchat.presenters.chatlist.MMXPollContract;
import com.magnet.magnetchat.ui.factories.MMXListItemFactory;
import com.magnet.magnetchat.ui.views.chatlist.poll.AbstractMMXPollItemView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aorehov on 06.05.16.
 */
public abstract class DefaultMMXPollMessageView extends AbstractMMXPollMessageView<MMXPollProperty> implements AbstractMMXPollItemView.OnPollItemClickListener {

    TextView uiPollType;
    TextView uiPollQuestion;
    LinearLayout uiPollQuestions;
    TextView uiSubmit;
    TextView uiSubtitle;
    View uiProgress;
    ImageView uiRefresh;
    View uiDivider;
    View uiPollRoot;

    Map<String, AbstractMMXPollItemView> pollViews = new HashMap<>();
    private MMXListItemFactory factory;

    public DefaultMMXPollMessageView(Context context) {
        super(context);
    }

    public DefaultMMXPollMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultMMXPollMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLinkingViews(View baseView) {
        super.onLinkingViews(baseView);

        factory = ChatSDK.getMmxListItemFactory();

        uiPollType = findView(baseView, R.id.mmx_poll_type);
        uiPollQuestion = findView(baseView, R.id.mmx_poll_question);
        uiPollQuestions = findView(baseView, R.id.mmx_poll_answers);
        uiProgress = findView(baseView, R.id.mmx_progress);
        uiRefresh = findView(baseView, R.id.mmx_update);
        uiSubmit = findView(baseView, R.id.mmx_submit);
        uiDivider = findView(baseView, R.id.mmx_divider);
        uiPollRoot = findView(baseView, R.id.mmx_poll_root);
        uiSubtitle = findView(baseView, R.id.mmx_poll_subtitle);

        uiSubmit.setOnClickListener(this);
        uiRefresh.setOnClickListener(this);
    }

    @Override
    protected MMXPollProperty onReadAttributes(AttributeSet attrs) {
        TypedArray arr = readTypedArray(attrs, R.styleable.DefaultMMXPollMessageView);
        try {
            MMXPollProperty props = new MMXPollProperty();
            props.letters_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_letters_textSize, R.dimen.text_16);
            props.letters_textColor = arr.getColor(R.styleable.DefaultMMXPollMessageView_letters_textColor, -1);

            props.upic_src = arr.getDrawable(R.styleable.DefaultMMXPollMessageView_upic_src);
            props.upic_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_upic_height, -1);
            props.upic_width = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_upic_width, -1);
            props.upic_borderSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_upic_borderSize, -1);
            props.upic_borderColor = arr.getColor(R.styleable.DefaultMMXPollMessageView_upic_borderColor, -1);
            props.upic_marginLeft = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_upic_marginLeft, 0);
            props.upic_marginRight = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_upic_marginRight, 0);
            props.upic_marginTop = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_upic_marginTop, 0);
            props.upic_marginBottom = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_upic_marginBottom, 0);

            props.date_textColor = arr.getColor(R.styleable.DefaultMMXPollMessageView_date_textColor, -1);
            props.date_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_date_textSize, -1);

            props.uname_textSize = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_uname_textSize, -1);
            props.uname_textColor = arr.getColor(R.styleable.DefaultMMXPollMessageView_uname_textColor, 0);
            props.uname_marginLeft = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_uname_marginLeft, getDimensAsPixel(R.dimen.dimen_10));
            props.uname_marginRight = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_uname_marginRight, getDimensAsPixel(R.dimen.dimen_10));
            props.uname_marginTop = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_uname_marginTop, 0);
            props.uname_marginBottom = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_uname_marginBottom, 0);

            props.common_background = arr.getDrawable(R.styleable.DefaultMMXPollMessageView_common_background);

            props.poll_background = arr.getDrawable(R.styleable.DefaultMMXPollMessageView_poll_background);
            props.poll_padding = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_poll_padding, -1);
            props.poll_margin = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_poll_margin, -1);
            props.poll_width = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_poll_width, -1);
            props.poll_factoryName = arr.getString(R.styleable.DefaultMMXPollMessageView_poll_factoryName);

            props.poll_text_color = arr.getColor(R.styleable.DefaultMMXPollMessageView_poll_text_color, -1);
            props.poll_text_type_size = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_poll_text_type_size, -1);
            props.poll_text_question_size = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_poll_text_question_size, -1);
            props.poll_text_submit_size = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_poll_text_submit_size, -1);
            props.poll_text_submit_text = arr.getString(R.styleable.DefaultMMXPollMessageView_poll_text_submit_text);
            props.poll_text_subtitle_size = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_poll_text_submit_size, -1);
            props.poll_text_subtitle_text = arr.getString(R.styleable.DefaultMMXPollMessageView_poll_text_subtitle_text);

            props.poll_divider_color = arr.getColor(R.styleable.DefaultMMXPollMessageView_poll_divider_color, -1);
            props.poll_divider_height = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_poll_divider_height, -1);

            props.poll_refresh_src = arr.getDrawable(R.styleable.DefaultMMXPollMessageView_poll_refresh_src);
            props.poll_refresh_size = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_poll_refresh_size, -1);
            props.poll_refresh_padding = arr.getDimensionPixelSize(R.styleable.DefaultMMXPollMessageView_poll_refresh_padding, -1);

            return props;
        } finally {
            arr.recycle();
        }
    }

    @Override
    protected void onApplyAttributes(MMXPollProperty prop) {
        super.onApplyAttributes(prop);

        if (prop.poll_background != null) uiPollRoot.setBackground(prop.poll_background);
        if (prop.poll_padding != -1)
            uiPollRoot.setPadding(prop.poll_padding, prop.poll_padding, prop.poll_padding, prop.poll_padding);
        if (prop.poll_margin != -1) {
            FrameLayout.LayoutParams params = (LayoutParams) uiPollRoot.getLayoutParams();
            params.setMargins(prop.poll_margin, prop.poll_margin, prop.poll_margin, prop.poll_margin);
        }
        if (prop.poll_width != -1) uiPollRoot.getLayoutParams().width = prop.poll_width;
//        if (prop.poll_factoryName != null) TODO implement here factory finding by name
        if (prop.poll_text_color != -1) {
            uiPollType.setTextColor(prop.poll_text_color);
            uiPollQuestion.setTextColor(prop.poll_text_color);
            uiSubtitle.setTextColor(prop.poll_text_color);
            uiSubmit.setTextColor(prop.poll_text_color);
        }

        if (prop.poll_text_type_size != -1)
            uiPollType.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.poll_text_type_size);
        if (prop.poll_text_question_size != -1)
            uiPollQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.poll_text_question_size);
        if (prop.poll_text_submit_size != -1)
            uiSubmit.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.poll_text_submit_size);
        if (prop.poll_text_submit_text != null) uiSubmit.setText(prop.poll_text_submit_text);
        if (prop.poll_text_subtitle_size != -1)
            uiSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, prop.poll_text_submit_size);
        if (prop.poll_text_subtitle_text != null) uiSubtitle.setText(prop.poll_text_subtitle_text);

        if (prop.poll_divider_color != -1) uiDivider.setBackgroundColor(prop.poll_divider_color);
        if (prop.poll_divider_height != -1)
            uiDivider.getLayoutParams().height = prop.poll_divider_height;

        if (prop.poll_refresh_src != null) uiRefresh.setImageDrawable(prop.poll_refresh_src);
        if (prop.poll_refresh_size != -1) {
            ViewGroup.LayoutParams params = uiRefresh.getLayoutParams();
            params.height = prop.poll_refresh_size;
            params.width = prop.poll_refresh_size;
        }
        if (prop.poll_refresh_padding != -1)
            uiRefresh.setPadding(prop.poll_refresh_padding, prop.poll_refresh_padding, prop.poll_refresh_padding, prop.poll_refresh_padding);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mmx_submit) {
            getPresenter().submitAnswers();
        } else if (v.getId() == R.id.mmx_update) {
            getPresenter().doRefresh();
        } else
            super.onClick(v);
    }

    @Override
    public void onPollType(int resId) {
        uiPollType.setText(resId);
    }

    @Override
    public void onPollQuestion(String question) {
        uiPollQuestion.setText(question);
    }

    @Override
    public void onPollAnswersReceived(List<MMXPollOptionWrapper> data) {
        uiPollQuestions.removeAllViews();
        for (int index = 0; index < data.size(); index++) {
            MMXPollOptionWrapper option = data.get(index);
            updateItem(index, option);
        }
    }

    private void updateItem(int index, MMXPollOptionWrapper option) {
        if (index > 0) {
            View childAt = uiPollQuestions.getChildAt(index);
            if (childAt != null) uiPollQuestions.removeView(childAt);
        }

        AbstractMMXPollItemView view = getView(option);
        uiPollQuestions.addView(view, index);
        view.setObject(option);
    }

    private AbstractMMXPollItemView getView(MMXPollOptionWrapper opt) {
        String id = opt.getId();
        AbstractMMXPollItemView view = pollViews.get(id);
        if (view == null) {
            view = (AbstractMMXPollItemView) factory.createView(getContext(), opt.getType());
            view.setListener(this);
        }

        return view;
    }

    @Override
    public void onShowUserPicture(String url, String name) {
        onSetUserPicOrLetters(url, name);
    }

    @Override
    public void isNeedShowDate(boolean isShowDate) {
        uiDate.setVisibility(isShowDate ? VISIBLE : GONE);
    }

    @Override
    public void onSenderName(String name) {
        if (uiSenderName != null) uiSenderName.setText(name);
    }

    @Override
    protected MMXPollContract.Presenter readPresenter(MMXMessagePresenterFactory factory) {
        return factory.createPollPresenter(this);
    }

    @Override
    public void onClicked(MMXPollOptionWrapper wrapper) {
        getPresenter().onNeedChangedState(wrapper);
    }

    @Override
    public void onEnableSubmitButton(boolean isEnable) {
        uiSubmit.setVisibility(isEnable ? VISIBLE : INVISIBLE);
    }

    @Override
    public void showMessage(CharSequence sequence) {
        toast(sequence);
    }

    @Override
    public void showMessage(int resId, Object... objects) {
        toast(getString(resId, objects));
    }

    @Override
    public void onRefreshingFinished() {
        uiRefresh.setVisibility(VISIBLE);
        uiProgress.setVisibility(GONE);
    }

    @Override
    public void onRefreshing() {
        uiProgress.setVisibility(VISIBLE);
        uiRefresh.setVisibility(GONE);
    }
}
