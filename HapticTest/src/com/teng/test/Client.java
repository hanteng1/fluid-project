package com.teng.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
	
	public String dstAddress;
    public int dstPort;
    String response = "";

    private Socket socket = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private PrintStream printStream;
    
    public Client(String addr, int port) {
        dstAddress = addr;
        dstPort = port;
    }
    
    public class SocketClientThread extends Thread{
    	
    	@Override
		public void run()
		{
    		try {
                socket = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                        1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                inputStream = socket.getInputStream();  //receive message 
                outputStream =  socket.getOutputStream();
                printStream = new PrintStream(outputStream);
            
             /*
              * notice: inputStream.read() will block if no data return
              */
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    byteArrayOutputStream.write(buffer, 0, bytesRead);
//                    response += byteArrayOutputStream.toString("UTF-8");
//                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                    	printStream.close();
                    	outputStream.close();
                    	inputStream.close();
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
		}
    }
    
    
    private class SocketClientSendThread extends Thread {
    	String msgSend;
    	SocketClientSendThread(String msg)
    	{
    		msgSend = msg;
    	}
    	
    	@Override
        public void run() {
        	printStream.print(msgSend);
        } 
    }
    
    
    
    public void sendMessage(String msg)
    {
    	SocketClientSendThread socketServerSendThread = new SocketClientSendThread(msg);
    	socketServerSendThread.run();
    }
    
    private class SocketClientReceiveThread extends Thread {
		public boolean keepReading = true;
		private BufferedReader input;
		private String msg;
		private String[] values;
		
		public SocketClientReceiveThread()
		{
			this.input = new BufferedReader(new InputStreamReader(inputStream));
		}
		
		@Override
		public void run()
		{
			while(!Thread.currentThread().isInterrupted() && keepReading)
			{
				try {
					if(socket.isConnected())
					{
						
					}else
					{
						keepReading = false;
						System.out.println("connection lost");
						break;
					}
					
					//lets read string
					if(input != null)
					{
						if((msg = input.readLine()) != null)  //when lost, will read null
						{
							System.out.println(msg);
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
    

	
}
