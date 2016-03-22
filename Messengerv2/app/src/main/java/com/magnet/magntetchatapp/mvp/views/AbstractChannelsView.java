package com.magnet.magntetchatapp.mvp.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.abs.BasePresenterView;
import com.magnet.magntetchatapp.mvp.api.ChannelsListContract;
import com.magnet.magntetchatapp.mvp.presenters.DefaultChannelsPresenter;
import com.magnet.magntetchatapp.ui.custom.AdapteredRecyclerView;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public class AbstractChannelsView extends BasePresenterView<ChannelsListContract.Presenter> implements ChannelsListContract.View {

    private static final String TAG = "AbstractChannelsView";

    /**
     * Class which provide attributes saving
     */
    public static class Attributes {
        private static Attributes instance;
        public boolean attrIsNeedImage;
        protected ColorStateList colorBackground;
        protected ColorStateList colorTint;
        protected ColorStateList colorHeader;
        protected ColorStateList colorMessage;
        protected ColorStateList colorTime;
        protected ColorStateList colorDivider;
        protected String textNoMessages;
        protected String textPhotoMessage;
        protected String textLocationMessage;
        protected String textDateFormat;
        protected boolean isAttributed;

        private Attributes() {
            isAttributed = false;
        }

        /**
         * Method which provide the getting instance of the current singleton
         *
         * @return current singleton instance
         */
        public static Attributes getInstance() {
            if (instance == null) {
                instance = new Attributes();
            }
            return instance;
        }

        public boolean isAttrIsNeedImage() {
            return attrIsNeedImage;
        }

        public ColorStateList getColorBackground() {
            return colorBackground;
        }

        public ColorStateList getColorTint() {
            return colorTint;
        }

        public ColorStateList getColorHeader() {
            return colorHeader;
        }

        public ColorStateList getColorMessage() {
            return colorMessage;
        }

        public ColorStateList getColorTime() {
            return colorTime;
        }

        public ColorStateList getColorDivider() {
            return colorDivider;
        }

        public String getTextNoMessages() {
            return textNoMessages;
        }

        public String getTextPhotoMessage() {
            return textPhotoMessage;
        }

        public String getTextLocationMessage() {
            return textLocationMessage;
        }

        public String getTextDateFormat() {
            return textDateFormat;
        }
    }

    @InjectView(R.id.listChannels)
    AdapteredRecyclerView recyclerView;

    public AbstractChannelsView(Context context) {
        super(context);
    }

    public AbstractChannelsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractChannelsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public ChannelsListContract.Presenter getPresenter() {
        return new DefaultChannelsPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_channels;
    }

    /**
     * Method which provide the attribute initializing
     *
     * @param attrs attributes
     */
    @Override
    protected void onAttributeInitialize(@NonNull AttributeSet attrs) {
        if (Attributes.getInstance().isAttributed == false) {
            TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AbstractChannelsView,
                    0, 0);
            try {
                Attributes attr = Attributes.getInstance();
                if (attr != null) {
                    //Get colors
                    attr.attrIsNeedImage = attributes.getBoolean(R.styleable.AbstractChannelsView_isNeedChatsImage, true);
                    attr.colorBackground = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsBackground);
                    attr.colorHeader = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsHeaderText);
                    attr.colorMessage = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsMessageText);
                    attr.colorTime = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsTimeText);
                    attr.colorTint = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsUnreadTint);
                    attr.colorDivider = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsDivider);

                    //Get texts
                    attr.textDateFormat = attributes.getString(R.styleable.AbstractChannelsView_textChatsDateFormat);
                    attr.textNoMessages = attributes.getString(R.styleable.AbstractChannelsView_textChatsNoMessage);
                    attr.textLocationMessage = attributes.getString(R.styleable.AbstractChannelsView_textChatsLocationMessage);
                    attr.textPhotoMessage = attributes.getString(R.styleable.AbstractChannelsView_textChatsPhotoMessage);

                }
            } finally {
                attributes.recycle();
                Attributes.getInstance().isAttributed = true;
                onApplyAttributes();
            }
        }
    }

    /**
     * Method which provide the UI customizing with accordance to the custom attributes
     */
    @Override
    protected void onApplyAttributes() {
        AbstractChannelsView.Attributes attributes = AbstractChannelsView.Attributes.getInstance();
        if (attributes != null
                && attributes.colorBackground != null) {
            recyclerView.setBackgroundColor(attributes
                    .colorBackground
                    .getColorForState(EMPTY_STATE_SET, android.R.color.transparent));
        }
    }

    @Override
    protected void onCreateView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getCurrentContext(), 1));
    }

    /**
     * Method which provide to getting of the context inside the View/Activity/Fragment
     *
     * @return current view
     */
    @NonNull
    @Override
    public Context getCurrentContext() {
        return getContext();
    }

    /**
     * Method which provide to add the channels to current list object
     *
     * @param objects current objects
     */
    @Override
    public void addChannels(@NonNull List<ChannelsListContract.ChannelObject> objects) {
        recyclerView.addList(objects);
    }

    /**
     * Method which provide to set the channels to current list object
     *
     * @param objects current objects
     */
    @Override
    public void setChannels(@NonNull List<ChannelsListContract.ChannelObject> objects) {
        recyclerView.updateList(objects);
    }

    /**
     * Method which provide to channels clearing
     */
    @Override
    public void clearChannels() {
        recyclerView.clearList();
    }

    /**
     * Method which provide the setting of the channel list callback
     *
     * @param channelListCallback channel list callback
     */
    public void setChannelListCallback(ChannelsListContract.OnChannelListCallback channelListCallback) {
        if (recyclerView != null) {
            recyclerView.setItemActionListener(channelListCallback);
        }
    }
}
