package com.teng.test;

import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class DataStorage {
	
	public int userId = 0;
	public String sensation;
	public int levels = 0;
	
	public DataStorage()
    {
        samples = new ArrayList<DataSample>(100000);
    }

    //for study 2
    public static boolean AddSample(int _trial, int _sensation, int _levels, int _target, int _answer, long _responseTime,
            int _iscorrect, float _targetValue, float _answerValue)
    {
        if(instance != null)
        {
            instance.add(_trial, _sensation, _levels, _target, _answer, _responseTime,
                    _iscorrect, _targetValue, _answerValue );
            return true;
        }

        return false;
    }

    public void add(int _trial, int _sensation, int _levels, int _target, int _answer, long _responseTime,
            int _iscorrect, float _targetValue, float _answerValue)
    {
        if(samples != null)
        {
            DataSample sample = new DataSample(_trial, _sensation, _levels, _target, _answer,  _responseTime,
                    _iscorrect, _targetValue, _answerValue);
            samples.add(sample);
        }
    }


    public static DataStorage getInstance()
    {
        if(instance == null)
        {
            instance = new DataStorage();
        }
        return instance;
    }

    public void clearData()
    {
        if(samples!= null)
        {
            samples.clear();
        }
    }


    //for study 2
    public String save(){
        return save(null);
    }

    public String save(String surfix)
    {
        if(samples == null || samples.size() == 0)
        {
            return "";
        }

        if(surfix == null)
        {
            surfix = "LogData";
        }
        if(!surfix.startsWith("_"))
        {
            surfix = "_" + surfix;
        }

        File dir;

        dir = new File("/Users/hanteng/Dropbox/fluiddatafromserver/user_" + userId);

        String time = String.valueOf(System.currentTimeMillis());
        String filename = time + surfix + "_" + sensation + "_" + levels + "_levels_samples.csv";

        File file = new File(dir, filename);

        if(!dir.exists())
        {
            dir.mkdir();
        }

        try
        {
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter( new FileOutputStream(file, true));

            outputstreamwriter.write( DataSample.toCSV(samples) );
            outputstreamwriter.close();
            System.out.println("write samples completes.");

        } catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(e.toString());
        }

        return surfix;
    }
    
    
    private static DataStorage instance;
    public ArrayList<DataSample> samples;
}
