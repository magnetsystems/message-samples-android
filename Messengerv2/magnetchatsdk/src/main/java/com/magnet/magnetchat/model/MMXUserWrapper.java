package com.magnet.magnetchat.model;

import com.magnet.magnetchat.ui.adapters.RecyclerViewTypedAdapter;
import com.magnet.max.android.User;

/**
 * Created by aorehov on 11.05.16.
 */
public class MMXUserWrapper extends MMXObjectWrapper<User> {

    public static final int TYPE_USER = 0xF100;

    private String name;
    private boolean isSelected;
    private boolean isShowLetter;

    public MMXUserWrapper(User obj, int type) {
        super(obj, type);
        name = obj.getDisplayName();
        if (name == null || name.length() == 0) {
            name = obj.getEmail();
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getFirstLetter() {
        return name.toUpperCase().substring(0, 1);
    }

    public String getPicUrl() {
        return getObj().getAvatarUrl();
    }

    public boolean isShowLetter() {
        return isShowLetter;
    }

    public void setShowLetter(boolean showLetter) {
        isShowLetter = showLetter;
    }

    @Override
    public String toString() {
        return obj.getDisplayName();
    }

    public String getName() {
        return name;
    }

    public final static RecyclerViewTypedAdapter.ItemComparator<MMXUserWrapper> ITEM_COMPARATOR = new RecyclerViewTypedAdapter.ItemComparator<MMXUserWrapper>() {
        @Override
        public int compare(MMXUserWrapper o1, MMXUserWrapper o2) {
            return o1.name.toUpperCase().compareTo(o2.name.toUpperCase());
        }

        @Override
        public boolean areContentsTheSame(MMXUserWrapper o1, MMXUserWrapper o2) {
            return o1.isShowLetter == o2.isShowLetter;
        }

        @Override
        public boolean areItemsTheSame(MMXUserWrapper item1, MMXUserWrapper item2) {
            return item1.equals(item2);
        }
    };
}
