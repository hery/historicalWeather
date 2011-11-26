package com.daemgahe.historicalWeather;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HistoricalWeatherActivity extends Activity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		
        ((Button) findViewById(R.id.submit1)).setOnClickListener
        (
        		new Button.OnClickListener() 
        		{
        			@Override public void onClick(View arg0) 
        			{
        				// Process data here
        				// setContentView(R.layout.date);
        			}
        		}
        );			
    }
}