package com.example.meetingapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.meetingapp.database.MeetingsDatabaseContract;
import com.example.meetingapp.database.MeetingsOpenHelper;
import com.example.meetingapp.models.Meeting;
import com.example.meetingapp.models.ZoomParticipants;
import com.example.meetingapp.zoom.InitAuthSDKCallback;
import com.example.meetingapp.zoom.InitAuthSDKHelper;
import com.example.meetingapp.zoom.JoinMeetingHelper;
import com.example.meetingapp.zoom.ZoomMeetingUISettingHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import retrofit.RetrofitInstance;
import retrofit.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParams4NormalUser;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;


public class ViewMeetingActivity extends AppCompatActivity implements InitAuthSDKCallback, ZoomSDKAuthenticationListener {

    TextView tvMeetingId;
    TextView tvAgenda;
    TextView tvVenue;
    TextView tvDate;
    TextView tvTime;
    Button btnScan;
    Button btnEditMeeting;
    Button btnViewRegister;
    Button btnRemoveMeeting;
    Button btnStartZoomMeeting;
    Button btnStartTeamsMeeting;
    Button btnJoinZoomMeeting;
    Button btnEndZoomMeeting;
    Button btnAssignAnotherOrganiser;

    private String mMeetingId;
    private String mEmployeeId;
    private String mDate;
    private String mFirstName;
    private String mSurname;
    private String mPost;
    private String mDepartment;
    private String mZoomMeetingID;

    SQLiteDatabase mDb;

    private IntentIntegrator qrScan;
    private String mZoomMeetingPassword;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meeting);

        tvMeetingId = (TextView) findViewById(R.id.tvMeetingId);
        tvAgenda = findViewById(R.id.tvAgenda);
        tvVenue = findViewById(R.id.tvVenue);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        btnScan = findViewById(R.id.btnScan);
        btnEditMeeting = findViewById(R.id.btnEditMeeting);
        btnRemoveMeeting = findViewById(R.id.btnRemoveMeeting);
        btnViewRegister = findViewById(R.id.btnViewRegister);
        btnStartZoomMeeting = findViewById(R.id.btnStartAZoom);
        btnJoinZoomMeeting = findViewById(R.id.btnJoinZoomMeeting);
        btnEndZoomMeeting = findViewById(R.id.btnEndZoomMeeting);
        qrScan = new IntentIntegrator(this);
//        qrScan.initiateScan();

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initiating the qr code scan
                qrScan.initiateScan();
            }
        });

        btnStartZoomMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getZoomMeetingDetails();
                startZoomMeeting(mZoomMeetingID);
            }
        });

        btnJoinZoomMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kunoda meeting id inobva paParcel remeeting ino, nePassword iri pakukokwa #hope
                createjoinDialog();
            }
        });

        btnEndZoomMeeting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        btnEditMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewMeetingActivity.this, "Edit meeting", Toast.LENGTH_LONG).show();
            }
        });

        btnRemoveMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                removeMeeting(new MeetingsOpenHelper(ViewMeetingActivity.this));
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //Do your No progress
                                break;
                        }
                    }
                };
                AlertDialog.Builder ab = new AlertDialog.Builder(ViewMeetingActivity.this);
                ab.setMessage("ARE SURE YOU WANT TO REMOVE THIS MEETING?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });



        btnViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMeetingActivity.this, ViewRegisterActivity.class);
                intent.putExtra("meeting_id", tvMeetingId.getText());
                intent.putExtra("agenda", tvAgenda.getText());
                intent.putExtra("venue", tvVenue.getText());
                intent.putExtra("date", tvDate.getText());
                intent.putExtra("time", tvTime.getText());
                startActivity(intent);
                Toast.makeText(ViewMeetingActivity.this, " Register", Toast.LENGTH_LONG).show();
            }
        });

        initialiseViews();

    }

    private void getZoomMeetingDetails() {
        readMeetings(new MeetingsOpenHelper(this));
    }

    private void joinZoomMeeting(String zoomMeetingNo, String zoomMeetingPassword){
        InitAuthSDKHelper.getInstance().initSDK(this, this);
        //String vanityId = mEdtVanityId.getText().toString().trim();

        if (zoomMeetingNo.length() == 0 && zoomMeetingPassword.length()== 0 ) {
            Toast.makeText(this, "You need to enter a meeting number and Password", Toast.LENGTH_LONG).show();

        }else{
            ZoomSDK zoomSDK = ZoomSDK.getInstance();
            if (zoomSDK.isInitialized()) {
                //                            mCountry = ZoomSDK.getInstance().getAccountService().getAvailableDialInCountry();
                if (zoomSDK.isLoggedIn()) {
                    Log.d("ZOOM", ">>>>>>>>>>>Zoom login sucessful<<<<<<<<<<<<<<<<<<<<<<<");
                    MeetingService meetingService = zoomSDK.getMeetingService();


                    if(meetingService == null) {
                        Log.d("ZOOM", ">>>>>>>>>>>Zoom login sucessful<<<<<<<<<<<<<<<<<<<<<<<");
                    }

                    JoinMeetingOptions opts =ZoomMeetingUISettingHelper.getJoinMeetingOptions();

                    JoinMeetingParams params = new JoinMeetingParams();

                    params.displayName = "DISPLAY_NAME";
                    params.meetingNo = zoomMeetingNo;
                    params.password = zoomMeetingPassword;
                    meetingService.joinMeetingWithParams(this, params,opts);

                }else {
                    createLoginDialog();
                    Log.d("ZOOM", ">>>>>>>>>>>>User not Logged in to Dialog create meeting<<<<<<<<<<<<<<<<<<<<<<<");
                }

            }else {
                Log.d("ZOOM", ">>>>>>>>>>>> SDK Not Initialized<<<<<<<<<<<<<<<<<<<<<<<");
            }
        }
    }





    private void startZoomMeeting(String zoomMeetingID) {
        InitAuthSDKHelper.getInstance().initSDK(this, this);
        //String vanityId = mEdtVanityId.getText().toString().trim();
        createLoginDialog();
        if (zoomMeetingID.length() == 0 ) {
            Toast.makeText(this, "You need to enter a meeting number", Toast.LENGTH_LONG).show();

        }else{
            ZoomSDK zoomSDK = ZoomSDK.getInstance();
            if (zoomSDK.isInitialized()) {
                //                            mCountry = ZoomSDK.getInstance().getAccountService().getAvailableDialInCountry();
                if (zoomSDK.isLoggedIn()) {
                    Log.d("ZOOM", ">>>>>>>>>>>Zoom login sucessful<<<<<<<<<<<<<<<<<<<<<<<");
                    MeetingService meetingService = zoomSDK.getMeetingService();


                    if(meetingService == null) {
                        Log.d("ZOOM", ">>>>>>>>>>>Zoom login sucessful<<<<<<<<<<<<<<<<<<<<<<<");
                    }

                    StartMeetingOptions opts = ZoomMeetingUISettingHelper.getStartMeetingOptions();
                    opts.no_video=false;

                    StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
                    params.meetingNo = zoomMeetingID;
                     meetingService.startMeetingWithParams(this, params, opts);

                }else {

                    Log.d("ZOOM", ">>>>>>>>>>>>User not Logged in to Dialog create meeting<<<<<<<<<<<<<<<<<<<<<<<");
                }

            }else {
                Log.d("ZOOM", ">>>>>>>>>>>> SDK Not Initialized<<<<<<<<<<<<<<<<<<<<<<<");
            }
        }
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

    private void createjoinDialog() {
        new AlertDialog.Builder(this).setView(R.layout.dialog_join).setPositiveButton("JOIN MEETING ", new DialogInterface.OnClickListener() {@Override
        public void onClick(DialogInterface dialogInterface, int i) {
            AlertDialog dialog = (AlertDialog) dialogInterface;
            TextInputEditText meetingIDInput = dialog.findViewById(R.id.join_input);
            TextInputEditText passwordInput = dialog.findViewById(R.id.pw_input);
            if (meetingIDInput != null && meetingIDInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                String meetingID = meetingIDInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (meetingID.trim().length() > 0 && password.trim().length() > 0) {
                    joinZoomMeeting(meetingID, password);
                }
            }
            dialog.dismiss();
        }
        }).show();
    }

    public void endZoomMeeting(){
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<List<ZoomParticipants>> listCall = retrofitInterface.getZoomParticipants();
        listCall.enqueue(new Callback<List<ZoomParticipants>>() {
            @Override
            public void onResponse(Call<List<ZoomParticipants>> call, Response<List<ZoomParticipants>> response) {
//                parseData(response.body());

            }

            @Override
            public void onFailure(Call<List<ZoomParticipants>> call, Throwable t) {

            }
        });
    }
    private void initialiseViews() {
        Intent intent = getIntent();
        Meeting meeting = intent.getParcelableExtra("meeting");
        tvMeetingId.setText(meeting.meetingId);
        tvAgenda.setText(meeting.agenda);
        tvVenue.setText(meeting.venue);
        tvDate.setText(meeting.meetingDate);
        tvTime.setText(meeting.meetingTime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject employeeJson = new JSONObject(result.getContents());
                    //setting values to textviews  tvScanning.setText(result.getContents()); textViewAddress.setText(obj.getString("address"));
                    mMeetingId = tvMeetingId.getText().toString();
                    mEmployeeId = employeeJson.getString("id");
                    mFirstName = employeeJson.getString("name");
                    mSurname = employeeJson.getString("surname");
                    mPost = employeeJson.getString("post");
                    mDepartment = employeeJson.getString("Department");
                    mDate = tvDate.getText().toString();

                    insertRegisterEntries(new MeetingsOpenHelper(this));

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here that means the encoded format not matches in this case you can display whatever data is available on the qrcode to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void readMeetings(com.example.meetingapp.database.MeetingsOpenHelper dbOpenHelper){
        Date d = new Date();

        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        final String [] meetingColumns = {
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_MEETING_ID,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_AGENDA,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_VENUE,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_DATE,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_START_TIME,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_ZOOM_MEETING_ID,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_ZOOM_MEETING_PASSWORD};

        String selection = MeetingsDatabaseContract.MeetingsEntry.COLUMN_MEETING_ID + " = ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = {(String) tvMeetingId.getText().toString()};

        final Cursor meetingsCursor = db.query(MeetingsDatabaseContract.MeetingsEntry.TABLE_NAME,
                meetingColumns,
                selection, selectionArgs, null, null, null);

        loadMeetingsFromDatabase(meetingsCursor);

    }

    private void loadMeetingsFromDatabase(Cursor cursor) {
        int meetingIdPos = cursor.getColumnIndex(MeetingsDatabaseContract.MeetingsEntry.COLUMN_MEETING_ID);
        int meetingAgendaPos = cursor.getColumnIndex(MeetingsDatabaseContract.MeetingsEntry.COLUMN_AGENDA);
        int meetingVenuePos = cursor.getColumnIndex(MeetingsDatabaseContract.MeetingsEntry.COLUMN_VENUE);
        int meetingDatePos = cursor.getColumnIndex(MeetingsDatabaseContract.MeetingsEntry.COLUMN_DATE);
        int meetingTimePos = cursor.getColumnIndex(MeetingsDatabaseContract.MeetingsEntry.COLUMN_START_TIME);
        int zoomMeetingIdPos = cursor.getColumnIndex(MeetingsDatabaseContract.MeetingsEntry.COLUMN_ZOOM_MEETING_ID);
        int zoomMeetingPasswordPos = cursor.getColumnIndex(MeetingsDatabaseContract.MeetingsEntry.COLUMN_ZOOM_MEETING_PASSWORD);

        while(cursor.moveToNext()) {
            mZoomMeetingID = cursor.getString(zoomMeetingIdPos);
            mZoomMeetingPassword = cursor.getString(zoomMeetingPasswordPos);
        }
        cursor.close();

    }

    private void insertRegisterEntries(MeetingsOpenHelper dbHelper) {
        mDb = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MeetingsDatabaseContract.RegisterEntry.COLUMN_MEETING_ID, mMeetingId);
        values.put(MeetingsDatabaseContract.RegisterEntry.COLUMN_EMPLOYEE_ID, mEmployeeId);
        values.put(MeetingsDatabaseContract.RegisterEntry.COLUMN_DATE, mDate);
        values.put(MeetingsDatabaseContract.RegisterEntry.COLUMN_FIRST_NAME, mFirstName);
        values.put(MeetingsDatabaseContract.RegisterEntry.COLUMN_SURNAME, mSurname);
        values.put(MeetingsDatabaseContract.RegisterEntry.COLUMN_POST, mPost);
        values.put(MeetingsDatabaseContract.RegisterEntry.COLUMN_DEPARTMENT, mDepartment);


        long newRow =  mDb.insert(MeetingsDatabaseContract.RegisterEntry.TABLE_NAME, null, values);
        Toast.makeText(this, "Register Entry " + newRow, Toast.LENGTH_LONG).show();
    }

    private void removeMeeting(MeetingsOpenHelper dbHelper){
        mDb = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = MeetingsDatabaseContract.MeetingsEntry.COLUMN_MEETING_ID + " LIKE ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = {(String) tvMeetingId.getText()};
// Issue SQL statement.
        int deletedRows = mDb.delete(MeetingsDatabaseContract.MeetingsEntry.TABLE_NAME, selection, selectionArgs);
        Toast.makeText(this, deletedRows +"Meeting(s) deleted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onZoomSDKInitializeResult(int i, int i1) {

    }

    @Override
    public void onZoomSDKLoginResult(long l) {

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

}

