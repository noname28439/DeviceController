package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import management.Settings;

public class Connection extends Thread{

	Socket connection;
	PrintWriter out;
	Scanner in;
	Thread listener;
	
	boolean authenticated = false;
	
	String name;
	int howToHandle;
	
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
			
			String rcv = in.nextLine();
			if(Settings.transmission_print)
				System.out.println("--> "+rcv);
			String[] args = rcv.split(Settings.transmission_seperator);
			
			
			if(authenticated) {
				
				if(args[0].equalsIgnoreCase("value")) {
					ValueHandeler.handleValue(Integer.valueOf(args[1]), howToHandle);
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
						authenticated = true;
						
					}else {
						try {throw new Exception("Wrong Password");} catch (Exception e) {e.printStackTrace();}
					}
					
				}
				
			}
			
		}
		
	}
	
	public void sendMessage(String text) {
		out.println(text);
		out.flush();
		System.out.println("<-- "+text);
	}
	
}
