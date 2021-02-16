package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

import management.Settings;
import panel.Frame;

public class Connection extends Thread{
	
	public static String currentTime() {
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		return "["+timeStamp.getHours()+":"+timeStamp.getMinutes()+":"+timeStamp.getSeconds()+"]";
	}
	
	
	Socket connection;
	PrintWriter out;
	Scanner in;
	Thread listener;
	
	boolean authenticated = false;
	
	public String name;
	public int howToHandle;
	
	private ArrayList<Integer> value = new ArrayList<>();
	
	private ArrayList<String> triggerList = new ArrayList<>();
	
	public String lastPingTime = "[no ping receaved yet...]";
	
	public ArrayList<Integer> getValue() {
		return value;
	}
	
	public String getValueAsString() {
		String toReturn = "";
		for(int i = 0; i<value.size();i++) {
			toReturn+= " "+value.get(i);
		}
		return toReturn;
	}
	
	public Connection(Socket connection) {
		this.connection = connection;
		try {
			out = new PrintWriter(connection.getOutputStream());
			in = new Scanner(connection.getInputStream());
		} catch (IOException e) {e.printStackTrace();}
		
		listener = new Thread(this);
		listener.start();
		
		
		
	}
	
	@Override
	public void run() {
		
		while(true) {
			try {
			String rcv = in.nextLine();
			if(Settings.transmission_print)
				System.out.println("--> "+rcv);
			String[] args = rcv.split(Settings.transmission_seperator);
			
			
			if(authenticated) {
				
				if(args[0].equalsIgnoreCase("set")) {
					int receavedValue = Integer.valueOf(args[1]);
					
					for(Connection cc : Server.getConnections()) {
						if(cc.howToHandle==-1) {	//Receaver
							if(cc.triggerList.contains(name)) {
								cc.sendMessage("triggered:"+name+":"+String.valueOf(receavedValue));
							}
						}
					}
					
					switch (howToHandle) {
					case ValueHandeler.SAVE_VAR:
							if(value.size()!=0)
								value.set(0, receavedValue);
							else
								value.add(receavedValue);
							break;
						
					case ValueHandeler.SAVE_ARRAY:
						value.add(receavedValue);
						break;
					
					case ValueHandeler.LOGFILE_VAR:
						try {throw new Exception("function not built!");} catch (Exception e) {e.printStackTrace();}
						break;
						
					case ValueHandeler.LOGFILE_ARRAY:
						try {throw new Exception("function not built!");} catch (Exception e) {e.printStackTrace();}
						break;
					
					case ValueHandeler.RECEAVER_CLIENT:
						System.err.println("A ReceaverClient just sent a value tag");
						break;

					default:
						break;
					}
					
				}
				
				if(args[0].equalsIgnoreCase("trigger")) {
					triggerList.add(args[1]);
					System.out.println(name + " trigger add "+args[1]);
					sendMessage("successfully added trigger "+args[1]);
				}
				
				if(args[0].equalsIgnoreCase("ping")) {
					lastPingTime = currentTime();
				}
				
				if(args[0].equalsIgnoreCase("list")) {
					String answer = "";
					for(Connection cc : Server.getConnections())
						answer+=(cc.name+""+cc.lastPingTime+""+" ==> "+cc.getValueAsString()+"\n");
					sendMessage(answer);
				}
				
				if(args[0].equalsIgnoreCase("get")) {
					
					
					
					String reqestedName = args[1];
					if(Server.getValueByName(reqestedName)!=null) {
						ArrayList<Integer> foundValue = Server.getValueByName(reqestedName);
						
						String answer = "";
						
						
//						answer+= "{";
//						answer+= "MODE: "+howToHandle;
//						answer+=",";
//						answer+="Values: ";
//						answer+="[";
//						for(int i = 0; i<foundValue.size();i++) {
//							
//						}
//						answer+="]";
//						answer+= "}";
						
						
						answer+=String.valueOf(Server.getConnectionByName(reqestedName).howToHandle);
						answer+="|";
						for(int i = 0; i<foundValue.size();i++) {
							answer += " "+foundValue.get(i);
						}
						answer.replaceFirst(" ", "");
						
						System.out.println("Answer:");
						System.out.println(answer);
						sendMessage(answer);
						
					}
				}
					
			}else {
				//Login
				//Syntax: login:{password}:{device_name}:{data_handeling}
				if(args[0].equalsIgnoreCase("login")) {
					String password = args[1];
					String deviceName = args[2];
					int dataHandeling = Integer.valueOf(args[3]);
					
					if(password.equalsIgnoreCase(Settings.login_key)) {
						name = deviceName;
						howToHandle = dataHandeling;
						
						if(Server.getConnectionByName(deviceName)==null) {
							authenticated = true;
							System.out.println("Client "+deviceName+" successfully logged in!");
						}else {
							
							Server.getConnectionByName(deviceName).kill();
							
							Connection clone = new Connection(connection);
							
							clone.name = name;
							clone.howToHandle = howToHandle;
							clone.authenticated = true;
							
							Server.connections.add(clone);
							
							System.out.println("Client "+deviceName+" successfully relogged in!");
							kill();
						}
						
						
					}else {
						try {throw new Exception("Wrong Password");} catch (Exception e) {e.printStackTrace();}
					}
					
				}
				
			}
			Frame.setList(Server.getConnections());
		}catch (java.util.NoSuchElementException e) {
			System.err.println(name+" lost connection...");
			Thread.currentThread().stop();
		}
			
		}
		
	}
	
	public void kill() {
		System.out.println("Killing "+name+"...");
		Server.connections.remove(this);
		listener.stop();
	}
	
	public void sendMessage(String text) {
		out.println(text);
		out.flush();
		System.out.println("<-- "+text);
	}
	
}
