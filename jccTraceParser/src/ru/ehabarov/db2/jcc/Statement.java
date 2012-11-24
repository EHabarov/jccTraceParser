package ru.ehabarov.db2.jcc;

import java.util.ArrayList;


public class Statement extends TraceObject 
{
	public String sql;
	public String connectionId;
	public String threadId;
	public long queryTime;
	public long createTime;
	public long closeTime;
	public long finalyzeTime;
	private ArrayList<String> resultSetIds = new ArrayList<String>();
	
	public Statement(long queryTime, long createTime, String threadId, String connectionId, String id, String sql)
	{
		this.id = id;
		this.queryTime = queryTime;
		this.createTime = createTime;
		this.threadId = threadId;
		this.connectionId = connectionId;
		this.sql = sql;
		
	}
	public void addResultSetId(String resultsetId)
	{
		resultSetIds.add(resultsetId);
	}
	public String[] getResultSetIds()
	{
		return resultSetIds.toArray(new String[resultSetIds.size()]);
	}
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append("T:");
		result.append(threadId);
		result.append(" C:");
		result.append(connectionId);
		result.append(" S:");
		result.append(id);
		result.append(" RS:");
		result.append(resultSetIds.size());
		if (queryTime > 0 && closeTime > 0)
		{
			result.append(" TTL(ms):");
			result.append(closeTime - queryTime);
		}
		if (closeTime > 0 && finalyzeTime > 0)
		{
			result.append(" FIN(ms):");
			result.append(finalyzeTime - closeTime);
		}
		if (queryTime > 0 && createTime > 0)
		{
			result.append(" PREP(ms):");
			result.append(createTime - queryTime);
		}
		result.append(" SQL:");
		result.append(sql);
		return result.toString();
	}
}
