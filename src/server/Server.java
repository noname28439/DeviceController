package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import management.Settings;

public class Server {

	static ArrayList<Connection> connections = new ArrayList<>();
	
	static ServerSocket server;
	
	public static ArrayList<Connection> getConnections() {
		ArrayList<Connection> toReturn = new ArrayList<>();
		for(Connection cc : connections)
			if(cc.authenticated)
				toReturn.add(cc);
		
		return toReturn;
	}
	
	public static Connection getConnectionByName(String name) {
		ArrayList<Connection> connections = getConnections();
		for(Connection cc : connections)
			if(cc.name.equalsIgnoreCase(name))
				return cc;
		return null;
	}
	
	
	
	public static ArrayList<Integer> getValueByName(String name) {
		if(getConnectionByName(name)!=null) {
			return getConnectionByName(name).getValue();
		}
		return null;
	}
	
	public static void load() {
		try {
			server = new ServerSocket(Settings.server_port);
		} catch (IOException e) {System.out.println("ERROR: Failded to initialize Server!");e.printStackTrace();}
		System.out.println("Server loaded on port "+Settings.server_port+"...");
		
		//Main Connection Listener (Accepts all Connections)
		while(true) {
			try {
				Socket currentConnection = server.accept();
				System.out.println("Connected ["+currentConnection.getInetAddress().toString()+"]");
				connections.add(new Connection(currentConnection));
			} catch (IOException e) {e.printStackTrace();}
		}
		
		
	}
	
	
}
