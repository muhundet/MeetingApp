package com.example.meetingapp.date_time_fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.meetingapp.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        int actualMonth = month + 1;
        // Do something with the date chosen by the user
        TextView meetingDate = (TextView) getActivity().findViewById(R.id.text_meeting_date);
        if(actualMonth<10 && day<10){
            meetingDate.setText(year + "/0" + actualMonth + "/" + "0"+ day);

        }else if(day<10){
            meetingDate.setText(year + "/" + actualMonth + "/" + "0"+ day );
        }else if(actualMonth<10){
            meetingDate.setText(year + "/0" + actualMonth + "/" + day );
        }else
        meetingDate.setText( year + "/" + actualMonth + "/" + day);
        Toast.makeText(getContext(), "Meeting Date Set " , Toast.LENGTH_LONG).show();
    }
}