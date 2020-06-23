package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import com.csvreader.CsvReader;

public class CSVParser {
	private String fileFolder;
	private CsvReader source;
	private DBManager dbManager;
	
	public CSVParser(String file, DBManager dbManager) {
		super();
		this.fileFolder = file;
		this.dbManager=dbManager;
	}
	
	public CSVParser(String file) {
		super();
		this.fileFolder = file;
	}
	
	public ArrayList<AlignedTrace> parse(){
		
		ArrayList<AlignedTrace> alignedTraces= new ArrayList<AlignedTrace>();
		try {
			File fileFolderObj= new File(fileFolder);
			File[] conformanceFiles=fileFolderObj.listFiles();
			for(int j=0; j<conformanceFiles.length;j++){
			FileReader f= new FileReader(conformanceFiles[j]);
			BufferedReader b=new BufferedReader(f);
			//cycle
			int lineNumb=0;
			while(true){
				String line=b.readLine();
				if(lineNumb>2){ //skip the first three lines
				if(line==null)
					break;
				String[] token= line.split(",");
				if(token.length==1)
					break;
				//IMPORTANT: there is the assumption that the "." in csv have been replaced by ","!
				// changes 7/7/15: the "point" question is too variable and uncertain, I've removed it
//				String fitnessString= token[12].replaceAll("\"", "")+"."+token[13].replaceAll("\"", "");
//				double fitness=Double.parseDouble(fitnessString);
//				if(fitness < 1){
				String traceIdList=token[1];
				String alignment=token[token.length-1];
				if(dbManager!=null)
					updateAlignedEvents(alignment,traceIdList);
				if(alignment.contains("[L]") || alignment.contains("[M-REAL]")){
				String[] tokenTrace=traceIdList.replaceAll("\"", "").split("\\|");
					for(int i=0; i<tokenTrace.length;i++){
						String traceId=tokenTrace[i];
						AlignedTrace alignedTrace= new AlignedTrace(traceId,alignment);
						alignedTraces.add(alignedTrace);
					}
				}
				
			}
				lineNumb++;
			}
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return alignedTraces;
		
	}
	
	private void updateAlignedEvents(String alignment, String traceIdList) {
		String[] tokenTrace=traceIdList.replaceAll("\"", "").split("\\|");
		for(int i=0; i<tokenTrace.length;i++){
			String traceIdDebug=tokenTrace[i];
			//if(traceIdDebug.equals("70"))
			//	System.out.println("qui");
		}
		String[] tokensEventWithAlignment= alignment.split("\\|");
		HashMap<Integer,String> posEvents= new HashMap<Integer, String>();
		HashMap<Integer,String> kindAlignmentEvents= new HashMap<Integer, String>();
		int posEvent=1;
		for(int j=0;j<tokensEventWithAlignment.length;j++){
			String[] tokensEvent=tokensEventWithAlignment[j].split("]");
			String eventClass=tokensEvent[1];
			String anomalyKind= tokensEvent[0];
			if(!anomalyKind.contains("M-INVI")){
				posEvents.put(posEvent, eventClass);
				kindAlignmentEvents.put(posEvent, tokensEvent[0]);
				posEvent++;
			}
		}
		for(int i=0; i<tokenTrace.length;i++){
			String traceId=tokenTrace[i];
			for(int pos: posEvents.keySet()){
				String queryInsert = "INSERT IGNORE INTO alignedevent(posTrace,class,trace,traceid,"
						+ "timestamp,kind)"
						+ " VALUES(?,?,?,?,?,?)";
				//remove space and ' char
				String classEventNoSpace=posEvents.get(pos).replace(" ", "");
				classEventNoSpace=classEventNoSpace.replace("'", "");
				String kind=kindAlignmentEvents.get(pos);
				if(traceId.equals("70"))
					System.out.println("qui");
				ArrayList<String> fieldList = new ArrayList<String>(
					Arrays.asList(String.valueOf(pos), classEventNoSpace, "0", traceId, "NULL",kind));
				dbManager.executeStatement(queryInsert, fieldList);
				
				/*String queryInsert="INSERT INTO alignedevent(posTrace,class,trace,traceid,timestamp) "
						+ " VALUES('"+pos+"','"+posEvents.get(pos)+"',0,'"+traceId+"',NULL)";
				dbManager.queryUpdate(queryInsert);*/
			}
		}
	}

	private String replaceString(String toReplace){
		String[] token= toReplace.split(",");
		String result=token[0]+"."+token[1];
		return result;
	}
public ArrayList<AlignedTrace> parseNoConf(XLog log){
		
		ArrayList<AlignedTrace> alignedTraces= new ArrayList<AlignedTrace>();
		int traceCont = 0;
		for (XTrace trace : log) {
			traceCont++;
			int posEv = 0;
			ArrayList<String> eventNames = new ArrayList<String>();;
			String traceId=trace.getAttributes().get("concept:name").toString();
			ArrayList<Event> events = new ArrayList<Event>();
			for (XEvent event : trace) {
				
				String eventName = event.getAttributes().get("concept:name")
						.toString();
				eventName = eventName.replace(" ", "");
				String timestamp = null;
				String kind = "[L/M]";
				eventNames.add(eventName);
				posEv++;
				this.updateAlignedEventsFake(posEv, eventName, traceCont, traceId, timestamp, kind);
			}
			/*
			String valori = "";
			String eventi = "";
			for (int i = 0; i < posEv; i++) {
				valori = valori + "\"0,00\",";
				eventi = "[L/M]" + eventNames.get(i);
			}
			
			String alignment = traceCont + "," + traceId + ",1,Yes," + valori + "," + eventi;
			AlignedTrace alignedTrace= new AlignedTrace(traceId,alignment);
			alignedTraces.add(alignedTrace);*/
		}

		return alignedTraces;
		
	}
	
	private void updateAlignedEventsFake(Integer posTrace, String name, int traceCont, String traceId, String timestamp, String kind) {
		
		String queryInsert = "INSERT IGNORE INTO alignedevent(posTrace,class,trace,traceid,"
				+ "timestamp,kind)"
				+ " VALUES(?,?,?,?,?,?)";
		ArrayList<String> fieldList = new ArrayList<String>(
			Arrays.asList(String.valueOf(posTrace), name, String.valueOf(traceCont), traceId, timestamp,kind));
		dbManager.executeStatement(queryInsert, fieldList);
		}
	}
	

