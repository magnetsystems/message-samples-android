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
import java.util.List;

public class UserSearchActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private EditText searchText;
    private ListView searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        Spinner modeChooser = (Spinner) findViewById(R.id.searchMode);
        modeChooser.setOnItemSelectedListener(this);
        searchText = (EditText) findViewById(R.id.searchText);
        searchResult = (ListView) findViewById(R.id.searchResult);
        findViewById(R.id.searchBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchBtn:
                String query = searchText.getText().toString();
                if (!query.isEmpty()) {
                    search(query);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = "";
        String currentUsername = User.getCurrentUser().getUserName();
        if (currentUsername != null) {
            switch (position) {
                case 0:
                    text = "userName:" + currentUsername.charAt(0) + "*";
                    break;
                case 1:
                    text = "userName:*" + currentUsername.charAt(currentUsername.length() - 1);
                    break;
            }
        }
        searchText.setText(text);
        searchText.setHint(text);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void search(String query) {
        User.search(query, 100, 0, "userName:asc", new ApiCallback<List<User>>() {
            @Override
            public void success(List<User> users) {
                Logger.debug("search users", "success");
                updateList(users);
            }

            @Override
            public void failure(ApiError apiError) {
                showMessage("Can't search users : " + apiError.getMessage());
                Logger.error("search users", apiError, "error");
            }
        });
    }

    private void updateList(List<User> users) {
        UserListAdapter adapter = new UserListAdapter(this, users);
        searchResult.setAdapter(adapter);
    }

}
