package com.daemgahe.historicalWeather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HistoricalWeatherActivity extends Activity 
{
	private Button submitZipCodeButton;
	private ProgressBar myProgressBar;
	private EditText ZipCodeField;
	private static final String DEVKEY = "bf90362e52a6012e";
	private String zipCode;
	private String URL;
	private TextView testString;
	private String myIcao;	// debug string
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
        
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        myProgressBar.setProgress(0);
      
        
        /** submitZipCodeButton.setOnClickListener
        (new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new BackgroundAsyncTask().execute();
				submitZipCodeButton.setClickable(false);
			}
		}); */
        
        submitZipCodeButton.setOnClickListener
        (new View.OnClickListener() 
        {
        	@Override
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
					// testString.setText(jsonOutput); // debug line to show get request is working
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.v("Weather Graph", "Inside successful zip catch block");
					e.printStackTrace();
					testString.setText("Error.");
				}
        		
        		// Declare and initialize JSONObject 
        		JSONObject jObject = null;
        		
        		try {
        			// Create JSONObject from the JSON file string resulting from the zipcode get request
					jObject = new JSONObject(jsonOutput);
					JSONObject locationObject = jObject.getJSONObject("location");	// debug ok
					JSONObject nearbyStationsObject = locationObject.getJSONObject("nearby_weather_stations");
					JSONObject airportObject = nearbyStationsObject.getJSONObject("airport"); 
					JSONArray stationsArray = airportObject.getJSONArray("station");
					myIcao = stationsArray.getJSONObject(0).getString("icao").toString();
					//myString = locationObject.getString("tz_long");	// debug ok
					testString.setText(myIcao);		// debug only
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					myIcao = "Error.";
					testString.setText(myIcao);
				}       		    		
        		 // the value of myIcao is passed with the intent, in GoToDateScreen() 
        		 GoToDateScreen();  
        		}
        	}
        }
        );  
    } 
    
    protected void GoToDateScreen()
    {
    	Bundle bundle = new Bundle();
    	bundle.putString("icao", myIcao);
    	Intent zipcodeToDate = new Intent(this, DateScreen.class);
    	zipcodeToDate.putExtras(bundle);
    	startActivityForResult(zipcodeToDate,0);
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
    
    public class BackgroundAsyncTask extends AsyncTask<Void, Integer, Void>
    {
    	int myProgress;
    	
    	@Override
    	protected void onPostExecute(Void result)
    	{
    		   Toast.makeText(HistoricalWeatherActivity.this,
    			         "onPostExecute", Toast.LENGTH_LONG).show();
    			   submitZipCodeButton.setClickable(true);
    	}
    	
    	@Override
    	protected void onPreExecute()
    	{
    	//	   Toast.makeText(HistoricalWeatherActivity.this,
    	//		         "onPreExecute", Toast.LENGTH_LONG).show();
    			   myProgress = 0;
    	}
    	
    	@Override
    	protected Void doInBackground(Void... params)
    	{
    		while(myProgress<100)
    		{
    			myProgress++;
    			publishProgress(myProgress);
    			SystemClock.sleep(10);
    		}
    		return null;
    	}
    	
    	@Override
    	protected void onProgressUpdate(Integer... values)
    	{
    		myProgressBar.setProgress(values[0]);
    	}
    } 
     
}