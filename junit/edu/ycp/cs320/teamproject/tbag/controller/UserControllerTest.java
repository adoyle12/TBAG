package edu.ycp.cs320.teamproject.tbag.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.teamproject.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.teamproject.tbag.db.persist.DerbyDatabase;
import edu.ycp.cs320.teamproject.tbag.db.persist.IDatabase;
import edu.ycp.cs320.teamproject.tbag.model.*;

public class UserControllerTest 
{
	private User model;
	private UserController controller; 
	private String password = "world";
	private String username = "hello";
	private IDatabase db = null;
	
	@Before
	public void setUp() 
	{
		DatabaseProvider.setInstance(new DerbyDatabase());
		db = DatabaseProvider.getInstance();
		
		model = new User();
		model.setUsername(username);
		model.setJSPPassword(password);
		model.setDBPassword(password);
		
		controller = new UserController();
		controller.setModel(model);
	}
	
	@Test
	public void testCheckCredentials()
	{
		db.insertUserIntoUsersTable(username, password);
		controller.credentials(username, password);
		
		assertEquals("hello", model.getUsername());
		assertEquals("world", model.getJSPPassword());
		assertEquals(model.getJSPPassword(), model.getDBPassword());
	}
}