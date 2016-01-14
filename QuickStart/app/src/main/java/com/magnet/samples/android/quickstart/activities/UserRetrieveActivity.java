/*
 *  Copyright (c) 2016 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magnet.samples.android.quickstart.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.samples.android.quickstart.R;
import com.magnet.samples.android.quickstart.adapters.UserListAdapter;
import com.magnet.samples.android.quickstart.util.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class UserRetrieveActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private Spinner modeChooser;
    private EditText retrieveText;
    private ListView retrieveResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_retrieve);
        modeChooser = (Spinner) findViewById(R.id.retrieveMode);
        modeChooser.setOnItemSelectedListener(this);
        retrieveText = (EditText) findViewById(R.id.retrieveText);
        retrieveResult = (ListView) findViewById(R.id.retrieveResult);
        findViewById(R.id.retrieveBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retrieveBtn:
                String query = retrieveText.getText().toString();
                if (!query.isEmpty()) {
                    search(query);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                retrieveText.setText(User.getCurrentUserId());
                retrieveText.setHint("userID1, userID2, userID3");
                break;
            case 1:
                retrieveText.setText(User.getCurrentUser().getUserName());
                retrieveText.setHint("userName1, userName2, userName3");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void search(String query) {
        int selected = modeChooser.getSelectedItemPosition();
        List<String> searchItems = getSearchingItems(query);
        switch (selected) {
            case 0:
                searchByIds(searchItems);
                break;
            case 1:
                searchByUserNames(searchItems);
                break;
        }
    }

    private void searchByIds(List<String> searchItems) {
        User.getUsersByUserIds(searchItems, new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> users) {
                Logger.debug("retrieve users by ID", "success");
                updateList(users);
            }

            @Override
            public void failure(ApiError apiError) {
                showMessage("Can't retrieve users by ID: " + apiError.getMessage());
                Logger.error("retrieve users", apiError, "error");
            }
        });
    }

    private void searchByUserNames(List<String> searchItems) {
        User.getUsersByUserNames(searchItems, new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> users) {
                Logger.debug("retrieve users by userName", "success");
                updateList(users);
            }

            @Override
            public void failure(ApiError apiError) {
                showMessage("Can't retrieve users by userName: " + apiError.getMessage());
                Logger.error("retrieve users", apiError, "error");
            }
        });
    }

    private List<String>  getSearchingItems(String query) {
        List<String> items = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(query, ",");
        while (tokenizer.hasMoreTokens()) {
            items.add(tokenizer.nextToken());
        }
        return items;
    }

    private void updateList(List<User> users) {
        UserListAdapter adapter = new UserListAdapter(this, users);
        retrieveResult.setAdapter(adapter);
    }

}
