package com.teng.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class Server {
	static final int socketServerPORT = 9090;
	public ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintStream printStream;
	private OutputStream outputStream;
	
	private ArrayList<Integer> byteArray;
	private boolean receivingMode = false;
	
	private String message = "";
	private String activityTag;
	
	public Server() throws IOException
	{
		byteArray = new ArrayList<Integer>();
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
	}
	
	public void onDestroy()
	{
		if(serverSocket != null)
		{
			try{
				if(printStream != null)
					printStream.close();
				serverSocket.close();
				System.out.println("server closed");
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private class SocketServerThread extends Thread {
		
		@Override
		public void run()
		{
			try{
				serverSocket = new ServerSocket(socketServerPORT);
				//incoming socket connection
				clientSocket = serverSocket.accept();
				message = "#1" + " from "
                        + clientSocket.getInetAddress() + ":"
                        + clientSocket.getPort() + "\n";
				
				System.out.println(message);
				
				outputStream = clientSocket.getOutputStream();
				printStream = new PrintStream(outputStream);
				
				//reply a connection confirmation
				//SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread();
            	//socketServerReplyThread.run();
				
            	//keep listening
            	SocketServerReceiveThread socketServerReceiveThread = new SocketServerReceiveThread();
            	socketServerReceiveThread.run();
            	
			}catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			
		}
	}
	
	private class SocketServerReplyThread extends Thread {

        SocketServerReplyThread() {
            
        }

        @Override
        public void run() {
            String msgReply = "Hello from Server";
            printStream.print(msgReply);
        } 
    }
	
	
	private class SocketServerSendThread extends Thread {
		String msgSend;
		
        SocketServerSendThread(String msg) {
        	msgSend = msg;
        }

        @Override
        public void run() {
        	printStream.print(msgSend);
        } 
    }
	
	public void sendMessage(String msg)
	{
		SocketServerSendThread socketServerSendThread = new SocketServerSendThread(msg);
    	socketServerSendThread.run();
	}
	
	private class SocketServerReceiveThread extends Thread {
		public boolean keepReading = true;
		//private BufferedInputStream input;
		//private InputStreamReader input;
		private BufferedReader input;
		private String msg;
		private String[] values;
		
		public SocketServerReceiveThread()
		{
			try {
				//this.input = new BufferedInputStream(clientSocket.getInputStream());
				this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run()
		{
			while(!Thread.currentThread().isInterrupted() && keepReading)
			{
				try {
					if(clientSocket.isConnected())
					{
						
					}else
					{
						keepReading = false;
						System.out.println("connection lost");
						break;
					}
					
					//int byteRead = input.read();  
					//byteArray.add(byteRead);
					
					//lets read string
					if(input != null)
					{
						if((msg = input.readLine()) != null)  //when client lost, will read null
						{
							//System.out.println(msg);
							values = msg.split(",");
						}else
						{
							keepReading = false;
							continue;
						}
					}
					
		
					//do sth
					
					
			
					
				}catch (IOException e) {
                    e.printStackTrace();
                }
			}
		}
	}
	
	
	public String getIpAddress() {
        String ip = "get ip address ";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();

                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    //if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                //+ inetAddress.getHostAddress();
                                    +inetAddress.toString();
                    //}
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}
