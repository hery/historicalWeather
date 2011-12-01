package com.daemgahe.historicalWeather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class DateScreen extends Activity 
{
	private String theIcao;
	private Button submit;
	private TextView debug;
	private String myURL;
	
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
        
        EditText startDate = (EditText) findViewById(R.id.start_date);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.roll(Calendar.YEAR, -1);
        String startDateValue = df.format(c.getTime());
        startDate.setText(startDateValue);
        EditText endDate = (EditText) findViewById(R.id.end_date);
        String endDateValue = df.format(d);
        endDate.setText(endDateValue);
        
        // Use regex to parse dates
        Pattern pattern = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})");
        
        Matcher matcher = pattern.matcher(startDateValue);
        if (matcher.find()) 
        {
        	startyear  = matcher.group(1);
        	startmonth = matcher.group(2);
        	startday = matcher.group(3);		
        }	
        
        Matcher matcher1 = pattern.matcher(endDateValue);
        if (matcher1.find()) 
        {
        	endyear  = matcher1.group(1);
        	endmonth = matcher1.group(2);
        	endday = matcher1.group(3);		
        }	
        
        // Create a bundle to get the intent parameters
        Bundle bundle = this.getIntent().getExtras();
        
        // Prepare different parts of the URL
        String host = "http://www.wunderground.com/history/airport/";
        theIcao = bundle.getString("icao");
        String file = "CustomHistory.html?";
        String urlEnd = "&req_city=NA&req_state=NA&req_statename=NA&format=1";
        
        // Form URL
        completeURL = String.format("%s%s/%s/%s/%s/%sdayend=%s&monthend=%s&yearend=%s%s",host,theIcao,startyear,startmonth,startday,file,endday,endmonth,endyear,urlEnd);
        
        debug = (TextView) findViewById(R.id.debugTextView);
        Button submit = (Button) findViewById(R.id.submitButton);
       
        submit.setOnClickListener
        (new View.OnClickListener() 
        {	
        	public void onClick(View v) 
        	{
        		  //debug.setText(completeURL);
        		try {
					getData();
					//debug.setText(graphData);
					GoToGraphScreen();
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
    	Intent dateToGraph = new Intent(this, GraphScreen.class);
    	dateToGraph.putExtras(bundle);
    	startActivityForResult(dateToGraph,0);
    }
    
    public void getData() throws Exception
    {
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
}

