package com.magnet.magntetchatapp.mvp.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.abs.BasePresenterView;
import com.magnet.magntetchatapp.mvp.api.ChannelsListContract;
import com.magnet.magntetchatapp.ui.custom.AdapteredRecyclerView;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public abstract class AbstractChannelsView extends BasePresenterView<ChannelsListContract.Presenter> implements ChannelsListContract.View {

    private static final String TAG = "AbstractChannelsView";

    /**
     * Class which provide attributes saving
     */
    public static class Attributes {
        private static Attributes instance;
        protected boolean isNeedImages;
        protected boolean isNeedBoldHeader;
        protected ColorStateList colorBackground;
        protected ColorStateList colorUnreadTint;
        protected ColorStateList colorHeader;
        protected ColorStateList colorMessage;
        protected ColorStateList colorTime;
        protected ColorStateList colorDivider;
        protected ColorStateList colorBackgroundLoading;
        protected ColorStateList colorTextLoading;
        protected String textNoMessages;
        protected String textPhotoMessage;
        protected String textLocationMessage;
        protected String textDateFormat;
        protected int dimenTextHeader;
        protected int dimenTextMessage;
        protected int dimenTextTime;
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

        public boolean isNeedImages() {
            return isNeedImages;
        }

        public ColorStateList getColorBackground() {
            return colorBackground;
        }

        public ColorStateList getColorUnreadTint() {
            return colorUnreadTint;
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

        public int getDimenTextHeader() {
            return dimenTextHeader;
        }

        public int getDimenTextMessage() {
            return dimenTextMessage;
        }

        public int getDimenTextTime() {
            return dimenTextTime;
        }

        public ColorStateList getColorBackgroundLoading() {
            return colorBackgroundLoading;
        }

        public ColorStateList getColorTextLoading() {
            return colorTextLoading;
        }

        public boolean isNeedBoldHeader() {
            return isNeedBoldHeader;
        }

    }

    @InjectView(R.id.listChannels)
    AdapteredRecyclerView recyclerView;
    @InjectView(R.id.labelLoading)
    AppCompatTextView labelLoading;
    @InjectView(R.id.viewSwipeRefresh)
    SwipeRefreshLayout viewSwipeRefresh;

    public AbstractChannelsView(Context context) {
        super(context);
    }

    public AbstractChannelsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractChannelsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
                    attr.isNeedImages = attributes.getBoolean(R.styleable.AbstractChannelsView_isNeedChatsImage, true);
                    attr.isNeedBoldHeader = attributes.getBoolean(R.styleable.AbstractChannelsView_isNeedBoldHeader, true);
                    attr.colorBackground = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsBackground);
                    attr.colorHeader = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsHeaderText);
                    attr.colorMessage = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsMessageText);
                    attr.colorTime = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsTimeText);
                    attr.colorUnreadTint = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsUnreadTint);
                    attr.colorDivider = attributes.getColorStateList(R.styleable.AbstractChannelsView_colorChatsDivider);
                    attr.colorBackgroundLoading = attributes
                            .getColorStateList(R.styleable.AbstractChannelsView_colorChatsBackgroundLoading);
                    attr.colorTextLoading = attributes
                            .getColorStateList(R.styleable.AbstractChannelsView_colorChatsTextLoading);

                    //Get texts
                    attr.textDateFormat = attributes.getString(R.styleable.AbstractChannelsView_textChatsDateFormat);
                    attr.textNoMessages = attributes.getString(R.styleable.AbstractChannelsView_textChatsNoMessage);
                    attr.textLocationMessage = attributes.getString(R.styleable.AbstractChannelsView_textChatsLocationMessage);
                    attr.textPhotoMessage = attributes.getString(R.styleable.AbstractChannelsView_textChatsPhotoMessage);

                    attr.dimenTextHeader = attributes.getDimensionPixelSize(R.styleable.AbstractChannelsView_dimenTextChatsHeader, R.dimen.text_18);
                    attr.dimenTextTime = attributes.getDimensionPixelSize(R.styleable.AbstractChannelsView_dimenTextChatsTime, R.dimen.text_13);
                    attr.dimenTextMessage =
                            attributes.getDimensionPixelSize(R.styleable.AbstractChannelsView_dimenTextChatsMessage, R.dimen.text_16);


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
        AbstractChannelsView.Attributes attr = AbstractChannelsView.Attributes.getInstance();
        if (attr != null
                && attr.colorBackground != null) {
            recyclerView.setBackgroundColor(attr
                    .colorBackground
                    .getColorForState(EMPTY_STATE_SET, android.R.color.transparent));

            if (attr.getColorBackgroundLoading() != null) {
                labelLoading.setSupportBackgroundTintList(attr.getColorBackgroundLoading());
            }

            if (attr.getColorTextLoading() != null) {
                labelLoading.setTextColor(attr.getColorTextLoading());
            }
        }
    }

    @Override
    protected void onCreateView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getCurrentContext(), 1));
        viewSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (viewSwipeRefresh != null) {
                    viewSwipeRefresh.setRefreshing(false);
                }

                if (presenter != null) {
                    presenter.startChannelReceiving(0);
                }
            }
        });
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
     * Method which provide the setting of the lazy load callback
     *
     * @param lazyLoadCallback lazy load callback
     */
    @Override
    public void setLazyLoadCallback(@NonNull AdapteredRecyclerView.OnLazyLoadCallback lazyLoadCallback) {
        recyclerView.setLazyLoadCallback(lazyLoadCallback);
    }

    /**
     * Method which provide the setting of the loading message
     *
     * @param message    message
     * @param isNeedShow is need show
     */
    @Override
    public void switchLoadingMessage(@Nullable final String message, final boolean isNeedShow) {
        runOnMainThread(0, new OnActionPerformer() {
            @Override
            public void onActionPerform() {
                if (labelLoading != null) {
                    if (message == null || message.isEmpty() == true) {
                        labelLoading.setVisibility(GONE);
                    } else if (isNeedShow == true) {
                        labelLoading.setText(message);
                        labelLoading.setVisibility(VISIBLE);
                    } else {
                        labelLoading.setVisibility(GONE);
                    }
                }
            }
        });
    }

    /**
     * Method which provide the adding of the high priority items
     *
     * @param baseObjects list of the high priority items
     */
    @Override
    public void addHighPriorityItem(@NonNull List<AdapteredRecyclerView.BaseObject> baseObjects) {
        recyclerView.addList(baseObjects);
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

//SAMPLE TO CUSTOMIZE
//    <...AbstractChannelsView
//    android:id="@+id/viewChannels"
//    android:layout_width="match_parent"
//    android:layout_height="match_parent"
//    app:colorChatsBackground="@android:color/black"
//    app:colorChatsDivider="#64dd17"
//    app:colorChatsHeaderText="@android:color/white"
//    app:colorChatsMessageText="#ff6e40"
//    app:colorChatsTimeText="#00b8d4"
//    app:colorChatsUnreadTint="#ffea00"
//    app:dimenTextChatsHeader="@dimen/text_16"
//    app:dimenTextChatsMessage="@dimen/text_14"
//    app:dimenTextChatsTime="@dimen/dimen_10"
//    app:isNeedChatsImage="true"
//    app:textChatsDateFormat="MMM -> hh:mm"
//    app:textChatsLocationMessage="Map message"
//    app:textChatsNoMessage="No more message"
//    app:textChatsPhotoMessage="Instagram message" />
