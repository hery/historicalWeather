package com.daemgahe.historicalWeather;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HistoricalWeatherActivity extends Activity 
{
	
	private Button submitZipCodeButton;
	private EditText ZipCodeField;
	private static final String DEVKEY = "bf90362e52a6012e";
	private String zipCode;
	private String URL;
	private TextView testString;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        submitZipCodeButton = (Button) findViewById(R.id.submit1);
        ZipCodeField = (EditText) findViewById(R.id.zipcodefield);
        testString = (TextView) findViewById(R.id.testString);
        
        submitZipCodeButton.setOnClickListener
        (new View.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		zipCode = ZipCodeField.getText().toString();
        		
        		if (zipCode.length() != 5)
        		{
        			URL = "Invalid zipcode length.";
        		} else
        		{
        		URL = String.format("%s%s%s%s%s", "http://api.wunderground.com/api/", DEVKEY,"/geolookup/q/", zipCode,".json");
        		}
        		testString.setText(URL);
        		// Fetch data from server
        		// Parse data        		
        		// GoToDateScreen();        		
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