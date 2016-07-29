/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * MODIFICATIONS:
 * - getEditTextPreference() returns com.takisoft.fix.support.v7.preference.EditTextPreference instead of android.support.v7.preference.EditTextPreference
 * - onBindDialogView(View view) retrieves the EditText from com.takisoft.fix.support.v7.preference.EditTextPreference
 */

package com.takisoft.fix.support.v7.preference;

import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;

public class EditTextPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    private EditText mEditText;

    public EditTextPreferenceDialogFragmentCompat() {
    }

    public static EditTextPreferenceDialogFragmentCompat newInstance(String key) {
        EditTextPreferenceDialogFragmentCompat fragment = new EditTextPreferenceDialogFragmentCompat();
        Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        this.mEditText = getEditTextPreference().getEditText();
        this.mEditText.setText(this.getEditTextPreference().getText());

        Editable text = mEditText.getText();
        if (text != null) {
            mEditText.setSelection(text.length(), text.length());
        }

        ViewParent oldParent = this.mEditText.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(this.mEditText);
            }

            this.onAddEditTextToDialogView(view, this.mEditText);
        }
    }

    private EditTextPreference getEditTextPreference() {
        return (EditTextPreference) this.getPreference();
    }

    protected boolean needInputMethod() {
        return true;
    }

    protected void onAddEditTextToDialogView(View dialogView, EditText editText) {
        //ViewGroup container = (ViewGroup) dialogView.findViewById(android.support.v7.preference.R.id.edittext_container);
        View oldEditText = dialogView.findViewById(android.R.id.edit);
        if (oldEditText != null) {
            ViewGroup container = (ViewGroup) (oldEditText.getParent());
            if (container != null) {
                container.removeView(oldEditText);
                container.addView(editText, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String value = this.mEditText.getText().toString();
            if (this.getEditTextPreference().callChangeListener(value)) {
                this.getEditTextPreference().setText(value);
            }
        }

    }
}
