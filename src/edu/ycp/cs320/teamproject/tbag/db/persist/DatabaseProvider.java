package edu.ycp.cs320.teamproject.tbag.db.persist;

/*
 * Some or all code borrowed from CS320 Library Example
 */

import edu.ycp.cs320.teamproject.tbag.db.persist.IDatabase;;

public class DatabaseProvider {
	private static IDatabase theInstance;
	
	public static void setInstance(IDatabase db) {
		theInstance = db;
	}
	
	public static IDatabase getInstance() {
		if (theInstance == null) {
			throw new IllegalStateException("IDatabase instance has not been set!");
		}
		return theInstance;
	}
}