package com.larryhowell.xunta.widget;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * 时间选择对话框
 */
public class TimeDialog extends TimePickerDialog {
    public TimeDialog(Context context, OnTimeSetListener listener,
                      int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
    }

    @Override
    protected void onStop() {
    }
}