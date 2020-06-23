package LogGenerator;

import utility.DBManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.out.XesXmlSerializer;

public class LogWriter {
	private int numTrace;
	private int anomalousPercentage;
	private ArrayList<Place> placesList;
	private ArrayList<String> filespathList;
	private String logFilePath;
	
	public LogWriter(int numTrace, int anomalousPercentage, String logFilePath) {
		super();
		this.numTrace = numTrace;
		this.anomalousPercentage = anomalousPercentage;
		this.logFilePath = logFilePath;
		placesList=initializePlaces();
	}
	
	public LogWriter(int numTrace,  ArrayList<String> filesPathList, String logFilePath) {
		super();
		this.numTrace = numTrace;
		this.filespathList=filesPathList;
		this.logFilePath = logFilePath;
	}
	
	private ArrayList<Place> initializePlaces() {
		ArrayList<Place> places= new ArrayList<Place>();
		DBManager dbManager= new DBManager("","loggenerator","","");
		Random random= new Random();
		int pathNumber= random.nextInt(2);
		String queryPlaces="SELECT id,path FROM places WHERE path=1";
		ArrayList<String> placesList= dbManager.executeQuery(queryPlaces, "id;path");
		for(String placeFields: placesList){
			String[] token= placeFields.split(";");
			String placeId=token[0];
			String placePath=token[1];
			String queryFollowers= "SELECT follower FROM places WHERE idplace="+placeId;
			ArrayList<String> followersList= dbManager.executeQuery(queryFollowers, "follower");
			Place place= new Place(followersList,Integer.parseInt(placeId),"uniform");
			place.setPath(Integer.parseInt(placePath));
			places.add(place);
		}
		return places;
	}
	
	public void writeLogFromExamples(int count){
		XesXmlParser xesParser= new XesXmlParser();
		XLog xlog=null;
		try {
			List<XLog> xlogList=xesParser.parse(new File(logFilePath));
			xlog=xlogList.get(0);
			XLog xlogIG=null;
			int logSize=xlog.size();
			int I1traces= count/3;
			for(int i=0; i<I1traces; i++){
				xlogIG= xesParser.parse(new File(filespathList.get(2))).get(0);
				for(XTrace traceIG: xlogIG){
					logSize++;
					traceIG.getAttributes().remove("concept:name");
					XAttributeLiteralImpl newAttribute= new XAttributeLiteralImpl("concept:name",String.valueOf(logSize));
					traceIG.getAttributes().put("concept:name", newAttribute);
				}
				xlog.addAll(xlogIG);
			}
			Random random= new Random();
			Random random2= new Random();
			for(int i=I1traces; i<count; i++){
				int pos=random.nextInt(2)+1;
					if(pos==1){
						//C->D, with and without cycle
						
						int pos2= random2.nextInt(2)+1;
						if(pos2==1){
							xlogIG= xesParser.parse(new File(filespathList.get(1))).get(0);
						}
						else if (pos2==2){
							xlogIG= xesParser.parse(new File(filespathList.get(4))).get(0);
						}
						
					}
					else if(pos==2){
						//C->E with and without cycle
						int pos2= random2.nextInt(2)+1;
						if(pos2==1){
							xlogIG= xesParser.parse(new File(filespathList.get(0))).get(0);
						}
						else if (pos2==2){
							xlogIG= xesParser.parse(new File(filespathList.get(3))).get(0);
						}
					}
				
	
					for(XTrace traceIG: xlogIG){
						logSize++;
						traceIG.getAttributes().remove("concept:name");
						XAttributeLiteralImpl newAttribute= new XAttributeLiteralImpl("concept:name",String.valueOf(logSize));
						traceIG.getAttributes().put("concept:name", newAttribute);
					}
					
				
				xlog.addAll(xlogIG);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XesXmlSerializer serializer= new XesXmlSerializer() ;
		try {
			serializer.serialize(xlog, new FileOutputStream("C:/Users/Laura/Desktop/Test/Synthethic/EsempiPaper/TestRete/ProveInSerie/NewLogs/"+numTrace+".xes"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeLog(){
		XesXmlParser xesParser= new XesXmlParser();
		XLog xlog=null;
		try {
			List<XLog> xlogList=xesParser.parse(new File(logFilePath));
			xlog=xlogList.get(0);
			int numAnTrace=numTrace/100 * anomalousPercentage;
			for(int i=0; i<numTrace; i++){
				if(i<numAnTrace){
					writeCorrectInstances(xlog);
				}
				else{
					XFactoryNaiveImpl impl= new XFactoryNaiveImpl();
					XTrace trace= impl.createTrace();
					XAttributeLiteralImpl newAttribute= new XAttributeLiteralImpl("concept:name",String.valueOf(i));
					trace.getAttributes().put("concept:name",newAttribute);
					ArrayList<String> insertedEvent=new ArrayList<String>();
					for(Place place: placesList){
						String event= place.picksAFollower();
						while(insertedEvent.contains(event)){
							event=place.picksAFollower();
						}
						if(!insertedEvent.contains(event) && !event.equals("tau")){
							XEvent xevent= impl.createEvent();
							XAttributeLiteralImpl eventName= new XAttributeLiteralImpl("concept:name",event);
							trace.getAttributes().put("concept:name",eventName);
							XAttributeLiteralImpl eventLC= new XAttributeLiteralImpl("lifecycle:transition","complete");
							trace.getAttributes().put("lifecycle:transition",eventLC);
							trace.add(xevent);
							insertedEvent.add(event);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private void writeCorrectInstances(XLog writingLog) {
		Random random= new Random();
		int instanceKind= random.nextInt(3);
		XesXmlParser xesParser= new XesXmlParser();
		XLog xlog=null;
		List<XLog> xlogList;
		switch(instanceKind){
		case 1:
			try {
				xlogList = xesParser.parse(new File("C:/Users/Laura/Desktop/LogGenerator/i1.xes"));
				xlog=xlogList.get(0);
				for(XTrace tLog: xlog){
					writingLog.add(tLog);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				xlogList = xesParser.parse(new File("C:/Users/Laura/Desktop/LogGenerator/i2.xes"));
				xlog=xlogList.get(0);
				for(XTrace tLog: xlog){
					writingLog.add(tLog);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				xlogList = xesParser.parse(new File("C:/Users/Laura/Desktop/LogGenerator/i3.xes"));
				xlog=xlogList.get(0);
				for(XTrace tLog: xlog){
					writingLog.add(tLog);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		
	}

	public int getNumTrace() {
		return numTrace;
	}
	public void setNumTrace(int numTrace) {
		this.numTrace = numTrace;
	}
	public int getAnomalousPercentage() {
		return anomalousPercentage;
	}
	public void setAnomalousPercentage(int anomalousPercentage) {
		this.anomalousPercentage = anomalousPercentage;
	}
	public String getLogFilePath() {
		return logFilePath;
	}
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	
}
