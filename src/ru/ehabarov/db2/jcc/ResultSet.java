package ru.ehabarov.db2.jcc;

public class ResultSet extends TraceObject 
{
	public long createTime;
	public long executeQueryStart;
	public long closeTime;
	public long finalyzeTime;
	
	public ResultSet(long executeQueryStart, long createTime)
	{
		this.executeQueryStart = executeQueryStart;
		this.createTime = createTime;
	}
}
