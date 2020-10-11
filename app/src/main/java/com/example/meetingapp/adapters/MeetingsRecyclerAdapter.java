package com.example.meetingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetingapp.R;
import com.example.meetingapp.ViewMeetingActivity;
import com.example.meetingapp.models.Meeting;

import java.util.ArrayList;

public class MeetingsRecyclerAdapter extends RecyclerView.Adapter<MeetingsRecyclerAdapter.MeetingViewHolder>  {

    ArrayList<Meeting> meetings;

    public MeetingsRecyclerAdapter(ArrayList<Meeting> meetings) {
        this.meetings = meetings;
    }
    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.meeting_list_item, parent, false);

        return new MeetingViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(MeetingViewHolder holder, int position) {
        Meeting meeting = meetings.get(position);
        holder.bind(meeting);

    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }


    public class MeetingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        TextView textMeetingId;
        TextView textAgenda;
        TextView textVenue;
        TextView textMeetingDate;
        TextView textMeetingTime;

        public MeetingViewHolder(View itemView) {
            super(itemView);
//            textMeetingId = (TextView) itemView.findViewById(R.id.text_meeting_id);
            textAgenda = (TextView) itemView.findViewById(R.id.text_agenda);
            textVenue = (TextView) itemView.findViewById(R.id.text_venue);
            textMeetingDate = (TextView) itemView.findViewById(R.id.text_meeting_date);
            textMeetingTime =(TextView) itemView.findViewById(R.id.text_meeting_time);
            itemView.setOnClickListener(this);

        }
        public void bind (Meeting meeting) {
//            textMeetingId.setText(meeting.meetingId);
            textAgenda.setText(meeting.agenda);
            textVenue.setText(meeting.venue);
            textMeetingDate.setText(meeting.meetingDate);
            textMeetingTime.setText(meeting.meetingTime);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            //gets the meeting from the arrayList
            Meeting selectedMeeting = meetings.get(position);
            Intent intent = new Intent(view.getContext(), ViewMeetingActivity.class);
            intent.putExtra("meeting", selectedMeeting);
            view.getContext().startActivity(intent);
        }
    }
}

