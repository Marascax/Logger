package client;

import java.io.*;
import pages.PageController;

/**
 * Driver for program when used by user client.
 * @author Alex Marasco, Joseph Stefanik
 *
 */
public class ClientDriver implements Runnable
{
	private static final String INVALID_USER_ERROR = 
			"Invalid User: Other user not found or current user\n";
	
	private volatile boolean keepRunning;
	private static String currUser;
	private Thread thread;
	private static ClientDriver instance;
	
	private ClientDriver()
	{
		System.setProperty("java.security.policy", "logger.policy"); 
		
	}
	
	public static ClientDriver getClient()
	{
		if (instance == null) instance = new ClientDriver();
		return instance;
	}
	
	public Thread start()
	{
		if (thread == null)
		{
			thread = new Thread(this);
			keepRunning = true;
			thread.start();
		}
		return thread;
	}

	@Override
	public void run() {
		try
		{
			// make sure page controller gets the database from the RMI registry
			PageController.initializeUserDatabase();
			
			// get the name of the current user
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please Enter Name of Current User:\n");
			currUser = in.readLine();
			
			// check if user has a page, if not, create one for user
			if (!PageController.checkForUser(currUser))
			{
				PageController.createUserPage(currUser);
			}
			
			String options = "Menu:\n"
					+ "1. Set Background Color of User Page\n"
					+ "2. Set Log of User Page\n"
					+ "3. Exit Program\n";
			
			String input, user;
			int selection = -1;
			while(keepRunning)
			{
				// get action from user
				System.out.println(options);
				input = in.readLine();
				selection = Integer.parseInt(input);
				
				switch(selection)
				{
					// background color
					case 1:
						System.out.println(">[Set Background Color]\n");
						
						System.out.println("User For Background Color Change:\n");
						input = in.readLine();
						
						// check for other user
						if (!PageController.checkForUser(input) 
								|| input.equals(currUser))
						{
							System.out.println(INVALID_USER_ERROR);
							break;
						} else
						{
							user = input;
							System.out.println(
									"Enter New Background Color"
									+ " (#XXXXXX):\n");
							input = in.readLine();
							PageController.updateBackgroundColor(user, 
									input, currUser);
							PageController.updateUserHtml(user);
						}
						
						break;
					// log
					case 2:
						System.out.println(">[Set Log]\n");
						
						System.out.println("User For Log Change:\n");
						input = in.readLine();
						
						// check for other user
						if (!PageController.checkForUser(input) 
								|| input.equals(currUser))
						{
							System.out.println(INVALID_USER_ERROR);
							break;
						} else
						{
							user = input;
							System.out.println("Enter New User Log:\n");
							input = in.readLine();
							PageController.updateLog(user, input, currUser);
							PageController.updateUserHtml(user);
						}
						
						break;
					// exit
					case 3:
						System.out.println("Exiting program...\n");
						Thread.sleep(1000);
						keepRunning = false;
						break;
					default:
						System.out.println(
								"Cannot process selection,"
								+ " please try again...\n");
						break;
				}
				
				if (selection != 3 && selection != -1)
				{
					System.out.println("Press [Enter] to Continue...");
					in.readLine();
	//				System.out.print("\033[H\033[2J");
	//				System.out.flush();
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		thread.interrupt();
		keepRunning = false;
		thread = null;
	}

}
