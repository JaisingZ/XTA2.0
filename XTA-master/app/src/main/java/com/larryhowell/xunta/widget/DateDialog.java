package com.larryhowell.xunta.widget;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * 日期选择对话框
 */
public class DateDialog extends DatePickerDialog {

    public DateDialog(Context context, OnDateSetListener callBack,
                      int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onStop() {
    }
}