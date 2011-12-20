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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
	
	private boolean conn;
	private Toast connToast;
	private CheckConnectivity check;
	
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
        
        check = new CheckConnectivity();
       
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
                Calendar cal2 = (Calendar) calendar.clone();
                calendar.set(endDateField.getYear(), endDateField.getMonth(), endDateField.getDayOfMonth());
                endDate = calendar.getTime();
                endyear = String.format("%04d", calendar.get(Calendar.YEAR));
                endmonth = String.format("%02d", calendar.get(Calendar.MONTH)+1);
                endday = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
                if (cal2.compareTo(calendar) == 1) {
                	Toast toast = Toast.makeText(DateScreen.this,
                			"End Date must be after start date.", 
                			Toast.LENGTH_LONG);
                	toast.show();
                	return;
                }
                cal2.add(Calendar.YEAR, 1);
                if (cal2.compareTo(calendar) == -1) {
                	Toast toast = Toast.makeText(DateScreen.this,
                			"Maximum historical data that can be pulled at one time is one year.", 
                			Toast.LENGTH_LONG);
                	toast.show();
                	return;
                }
                
                String host = "http://www.wunderground.com/history/airport/";
                String file = "CustomHistory.html?";
                String urlEnd = "&req_city=NA&req_state=NA&req_statename=NA&format=1";
                completeURL = String.format("%s%s/%s/%s/%s/%sdayend=%s&monthend=%s&yearend=%s%s",host,theIcao,startyear,startmonth,startday,file,endday,endmonth,endyear,urlEnd);
        		//debug.setText(completeURL);
                conn = check.checkNow(DateScreen.this.getApplicationContext());
                if (conn) {
	        		try {
	    				new BackgroundAsyncTask().execute(completeURL);
	    				submitButton.setClickable(false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	} else {
					connToast = Toast.makeText(DateScreen.this,
	   			         "Check your connection.", Toast.LENGTH_LONG);
					connToast.show();
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
    	} catch (IOException e) {
			connToast = Toast.makeText(this,
			         "Error while connecting.", Toast.LENGTH_LONG);
			connToast.show();
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
			         "Parsing CSV file, please wait...", Toast.LENGTH_LONG);		   
		   ourToast.show();
    	}
    	
    	@Override
    	protected void onPostExecute(String result)
    	{
    		ourToast.cancel();
    		if (graphData != null)
    			GoToGraphScreen();
    		else {
				Toast.makeText(DateScreen.this,
						"Error while connecting.", Toast.LENGTH_LONG).show();
				submitButton.setClickable(true);		
			}
    	}
	
    	@Override
    	protected String doInBackground(String... theURL)
    	{
    		graphData = null;
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

    public class CheckConnectivity {
        ConnectivityManager connectivityManager;
        NetworkInfo info;

        public boolean checkNow(Context con){
     
            try{
                connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
     
                if(info != null && info.isAvailable() && info.isConnected())
                {
                    return true;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
     
            return false;
        }
    }
    
}

