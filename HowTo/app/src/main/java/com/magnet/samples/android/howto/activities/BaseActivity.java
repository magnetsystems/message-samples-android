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

package com.magnet.samples.android.howto.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected String getFieldText(int fieldId) {
        hideKeyboard();
        EditText editText = (EditText) findViewById(fieldId);
        if (editText != null) {
            return editText.getText().toString().trim();
        }
        return null;
    }

    public void hideKeyboard() {
        InputMethodManager inputMethod = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethod.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showMessage(int stringRes) {
        showMessage(getString(stringRes));
    }

    public void setText(int textViewId, String text) {
        TextView textView = (TextView) findViewById(textViewId);
        if (textView != null) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void setText(int textViewId, int stringRes) {
        setText(textViewId, getString(stringRes));
    }

}
