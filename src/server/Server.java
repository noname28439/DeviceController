package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import management.Settings;

public class Server {

	static ArrayList<Connection> connections = new ArrayList<>();
	
	static ServerSocket server;
	
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
