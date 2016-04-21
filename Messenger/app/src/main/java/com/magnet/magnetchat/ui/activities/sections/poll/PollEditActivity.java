package com.magnet.magnetchat.ui.activities.sections.poll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.activities.abs.BaseActivity;

import butterknife.InjectView;
import com.magnet.mmx.client.ext.poll.MMXPoll;
import java.util.ArrayList;
import java.util.List;

public class PollEditActivity extends BaseActivity {
    public final static int REQUEST_CODE = 212;

    @InjectView(R.id.toolbar) Toolbar toolbar;
    //@InjectView(R.id.rvOptions) RecyclerView rvOptions;
    @InjectView(R.id.rvOptions) ListView rvOptions;
    @InjectView(R.id.btnAdd) Button btnAdd;
    @InjectView(R.id.etOptionText) EditText etOptionText;
    @InjectView(R.id.etName) EditText etName;
    @InjectView(R.id.etQuestion) EditText etQuestion;
    @InjectView(R.id.cbMultiChoice) CheckBox cbMultiChoice;
    @InjectView(R.id.cbHideResults) CheckBox cbHideResults;

    //PollOptionItemAdapter pollOptionItemAdapter;
    ArrayAdapter<String> itemsAdapter;
    List<String> options = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create a Poll");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        //rvOptions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //rvOptions.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        //pollOptionItemAdapter = new PollOptionItemAdapter(this, null);
        //rvOptions.setAdapter(pollOptionItemAdapter);

        itemsAdapter =
            new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
        rvOptions.setAdapter(itemsAdapter);

        setOnClickListeners(R.id.btnAdd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_poll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                MMXPoll.Builder newPollBuilder = new MMXPoll.Builder().name(etName.getText().toString())
                    .question(etQuestion.getText().toString())
                    .hideResultsFromOthers(cbHideResults.isChecked())
                    .allowMultiChoice(cbMultiChoice.isChecked());
                for(String s : options) {
                    newPollBuilder.option(s);
                }
                Intent data = new Intent();
                data.putExtra("newPoll", newPollBuilder.build());
                setResult(RESULT_OK,data);
                finish();

                return true;
            case R.id.action_cancel:
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override protected int getLayoutResource() {
        return R.layout.activity_poll_edit;
    }

    public static void startPollEdit(Activity activity) {
        Intent intent = new Intent(activity, PollEditActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override public void onClick(View v) {
        if(v.getId() == R.id.btnAdd) {
            //pollOptionItemAdapter.addOption(etOptionText.getText().toString());
            options.add(etOptionText.getText().toString());
            itemsAdapter.notifyDataSetChanged();
            etOptionText.setText("");
        }
    }
}
