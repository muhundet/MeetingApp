package com.example.meetingapp;


import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.meetingapp.date_time_fragments.DatePickerFragment;
import com.example.meetingapp.date_time_fragments.TimePickerFragment;
import com.example.meetingapp.database.MeetingsDatabaseContract;
import com.example.meetingapp.database.MeetingsOpenHelper;
import com.example.meetingapp.zoom.AuthConstants;
import com.example.meetingapp.zoom.InitAuthSDKCallback;
import com.example.meetingapp.zoom.InitAuthSDKHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import us.zoom.sdk.AccountService;
import us.zoom.sdk.MeetingItem;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.PreMeetingError;
import us.zoom.sdk.PreMeetingService;
import us.zoom.sdk.PreMeetingServiceListener;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;


public class CreateMeetingActivity extends AppCompatActivity implements AuthConstants, ZoomSDKInitializeListener, InitAuthSDKCallback, PreMeetingServiceListener, MeetingServiceListener, ZoomSDKAuthenticationListener {

    private SQLiteDatabase mDb;
    private String mMeetingId;
    private String mAgenda;
    private String mVenue;
    private String mMeetingDate;
    private String mMeetingTime;
    private long  mZoomMeetingID;
    private boolean mIsZoomEnabled;
    private boolean mIsTeamsEnabled;
    private boolean mIsZoomMeetingScheduled;
    private boolean mIsTeamsMeetingScheduled;
    private Calendar mDate;
    EditText textMeetingTime;
    EditText textMeetingId;

    Calendar calendar = Calendar.getInstance();
    TimePickerDialog.OnTimeSetListener timeSetListener;
    private AccountService mAccountService;
    private PreMeetingService mPreMeetingService = null;
    private long mTeamsMeetingID;
    ZoomSDK mZoomSDK;

//    private ZoomSDKAuthenticationListener authListener = new ZoomSDKAuthenticationListener() {
//        /**
//         * This callback is invoked when a result from the SDK's request to the auth server is
//         * received.
//         */
//
//        @Override
//        public void onZoomSDKLoginResult(long result) {
//
//        }
//
//        @Override
//        public void onZoomSDKLogoutResult(long l) {}
//        @Override
//        public void onZoomIdentityExpired() {}
//        @Override
//        public void onZoomAuthIdentityExpired() {}
//    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        final EditText textAgenda = findViewById(R.id.text_agenda);
        final EditText textVenue = findViewById(R.id.text_venue);
        final EditText textMeetingDate =  findViewById(R.id.text_meeting_date);
        final EditText textMeetingTime = findViewById(R.id.text_meeting_time);
        final CheckBox chkScheduleZoom = findViewById(R.id.chkScheduleZoomMeeting);
        final CheckBox chkScheduleTeams = findViewById(R.id.chkSecduleTeamsMeeting);
        final Button btnCreateMeeting = findViewById(R.id.button_create_meeting);
        //InitAuthSDKHelper.getInstance().initSDK(this, this);



        textMeetingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        textMeetingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }

        });

        chkScheduleZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeetingId = "MEETINGID" + textMeetingDate.getText().toString() + textMeetingTime.getText().toString();
                mAgenda = textAgenda.getText().toString();
                mVenue = textVenue.getText().toString();
                mMeetingDate = textMeetingDate.getText().toString();
                mMeetingTime = textMeetingTime.getText().toString();

                initializeSdk(CreateMeetingActivity.this);
                createLoginDialog();


            }
        });
        btnCreateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeetingId = "MEETINGID" + textMeetingDate.getText().toString() + textMeetingTime.getText().toString();
                mAgenda = textAgenda.getText().toString();
                mVenue = textVenue.getText().toString();
                mMeetingDate = textMeetingDate.getText().toString();
                mMeetingTime = textMeetingTime.getText().toString();

                if(mMeetingId.equals("") && mAgenda.equals("") && mVenue.equals("") && mMeetingDate == null && mMeetingTime.equals("")) {
                    Toast.makeText(CreateMeetingActivity.this, "Make sure you have entered all meeting details ", Toast.LENGTH_LONG).show();
                }else {
                    if(chkScheduleZoom.isChecked()){
                        mIsZoomEnabled = true;
                        try {
                            scheduleZoomMeeting(mMeetingId, mAgenda, mMeetingDate, mMeetingTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.d("ZOOM", ">>>>>>>>>>>>mIsZoomEnabled, checked button<<<<<<<<<<<<<<<<<<<<<<<");

                    }else{
                        mZoomMeetingID = 0;
                    }
                    if(chkScheduleTeams.isChecked()){
                        mIsTeamsEnabled =true;
                        scheduleTeamsMeeting(mMeetingId, mAgenda, mMeetingDate, mMeetingTime);
                        Toast.makeText(CreateMeetingActivity.this, "Teams meeting schedule enabled ", Toast.LENGTH_LONG).show();
                    }else{
                        mTeamsMeetingID = 0;
                    }

                    Log.d("ZOOM", ">>>>>>>>>>>>Now going to save database: ZoomID =" + mZoomMeetingID +"<<<<<<<<<<<<<<<<<<<<<<<");
                    //                      textMeetingId.setText(null);
                    textAgenda.setText(null);
                    textVenue.setText(null);
                    textMeetingDate.setText(null);
                    textMeetingTime.setText(null);


                }
            }
        });

    }

    /**
     * Initialize the SDK with your credentials. This is required before accessing any of the
     * SDK's meeting-related functionality.
     */
    public void initializeSdk(Context context) {
        Log.i("TAG", ">>>>>>>>>>>>>Into initialise SDK Method<<<<<<<<<<<");
        ZoomSDK sdk = ZoomSDK.getInstance();
        // TODO: Do not use hard-coded values for your key/secret in your app in production!
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = APP_KEY; // TODO: Retrieve your SDK key and enter it here
        params.appSecret = APP_SECRET; // TODO: Retrieve your SDK secret and enter it here
        params.domain = "zoom.us";
        params.enableLog = true;
        // TODO: Add functionality to this listener (e.g. logs for debugging)
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            /**
             * @param errorCode {@link us.zoom.sdk.ZoomError#ZOOM_ERROR_SUCCESS} if the SDK has been initialized successfully.
             */
            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
                Log.i("TAG", "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);
            }

            @Override
            public void onZoomAuthIdentityExpired() {}
        };
        sdk.initialize(context, listener, params);
    }

    private void createLoginDialog() {
        new AlertDialog.Builder(this).setView(R.layout.dialog_login).setPositiveButton("Log in Zoom to proceed ", new DialogInterface.OnClickListener() {@Override
        public void onClick(DialogInterface dialogInterface, int i) {
            AlertDialog dialog = (AlertDialog) dialogInterface;
            TextInputEditText emailInput = dialog.findViewById(R.id.email_input);
            TextInputEditText passwordInput = dialog.findViewById(R.id.pw_input);
            if (emailInput != null && emailInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    login(email, password);
                }
            }
            dialog.dismiss();
        }
        }).show();
    }

    public void login(String username, String password) {
        int result = ZoomSDK.getInstance().loginWithZoom(username, password);
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
            // Request executed, listen for result to start meeting
            ZoomSDK.getInstance().addAuthenticationListener(this);
            Log.d("ZOOM", ">>>>>>>>>>>>Email Log in success <<<<<<<<<<<<<<<<<<<<<<<");
        }else{









            Log.d("ZOOM", ">>>>>>>>>>>>Zoom API ERROR, Failure to login with zoom <<<<<<<<<<<<<<<<<<<<<<<");
        }
    }

    private void scheduleTeamsMeeting(String mMeetingId, String mAgenda, String mMeetingDate, String mMeetingTime) {


    }

    private void scheduleZoomMeeting(String meetingId, String agenda, String meetingDate, String meetingTime) throws ParseException {
        Log.d("ZOOM", ">>>>>>>>>>>>Into Schedule Meeting method<<<<<<<<<<<<<<<<<<<<<<<");
        String topic = agenda.trim();
        String zoomMeetingDate = meetingDate.trim();
        String zoomMeetingTime = meetingTime.trim();
        String zoomDateFrom = zoomMeetingDate + " " + zoomMeetingTime +":00";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = sdf.parse(zoomDateFrom);
        long zoomTimeFrom = date.getTime();

        // convert date and time to time format understood by setTime Arg
        if (topic.length() == 0) {
            Toast.makeText(this, "Topic can not be empty", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (zoomSDK.isInitialized()) {
            mAccountService = ZoomSDK.getInstance().getAccountService();
            mPreMeetingService = ZoomSDK.getInstance().getPreMeetingService();
            Log.d("ZOOM", ">>>>>>>>>>>>Account Service, PremeetingService Populated<<<<<<<<<<<<<<<<<<<<<<<");
//                            mCountry = ZoomSDK.getInstance().getAccountService().getAvailableDialInCountry();
            if (mAccountService == null || mPreMeetingService == null) {
                Log.d("ZOOM", ">>>>>>>>>>>>AccountService and PreMeetingService null<<<<<<<<<<<<<<<<<<<<<<<");

            }
                if (zoomSDK.isLoggedIn()) {
                    MeetingItem meetingItem = mPreMeetingService.createScheduleMeetingItem();
                    meetingItem.setMeetingTopic(topic);
                    meetingItem.setStartTime(zoomTimeFrom);
                    meetingItem.setDurationInMinutes(30);
                    meetingItem.setCanJoinBeforeHost(true);
                    meetingItem.setPassword("password");
                    meetingItem.setTimeZoneId(TimeZone.getDefault().getID());
                    Log.d("ZOOM", ">>>>>>>>>>>>Meeting Item populated success<<<<<<<<<<<<<<<<<<<<<<<");
                    registerListener();

                        mPreMeetingService.addListener(this);
                        PreMeetingService.ScheduleOrEditMeetingError error = mPreMeetingService.scheduleMeeting(meetingItem);
                        Log.d("ZOOM", ">>>>>>>>>>>>Zoom Meeting Being Scheduled<<<<<<<<<<<<<<<<<<<<<<<");
                        if (error == PreMeetingService.ScheduleOrEditMeetingError.SUCCESS) {
//                          btnSchedule.setEnabled(false);
                            Log.d("ZOOM", ">>>>>>>>>>>>PreMeetingService successiful<<<<<<<<<<<<<<<<<<<<<<<");
                        } else {
                            Log.d("ZOOM", ">>>>>>>>>>>>PreMeetingService NOT successiful<<<<<<" + error + "<<<<<<<<<<<<<<<<<");
                            Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
                        }

                }else {
//                    createLoginDialog();
                    Log.d("ZOOM", ">>>>>>>>>>>>User not Logged in<<<<<<<<<<<<<<<<<<<<<<<");
                }

        }else {
            Log.d("ZOOM", ">>>>>>>>>>>> Zoom SDK Not Initialized<<<<<<<<<<<<<<<<<<<<<<<");
        }
    }

    private void registerListener() {
        mZoomSDK = ZoomSDK.getInstance();
        mZoomSDK.addAuthenticationListener(this);
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if (meetingService != null) {
            meetingService.addListener(this);
        }
    }
    @Override
    public void onListMeeting(int i, List<Long> list) {

    }

    @Override
    public void onScheduleMeeting(int result, long meetingNumber) {
        if (result == PreMeetingError.PreMeetingError_Success) {
            mZoomMeetingID = meetingNumber;
            insertMeetings(new MeetingsOpenHelper(CreateMeetingActivity.this));
            Log.d("ZOOM", ">>>>>>>>>>>>Schedule successful with unique meetingNumber <<<<<<<<<<<<<<<<<<<<<<<");
            Toast.makeText(this, "Schedule successfully. Meeting's unique id is " + meetingNumber, Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Schedule failed result code =" + result, Toast.LENGTH_LONG).show();
            Log.d("ZOOM", ">>>>>>>>>>>>Schedule fail<<<<<<<<<<<<<<<<<<<<<<<");
//            mBtnSchedule.setEnabled(true);
        }

    }

    @Override
    public void onUpdateMeeting(int i, long l) {

    }

    @Override
    public void onDeleteMeeting(int i) {

    }



    private void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");

        // textMeetingTime.setText(textTime);
    }


    public void insertMeetings(MeetingsOpenHelper dbHelper){
//         = new MeetingsOpenHelper(getContext());
        Log.d("ZOOM", ">>>>>>>>>>>>Now going to save database: ZoomID =" + mZoomMeetingID +"<<<<<<<<<<<<<<<<<<<<<<<");
        mDb = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MeetingsDatabaseContract.MeetingsEntry.COLUMN_MEETING_ID, mMeetingId);
        values.put(MeetingsDatabaseContract.MeetingsEntry.COLUMN_AGENDA, mAgenda);
        values.put(MeetingsDatabaseContract.MeetingsEntry.COLUMN_VENUE, mVenue);
        values.put(MeetingsDatabaseContract.MeetingsEntry.COLUMN_DATE, mMeetingDate);
        values.put(MeetingsDatabaseContract.MeetingsEntry.COLUMN_START_TIME, mMeetingTime);
        values.put(MeetingsDatabaseContract.MeetingsEntry.COLUMN_MEETING_ORGANISER, "ORGANISER");
        values.put(MeetingsDatabaseContract.MeetingsEntry.COLUMN_ZOOM_MEETING_ID, String.valueOf(mZoomMeetingID));
        values.put(MeetingsDatabaseContract.MeetingsEntry.COLUMN_ZOOM_MEETING_PASSWORD, "password");

        long newRow =  mDb.insert(MeetingsDatabaseContract.MeetingsEntry.TABLE_NAME, null, values);
        Toast.makeText(this, "Meeting created @ " + newRow + " Zoom Meeting Id: " + mZoomMeetingID, Toast.LENGTH_LONG).show();

    }

    public String getLastMeetingID(MeetingsOpenHelper dbHelper){
        mDb = dbHelper.getReadableDatabase();
        String [] projection ={MeetingsDatabaseContract.MeetingsEntry.COLUMN_MEETING_ID};

        Cursor cursor = mDb.query(
                MeetingsDatabaseContract.MeetingsEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToLast();
        return cursor.getString(cursor.getColumnIndex(MeetingsDatabaseContract.MeetingsEntry.COLUMN_MEETING_ID));
    }

    @Override
    public void onZoomSDKInitializeResult(int i, int i1) {

    }

    @Override
    public void onZoomSDKLoginResult(long result) {
        if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            // Once we verify that the request was successful,
            //create a label to indicate zoom is logged in

            Log.d("ZOOM", ">>>>>>>>>>>>loggin successfull<<<<<<<<<<<<<<<<<<<<<<<");


        }else{
            Log.d("ZOOM", ">>>>>>>>>>>>loggin failed<<<<<<<<<<<<<<<<<<<<<<<");
        }
    }

    @Override
    public void onZoomSDKLogoutResult(long l) {

    }

    @Override
    public void onZoomIdentityExpired() {

    }

    @Override
    public void onZoomAuthIdentityExpired() {

    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int i, int i1) {

    }

}




