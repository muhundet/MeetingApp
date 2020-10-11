package com.example.meetingapp.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.geodatacollector.database.GeoDataBaseHelper;
import com.example.geodatacollector.database.GeoDatabaseContract;
import com.example.geodatacollector.ui.collect_geo_data.CollectGeoDataFragment;
import com.example.meetingapp.database.MeetingsDatabaseContract;
import com.example.meetingapp.database.MeetingsOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;




public class NetworkStateChecker extends BroadcastReceiver {

    private Context context;
    private MeetingsOpenHelper db;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new MeetingsOpenHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced data
                Cursor cursor = db.getUnsyncedGeoData();
                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced data to MySQL
                        saveGeoData(
                                cursor.getInt(cursor.getColumnIndex(MeetingsDatabaseContract.MeetingsEntry._ID)),
                                cursor.getString(cursor.getColumnIndex(GeoDatabaseContract.GeoDataEntry.COLUMN_IMAGE)),
                                cursor.getString(cursor.getColumnIndex(GeoDatabaseContract.GeoDataEntry.COLUMN_DATE)),
                                cursor.getString(cursor.getColumnIndex(GeoDatabaseContract.GeoDataEntry.COLUMN_TIME)),
                                cursor.getDouble(cursor.getColumnIndex(GeoDatabaseContract.GeoDataEntry.COLUMN_LATITUDE)),
                                cursor.getDouble(cursor.getColumnIndex(GeoDatabaseContract.GeoDataEntry.COLUMN_LONGITUDE)),
                                cursor.getDouble(cursor.getColumnIndex(GeoDatabaseContract.GeoDataEntry.COLUMN_ALTITUDE)),
                                cursor.getInt(cursor.getColumnIndex(GeoDatabaseContract.GeoDataEntry.COLUMN_STATUS))
                        );
                    } while (cursor.moveToNext());
                }
            }
        }
    }


    private void saveGeoData(final int id, final String image, final String date, final String time, final double latitude, final double longitude, final double altitude, int status) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CollectGeoDataFragment.URL_SAVE_GEO_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateGeoStatus(id, CollectGeoDataFragment.GEO_DATA_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(CollectGeoDataFragment.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("image", image);
                params.put("date", date);
                params.put("time", time);
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("altitude", String.valueOf(altitude));

                return params;
            }
        };

        VolleyUtil.getInstance(context).addToRequestQueue(stringRequest);
    }

}
