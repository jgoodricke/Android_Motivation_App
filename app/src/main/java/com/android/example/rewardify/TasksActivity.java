package com.android.example.rewardify;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class TasksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TasksActiveDialogFragment.OnActiveCompleteListener, TasksArchivedDialogFragment.OnArchivedCompleteListener {



	//Object for handling writing to and from file
	public FileIO fileIO = new FileIO(this);

	//Tab Fragments
	private TasksActiveTabFragment _activef;
	private TasksArchivedTabFragment _archivedf;
	private TabLayout tabs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasks);

		//Set Toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// setup fragments
		_activef = new TasksActiveTabFragment();
		String TasksActiveName = "Active Tasks";
		_archivedf = new TasksArchivedTabFragment();
		String TasksArchivedTabName = "Archived Tasks";

		//Set Tabs
		tabs = (TabLayout) findViewById(R.id.tabLayout);

		tabs.addTab(tabs.newTab().setText(TasksActiveName));
		tabs.addTab(tabs.newTab().setText(TasksArchivedTabName));
		tabs.setTabGravity(TabLayout.GRAVITY_FILL);

		// Set up Tab Pager
		final MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), 2);
		final ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
		pager.setAdapter(adapter);
		// listen for page changes
		pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

		// listen for tab selection
		tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				pager.setCurrentItem(tab.getPosition());
			}
			@Override
			public void onTabUnselected(TabLayout.Tab tab) { }
			@Override
			public void onTabReselected(TabLayout.Tab tab) { }
		});

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	//On back pressed, if drawer is open close drawer
	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}
	// Inflate the menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tasks, menu);
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
		Toast.makeText(TasksActivity.this, "Syncing data...", Toast.LENGTH_SHORT).show();
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
			//intent = new Intent(this, TasksActivity.class);
			intent = null;
		} else if (id == R.id.nav_rewards) {
			intent = new Intent(this, RewardsActivity.class);
		} else if (id == R.id.nav_options) {
			intent = new Intent(this, OptionsActivity.class);
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

	//Page Adaptor Class
	class MyPagerAdapter extends FragmentStatePagerAdapter {
		private int _numberOfTabs;

		public MyPagerAdapter(FragmentManager fm, int numberOfTabs) {
			super(fm);
			_numberOfTabs = numberOfTabs;
		}

		// dispatch appropriate fragment
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return _activef;
				case 1:
					return _archivedf;
				default:
					return null;
			}
		}
		public int getCount() {
			return _numberOfTabs;
		}
	}

	//Interface for Active Dialog box
	public void onActiveComplete(String oldName, String name, int repeats, String Interval, int exp, boolean archive) {
		Task oldTask = fileIO.getTask(oldName);
		Task newTask = new Task(name, oldTask.points, repeats, exp, Interval);

		//If the task is to be archived, remove it from active list and place in archive list
		if (archive) {
			fileIO.activeTaskList.remove(oldTask);
			fileIO.archivedTaskList.add(newTask);
		}
		//If not to be archived, override task
		else
		{
			fileIO.activeTaskList.set( fileIO.activeTaskList.indexOf(oldTask), newTask);
		}

		//Export tasks and refresh tab
		fileIO.exportTasks();
		_activef.editingComplete();
		_archivedf.editingComplete();

	}

	
	public void onArchivedComplete(String oldName, String name, int repeats, String Interval, int exp, boolean restore, boolean delete) {
		Task oldTask = fileIO.getArchivedTask(oldName);
		Task newTask = new Task(name, oldTask.points, repeats, exp, Interval);

		//If file is to be deleted, remove it from list
		if (delete) {
			fileIO.archivedTaskList.remove(oldTask);
		}
		//If the task is to be archived, remove it from active list and place in archive list
		else if (restore) {
			fileIO.archivedTaskList.remove(oldTask);
			fileIO.activeTaskList.add(newTask);
		}
		//If not to be archived, override task
		else
		{
			fileIO.archivedTaskList.set( fileIO.archivedTaskList.indexOf(oldTask), newTask);
		}

		//Export tasks and refresh tab
		fileIO.exportTasks();
		_activef.editingComplete();
		_archivedf.editingComplete();
	}


}
