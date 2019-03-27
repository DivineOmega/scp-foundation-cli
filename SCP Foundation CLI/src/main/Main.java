package main;

import java.util.Properties;

import network.Server;

public class Main 
{
	public static Server server1;
	public static Properties settings = new Properties();
	
	public static void main(String[] args)
	{		
		System.out.println("*** SCP Foundation CLI ***");
		
		System.out.println("Starting server on port 2222...");
		server1 = new Server(2222);
		server1.start();

		System.out.println("Start up complete.");
		
		while(true)
		{
			try 
			{
				Thread.sleep(5000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
			System.out.println("Active connections: "+Main.server1.connections.size());
		}
	}

}
