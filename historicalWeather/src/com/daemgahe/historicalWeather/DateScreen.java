package com.daemgahe.historicalWeather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DateScreen extends Activity 
{
	
	private String theIcao;
	private Button submit;
	private TextView debug;
	
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
        
        Bundle bundle = this.getIntent().getExtras();
        theIcao = bundle.getString("icao");
        
        // debug = (TextView) findViewById(R.id.debugTextView);
        Button submit = (Button) findViewById(R.id.submitButton);
       
        submit.setOnClickListener
        (new View.OnClickListener() 
        {	
        	public void onClick(View v) 
        	{
        		// debug.setText(theIcao);
        	}
        });
    }
}