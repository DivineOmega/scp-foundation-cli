package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import main.Main;

public class ServerConnection extends Thread 
{
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	DataOutputStream binaryOut;
	boolean threadDone = false;
	File logFile; 
	PrintWriter logFileOut;
	
	public ServerConnection(Socket socket)
	{
		this.socket = socket;
	}
	
	public void run()
	{
		System.setProperty("line.separator", "\r\n");
		
		try 
		{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			binaryOut = new DataOutputStream(socket.getOutputStream());
			socket.setSoTimeout(300000);
			logFile = new File("scpcli_"+socket.getInetAddress().getHostAddress()+".log");
			logFileOut = new PrintWriter(logFile);
		}
		catch (IOException e) 
		{
			threadDone = true;
			e.printStackTrace();
		}
		
		
		while (!threadDone)
		{
			if (Math.random()*100 > 95)
			{
				sendLine("");
				sendLine("Remote connection from your location is denied. This action occurs in regulatory compliance with the Foundation's secure systems policy.");
				threadDone = true;
				continue;
			}
			
			sendLine("");
			sendLine("Foundation Terminal access:");
			
			if (Math.random()*100 > 75)
			{
				sendLine("");
				sendLine("Unauthorized access is strictly prohibited and violators are subject to prosecution to the full extent of the law.");
			}
			
			if (Math.random()*100 >95)
			{
				sendLine("");
				sendLine("Error enumerating available network services. This terminal requires maintenance. Please contact your system administrator.");
				threadDone = true;
				continue;
			}
			
			sendLine("");
			sendLine("Internal networking connection: "+socket.getInetAddress().toString()+":"+socket.getPort());
			
			if (Math.random()*100 > 50)
			{
				sendLine("");
				sendLine("WARNING: This Foundation terminal is running in maintenance mode. Please alert your system administrator.");
			}
			
			sendLine("");
			sendPrompt("LOGIN: ");
			String login = receive();
			if (login.equals("") || login.equalsIgnoreCase("idle"))
			{
				threadDone = true;
				continue;
			}
			sendPrompt("PASSW: ");
			String passwd = receive();
			if (passwd.equals("") || passwd.equalsIgnoreCase("idle"))
			{
				threadDone = true;
				continue;
			}
			sendLine("");
			
			if (Math.random()*100 > 95)
			{
				sendLine("LOGIN FAILED: Base account details not valid and/or maintenance mode terminal can not initiate login.");
				sendLine("Disconnection will occur in regulatory compliance with the Foundation's secure systems policy.");
				threadDone = true;
				continue;
			}
			
			sendLine("LOGIN OK.");
			
			if (Math.random()*100 > 75)
			{
				sendLine("ERROR: Roaming profile corruption and/or a local profile configuration error has occured. Please contact your system administrator.");
			}
			
			if (Math.random()*100 > 75)
			{
				sendLine("");
				sendPrompt("2ND FACTOR CODE: ");
				String secondfactor = receive();
				if (secondfactor.equals("") || secondfactor.equalsIgnoreCase("idle"))
				{
					threadDone = true;
					continue;
				}
			}
			
			sendLine("");
			sendLine("Welcome to Foundation terminal: XEEE-15-2.a");
			sendLine("Your session has been initialized in compliance with the Foundation's secure systems policy.");
			
			String cmd = "";
			String lookup = "";
			String policy = "";
			String article = "";
			int invalidCommandCount = 0;
			while (!threadDone)
			{
				sendLine("");
				sendPrompt("SYSCMD: ");
				cmd = receive();
				sendLine("");
				
				if (cmd.toLowerCase().startsWith("help"))
				{
					lookup = cmd.substring("help".length()).trim();
					
					if (lookup.isEmpty())
					{
						sendLine("XEEE-15-2.a: [Maintenance] System Commands: HELP REPORT POLICY EXIT");
					}
					else if (lookup.equalsIgnoreCase("report"))
					{
						sendLine("XEEE-15-2.a: Retrieval of standard containment procedure report based on identifier.");
						sendLine("Usage:       'REPORT SCP-XXX' or 'REPORT-SCP-XXXX'");
					}
					else if (lookup.equalsIgnoreCase("policy"))
					{
						sendLine("XEEE-15-2.a: Foundation library listing and view access to internal policy documents.");
						sendLine("Usage:       'POLICY' to present document selection menu.");
					}
					else if (lookup.equalsIgnoreCase("exit"))
					{
						sendLine("XEEE-15-2.a: Connection termination and regular personnel log out procedure.");
						sendLine("Usage:       'EXIT'");
					}
					else
					{
						sendLine("XEEE-15-2.a: Help not avaialble for this command.");
					}
				}
				else if (cmd.toLowerCase().startsWith("report"))
				{
					lookup = cmd.substring("report".length()).trim();
					
					if (lookup.isEmpty())
					{
						sendPrompt("REPORT: ");
						lookup = receive();
						sendLine("");
					}
					
					if (lookup.trim().startsWith("0") || lookup.trim().startsWith("1") || lookup.trim().startsWith("2") || lookup.trim().startsWith("3") ||
							lookup.trim().startsWith("4") || lookup.trim().startsWith("5") || lookup.trim().startsWith("6") || lookup.trim().startsWith("7") ||
							lookup.trim().startsWith("8") || lookup.trim().startsWith("9"))
							{
								lookup = "scp-"+lookup;
							}
					
					if (lookup.trim().toLowerCase().startsWith("scp-"))
					{
						sendLine("Searching database...");
						sendLine("");
						article = getArticle(lookup);
						if (article.equals("")) 
						{
							sendLine("Not found. Verify your search parameter is correct.");
						}
						else 
						{
							sendLine("Data object found. Formatting data output stream...");
							sendLine("* Data object output begins.");
							sendLine("");
							sendLine(article);
							sendLine("");
							sendLine("* Data object output ends.");
						}
					}
					else if (lookup.trim().equalsIgnoreCase("random"))
					{
						sendLine("Searching database...");
						
						article = "";
						while (article=="")
						{
							lookup = "SCP-";
							if (Math.random()*100 > 50) lookup += "1";
							lookup += Integer.toString((int) (Math.random()*9));
							lookup += Integer.toString((int) (Math.random()*9));
							lookup += Integer.toString((int) (Math.random()*9));
							sendLine("Query for report identifier: "+lookup);
							article = getArticle(lookup);
						}
						sendLine("");
						sendLine("Data object found. Formatting data output stream...");
						sendLine("* Data object output begins.");
						sendLine("");
						sendLine(article);
						sendLine("");
						sendLine("* Data object output ends.");
						
					}
					else if (lookup.equalsIgnoreCase("idle"))
					{
						sendLine("");
						sendLine("Report: Internal command error. Database connectivity expired due to internal gateway timeout (failure event at dataset branch level 406Q-9).");
						continue;
					}
					else if (lookup.equals(""))
					{
						sendLine("Data object search notation invalid. Null system name not permitted for report.");
					}
					else
					{
						sendLine("Invalid search parameter. Report name using standardised Foundation notation (SCP-XXX or SCP-XXXX) is required.");
					}
				}
				else if (cmd.equalsIgnoreCase("policy"))
				{
					sendLine("Documents regarding SCP operations, containment procedures, policies and miscellaneous information:");
					sendLine("");
					sendLine("POLICY-ABOUTF - SCP Foundation overview"); // about-the-scp-foundation
					sendLine("POLICY-FRONTS - Fronts to be used in case of civilian investigation"); // scp-fronts
					sendLine("POLICY-TASKFS - Active SCP task forces"); // task-forces
					sendLine("POLICY-SECCLR - Security clearance and compliance information"); // security-clearance-levels
					sendLine("POLICY-SECFAC - Secure facilities geographic and functional information"); // secure-facilities-locations
					sendLine("POLICY-OBJCLS - Object classifications document"); // scp-object-classes
					sendLine("POLICY-REPORT - Incident reports, eye witeness interviews and personal logs"); // incident-reports-eye-witness-interviews-and-personal-logs
					sendLine("POLICY-GROUPS - Groups of SCP interest and relevant summary information"); // groups-of-interest
					sendLine("");
					sendPrompt("POLICY: ");
					policy = receive();
					sendLine("");
					
					if (policy.equalsIgnoreCase("idle"))
					{
						sendLine("Policy: Internal command error. Database connectivity expired due to internal gateway timeout (failure event at dataset branch level 513P-8).");
						continue;
					}
					else if (policy.equals(""))
					{
						sendLine("Data object search notation invalid. Null system name not permitted for policy.");
						continue;
					}					
					else if (!policy.trim().toLowerCase().startsWith("policy-"))
					{
						policy = "policy-"+policy;
					}
					
					sendLine("Please wait...");
					sendLine("");
					
					if (policy.equalsIgnoreCase("POLICY-ABOUTF")) article = getArticle("about-the-scp-foundation");
					else if (policy.equalsIgnoreCase("POLICY-FRONTS")) article = getArticle("scp-fronts");
					else if (policy.equalsIgnoreCase("POLICY-TASKFS")) article = getArticle("task-forces");
					else if (policy.equalsIgnoreCase("POLICY-SECCLR")) article = getArticle("security-clearance-levels");
					else if (policy.equalsIgnoreCase("POLICY-SECFAC")) article = getArticle("secure-facilities-locations");
					else if (policy.equalsIgnoreCase("POLICY-OBJCLS")) article = getArticle("scp-object-classes");
					else if (policy.equalsIgnoreCase("POLICY-REPORT")) article = getArticle("incident-reports-eye-witness-interviews-and-personal-logs");
					else if (policy.equalsIgnoreCase("POLICY-GROUPS")) article = getArticle("groups-of-interest");
					else article = "";
					
					sendLine("");
					if (article.equals(""))
					{
						sendLine("Policy: Mismatched policy data object identifier. Confirm policy format data paramter is valid based on provided listing.");
					}
					else
					{
						sendLine("Policy object "+policy.toUpperCase()+" retrieved from server.");
						sendLine("* Data object output begins.");
						sendLine("");
						sendLine(article);
						sendLine("");
						sendLine("* Data object output ends.");
					}
				}
				else if (cmd.equalsIgnoreCase("idle"))
				{
					sendLine("");
					sendLine("Security alert: Your connection is being terminated in regulatory compliance with Foundation secure systems policy section. 0x0085317.A3-52XX.629...");
					threadDone = true;
					continue;
				}
				else if (cmd.equalsIgnoreCase("exit"))
				{
					sendLine("Logout complete. Terminating connection...");
					threadDone = true;
					continue;
				}
				else
				{
					invalidCommandCount++;
					if (invalidCommandCount>25)
					{
						sendLine("Invalid command. Maximum exceeded. Protocol error.");
						threadDone = true;
					}
					else
					{
						sendLine("Invalid command.");
					}
				}
			}
			
		}
		
		sendLine("");
		sendLine("");
		sendLine("CONNECTION TERMINATED. Error code: 0x00859FE"+Math.random()*1000);
		sendLine("");
		
		logFileOut.close();
		
		try 
		{
			socket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		if (!Main.server1.connections.remove(this))
		{
			System.out.println("Cleanup issue: Could not remove ServerConnection object from Server.connections list.");
			//System.exit(0);
		}
	}
	
	private void sendLine(String text)
	{
		send(text, true, true);
	}
	
	private void sendPrompt(String text)
	{
		send(text, false, false);
	}

	private String receive()
	{
		try 
		{
			String received = in.readLine();
			received = received.trim();
			received = processBackspace(received);
			if (received==null) received = "";
			logFileOut.write(received);
			logFileOut.write("\r\n");
			logFileOut.flush();
			return received;
		}
		catch (SocketTimeoutException e)
		{
			return "idle";
		}
		catch (IOException e) 
		{
			return "";
		}
	}
	
	private void send(String text, boolean newLine, boolean colonDelay)
	{
		logFileOut.write(text);
		if (newLine) logFileOut.write("\r\n");
		logFileOut.flush();
		
		try 
		{
			Thread.sleep((long) (250+Math.random()*250));
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		int x = 0;
		while (x<text.length())
		{
			out.print(text.charAt(x));
			out.flush();
			
			if (text.charAt(x)=='\r' || text.charAt(x)=='\n' || text.charAt(x)=='.' || (text.charAt(x)==':' && colonDelay))
			{
				try 
				{
					Thread.sleep((long) (250+Math.random()*250));
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			else if (text.charAt(x)==' ')
			{
				try 
				{
					Thread.sleep((long) (20));
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			else
			{
				try 
				{
					Thread.sleep((long) (20));
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			x++;
		}
		if (newLine) out.println();
		out.flush();
	}
	
	private String getArticle(String name)
	{
		URL scpProxy;
		URLConnection scpProxyConn;
		String output = "";
		String outputLine = "";
		try 
		{
			scpProxy = new URL("http://jordanhall.co.uk/scp-proxy.php?article="+name);
			scpProxyConn = scpProxy.openConnection();
			scpProxyConn.setConnectTimeout(1000*30);
			scpProxyConn.setReadTimeout(1000*30);
			scpProxyConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11");
	        BufferedReader scpProxyIn = new BufferedReader(new InputStreamReader(scpProxyConn.getInputStream()));
	        while ((outputLine = scpProxyIn.readLine()) != null) output += outputLine + System.getProperty("line.separator");
	        scpProxyIn.close();
		} 
		catch (MalformedURLException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return output;
	}
	
	private String processBackspace(String input) 
	{
	    StringBuilder sb = new StringBuilder();
	    for (char c : input.toCharArray()) 
	    {
	        if (c == '\b') 
	        {
	            if (sb.length() > 0) 
	            {
	                sb.deleteCharAt(sb.length() - 1);
	            }
	        } 
	        else 
	        {
	            sb.append(c);
	        }
	    }
	    return sb.toString();
	}
}
