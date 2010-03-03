package com.busstopalarm;

import java.io.FileNotFoundException;

import java.io.IOException;

import com.busstopalarm.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main page of the Bus Stop Alarm
 * 
 * @author David Nufer, Orkhan Muradov
 */
public class MainPage extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Button RouteSearchButton = (Button) findViewById(R.id.RouteSearchButton);

		// search button behavior
		RouteSearchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String routeText = ((EditText) findViewById(R.id.RouteSearchBox)).getText().toString();
				try {
					DataFetcher df = new DataFetcher();
					int routeNumber = Integer.parseInt(routeText);
					//BusRoute br = df.getBusRouteById(routeNumber, false);
					String s = "PlaceHolder for Route Info.";
					Intent i = new Intent(v.getContext(), MapPage.class);
					i.putExtra("routeNumber", routeNumber);
					startActivity(i);
					Toast t = Toast.makeText(v.getContext(), s.substring(0,
							Math.min(s.length(), 500)), Toast.LENGTH_LONG);
					t.show();
					finish();
				} catch (Exception e) {
					Toast t = Toast.makeText(v.getContext(),
							"Invalid Route Number", Toast.LENGTH_LONG);
					t.show();
				}
			}
		});

		// favorite button behavior
		final Button favButton = (Button) findViewById(R.id.FavButton);
		favButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), LocationListPage.class);
				i.putExtra("listType", LocationListPage.FAVORITES);
				startActivity(i);
			}
		});

		// major button behavior
		final Button majorButton = (Button) findViewById(R.id.MajorButton);
		majorButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), LocationListPage.class);
				i.putExtra("listType", LocationListPage.MAJOR);
				startActivity(i);
			}
		});

		BusDbAdapter ad = new BusDbAdapter(getApplicationContext());
		ad.open();
		
		//////// temporary /////////
		ad.deleteAllDestinations();
		try { 
			ad.readDbFile(0);
			ad.readDbFile(1);
			ad.readDbFile(2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		////////////////////////////
		
		Cursor recent = ad.getRecentDest(5);
		LinearLayout recentList = (LinearLayout) findViewById(R.id.recent_routes);
		int routeIndex = recent.getColumnIndex("route_id");
		int routeDescIndex = recent.getColumnIndex("route_desc");
		while (!recent.isAfterLast()) {
			final TextView recentItem = new TextView(this);
			recentItem.setClickable(true);
			final int routeNumber = Integer.parseInt(recent.getString(routeIndex));
			
			recentItem.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(v.getContext(), MapPage.class);
					i.putExtra("routeNumber", routeNumber);
					startActivity(i);
				}
			});

			recentItem.setTextSize(TypedValue.COMPLEX_UNIT_PT, 8);
			recentItem.setText("Route " + recent.getString(routeIndex) + ", "
					+ recent.getString(routeDescIndex));
			recentList.addView(recentItem);
			recent.moveToNext();
		}
		ad.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 1, "Confirmation");
		menu.add(0, 2, 2, "Help/About");
		menu.add(0, 3, 3, "Settings");
		menu.add(0, 4, 4, "Exit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			final Intent q = new Intent(this, ConfirmationPage.class);
			startActivity(q);
			finish();
			break;
		case 2:
			final Intent helpPage = new Intent(this, HelpPage.class);
			startActivity(helpPage);
			break;
		case 3:
			final Intent Settings = new Intent(this, Settings.class);
			startActivity(Settings);
			break;
		case 4:
			finish();
			break;
		}
		return false;

	}
}