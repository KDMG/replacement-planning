package testSubprocesses;


import utility.AlignedTrace;
import utility.CSVParser;
import utility.Constants;
import utility.DBManager;
import utility.LogManager;
import utility.Path;
import utility.PetriNetParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

import instancegraph.IGBuilderUpdated;

public class SubprocessesPaper {
	
	// First Step: generate Graphs 
	// van Dongen
	// van Beest
	// BIG
	//correct
	// for each of them:
	// for each folder:
	//generate db, generate graphs folder. generate graphs. call FSG. convert FSG in
	//subdue format [storing graph support and graphs list]. 
	// call graph matching and store matching result. Compute average difference.
	// compute trace support. Compute Average difference from graph support

	public enum IGMETHODS {
		VANDONGEN, VANBEEST, BIG, CORRECT
	}
	
	public static void main(String[] args) {
		String folderName=args[0];
		File folder= new File(folderName);
		File[] folderList= folder.listFiles();
		for(int i=0; i<folderList.length; i++){
			if(folderList[i].isDirectory())
				if(folderList[i].getName().contains("Laura")){
					IGMETHODS[] methods=IGMETHODS.values();
					for(int j=0; j< methods.length; j++){
						buildIG(folderList[i], methods[j], args);
						System.out.println("IG built");
						// to generate the FSG, I have to launch it on the server. Then I move
						// the fp file in the experiment folder. I assume to have it herein
						String subsfile=convertFSGtoSubdue(folderList[i]+"subgraphs.subs");
						System.out.println("experiments "+methods[j]+" concluded");
					}
				}
		}
		
	}

	private static String convertFSGtoSubdue(String fileName) {
		String result="";
		try {
			FileReader fr= new FileReader(fileName);
			BufferedReader br= new BufferedReader(fr);
			String[] fileNameTokens=fileName.split(".");
			String fileNameNoExtension=fileNameTokens[0];
			FileWriter fw= new FileWriter(fileNameNoExtension+"Converted.subs");
			BufferedWriter bw= new BufferedWriter(fw);
			String line="";
			while((line=br.readLine())!=null){
				if(line.startsWith("t")){
					
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private static void buildIG(File file, IGMETHODS igmethods, String[] args) {
		DBManager dbManager= new DBManager("","","","");
		dbManager.createDB();
		try {
			String graphsPath=file.getAbsolutePath()+"Graphs"+igmethods;
			String eventLogFile="";
			String conformanceChecking="";
			String petriNetFile="";
			FileUtils.forceMkdir(new File(graphsPath));
			File[] files=file.listFiles();
			for(int i=0; i<files.length; i++){
				if(files[i].isFile())
				{
					if(files[i].getAbsolutePath().endsWith(".xes"))
						eventLogFile=files[i].getAbsolutePath();
					
				}
				else if(files[i].isDirectory()){
					if(files[i].getAbsolutePath().toLowerCase().endsWith("conformance"))
						conformanceChecking=files[i].getAbsolutePath();
				}
			}
			Path paths= new Path(eventLogFile, petriNetFile, conformanceChecking, graphsPath);
			PetriNetParser parser=null;
			CSVParser csvParser= new CSVParser(paths.getReplayResult(), dbManager);
			LogManager logManager=null;
			//causalRel from a Petri Net
			parser=new PetriNetParser(new File(paths.getPetriNet()),dbManager);
			parser.getOrderingRelations(args);
			//parser.deleteTauFromRel(Integer.parseInt(args[3]), Integer.parseInt(args[4]));
			ArrayList<AlignedTrace> conformanceResult= csvParser.parse();
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
				igbuilder=new IGBuilderUpdated(xlog, dbManager, conformanceResult, Integer.parseInt(args[0]),Integer.parseInt(args[1]));
				//igbuilder=new IGBuilderUpdated(xlog, dbManager, conformanceResult,Integer.parseInt(args[0]),Integer.parseInt(args[1]), paths);
				igbuilder.createIGSet();
				System.out.println("Done");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}