package com.android.example.rewardify;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class manages the data that is imported from internal storage.
 */

public class FileIO {
	//Lists of Tasks
	public ArrayList<Task> activeTaskList;
	public ArrayList<Task> archivedTaskList;

	//Lists of Rewards
	public ArrayList<Reward> activeRewardList;
	public ArrayList<Reward> archivedRewardList;

	//Codes for allocating list items
	public static final String activeCode = "active";
	public static final String archivedCode = "archived";

	//Filenames
	public static final String tasksFile = "tasks";
	public static final String prefsFile = "preferences";
	public static final String rewardsFile = "rewards";

	//Parent Activity - Required to write to and from file
	private Context context;

	//App Settings Values
	public int points;
	public int total;
	public int rewardPoints;

	//list of refresh types
	//TODO: Add more refresh types
	ArrayList<String> refreshTypes = new ArrayList<String>(Arrays.asList("daily", "weekly"));

	/**Get App Settings from File*/
	//Source: https://developer.android.com/guide/topics/data/data-storage.html#pref
	public void getPrefs()
	{
		SharedPreferences settings = context.getSharedPreferences(prefsFile, 0);
		points = settings.getInt("points", 0);
		total = settings.getInt("total", 10);
		rewardPoints = settings.getInt("rewardPoints", 0);
	}
	/**Set App Settings to File*/
	public void setPrefs()
	{
		SharedPreferences settings = context.getSharedPreferences(prefsFile, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("points", points);
		editor.putInt("total", total);
		editor.putInt("rewardPoints", rewardPoints);
		editor.commit();
	}

	/**
	 * Exports task lists to internal storage
	 * */
	public void exportTasks()
	{
		//Write data to file
		try {
			//File Stream and Print Writer
			FileOutputStream fos = context.openFileOutput(tasksFile, Context.MODE_PRIVATE);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));

			//Write active Tasks to file
			for (Task T : activeTaskList)
			{
				String s = T.name + ","
						+ T.points + ","
						+ T.total + ","
						+ T.xpPerPoint + ","
						+ T.frequency + ","
						+ activeCode;

				writer.println(s);

			}
			//Write archived Tasks to file
			for (Task T : archivedTaskList)
			{
				String s = T.name + ","
						+ T.points + ","
						+ T.total + ","
						+ T.xpPerPoint + ","
						+ T.frequency + ","
						+ archivedCode;

				writer.println(s);
			}
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Imports task lists from internal storage
	 * */
	public void importTasks()
	{
		//Erase Existing Lists
		activeTaskList = new  ArrayList<Task>();
		archivedTaskList = new  ArrayList<Task>();

		//Import all items from internal storage into lists
		try {
			//Set input stream and buffer reader
			FileInputStream fis = context.openFileInput(tasksFile);
			BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
			String data;

			//While not at end of file, get data and add to lists
			while ( (data = reader.readLine()) != null ) {
				stringToTask(data);
			}
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Coverts a string to a task and stores it in the relevant TaskList.
	 */
	public void stringToTask(String input)
	{
		String [] values = input.split(",");

		//If task is active, put in active tasks list
		if(values[5].equals(activeCode))
		{
			activeTaskList.add(
					new Task(values[0],           		 	//Name
							Integer.parseInt(values[1]),    //points
							Integer.parseInt(values[2]),    //total
							Integer.parseInt(values[3]),    //xpPerPoint
							values[4]));                    //frequency
		}
		//If task is not active, put in active tasks list
		else {
			archivedTaskList.add(
					new Task(values[0],                    	//Name
							Integer.parseInt(values[1]),    //points
							Integer.parseInt(values[2]),    //total
							Integer.parseInt(values[3]),    //xpPerPoint
							values[4]));                    //frequency
		}
	}

	/**
	 * Exports rewards lists to internal storage
	 * */
	//TODO: Finish This
	public void exportRewards(){
		//Write data to file
		try {
			//File Stream and Print Writer
			FileOutputStream fos = context.openFileOutput(rewardsFile, Context.MODE_PRIVATE);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));

			//Write active Tasks to file
			for (Reward R : activeRewardList)
			{
				String s = R.name + ","
						+ R.rewardPoints + ","
						+ activeCode;

				writer.println(s);

			}
			//Write archived Tasks to file
			for (Reward R : archivedRewardList)
			{
				String s = R.name + ","
						+ R.rewardPoints + ","
						+ activeCode;

				writer.println(s);
			}
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * imports rewards lists from internal storage
	 * */
	public void importRewards(){
		//Erase Existing Lists
		activeRewardList = new  ArrayList<Reward>();
		archivedRewardList = new  ArrayList<Reward>();

		//Import all items from internal storage into lists
		try {
			//Set input stream and buffer reader
			FileInputStream fis = context.openFileInput(rewardsFile);
			BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
			String data;

			//While not at end of file, get data and add to lists
			while ( (data = reader.readLine()) != null ) {
				stringToReward(data);
			}
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**Coverts a string to a reward and stores it in the relevant TaskList.*/
	public void stringToReward(String input)
	{
		String [] values = input.split(",");

		//If task is active, put in active tasks list
		if(values[2].equals(activeCode))
		{
			activeRewardList.add(
					new Reward(values[0],           		//Name
							Integer.parseInt(values[1])));	//rewardPoints
		}
		//If task is not active, put in active tasks list
		else {
			activeRewardList.add(
					new Reward(values[0],           		//Name
							Integer.parseInt(values[1])));	//rewardPoints
		}
	}
	/**
	 * This method generates a generic TaskList, for testing purposes only.
	 * */
	public void createGenericTasks()
	{
		//ArrayList<Task> result = new ArrayList<Task>();
		for (int i = 1; i < 10; i++)
			activeTaskList.add( new Task("Task " + Integer.toString(i), 0, 50, i, "daily"));
	}

	/**
	 * This method generates a generic RewardsList, for testing purposes only.
	 * */
	public void createGenericRewards()
	{
		for (int i = 1; i < 10; i++)
			activeRewardList.add( new Reward("Reward " + Integer.toString(i), i));
	}


	/**
	 * Gets a list of names from the Active Rewards List
	 * */
	public ArrayList<String> getRewardNames(String selection)
	{
		ArrayList<Reward> selectedList = selectRewardList(selection);

		ArrayList<String> result = new ArrayList<String>();
		for (Reward R : selectedList)
		{
			result.add(R.name);
		}
		return result;
	}

	/**
	 * Finds a Reward by name
	 * */
	public Reward getReward(String name, String selection)
	{
		Reward result = null;

		ArrayList<Reward> selectedList = selectRewardList(selection);

		for (Reward R : selectedList)
		{
			if (R.name.equals(name)) {
				result = R;
				break;
			}
		}
		if (result == null)
			Log.e("MainActivity", "getTask: name not found in list of tasks.");

		return result;
	}

	/**Used to choose active or archived list*/
	private ArrayList<Reward> selectRewardList(String selection)
	{
		if(selection.equals(activeCode)) {
			return activeRewardList;
		}
		else {
			return archivedRewardList;
		}
	}

	/**
	 * Gets a list of names from the Active Task List
	 * */
	public ArrayList<String> getTaskNames()
	{
		ArrayList<String> result = new ArrayList<String>();
		for (Task T : activeTaskList)
		{
			result.add(T.name);
		}
		return result;
	}

	public ArrayList<String> getArchivedTaskNames()
	{
		ArrayList<String> result = new ArrayList<String>();
		for (Task T : archivedTaskList)
		{
			result.add(T.name);
		}
		return result;
	}

	/**
	 * Finds a task by name
	 * */
	public Task getTask(String name)
	{
		Task result = null;

		for (Task T : activeTaskList)
		{
			if (T.name.equals(name)) {
				result = T;
				break;
			}
		}
		if (result == null)
			Log.e("MainActivity", "getTask: name not found in list of tasks.");

		return result;
	}

	/**
	 * Finds a task by name
	 * */
	public Task getArchivedTask(String name)
	{
		Task result = null;

		for (Task T : archivedTaskList)
		{
			if (T.name.equals(name)) {
				result = T;
				break;
			}
		}
		if (result == null)
			Log.e("MainActivity", "getArchivedTask: name not found in list of tasks.");

		return result;
	}


	public FileIO(Context context) {
		this.context = context;

		activeTaskList = new  ArrayList<Task>();
		archivedTaskList = new  ArrayList<Task>();
		activeRewardList = new  ArrayList<Reward>();
		archivedRewardList = new  ArrayList<Reward>();

	}
}
