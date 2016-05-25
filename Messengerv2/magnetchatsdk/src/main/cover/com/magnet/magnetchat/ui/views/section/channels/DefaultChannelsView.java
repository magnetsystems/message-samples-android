package com.magnet.magnetchat.ui.views.section.channels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.filters.ArrayFilter;
import com.magnet.magnetchat.presenters.ChannelsListContract;
import com.magnet.magnetchat.ui.views.AbstractChannelsView;
import com.magnet.magnetchat.ui.custom.AdapteredRecyclerView;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dlernatovich on 3/24/16.
 */
public class DefaultChannelsView extends AbstractChannelsView {

    public DefaultChannelsView(Context context) {
        super(context);
    }

    public DefaultChannelsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultChannelsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

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

    AdapteredRecyclerView recyclerView;

    AppCompatTextView labelLoading;
    ViewGroup viewProgress;
    ProgressBar progressLoading;

    SwipeRefreshLayout viewSwipeRefresh;

    private String filterQuery;
    private List<ChannelsListContract.ChannelObject> fullArrayObject;
    private ChannelsListContract.OnChannelsViewCallback channelsViewCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.view_channels_cover;
    }

    /**
     * Method which provide the interface linking
     */
    @Override
    protected void onLinkInterface() {
        recyclerView = (AdapteredRecyclerView) findViewById(R.id.listChannels);
        labelLoading = (AppCompatTextView) findViewById(R.id.labelLoading);
        viewSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.viewSwipeRefresh);
        viewProgress = (ViewGroup) findViewById(R.id.viewProgress);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoading);
    }

    /**
     * Method which provide the attribute initializing
     *
     * @param attrs attributes
     */
    @Override
    protected void onAttributeInitialize(@NonNull AttributeSet attrs) {
        if (Attributes.getInstance().isAttributed == false) {
            TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DefaultChannelsView,
                    0, 0);
            try {
                Attributes attr = Attributes.getInstance();
                if (attr != null) {
                    //Get colors
                    attr.isNeedImages = attributes.getBoolean(R.styleable.DefaultChannelsView_isNeedChatsImage, true);
                    attr.isNeedBoldHeader = attributes.getBoolean(R.styleable.DefaultChannelsView_isNeedBoldHeader, true);
                    attr.colorBackground = attributes.getColorStateList(R.styleable.DefaultChannelsView_colorChatsBackground);
                    attr.colorHeader = attributes.getColorStateList(R.styleable.DefaultChannelsView_colorChatsHeaderText);
                    attr.colorMessage = attributes.getColorStateList(R.styleable.DefaultChannelsView_colorChatsMessageText);
                    attr.colorTime = attributes.getColorStateList(R.styleable.DefaultChannelsView_colorChatsTimeText);
                    attr.colorUnreadTint = attributes.getColorStateList(R.styleable.DefaultChannelsView_colorChatsUnreadTint);
                    attr.colorDivider = attributes.getColorStateList(R.styleable.DefaultChannelsView_colorChatsDivider);
                    attr.colorBackgroundLoading = attributes
                            .getColorStateList(R.styleable.DefaultChannelsView_colorChatsBackgroundLoading);
                    attr.colorTextLoading = attributes
                            .getColorStateList(R.styleable.DefaultChannelsView_colorChatsTextLoading);

                    //Get texts
                    attr.textDateFormat = attributes.getString(R.styleable.DefaultChannelsView_textChatsDateFormat);
                    attr.textNoMessages = attributes.getString(R.styleable.DefaultChannelsView_textChatsNoMessage);
                    attr.textLocationMessage = attributes.getString(R.styleable.DefaultChannelsView_textChatsLocationMessage);
                    attr.textPhotoMessage = attributes.getString(R.styleable.DefaultChannelsView_textChatsPhotoMessage);

                    attr.dimenTextHeader = attributes.getDimensionPixelSize(R.styleable.DefaultChannelsView_dimenTextChatsHeader, R.dimen.text_18);
                    attr.dimenTextTime = attributes.getDimensionPixelSize(R.styleable.DefaultChannelsView_dimenTextChatsTime, R.dimen.text_13);
                    attr.dimenTextMessage =
                            attributes.getDimensionPixelSize(R.styleable.DefaultChannelsView_dimenTextChatsMessage, R.dimen.text_16);


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
        Attributes attr = Attributes.getInstance();
        if (attr != null
                && attr.colorBackground != null) {
            recyclerView.setBackgroundColor(attr
                    .colorBackground
                    .getColorForState(EMPTY_STATE_SET, android.R.color.transparent));

            if (attr.getColorBackgroundLoading() != null) {
                viewProgress.getBackground().setColorFilter(attr.getColorBackgroundLoading()
                        .getColorForState(EMPTY_STATE_SET, android.R.color.transparent), PorterDuff.Mode.SRC_IN);
            }

            if (attr.getColorTextLoading() != null) {
                labelLoading.setTextColor(attr.getColorTextLoading());
                progressLoading.getIndeterminateDrawable().setColorFilter(attr.getColorTextLoading()
                        .getColorForState(EMPTY_STATE_SET, android.R.color.transparent), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    protected void onCreateView() {
        super.onCreateView();
        onInitializeListeners();
        fullArrayObject = new ArrayList<>();
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

    @Override
    protected String getPresenterName() {
        return null;
    }


    /**
     * Method which provide the initializing of the lesteners
     */
    private void onInitializeListeners() {
        MMX.registerListener(new MMX.EventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public boolean onMessageReceived(MMXMessage message) {
                Log.e(getClass().getSimpleName(), "MMX.EventListener -> onMMXMessageReceived(MMXMessage message)");
                if (message != null) {
                    onMMXMessageReceived(message);
                }
                return false;
            }
        });
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
     * Method which provide the sorting of the channels
     */
    @Override
    public void sortChannels() {
        Collections.sort(recyclerView.getListItems(), Collections.reverseOrder(new ChannelsListContract.ChannelsDateComparator()));
        runOnMainThread(0, new OnActionPerformer() {
            @Override
            public void onActionPerform() {
                recyclerView.notifyDataSetChanged();
            }
        });
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
                if (labelLoading != null && viewProgress != null) {
                    if (message == null || message.isEmpty() == true) {
                        viewProgress.setVisibility(GONE);
                    } else if (isNeedShow == true) {
                        labelLoading.setText(message);
                        viewProgress.setVisibility(VISIBLE);
                    } else {
                        viewProgress.setVisibility(GONE);
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
     * Method which provide the filtering of the channel
     *
     * @param query current query
     */
    @Override
    public void filterChannels(@Nullable String query) {
        if (query != null && query.isEmpty() == false) {
            if (fullArrayObject.isEmpty() == true) {
                fullArrayObject.addAll(recyclerView.getListItems());
            }
            setChannels(LAST_MESSAGE_FILTER.applyFilter(fullArrayObject, query));
        } else {
            if (fullArrayObject.isEmpty() == false) {
                setChannels(fullArrayObject);
                fullArrayObject.clear();
            }
        }
    }

    /**
     * Method which provide the deleting of the channel object
     *
     * @param channelObject channel object
     */
    @Override
    public void deleteChannel(@NonNull final ChannelsListContract.ChannelObject channelObject) {
        if (channelObject != null
                && channelObject.getChannelDetail() != null
                && channelObject.getChannelDetail().getChannel() != null) {
            MMXChannel mmxChannel = channelObject.getChannelDetail().getChannel();
            mmxChannel.unsubscribe(channelDeleteListener);
        }

        runOnMainThread(0, new OnActionPerformer() {
            @Override
            public void onActionPerform() {
                if (recyclerView != null) {
                    recyclerView.deleteItem(channelObject);
                }
            }
        });
    }

    /**
     * Method which provide the action when MMXMessage received
     *
     * @param message current message
     */
    private void onMMXMessageReceived(@NonNull final MMXMessage message) {
        runOnBackground(new OnActionPerformer() {
            @Override
            public void onActionPerform() {
                if (recyclerView == null || message == null) {
                    Log.e(getClass().getSimpleName(), "onMMXMessageReceived -> recyclerView == null || message == null");
                    return;
                }

                final List<Object> objects = recyclerView.getListItems();
                for (Object object : objects) {
                    if (object instanceof ChannelsListContract.ChannelObject) {
                        final ChannelsListContract.ChannelObject channelObject = (ChannelsListContract.ChannelObject) object;
                        final MMXChannel mmxChannel = channelObject.getChannelDetail().getChannel();
                        final MMXChannel mmxChannel1 = message.getChannel();
                        if (mmxChannel != null
                                && mmxChannel1 != null
                                && mmxChannel.getName() != null
                                && mmxChannel1.getName() != null) {
                            final String channelName = mmxChannel.getName();
                            final String channelName1 = mmxChannel1.getName();
                            if (channelName.equalsIgnoreCase(channelName1) == true) {
                                channelObject.updateChannelMessage(message);
                                break;
                            }
                        }
                    }
                }
                sortChannels();
            }
        });
    }

    @Override
    public void setChannelListCallback(ChannelsListContract.OnChannelsListCallback channelListCallback) {
        if (recyclerView != null) {
            recyclerView.setItemActionListener(channelListCallback);
        }
    }

    @Override
    public void setChannelsViewCallback(ChannelsListContract.OnChannelsViewCallback channelsViewCallback) {
        this.channelsViewCallback = channelsViewCallback;
    }

    //ARRAY FILTERS

    /**
     * Filter which provide the channels filtering by last message context
     */
    private static final ArrayFilter LAST_MESSAGE_FILTER = new ArrayFilter<ChannelsListContract.ChannelObject, String>() {
        @Override
        public boolean compare(@NonNull ChannelsListContract.ChannelObject channelObject, @Nullable String s) {
            if (s == null
                    || s.isEmpty()
                    || channelObject == null
                    || channelObject.getLastMessage() == null) {
                return false;
            }
            return channelObject.getLastMessage().toLowerCase().contains(s.toLowerCase());
        }
    };

    //CALLBACKS

    /**
     * Callback which provide to listening of the channel unsubscription
     */
    private final MMXChannel.OnFinishedListener channelDeleteListener = new MMXChannel.OnFinishedListener<Boolean>() {
        @Override
        public void onSuccess(Boolean result) {
            if (channelsViewCallback != null
                    && getContext() != null) {
                channelsViewCallback.onMessageReceived(
                        getContext().getString(R.string.text_channel_deleted_successfuly_cover));
            }
        }

        @Override
        public void onFailure(MMXChannel.FailureCode code, Throwable throwable) {
            if (channelsViewCallback != null
                    && getContext() != null
                    && throwable != null) {
                channelsViewCallback.onMessageReceived(String.format("%s", throwable.toString()));
            }
        }
    };


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