package com.magnet.magnetchat.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.core.managers.TypeFaceManager;

import java.lang.reflect.Field;

/**
 * Created by dlernatovich on 2/10/16.
 */
public class CustomSearchView extends SearchView {

    public CustomSearchView(Context context) {
        super(context);
        onCustomize();
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCustomize();
    }


    private void onCustomize() {
        try {

            int searchPlateId = getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            int searchIconID = getContext().getResources().getIdentifier("android:id/search_button", null, null);
            int searchEditFrameID = getContext().getResources().getIdentifier("android:id/search_edit_frame", null, null);

            EditText searchPlate = (EditText) findViewById(searchPlateId);

            searchPlate.setTextColor(getResources().getColor(android.R.color.black));
            searchPlate.setHintTextColor(getResources().getColor(R.color.colorLightGray));

//            Drawable img = getContext().getResources().getDrawable(R.drawable.ic_search_blue);
//            searchPlate.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

            searchPlate.setHint("Search messages");
            searchPlate.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_14));
            searchPlate.setTypeface(TypeFaceManager.getInstance().getBarriolFont());
            searchPlate.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

//            LinearLayout editFrame = (LinearLayout) findViewById(searchEditFrameID);
//            editFrame.setBackgroundResource(R.drawable.background_edit_text_small);


//            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
//            searchField.setAccessible(true);
//            ImageView closeBtn = (ImageView) searchField.get(this);
//            closeBtn.setImageResource(R.drawable.ic_close_icon_small);

            ImageView searchButton = (ImageView) findViewById(searchIconID);
            searchButton.setImageResource(R.drawable.ic_search_blue);

        } catch (Exception e) {
            Log.e("SearchView", e.getMessage(), e);
        }
    }
}
