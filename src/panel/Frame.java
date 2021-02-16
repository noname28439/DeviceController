package panel;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import server.Connection;
import server.Server;

public class Frame {

	public static JFrame frame;
	public static JPanel panel;
	public static JList<String> connectionList = new JList<>();
	
	public static void load() {
		
		frame = new JFrame();
		panel = new JPanel();
		
		panel.add(connectionList);
		
		
		frame.add(panel);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setTitle("Main IOT Controller   ["+"]");
		frame.setVisible(true);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try { Thread.currentThread().sleep(500); } catch (InterruptedException e) {e.printStackTrace();}
					frame.setTitle("Main IOT Controller   "+Connection.currentTime());
				}
			}
		}).start();
		
		
		System.err.println("OPEN-ENDPOINT: Frame loading...");
	}
	
	public static void setList(ArrayList<Connection> list) {
		
		String[] newList = new String[list.size()];
		for(int i = 0; i<list.size();i++) {
			Connection cc = list.get(i);
			newList[i]=(cc.name+""+cc.lastPingTime+""+" ==> "+cc.getValueAsString());
		}
			
		
		connectionList.setListData(newList);
		
	}
	
}
