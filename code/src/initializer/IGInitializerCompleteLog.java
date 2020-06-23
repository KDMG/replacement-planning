package initializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Timestamp;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.out.XesXmlSerializer;

import utility.AlignedTrace;
import utility.Constants;
import utility.CSVParser;
import utility.DBManager;
import utility.Event;
import utility.LogManager;
import utility.Parser;
import utility.Path;
import utility.PetriArc;
import utility.PetriNetParser;

import com.mysql.jdbc.Connection;

public class IGInitializerCompleteLog {
	
	private String eventLog;
	private String model;
	private String conformance;
	private String graphsFolder;
	private String graphsFile;
	
	private String dbUrl;
	private String dbName;
	private String username;
	private String password;
	
	private String args[];
	
	private ArrayList<AlignedTrace> conformanceResult;
	
	private HashMap<Integer, ArrayList<Event>> tracesList;
	
	private long dbCreationTimeStart;
	private long dbCreationTimeEnd;
	
	/**
	 * 
	 */
	public IGInitializerCompleteLog() {
		int i=0;
		//update 27/06: to avoid possible problems with static classes
		Path paths= new Path(Constants.EVENTLOG, Constants.MODEL, Constants.CONFORMANCE, Constants.GRAPHPATH);
		//to launch form the anomalous patterns program
		
		/*System.out.println("arguments size "+args.length);
		String eventLog=args[6];
		String model=args[7];
		String conformance=args[8];
		String graphsFolder=args[9];
		*/
		
		/*Inizializzazione dei parametri presi dal file di configurazione "fileName"*/
		Properties prop = new Properties();
		String fileName = "res/config.txt";
		InputStream is = null;
		try {
		    is = new FileInputStream(fileName);
		} catch (FileNotFoundException ex) {
		    System.out.println(ex);		}
		try {
		    prop.load(is);
		} catch (IOException ex) {
			System.out.println(ex);
		}
		
		this.eventLog=prop.getProperty("eventLog");
		this.model=prop.getProperty("model");
		this.conformance=prop.getProperty("conformance");
		this.graphsFolder=prop.getProperty("graphsFolder");
		this.graphsFile=prop.getProperty("graphsFile");
		
		System.out.println(this.eventLog);
		
		Integer dbCreato = Integer.parseInt(prop.getProperty("dbCreato"));
		this.dbUrl=prop.getProperty("dbUrl");
		this.dbName=prop.getProperty("dbName");
		this.username=prop.getProperty("username");
		this.password=prop.getProperty("password");
		String folder=prop.getProperty("folder");														//this.graphsFolder.replace("graphsDot", "");

		
		String[] arg= new String[10];
		arg[0]= prop.getProperty("arg0");
		arg[1]= prop.getProperty("arg1");
		arg[2]= dbName;
		arg[3]= prop.getProperty("arg3");
		arg[4]= prop.getProperty("arg4");
		arg[5]= this.graphsFile;
		arg[6]= this.eventLog;
		arg[7]= this.model;
		arg[8]= this.conformance;
		arg[9]= this.graphsFolder;
		
		this.args = arg;
		
		
		/*Creazione di un file di log*/		
		PrintWriter writer = null;
		 try {
			 File file = new File (folder+"logIGInitializer.txt");
			 System.out.println(file);
			 writer = new PrintWriter(file, "UTF-8");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		writer.write("Avvio esecuzione");
		writer.flush();
		
		/*Inizializzazione variabili in paths*/ 
		paths.setEventLog(this.eventLog);
		paths.setPetriNet(this.model);
		paths.setReplayResult(this.conformance);
		paths.setGraphDirectory(this.graphsFolder);
		writer.write("event log "+paths.getEventLog());
		PetriNetParser parser=null;
		writer.write("variables initialized");
		writer.flush();
		
		/*Creazione del DB*/
		if (dbCreato == 0) {
			 dbCreationTimeStart=System.currentTimeMillis();
			createExpDatabase(dbName, dbUrl, username, password);
			 dbCreationTimeEnd=System.currentTimeMillis();
			writer.write("db created");
			writer.flush();
		}
		else deleteData(dbName, dbUrl, username, password);
		DBManager dbManager=new DBManager(dbUrl, dbName, username, password);
		
		//update 29/05: the conformance checking is performed after uploading the files
		XesXmlParser xesParser= new XesXmlParser();
		XLog xlog=null;
		LogManager logManager=null;

		try {
			List<XLog> xlogList=xesParser.parse(new File(paths.getEventLog()));

			xlog=xlogList.get(0);
			logManager= new LogManager(xlog);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//have to be repeated...
		
		
		//InputGenerator inG= new InputGenerator(xlog,model, "C:/xamppOld/htdocs/Esub/GraphManager/browsing_workflow2/files/"+dbName);
		
		/*se si vuole saltare la fase di Conformance Checking (altrimenti togliere il commento)*/
		//InputGenerator inG= new InputGenerator(xlog,this.model, folder);
		//inG.generateInput();
		CSVParser csvParser= new CSVParser(paths.getReplayResult(), dbManager);
		
		switch(i){
		case 0:
			//causalRel from a Petri Net
			parser=new PetriNetParser(new File(paths.getPetriNet()),dbManager);
			parser.getOrderingRelations(this.args);
			writer.write("Ordering relations stored \n");
			break;
		case 1:
			System.out.println("given ordering relations");
			break;
		}
		if(i==0 || i==1){
		
		
			
		/*se si vuole saltare la fase di Conformance Checking (altrimenti usare parse() invece di parseNoConf())*/
		conformanceResult = csvParser.parseNoConf(xlog);
		//conformanceResult = csvParser.parse();
				
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter(folder+"alignedTraces.txt"));
			//we consider only the non compliant traces
		for (AlignedTrace at: conformanceResult) {
			//String alignment=at.getAlingment();
			//if(alignment.contains("[L]") || alignment.contains("[M-REAL]"))
				pw.write(at.getTraceId()+";"+at.getAlingment()+"\n");
		}
	 
		pw.close();
		writer.write("alignedTraces file created\n");
		writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		HashMap<Integer, String> traceIdMapping=setTraceId(xlog, dbManager);
		PrintWriter pwTraces;
		try {
			pwTraces = new PrintWriter(new FileWriter(folder+"traceIdMapping.txt"));
		 
		for (Integer key: traceIdMapping.keySet()) {
			pwTraces.write(key+";"+traceIdMapping.get(key)+"\n");
		}
	 
		pwTraces.close();
		writer.write("traceIdMapping file created\n");
		writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//update events table
		this.tracesList= updateEventTable(xlog, dbManager);
		
		PrintWriter pwEvents;
		try {
			pwEvents = new PrintWriter(new FileWriter(folder+"tracesList.txt"));
		 
		for (Integer key: traceIdMapping.keySet()) {
			String eventsListString="";
			ArrayList<Event> eventList=tracesList.get(key);
			for(Event event: eventList){
				if(eventsListString.equals(""))
					eventsListString=event.getEventclass();
				else
					eventsListString=eventsListString+"$"+event.getEventclass();
			}
			pwEvents.write(key+";"+eventsListString+"\n");
		}
	 
		pwEvents.close();
		writer.write("tracesList file created\n");
		writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		// at the end I create another log, identical to the original one.
		// this is needed to support calls in which I want the complete log
		
		XLogImpl newLog= new XLogImpl(xlog.getAttributes());
		Iterator<XTrace> logIterator= xlog.iterator();
		while(logIterator.hasNext()){
		XTrace trace=logIterator.next();
		String traceId=trace.getAttributes().get("concept:name").toString();
		newLog.add(trace);
		}
		OutputStream output=null;
		try {
				output = new FileOutputStream(folder+"/filteredLog.xes");
				XesXmlSerializer serializer= new XesXmlSerializer() ;
				serializer.serialize(newLog, output);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
		
		
		System.out.println("Done Initialization");

	}
	
	public String getGraphsFile() {
		return graphsFile;
	}

	public void setGraphsFile(String graphsFile) {
		this.graphsFile = graphsFile;
	}

	public ArrayList<AlignedTrace> getConformanceResult() {
		return conformanceResult;
	}

	public void setConformanceResult(ArrayList<AlignedTrace> conformanceResult) {
		this.conformanceResult = conformanceResult;
	}

	public String getEventLog() {
		return eventLog;
	}

	public void setEventLog(String eventLog) {
		this.eventLog = eventLog;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getConformance() {
		return conformance;
	}

	public void setConformance(String conformance) {
		this.conformance = conformance;
	}

	public String getGraphsFolder() {
		return graphsFolder;
	}

	public void setGraphsFolder(String graphsFolder) {
		this.graphsFolder = graphsFolder;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	private static XLog checkEventLog(XLog log) {
		//remove the lifecycle attribute
		XLog newLog=log;
		Iterator<XTrace> it= newLog.iterator();
		while(it.hasNext()){
			XTrace trace= it.next();
			XAttributeMap traceAtt=trace.getAttributes();
			Iterator<XEvent> itEvent=trace.iterator();
			while(itEvent.hasNext()){
				XEvent event= itEvent.next();
				XAttributeMap attributesMap=event.getAttributes();
				String lifecycle="";
				try{
					lifecycle=attributesMap.get("lifecycle:transition").toString();
				}
				catch(NullPointerException e){
					System.out.println("lifecycle not present");
				}
				if(!lifecycle.equals(""))
					attributesMap.remove("lifecycle:transition");
				event.setAttributes(attributesMap);
								}
				}
		OutputStream output=null;
		try {
			output = new FileOutputStream("logChanged.xes");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XesXmlSerializer serializer= new XesXmlSerializer() ;
		try {
			serializer.serialize(newLog, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		newLog=null;
		XesXmlParser xesParser= new XesXmlParser();
		java.util.List<XLog> xlogList=null;
		try {
			xlogList = xesParser.parse(new File("logChanged.xes"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			newLog=xlogList.get(0);	
			return newLog;
		}
	
	private static void createExpDatabase(String dbName, String dbUrl, String username, String password) {
		// JDBC driver name and database URL
		   System.out.println("Create Database function called");
		   final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		   final String DB_URL = dbUrl;

		   //  Database credentials
		   final String USER = username;
		   final String PASS = password;																//"enr+icc";
		   java.sql.Connection conn = null;
		   java.sql.Statement stmt = null;
		   java.sql.Connection conn1= null;
		   java.sql.Statement stmt1 = null;
		   
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating database...");
		      stmt = conn.createStatement();
		      
		      String sql = "DROP DATABASE IF EXISTS "+dbName;
		      stmt.executeUpdate(sql);
		      
		      sql = "CREATE DATABASE IF NOT EXISTS "+dbName;
		      stmt.executeUpdate(sql);
		      System.out.println("Database created successfully...");
		      conn1= DriverManager.getConnection(DB_URL + dbName,USER,PASS);
		      stmt1 = conn1.createStatement();
		      
		      
		      // create tables
		      String query="CREATE TABLE IF NOT EXISTS `alignedevent` ( "
		      		+ "`posTrace` varchar(11) NOT NULL DEFAULT '', "
		      		+ "  `class` varchar(500) NOT NULL DEFAULT '', "
		      		+ "  `trace` varchar(11) NOT NULL DEFAULT '', "
		      		+ "  `traceid` varchar(500) NOT NULL, "
		      		+ "  `timestamp` varchar(100) DEFAULT NULL, "
		      		+ "  `kind` varchar(100) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='eventwabototal';";
		      stmt1.executeUpdate(query);
		      
		      query=" CREATE TABLE IF NOT EXISTS `alignments` ("
		      		+ "  `traceId` varchar(100) NOT NULL, "
		      		+ "  `alignment` text NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		      stmt1.executeUpdate(query);
		      
		      query="CREATE TABLE IF NOT EXISTS `anomalies` ("
		      		+ " `id` int(11) NOT NULL, "
		      		+ " `posEvent` int(11) NOT NULL, "
		      		+ " `numTrace` int(11) NOT NULL, "
		      		+ "	`type` varchar(50) NOT NULL, "
		      		+ "	`eventclass` varchar(500) NOT NULL "
		      		+ "	) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `anomedges` ("
		      		+ "  `id` int(11) NOT NULL, "
		      		+ "  `source` varchar(100) NOT NULL, "
		      		+ "  `target` varchar(100) NOT NULL, "
		      		+ "  `numTrace` int(11) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `causalrel` ("
		      		+ "  `source` varchar(500) NOT NULL, "
		      		+ "  `target` varchar(500) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `causalreltau` ("
		      		+ "  `id` int(11) NOT NULL, "
		      		+ "  `source` varchar(500) NOT NULL, "
		      		+ "  `target` varchar(500) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `covgraph` ( "
		      		+ "  `id` int(11) NOT NULL, "
		      		+ "  `sourceplace` varchar(500) NOT NULL, "
		      		+ "  `transition` varchar(500) NOT NULL, "
		      		+ "  `targetplace` varchar(500) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `disconnected` ("
		      		+ "  `numtrace` int(11) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1; ";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `edge` ( "
		      		+ "  `id` varchar(100) NOT NULL, "
		      		+ "  `source` varchar(100) NOT NULL, "
		      		+ "  `target` varchar(100) NOT NULL, "
		      		+ "  `visited` int(1) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1; "
		      		+ "";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `edges` ( "
		      		+ "  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, "
		      		+ "  `id1` int(11) NOT NULL, "
		      		+ "  `id2` int(11) NOT NULL, "
		      		+ "  `label` varchar(400) NOT NULL, "
		      		+ "  `dir` varchar(200) NOT NULL, "
		      		+ "  `id_wf` int(11) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1; "
		      		+ "";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `event` ("
		    		  	+ "  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			      		+ "  `posTrace` varchar(11) NOT NULL DEFAULT '', "
			      		+ "  `class` varchar(500) NOT NULL DEFAULT '', "
			      		+ "  `trace` varchar(11) NOT NULL DEFAULT '', "
			      		+ "  `resource` varchar(100), "
			      		+ "  `startTime` DATETIME "
			      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='eventwabototal';"
			      		+ "";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `nodes` ("
			      		+ "  `pos` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			      		+ "  `id` int(11) NOT NULL, "
			      		+ "  `label` varchar(400) NOT NULL, "
			      		+ "  `resource` varchar(100), "
			      		+ "  `id_wf` int(11) NOT NULL, "
			      		+ "  `startTime` DATETIME "
			      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1; "
			      		+ "";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `place` ( "
		      		+ "  `id` varchar(100) NOT NULL, "
		      		+ "  `name` varchar(500) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1; "
		      		+ "";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `startend` ("
		      		+ "  `event` varchar(200) NOT NULL, "
		      		+ "  `type` varchar(10) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1; "
		      		+ "";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `traceid` ("
		      		+ "  `numTrace` varchar(500) NOT NULL, "
		      		+ "  `idTrace` varchar(200) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT "
		      		+ "CHARSET=latin1 COMMENT='traceidwabototal';";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `traceiddisconnected` ( "
		      		+ "  `numTrace` int(11) NOT NULL, "
		      		+ "  `id_wf` int(11) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `tracesgraph` ( "
		      		+ "  `idgraph` int(11) NOT NULL, "
		      		+ "  `numtraces` int(11) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `transition` ( "
		      		+ "  `id` varchar(100) NOT NULL, "
		      		+ "  `name` varchar(500) NOT NULL "
		      		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1; ";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `traceidwabo` (   "
		      		+ "`numTrace` varchar(500) NOT NULL, "
		      		+ "  `idTrace` varchar(200) NOT NULL  "
		      		+ "  ) ENGINE=InnoDB DEFAULT CHARSET=latin1"
		      		+ " COMMENT='traceidwabototal';";
		      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `matrix` (   "
			      		+ " `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			      		+ " `res1` varchar(200),"
			      		+ " `res2` varchar(200),"
			      		+ " `activity1` varchar(200),"
			      		+ " `activity2` varchar(200),"
			      		+ " `handover` float(5), "
			      		+ " `occurrences` int(11), "
			      		+ " `time` int(11) "
			      		+ "  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
			      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `affinity` (   "
			      		+ " `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			      		+ " `resource` varchar(200),"
			      		+ " `candidate` varchar(200),"
			      		+ " `activityFactor` float(3),"
			      		+ " `collaborationFactor` float(3),"
			      		+ " `experience` float(3), "
			      		+ " `speed` float(3), "
			      		+ " `total` float(3) "
			      		+ "  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
			      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `cost` (   "
			      		+ " `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			      		+ " `resource` varchar(200),"
			      		+ " `candidate` varchar(200),"
			      		+ " `activity` varchar(200),"
			      		+ " `collaboration` float(3),"
			      		+ " `experience` float(3), "
			      		+ " `speed` float(3), "
			      		+ " `total` float(3) "
			      		+ "  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
			      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `activity` (   "
			      		+ " `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			      		+ " `label` varchar(200),"
			      		+ " `avgTime` float(3) "
			      		+ "  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
			      stmt1.executeUpdate(query);
		      query="CREATE TABLE IF NOT EXISTS `resource` (   "
			      		+ " `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			      		+ " `name` varchar(200),"
			      		+ " `maxWorkload` float(5),"
			      		+ " `currWorkload` float(5) "
			      		+ "  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
			      stmt1.executeUpdate(query);    
		      query="ALTER TABLE `anomalies` "
		      		+ "  ADD PRIMARY KEY (`id`);";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `anomedges` "
		      		+ "  ADD PRIMARY KEY (`id`); ";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `causalreltau`"
		      		+ "  ADD PRIMARY KEY (`id`);";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `covgraph`  ADD PRIMARY KEY (`id`);";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `edge` "
		      		+ "  ADD PRIMARY KEY (`id`),"
		      		+ "  ADD KEY `i1` (`source`);";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `edges` "
		      		+ "  ADD KEY `id1` (`id1`), "
		      		+ "  ADD KEY `id2` (`id2`), "
		      		+ "  ADD KEY `label` (`label`);";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `place`   ADD PRIMARY KEY (`id`);";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `traceid`   ADD PRIMARY KEY (`numTrace`);";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `transition`   ADD PRIMARY KEY (`id`);";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `anomalies`"
		      		+ "  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `anomedges`"
		      		+ "  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;";
		      stmt1.executeUpdate(query);
		      query="ALTER TABLE `causalreltau` "
		      		+ "  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT; "
		      		+ "";
		      stmt1.executeUpdate(query);
		    		  
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		         if(stmt1!=null)
		        	 stmt1.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		         if(conn1!=null)
		        	 conn1.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		}
	
	private static void deleteData(String dbName, String dbUrl, String username, String password) {
		
		java.sql.Connection conn1 = null;
		java.sql.Statement stmt1 = null;
		
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      
		      conn1= DriverManager.getConnection(dbUrl + dbName,username,password);
		      stmt1 = conn1.createStatement();
		      
		    
		      
				String emptyTable = "TRUNCATE alignedevent";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE alignments";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE anomalies";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE anomedges";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE causalrel";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE causalreltau";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE disconnected";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE edge";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE edges";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE resource";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE affinity";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE cost";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE matrix";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE activity";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE event";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE nodes";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE place";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE startend";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE traceid";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE traceiddisconnected";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE traceidwabo";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE tracesgraph";
				stmt1.executeUpdate(emptyTable);
				emptyTable = "TRUNCATE transition";
				stmt1.executeUpdate(emptyTable);
					
					
		    		  
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(conn1!=null)
		        	 conn1.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	}
	
	private static HashMap<Integer, String> setTraceId(XLog xlog, DBManager dbManager) {
		// for each trace number stores the trace id in the log
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		int numTrace = 1;
		for (XTrace trace : xlog) {
			String traceId = trace.getAttributes().get("concept:name").toString();
			
				String queryInsert = "INSERT IGNORE INTO traceid(numTrace,idTrace) VALUES("
					+ numTrace + ",'" + traceId + "')";
				dbManager.queryUpdate(queryInsert);
			
			result.put(numTrace, traceId);
			numTrace++;
		}
		return result;
	}

	private static HashMap<Integer, ArrayList<Event>> updateEventTable(XLog log, DBManager dbManager) {
		//check if the table is not already set [possible in case of multiple calls]
		HashMap<Integer, ArrayList<Event>> result = new HashMap<Integer, ArrayList<Event>>();
		int numTrace = 1;
		for (XTrace trace : log) {
			int posEv = 0;
			ArrayList<Event> events = new ArrayList<Event>();
			for (XEvent event : trace) {
				posEv++;
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy/M/d H:mm:ss.SSS");
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				String startString = event.getAttributes().get("time:timestamp").toString();
				Date startTime = new Date();
				try {
					startString=startString.replace("T"," ").replace("Z","").substring(0,18)+".000";
					//format o format2 in base al tipo di formato della data
					startTime = format2.parse(startString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String start = startString; 
				// nel caso in cui il formato originale della data sia differente da format2
				 format2.format(startTime);
								
				String eventName = event.getAttributes().get("concept:name")
						.toString();
				String eventClass = eventName;
				String eventResource = null;  			//modificato

				eventClass = eventClass.replace(" ", "");
				
				if(event.getAttributes().get("org:resource")!=null){
					eventResource = event.getAttributes()
						.get("org:resource").toString();
				}
				if(event.getAttributes().get("lifecycle:transition")!=null){
					String eventType = event.getAttributes()
						.get("lifecycle:transition").toString();	
				}
				String queryInsert = "INSERT IGNORE INTO event(posTrace,class,trace,resource,startTime) VALUES(?,?,?,?,?)";
					ArrayList<String> fieldList = new ArrayList<String>(
					Arrays.asList(String.valueOf(posEv), eventClass,
							String.valueOf(numTrace), eventResource, start));
				dbManager.executeStatement(queryInsert, fieldList);
				Event eventTrace = new Event(String.valueOf(posEv), eventClass);
				events.add(eventTrace);
				
			}
			result.put(numTrace, events);
			numTrace++;
		}
		
		return result;
	}
	
	
	public HashMap<Integer, ArrayList<Event>> getTracesList() {
		return tracesList;
	}

	public void setTracesList(HashMap<Integer, ArrayList<Event>> tracesList) {
		this.tracesList = tracesList;
	}
	
	public long getDbExecutionTime() {
		return dbCreationTimeEnd-dbCreationTimeStart;
	}

}
