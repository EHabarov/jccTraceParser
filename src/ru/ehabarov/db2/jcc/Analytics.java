package ru.ehabarov.db2.jcc;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Analytics 
{
	public static void process(TreeMap<String , Statement> statements, TreeMap<String, ResultSet> resultSets)
	{
		TreeMap< String, AnalyticsStatement> analytics = new TreeMap<String, AnalyticsStatement>();
		
		Iterator<Entry<String, Statement>> iter = statements.entrySet().iterator();
		while (iter.hasNext())
		{
			Statement stmt = iter.next().getValue();
			String key = stmt.sql;
			AnalyticsStatement astmt = analytics.get(key);
			if (astmt == null)
			{
				astmt = new AnalyticsStatement();
				analytics.put(key, astmt);
			}
			astmt.add(stmt.inExecCount, stmt.getResultSetCount(), stmt.getLiveTime(), stmt.inExecTime);
			//System.out.println(iter.next().getValue().toString());
		}
		Iterator<Entry<String, AnalyticsStatement>> aiter = analytics.entrySet().iterator();
		while (aiter.hasNext())
		{
			Entry<String, AnalyticsStatement> entry = aiter.next();
//			System.out.println(entry.getValue().toString()+" SQL:"+entry.getKey());
			System.out.println(entry.getValue().toCSV()+"\""+entry.getKey()+"\";");
		}
		
	}
}
