package com.android.example.rewardify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import android.content.IntentSender.SendIntentException;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.android.gms.drive.DriveApi.DriveIdResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.MetadataChangeSet;

public class MainActivity extends AppCompatActivity	implements NavigationView.OnNavigationItemSelectedListener, MainDialogFragment.OnCompleteListener, ConnectionCallbacks, OnConnectionFailedListener {

	//Google API Client//GOOGLE DRIVE
	private GoogleApiClient mGoogleApiClient;
	private static final String TAG = "drive-quickstart";
	private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
	private static final int REQUEST_CODE_CREATOR = 2;
	private static final int REQUEST_CODE_RESOLUTION = 3;

	//From Tutorial File
	private static final int REQUEST_CODE = 102;
	private static final String EXISTING_FILE_ID = "0B_ylYiT0PYlPUnp1dUk3cEFLRVU";


	//Adaptor for list
	private RowAdapter rAdapter;

	//Dialogue Box for adding tasks
	public MainDialogFragment addTaskDialogue;

	//Object for handling writing to and from file
	public FileIO fileIO = new FileIO(this);

	//Rewards Bar and Text View
	private ProgressBar xpBar;
	private TextView rewardPointsTV;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//Create Floating Action Button
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showTaskDialog();
			}
		});

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		initialiseValues();
		getData();
		initialiseList();
		updateRewardsPoints();
	}


	@Override//GOOGLE DRIVE
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (!connectionResult.hasResolution()) {
			// show the localized error dialog.
			GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
			return;
		}

		try {
			connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
		} catch (SendIntentException e) {
			Log.e(TAG, "Exception while starting resolution activity", e);
		}

//		if (connectionResult.hasResolution()) {
//			try {
//				connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
//			} catch (IntentSender.SendIntentException e) {
//				// Unable to resolve, message user appropriately
//			}
//		} else {
//			GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
//		}

	}
	@Override//GOOGLE DRIVE
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			Log.i(TAG, "In onActivityResult() - connecting...");
			mGoogleApiClient.connect();
		}
	}
	@Override//GOOGLE DRIVE
	public void onConnected(Bundle connectionHint) {
//		switch (requestCode) {
//        //SOMETHING HERE...
//			case RESOLVE_CONNECTION_REQUEST_CODE:
//				if (resultCode == RESULT_OK) {
//					mGoogleApiClient.connect();
//				}
//				break;
//		}

		Drive.DriveApi.fetchDriveId(mGoogleApiClient, EXISTING_FILE_ID).setResultCallback(idCallback);

	}

	//This part actually gets the file
	final private ResultCallback<DriveIdResult> idCallback = new ResultCallback<DriveIdResult>() {
				@Override
				public void onResult(DriveIdResult result) {
					//TODO: put export code here
				}
			};

	@Override//GOOGLE DRIVE
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "GoogleApiClient connection suspended");
	}




	@Override
	public void onResume(){
		super.onResume();
		getData();
		refreshUI();

		if (mGoogleApiClient == null) {
			// Create the API client and bind it to an instance variable.
			// We use this instance as the callback for connection and connection
			// failures.
			// Since no account name is passed, the user is prompted to choose.
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(Drive.API)
					.addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();
		}
		// Connect the client. Once connected, the camera is launched.
		mGoogleApiClient.connect();
	}
	/**
	 * If the user presses back and the app drawer is open, it will close the app drawer.
	 * */
	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * Inflate the menu; this adds items to the action bar if it is present.
	 * */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

/**
 * Handle action bar item clicks here.
 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
		//Toast.makeText(MainActivity.this, "Syncing data...", Toast.LENGTH_SHORT).show();
		new LoadDataTask().execute();
	}




	/**
	 * Controls navigation drawer selections.
	 * */
	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		Intent intent = null;

		if (id == R.id.nav_todo) {
			//intent = new Intent(this, MainActivity.class);
			intent = null;
		} else if (id == R.id.nav_tasks) {
			intent = new Intent(this, TasksActivity.class);
		} else if (id == R.id.nav_rewards) {
			intent = new Intent(this, RewardsActivity.class);
		} else if (id == R.id.nav_options) {
			intent = new Intent(this, OptionsActivity.class);
		} else {
			intent = null;;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);

		if (intent != null)
		{
			startActivity(intent);
		}

		return true;
	}

	/**
	 * Interface Implementation to get result from dialogue fragment
	 * */
	public void onComplete(String result) {
		fileIO.stringToTask(result);
		fileIO.exportTasks();
		initialiseList();
		refreshUI();
	}

	/**
	 * Opens the edit task dialogue box
	 * */
	private void showTaskDialog() {
		FragmentManager fm = getSupportFragmentManager();
		addTaskDialogue = MainDialogFragment.newInstance("Some Title");
		addTaskDialogue.show(fm, "fragment_edit_task");
	}

	/**
	 * Sets the view variables when the app starts
	 * */
	private void initialiseValues()
	{
		//Set Progress Bar
		xpBar = findViewById(R.id.progress_bar);
		xpBar.setProgress(0);   // Main Progress
		//xpBar.setSecondaryProgress(100); // Secondary Progress
		xpBar.setMax(100); // Maximum Progress
		//xpBar.setProgressDrawable(drawable);

		rewardPointsTV = findViewById(R.id.points_tv);
	}

	/**
	 * Initialises the custom list when the app starts
	 * */
	private void initialiseList()
	{
		ListView list = (ListView) findViewById(R.id.list_view);
		rAdapter = new RowAdapter();
		list.setAdapter(rAdapter);
	}

	/**
	 * Gets data when the app starts
	 * */
	private void getData() {
		fileIO.importTasks();
		fileIO.getPrefs();
	}


	/**
	 * Custom row adapter
	 **/
	class RowAdapter extends ArrayAdapter<String> {
		public RowAdapter() {
			super(MainActivity.this, R.layout.list_row_task, R.id.title_tv, fileIO.getTaskNames());
		}
		public View getView(int pos, View cView, ViewGroup parent) {
			final View row = super.getView(pos, cView, parent);

			//Get the current task
			Task currentTask = fileIO.activeTaskList.get(pos);

			//Set the score text
			TextView pointsTV = (TextView) row.findViewById(R.id.remaining_tv);
			if (currentTask != null) {
				pointsTV.setText(currentTask.getPointString());
			}
			else {
				pointsTV.setText("ERR");
			}

			//Set onclick Listeners for the buttons
			final String name = currentTask.name;
			ImageButton incrementButton = row.findViewById(R.id.increment_button);
			incrementButton.setOnClickListener(
					new View.OnClickListener() {
						public void onClick(View view) {
							TaskPointsIncrement(name);
						}
					});

			ImageButton decrementButton = row.findViewById(R.id.decrement_button);
			decrementButton.setOnClickListener(
					new View.OnClickListener() {
						public void onClick(View view) {
							TaskPointsDecrement(name);
						}
					});

			return row;
		}
	}

	/**
	 * Increments a tasks points and updates the corresponding row view
	 * */
	private void TaskPointsIncrement(String name)
	{
		Task t = fileIO.getTask(name);

		//If points are less than the total, update points
		if (t.points < t.total)
		{
			t.points ++;

			fileIO.getPrefs();
			fileIO.points = fileIO.points + t.xpPerPoint;
			fileIO.setPrefs();

			updateRewardsPoints();

			//Update file
			fileIO.exportTasks();
		}
	}

	/**
	 * Decrements a tasks points and updates the corresponding row view
	 * */
	private void TaskPointsDecrement(String name)
	{
		Task t = fileIO.getTask(name);

		//If points are greater than 0, update points
		if (t.points > 0)
		{
			t.points --;
			fileIO.getPrefs();
			fileIO.points = fileIO.points - t.xpPerPoint;
			fileIO.setPrefs();

			updateRewardsPoints();

			//Update file
			fileIO.exportTasks();
		}
	}
	/**
	 * Updates the amount of reward points when the user earns one
	 * */
	private void updateRewardsPoints()
	{
		fileIO.getPrefs();
		if(fileIO.points >= fileIO.total)
		{
			fileIO.points = fileIO.points - fileIO.total;
			fileIO.rewardPoints ++;
		}
		else if(fileIO.points < 0)
		{
			fileIO.points = fileIO.total + fileIO.points;
			if (fileIO.rewardPoints > 0) {
				fileIO.rewardPoints --;
			}
		}
		refreshUI();
		fileIO.setPrefs();
	}

	/**
	 * Refreshes the UI when changes are made
	 * */
	private void refreshUI()
	{
		rAdapter.notifyDataSetChanged();
		xpBar.setProgress(fileIO.points * 100 / fileIO.total);
		rewardPointsTV.setText(Integer.toString(fileIO.rewardPoints) + " points");
	}


	//NEW CODE
	protected ProgressDialog progressDialog;

	private class LoadDataTask extends AsyncTask<Void, Integer, Void> {
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(MainActivity.this, "Loading List","starting", true);
		}
		protected Void doInBackground(Void... params) {
			publishProgress(0);
			//InputStream inputStream = getResources().openRawResource( R.raw.ratings);
			SystemClock.sleep(7000);
			publishProgress(1);
			//movies = Movie.loadFromFile(inputStream);
			SystemClock.sleep(7000);
			publishProgress(2);
			SystemClock.sleep(7000);
			return null;
		}
		public void onProgressUpdate(Integer... progUpdate){
			switch(progUpdate[0]){
				case 0:
					progressDialog.setMessage("Opening File");
					break;
				case 1:
					progressDialog.setMessage("Reading File");
					break;
				case 2:
					progressDialog.setMessage("Initiating UI");
					break;
			}
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//setListAdapter(new RowIconAdapter(MovieRatingsActivity.this, R.layout.listrow, R.id.row_label, movies));
			progressDialog.dismiss();
		}
	}
}



//----------------
