package ru.ehabarov.db2.jcc;

import java.util.ArrayList;

import edu.princeton.cs.StdStats;

public class AnalyticsStatement 
{
	public int execCount = 0;
	public int resultCount = 0;
	public ArrayList<Long> liveTimes = new ArrayList<Long>(); 
	public ArrayList<Long> execTimes = new ArrayList<Long>(); 
	public void add(int execCount, int resultCount, long liveTime, long execTime)
	{
		this.execCount += execCount;
		this.resultCount += resultCount;
		this.liveTimes.add(liveTime);
		this.execTimes.add(execTime);
	}
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append("EX(cnt):");
		result.append(execCount);
		result.append(" RS(cnt):");
		result.append(resultCount);
		Long[] LT = liveTimes.toArray(new Long[0]);
		result.append(" LT(sum):");
		result.append(StdStats.sum(LT));
		result.append(" LT(min):");
		result.append(StdStats.min(LT));
		result.append(" LT(max):");
		result.append(StdStats.max(LT));
		result.append(" LT(mean):");
		result.append(Math.round(StdStats.mean(LT)));
		return result.toString();
	}
	public String toCSV()
	{
		StringBuffer result = new StringBuffer();
		result.append(execCount);
		result.append(";");
		result.append(resultCount);
		Long[] LT = liveTimes.toArray(new Long[0]);
		result.append(";");
		result.append(StdStats.sum(LT));
		result.append(";");
		result.append(StdStats.min(LT));
		result.append(";");
		result.append(StdStats.max(LT));
		result.append(";");
		result.append(Math.round(StdStats.mean(LT)));
		result.append(";");
		return result.toString();
	}
}
