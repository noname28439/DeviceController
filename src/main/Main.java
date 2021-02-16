package main;

import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;

import javax.swing.JFrame;
import javax.swing.JPanel;

import panel.Frame;
import server.Server;

public class Main {
	
	public static String currentTime() {
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		return "["+timeStamp.getHours()+":"+timeStamp.getMinutes()+":"+timeStamp.getSeconds()+"]";
	}
	
	
	public static void main(String[] args) {
		System.setOut(new ModifiedPrintStream(System.out));
		ConsoleReader.load();
		Frame.load();
		Server.load();
	}
	
	

}

class ModifiedPrintStream extends PrintStream {
	OutputStream out;
	public ModifiedPrintStream(OutputStream out) {
		super(out);
		this.out = out;
	}

	@Override
	public void println(String text) {
		super.println(Main.currentTime()+" "+text);
	}
	
}
