package com.android.example.rewardify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	public FileIO fileIO = new FileIO(this);
	private TextView pointsET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);


		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

	initiateUI();

	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_sync) {
			syncData();
			return true;
		}
		else if (id == R.id.action_settings) {
			Intent intent = new Intent(this, OptionsActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	private void syncData()
	{
		Toast.makeText(OptionsActivity.this, "Syncing data...", Toast.LENGTH_SHORT).show();
	}
	
	// Handle navigation view item clicks.
	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();

		Intent intent = null;

		if (id == R.id.nav_todo) {
			intent = new Intent(this, MainActivity.class);
		} else if (id == R.id.nav_tasks) {
			intent = new Intent(this, TasksActivity.class);
		} else if (id == R.id.nav_rewards) {
			intent = new Intent(this, RewardsActivity.class);
		} else if (id == R.id.nav_options) {
			//intent = new Intent(this, OptionsActivity.class);
			intent = null;
		} else {
			intent = null;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);

		if (intent != null)
		{
			startActivity(intent);
		}

		return true;
	}
	
	//Initiates the UI on activity startup
	private void initiateUI()
	{
		//Get Data
		fileIO.getPrefs();

		//Set button
		Button saveButton = (Button) findViewById(R.id.save_btn);
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				save();
			}
		});

		//Initiate Points edit text view
		pointsET = (EditText)findViewById(R.id.exp_et);
		pointsET.setText(""+ fileIO.total);
	}

	//Saves changes
	private void save()
	{
		String result = pointsET.getText().toString();
		if(!(result.equals("") || (result.matches("[0]+"))))
		{
			fileIO.total = Integer.parseInt(result);
			fileIO.setPrefs();
			makeToast("Options saved", false);
		}
		else
		{
			makeToast("Error: some fields contain invalid data", false);
		}
	}
	
	/**
	 * Used to easily make a toast
	 **/
	private void makeToast(String message, boolean longToast) {
		if (longToast)	{ Toast.makeText(this, message, Toast.LENGTH_LONG).show(); }
		else			{ Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); }
	}
}
