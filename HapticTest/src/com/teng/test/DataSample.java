package com.teng.test;

import java.util.ArrayList;
import java.util.Iterator;

public class DataSample {
	public int trial;
	public int sensation; //1-pressure, 2-vibration, 3-temperature
	public int levels;

    public int target;
    public int answer;

    public float targetValue;
    public float answerValue;
    
    
    public long responseTime;
    public int iscorrect;  // 1 - correct, 0 - incorrect
 
    public DataSample(int _trial, int _sensation, int _levels, int _target, int _answer, long _responseTime,
                      int _iscorrect, float _targetValue, float _answerValue)
    {
        trial = _trial;
        sensation = _sensation;
        levels = _levels;
        target = _target;
        answer = _answer;
        responseTime = _responseTime;
        iscorrect = _iscorrect;
        
        targetValue = _targetValue;
        answerValue = _answerValue;
        
    }

    public static String toCSV(ArrayList<DataSample> arrayList)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for(Iterator<DataSample> iterator = arrayList.iterator(); iterator.hasNext();)
        {
            DataSample sample = iterator.next();
            stringbuilder.append("" + sample.trial + "," + sample.sensation + "," + sample.levels + "," 
            		+ sample.target + "," + sample.answer + ","
                    + sample.responseTime + ","
                    + sample.iscorrect + "," 
                    + sample.targetValue + ","
                    + sample.answerValue + ","
                    + "\r\n");
           
        }

        return stringbuilder.toString();
    }
    
}
