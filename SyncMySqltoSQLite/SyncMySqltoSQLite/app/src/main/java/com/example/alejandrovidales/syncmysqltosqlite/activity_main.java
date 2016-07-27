//main screen class where we have all the button to access the different modules

package com.example.alejandrovidales.syncmysqltosqlite;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Calendar;


public class activity_main extends AppCompatActivity {

    private Toolbar myToolbar;
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);

        prgDialog = new ProgressDialog(this);

        prgDialog.setMessage("Transferring Data from Remote MySQL DB and Syncing SQLite. Please wait...");

        prgDialog.setCancelable(false);

        // BroadCase Receiver Intent Object
        Intent alarmIntent = new Intent(getApplicationContext(), SampleBC.class);

        // Pending Intent Object
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Alarm Manager Object
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        // Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, 10 * 1000, pendingIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//     public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    //public void setSupportActionBar(Toolbar myToolbar) {
    //}

    //switchButton.setOnClickListener(new View.OnClickListener() {

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.imageAddAsset:
                Intent intent = new Intent(activity_main.this, newAsset.class);
                startActivity(intent);
        break;
            case R.id.imageButton3:
                Intent intent1 = new Intent(activity_main.this, syncronizeAssets.class);
                startActivity(intent1);
        break;
            case R.id.imageButton:
                Intent intent2 = new Intent(activity_main.this, viewAssets.class);
                startActivity(intent2);
        break;
            case R.id.imageButton2:
                Intent intent3 = new Intent(activity_main.this, transferasset.class);
                startActivity(intent3);
        break;
        }

    }
}