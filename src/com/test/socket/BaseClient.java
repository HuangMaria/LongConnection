package com.test.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class BaseClient {
	
	private Socket socket = null;
	public void connect(String host , int port) throws Exception{
		if(socket==null)
		{
			this.socket=new Socket(host, port);
			this.socket.setKeepAlive(true);
			System.out.println("Connected.");
		}
	}
	
	
	public void process() throws Exception{
		assert socket !=null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			System.out.print("Input: ");
			String inputString = input.readLine();
			writer.println(inputString);
			writer.flush();
			
			String responseString = reader.readLine();
			System.out.println("Server response: "+responseString);
			if ("close".equalsIgnoreCase(inputString)) {
				this.socket.close();
				break;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		BaseClient client = new BaseClient();
		client.connect("127.0.0.1", 9000);
		client.process();
	}
}
