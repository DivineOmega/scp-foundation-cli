package network;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import main.Main;


public class Server extends Thread 
{
	int port;
	public ArrayList<ServerConnection> connections = new ArrayList<ServerConnection>();
	int incomingLimit = 400;
	
	public Server(int port)
	{
		this.port = port;
	}
	
	public void run()
	{
		ServerSocket serverSocket = null;
		try 
		{
			serverSocket = new ServerSocket(port, 0);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.out.println("Error starting server.");
			System.exit(0);
		}
		
		Socket socket;
		while (true)
		{
			if (this.connections.size()>=incomingLimit)
			{
				try 
				{
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				continue;
			}
			
			try 
			{
				socket = serverSocket.accept();
				ServerConnection incomingConnection = new ServerConnection(socket);
				Main.server1.connections.add(incomingConnection);
				incomingConnection.start();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				System.out.println("Error with server connection.");
			}
		}
	}
}
