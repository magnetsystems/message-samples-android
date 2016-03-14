package com.magnet.magnetchat.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import com.magnet.magnetchat.R;

/**
 * Created by dlernatovich on 2/10/16.
 */
public class CustomSearchView extends SearchView {

    private static final String K_SEARCH_PLATE_IDENTIFIER = "android:id/search_src_text";
    private static final String K_SEARCH_ICON_IDENTIFIER = "android:id/search_button";
    private static final String K_SEARCH_EDIT_IDENTIFIER = "android:id/search_edit_frame";

    private static final int K_DEFAULT_ID = Integer.MIN_VALUE;

    private EditText searchPlate;

    private
    int colorText;
    private int colorHint;
    private String textHint;
    private float sizeText;
    private int iconSearch;

    public CustomSearchView(Context context) {
        super(context);
        onInitializeByDefaults(context);
        onCustomize();
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitializeByDefaults(context);
        onGetAttributes(context, attrs);
        onCustomize();
    }

    /**
     * Method which provide to getting of the custom attributes
     *
     * @param context      context
     * @param attributeSet attribute set
     */
    private void onGetAttributes(Context context, AttributeSet attributeSet) {

        if ((attributeSet == null) || (context == null)) {
            return;
        }

        TypedArray a = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.CustomSearchView, 0, 0);
        try {
            colorText = a.getColor(R.styleable.CustomSearchView_colorText, 0);
            colorHint = a.getColor(R.styleable.CustomSearchView_colorHintText, 0);
            textHint = a.getString(R.styleable.CustomSearchView_textHint);
            sizeText = a.getDimension(R.styleable.CustomSearchView_sizeText, R.dimen.text_15);
            iconSearch = a.getResourceId(R.styleable.CustomSearchView_iconSearch, R.drawable.ic_search_tabbar);
        } catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        } finally {
            a.recycle();
        }
    }

    /**
     * Method which provide the initialize by defaults
     *
     * @param context
     */
    private void onInitializeByDefaults(@Nullable Context context) {
        if (context != null) {
            colorText = android.R.color.white;
            colorHint = android.R.color.darker_gray;
            textHint = "Search message";
            sizeText = context.getResources().getDimension(R.dimen.text_15);
            iconSearch = R.drawable.ic_action_name;
        }
    }

    /**
     * Method which provide the search view customization
     */
    private void onCustomize() {
        try {

            int searchPlateId = getContext().getResources().getIdentifier(K_SEARCH_PLATE_IDENTIFIER, null, null);
            int searchIconID = getContext().getResources().getIdentifier(K_SEARCH_ICON_IDENTIFIER, null, null);
            int searchEditFrameID = getContext().getResources().getIdentifier(K_SEARCH_EDIT_IDENTIFIER, null, null);

            searchPlate = (EditText) findViewById(searchPlateId);

            searchPlate.setTextColor(getResources().getColor(colorText));
            searchPlate.setHintTextColor(getResources().getColor(colorHint));

//            Drawable img = getContext().getResources().getDrawable(R.drawable.ic_search_blue);
//            searchPlate.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

            searchPlate.setHint(textHint);
            searchPlate.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeText);
            //searchPlate.setTypeface(TypeFaceManager.getInstance().getBarriolFont());
            searchPlate.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

//            LinearLayout editFrame = (LinearLayout) findViewById(searchEditFrameID);
//            editFrame.setBackgroundResource(R.drawable.background_edit_text_small);


//            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
//            searchField.setAccessible(true);
//            ImageView closeBtn = (ImageView) searchField.get(this);
//            closeBtn.setImageResource(R.drawable.ic_close_icon_small);

            ImageView searchButton = (ImageView) findViewById(searchIconID);
            searchButton.setImageResource(iconSearch);

        } catch (Exception e) {
            Log.e("SearchView", e.getMessage(), e);
        }
    }

    public void setHint(String hint) {
        searchPlate.setHint(hint);
    }

}
