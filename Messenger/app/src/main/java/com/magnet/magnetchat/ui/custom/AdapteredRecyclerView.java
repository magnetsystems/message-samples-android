package com.magnet.magnetchat.ui.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.magnet.magnetchat.ui.views.abs.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artli_000 on 02.11.2015.
 */
public class AdapteredRecyclerView<T extends AdapteredRecyclerView.BaseObject> extends RecyclerView {

    private BaseApplicationRecyclerAdapter adapter;
    private List<BaseObject> objectList;

    public AdapteredRecyclerView(Context context) {
        super(context);
        onCreate(context);
    }

    public AdapteredRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public AdapteredRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate(context);
    }

    /**
     * Method which provide the action when RecyclerView is creating
     *
     * @param context current context
     */
    private void onCreate(Context context) {
        objectList = new ArrayList<>();
        adapter = new BaseApplicationRecyclerAdapter(objectList);
        setAdapter(adapter);
        setHasFixedSize(true);
    }

    /**
     * Method which provide the updating list inside the RecyclerView
     *
     * @param baseObjects current object list
     */
    public void updateList(List<T> baseObjects) {
        objectList.clear();
        objectList.addAll(baseObjects);
        adapter.notifyDataSetChanged();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////HELP OBJECTS/////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Created by Artli_000 on 14.10.2015.
     */
    public static abstract class BaseObject {

        /**
         * Method which provide the getting of the current recycler item
         *
         * @param context current context
         * @return current instance for the Recycler item
         */
        public abstract BaseRecyclerItem getRecyclerItem(Context context);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////HELP ADAPTER/////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static class BaseApplicationRecyclerAdapter<T extends BaseObject> extends Adapter<BaseApplicationRecyclerAdapter.ViewHolder> {

        private List<T> listItems;

        public BaseApplicationRecyclerAdapter(List<T> listItems) {
            this.listItems = listItems;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(listItems.get(viewType).getRecyclerItem(parent.getContext()));
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.recycleItem.setUp(listItems.get(position));
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public BaseRecyclerItem recycleItem;

            public ViewHolder(View itemView) {
                super(itemView);
                recycleItem = (BaseRecyclerItem) itemView;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////HELP RECYCLER ITEM//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static abstract class BaseRecyclerItem<T extends BaseObject> extends BaseView {
        public BaseRecyclerItem(Context context) {
            super(context);
        }

        public BaseRecyclerItem(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public BaseRecyclerItem(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        /**
         * Method which provide the setting up for the current recycler item
         *
         * @param baseObject current object
         */
        public abstract void setUp(T baseObject);
    }

}
