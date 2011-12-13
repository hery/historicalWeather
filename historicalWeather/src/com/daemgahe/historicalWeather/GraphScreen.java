package com.daemgahe.historicalWeather;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.androidplot.Plot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GraphScreen extends Activity {
	
	private XYPlot theGraph;
	private Button exitButton;
	private Button startOverButton;
	//private TextView debug;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        Bundle bundle = this.getIntent().getExtras();
        //debug = (TextView) findViewById(R.id.debug);
        
        long startDate = bundle.getLong("startDate");
        long endDate = bundle.getLong("endDate");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(new Date(startDate));
        c2.setTime(new Date(endDate));
        ArrayList<Long> timeStamps = new ArrayList<Long>();
        while (c1.before(c2)) {
        	timeStamps.add(c1.getTime().getTime());
        	c1.add(Calendar.DAY_OF_MONTH, 1);
        }
        int size = timeStamps.size();
        
        String graphData = bundle.getString("graph");
        // debug.setText(graphData);
        Log.v("Weather Graph", graphData);
        String[] lines = graphData.split("<br />\n");
        Number[] avgTemps = new Number[size];
        int writeIndex = 0;
        for (int i = 0; i < lines.length; ++i) {
        	String[] line = lines[i].split(",");
        	try {
				avgTemps[writeIndex++] = Integer.parseInt(line[2]);
			} catch (Exception e) {
				// skip
			}
        }
        
        theGraph = (XYPlot) findViewById(R.id.plot);
        XYSeries avgTempSeries = new SimpleXYSeries(
        		timeStamps,
        		Arrays.asList(avgTemps),
        		"Average Temperatures");
        
        theGraph.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        theGraph.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
        theGraph.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        theGraph.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        theGraph.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
        theGraph.getGraphWidget().setPaddingTop(15);
        theGraph.getGraphWidget().setPaddingRight(32);
        theGraph.getGraphWidget().setPaddingBottom(4);
 
        theGraph.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        theGraph.getBorderPaint().setStrokeWidth(1);
        theGraph.getBorderPaint().setAntiAlias(false);
        theGraph.getBorderPaint().setColor(Color.WHITE);
 
        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter formatter = new LineAndPointFormatter(
                Color.rgb(0, 0, 0),                   // line color
                Color.rgb(0, 0, 255),                   // point color
                Color.rgb(100, 200, 0));                // fill color
 
 
        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
 
        formatter.setFillPaint(lineFill);
        formatter.getVertexPaint().setStrokeWidth(3);
        theGraph.addSeries(avgTempSeries, formatter);
 
        // draw a domain tick for each month:
        theGraph.setDomainStep(XYStepMode.SUBDIVIDE, 4);
 
        // customize our domain/range labels
        theGraph.setDomainLabel("Date");
        theGraph.setRangeLabel("Average Temp");
 
        // get rid of decimal points in our range labels:
        theGraph.setRangeValueFormat(new DecimalFormat("0"));
 
        theGraph.setDomainValueFormat(new SimpleDateFormat("MM/dd/yyyy"));
 
        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        theGraph.disableAllMarkup();
        
        exitButton = (Button) findViewById(R.id.exitButton);
        startOverButton = (Button) findViewById(R.id.startOverButton);
        
        startOverButton.setOnClickListener
        (new View.OnClickListener() 
        {	
        	@Override
			public void onClick(View v) 
        	{
                GoToFirstScreen();
        	}
        });
        
        exitButton.setOnClickListener
        (new View.OnClickListener() 
        {	
        	@Override
			public void onClick(View v) 
        	{
                moveTaskToBack(true);
        	}
        });
        
    }
    
    protected void GoToFirstScreen()
    {
    	Intent goBackToFirstScreen = new Intent(this, HistoricalWeatherActivity.class);
    	startActivityForResult(goBackToFirstScreen,0);
    }
    

}
