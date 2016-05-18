package com.magnet.magnetchat.ui.views.poll;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.MMXObjectWrapper;
import com.magnet.magnetchat.model.MMXStringWrapper;
import com.magnet.magnetchat.ui.adapters.RecyclerViewTypedAdapter;
import com.magnet.magnetchat.ui.factories.MMXListItemFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aorehov on 17.05.16.
 */
public class NewDefaultMMXEditPollView extends MMXEditPollView<EditPollProperty> {

    private EditText uiName;
    private EditText uiQuestion;
    private CheckBox uiIsMultiple;
    private CheckBox uiIsHideAnswers;
    private RecyclerView uiRecyclerView;
    private EditText uiAddAnswer;
    private View uiAddAction;

    private RecyclerViewTypedAdapter<MMXStringWrapper> adapter;

    public NewDefaultMMXEditPollView(Context context) {
        super(context);
    }

    public NewDefaultMMXEditPollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewDefaultMMXEditPollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_mmxchat_poll_edit;
    }

    @Override
    protected void onLinkingViews(View baseView) {
        uiName = findView(baseView, R.id.mmx_name);
        uiQuestion = findView(baseView, R.id.mmx_question);
        uiIsMultiple = findView(baseView, R.id.mmx_multiple);
        uiIsHideAnswers = findView(baseView, R.id.mmx_hide);
        uiRecyclerView = findView(baseView, R.id.mmx_recycler_view);
        uiAddAnswer = findView(baseView, R.id.mmx_answer);
        uiAddAction = findView(baseView, R.id.mmx_add);

        uiAddAction.setOnClickListener(this);


        MMXListItemFactory factory = createMMXListItemFactory();
        adapter = new RecyclerViewTypedAdapter<>(factory, MMXStringWrapper.class, new RecyclerViewTypedAdapter.ItemComparator<MMXStringWrapper>() {
            @Override
            public int compare(MMXStringWrapper o1, MMXStringWrapper o2) {
                return -1;
            }

            @Override
            public boolean areContentsTheSame(MMXStringWrapper o1, MMXStringWrapper o2) {
                return o1.getObj().equals(o2.getObj());
            }

            @Override
            public boolean areItemsTheSame(MMXStringWrapper item1, MMXStringWrapper item2) {
                return item1.equals(item2);
            }
        });

        uiRecyclerView.setAdapter(adapter);
        uiRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mmx_add) {
            addAnswer();
        } else
            super.onClick(v);
    }

    private void addAnswer() {
        String answer = uiAddAnswer.getText().toString().trim();
        uiAddAnswer.setText("");
        if (answer.length() == 0) {
            return;
        }
        int put = adapter.put(new MMXStringWrapper(answer));
        uiRecyclerView.scrollToPosition(put);
    }

    @Override
    public void setProperties(EditPollProperty property) {

    }

    @Override
    public String getName() {
        return uiName.getText().toString();
    }

    @Override
    public String getQuestion() {
        return uiQuestion.getText().toString();
    }

    @Override
    public List<String> getAnswers() {
        ArrayList<String> answers = new ArrayList<>(adapter.getItemCount());

        for (int index = 0; index < adapter.getItemCount(); index++) {
            MMXObjectWrapper item = adapter.getItem(index);
            answers.add((String) item.getObj());
        }

        return answers;
    }

    @Override
    public boolean isAllowMultipleChoice() {
        return uiIsMultiple.isChecked();
    }

    @Override
    public boolean isHiderResult() {
        return uiIsHideAnswers.isChecked();
    }
}
