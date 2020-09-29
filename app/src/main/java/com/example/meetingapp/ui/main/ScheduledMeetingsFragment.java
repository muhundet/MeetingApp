package com.example.meetingapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetingapp.R;
import com.example.meetingapp.adapters.MeetingsRecyclerAdapter;
import com.example.meetingapp.data.MeetingsDataManager;
import com.example.meetingapp.database.MeetingsOpenHelper;
import com.example.meetingapp.models.Meeting;

import java.util.ArrayList;


public class ScheduledMeetingsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private RecyclerView rvMeetings;
    MeetingsRecyclerAdapter adapter;
    ArrayList<Meeting> meetings;

    public static ScheduledMeetingsFragment newInstance(int index) {
        ScheduledMeetingsFragment fragment = new ScheduledMeetingsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scheduled_meeting, container, false);
        rvMeetings = (RecyclerView) root.findViewById(R.id.rv_scheduled_meetings);
        LinearLayoutManager meetingsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvMeetings.setLayoutManager(meetingsLayoutManager);

        meetings = MeetingsDataManager.getInstance().getMeetings();
        MeetingsDataManager.readMeetings(new MeetingsOpenHelper(getContext()));
        adapter = new MeetingsRecyclerAdapter(meetings);
        rvMeetings.setAdapter(adapter);
        return root;
    }
}