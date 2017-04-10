package com.test.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.SocketHandler;

public class BaseServer extends Thread{

	private static final int DEFAULT_OPRT =9000;
	private final int port ;
	
	public BaseServer(final int port)
	{
		this.port=port;
	}
	
	public void run(){
			ServerSocket serverSocket;
			try {
				serverSocket = new ServerSocket(this.port);
				while(true){
					Socket clientSocket = serverSocket.accept();
					new SocketHandler(clientSocket).start();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	}
	
	
	public static void main(String[] args) {
		new BaseServer(DEFAULT_OPRT).start();
		System.out.println("Server started on port :"+DEFAULT_OPRT);
	}
	
	
	
	class SocketHandler extends Thread {
		private final Socket socket;
		public SocketHandler(final Socket socket){
			this.socket=socket;
		}
		
		public void run(){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
				System.out.println("Client connected");
				while (true)
				{
					String receiveString = reader.readLine();
					System.out.println("Server receive:"+receiveString);
					if("Close".equalsIgnoreCase(receiveString))
					{
						writer.println("Connection closed");
						writer.flush();
						reader.close();
						writer.close();
						break;
					}else
					{
						writer.println("------->"+receiveString);
						writer.flush();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
