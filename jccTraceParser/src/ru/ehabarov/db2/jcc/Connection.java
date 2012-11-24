package ru.ehabarov.db2.jcc;

import java.util.TreeMap;

public class Connection extends TraceObject 
{
	public String driverName;
	public String driverVersion;
	public String serverName;
	public String databaseProductName;
	public String databaseProductVersion;
	public String userName;
	public String correlator;
	public TreeMap<String, Statement> statements = new TreeMap<String, Statement>();
	
	public Connection()
	{
		
	}

}
