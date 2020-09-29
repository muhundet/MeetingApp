package com.example.meetingapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.meetingapp.database.MeetingsDatabaseContract;
import com.example.meetingapp.database.MeetingsOpenHelper;
import com.example.meetingapp.util.PDFUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ViewRegisterActivity extends AppCompatActivity implements PDFUtil.PDFUtilListener {
    private TableLayout tableLayout;
    View registerView;
    private String filePath = "generatedPDF.pdf";
    List<View> viewsToPDF = new ArrayList<>();

    String meeting_id;
    TextView meetingid;
    TextView tvAgenda;
    TextView tvVenue;
    TextView tvDate;
    TextView tvTime;
    Button btnGenerateRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_register);
        tableLayout = findViewById(R.id.tableLayout);
        Intent intent = getIntent();
        meeting_id = intent.getStringExtra("meeting_id");

        meetingid = findViewById(R.id.tvMeetingId);
        tvAgenda = findViewById(R.id.tvAgenda);
        tvVenue = findViewById(R.id.tvVenue);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        btnGenerateRegister = findViewById(R.id.btnGenerateRegister);

        meetingid.setText(intent.getStringExtra("meeting_id"));
        tvAgenda.setText(intent.getStringExtra("agenda"));
        tvVenue.setText(intent.getStringExtra("venue"));
        tvDate.setText(intent.getStringExtra("date"));
        tvTime.setText(intent.getStringExtra("time"));

        readRegister(new MeetingsOpenHelper(this));

        btnGenerateRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePDF();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_register_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_send){
            generatePDF();
        }
        return super.onOptionsItemSelected(item);
    }

    private void generatePDF() {
//        View v1 = registerView.getRootView();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View v1 = inflater.inflate(R.layout.activity_view_register, null, false);

        Intent intent = getIntent();
        meeting_id = intent.getStringExtra("meeting_id");

        meetingid = v1.findViewById(R.id.tvMeetingId);
        tvAgenda = v1.findViewById(R.id.tvAgenda);
        tvVenue = v1.findViewById(R.id.tvVenue);
        tvDate = v1.findViewById(R.id.tvDate);
        tvTime = v1.findViewById(R.id.tvTime);
        tableLayout = v1.findViewById(R.id.tableLayout);

        meetingid.setText(intent.getStringExtra("meeting_id"));
        tvAgenda.setText(intent.getStringExtra("agenda"));
        tvVenue.setText(intent.getStringExtra("venue"));
        tvDate.setText(intent.getStringExtra("date"));
        tvTime.setText(intent.getStringExtra("time"));


        readRegister(new MeetingsOpenHelper(this));

        viewsToPDF.add(v1);
        PDFUtil.getInstance().generatePDF(viewsToPDF,filePath, this);
    }


    public  void readRegister(MeetingsOpenHelper dbOpenHelper){
        Date d = new Date();

        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        final String [] registerColumns = {
                MeetingsDatabaseContract.RegisterEntry.COLUMN_MEETING_ID,
                MeetingsDatabaseContract.RegisterEntry.COLUMN_EMPLOYEE_ID,
                MeetingsDatabaseContract.RegisterEntry.COLUMN_DATE,
                MeetingsDatabaseContract.RegisterEntry.COLUMN_FIRST_NAME,
                MeetingsDatabaseContract.RegisterEntry.COLUMN_SURNAME,
                MeetingsDatabaseContract.RegisterEntry.COLUMN_POST,
                MeetingsDatabaseContract.RegisterEntry.COLUMN_DEPARTMENT};

        String selection = MeetingsDatabaseContract.RegisterEntry.COLUMN_MEETING_ID + " = ?";
        String[] selectionArgs = { meeting_id };

        final Cursor registerCursor = db.query(MeetingsDatabaseContract.RegisterEntry.TABLE_NAME,
                registerColumns,
                selection, selectionArgs, null, null, null);
        makeTableHeader();
        loadRegisterFromDatabase(registerCursor);

    }

    private void makeTableHeader() {
        View tableRowHeader = LayoutInflater.from(this).inflate(R.layout.table_item, null, false);
        TextView _Id = (TextView) tableRowHeader.findViewById(R.id._id);
        TextView name = (TextView) tableRowHeader.findViewById(R.id.name);
        TextView surname = (TextView) tableRowHeader.findViewById(R.id.surname);
        TextView department = (TextView) tableRowHeader.findViewById(R.id.department);
        TextView post = (TextView) tableRowHeader.findViewById(R.id.post);

        _Id.setText("MINE #");
        name.setText("NAME");
        surname.setText("SURNAME");
        department.setText("DEPARTMENT");
        post.setText("SECTION");

        tableLayout.addView(tableRowHeader);
    }

    void loadRegisterFromDatabase(Cursor cursor) {
        int meetingIdPos = cursor.getColumnIndex(MeetingsDatabaseContract.RegisterEntry.COLUMN_MEETING_ID);
        int employeeIdPos = cursor.getColumnIndex(MeetingsDatabaseContract.RegisterEntry.COLUMN_EMPLOYEE_ID);
        int registerDatePos = cursor.getColumnIndex(MeetingsDatabaseContract.RegisterEntry.COLUMN_DATE);
        int firstNamePos = cursor.getColumnIndex(MeetingsDatabaseContract.RegisterEntry.COLUMN_FIRST_NAME);
        int surnamePos = cursor.getColumnIndex(MeetingsDatabaseContract.RegisterEntry.COLUMN_SURNAME);
        int postPos = cursor.getColumnIndex(MeetingsDatabaseContract.RegisterEntry.COLUMN_POST);
        int departmentPos = cursor.getColumnIndex(MeetingsDatabaseContract.RegisterEntry.COLUMN_DEPARTMENT);



        while(cursor.moveToNext()){
            String meetingId = cursor.getString(meetingIdPos);
            String employeeId = cursor.getString(employeeIdPos);
            String registerDate = cursor.getString(registerDatePos);
            String registerfirstName = cursor.getString(firstNamePos);
            String registersurname = cursor.getString(surnamePos);
            String registerpost = cursor.getString(postPos);
            String registerdepartment = cursor.getString(departmentPos);
//            if(meeting_id == meetingId) {
            View tableRow = LayoutInflater.from(this).inflate(R.layout.table_item, null, false);
            TextView _Id = (TextView) tableRow.findViewById(R.id._id);
            TextView name = (TextView) tableRow.findViewById(R.id.name);
            TextView surname = (TextView) tableRow.findViewById(R.id.surname);
            TextView department = (TextView) tableRow.findViewById(R.id.department);
            TextView post = (TextView) tableRow.findViewById(R.id.post);

            _Id.setText(employeeId);
            name.setText(registerfirstName);
            surname.setText(registersurname);
            department.setText(registerdepartment);
            post.setText(registerpost);

            tableLayout.addView(tableRow);
//            }

        }
        cursor.close();
    }

    @Override
    public void pdfGenerationSuccess(File savedPDFFile) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, "muhundetristone@gmail.com");
        email.putExtra(Intent.EXTRA_SUBJECT, tvAgenda.getText().toString() + "held on" + tvDate.getText().toString() + "at" + tvTime.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, "PFA");
        Uri uri = Uri.fromFile(savedPDFFile);
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("application/pdf");
        email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(email);
        Toast.makeText(this, "PDF Generated Successifully " , Toast.LENGTH_LONG).show();
    }

    @Override
    public void pdfGenerationFailure(Exception exception) {
        Toast.makeText(this, "PDF Generation failure ", Toast.LENGTH_LONG).show();

    }
}

