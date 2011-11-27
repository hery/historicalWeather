package com.daemgahe.historicalWeather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HistoricalWeatherActivity extends Activity 
{
	
	private Button submitZipCodeButton;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        submitZipCodeButton = (Button) findViewById(R.id.submit1);
        submitZipCodeButton.setOnClickListener
        (new View.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		GoToDateScreen();        		
        	}
        }
        );  
    }
    
    protected void GoToDateScreen()
    {
    	Intent i = new Intent(this, DateScreen.class);
    	startActivity(i);
    }
}