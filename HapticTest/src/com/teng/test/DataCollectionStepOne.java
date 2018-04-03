package com.teng.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class DataCollectionStepOne {

	public BufferedReader br;
	private String splitBy = ",";
	public String line = "";
	private String dataPath;
	
	
	
	public DataCollectionStepOne()
	{
		
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("user,trial, sensation,levels,target,answer,time,correct,\r\n");
		
		for(int itr = 1; itr <= 12; itr++)
		{
			//dataPath = "/Users/hanteng/Dropbox/fluiddatafromserver/user_" + itr;
			dataPath = "C:\\Users\\t_hant\\Dropbox\\fluiddatafromserver\\user_" + itr;
			
			File dir = new File(dataPath);
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
			    for (File child : directoryListing) {	
			    	
			    	String filePath = child.getAbsolutePath();
			    	
					try {
						br = new BufferedReader(new FileReader(filePath));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try{
						
						int totalTrials = 0;
						int correctTrials = 0;
						while((line = br.readLine()) != null)
						{
							String[] values = line.split(splitBy);
							
							if(values.length == 9)
							{
								//testing data log
								stringbuilder.append("" + itr + "," + values[0] + "," 
										+ values[1] + "," 
										+ values[2] + "," 
										+ values[3] + "," 
										+ values[4] + "," 
										+ values[5] + "," 
										+ values[6] + "," 
										+ "\r\n");
								
							}else if(values.length == 2)
							{
								//confidence level
								
							}
						}
					}catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			    }
			} else {
			    System.out.print("not a dir");
			  }

			
		}
		

		//save the data to a single file
		File dir = new File("C:\\Users\\t_hant\\Dropbox\\fluiddatafromserver");
        String filename = "study_2_all_data.csv";

        File file = new File(dir, filename);

        if(!dir.exists())
        {
            dir.mkdir();
        }

        try
        {
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter( new FileOutputStream(file, true));

            outputstreamwriter.write( stringbuilder.toString() );
            outputstreamwriter.close();
            System.out.println("write data completes.");

        } catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(e.toString());
        }
		
		
	
	}
	
	
	public static final void main(String args[])
	{
		DataCollectionStepOne data = new DataCollectionStepOne();
	}
	
	
}
