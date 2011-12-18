package com.daemgahe.historicalWeather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.daemgahe.historicalWeather.HistoricalWeatherActivity.BackgroundAsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class DateScreen extends Activity 
{
	private Button submitButton;
	private String theIcao;
	//private Button submit;
	private TextView debug;
	//private String myURL;
	
	private DatePicker startDateField;
	private DatePicker endDateField;
	
	private Calendar calendar;
	private Date startDate;
	private Date endDate;
	
	private String startyear;
	private String startmonth;
	private String startday;
	
	private String endyear;
	private String endmonth;
	private String endday;
	
	private String completeURL;
	private String graphData;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date);
        
        startDateField = (DatePicker) findViewById(R.id.start_date);
        endDate = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.roll(Calendar.YEAR, -1);
        startDate = calendar.getTime();
        endDateField = (DatePicker) findViewById(R.id.end_date);
        
        calendar.setTime(startDate);
        startyear = String.format("%04d", calendar.get(Calendar.YEAR));
        startmonth = String.format("%02d", calendar.get(Calendar.MONTH)+1);
        startday = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        startDateField.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
        calendar.setTime(endDate);
        endyear = String.format("%04d", calendar.get(Calendar.YEAR));
        endmonth = String.format("%02d", calendar.get(Calendar.MONTH)+1);
        endday = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));        
        endDateField.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);        
        
        // Create a bundle to get the intent parameters
        Bundle bundle = this.getIntent().getExtras();
        theIcao = bundle.getString("icao");
        
        debug = (TextView) findViewById(R.id.debugTextView);
        submitButton = (Button) findViewById(R.id.submitButton);
        // Button dataButton = (Button) findViewById(R.id.dataButton);
       
        submitButton.setOnClickListener
        (new View.OnClickListener() 
        {	
        	@Override
			public void onClick(View v) 
        	{
                calendar.set(startDateField.getYear(), startDateField.getMonth(), startDateField.getDayOfMonth());
                startDate = calendar.getTime();
                startyear = String.format("%04d", calendar.get(Calendar.YEAR));
                startmonth = String.format("%02d", calendar.get(Calendar.MONTH)+1);
                startday = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
                calendar.set(endDateField.getYear(), endDateField.getMonth(), endDateField.getDayOfMonth());
                endDate = calendar.getTime();
                endyear = String.format("%04d", calendar.get(Calendar.YEAR));
                endmonth = String.format("%02d", calendar.get(Calendar.MONTH)+1);
                endday = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
                
                String host = "http://www.wunderground.com/history/airport/";
                String file = "CustomHistory.html?";
                String urlEnd = "&req_city=NA&req_state=NA&req_statename=NA&format=1";
                completeURL = String.format("%s%s/%s/%s/%s/%sdayend=%s&monthend=%s&yearend=%s%s",host,theIcao,startyear,startmonth,startday,file,endday,endmonth,endyear,urlEnd);
        		//debug.setText(completeURL);
                
        		try {
        			
    				new BackgroundAsyncTask().execute(completeURL);
    				submitButton.setClickable(false);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        });
        
    }
    
    protected void GoToGraphScreen()
    {
    	Bundle bundle = new Bundle();
    	bundle.putString("graph", graphData);
    	bundle.putLong("startDate", startDate.getTime());
    	bundle.putLong("endDate", endDate.getTime());
    	Intent dateToGraph = new Intent(this, GraphScreen.class);
    	dateToGraph.putExtras(bundle);
    	startActivityForResult(dateToGraph,0);
    }
    
    public void getData() throws Exception
    {
        // Prepare different parts of the URL
        String host = "http://www.wunderground.com/history/airport/";
        String file = "CustomHistory.html?";
        String urlEnd = "&req_city=NA&req_state=NA&req_statename=NA&format=1";
        
        // Form URL
        completeURL = String.format("%s%s/%s/%s/%s/%sdayend=%s&monthend=%s&yearend=%s%s",host,theIcao,startyear,startmonth,startday,file,endday,endmonth,endyear,urlEnd);
        debug.setText(completeURL);
        
    	BufferedReader in = null;
    	try
    	{
    		HttpClient client = new DefaultHttpClient ();
    		HttpGet request = new HttpGet();
    		request.setURI(new URI(completeURL));
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
			graphData = new String(sb.toString());
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
    
    public class BackgroundAsyncTask extends AsyncTask<String, Integer, String>
    {
    	
    	private Toast ourToast;
    	
    	@Override
    	protected void onPreExecute()
    	{
 		   this.ourToast = Toast.makeText(DateScreen.this,
			         "Parsing CVS file, please wait...", Toast.LENGTH_LONG);		   
		   ourToast.show();
    	}
    	
    	@Override
    	protected void onPostExecute(String result)
    	{	
    		GoToGraphScreen(); 
    		ourToast.cancel();
    	}
	
    	@Override
    	protected String doInBackground(String... theURL)
    	{
    		BufferedReader in = null;
        	try
        	{
        		HttpClient client = new DefaultHttpClient ();
        		HttpGet request = new HttpGet();
        		request.setURI(new URI(theURL[0]));
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
    			graphData = new String(sb.toString());
        	} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			return graphData;    		
    	}    	

    }
}

