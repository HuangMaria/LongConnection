package com.test.NIOsocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
	//no thread need here 
	private static final int NIO_SERVER_PORT =9001;
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private ByteBuffer buffer;
	
	public void init() throws IOException{
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);//must be non-blocking 
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(NIO_SERVER_PORT));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		buffer = ByteBuffer.allocate(1024);
	}
	
	public void process () throws IOException{
		while(true)
		{
			int keys = selector.select(); //blocked
			if(keys<=0)
				continue;
			Set readyKeys = selector.selectedKeys();
			Iterator it = readyKeys.iterator();
			while(it.hasNext())
			{
				 SelectionKey key = (SelectionKey) it.next();
				it.remove();
				if (key.isAcceptable()) {
					this.accept(key);
					System.out.println("Client connected.");
				}else if (key.isReadable()) {
					SocketChannel channel = (SocketChannel) key.channel();
					String receiveString = this.read(channel);
					System.out.println("Server Recive: "+receiveString);
					String responseString ="----->" +receiveString;
					this.write(channel,responseString);
				}
			}
		}
	}
	
	
	private void accept(final SelectionKey key) throws IOException{
		SocketChannel clientChannel = ((ServerSocketChannel)key.channel()).accept();
		if (clientChannel != null) {
			clientChannel.configureBlocking(false);
			clientChannel.register(selector, SelectionKey.OP_READ);
		}
	}
	
	
	private String read(final SocketChannel channel) throws IOException{
		buffer.clear();// not actually erase the data
		int numRead = channel.read(buffer);//once
		byte[] newBytes = new byte[numRead];
		System.arraycopy(buffer.array(), 0, newBytes, 0, numRead);
		String receiveString = new String(newBytes);
		return receiveString;
	}
	
	
	private void write(final SocketChannel channel,final String resp) throws IOException{
		ByteBuffer responseBuffer = ByteBuffer.wrap(resp.getBytes());
		while(responseBuffer.hasRemaining()){
			channel.write(responseBuffer);
		}
	}
	
	
	public static void main(String[] args) {
		NIOServer server = new NIOServer();
		try {
			server.init();
			server.process();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
