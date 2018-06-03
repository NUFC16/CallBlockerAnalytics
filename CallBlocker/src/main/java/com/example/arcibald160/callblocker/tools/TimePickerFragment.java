package com.example.arcibald160.callblocker.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private EditText editText;

    @SuppressLint("ValidFragment")
    public TimePickerFragment(EditText editText) {
        this.editText = editText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        if (!TextUtils.isEmpty(editText.getText().toString())) {
            hour = getCurrentPickedTime()[0];
            minute = getCurrentPickedTime()[1];
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
        String min = (minutes < 10) ? "0" + minutes: Integer.toString(minutes);
        editText.setText(hours + ":" + min);
    }

    private int[] getCurrentPickedTime() {
        String[] splitTime = this.editText.getText().toString().split(":");
        int[] intSplitTime = new int[splitTime.length];
        // first part is hour, second minutes
        for(int i=0; i<splitTime.length; i++) {
            intSplitTime[i] = Integer.parseInt(splitTime[i]);
        }
        return intSplitTime;
    }
}
