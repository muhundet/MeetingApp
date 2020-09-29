package com.example.meetingapp.database;

import android.provider.BaseColumns;

public final class MeetingsDatabaseContract {
    private MeetingsDatabaseContract(){}

    public static final class MeetingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "meetings_entry";
        public static final String COLUMN_MEETING_ID = "meeting_id";
        public static final String COLUMN_AGENDA = "agenda";
        public static final String COLUMN_VENUE = "venue";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MEETING_ORGANISER = "meeting_organiser";
        public static final String COLUMN_ZOOM_MEETING_ID = "zoom_meeting_id";
        public static final String COLUMN_ZOOM_MEETING_PASSWORD = "zoom_password";
        public static final String COLUMN_TEAMS_MEETING_ID = "teams_meeting_id";
        public static final String COLUMN_TEAMS_MEETING_PASSWORD = "teams_password";
        public static final String COLUMN_STATUS = "status";


        public static final String SQL_CREATE_TABLE = "CREATE TABLE " +  TABLE_NAME + " (" +
                COLUMN_MEETING_ID + " " +
                COLUMN_AGENDA + " TEXT NOT NULL, " +
                COLUMN_AGENDA + " TEXT NOT NULL, " +
                COLUMN_VENUE + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_START_TIME + " TEXT NOT NULL, " +
                COLUMN_MEETING_ORGANISER + " TEXT NOT NULL, " +
                COLUMN_ZOOM_MEETING_ID + ", " +
                COLUMN_ZOOM_MEETING_PASSWORD + ", " +
                COLUMN_TEAMS_MEETING_ID + ", " +
                COLUMN_TEAMS_MEETING_PASSWORD + ", " +
                COLUMN_STATUS + ")";

    }

    public static final class EmployeesEntry implements BaseColumns{
        public static final String TABLE_NAME = "employees_entry";
        public static final String COLUMN_EMPLOYEE_ID = "employee_id";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_SURNAME = "surname";
        public static final String COLUMN_POST = "post";
        public static final String COLUMN_NATIONAL_ID = "national_ID";
        public static final String COLUMN_DEPARTMENT_ID = "department_id";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_EXPIRY_DATE = "expiry_date";
        public static final String COLUMN_STATUS = "status";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_EMPLOYEE_ID + " TEXT UNIQUE NOT NULL, " +
                COLUMN_FIRST_NAME + " TEXT NOT NULL, "+
                COLUMN_SURNAME + " TEXT NOT NULL, " +
                COLUMN_NATIONAL_ID + " TEXT NOT NULL, " +
                COLUMN_POST + " TEXT NOT NULL, " +
                COLUMN_DEPARTMENT_ID + " TEXT NOT NULL, " +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_EXPIRY_DATE + " TEXT NOT NULL, " +
                COLUMN_STATUS + ")";

    }

    public static final class RegisterEntry implements BaseColumns{
        public static final String TABLE_NAME = "register_entry";
        public static final String COLUMN_MEETING_ID = "meeting_id";
        public static final String COLUMN_EMPLOYEE_ID = "employee_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_SURNAME = "surname";
        public static final String COLUMN_POST = "post";
        public static final String COLUMN_DEPARTMENT = "department";
        public static final String COLUMN_STATUS = "status";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_MEETING_ID + " TEXT NOT NULL, " +
                COLUMN_EMPLOYEE_ID + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_FIRST_NAME + " TEXT NOT NULL, "+
                COLUMN_SURNAME + " TEXT NOT NULL, " +
                COLUMN_POST + " TEXT NOT NULL, " +
                COLUMN_DEPARTMENT + " TEXT NOT NULL, " +
                COLUMN_STATUS + ")";

    }

    public static final class AttendanceEntry implements BaseColumns{
        public static final String TABLE_NAME = "attendance_entry";
        public static final String COLUMN_MEETING_ID = "meeting_id";
        public static final String COLUMN_EMPLOYEE_ID = "employee_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_CRITERIA = "criteria";
        public static final String COLUMN_STATUS = "status";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_MEETING_ID + " TEXT NOT NULL, " +
                COLUMN_EMPLOYEE_ID + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TIME + " TEXT NOT NULL, "+
                COLUMN_CRITERIA + " TEXT NOT NULL, " +
                COLUMN_STATUS + ")";

    }
}
