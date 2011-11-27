package com.daemgahe.historicalWeather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class DateScreen extends Activity 
{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date);
        EditText startDate = (EditText) findViewById(R.id.start_date);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.roll(Calendar.YEAR, -1);
        startDate.setText(df.format(c.getTime()));
        EditText endDate = (EditText) findViewById(R.id.end_date);
        endDate.setText(df.format(d));
    }
    

}