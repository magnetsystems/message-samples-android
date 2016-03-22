package com.magnet.magntetchatapp.mvp.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.magnet.magnetchat.model.Chat;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.ui.views.section.chat.CircleNameView;
import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.abs.BaseContract;
import com.magnet.magntetchatapp.mvp.views.AbstractChannelsView;
import com.magnet.magntetchatapp.ui.custom.AdapteredRecyclerView;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.MMXMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public interface ChannelsListContract {

    interface View extends BaseContract.BaseView {
        /**
         * Method which provide to add the channels to current list object
         *
         * @param objects current objects
         */
        void addChannels(@NonNull List<ChannelObject> objects);

        /**
         * Method which provide to set the channels to current list object
         *
         * @param objects current objects
         */
        void setChannels(@NonNull List<ChannelObject> objects);

        /**
         * Method which provide to channels clearing
         */
        void clearChannels();

    }

    interface Presenter extends BaseContract.BasePresenter {
        /**
         * Method which provide to start of channel receiving
         */
        void startChannelReceiving();
    }

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

        @InjectView(R.id.labelChannelName)
        AppCompatTextView labelChannelName;
        @InjectView(R.id.imageChannel)
        CircleImageView imageView;
        @InjectView(R.id.labelDate)
        AppCompatTextView labelDate;
        @InjectView(R.id.labelLatestMessage)
        AppCompatTextView labelLatestMessage;
        @InjectView(R.id.viewCircleName)
        CircleNameView viewCircleName;
        @InjectView(R.id.viewNewMessage)
        FrameLayout viewNewMessage;
        @InjectView(R.id.viewImages)
        ViewGroup viewImages;
        @InjectView(R.id.itemContent)
        ViewGroup viewContent;
        @InjectView(R.id.viewDivider)
        ViewGroup viewDivider;

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
        public void setUp(ChannelObject baseObject) {

            textNoMessages = K_DEFAULT_NO_MESSAGES;
            textLocationMessage = K_DEFAULT_LOCATION_MESSAGE;
            textPhotoMessage = K_DEFAULT_PHOTO_MESSAGE;
            textDateFormat = K_DEFAULT_DATE_FORMAT;

            if (baseObject == null) {
                return;
            }
            final ChannelDetail channelDetail = baseObject.channelDetail;
            if (channelDetail != null) {
                runOnBackground(0, new OnActionPerformer() {
                    @Override
                    public void onActionPerform() {
                        setUpUi();
                        setUpName(channelDetail);
                        setUpImage(channelDetail);
                        setUpDate(channelDetail, textDateFormat);
                        setUpLastMessage(channelDetail);
                        setUpNewMessage(channelDetail);
                    }
                });
            }
        }

        /**
         * Method which provide the setting up of the UI
         */
        private void setUpUi() {
            runOnMainThread(0, new OnActionPerformer() {
                @Override
                public void onActionPerform() {
                    AbstractChannelsView.Attributes attr = AbstractChannelsView.Attributes.getInstance();
                    if (attr != null) {

                        if (attr.attrIsNeedImage == true) {
                            viewImages.setVisibility(VISIBLE);
                        } else {
                            viewImages.setVisibility(GONE);
                        }

                        if (attr.getColorHeader() != null) {
                            labelChannelName.setTextColor(attr.getColorHeader());
                        }

                        if (attr.getColorMessage() != null) {
                            labelLatestMessage.setTextColor(attr.getColorMessage());
                        }

                        if (attr.getColorTime() != null) {
                            labelDate.setTextColor(attr.getColorTime());
                        }

                        if (attr.getColorDivider() != null) {
                            viewDivider.setBackgroundColor(attr
                                    .getColorDivider()
                                    .getColorForState(EMPTY_STATE_SET, android.R.color.transparent));
                        }

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
                    }
                }
            });
        }

        /**
         * Method which provide to getting of the layout ID
         *
         * @return layout ID
         */
        @Override
        protected int getLayoutId() {
            return R.layout.item_view_channel;
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
        }

        /**
         * Method which provide the setting up of the name for the current channel object
         *
         * @param channelDetail channel details object
         */
        private void setUpName(@NonNull ChannelDetail channelDetail) {
            final StringBuilder name = new StringBuilder();
            for (UserProfile profile : channelDetail.getSubscribers()) {
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
            runOnMainThread(0, new OnActionPerformer() {
                @Override
                public void onActionPerform() {
                    labelChannelName.setText(name);
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
        private void setUpDate(@NonNull ChannelDetail channelDetail, @NonNull String dateFormat) {
            final Date date = channelDetail.getLastPublishedTime();
            try {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                runOnMainThread(0, new OnActionPerformer() {
                    @Override
                    public void onActionPerform() {
                        labelDate.setText(simpleDateFormat.format(date));
                    }
                });
            } catch (Exception exception) {
                Log.e(TAG, exception.toString());
                if (dateFormat.equalsIgnoreCase(K_DEFAULT_DATE_FORMAT) == false) {
                    setUpDate(channelDetail, K_DEFAULT_DATE_FORMAT);
                }
            }
        }

        /**
         * Method which provide to updating of the latest message
         *
         * @param channelDetail latest message
         */
        private void setUpLastMessage(@NonNull ChannelDetail channelDetail) {
            String messageText = textNoMessages;
            int messageCount = channelDetail.getTotalMessages();
            if (messageCount > 0) {
                List<MMXMessage> messages = channelDetail.getMessages();
                if (messages != null && messages.size() > 0) {
                    MMXMessage mmxMessage = messages.get(0);
                    if (mmxMessage != null) {
                        Message message = Message.createMessageFrom(mmxMessage);
                        if (message != null
                                && message.getText() != null
                                && message.getText().isEmpty() != true) {
                            messageText = message.getText();
                        } else if (message != null
                                && message.getType() == Message.TYPE_PHOTO) {
                            messageText = textPhotoMessage;
                        } else if (message != null
                                && message.getType() == Message.TYPE_MAP) {
                            messageText = textLocationMessage;
                        }
                    }
                }
            }

            final String finalMessageText = messageText;
            runOnMainThread(0, new OnActionPerformer() {
                @Override
                public void onActionPerform() {
                    labelLatestMessage.setText(finalMessageText);
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
                Log.e("ChannelRecyclerItem", e.toString());
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

        private final ChannelDetail channelDetail;

        public ChannelObject(ChannelDetail channelDetail) {
            this.channelDetail = channelDetail;
        }

        @Override
        public AdapteredRecyclerView.BaseRecyclerItem getRecyclerItem(Context context) {
            return new ChannelRecyclerItem(context);
        }

    }

//=======================================================================================
//=====================================CALLBACK==========================================
//=======================================================================================


    /**
     * Callback which provide the listening the action which happening inside the RecyclerView
     */
    interface OnChannelListCallback extends AdapteredRecyclerView.BaseRecyclerCallback<ChannelObject> {
    }
}
