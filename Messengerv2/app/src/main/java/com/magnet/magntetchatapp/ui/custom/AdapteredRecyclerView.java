package com.magnet.magntetchatapp.ui.custom;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.magnet.magntetchatapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Artli_000 on 02.11.2015.
 */
public class AdapteredRecyclerView<T extends AdapteredRecyclerView.BaseObject> extends RecyclerView {

    private static final String TAG = "AdapteredRecyclerView";

    private BaseApplicationRecyclerAdapter adapter;
    private List<T> objectList;

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
    private void onCreate(@NonNull Context context) {
        if (isInEditMode() == true) {
            return;
        }
        objectList = new ArrayList<>();
        adapter = new BaseApplicationRecyclerAdapter(objectList);
        adapter.setSizeListOld(0);
        setAdapter(adapter);
        setHasFixedSize(true);
    }

    /**
     * Method which provide the updating list inside the RecyclerView
     *
     * @param baseObjects current object list
     */
    public void updateList(@NonNull List<T> baseObjects) {
        objectList.clear();
        adapter.setSizeListOld(0);
        objectList.addAll(baseObjects);
        sortByPriority(objectList);
        adapter.notifyDataSetChanged();
    }

    /**
     * Method which provide the updating list inside the RecyclerView
     *
     * @param baseObjects current object list
     */
    public void addList(@NonNull List<T> baseObjects) {
        objectList.addAll(baseObjects);
        sortByPriority(objectList);
        adapter.notifyDataSetChanged();
    }

    /**
     * Method which provide the list clearing
     */
    public void clearList() {
        objectList.clear();
        adapter.setSizeListOld(0);
        adapter.notifyDataSetChanged();
    }

    /**
     * Method which provide the getting of the item by index
     *
     * @param index
     * @return current object
     */
    @Nullable
    public T getItemByIndex(int index) {
        try {
            T object = (T) objectList.get(index);
            return object;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * Method which provide the notifying of the data set changed
     */
    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Method which provide the getting of the items list
     *
     * @return current items list
     */
    @NonNull
    public List<T> getListItems() {
        return objectList;
    }

    /**
     * Method which provide the setting of the item action listener
     *
     * @param itemActionListener
     */
    public void setItemActionListener(BaseRecyclerCallback itemActionListener) {
        if (adapter != null) {
            adapter.setItemActionListener(itemActionListener);
        }
    }

    /**
     * Method which provide the setting of the lazy load callback
     *
     * @param lazyLoadCallback lazy load callback
     */
    public void setLazyLoadCallback(@NonNull OnLazyLoadCallback lazyLoadCallback) {
        if (adapter != null) {
            adapter.setLazyLoadCallback(lazyLoadCallback);
        }
    }

    /**
     * Method which provide the sorting by priority
     *
     * @param objectList priority list
     */
    private void sortByPriority(@Nullable List<T> objectList) {
        if (objectList != null) {
            Collections.sort(objectList, new PriorityComparator());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////HELP OBJECTS/////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Created by Artli_000 on 14.10.2015.
     */
    public static abstract class BaseObject {

        enum Priority {
            LOW,
            MIDDLE,
            HIGHT
        }

        private Priority priority = Priority.MIDDLE;
        protected int index;

        /**
         * Method which provide the getting of the current recycler item
         *
         * @param context current context
         * @return current instance for the Recycler item
         */
        public abstract BaseRecyclerItem getRecyclerItem(@NonNull Context context);

        public Priority getPriority() {
            return priority;
        }

        public void setPriority(Priority priority) {
            this.priority = priority;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////HELP ADAPTER/////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static class BaseApplicationRecyclerAdapter<T extends BaseObject> extends Adapter<BaseApplicationRecyclerAdapter.ViewHolder> {

        private List<T> listItems;
        private BaseRecyclerCallback itemActionListener;
        private OnLazyLoadCallback lazyLoadCallback;
        private int sizeListOld;

        public BaseApplicationRecyclerAdapter(List<T> listItems) {
            this.listItems = listItems;
            this.sizeListOld = 0;
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
            //Get object
            T recyclerItem = listItems.get(position);
            //Set index
            recyclerItem.setIndex(position);
            //Set up the view
            holder.recycleItem.setUp(recyclerItem);
            //Set object inside the view as WeakReference
            holder.recycleItem.setObject(recyclerItem);
            //Set item listener
            holder.recycleItem.setItemActionListener(itemActionListener);
            //Lazy load
            int listItemSize = listItems.size();
            if ((position == listItemSize - 1)
                    && (listItemSize > sizeListOld)) {
                if (lazyLoadCallback != null) {
                    lazyLoadCallback.onAlmostAtBottom(listItemSize);
                    sizeListOld = listItemSize;
                }
            }
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

        /**
         * Method which provide the setting of the item action listener
         *
         * @param itemActionListener
         */
        public void setItemActionListener(BaseRecyclerCallback itemActionListener) {
            this.itemActionListener = itemActionListener;
            notifyDataSetChanged();
        }

        /**
         * Method which provide the setting of the lazy load callback
         *
         * @param lazyLoadCallback lazy load callback
         */
        public void setLazyLoadCallback(@NonNull OnLazyLoadCallback lazyLoadCallback) {
            this.lazyLoadCallback = lazyLoadCallback;
        }

        /**
         * Method which provide the setting of the old list size
         *
         * @param sizeListOld size list (old value)
         */
        public void setSizeListOld(int sizeListOld) {
            this.sizeListOld = sizeListOld;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////HELP RECYCLER ITEM//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static abstract class BaseRecyclerItem<T extends BaseObject> extends BaseRecyclerView {

        protected WeakReference<BaseRecyclerCallback> callbackReference;
        protected WeakReference<T> objectReference;
        protected int index;

        public BaseRecyclerItem(Context context) {
            super(context);
        }

        /**
         * Method which provide the setting up for the current recycler item
         *
         * @param baseObject current object
         */
        public abstract void setUp(@NonNull T baseObject);

        /**
         * Method which provide the listener initializing
         */
        @Override
        protected void onListenerInitialize() {
            ViewGroup view = (ViewGroup) findViewById(getClickedID());
            if (view != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callbackReference != null
                                && objectReference != null
                                && objectReference.get() != null
                                && callbackReference.get() != null) {
                            T object = objectReference.get();
                            callbackReference.get().onItemClick(index, object);
                        }
                    }
                });
            }
        }

        /**
         * Method which provide the setting of the object inside the view
         *
         * @param object current object
         */
        public void setObject(@NonNull T object) {
            index = object.index;
            objectReference = new WeakReference<T>(object);
        }

        /**
         * Method which provide the setting of the item action listener
         *
         * @param itemActionListener
         */
        public void setItemActionListener(BaseRecyclerCallback itemActionListener) {
            this.callbackReference = new WeakReference<BaseRecyclerCallback>(itemActionListener);
        }

        /**
         * Method which provide the event sending
         *
         * @param recycleEvent recycler event
         */
        protected void sendEvent(@NonNull BaseRecyclerCallback.RecycleEvent recycleEvent) {
            if (callbackReference != null
                    && callbackReference.get() != null
                    && objectReference != null
                    && objectReference.get() != null) {
                callbackReference.get().onActionPerformed(recycleEvent, index, objectReference.get());
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////BASE VIEW///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static abstract class BaseRecyclerView extends FrameLayout implements View.OnClickListener {

        /**
         * Interface which provide the doing some action inside the Handler thread
         */
        protected interface OnActionPerformer {
            void onActionPerform();
        }

        protected static int K_DEFAULT_ID = Integer.MIN_VALUE;

        private final Handler MAIN_THREAD_HANDLER = new Handler();

        protected View baseView;

        public BaseRecyclerView(@NonNull final Context context) {
            super(context);
            onInitializeView(context, null);
        }

        /**
         * Method which provide the attribute initialize
         *
         * @param context current context
         * @param attrs   current attribute
         */
        private void onInitializeView(@NonNull Context context, @Nullable AttributeSet attrs) {

            if (isInEditMode() == true) {
                return;
            }

            inflateView(context, getLayoutId());
            if (baseView != null) {
                ButterKnife.inject(this, baseView);
                if (attrs != null) {
                    onAttributeInitialize(attrs);
                }
            }
            onListenerInitialize();
            onCreateView();
        }

        /**
         * Method which provide the attribute initializing
         *
         * @param attrs attributes
         */
        protected void onAttributeInitialize(@NonNull AttributeSet attrs) {

        }

        /**
         * Method which provide the UI customizing with accordance to the custom attributes
         */
        protected void onApplyAttributes() {
        }

        /**
         * Method which provide the inflating of the view
         *
         * @param context  current context
         * @param layoutID layout id
         */
        private void inflateView(@NonNull Context context, int layoutID) {
            LayoutInflater inflater = LayoutInflater.from(context);
            baseView = inflater.inflate(layoutID, this);
        }

        /**
         * Method which provide the listener initializing
         */
        protected abstract void onListenerInitialize();


        /**
         * Method which provide to getting of the layout ID
         *
         * @return layout ID
         */
        protected abstract int getLayoutId();

        /**
         * Method which provide the getting of the clicked view ID
         *
         * @return clicked view ID
         */
        protected abstract int getClickedID();

        /**
         * Method which provide the action when view will create
         */
        protected abstract void onCreateView();

        /**
         * Method which provide starting the Activity
         *
         * @param activtyClass activity which should be starting
         */
        public void startActivity(@NonNull Class activtyClass) {
            getContext().startActivity(new Intent(getContext(), activtyClass));
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
        }

        /**
         * Method which provide the start activity with top clearing
         *
         * @param activtyClass activity class
         */
        protected void startActivityWithClearTop(@NonNull Class activtyClass) {
            Intent intent = new Intent(getContext(), activtyClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getContext().startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
        }

        /**
         * Method which provide starting the Activity for results
         *
         * @param activtyClass activity which should be starting
         * @param resultCode   result code
         */
        protected void startActivityForResults(@NonNull Class activtyClass, final int resultCode) {
            if (getActivity() != null) {
                getActivity().startActivityForResult(new Intent(getContext(), activtyClass), resultCode);
            }
        }

        /**
         * Method which provide starting the Activity for results
         *
         * @param activtyClass activity which should be starting
         * @param resultCode   result code
         */
        protected void startActivityForResults(@NonNull Intent activtyClass, final int resultCode) {
            if (getActivity() != null) {
                getActivity().startActivityForResult(activtyClass, resultCode);
            }
        }

        /**
         * Method which provide the setting of the OnClickListener
         *
         * @param views current list of Views
         */
        protected void setOnClickListeners(@NonNull View... views) {
            for (View view : views) {
                view.setOnClickListener(this);
            }
        }

        /**
         * Method which provide the action for onClickListener
         *
         * @param v
         */
        @Override
        public void onClick(View v) {

        }

        /**
         * Method which provide the message showing in the snack bar
         *
         * @param message current message value
         */
        protected void showMessage(@NonNull String message) {
            Snackbar snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

        /**
         * Method which provide the doing action on UI thread after the delaying time
         *
         * @param delay     delaying time (in seconds)
         * @param performer current action
         */
        protected void runOnMainThread(int delay, final OnActionPerformer performer) {
            MAIN_THREAD_HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    performer.onActionPerform();
                }
            }, delay);
        }

        /**
         * Method which provide the running action on the background thread
         *
         * @param delay             delay
         * @param onActionPerformer action performer
         */
        protected void runOnBackground(int delay, final OnActionPerformer onActionPerformer) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    onActionPerformer.onActionPerform();
                }
            };
            new Thread(runnable).start();
        }

        /**
         * Method which provide the keyboard hiding
         */
        protected void hideKeyboard() {
            Activity activity = getActivity();
            if (activity != null) {
                InputMethodManager inputMethod = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethod.hideSoftInputFromWindow(activity.getWindow().getDecorView().getRootView().getWindowToken(), 0);
            }
        }

        /**
         * Method which provide the getting of the current activity from view
         *
         * @return getting activity
         */
        protected Activity getActivity() {
            Context context = getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////CALLBACKS///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public interface BaseRecyclerCallback<T extends BaseObject> {

        class RecycleEvent {
            private final int eventCode;

            public RecycleEvent(int eventCode) {
                this.eventCode = eventCode;
            }
        }

        /**
         * Method which provide the action when user press on the channel object
         *
         * @param index  current index
         * @param object current object
         */
        void onItemClick(int index, @NonNull final T object);

        /**
         * Method which provide the action listening
         *
         * @param recycleEvent event
         * @param index        index
         * @param object       object
         */
        void onActionPerformed(RecycleEvent recycleEvent, int index, @NonNull final T object);
    }

    /**
     * Callback which provide the lazy loading inside the RecyclerView
     */
    public interface OnLazyLoadCallback {
        /**
         * Method which provide the notifying about end of list
         *
         * @param listSize list size
         */
        void onAlmostAtBottom(int listSize);
    }

    //COMPARATORS

    private static class PriorityComparator implements Comparator<BaseObject> {
        @Override
        public int compare(BaseObject lhs, BaseObject rhs) {
            return rhs.getPriority().compareTo(lhs.getPriority());
        }
    }

}
