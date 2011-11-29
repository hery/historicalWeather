package com.daemgahe.historicalWeather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HistoricalWeatherActivity extends Activity 
{
	// doing some modifications...
	private Button submitZipCodeButton;
	private EditText ZipCodeField;
	private static final String DEVKEY = "bf90362e52a6012e";
	private String zipCode;
	private String URL;
	private TextView testString;
	private String jsonOutput;
	
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
        			Log.v("Weather Graph", "Inside invalid zip try block");
        			URL = "Invalid zipcode length.";
        			testString.setText(URL);
        		} else
        		{
        		URL = String.format("%s%s%s%s%s", "http://api.wunderground.com/api/", DEVKEY,"/geolookup/q/", zipCode,".json");
        		try {
        			Log.v("Weather Graph", "Inside successful zip try block");
					getJson();
					testString.setText(jsonOutput);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.v("Weather Graph", "Inside successful zip catch block");
					e.printStackTrace();
				}
        		// Fetch data from server
        		// Parse data 10       		
        		// GoToDateScreen();  
        		}

        	}
        }
        );  
    } 
    
    protected void GoToDateScreen()
    {
    	Intent i = new Intent(this, DateScreen.class);
    	startActivity(i);
    }
    
    public void getJson() throws Exception
    {
    	BufferedReader in = null;
    	try
    	{
    		HttpClient client = new DefaultHttpClient ();
    		HttpGet request = new HttpGet();
    		request.setURI(new URI(URL));
    		HttpResponse response = client.execute(request);
    		in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null)
			{
				sb.append(line + NL);
				Log.v("Weather Graph", line+NL);
			}
			in.close();
			jsonOutput = new String(sb.toString());
    	} finally 
    	{
    		if (in != null) 
    		{
    			try
    			{
    				in.close();
    			} catch (IOException e)
    			{
    				e.printStackTrace();
    			}
    		}
    	} 
    }
     
}