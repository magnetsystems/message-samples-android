package com.magnet.magnetchat.model;

import com.magnet.magnetchat.ui.adapters.RecyclerViewTypedAdapter;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.Date;
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
    public static final int TYPE_MAP_ANOTHER = 0xFF05;
    public static final int TYPE_VIDEO_ANOTHER = 0xFF06;
    public static final int TYPE_PHOTO_ANOTHER = 0xFF07;
    public static final int TYPE_TEXT_ANOTHER = 0xFF08;
    public static final int TYPE_POLL_ANOTHER = 0xFF09;

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

    public static int defineType(MMXMessage mmxMessage, boolean isMine) {
        Map<String, String> content = mmxMessage.getContent();
        if (content != null && content.containsKey(Message.TAG_TYPE)) {
            String tagType = content.get(Message.TAG_TYPE);
            switch (tagType) {
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
        }
        return TYPE_TEXT_ANOTHER;
    }

    public Date getPublishDate() {
        return getObj().getTimestamp();
    }

    public static final RecyclerViewTypedAdapter.ItemComparator<MMXMessageWrapper> COMPARATOR = new RecyclerViewTypedAdapter.ItemComparator<MMXMessageWrapper>() {
        @Override
        public int compare(MMXMessageWrapper o1, MMXMessageWrapper o2) {
            return o1.getPublishDate().compareTo(o2.getPublishDate());
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
