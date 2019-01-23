package com.android.example.rewardify;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;
import android.widget.Toast;

/**
* Activity for adding rewards
*/
public class RewardsActivity extends AppCompatActivity	implements NavigationView.OnNavigationItemSelectedListener, RewardsDialogFragment.OnCompleteListener {

	private RowAdapter rAdapter;
	TextView rewardPointsTV;

	//Dialogue Box for adding tasks
	public RewardsDialogFragment addRewardDialogue;

	public FileIO fileIO = new FileIO(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rewards);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showRewardDialog();
			}
		});


		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		rewardPointsTV = findViewById(R.id.points_tv);
	}

	@Override
	public void onResume(){
		super.onResume();
		getData();
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
	
	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rewards, menu);
		return true;
	}

	// Handle action bar item clicks.
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
		Toast.makeText(RewardsActivity.this, "Syncing data...", Toast.LENGTH_SHORT).show();
	}

	// Handle navigation view item clicks here.
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
			//intent = new Intent(this, RewardsActivity.class);
			intent = null;
		} else if (id == R.id.nav_options) {
			intent = new Intent(this, OptionsActivity.class);
		} else {
			intent = null;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);

		if (intent != null) {
			startActivity(intent);
		}

		return true;
	}
	//Gets data when activity starts
	public void getData() {
		fileIO.importRewards();
		fileIO.getPrefs();
	}
	//Initiates the UI on startup
	private void initiateUI()
	{
		//Set List
		ListView list = (ListView) findViewById(R.id.rewards_list);
		rAdapter = new RowAdapter();
		list.setAdapter(rAdapter);
		//Set Score
		rewardPointsTV.setText(Integer.toString(fileIO.rewardPoints));

	}

	//Custom List Row Adaptor
	class RowAdapter extends ArrayAdapter<String> {
		public RowAdapter() {
			super(RewardsActivity.this, R.layout.list_row_rewards, R.id.title_tv, fileIO.getRewardNames(fileIO.activeCode));
		}
		public View getView(int pos, View cView, ViewGroup parent) {
			final View row = super.getView(pos, cView, parent);

			//Get the current reward
			Reward currentReward = fileIO.activeRewardList.get(pos);

			//Set the score text
			TextView pointsTV = (TextView) row.findViewById(R.id.points_tv);
			if (currentReward != null)
			{
				pointsTV.setText(currentReward.getPointString());
			}
			else
			{
				pointsTV.setText("ERR");
			}

			//Set onclick Listeners for the buttons
			final int amountPos = currentReward.rewardPoints;
			final int amountNeg = currentReward.rewardPoints * -1;
			ImageButton incrementButton = row.findViewById(R.id.increment_button);
			incrementButton.setOnClickListener(
					new View.OnClickListener() {
						public void onClick(View view) {
							updateRewardsPoints(amountPos);
						}
					});

			ImageButton decrementButton = row.findViewById(R.id.decrement_button);
			decrementButton.setOnClickListener(
					new View.OnClickListener() {
						public void onClick(View view) {
							updateRewardsPoints(amountNeg);
						}
					});

			return row;
		}
	}


	/**
	 * Update the reward point value
	 * */
	private void updateRewardsPoints(int amount)
	{
		if(	fileIO.rewardPoints - amount >= 0){
			fileIO.getPrefs();
			fileIO.rewardPoints = fileIO.rewardPoints - amount;
			fileIO.setPrefs();
			rewardPointsTV.setText(Integer.toString(fileIO.rewardPoints));
		}
		else
		{
			makeToast("You don't have enough points for that reward. You can earn more points by completing tasks.", true);
		}
	}

	/**
	 * Used to easily make a toast
	 **/
	private void makeToast(String message, boolean longToast) {
		if (longToast)	{ Toast.makeText(this, message, Toast.LENGTH_LONG).show(); }
		else			{ Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); }
	}

	/**
	 * Interface Implementation to get result from dialogue fragment
	 * */
	public void onComplete(String result) {
		fileIO.stringToReward(result);
		fileIO.exportRewards();
		getData();
		initiateUI();
	}

	/**
	 * Displays Dialog box for adding rewards
	 * */
	private void showRewardDialog() {
		FragmentManager fm = getSupportFragmentManager();
		addRewardDialogue = RewardsDialogFragment.newInstance("Some Title");
		addRewardDialogue.show(fm, "fragment_edit_task");
	}
}
