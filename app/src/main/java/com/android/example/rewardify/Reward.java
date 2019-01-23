package com.android.example.rewardify;

/**
 * Used to hold data for each reward the user creates.
 */

public class Reward {

	public String name;
	public int rewardPoints;

	public String getPointString()
	{
		return Integer.toString(rewardPoints) + " points";
	}


	public Reward(String name, int rewardPoints)
	{
		this.name = name;
		this.rewardPoints = rewardPoints;
	}
}
