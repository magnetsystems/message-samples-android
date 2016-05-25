package com.magnet.magnetchat.layers;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.ui.views.AbstractChannelsView;
import com.magnet.magnetchat.ui.custom.AdapteredRecyclerView;
import com.magnet.magnetchat.ui.custom.CircleNameView;
import com.magnet.magnetchat.ui.views.section.channels.DefaultChannelsView;
import com.magnet.magnetchat.util.Logger;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.MMXMessage;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dlernatovich on 4/1/16.
 */
public interface ChannelsListContractLayer {

    //=======================================================================================
    //==================================RECYCLER ITEMS=======================================
    //=======================================================================================

    /**
     * Channel view
     */
    class ChannelRecyclerItem extends AdapteredRecyclerView.BaseRecyclerItem<ChannelObject> {

        private static final String K_DEFAULT_DATE_FORMAT = "MMM, dd";
        private static final String K_DEFAULT_NO_MESSAGES = "No messages";
        private static final String K_DEFAULT_PHOTO_MESSAGE = "Photo message";
        private static final String K_DEFAULT_LOCATION_MESSAGE = "Location message";
        private static final String TAG = "ChannelRecyclerItem";

        //Attributes

        AppCompatTextView labelChannelName;
        CircleImageView imageView;
        AppCompatTextView labelDate;
        AppCompatTextView labelLatestMessage;
        CircleNameView viewCircleName;
        FrameLayout viewNewMessage;
        ViewGroup viewImages;
        ViewGroup viewContent;
        ViewGroup viewDivider;
        ImageView imagePointer;
        ImageView imageNewMessage;
        ViewGroup contentView;

        private String dateFormat;
        private String textNoMessages;
        private String textPhotoMessage;
        private String textLocationMessage;
        private String textDateFormat;

        public ChannelRecyclerItem(Context context) {
            super(context);
        }

        /**
         * Method which provide the setting up for the current recycler item
         *
         * @param baseObject current object
         */
        @Override
        public void setUp(final ChannelObject baseObject) {
            onLinkInterface();
            contentView.setVisibility(INVISIBLE);
            if (baseObject == null) {
                return;
            }
            final String channelName = baseObject.getChannelName();
            final ChannelDetail channelDetail = baseObject.channelDetail;
            if (channelDetail != null) {
                runOnBackground(0, new OnActionPerformer() {
                    @Override
                    public void onActionPerform() {
                        setUpUi();
                        setUpName(channelName);
                        setUpImage(channelDetail);
                        setUpDate(channelDetail, textDateFormat);
                        setUpLastMessage(baseObject);
                        setUpNewMessage(channelDetail);
                    }
                });
            }
        }

        /**
         * Method which provide the setting up of the UI
         */
        private void setUpUi() {

            final DefaultChannelsView.Attributes attr = DefaultChannelsView.Attributes.getInstance();

            textNoMessages = K_DEFAULT_NO_MESSAGES;
            textLocationMessage = K_DEFAULT_LOCATION_MESSAGE;
            textPhotoMessage = K_DEFAULT_PHOTO_MESSAGE;
            textDateFormat = K_DEFAULT_DATE_FORMAT;

            if (attr != null) {

                //String getting
                if (attr.getTextLocationMessage() != null) {
                    textLocationMessage = attr.getTextLocationMessage();
                }

                if (attr.getTextNoMessages() != null) {
                    textNoMessages = attr.getTextNoMessages();
                }

                if (attr.getTextPhotoMessage() != null) {
                    textPhotoMessage = attr.getTextPhotoMessage();
                }

                if (attr.getTextDateFormat() != null) {
                    textDateFormat = attr.getTextDateFormat();
                }

                runOnMainThread(0, new OnActionPerformer() {
                    @Override
                    public void onActionPerform() {
                        //UI setting
                        if (attr.isNeedImages() == true) {
                            viewImages.setVisibility(VISIBLE);
                        } else {
                            viewImages.setVisibility(GONE);
                        }

                        if (attr.isNeedBoldHeader()) {
                            labelChannelName.setTypeface(null, Typeface.BOLD);
                        } else {
                            labelChannelName.setTypeface(null, Typeface.NORMAL);
                        }

                        if (attr.getColorHeader() != null) {
                            labelChannelName.setTextColor(attr.getColorHeader());
                        }

                        if (attr.getColorMessage() != null) {
                            labelLatestMessage.setTextColor(attr.getColorMessage());
                        }

                        if (attr.getColorTime() != null) {
                            imagePointer.setColorFilter(attr.getColorTime()
                                    .getColorForState(EMPTY_STATE_SET, android.R.color.transparent));
                            labelDate.setTextColor(attr.getColorTime());
                        }

                        if (attr.getColorDivider() != null) {
                            viewDivider.setBackgroundColor(attr
                                    .getColorDivider()
                                    .getColorForState(EMPTY_STATE_SET, android.R.color.transparent));
                        }
                        if (attr.getColorUnreadTint() != null) {
                            imageNewMessage.setColorFilter(attr.getColorUnreadTint()
                                    .getColorForState(EMPTY_STATE_SET, android.R.color.transparent));
                        }

                        labelDate.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                attr.getDimenTextTime());
                        labelLatestMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                attr.getDimenTextMessage());
                        labelChannelName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                attr.getDimenTextHeader());
                    }
                });

            }
        }

        /**
         * Method which provide to getting of the layout ID
         *
         * @return layout ID
         */
        @Override
        protected int getLayoutId() {
            return R.layout.item_view_channel_cover;
        }

        /**
         * Method which provide the interface linking
         */
        @Override
        protected void onLinkInterface() {
            labelChannelName = (AppCompatTextView) findViewById(R.id.labelChannelName);
            imageView = (CircleImageView) findViewById(R.id.imageChannel);
            labelDate = (AppCompatTextView) findViewById(R.id.labelDate);
            labelLatestMessage = (AppCompatTextView) findViewById(R.id.labelLatestMessage);
            viewCircleName = (CircleNameView) findViewById(R.id.viewCircleName);
            viewNewMessage = (FrameLayout) findViewById(R.id.viewNewMessage);
            viewImages = (ViewGroup) findViewById(R.id.viewImages);
            viewContent = (ViewGroup) findViewById(R.id.itemContent);
            viewDivider = (ViewGroup) findViewById(R.id.viewDivider);
            imagePointer = (ImageView) findViewById(R.id.imagePointer);
            imageNewMessage = (ImageView) findViewById(R.id.imageNewMessage);
            contentView = (ViewGroup) findViewById(R.id.itemContent);
        }

        /**
         * Method which provide the getting of the clicked view ID
         *
         * @return clicked view ID
         */
        @Override
        protected int getClickedID() {
            return R.id.itemContent;
        }

        /**
         * Method which provide the action when view will create
         */
        @Override
        protected void onCreateView() {
            //Send on click listener
            contentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    sendEvent(ChannelEvent.LONG_CLICK.getRecycleEvent());
                    return true;
                }
            });
        }

        /**
         * Method which provide the setting up of the name for the current channel object
         *
         * @param channelName channel name
         */
        private void setUpName(@NonNull final String channelName) {
            runOnMainThread(0, new OnActionPerformer() {
                @Override
                public void onActionPerform() {
                    labelChannelName.setText(channelName);
                }
            });
        }

        /**
         * Method which provide the setting up of the channel image
         *
         * @param channelDetail channel detail object
         */
        private void setUpImage(@NonNull ChannelDetail channelDetail) {
            int subscribersCount = channelDetail.getTotalSubscribers();
            if (subscribersCount > 2) {
                runOnMainThread(0, new OnActionPerformer() {
                    @Override
                    public void onActionPerform() {
                        imageView.setImageResource(R.drawable.user_group);
                    }
                });
            } else if (subscribersCount > 0) {
                List<UserProfile> subscribers = channelDetail.getSubscribers();
                if (subscribers != null && subscribers.size() > 0) {
                    final UserProfile userProfile = subscribers.get(0);
                    if (userProfile != null) {
                        setUpCircleName(userProfile);
                        runOnMainThread(0, new OnActionPerformer() {
                            @Override
                            public void onActionPerform() {
                                Glide.with(getContext())
                                        .load(userProfile.getAvatarUrl())
                                        .centerCrop()
                                        .fitCenter()
                                        .listener(glideCallback)
                                        .into(imageView);
                            }
                        });
                    }
                }
            }
        }

        /**
         * Method which provide the setting up of the date
         *
         * @param channelDetail channel detail object
         */
        private void setUpDate(@NonNull final ChannelDetail channelDetail, @NonNull final String dateFormat) {
            final Date date = channelDetail.getLastPublishedTime();
            runOnMainThread(0, new OnActionPerformer() {
                @Override
                public void onActionPerform() {
                    try {
                        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                        labelDate.setText(simpleDateFormat.format(date));
                    } catch (Exception exception) {
                        Log.e(TAG, exception.toString());
                        if (dateFormat.equalsIgnoreCase(K_DEFAULT_DATE_FORMAT) == false) {
                            setUpDate(channelDetail, K_DEFAULT_DATE_FORMAT);
                        }
                    }
                }
            });
        }

        /**
         * Method which provide to updating of the latest message
         *
         * @param channelObject channel object
         */
        private void setUpLastMessage(@NonNull final ChannelObject channelObject) {
            final String lastMessage = channelObject.getLastMessage(textPhotoMessage,
                    textLocationMessage, textNoMessages);
            runOnMainThread(0, new OnActionPerformer() {
                @Override
                public void onActionPerform() {
                    labelLatestMessage.setText(lastMessage);
                }
            });
        }

        /**
         * Method which provide the setting up of the user profile
         *
         * @param userProfile user profile
         */
        private void setUpCircleName(@NonNull UserProfile userProfile) {
            String text = userProfile.getDisplayName();
            if (text == null) {
                if (userProfile.getFirstName() != null && userProfile.getLastName() != null) {
                    text = String.format("%s %s", userProfile.getFirstName(), userProfile.getLastName());
                }
            }

            final String finalText = text;
            runOnMainThread(0, new OnActionPerformer() {
                @Override
                public void onActionPerform() {
                    viewCircleName.setUserName(finalText);
                }
            });
        }

        /**
         * Method which provide the setting up of the new message availability
         *
         * @param channelDetail channel details object
         */
        private void setUpNewMessage(@NonNull ChannelDetail channelDetail) {
            Chat chat = new Chat(channelDetail);
            final boolean isHaveNewMessages = chat.hasUnreadMessage();
            runOnMainThread(0, new OnActionPerformer() {
                @Override
                public void onActionPerform() {
                    contentView.setVisibility(VISIBLE);
                    if (isHaveNewMessages == true) {
                        viewNewMessage.setVisibility(VISIBLE);
                    } else {
                        viewNewMessage.setVisibility(GONE);
                    }
                }
            });
        }


        /**
         * Callback which provide to loading of the image into the channel image view
         */
        private final RequestListener<String, GlideDrawable> glideCallback = new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                if (e != null) {
                    Log.e("ChannelRecyclerItem", e.toString());
                }
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (imageView != null) {
                    imageView.setImageDrawable(resource);
                }
                return false;
            }
        };


    }

    /**
     * Channel object
     */
    class ChannelObject extends AdapteredRecyclerView.BaseObject {

        private static final String K_NO_AVAILABLE = "Not available";
        private static final String K_NO_MESSAGES = "No messages";

        private final ChannelDetail channelDetail;
        private final String channelName;
        private Date lastTimeActive;
        private String lastMessage;

        private String textNoMessage;
        private String textPhotoMessage;
        private String textLocationMessage;

        public ChannelObject(ChannelDetail channelDetail) {
            this.channelDetail = channelDetail;
            this.channelName = getChannelName(channelDetail);
            updateChannelMessage(channelDetail);
        }

        @Override
        public AdapteredRecyclerView.BaseRecyclerItem getRecyclerItem(Context context) {
            return new ChannelRecyclerItem(context);
        }

        /**
         * Method which provide getting of the channel details
         *
         * @return channel details
         */
        public ChannelDetail getChannelDetail() {
            return channelDetail;
        }

        /**
         * Method which provide the setting up of the name for the current channel object
         *
         * @param channelDetail channel details object
         */
        private String getChannelName(@NonNull ChannelDetail channelDetail) {
            if (channelDetail != null
                    && channelDetail.getChannel() != null
                    && channelDetail.getSubscribers().isEmpty() == false) {
                StringBuilder name = new StringBuilder();
                for (UserProfile profile : channelDetail.getSubscribers()) {
                    if (profile != null
                            && profile.getDisplayName() != null
                            && profile.getDisplayName().isEmpty() == false) {
                        String displayName = profile.getDisplayName();
                        if (displayName != null) {
                            String separator = ", ";
                            if (name.length() == 0) {
                                separator = "";
                            }
                            name.append(separator);
                            name.append(displayName);
                        }
                    }
                }
                return name.toString().trim();
            }
            return K_NO_AVAILABLE;
        }

        /**
         * Method which provide the getting of the last message
         *
         * @param textLocationMessage default text for location message
         * @param textPhotoMessage    default text for photo message
         * @return last message
         */
        public String getLastMessage(@NonNull String textPhotoMessage,
                                     @NonNull String textLocationMessage,
                                     @NonNull String textNoMessage) {

            this.textNoMessage = textNoMessage;
            this.textLocationMessage = textLocationMessage;
            this.textPhotoMessage = textPhotoMessage;

            if (lastMessage != null) {
                return lastMessage;
            }

            int messageCount = channelDetail.getTotalMessages();
            if (messageCount > 0) {
                List<MMXMessage> messages = channelDetail.getMessages();
                if (messages != null && messages.size() > 0) {
                    MMXMessage mmxMessage = messages.get(messages.size() - 1);
                    if (mmxMessage != null && mmxMessage.getContent() != null && mmxMessage.getContent().containsKey(Message.TAG_TYPE)) {
                        Message message = Message.createMessageFrom(mmxMessage);
                        if (message != null
                                && message.getType().equalsIgnoreCase(Message.TYPE_PHOTO)) {
                            lastMessage = textPhotoMessage;
                        } else if (message != null
                                && message.getType().equalsIgnoreCase(Message.TYPE_MAP)) {
                            lastMessage = textLocationMessage;
                        } else if (message != null
                                && message.getText() != null
                                && message.getText().isEmpty() != true) {
                            lastMessage = message.getText();
                        }
                    }
                }
            }

            if (lastMessage == null) {
                lastMessage = textNoMessage;
            }

            return lastMessage;
        }

        /**
         * Method which provide the updating of the last time activ
         *
         * @param channelDetail channel details
         */
        private void updateChannelMessage(@Nullable ChannelDetail channelDetail) {
            if (channelDetail != null
                    && channelDetail.getChannel() != null
                    && channelDetail.getChannel().getLastTimeActive() != null) {
                this.lastTimeActive = channelDetail.getChannel().getLastTimeActive();
            } else {
                if (this.lastTimeActive == null) {
                    this.lastTimeActive = new Date();
                }
            }
        }

        /**
         * Method which provide the updating of the last time active date
         *
         * @param mmxMessage mmx message
         */
        public void updateChannelMessage(@Nullable final MMXMessage mmxMessage) {
            if (mmxMessage != null) {
                Message message = Message.createMessageFrom(mmxMessage);
                if (message != null) {

                    lastTimeActive = message.getCreateTime();

                    /**
                     * try-catch - is fast fix
                     */
                    try {
                        if (message != null
                                && message.getType().equalsIgnoreCase(Message.TYPE_PHOTO)) {
                            lastMessage = textPhotoMessage;
                        } else if (message != null
                                && message.getType().equalsIgnoreCase(Message.TYPE_MAP)) {
                            lastMessage = textLocationMessage;
                        } else if (message != null
                                && message.getText() != null
                                && message.getText().isEmpty() != true) {
                            lastMessage = message.getText();
                        }
                    } catch (Exception ex) {
                        Logger.error(getClass().getSimpleName(), ex);
                    }
                }

                if (lastMessage == null) {
                    if (textNoMessage == null) {
                        lastMessage = textNoMessage;
                    } else {
                        lastMessage = K_NO_MESSAGES;
                    }
                }
            }
        }

        /**
         * Method which provide the getting of the last message
         *
         * @return last message
         */
        public String getLastMessage() {
            return lastMessage;
        }

        /**
         * Method which provide the getting of the channel name
         *
         * @return channel name
         */
        public String getChannelName() {
            return channelName;
        }

        /**
         * Method which provide the getting of the last time active
         *
         * @return last time active
         */
        public Date getLastTimeActive() {
            return lastTimeActive;
        }
    }

//=======================================================================================
//=====================================CALLBACK==========================================
//=======================================================================================


    /**
     * Callback which provide the listening the action which happening inside the RecyclerView
     */
    interface OnChannelsListCallback extends AdapteredRecyclerView.BaseRecyclerCallback<ChannelObject> {
    }

    /**
     * Callback which provide the litening action what is happening inside the AbstractChannelsView
     *
     * @see AbstractChannelsView
     */
    interface OnChannelsViewCallback {
        /**
         * Method which provide the action when object send message
         *
         * @param message message
         */
        void onMessageReceived(@NonNull final String message);
    }

//=======================================================================================
//===================================COMPARATORS=========================================
//=======================================================================================

    /**
     * Comparartor which provide the sorting of the
     */
    class ChannelsDateComparator implements Comparator<ChannelObject> {
        @Override
        public int compare(ChannelObject lhs, ChannelObject rhs) {
            if (lhs.getLastTimeActive() == null || rhs.getLastTimeActive() == null) {
                return 0;
            }
            return lhs.getLastTimeActive().compareTo(rhs.getLastTimeActive());
        }
    }
//=======================================================================================
//====================================CONSTANTS==========================================
//=======================================================================================

    enum ChannelEvent {
        LONG_CLICK(0);

        /**
         * Object
         */
        private final AdapteredRecyclerView.BaseRecyclerCallback.RecycleEvent recycleEvent;

        /**
         * Constructor
         *
         * @param eventCode event code
         */
        ChannelEvent(int eventCode) {
            this.recycleEvent = new AdapteredRecyclerView.BaseRecyclerCallback.RecycleEvent(eventCode);
        }

        /**
         * Method which provide the getting of the event
         *
         * @return current event
         */
        public AdapteredRecyclerView.BaseRecyclerCallback.RecycleEvent getRecycleEvent() {
            return recycleEvent;
        }
    }
}
