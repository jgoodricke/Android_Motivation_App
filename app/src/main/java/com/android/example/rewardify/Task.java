package com.android.example.rewardify;

/**
 * Holds the data for tasks
 */

public class Task {
	public int id;
	public String name;
	public int points;
	public int total;

	public int xpPerPoint;
	String frequency;


	public String getPointString()
	{
		return Integer.toString(points) + "/" + Integer.toString(total);
	}

	public Task(String name, int points, int total, int xpPerPoint, String frequency) {
		this.name = name;
		this.total = total;
		this.points = points;
		this.xpPerPoint = xpPerPoint;
		this.frequency = frequency;
	}
}
