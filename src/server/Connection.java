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
	Object value;
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
				
			}else {
				//Login
				//Syntax: login:{password}:{device_name}:{data_type}:{data_handeling}
			}
			
		}
		
	}
	
	public void sendMessage(String text) {
		out.println(text);
		out.flush();
		System.out.println("<-- "+text);
	}
	
}
