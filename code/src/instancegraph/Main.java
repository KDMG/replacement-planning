// Author: Laura Genga, Università Politecnica delle Marche
// email: l.genga@univpm.it

package instancegraph;

import inputgenerationtest.InputGenerator;
import socialnet.Matrix;
import socialnet.Replace;
import initializer.IGInitializerCompleteLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
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
import com.mysql.jdbc.Statement;




//import eu.bivee.test.TimeoutProlog;
//import eu.bivee.utils.SyntaxError;
import LogGenerator.LogWriter;

public class Main {

	public static void main(String[] args) {
		
		int i=0;
		//update 27/06: to avoid possible problems with static classes
		Path paths= new Path(Constants.EVENTLOG, Constants.MODEL, Constants.CONFORMANCE, Constants.GRAPHPATH);
		//to launch form the anomalous patterns program
		
		long inizio = System.currentTimeMillis();
		/*Inizializzazione*/
		IGInitializerCompleteLog igIni = new IGInitializerCompleteLog();
		
		//System.out.println("arguments size "+args.length);
		//if(args.length>5){
		//System.out.println("long arguments ");
		String eventLog= igIni.getEventLog();
		String model= igIni.getModel();
		String conformance=igIni.getConformance();
		String graphsFolder=igIni.getGraphsFolder();
		paths.setEventLog(eventLog);
		paths.setPetriNet(model);
		paths.setReplayResult(conformance);
		paths.setGraphDirectory(graphsFolder);
		
		args = igIni.getArgs();
		
		System.out.println("event log "+paths.getEventLog());
		PetriNetParser parser=null;
		String dbUrl=igIni.getDbUrl();
		String dbName=igIni.getDbName();
		String username=igIni.getUsername();
		String password=igIni.getPassword();
		
		DBManager dbManager=new DBManager(dbUrl, dbName, username, password);
		
		
		HashMap<Integer, ArrayList<Event>> tracesList = igIni.getTracesList();
		
		
		LogManager logManager=null;
		if(i==0 || i==1){
		ArrayList<AlignedTrace> conformanceResult = igIni.getConformanceResult();
		//IGBuilder igbuilder;
		IGBuilderUpdated igbuilder;
		XesXmlParser xesParser= new XesXmlParser();
		XLog xlog=null;
		try {
			List<XLog> xlogList=xesParser.parse(new File(paths.getEventLog()));

			xlog=xlogList.get(0);
			logManager= new LogManager(xlog);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(xlog!=null){
			
			
			//igbuilder=new IGBuilderUpdated(xlog, dbManager, conformanceResult, Integer.parseInt(args[0]),Integer.parseInt(args[1]));
			igbuilder=new IGBuilderUpdated(xlog, dbManager, conformanceResult,Integer.parseInt(args[0]),Integer.parseInt(args[1]), paths, tracesList);
			igbuilder.createIGSet();
			
			if(args.length>5){
				igbuilder.convertSubdueTables2Subdue(dbManager, args[5]);
				//cancellazione dati per esperimenti
			}
			//DBManager db1= new DBManager(dbUrl, dbName, username, password);
			//DotConverter dc= new DotConverter("",db1);
			//dc.convertSubdeTables2Pafi(db1, Constants.FOLDER);
			
			//
//			System.out.println("Conversion");
//			File dir = new File(Constants.FOLDERSUBDUE);
//			dir.mkdir();
//			DotConverter converter= new DotConverter(dir.getAbsolutePath().substring(0, dir.getAbsolutePath().lastIndexOf("subdue")),dbManager);
//			converter.convertDot2SubdueFiles();
//			System.out.println("Generalization Computation");
//			try {
//				TimeoutProlog.computeGen(args);
//			} catch (SyntaxError e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			
			Matrix orgMatrix = new Matrix(dbManager);
			orgMatrix.importValue();
			Integer[] perc = new Integer[]{0, 10, 15, 20, 25}; 
			for (Integer param : perc) {

				String Path = graphsFolder + "matrix_" + param + "perc.dot";
				if (param == 0) Path = graphsFolder + "matrix.dot";
				orgMatrix.createSocialGraph(Path, param);
			}
			
			//Replace replace = new Replace(dbManager);
			//replace.main(null);
			
			long fine = System.currentTimeMillis();
			//System.out.println("Done");
			System.out.println("Tempo di esecuzione: " + (fine-inizio)/1000 + " secondi");
			System.out.println("Tempo di creazione del DB:"+igIni.getDbExecutionTime());
		}
		else
			System.out.println("log not built");
		}
		/*else if(i==2){
			String[] list={"C:/Users/Laura/Desktop/outputsInductiveNR298_40.txt"};
			Comparator comp= new Comparator(list);
			comp.compareGraphs();
		}
		else if(i==3){
			ArrayList<String> files= new ArrayList<String>();
			files.add("C:/Users/Laura/Desktop/Test/Synthethic/EsempiPaper/TestRete/ProveInSerie/IG1.xes");
			files.add("C:/Users/Laura/Desktop/Test/Synthethic/EsempiPaper/TestRete/ProveInSerie/IG2.xes");
			files.add("C:/Users/Laura/Desktop/Test/Synthethic/EsempiPaper/TestRete/ProveInSerie/IG3.xes");
			files.add("C:/Users/Laura/Desktop/Test/Synthethic/EsempiPaper/TestRete/ProveInSerie/IG4.xes");
			files.add("C:/Users/Laura/Desktop/Test/Synthethic/EsempiPaper/TestRete/ProveInSerie/IG5.xes");
			LogWriter writer= new LogWriter(49, files, "C:/Users/Laura/Desktop/Test/Synthethic/EsempiPaper/TestRete/ProveInSerie/NewLogs/Test49/Log35.xes");
			writer.writeLogFromExamples(15);
		}*/
		
		else if(i==-1){
				FileWriter writer = null;
				try {
					String filePath="/Users/Laura/Documents/workspace/dbPafi/wabo4nofilteredrepTestDicembre.g";
					writer = new FileWriter(filePath, false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String querySelectNumTraces="SELECT COUNT(DISTINCT id_wf) as numTrace FROM nodes";
				int numCases=Integer.parseInt(dbManager.executeQuery(querySelectNumTraces, "numTrace").get(0));
				for(int j=1; j<numCases+1; j++){
					try {
						writer.write("t # "+j+" \n");
						String querySelectNodes="SELECT id,label FROM nodes WHERE id_wf="+j;
						ArrayList<String> nodesList=dbManager.executeQuery(querySelectNodes, "id;label");
						for(String nodeField: nodesList){
							String[] nodeTokens= nodeField.split(";");
							String nodeId=nodeTokens[0];
							String nodeLabel= nodeTokens[1];
							writer.write("v "+nodeId+" "+nodeLabel+" \n");
						}
						String querySelectEdges="SELECT id1,id2,label FROM 	edges WHERE id_wf="+j;
						ArrayList<String> edgesList=dbManager.executeQuery(querySelectEdges, "id1;id2;label");
						for(String edgeField: edgesList){
							String[] edgeTokens=edgeField.split(";");
							String id1=edgeTokens[0];
							String id2=edgeTokens[1];
							String label=edgeTokens[2];
							writer.write("u "+id1+" "+id2+" "+label+ "\n");
						}
						System.out.println(j);
						} catch (IOException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
					}
	}

	private static void compareGraphsSet(){
		String db1Name="";
		String db2Name="";
		DBManager db1= new DBManager("", "", "", "");
		DBManager db2= new DBManager("","", "", "");
		for(int i=0; i<787; i++){
			String edges1="";
			String edges2="";
			String queryEdges="SELECT id1,id2,label,id_wf FROM edges WHERE id_wf="+i;
			ArrayList<String> reqEdges1=db1.executeQuery(queryEdges, "id1;id2;label;id_wf");
			ArrayList<String> reqEdges2=db2.executeQuery(queryEdges, "id1;id2;label;id_wf");
			if(reqEdges1==null || reqEdges2==null){
				System.out.println("lists done");
			}
			else{
			int j=0;
			for(String edge: reqEdges1){
				String[] edgeTokens= edge.split(";");
				edges1=edges1+" "+j+" id1-"+edgeTokens[0]+" id2-"+edgeTokens[1]+" label-"+edgeTokens[2]+" id_wf-"+edgeTokens[3];
				j++;
			}
			j=0;
			for(String edge2: reqEdges2){
				String[] edgeTokens2= edge2.split(";");
				edges2=edges2+" "+j+" id1-"+edgeTokens2[0]+" id2-"+edgeTokens2[1]+" label-"+edgeTokens2[2]+" id_wf-"+edgeTokens2[3];
				j++;
			}
			if(!edges1.equals(edges2)){
				System.out.println("Graphs "+i+" not equal");
				break;
			}
			else{
				edges1="";
				edges2="";
			}
		}
		}
		
	}
}
