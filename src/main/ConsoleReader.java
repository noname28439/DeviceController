package main;

import java.util.Scanner;

import server.Connection;
import server.Server;

public class ConsoleReader extends Thread{

	public static void load() {
		Thread t = new Thread(new ConsoleReader());
		t.start();
	}
	
	@Override
	public void run() {
		Scanner in = new Scanner(System.in);
		while(true) {
			String rcv = in.nextLine();
			if(rcv.equalsIgnoreCase("list")) {
				for(Connection cc : Server.getConnections())
					System.out.println(cc.name+"["+cc.lastPingTime+"]"+" ==> "+cc.getValueAsString());
			}
		}
		
		
	}
	
}
