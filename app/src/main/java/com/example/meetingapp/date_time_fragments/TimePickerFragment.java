package com.example.meetingapp.date_time_fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.meetingapp.R;

import java.util.Calendar;

public  class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public static String textTime;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user

        TextView meetingTime = (TextView) getActivity().findViewById(R.id.text_meeting_time);
        if(minute<10){
            meetingTime.setText(String.valueOf(hourOfDay + ":0" + String.valueOf(minute)));
        }else {
            meetingTime.setText(String.valueOf(hourOfDay + ":" + String.valueOf(minute)));
        }
        Toast.makeText(getContext(), "Meeting Time Set " , Toast.LENGTH_LONG).show();
    }
}