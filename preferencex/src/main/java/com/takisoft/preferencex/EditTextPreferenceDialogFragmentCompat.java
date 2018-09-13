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
 * - getEditTextPreference() returns com.takisoft.preferencex.EditTextPreference instead of androidx.preference.EditTextPreference
 * - onBindDialogView(View view) retrieves the EditText from com.takisoft.preferencex.EditTextPreference
 */

package com.takisoft.preferencex;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.PreferenceDialogFragmentCompat;
import android.text.Editable;
import android.view.LayoutInflater;
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

    @Override
    protected View onCreateDialogView(Context context) {
        View view = super.onCreateDialogView(context), oldChild = view.findViewById(android.R.id.edit);
        if (oldChild == null) {
            throw new IllegalStateException("Dialog view must contain an EditText with id" +
                    " @android:id/edit");
        }

        ViewGroup container = (ViewGroup) oldChild.getParent();
        int layout = getEditTextPreference().getEditLayout();
        View newChild = layout == 0 ? getEditTextPreference().getEditText()
                : LayoutInflater.from(context).inflate(layout, container, false);
        this.mEditText = newChild.findViewById(android.R.id.edit);
        this.mEditText.setText(this.getEditTextPreference().getText());

        Editable text = mEditText.getText();
        if (text != null) {
            mEditText.setSelection(text.length(), text.length());
        }

        ViewParent oldParent = newChild.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(newChild);
            }

            if (container != null) {
                container.removeView(oldChild);
                container.addView(newChild, oldChild.getLayoutParams());
            }
        }
        return view;
    }

    private EditTextPreference getEditTextPreference() {
        return (EditTextPreference) this.getPreference();
    }

    protected boolean needInputMethod() {
        return true;
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
