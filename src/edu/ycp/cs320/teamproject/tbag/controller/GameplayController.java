package edu.ycp.cs320.teamproject.tbag.controller;


import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.teamproject.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.teamproject.tbag.db.persist.DerbyDatabase;
import edu.ycp.cs320.teamproject.tbag.db.persist.IDatabase;
import edu.ycp.cs320.teamproject.tbag.model.Gameplay;
import edu.ycp.cs320.teamproject.tbag.model.Item;

/*
 * Some or all code borrowed from CS320 Library Example
 */

public class GameplayController 
{
	private IDatabase db;
	private Gameplay model; 
	private ArrayList<String> output;
	private int userLocation;
	private List<Item> itemsInRoom;
	private List<Item> usersInventory; 
	private int userScore; 
	private int userHealth; 
	
	
	public GameplayController(String username, Boolean newGame) {
		DatabaseProvider.setInstance(new DerbyDatabase());
		db = DatabaseProvider.getInstance();
		db.setUserFilePath(username);
		
		
		if (!newGame)
		{
			userLocation = db.getUserLocation();
			itemsInRoom = db.getItemsInLocation(userLocation);
			usersInventory = db.getItemsInLocation(0); 
			userScore = db.getUserScore(); 
			userHealth = db.getUserHealth(); 
			
			
			
		}
		else
		{
			db.resetGame();	

			db.addToCommands(db.getLocationDescriptionLong(1)); //Get room 1's description on new game since you don't "move" there
		}
		
		
			
		
	}
	

	
	public void gameLogic(String rawInput) {
		db.addToCommands(rawInput); 
		model.setInput(rawInput);
		String input = rawInput.toLowerCase();
		db.setUserScore(1);
		
	
		
		// ___________________Movement_______________________
		if(input.contains("move") || input.contains("go ")) {
			if(input.contains("north")) {
				moveTo(0);
			}
			else if(input.contains("south")) {
				moveTo(1);
			}
			else if(input.contains("east")) {
				moveTo(2);
			} 
			else if(input.contains("west")){
				moveTo(3);
			}
			else if(input.contains("up")){
				moveTo(4);
			}
			else if(input.contains("down")){
				moveTo(5);
			}
			else {
				System.out.println("Unknown direction");
				db.addToCommands("Unknown direction");
			}
		}
		
		//__________________Picking up items___________________
		
		else if (input.contains("pick up") || input.contains("grab") || input.contains("take")) 
		{
			 
			int itemsPickedUp = 0; 
			for (Item item : itemsInRoom)
			{
				String itemName = item.getName(); 
				
				// Trap in room 26
				if (input.contains("brilliant blade") && userLocation == 26 && itemName.equals("brilliant blade")) {
					db.setUserLocation(17);
					db.setItemLocation("brilliant blade", -1);
					db.addToCommands("You grab the weapon and suddenly the floor collapses beneath you. You fall into the room below and you loose grip of the sword and it shatters into pieces on impact");
					db.setUserHealth(-15);
				}
				else if (input.contains(itemName))
				{
					db.setItemLocation(itemName, 0);
					db.addToCommands("You picked up " + itemName);
					itemsPickedUp++; 
				}
				
								
			}
			
			if (itemsPickedUp == 0)
			{
				db.addToCommands("Couldn't pickup item"); 
			}
			
		//_________________Drop Item_____________________
		} 
		else if(input.contains("drop")) 
		{
			int itemsDropped = 0; 
			for (Item item : db.getItemsInLocation(0))
			{
				String itemName = item.getName(); 
				
				if (input.contains(itemName))
				{
					db.setItemLocation(itemName, db.getUserLocation());
					db.addToCommands("You dropped " + itemName + " in " + "room " + userLocation);
					itemsDropped++; 
				}
			}
			
			if (itemsDropped == 0)
			{
				db.addToCommands("You don't have that item");
			}
			
		} 
		else if(input.contains("examine"))
		{
			if(input.contains("examine ")) {
				List<Item> examinedItems = new ArrayList<Item>();
				for (Item item : usersInventory)
				{
					if(input.contains(item.getName())) {
						examinedItems.add(item);
					}
				}
				if(!examinedItems.isEmpty()) {
					for(Item item : examinedItems) {
						db.addToCommands(item.getItemDescription());
					}
				}
				else {
					db.addToCommands("You don't have the requested item(s)");
				}
			}
			else {
				db.addToCommands(db.getLocationDescriptionLong(userLocation)); 
				
				if (itemsInRoom.isEmpty())
				{
					db.addToCommands("There are no items in the room"); 
				}
				else
				{
					for (Item item : itemsInRoom)
					{
						db.addToCommands("There is a " + item.getName() + " in the room"); 
					}
				}
			}
		} 
		else if (input.contains("status") || input.contains("health") || 
					input.contains("score") || input.contains("inventory"))
		{
			List<String> itemNames = new ArrayList<String>(); 
			List<String> noItems = new ArrayList<String>(); 
			
			noItems.add("No items found"); 
		
			
			if (usersInventory.size() == 0)
			{
				
				model.addInventory(noItems);
			}
			else 
			{
				for (Item item : usersInventory)
				{
					
					itemNames.add(item.getName()); 
				}
				
				model.addInventory(itemNames);
				
			}
			db.setUserScore(4);
			userScore = db.getUserScore(); 
			model.setHealth(userHealth);
			model.setScore(userScore);
			model.setPlayerLocation(userLocation);
			
			db.addToCommands("------------------------->"); 
		}
		
		else if(input.contains("fight")) {
			
			//if the user has found and obtained the sword
			if(db.getItemLocationID("sword") == 0) {
				
				if(userLocation == 12) {
					
					db.setUserHealth(-5);
					db.setAgentLocation(1, 0);
					db.addToCommands("You have defeated the mighty Hercules! Your path is now clear!");
					
				}
				else if (userLocation == 16) {
					
					db.setUserHealth(-10);
					db.setAgentLocation(4, 0);
					db.addToCommands("You have slain Asterion! He no longer blocks your way!");
					
				}
				else if (userLocation == 21) {
					
					db.setUserHealth(-10);
					db.setAgentLocation(2, 28);
					db.addToCommands("You have bested Squall but he has ran off!");
					
				} else if (userLocation == 28) {
					
					db.setUserHealth(-10);
					db.setAgentLocation(2, 0);
					db.addToCommands("You have bested Squall and triumphed over his gunblade!");
					db.setItemLocation("gunblade", userLocation);
					
				}
				else if(userLocation == 20) {
					
					db.setUserHealth(-15);
					db.setUserLocation(23);
					db.setAgentLocation(3, 20);
					db.addToCommands("Theseus bests you again, you might want to drop that sword & get something better");
					
				}
				
			}
			else if (db.getItemLocationID("gunblade") == 0 && db.getItemLocationID("sword") != 0)
			{
				db.setUserHealth(-5);
				db.setUserLocation(20);
				db.setAgentLocation(3, 0);
				db.addToCommands("Finally you have had your revenge against Theseus, freedom beckons!"); 
			}
			//if the user does not possess the sword
			else {
				
				if(userLocation == 12) {
					
					db.setUserHealth(-5);
					db.setUserLocation(15);
					db.setAgentLocation(1, 12);
					db.addToCommands("Hercules punches you with his god-like strength sending you flying into another room.");
					
				}
				else if (userLocation == 16) {
					
					db.setUserHealth(-10);
					db.setUserLocation(15);
					db.setAgentLocation(4, 16);
					db.addToCommands("Asterion utters words of a spell, creating a fireball of azure flames and sends them hurtling into you."
							+ " Somewhat singed, you suddenly find yourself forced into another room.");
					
				}
				else if (userLocation == 21 && db.getAgentLocation(2) == 21) {
					
					db.setUserHealth(-10);
					db.setUserLocation(24);
					db.setAgentLocation(2, 28);
					db.addToCommands("Squall slashes at you with his gunblade while simultaneously firing the trigger."
							+ " The blast from the bullet along with the strength weilded behind his sword blast you into another room.");
					
				} else if (userLocation == 28) {
					
					db.setUserHealth(-10);
					db.setUserLocation(24);
					db.setAgentLocation(2, 28);
					db.addToCommands("Squall slashes at you with his gunblade while simultaneously firing the trigger."
							+ " The blast from the bullet along with the strength weilded behind his sword blast you into another room.");
					
				}
			}
			
		}
		
		else if(input.contains("run")) {
			
			if(userLocation == 12) {
				
				db.setUserLocation(15);
				db.setAgentLocation(1, 12);
				db.addToCommands("Intimidated by Hercules' god-like strength, you desperately flee into another room.");
				
			}
			else if (userLocation == 16) {
				
				db.setUserLocation(15);
				db.setAgentLocation(4, 16);
				db.addToCommands("The sounds of Asterion uttering a spell under his breath frightens you. You escape to another room.");
				
			}
			else if (userLocation == 21 && db.getAgentLocation(2) == 21) {
				
				db.setUserLocation(24);
				db.setAgentLocation(2, 28);
				db.addToCommands("The sound of gunfire reverberates off the walls, assaulting your ears and causing you to run back.");
				
			}
			else if(userLocation == 28) {
				
				db.setUserLocation(22);
				db.setAgentLocation(2, 28);
				db.addToCommands("The sound of gunfire reverberates off the walls, assaulting your ears and causing you to run back.");
				
			}
			else if(userLocation == 20) {
				
				db.setUserLocation(23);
				db.setAgentLocation(3, 20);
				db.addToCommands("You don't feel ready to confront your original captor just yet and turn back.");
				
			}
			
		}
		else if (input.contains("drink"))
		{
			for (Item item : usersInventory)
			{
				String itemName = item.getName(); 
				if (itemName.equals("green potion") || itemName.equals("pleasant potion"))
				{
					if (input.contains("drink " + itemName))
					{
						db.setUserHealth(30);
						db.setItemLocation(itemName, -1);
						db.addToCommands("You drank " + itemName); 
						
					}
				}
			}
		}
		
		else {
			System.out.println("Unknown command");
			db.addToCommands("Unknown command");
		}
				
		
		output();
	}
	
	public void setModel(Gameplay model)
	{
		this.model = model; 
	}

	
	
	public void output(){
		output = db.getCommands();
		model.setOutput(output);
	}
	
	
	public void moveTo(int direction) {
	
		int nextLocation = -1;
		if(direction == 0) {
			nextLocation = db.getLocationNorth(userLocation);
		}
		else if(direction == 1) {
			nextLocation = db.getLocationSouth(userLocation);
		}
		else if(direction == 2) {
			nextLocation = db.getLocationEast(userLocation);
		}
		else if(direction == 3){
			nextLocation = db.getLocationWest(userLocation);
		}
		else if(direction == 4){
			nextLocation = db.getLocationUp(userLocation);
		} 
		else if(direction == 5){
			nextLocation = db.getLocationDown(userLocation);
		}
		
		if(userLocation == nextLocation) 
		{
			System.out.println("Can't move that way");
			db.addToCommands("Can't move that way");
		}
		//__________________Puzzle_______________________
		else if(puzzle(nextLocation) == false) 
		{
			System.out.println("Door is locked...");
			db.addToCommands("Door is locked...");
		}
		//_______________Getting Past an Agent___________________
		else if(passAgent(nextLocation) == false) {
			db.addToCommands("Nice try! You must defeat or run from the enemy in front of you before you can leave.");
		}
		else {
			
			db.setUserLocation(nextLocation);
			userLocation = db.getUserLocation();
			String roomDescription = null;
			if(db.getPlayerHasBeen(nextLocation) == 0) {
				roomDescription = db.getLocationDescriptionLong(nextLocation);
				db.setPlayerHasBeen(nextLocation);
			}
			else if(db.getPlayerHasBeen(nextLocation) == 1) {
				roomDescription = db.getLocationDescriptionShort(nextLocation);
			}
			db.addToCommands(roomDescription);
			itemsInRoom = db.getItemsInLocation(userLocation);
			if (!itemsInRoom.isEmpty())
			{
				for (Item item : itemsInRoom)
				{
					db.addToCommands("There is a " + item.getName() + " in the room"); ///////////////////////////////////////////////////////////
				} 
			}
			
			// ____________________Agent Encounter ___________________
			for(int i = 1; i < 5; i++) {
				agentEncounter(db.getAgentLocation(i), db.getUserLocation());
			}
		}
		if(nextLocation == -1) {
			System.out.println("Movement failed, legs gone.");
		}
	}
	
	
	public void agentEncounter(int agent_location, int user_location) {
		if(agent_location == user_location) {
			db.addToCommands(db.getAgentDescription(agent_location));
		}
	}
	
	public boolean puzzle(int nextLocation) {
		// Go through player inventory
		for(Item item : usersInventory) {
			// if playerHas item and NextLocation == puzzleRoom
			String itemName = item.getName();
			if(nextLocation == 17 && itemName.equals("key")){
				return true;
			} 
		}
		
		// IF USER INVENTORY EMPTY
		// Do not let the player enter these rooms
		if(nextLocation == 17) {
			return false;
		}
		return true;
	}
	
	public boolean passAgent(int nextLocation) {
		
		//check to see if user defeated agent
		
		if((nextLocation == 15 || nextLocation == 13) && db.getAgentLocation(1) == db.getUserLocation()) {
			
			return false;
			
		}
		else if ((nextLocation == 15 || nextLocation == 13) && db.getAgentLocation(4) == db.getUserLocation()) {
			
			return false;
			
		}
		else if ((nextLocation == 22 || nextLocation == 24) && db.getAgentLocation(2) == db.getUserLocation()) {
			
			return false;
			
		} else if ((nextLocation == 27 || nextLocation == 25) && db.getAgentLocation(2) == db.getUserLocation()) {
			
			return false;
			
		} else if (nextLocation == 29 && db.getAgentLocation(3) == db.getUserLocation()) {
			
			return false;
			
		}
		
		return true;
	}
	
}
