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
    
    SocketClientReceiveThread socketClientReceiveThread;
    
    public Client(String addr, int port) {
        dstAddress = addr;
        dstPort = port;
        
        Thread socketClientThread = new Thread(new SocketClientThread());
        socketClientThread.start();
        
    }
    
    public void onDestroy()
    {
    	 if (socket != null) {
           try {
           	
        	if(printStream != null)
        	{
        	   printStream.close();
        	}
        	//outputStream.close();
           	//inputStream.close();
            socket.close();
            System.out.println("socket closed");
           } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
       }
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
                
                
                //run the receiving 
                socketClientReceiveThread = new SocketClientReceiveThread();
                socketClientReceiveThread.run();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
               
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
        
        	System.out.println("check point");
        } 
    }
    
    public void sendMessage(String msg)
    {
    	SocketClientSendThread socketClientSendThread = new SocketClientSendThread(msg);
    	socketClientSendThread.run();
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
					if(socket.isConnected() && !socket.isClosed())
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
							break;
						}
					}
					
		
					//do sth
				
					
				}catch (IOException e) {
                    //e.printStackTrace();
                }
			}
		}
	}
    

	
}
