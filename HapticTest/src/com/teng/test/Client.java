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
    
    TemperatureTest temperatureTest;
    PressureTest pressureTest;
    VibrationTest vibrationTest;
    int testIndex = 0;
    
    public Client(String addr, int port, TemperatureTest instance) {
        dstAddress = addr;
        dstPort = port;
        temperatureTest = instance;
        testIndex = 3;
        
        Thread socketClientThread = new Thread(new SocketClientThread());
        socketClientThread.start();
    }
    
    public Client(String addr, int port, PressureTest instance) {
        dstAddress = addr;
        dstPort = port;
        pressureTest = instance;
        testIndex = 1;
        
        Thread socketClientThread = new Thread(new SocketClientThread());
        socketClientThread.start();
    }
    
    public Client(String addr, int port, VibrationTest instance) {
        dstAddress = addr;
        dstPort = port;
        vibrationTest = instance;
        testIndex = 2;
        
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
					if(testIndex == 1)
					{
						if(values.length > 0)
						{
							if(values[0].equals("s")) {
								//copy the trial sequence
								pressureTest.levels =  Integer.parseInt(values[1]);
								pressureTest.totalTrials = Integer.parseInt(values[2]);
								pressureTest.trial = Integer.parseInt(values[3]);
								for(int itr = 0; itr < pressureTest.totalTrials ; itr++)
								{
									pressureTest.trialSequence.add(Integer.parseInt(values[itr + 4] ));
								}
								
								pressureTest.isTrialSequenceSet = true;
							}else if(values[0].equals("sos"))
							{
								//stop
								pressureTest.sosAction();
							}
							
						}
					}else if(testIndex == 2)
					{
						if(values.length > 0)
						{
							if(values[0].equals("s"))
							{
								//copy the trial sequence
								vibrationTest.levels =  Integer.parseInt(values[1]);
								vibrationTest.totalTrials = Integer.parseInt(values[2]);
								
								for(int itr = 0; itr < vibrationTest.totalTrials ; itr++)
								{
									vibrationTest.trialSequence.add(Integer.parseInt(values[itr + 3] ));
								}
								
								vibrationTest.isTrialSequenceSet = true;
							}
							
						}
					}else if(testIndex == 3)
					{
						if(values.length > 0)
						{
							if(values[0].equals("s"))
							{
								//copy the trial sequence
								temperatureTest.levels =  Integer.parseInt(values[1]);
								temperatureTest.totalTrials = Integer.parseInt(values[2]);
								
								for(int itr = 0; itr < temperatureTest.totalTrials ; itr++)
								{
									temperatureTest.trialSequence.add(Integer.parseInt(values[itr + 3] ));
								}
								
								temperatureTest.isTrialSequenceSet = true;
							}
							
						}
						
					}
					
					
					
					
					
				}catch (IOException e) {
                    //e.printStackTrace();
                }
			}
		}
	}
    

	
}
