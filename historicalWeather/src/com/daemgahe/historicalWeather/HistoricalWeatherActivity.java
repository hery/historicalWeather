package com.daemgahe.historicalWeather;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
	private EditText zipCodeField;
	private static final String DEVKEY = "bf90362e52a6012e";
	private String savedZipCode;
	private String zipCode;
	private String URL;
	private TextView testString;
	private String savedIcao;
	private String icao;
	private String jsonOutput;
	private ByteArrayOutputStream outputStream;
	private String outputStreamString;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        savedZipCode = settings.getString("zipCode", "");
        savedIcao = settings.getString("icao", "");

    
        submitZipCodeButton = (Button) findViewById(R.id.submit1);
        zipCodeField = (EditText) findViewById(R.id.zipcodefield);
        zipCodeField.setText(savedZipCode);
        testString = (TextView) findViewById(R.id.testString);
        
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        myProgressBar.setProgress(0);
      
        
/**         submitZipCodeButton.setOnClickListener
        (new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				zipCode = ZipCodeField.getText().toString();
				if (zipCode.length() != 5)
        		{
        			Log.v("Weather Graph", "Inside invalid zip try block");
        			URL = "Invalid zipcode length.";
        			testString.setText(URL);
        		} else
        		{
				new BackgroundAsyncTask().execute();
				submitZipCodeButton.setClickable(false);
        		}
			}
		}); 	*/
        
        submitZipCodeButton.setOnClickListener
        (new View.OnClickListener() 
        {
        	@Override
        	public void onClick(View v) 
        	{
        		
        		zipCode = zipCodeField.getText().toString();
        		
        		if (zipCode.length() != 5)
        		{
        			Log.v("Weather Graph", "Inside invalid zip try block");
        			URL = "Invalid zipcode length.";
        			testString.setText(URL);
        		} else
        		{
        			if (!zipCode.equals(savedZipCode))
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
							icao = stationsArray.getJSONObject(0).getString("icao").toString();
							//myString = locationObject.getString("tz_long");	// debug ok
							testString.setText(icao);		// debug only
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							icao = "Error.";
							testString.setText(icao);
						}
        			}
        			else
        			{
        				icao = savedIcao;
        			}
	        		
					// the value of icao is passed with the intent, in GoToDateScreen() 
					GoToDateScreen();  
	        	}
        	}
        }
        );   
    }  
    
    protected void GoToDateScreen()
    {
    	SharedPreferences settings = getPreferences(MODE_PRIVATE);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putString("zipCode", zipCode);
    	editor.putString("icao", icao);
    	editor.commit();
    	
    	Bundle bundle = new Bundle();
    	bundle.putString("icao", icao);
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
    
    public class BackgroundAsyncTask extends AsyncTask<Void, Integer, String>
    {
    	int myProgress;
    	
    	@Override
    	protected void onPostExecute(String result)
    	{	// Parse Json file
    		// Declare and initialize JSONObject 
/**    		JSONObject jObject = null;
    		
    		try {
    			// Create JSONObject from the JSON file string resulting from the zipcode get request
				jObject = new JSONObject(outputStreamString);
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
    		 GoToDateScreen();   */
    		testString.setText("OutputStreamString: " + outputStreamString);
    	}
    	
    	@Override
    	protected void onPreExecute()
    	{
    	//	   Toast.makeText(HistoricalWeatherActivity.this,
    	//		         "onPreExecute", Toast.LENGTH_LONG).show();
    			   myProgress = 0;
    	}
    	
    	@Override
    	protected String doInBackground(Void... params)
    	{	//	get Json file
    		int count;
    		try {
    		URL url = new URL(URL);
    		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    		int length = connection.getContentLength();
    		
    		InputStream input = new BufferedInputStream(url.openStream());
    		OutputStream outputStream = new ByteArrayOutputStream();
    		
    		byte data[] = new byte[1024];
    		long total = 0;
    		
    		while((count = input.read(data)) != -1)
    		{
    			total += count;
    			publishProgress((int)(total*100/length));
    			outputStream.write(data, 0, count);
    		}
    		
    		outputStream.flush();
    		outputStreamString = outputStream.toString();
    		outputStream.close();
    		
    		input.close();
    		} catch (Exception e) {}
			return outputStreamString;
    	}
    	
    	@Override
    	protected void onProgressUpdate(Integer... values)
    	{
    		myProgressBar.setProgress(values[0]);
    	}
    	

    } 
     
}