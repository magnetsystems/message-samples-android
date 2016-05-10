package com.magnet.magnetchat.model;

import com.magnet.magnetchat.ui.adapters.RecyclerViewTypedAdapter;
import com.magnet.magnetchat.util.MMXMessageUtil;
import com.magnet.max.android.Attachment;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by aorehov on 28.04.16.
 */
public class MMXMessageWrapper extends MMXObjectWrapper<MMXMessage> implements Typed {

    public static final int TYPE_MAP_MY = 0xFF00;
    public static final int TYPE_VIDEO_MY = 0xFF01;
    public static final int TYPE_PHOTO_MY = 0xFF02;
    public static final int TYPE_TEXT_MY = 0xFF03;
    public static final int TYPE_POLL_MY = 0xFF04;
    public static final int TYPE_MAP_ANOTHER = 0x7F05;
    public static final int TYPE_VIDEO_ANOTHER = 0x7F06;
    public static final int TYPE_PHOTO_ANOTHER = 0x7F07;
    public static final int TYPE_TEXT_ANOTHER = 0x7F08;
    public static final int TYPE_POLL_ANOTHER = 0x7F09;
    public static final int MY_MESSAGE_MASK = 0x8000;

    public static final String TAG_QUESTION = "question";
    public static final String TYPE_POLL = "DefaultPollConfig";


    private boolean isShowDate = false;
    private boolean isMyMessage;
    private final int type;
    private Date date;

    public MMXMessageWrapper(MMXMessage obj, int type) {
        this(obj, type, false, obj.getTimestamp());
    }

    public MMXMessageWrapper(MMXMessage obj, int type, boolean isMyMessage) {
        this(obj, type, isMyMessage, obj.getTimestamp());
    }

    public MMXMessageWrapper(MMXMessage obj, int type, boolean isMyMessage, Date date) {
        super(obj);
        this.date = date;
        this.type = type;
        this.isMyMessage = isMyMessage;
    }

    @Override
    public int getType() {
        return type;
    }

    public Date getPublishDate() {
        return getObj().getTimestamp();
    }

    public boolean isShowDate() {
        return isShowDate;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public String getSenderName() {
        return getObj().getSender().getDisplayName();
    }

    public String getSenderPicture() {
        return getObj().getSender().getAvatarUrl();
    }

    public String getTextMessage() {
        return MMXMessageUtil.getTextMessage(getObj());
    }

    public double getLat() {
        return MMXMessageUtil.getLat(getObj());
    }

    public double getLon() {
        return MMXMessageUtil.getLon(getObj());
    }

    public Attachment getAttachment() {
        List<Attachment> list = getObj().getAttachments();
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    public String getMapLocationUrl() {
        return MMXMessageUtil.getMapPicUrl(getObj());
    }

    /**
     * ===============================================
     * static helpful methods
     * ===============================================
     */

    /**
     * using for type defining for typed adapter
     *
     * @param mmxMessage instance of message
     * @param isMine     true if message belong to current user
     * @return type which using in MMXListItemFactory
     * @see com.magnet.magnetchat.ui.factories.MMXListItemFactory
     * @see com.magnet.magnetchat.model.converters.MMXMessageWrapperConverter
     * @see com.magnet.magnetchat.model.converters.factories.MMXObjectConverterFactory
     */
    public static int defineType(MMXMessage mmxMessage, boolean isMine) {
        String tagType = null;
        Map<String, String> content = mmxMessage.getContent();
        if (content != null) {
            if (content.containsKey(Message.TAG_TYPE)) {
                tagType = content.get(Message.TAG_TYPE);
            } else if (content.containsKey(TAG_QUESTION)) {
                if (content.get(TAG_QUESTION).equals(TYPE_POLL)) {
                    tagType = Message.TYPE_POLL;
                }
            }
        }

        if (tagType != null) switch (tagType) {
            case Message.TYPE_PHOTO:
                return isMine ? TYPE_PHOTO_MY : TYPE_PHOTO_ANOTHER;
            case Message.TYPE_VIDEO:
                return isMine ? TYPE_VIDEO_MY : TYPE_VIDEO_ANOTHER;
            case Message.TYPE_POLL:
                return isMine ? TYPE_POLL_MY : TYPE_POLL_ANOTHER;
            case Message.TYPE_MAP:
                return isMine ? TYPE_MAP_MY : TYPE_MAP_ANOTHER;
            case Message.TYPE_TEXT:
            default:
                return isMine ? TYPE_TEXT_MY : TYPE_TEXT_ANOTHER;
        }

        return isMine ? TYPE_TEXT_MY : TYPE_TEXT_ANOTHER;
    }


    public static final RecyclerViewTypedAdapter.ItemComparator<MMXMessageWrapper> COMPARATOR = new RecyclerViewTypedAdapter.ItemComparator<MMXMessageWrapper>() {
        @Override
        public int compare(MMXMessageWrapper o1, MMXMessageWrapper o2) {
            return o2.getPublishDate().compareTo(o1.getPublishDate());
        }

        @Override
        public boolean areContentsTheSame(MMXMessageWrapper o1, MMXMessageWrapper o2) {
            return true;
        }

        @Override
        public boolean areItemsTheSame(MMXMessageWrapper item1, MMXMessageWrapper item2) {
            return item1.equals(item2);
        }
    };
}
