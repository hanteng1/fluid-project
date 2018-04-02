package com.teng.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataCollectionStepOne {

	public BufferedReader br;
	private String splitBy = ",";
	public String line = "";
	
	private String dataPath;
	public DataStorage dataStorage;
	
	
	
	public DataCollectionStepOne()
	{
		dataStorage = DataStorage.getInstance();
		
		for(int itr = 1; itr <= 12; itr++)
		{
			dataPath = "/Users/hanteng/Dropbox/fluiddatafromserver/user_" + itr;
			
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
						while((line = br.readLine()) != null)
						{
							String[] values = line.split(splitBy);
							
							if(values.length == 13)
							{
								//testing data log
								
							}else if(values.length == 5)
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
		
		
	
	}
	
	
}
