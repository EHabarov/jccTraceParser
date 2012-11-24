package ru.ehabarov.db2.jcc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TraceParser {

	Pattern ptTime = Pattern.compile("\\[Time:(\\d+)\\]");
	Pattern ptThread = Pattern.compile("\\[Thread:([A-Za-z0-9]+)\\]");
	Pattern ptConnection = Pattern.compile("\\[Connection@([a-z0-9]+)\\]");
	Pattern ptConCreStmtSql = Pattern.compile("prepareStatement \\((.+)\\) called");
	Pattern ptConCreStmtId = Pattern.compile("prepareStatement \\(\\) returned PreparedStatement@([a-z0-9]+)");
	Pattern ptStatement = Pattern.compile("\\[PreparedStatement@([a-z0-9]+)\\]");
	Pattern ptStmtExecQCall = Pattern.compile("executeQuery \\(\\) called");
	Pattern ptStmtExecQRet = Pattern.compile("executeQuery \\(\\) returned ResultSet@([a-z0-9]+)");
	Pattern ptResultSet = Pattern.compile("\\[ResultSet@([a-z0-9]+)\\]");
	Pattern ptClosed = Pattern.compile("close \\(\\) called");
	Pattern ptFinalized = Pattern.compile("finalize \\(\\) called");
	
	Matcher mtTime = null;
	Matcher mtThread = null;
	Matcher mtConnection = null;
	Matcher mtConCreStmtSql = null;
	Matcher mtConCreStmtId = null;
	Matcher mtStatement = null;
	Matcher mtStmtExecQCall = null;
	Matcher mtStmtExecQRet = null;
	Matcher mtResultSet = null;
	Matcher mtClosed = null;
	Matcher mtFinalized = null;

	TreeMap<String , Statement> statements = new TreeMap<String, Statement>();
	TreeMap<String, ResultSet> resultSets = new TreeMap<String, ResultSet>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), args[1]));
		TraceParser tp = new TraceParser();
		tp.parse(reader);
		tp.print();
	}
	public void print()
	{
		Iterator<Entry<String, Statement>> iter = statements.entrySet().iterator();
		while (iter.hasNext())
		{
			System.out.println(iter.next().getValue().toString());
		}
	}
	public void parse(BufferedReader reader) throws Exception
	{		
		String line;
		String sql = "";
		long prepareQueryTime = 0;
		long execQueryTime = 0;
				
		while ( (line = reader.readLine()) != null )
		{
			if (line.startsWith("[ibm][db2][jcc]"))
			{
				mtTime = ptTime.matcher(line);
				if (mtTime.find()) 
				{ 
					long time = Long.parseLong(mtTime.group(1));
					//System.out.println("Time found:"+date); 
					
					mtThread = ptThread.matcher(line);
					mtConnection = ptConnection.matcher(line);
					mtStatement = ptStatement.matcher(line);
					mtResultSet = ptResultSet.matcher(line);
					
					String threadId = "XXXX";
					String connectionId = "XXXX";
					String statementId = "XXXX";
					
					if (mtThread.find())
					{
						threadId = mtThread.group(1);
						//System.out.println("Thread found id = "+threadId);
					}
					if (mtConnection.find())
					{
						connectionId = mtConnection.group(1);
						//System.out.println("Connection found id = "+connectionId);
						mtConCreStmtSql = ptConCreStmtSql.matcher(line);
						mtConCreStmtId = ptConCreStmtId.matcher(line);
						if (mtConCreStmtSql.find())
						{
							sql = mtConCreStmtSql.group(1);
							//System.out.println("SQL found = "+sql);
							prepareQueryTime = time;
						}
						if (mtConCreStmtId.find())
						{
							statementId = mtConCreStmtId.group(1);
							statements.put(statementId, new Statement(prepareQueryTime, time, threadId, connectionId, statementId, sql));
							//System.out.println("Statement created = "+statementId);
							//System.out.println("Thread id = "+threadId);
						}												
					}
					if (mtStatement.find())
					{
						statementId = mtStatement.group(1);
						//System.out.println("prepareStatement found id = "+statementId);
						mtStmtExecQCall = ptStmtExecQCall.matcher(line);
						mtStmtExecQRet = ptStmtExecQRet.matcher(line);
						mtClosed = ptClosed.matcher(line);
						mtFinalized = ptFinalized.matcher(line);
						if (mtStmtExecQCall.find())
						{
							execQueryTime = time;
							//System.out.println("executeQuery called = "+execQueryTime);
						}
						if (mtStmtExecQRet.find())
						{
							String resultSetId = mtStmtExecQRet.group(1);
							//System.out.println("executeQuery return ResultSet = "+resultSetId);
							statements.get(statementId).addResultSetId(resultSetId);
							ResultSet rs = new ResultSet(execQueryTime, time);
							resultSets.put(resultSetId, rs);
						}
						if (mtClosed.find())
						{
							//System.out.println("Statement closed id = "+statementId);
							statements.get(statementId).closeTime = time;
						}						
						if (mtFinalized.find())
						{
							//System.out.println("Statement closed id = "+statementId);
							statements.get(statementId).finalyzeTime = time;
						}						
					}
					if (mtResultSet.find())
					{
						String resultSetId = mtResultSet.group(1);
						//System.out.println("ResultSet found id = "+resultSetId);
						mtClosed = ptClosed.matcher(line);
						if (mtClosed.find())
						{
							//System.out.println("ResultSet closed id = "+resultSetId);
							resultSets.get(resultSetId).closeTime = time;
						}
					}

				}
			}
		}
		reader.close();
	}
}
