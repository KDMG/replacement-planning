package instancegraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jgraph.JGraph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

import initializer.IGInitializerCompleteLog;

public class IGBuilderUpdated {
	// this class brings some updates to the original IGBuilder, in particular during the graphs
	// building phase:
	// -it does not consider inserted events when creating the IGs (useful to prevent floating nodes)
	//- it deals with multiple parallelisms without sync points (it should)
	private XLog xlog;
	private PetriNetParser parser;
	private HashMap<Integer, org.jgrapht.DirectedGraph<String, DefaultEdge>> graphsSet = new HashMap<Integer, org.jgrapht.DirectedGraph<String, DefaultEdge>>();

	private DBManager dbManager;
	private ArrayList<AlignedTrace> conformanceResult;
	private HashMap<Integer, String> changedPred = new HashMap<Integer, String>();
	private Path paths=null;
	private HashMap<Integer, String> traceIdMapping;
	private HashMap<Integer, ArrayList<String>> traceIdMappingDisconnected;
	private HashMap<Integer, ArrayList<Anomaly>> anomalies;
	private HashMap<Integer, ArrayList<Event>> tracesList;
	private HashMap<Integer, ArrayList<Event>> tracesAlignedList;
	private HashMap<String, ArrayList<String>> CRList;
	private HashMap<String,String> mapping= new HashMap<String,String>();
	private HashMap<Integer,HashMap<Integer,Integer>> mappingAlignedOriginalList= new HashMap<Integer,HashMap<Integer,Integer>>();
	private HashMap<Integer,HashMap<Integer,Integer>> mappingAlignedOriginalDeletionList= new HashMap<Integer,HashMap<Integer,Integer>>();
	private boolean pluginCalled=false;
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> anomaliesConnections= new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> deletionReplacementConnections= new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();

	int startingTrace;
	int endTrace;
	private boolean rep;
	
	/*traceIdMapping 
	anomalies 
	tracesList 
	tracesAlignedList=
	CRList */

	
	public IGBuilderUpdated(XLog xlog, DBManager dbManager,
			ArrayList<AlignedTrace> conformanceResult, int startingTrace,
			int endTrace) {
		super();
		this.xlog = xlog;
		this.dbManager = dbManager;
		this.conformanceResult = conformanceResult;
		this.startingTrace = startingTrace;
		this.endTrace = endTrace;
		this.traceIdMappingDisconnected= new HashMap<Integer, ArrayList<String>>();
	}

	public IGBuilderUpdated(XLog xlog, DBManager dbManager,
			ArrayList<AlignedTrace> conformanceResult, boolean rep) {
		super();
		this.xlog = xlog;
		this.dbManager = dbManager;
		this.conformanceResult = conformanceResult;
		this.rep=rep;
		this.traceIdMappingDisconnected= new HashMap<Integer, ArrayList<String>>();
		this.pluginCalled=true;
	}
	
	public IGBuilderUpdated(XLog xlog2, DBManager dbManager2,
			ArrayList<AlignedTrace> conformanceResult2, int parseInt,
			int parseInt2, Path paths,HashMap<Integer, ArrayList<Event>> tracesList) {
		super();
		this.xlog = xlog2;
		this.dbManager = dbManager2;
		this.conformanceResult = conformanceResult2;
		this.startingTrace = parseInt;
		this.endTrace = parseInt2;
		this.tracesList = tracesList;
		this.traceIdMappingDisconnected= new HashMap<Integer, ArrayList<String>>();
		this.paths=paths;
	}

	public HashMap<Integer,org.jgrapht.DirectedGraph<String, DefaultEdge>> getGraphsSet() {
		return graphsSet;
	}
	
	
	private String checkTrace(String idTrace) {
		String result = "";
		for (AlignedTrace alignedTrace : conformanceResult) {
			String alignedId = alignedTrace.getTraceId();
			if (idTrace.equals(alignedId)) {
				result = alignedTrace.getAlingment();
				break;
			}
		}
		return result;
	}

	public void createIGSet() {
		int numTrace = 1;
		traceIdMapping = setTraceId();
		anomalies = setAnomalies();
		tracesAlignedList=setTracesAligned();
		CRList = setCRList();
		//the coverability code is temporarily frozen
//		boolean coverability=true;
//		if(coverability){
//			//storeCovGraph();
//		}
		// to update the IG algorith, first I have to store the aligned events
		/*if(Integer.valueOf(startingTrace)==null || Integer.valueOf(endTrace)==null ||(startingTrace==0 && endTrace==0)){
			//fake values
			startingTrace=0;
			endTrace=100000;
		}
		else
			rep=true;*/
		if(this.pluginCalled){
			this.startingTrace=0;
			this.endTrace=xlog.size()+1;
		}
		else{
			this.rep=true;
		}
		
		for (XTrace trace : xlog) {
			if (numTrace > startingTrace && numTrace < endTrace) {
			//if (numTrace > 1062 && numTrace < endTrace) {
			// DA CAMBIARE IN 0
			if(numTrace>0){
				DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
						DefaultEdge.class);
				// ArrayList<Event> eventsTrace = tracesList.get(numTrace);
				ArrayList<Event> eventsTrace = new ArrayList<Event>();
				for(Event event:tracesAlignedList.get(numTrace)){
					eventsTrace.add(event);
				}
				//I need to build a mapping between the aligned positions and the original positions
				HashMap<Integer,Integer>mappingAlignedOriginal=buildMappingAlignedOriginal(tracesAlignedList.get(numTrace), tracesList.get(numTrace), numTrace);
				this.mappingAlignedOriginalList.put(numTrace, mappingAlignedOriginal);
				//The trace is preprocessed before building the graph in order to remove the inserted
				//events. This ensures to build a graph which DOES NOT contain insertions
				ArrayList<Event> insertedEvents= new ArrayList<Event>();
				for(Event eventTrace: eventsTrace){
					if(isInserted(mappingAlignedOriginal,Integer.parseInt(eventTrace.getPos()),numTrace))
						insertedEvents.add(eventTrace);
				}
				int countWithAlignments=1;
				eventsTrace.removeAll(insertedEvents);
				for (Event eventTrace : eventsTrace) {
						eventTrace.setPosWithAlignments(String.valueOf(countWithAlignments));
						directedGraph.addVertex(eventTrace.getPos());
						countWithAlignments++;
				}
			
				int posEvent = 1;
				for (Event event : eventsTrace) {
					ArrayList<Event> succList = findEventSucc(event, eventsTrace, numTrace, posEvent);
						if (succList.size() > 0)
							for (Event succ : succList) {
								String realPos=eventsTrace.get(posEvent-1).getPos();
								directedGraph.addEdge(String.valueOf(realPos),
										succ.getPos());
							}
					posEvent++;
				}
				String traceId = trace.getAttributes().get("concept:name")
						.toString();
				boolean connected=true;
				if (anomalies.containsKey(numTrace)) {
					if(rep){
						directedGraph = repairGraph(directedGraph, numTrace, eventsTrace);
						ConnectivityInspector<String, DefaultEdge> conn= new ConnectivityInspector<String, DefaultEdge>(directedGraph);
						connected=conn.isGraphConnected();
						if(!connected){
							String queryInsertDisc="INSERT INTO disconnected(numtrace) VALUES("+numTrace+");";
							dbManager.queryUpdate(queryInsertDisc);
						}
					}
					else if(false){
					//when I do not repair, I can obtain disconnected graphs: I have to identify them
					 ConnectivityInspector<String, DefaultEdge> conn= new ConnectivityInspector<String, DefaultEdge>(directedGraph);
					 //connected=conn.isGraphConnected();
					 connected=true; //I set it as true in order to not separate the graphs
					 if(!connected){
						 List<Set<String>> subComp= conn.connectedSets();
						 ArrayList<DirectedSubgraph<String, DefaultEdge>> subList= new ArrayList<DirectedSubgraph<String, DefaultEdge>>();
						 Set<DefaultEdge> originalEdges= directedGraph.edgeSet();
						 for(Set<String> vertexSet: subComp){
							 ArrayList<DefaultEdge> subEdges= new ArrayList<DefaultEdge>();
							 for(DefaultEdge edge: originalEdges){
								 String source= directedGraph.getEdgeSource(edge);
								 String target= directedGraph.getEdgeTarget(edge);
								 if(vertexSet.contains(source) && vertexSet.contains(target))
									 subEdges.add(edge);
							 }
							 Set<DefaultEdge> edgesSet = Sets.newHashSet(subEdges);
							 DirectedSubgraph<String, DefaultEdge> subgraph= new DirectedSubgraph<String, DefaultEdge>(directedGraph, vertexSet, edgesSet);
							 subList.add(subgraph);
						 }
						 writeDisconnectedGraph(subList,numTrace,traceId, eventsTrace);
					 }
					}
				}
				InstanceGraph graph = new InstanceGraph(directedGraph,
						numTrace, traceId);
				/*//update 22/06: I need to check the not reordered graph
				FileWriter writerTest = null;
				try {
					writerTest = new FileWriter("graph1Prova.dot", false);
					writerTest.write("digraph G { \n");
					Set<String> posVertexesTest = directedGraph.vertexSet();
					for (String posVertex : posVertexesTest) {
						String queryName="SELECT class FROM alignedevent WHERE trace=1 AND posTrace="+posVertex;	
						String eventClass=dbManager.executeQuery(queryName, "class").get(0);
							try {
									writerTest.write(posVertex + " [label=\""
											+ eventClass+"-"+posVertex
											+ "\",color=black,fontcolor=black];\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							
						
					}
					Set<DefaultEdge> posEdges = directedGraph.edgeSet();
					for (DefaultEdge posEdge : posEdges) {
						String id1=directedGraph.getEdgeSource(posEdge);
						String id2=directedGraph.getEdgeTarget(posEdge);
						String edgeLabel=id1+"-"+id2;
						edgeLabel="[label=\""+edgeLabel+"\",color=black,fontcolor=black]";
						try {
							writerTest.write(directedGraph.getEdgeSource(posEdge)
									+ "->" + directedGraph.getEdgeTarget(posEdge) +" "+edgeLabel
									+ ";\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					writerTest.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				/// end test no reordered
				int idWf=createSubdueTable(graph);
				directedGraph=adjustNodesOrder(graph);
				FileWriter writer = null;
				String filePath="";
				if(!this.pluginCalled){
					if(this.paths==null)
						filePath =Constants.GRAPHPATH;
					else
						filePath=paths.getGraphDirectory();
				}
				else{
					File checkDir= new File("Graphs/");
					if(!checkDir.exists()){
						checkDir.mkdirs();
					}
					filePath=checkDir.getAbsolutePath();
				}
				filePath = filePath + idWf + ".dot";
				//String filePath="graph"+idWf+".dot";
				
				//check if file exists
				/*File checkFile= new File(filePath);
				if (!checkFile.exists()) {
					filePath=idWf+".dot";
				}*/
				
				//modifica 14_12: cambio il metodo di scrittura
				boolean writeFromTables=true;
				if(writeFromTables){
					if(!this.pluginCalled)
						createGraphFromSubdueTables(numTrace,filePath,writer);
					else{
						// I need to store the IG set anyway
						DirectedGraph<String, DefaultEdge> renamedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
								DefaultEdge.class);
							Set<String> posVertexes = directedGraph.vertexSet();
							String queryLabels="SELECT id,label FROM nodes WHERE id_wf="+numTrace;
							ArrayList<String> labelsList=dbManager.executeQuery(queryLabels, "id;label");
							HashMap<String,String> labelsMap= new HashMap<String,String>();
							for(String label: labelsList){
								String[] labelTokens= label.split(";");
								labelsMap.put(labelTokens[0], labelTokens[1]);
							}
							for (String posVertex : posVertexes) {
									String eventClass = labelsMap.get(posVertex);
									renamedGraph.addVertex(posVertex+"-"+eventClass);
							}
							Set<DefaultEdge> posEdges = directedGraph.edgeSet();
							for (DefaultEdge posEdge : posEdges) {
								udpateRenamedGraph(renamedGraph, directedGraph, posEdge);
								String edgeLabel=setEdgeLabel(directedGraph.getEdgeSource(posEdge),directedGraph.getEdgeTarget(posEdge), directedGraph, numTrace,2,labelsMap);
								edgeLabel="[label=\""+edgeLabel+"\",color=black,fontcolor=black]";
							}
							this.graphsSet.put(numTrace,renamedGraph);
					}
				}
				else if(!writeFromTables){
					if(!this.pluginCalled)
					try {
						writer = new FileWriter(filePath, false);
						writer.write("digraph G { \n");
					} catch (IOException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DirectedGraph<String, DefaultEdge> renamedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
						DefaultEdge.class);
					Set<String> posVertexes = directedGraph.vertexSet();
					//the pos of the nodes has been changed: hence, I cannot call the traceAligned to set the event label.
					String queryLabels="SELECT id,label FROM nodes WHERE id_wf="+numTrace;
					ArrayList<String> labelsList=dbManager.executeQuery(queryLabels, "id;label");
					HashMap<String,String> labelsMap= new HashMap<String,String>();
					for(String label: labelsList){
						String[] labelTokens= label.split(";");
						labelsMap.put(labelTokens[0], labelTokens[1]);
					}
					for (String posVertex : posVertexes) {
				//	for (Event event : tracesAlignedList.get(numTrace)) {
					//	if (event.getPos().equals(posVertex)) {
							String eventClass = labelsMap.get(posVertex);//event.getEventclass();
							renamedGraph.addVertex(posVertex+"-"+eventClass);
							if(!this.pluginCalled)
							try {
								writer.write(posVertex + " [label=\""
										+ eventClass
										+ "\",color=black,fontcolor=black];\n");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//break;
						//}
					
				}
				Set<DefaultEdge> posEdges = directedGraph.edgeSet();
				for (DefaultEdge posEdge : posEdges) {
					udpateRenamedGraph(renamedGraph, directedGraph, posEdge);
					String edgeLabel=setEdgeLabel(directedGraph.getEdgeSource(posEdge),directedGraph.getEdgeTarget(posEdge), directedGraph, numTrace,2,labelsMap);
					edgeLabel="[label=\""+edgeLabel+"\",color=black,fontcolor=black]";
					if(!this.pluginCalled)
					try {
						writer.write(directedGraph.getEdgeSource(posEdge)
								+ "->" + directedGraph.getEdgeTarget(posEdge) +" "+edgeLabel
								+ ";\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				this.graphsSet.put(numTrace,renamedGraph);
				if(!this.pluginCalled)
				try {
					writer.write("}");
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				System.out.println("done graph " + numTrace);
			
				
			}
			numTrace++;
		}
//		Set<Integer> traceDiscList= traceIdMappingDisconnected.keySet();
//		for(int traceDisc: traceDiscList){
//			ArrayList<String> idWfList=traceIdMappingDisconnected.get(traceDisc);
//			for(String idWf: idWfList){
//				String queryInsert="INSERT INTO traceiddisconnected VALUES("+traceDisc+","
//						+ " "+Integer.parseInt(idWf)+")";
//				dbManager.queryUpdate(queryInsert);
//			}
		}
	}


	private void createGraphFromSubdueTables(int i, String filePath,FileWriter writer){
		String queryNodes= "SELECT id,label FROM nodes WHERE id_wf="+i;
		//String queryNodes= "SELECT id,label FROM bigloanTemp ";
		ArrayList<String> nodesList= dbManager.executeQuery(queryNodes, "id;label");
		String queryEdges="SELECT id1,id2,label FROM edges WHERE id_wf="+i;
		//String queryEdges="SELECT id1,id2,label FROM bigedgesloanTemp ";
		ArrayList<String> edgesList=dbManager.executeQuery(queryEdges, "id1;id2;label");
		try{
			writer = new FileWriter(filePath, false);
			writer.write("digraph G { \n");
			for(String node: nodesList){
				String[] nodesToken=node.split(";");
				String nodeId=nodesToken[0];
				String nodeLabel=nodesToken[1];
				writer.write(nodeId + " [label=\"" + nodeLabel
						+ "\",color=black,fontcolor=black];\n");
			}
			for(String edge: edgesList){
				String[] edgeTokens=edge.split(";");
				String id1= edgeTokens[0];
				String id2= edgeTokens[1];
				String edgeLabel= edgeTokens[2];
				writer.write(id1
						+ "->" + id2
						+ "[label=\" "+ edgeLabel
						+ "\",color=black,fontcolor=black];\n");
			}
			writer.write("}\n");
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		}
	
	
	private HashMap<Integer,Integer> buildMappingAlignedOriginal(ArrayList<Event> alignedTrace,
			ArrayList<Event> originalTrace, int numTrace) {
		HashMap<Integer,Integer> mappingAlignedOriginal=new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> mappingAlignedOriginalDeletion=new HashMap<Integer,Integer>();
		String queryAlignedKind="SELECT posTrace,kind FROM alignedevent WHERE trace="+numTrace;
		ArrayList<String> alignedKindResList=dbManager.executeQuery(queryAlignedKind, "posTrace;kind");
		HashMap<Integer,String> alignedKind= new HashMap<Integer,String>();
		for(String alignedKindRes: alignedKindResList){
			String[] tokens=alignedKindRes.split(";");
			int posTrace= Integer.parseInt(tokens[0]);
			String kind=tokens[1];
			alignedKind.put(posTrace, kind);
		}
		int posOr=1;
		int numDeletion=0;
		for(Event eventAligned:alignedTrace){
			int posEventAligned=Integer.parseInt(eventAligned.getPos());
			String kind=alignedKind.get(posEventAligned);
			if(!kind.contains("M-REAL")){
				mappingAlignedOriginal.put(posEventAligned, posOr);
				posOr++;
			}
			else{
				//posOr+ numPreviousDeletions
				mappingAlignedOriginalDeletion.put(posEventAligned, posOr);
				numDeletion++;
			}
		}
		//dal punto di vista di codice è una cosa agghiacciante.
		this.mappingAlignedOriginalDeletionList.put(numTrace, mappingAlignedOriginalDeletion);
		return mappingAlignedOriginal;
	}

	private void udpateRenamedGraph(
			DirectedGraph<String, DefaultEdge> renamedGraph,
			DirectedGraph<String, DefaultEdge> directedGraph, DefaultEdge posEdge) {
		//method to set the edges of the renamed graph
		String originalSource=directedGraph.getEdgeSource(posEdge);
		String originalTarget=directedGraph.getEdgeTarget(posEdge);
		String newSource="";
		String newTarget="";
		for(String node: renamedGraph.vertexSet()){
			if(!newSource.equals("") && !newTarget.equals("")){
				renamedGraph.addEdge(newSource, newTarget);
				break;
			}
			String[] tokensNode= node.split("-");
			if(tokensNode[0].equals(originalSource)){
				newSource=node;
				if(!newTarget.equals("")){
					renamedGraph.addEdge(newSource, newTarget);
					break;
				}
			}
			else if(tokensNode[0].equals(originalTarget)){
				newTarget=node;
				if(!newSource.equals("")){
					renamedGraph.addEdge(newSource, newTarget);
					break;
				}
			}
			
		}
		
	}

	private HashMap<Integer, ArrayList<Event>> setTracesAligned() {
		HashMap<Integer, ArrayList<Event>> result= new HashMap<Integer, ArrayList<Event>>();
		String querySelectTraces="SELECT DISTINCT traceid FROM alignedevent";
		ArrayList<String> tracesIdList= dbManager.executeQuery(querySelectTraces, "traceid");
		for(String traceId: tracesIdList){
			String queryNumTrace="SELECT numTrace FROM traceid WHERE idTrace='"+traceId+"'";
			ArrayList<String> resultQueryNumTrace=dbManager.executeQuery(queryNumTrace, "numTrace");
			if(resultQueryNumTrace.size()==0)
				System.out.println("Error: tracedId "+traceId+"not existing");
			String numTrace= resultQueryNumTrace.get(0);
			ArrayList<Event> events = new ArrayList<Event>();
			String querySelectEvents="SELECT posTrace,class FROM alignedevent WHERE traceid='"+traceId+"'";
			ArrayList<String> eventListResult= dbManager.executeQuery(querySelectEvents, "posTrace;class");
			for(String eventItem: eventListResult){
				String[] eventsTokens= eventItem.split(";");
				String posTrace=eventsTokens[0];
				String eventClass=eventsTokens[1];
				Event event= new Event(posTrace, eventClass);
				events.add(event);
			}
			String queryUpdate="UPDATE alignedevent SET trace="+numTrace+" WHERE traceid='"+traceId+"'";
			dbManager.queryUpdate(queryUpdate);
			result.put(Integer.parseInt(numTrace), events);
		}
		return result;
	}

	private void storeCovGraph() {
		//The table covgraph has to be empty
		String queryDelete="TRUNCATE TABLE covgraph";
		dbManager.queryUpdate(queryDelete);
		// Reads the file *.sg and stores the pairs state/transition in the table cov
		//Only for the tsg files
		/*try (BufferedReader br = new BufferedReader(new FileReader(new File(Constants.COVGRAPH)))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       if(!line.startsWith(".") && line.startsWith("s")){
		    	   String[] tokensLine= line.split(" ");
		    	   if(tokensLine.length>2){
		    		   String sourcePlace=tokensLine[0];
		    		   String transition=tokensLine[1];
		    		   String targetPlace=tokensLine[2];
		    		   String queryInsert= "INSERT INTO covgraph(sourceplace,transition,targetplace)"
		    		   		+ "  VALUES('"+sourcePlace+"','"+transition+"','"+targetPlace+"');";
		    		   dbManager.queryUpdate(queryInsert);
		    	   }
		    	   else{
		    		   System.out.println("Line not readable "+line);
		    	   }

		       }
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		ArrayList<String> parStatesIds = new ArrayList<String>();
		try {
			//initializeDB();
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(Constants.COVGRAPH));
			doc.getDocumentElement().normalize();
			NodeList statesList = doc.getElementsByTagName("state");
			NodeList transitionsList=doc.getElementsByTagName("transition");
			//this list stores for each state the transitions for which the state is the source
			HashMap<String, ArrayList<String>> stateTargetsTransitionList= new HashMap<String, ArrayList<String>>();
			stateTargetsTransitionList=createStateIO(transitionsList, "source");
			//this list stores the states that are reached from the transitions previously found
			HashMap<String, ArrayList<String>> stateTargetList= new HashMap<String, ArrayList<String>>();
			stateTargetList=createStateIO(transitionsList, "target");
			for (int i = 0; i < statesList.getLength(); i++) {
				String stateId=getElementId(statesList,i);
				if(stateId.equals("state8"))
					System.out.println("Debug");
				String stateContent = statesList.item(i).getTextContent();
				stateContent=stateContent.substring(stateContent.indexOf("[")+1, stateContent.lastIndexOf("]"));
				String[] tokensTokens=stateContent.split(",");
				if(tokensTokens.length>1){
					ArrayList<String> stateTarget=stateTargetList.get(stateId);
					//condition: the state has to have as many tokens as many states can be reached from it
					if(tokensTokens.length==stateTarget.size())
						parStatesIds.add(stateId);
					}
				
				}
			HashMap<String,String> syncStates= new HashMap<String, String>();
			syncStates=findSyncStates(parStatesIds, stateTargetList);
			//After obtained the list of states involved by parallelism, I have to list the concrete transitions //
			ArrayList<Parallelism> parallelTransitions= new ArrayList<Parallelism>();
			for(String parState: parStatesIds){
				ArrayList<String> transitionsStateList=stateTargetsTransitionList.get(parState);
				for(String transition: transitionsStateList){
					Parallelism par= new Parallelism(transition, parState, null);
					ArrayList<String> parTrans=findParallelTrans(transition, transitionsStateList, parState,transitionsList);
					par.setParallelTransitions(parTrans);
					parallelTransitions.add(par);
				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			
		}
		//once the graph is stored, I can extract the parallel transitions, i.e. those transitions
		//which have the same sourceplace but different targetplaces
		@SuppressWarnings("unused")
		String querySelect="";
		
	}

	private HashMap<String, String> findSyncStates(
			ArrayList<String> parStatesIds, HashMap<String, ArrayList<String>> stateTargetList) {
		HashMap<String, String> result= new HashMap<String, String>();
		for(String stateId: parStatesIds){
			ArrayList<String> input= new ArrayList<String>();
			input.add(stateId);
			String syncState=findSyncStateIt(input, stateTargetList,0);
			result.put(stateId, syncState);
		}
		return result;
	}

	private String findSyncStateIt(ArrayList<String> input,
			HashMap<String, ArrayList<String>> stateTargetList, int step) {
		//terminate condition: all the states are the same after the first call
		String result="";
		if(checkInput(input) && step>0)
				return input.get(0);
		else{
			ArrayList<String> newInput= new ArrayList<String>();
			for(String in: input){
				ArrayList<String> targetList=stateTargetList.get(in);
				newInput.addAll(targetList);
			}
			step++;
			result=findSyncStateIt(newInput, stateTargetList,step);
		}
		return result;
	}

	private boolean checkInput(ArrayList<String> input) {
		Set<String> set= new HashSet<String>(input.size());
		int numRepl=0;
		boolean result=false;
		for(String el: input){
			if(set.isEmpty()){
				set.add(el);
			}
			else{
				if(!set.add(el))
					numRepl++;
			}
		}
		if(numRepl==input.size()-1)
			result=true;
		return result;
	}

	private ArrayList<String> findParallelTrans(String transition,
			ArrayList<String> transitionsStateList, String parState, NodeList transitionsGraphList) {
		ArrayList<String> result= new ArrayList<String>();
		ArrayList<String> checkList= new ArrayList<String>();
		String targetPlace=findTargetPlace(transition, transitionsGraphList,parState);
		for(String trans: transitionsStateList){
			if(!trans.equals(transition))
				checkList.add(trans);
		}
		for(String checkTran: checkList){
			String targetCand=findTargetPlace(checkTran, transitionsGraphList,parState);
			if(!targetCand.equals(targetPlace))
				result.add(checkTran);
		}
		return result;
	}

	private String findTargetPlace(String transition,
			NodeList transitionsGraphList, String parState) {
		String result="";
		for(int i=0; i<transitionsGraphList.getLength();i++){
			Node trans=transitionsGraphList.item(i);
			if(trans.getTextContent().equals(transition)){
				String source=trans.getAttributes().getNamedItem("source").getNodeValue();
				if(source.equals(parState)){
					 result=trans.getAttributes().getNamedItem("target").getNodeValue();
					 break;
				}
			}
		}
		return result;
	}

	private String findTransPlace(String transition, NodeList transitionsList) {
		// TODO Auto-generated method stub
		return null;
	}

	private HashMap<String, ArrayList<String>> createStateIO(
			NodeList transitionsList, String stateKind) {
		HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < transitionsList.getLength(); i++) {
			Node transition = transitionsList.item(i);
			if (stateKind.equals("source")) {
				String transitionId = transition.getAttributes()
						.getNamedItem("id").getNodeValue();
				String transitionSource = transition.getAttributes()
						.getNamedItem(stateKind).getNodeValue();
				String transitionName = transition.getTextContent();
				if (result != null && result.size() > 0) {
					ArrayList<String> oldList = result.get(transitionSource);
					if (oldList != null && oldList.size() > 0) {
						result.remove(transitionSource);
						oldList.add(transitionName);
						result.put(transitionSource, oldList);
					} else {
						oldList = new ArrayList<String>();
						oldList.add(transitionName);
						result.put(transitionSource, oldList);
					}
				} else {
					ArrayList<String> firstList = new ArrayList<String>();
					firstList.add(transitionName);
					result.put(transitionSource, firstList);
				}
			}
			
			else{
				String transitionTarget = transition.getAttributes()
						.getNamedItem(stateKind).getNodeValue();
				String transitionSource = transition.getAttributes()
						.getNamedItem("source").getNodeValue();
				if (result != null && result.size() > 0) {
					ArrayList<String> oldList = result.get(transitionSource);
					if (oldList != null && oldList.size() > 0) {
						result.remove(transitionSource);
						if(!oldList.contains(transitionTarget))
							oldList.add(transitionTarget);
						result.put(transitionSource, oldList);
					} else {
						oldList = new ArrayList<String>();
						oldList.add(transitionTarget);
						result.put(transitionSource, oldList);
					}
				} else {
					ArrayList<String> firstList = new ArrayList<String>();
					firstList.add(transitionTarget);
					result.put(transitionSource, firstList);
				}
			}
		}
		return result;
	}

	private void checkMultipleParallelism(
			DirectedGraph<String, DefaultEdge> directedGraph, int numTrace) {
		//first: find all edges which sharing at least one input node
		
		
	}

	private void writeDisconnectedGraph(
			List<DirectedSubgraph<String, DefaultEdge>> subList, int numTrace, String traceId, ArrayList<Event> events) {
		for(DirectedSubgraph<String, DefaultEdge> subgraph: subList){
		
		InstanceGraph graph = new InstanceGraph(subgraph,
				numTrace, traceId);
		int idWf=createSubdueTable(graph);
		FileWriter writer = null;
		String filePath = "";
		if(this.paths==null)
			filePath=Constants.GRAPHPATH;
		else
			filePath=paths.getGraphDirectory();
		filePath = filePath + idWf + ".dot";
		try {
			writer = new FileWriter(filePath, false);
			writer.write("digraph G { \n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DirectedGraph<String, DefaultEdge> renamedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		Set<String> posVertexes = subgraph.vertexSet();
		for (String posVertex : posVertexes) {
			for (Event event : events) {
				if (event.getPos().equals(posVertex)) {
					String eventClass = event.getEventclass();
					try {
						writer.write(posVertex + " [label=\""
								+ eventClass
								+ "\",color=black,fontcolor=black];\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		Set<DefaultEdge> posEdges = subgraph.edgeSet();
		for (DefaultEdge posEdge : posEdges) {
			try {
				writer.write(subgraph.getEdgeSource(posEdge)
						+ "->" + subgraph.getEdgeTarget(posEdge)
						+ ";\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			writer.write("}");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done graph " + numTrace);

		}
		
	}

	private DirectedGraph<String, DefaultEdge> repairGraph(
			DirectedGraph<String, DefaultEdge> directedGraph, int numTrace, ArrayList<Event> events) {
		//
		System.out.println("Repair graph " + numTrace);
		ArrayList<Anomaly> anList = anomalies.get(numTrace);
		anList= checkAnomalies(anList,numTrace, events);
		DirectedGraph<String, DefaultEdge> repairedGraph = AnomalousTraceRepair(
				anList, directedGraph, numTrace, events);
		return repairedGraph;
	}

	private ArrayList<Anomaly> checkAnomalies(ArrayList<Anomaly> anList,
			int numTrace, ArrayList<Event> trace) {
		// this method prevents the problems which arise when a deletion involves the last element in the sequence,
		//generated by the fact that in this case we don't have a follower
		ArrayList<Anomaly> result= new ArrayList<Anomaly>();
		int max=0;
		for(Event event: trace){
			if(Integer.parseInt(event.getPos()) > max)
				max=Integer.parseInt(event.getPos());
		}
		for(Anomaly an: anList){
			if(an.getPosAn()<=max)
				result.add(an);
		}
		return result;
	}

	private DirectedGraph<String, DefaultEdge> AnomalousTraceRepair(
			ArrayList<Anomaly> anList,
			DirectedGraph<String, DefaultEdge> directedGraph, int numTrace, ArrayList<Event> events) {
		//05-05-15; creates a text file to check if the special insertion repair rules
		// should be used
		PrintWriter writer = null;
		try {
			
			writer = new PrintWriter("the-file-name.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ATR rule: first repairs the deletion, and the repair the insertion
		DirectedGraph<String, DefaultEdge> repairedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		Set<String> vertexSet = directedGraph.vertexSet();
		for (String vertex : vertexSet) {
			repairedGraph.addVertex(vertex);
		}
		Set<DefaultEdge> edgeSet = directedGraph.edgeSet();
		for (DefaultEdge edge : edgeSet) {
			repairedGraph.addEdge(directedGraph.getEdgeSource(edge),
					directedGraph.getEdgeTarget(edge));
		}
		ArrayList<Anomaly> delList = new ArrayList<Anomaly>();
		ArrayList<Anomaly> insList = new ArrayList<Anomaly>();
		for (Anomaly anomaly : anList) {
			if (anomaly.getType().equals("deletion"))
				delList.add(anomaly);
			else if (anomaly.getType().equals("insertion"))
				insList.add(anomaly);
		}
		if(numTrace==101)
			System.out.println("stop");
		for (Anomaly deletion : delList) {
			//repairedGraph = deletionRepair(deletion, repairedGraph, numTrace);
			repairedGraph = deletionRepairWithAlignments(deletion, repairedGraph, numTrace);
		}
		for (Anomaly insertion : insList) {
	//		repairedGraph = insertionRepair(insertion, repairedGraph, numTrace, writer);
			repairedGraph = insertionRepairWithAlignments(insertion,events, repairedGraph, numTrace, writer);

		}
		writer.close();
		return repairedGraph;
	}

	private DirectedGraph<String, DefaultEdge> insertionRepairWithAlignments(
			Anomaly insertion, ArrayList<Event> events, DirectedGraph<String, DefaultEdge> repairedGraph, int numTrace,
			PrintWriter writer) {
		boolean par=checkParallelism(events, insertion,numTrace, writer);
		DirectedGraph<String, DefaultEdge> tempGraph= new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		for(String node: repairedGraph.vertexSet()){
			tempGraph.addVertex(node);
		}
		for(DefaultEdge edge: repairedGraph.edgeSet()){
			tempGraph.addEdge(repairedGraph.getEdgeSource(edge), repairedGraph.getEdgeTarget(edge));
		}
		//original insertion position
		int posAnInOr=insertion.getPosAn();
		int posAnFinOr= insertion.getPosAnFin();
		int predInOr=posAnInOr-1;
		int succInOr=posAnFinOr+1;
		//current insertion position
		HashMap<Integer,Integer> mappingAlingedOriginal=this.mappingAlignedOriginalList.get(numTrace);
		int posAnIn=getKeyByValue(mappingAlingedOriginal,posAnInOr);
		int posAnFin=getKeyByValue(mappingAlingedOriginal,posAnFinOr);
		//int predIn=getKeyByValue(mappingAlingedOriginal,predInOr);
		//int succIn=getKeyByValue(mappingAlingedOriginal,succInOr);
		//All the inserted nodes are inserted in the graph
		int i=posAnIn;
		while(i<posAnFin+1)
		{
			tempGraph.addVertex(String.valueOf(i));
			i++;
		}
		
		ArrayList<String> inputNodesInsertion= new ArrayList<String>();
		ArrayList<String> outputNodesInsertion= new ArrayList<String>();
		Set<DefaultEdge> removingInputEdges= new HashSet<DefaultEdge>();
		Set<DefaultEdge> removingOutputEdges= new HashSet<DefaultEdge>();
		String insertionPred=findPredecessor(tempGraph,posAnIn);
		String insertionSucc= findSuccessor(tempGraph,posAnFin);
		
		//parallelism variant check
				if(par){
					System.out.println("Insertion variant "+numTrace);
					Set<DefaultEdge> originalPredOutgoing= tempGraph.outgoingEdgesOf(insertionPred);
					ArrayList<DefaultEdge> removingOriginalPredOutgoing = new ArrayList<DefaultEdge>();
					for(DefaultEdge predOut: originalPredOutgoing){
						tempGraph.addEdge(String.valueOf(posAnFin), tempGraph.getEdgeTarget(predOut));
						removingOriginalPredOutgoing.add(predOut);
					}
					tempGraph.removeAllEdges(removingOriginalPredOutgoing);
					tempGraph.addEdge(insertionPred, String.valueOf(posAnIn));
					/*String posIn=String.valueOf(insertion.getPosAn());
					Set<DefaultEdge> incPar= tempGraph.incomingEdgesOf(posIn);
					ArrayList<DefaultEdge> lastRemoving= new ArrayList<DefaultEdge>();
					lastRemoving.addAll(incPar);
					Set<DefaultEdge> oldOut= tempGraph.outgoingEdgesOf(insertionPred);
					lastRemoving.addAll(oldOut);
					for(DefaultEdge incEd: lastRemoving){
						tempGraph.removeEdge(incEd);
					}
					tempGraph.addEdge(insertionPred, posIn);*/
					
				}
				else{
		if(insertionSucc!="")
			for(DefaultEdge inputEdge: tempGraph.incomingEdgesOf(String.valueOf(insertionSucc))){
				inputNodesInsertion.add(tempGraph.getEdgeSource(inputEdge));
				removingInputEdges.add(inputEdge);
			}
		if(insertionPred!="")
			for(DefaultEdge outputEdge: tempGraph.outgoingEdgesOf(String.valueOf(insertionPred))){
				outputNodesInsertion.add(tempGraph.getEdgeTarget(outputEdge));
				removingOutputEdges.add(outputEdge);
			}
		
		//remove old connections
		tempGraph.removeAllEdges(removingInputEdges);
		tempGraph.removeAllEdges(removingOutputEdges);
		
		for(String inputNode: inputNodesInsertion){
			tempGraph.addEdge(inputNode, String.valueOf(posAnIn));
		}
		
		for(String outputNode: outputNodesInsertion){
			tempGraph.addEdge(String.valueOf(posAnFin), outputNode);
		}
				}
		//add edges between insertedNodes
		for (int k = posAnIn; k < posAnFin; k++) {
			tempGraph.addEdge(String.valueOf(k), String.valueOf(k + 1));
		}
		//additional check for replacement in the first/last node
		boolean replacement=false;
		//insertion in head
		if(posAnInOr==1){
			ArrayList<Anomaly> anList = anomalies.get(numTrace);
			for(Anomaly an: anList){
				if(an.getType().equals("deletion")){
					if (an.getPosAn()==1 || an.getPosAnFin()==1){
						HashMap<Integer, ArrayList<Integer>> conn=anomaliesConnections.get(numTrace);
						int key=-1;
						for(Integer k: conn.keySet())
							key=k;
						ArrayList<Integer> connNodes= conn.get(key);
						for(Integer n: connNodes){
							tempGraph.addEdge(String.valueOf(posAnFin), String.valueOf(n));
							replacement=true;
						}
					}
				}
			}
			if(!replacement){
				tempGraph.addEdge(String.valueOf(posAnFin), String.valueOf(posAnFin+1));
			}
		}
		//insertion in tail 
		else if(insertionSucc==""){
			if(deletionReplacementConnections.containsKey(numTrace)){
			ArrayList<Anomaly> anList = anomalies.get(numTrace);
			for(Anomaly an: anList){
				if(an.getType().equals("deletion")){
					if(an.getPosAn()==insertion.getPosAn()){
						HashMap<Integer, ArrayList<Integer>> traceConnNodes=deletionReplacementConnections.get(numTrace);
						int key=-1;
						for(Integer k: traceConnNodes.keySet())
							key=k;
						ArrayList<Integer> connNodes= deletionReplacementConnections.get(numTrace).get(key);
						if(connNodes!=null && connNodes.size()>0)
							for(Integer n: connNodes){
								tempGraph.addEdge(String.valueOf(n),String.valueOf(posAnIn));
								replacement=true;
							}
					}
				}
				}
		} //normal insertion in tail
			else {
				tempGraph.addEdge(insertionPred, String.valueOf(posAnIn));
			}
		}
		
		return tempGraph;
	}
	
	public  <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}

	private String findSuccessor(DirectedGraph<String, DefaultEdge> tempGraph,
			int posAnFin) {
		String result="";
		Set<String> nodesList=tempGraph.vertexSet();
		int nodeChecked=0;
		int i=posAnFin+1;
		while(result.equals("") && nodeChecked<nodesList.size()){
			if(nodesList.contains(String.valueOf(i))){
				result=String.valueOf(i);
			}
			else{
				i++;
				nodeChecked++;
			}
		}
		
		return result;
	}

	private int findMaxIndex(Set<String> nodesList) {
		int result=-1;
		for(String node: nodesList){
			int pos=Integer.parseInt(node);
			if(pos>result)
				result=pos;
		}
		return result;
	}

	private String findPredecessor(
			DirectedGraph<String, DefaultEdge> tempGraph, int posAnIn) {
		String result="";
		Set<String> nodesList=tempGraph.vertexSet();
		for(int i=posAnIn-1; i>0; i--){
			if(nodesList.contains(String.valueOf(i)) && result.equals("")){
				result=String.valueOf(i);
				break;
			}
		}
		return result;
	}

	private DirectedGraph<String, DefaultEdge> deletionRepairWithAlignments(
			Anomaly deletion, DirectedGraph<String, DefaultEdge> repairedGraph,
			int numTrace) {
		// create and fill a temporary graph
		DirectedGraph<String, DefaultEdge> tempGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		for (String node : repairedGraph.vertexSet()) {
			tempGraph.addVertex(node);
		}
		for (DefaultEdge edge : repairedGraph.edgeSet()) {
			tempGraph.addEdge(repairedGraph.getEdgeSource(edge),
					repairedGraph.getEdgeTarget(edge));
		}
		int posAnInOr = deletion.getPosAn();
		// per la deletion non ti puoi affidare al mapping: NON ESISTE un evento nella traccia originale
		// che corrisponda a quello cancellato. O meglio, ne dovresti forse fare uno a parte? mi sa di si.
		// Del resto la posizione nel nuovo grafo ti serve.
	
		// I need the current deletion position
		HashMap<Integer, Integer> mappingAlignedOriginalDeletion = this.mappingAlignedOriginalDeletionList
				.get(numTrace);
		//uodate 27/06/16 : for deleted events I can have more than one aligned event. Here I need the SMALLEST one
		int posAnIn = getMinimumKeyValue(mappingAlignedOriginalDeletion, posAnInOr);
		//update 23/06
		//int posAnFin = findNumDeletedEvent(deletion,posAnIn,numTrace);
		int numDeletedEvents=deletion.getPosAnFin()-deletion.getPosAn();
		int posAnFin=numDeletedEvents+posAnIn;
		//
		//Update 23/06 --> do not delete intermediate nodes
		// All the nodes whose position is between posAnIn, posAnFin, are
		// removed, with relative connections
		/*for (int i = posAnIn + 1; i < posAnFin; i++) {
			tempGraph.removeVertex(String.valueOf(i));
		}*/
		
		// all the inputNodes of deleted nodes and all the outputNodes of
		// deleted nodes are stored
		ArrayList<String> inputNodesDeletion = new ArrayList<String>();
		ArrayList<String> outputNodesDeletion = new ArrayList<String>();
		ArrayList<Integer> headDelConnectionsInput= new ArrayList<Integer>();

		for (int i = posAnIn; i < posAnFin + 1; i++) {
			//update 23/06 consider all deleted nodes
			//if (i == posAnIn || i == posAnFin) {
				for (DefaultEdge inputEdge : tempGraph.incomingEdgesOf(String
						.valueOf(i))) {
					if(i==posAnIn){
						headDelConnectionsInput.add(Integer.parseInt(tempGraph.getEdgeSource(inputEdge)));
					}
					inputNodesDeletion.add(tempGraph.getEdgeSource(inputEdge));
				}
				//for managing deletion in head
				ArrayList<Integer> headDelConnections= new ArrayList<Integer>();

				for (DefaultEdge outputEdge : tempGraph.outgoingEdgesOf(String
						.valueOf(i))) {
					if(i == posAnFin){
						headDelConnections.add(Integer.parseInt(tempGraph.getEdgeTarget(outputEdge)));
					}
					outputNodesDeletion
							.add(tempGraph.getEdgeTarget(outputEdge));
				}
				if(posAnIn==1){
					HashMap<Integer, ArrayList<Integer>> conn= new HashMap<Integer, ArrayList<Integer>>();
					conn.put(posAnIn, headDelConnections);
					anomaliesConnections.put(numTrace, conn);
				}
				else if(i==posAnFin && tempGraph.outgoingEdgesOf(String.valueOf(i)).size()==0){
					if(headDelConnectionsInput.size()>0){
						HashMap<Integer, ArrayList<Integer>> conn= new HashMap<Integer, ArrayList<Integer>>();
						conn.put(posAnIn, headDelConnectionsInput);
						deletionReplacementConnections.put(numTrace, conn);
					}

				}
			//}
		}
		// I have to remove from the list of nodes those which correspond to
		// deleted events
		ArrayList<String> removeFromInputNodes = new ArrayList<String>();
		ArrayList<String> removeFromOutputNodes = new ArrayList<String>();
		for (String node : inputNodesDeletion) {
			for (int i = posAnIn; i < posAnFin + 1; i++) {
				if (Integer.parseInt(node) == i)
					removeFromInputNodes.add(node);
			}
		}
		inputNodesDeletion.removeAll(removeFromInputNodes);
		for (String node : outputNodesDeletion) {
			for (int i = posAnIn; i < posAnFin + 1; i++) {
				if (Integer.parseInt(node) == i){
					
					removeFromOutputNodes.add(node);
				}
			}
		}
		outputNodesDeletion.removeAll(removeFromOutputNodes);
		// Now the deleted nodes are removed; all inputNodes of posAnIn are
		// connected with
		// outputNodes of posAnFin
		
		for (int i = posAnIn; i < posAnFin + 1; i++) {
			boolean removed=tempGraph.removeVertex(String.valueOf(i));
			System.out.println(removed);
		}

		Collections.sort(inputNodesDeletion, new NumericStringComparator());
		Collections.reverse(inputNodesDeletion);
		Collections.sort(outputNodesDeletion, new NumericStringComparator());
		// if I have no input nodes, I do not connect the output nodes with anything; this is a problem, if I have also an insertion
		for (String inputNode : inputNodesDeletion) {
			for (String outputNode : outputNodesDeletion) {
				// the edge is created only if the nodes are not already linked
				// by a path
				List<DefaultEdge> path = DijkstraShortestPath.findPathBetween(
						tempGraph, inputNode, outputNode);
				if (path == null) {
					tempGraph.addEdge(inputNode, outputNode);
				}
			}
		}
		//problem with replacement of last node. In this case I don't have any output node, because it was the last of the aligned graph.
		//fix:
		
		return tempGraph;
	}

	private int getMinimumKeyValue(
			HashMap<Integer, Integer> mappingAlignedOriginalDeletion,
			int posAnInOr) {
		List<Integer> keysList= new ArrayList<Integer>();
		for(int key: mappingAlignedOriginalDeletion.keySet()){
			int v=mappingAlignedOriginalDeletion.get(key);
			if(v==posAnInOr)
				keysList.add(key);
		}
		int result=Collections.min(keysList);
		return result;
	}

	private int findNumDeletedEvent(Anomaly deletion, int posAnAligned, int numTrace) {
		int posAn=deletion.getPosAn();
		int res=posAnAligned;
		String queryDeletedEvents="SELECT count(*) as numdeleted FROM anomalies"
				+ " WHERE numTrace="+numTrace+" AND posEvent="+posAn+" AND type='deletion'";
		ArrayList<String> deletedEventsList=dbManager.executeQuery(queryDeletedEvents, "numdeleted");
		if(deletedEventsList.size()>0){
			int numDeletedEvent=Integer.parseInt(deletedEventsList.get(0));
			if(numDeletedEvent>0){
				if(numDeletedEvent!=1)
					res=posAnAligned+numDeletedEvent-1;
			}
		}
		return res;
	}

	private DirectedGraph<String, DefaultEdge> insertionRepair(
			Anomaly insertion,
			DirectedGraph<String, DefaultEdge> repairedGraph, int numTrace, PrintWriter writer, ArrayList<Event> events) {
		boolean par=checkParallelism(events, insertion,numTrace, writer);
		// E5 involves also E6 and E7, since their computation is similar
		ArrayList<DefaultEdge> e5 = findRemovingEdgesInsertion(repairedGraph,
				insertion, "e5", numTrace, null);
		ArrayList<DefaultEdge> removingEdges = new ArrayList<DefaultEdge>();
		removingEdges.addAll(e5);
		for (DefaultEdge removingEdge : removingEdges) {
			repairedGraph.removeEdge(removingEdge);
		}
		DirectedGraph<String, DefaultEdge> e1Graph = findAddingEdgesInsertion(
				repairedGraph, insertion, "e1", numTrace);
		Set<DefaultEdge> addingEdges = e1Graph.edgeSet();
		ArrayList<DefaultEdge> e4 = findRemovingEdgesInsertion(repairedGraph,
				insertion, "e4", numTrace, e1Graph);
		
		for(DefaultEdge e4Edge: e4){
			repairedGraph.removeEdge(e4Edge);
		}
		
		for (DefaultEdge addingEdge : addingEdges) {
			repairedGraph.addEdge(e1Graph.getEdgeSource(addingEdge),
					e1Graph.getEdgeTarget(addingEdge));
		}
		//in case of the insertion occurred in between parallelism, the edges have to be adjusted
		if(par){
			System.out.println("Insertion variant "+numTrace);
			String posIn=String.valueOf(insertion.getPosAn());
			Set<DefaultEdge> incPar= repairedGraph.incomingEdgesOf(posIn);
			ArrayList<DefaultEdge> lastRemoving= new ArrayList<DefaultEdge>();
			lastRemoving.addAll(incPar);
			Set<DefaultEdge> oldOut= repairedGraph.outgoingEdgesOf(String.valueOf(insertion.getPosAn()-1));
			lastRemoving.addAll(oldOut);
			for(DefaultEdge incEd: lastRemoving){
				repairedGraph.removeEdge(incEd);
			}
			repairedGraph.addEdge(String.valueOf(insertion.getPosAn()-1), posIn);
			
		}
		return repairedGraph;
	}

	private boolean checkParallelism(ArrayList<Event> events, Anomaly insertion, int numTrace,
			PrintWriter writer) {
		boolean result=false;
		int posIn=insertion.getPosAn();
		int posFin= insertion.getPosAnFin();
		// I have to find the real positional index of the inserted event
		//first temptative --> I do not remember why
//		for(Event event: events){
//			if(event.getPos().equals(String.valueOf(insertion.getPosAn()))){
//				posIn=events.indexOf(event);
//			}
//			else if(event.getPos().equals(String.valueOf(insertion.getPosAnFin()))){
//				posFin=events.indexOf(event);
//			}
//		}
		//second temptative: I consider the ''original'' trace
		ArrayList<Event> originalTrace=this.tracesList.get(numTrace);
		int pred=posIn-1;
		int foll=posFin+1;
		String classPred="";
		String classFoll="";
		for(Event event: originalTrace){
			if(Integer.parseInt(event.getPos())==pred){
				classPred=event.getEventclass();
			}
			else if(Integer.parseInt(event.getPos())==foll){
				classFoll=event.getEventclass();
			}
			
			if(classPred != "" && classFoll !="")
				break;
		}
		// check if no relation exists between these events
		if(CRList.get(classPred)!=null && CRList.get(classFoll)!=null)
			if(!CRList.get(classPred).contains(classFoll) && !CRList.get(classFoll).contains(classPred)){
				//I have also to check if the position of the pred/follower corresponds to a deletion of
				//an activity which actually has a CR with the predecessor
				boolean connectedEventDeleted=false;
				ArrayList<Anomaly> an=anomalies.get(numTrace);
				for(Anomaly a: an){
					if(a.getPosAn()==posIn || a.getPosAnFin()==posIn || a.getPosAnFin()==posFin || a.getPosAn()==posFin){
						if(a.getType().equals("deletion")){
							String deletedEv=a.getEventClass();
							if(CRList.get(classPred).contains(deletedEv) &&
									CRList.get(deletedEv).contains(classFoll))
								connectedEventDeleted=true;
						}
					}
					
				}
				if(!connectedEventDeleted){
					writer.println("par: trace "+numTrace+" pos1: "+pred+" pos2: "+foll+" class1: "+classPred+" class2: "+classFoll);
					result=true;
				}
			}
		return result;
	}

	private DirectedGraph<String, DefaultEdge> findAddingEdgesInsertion(
			DirectedGraph<String, DefaultEdge> repairedGraph,
			Anomaly insertion, String string, int numTrace) {
		DirectedGraph<String, DefaultEdge> tempGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		Set<String> vertexSet = repairedGraph.vertexSet();
		for (String vertex : vertexSet) {
			tempGraph.addVertex(vertex);
		}
		Set<DefaultEdge> edgesSet= repairedGraph.edgeSet();
		for(DefaultEdge edge: edgesSet){
			tempGraph.addEdge(repairedGraph.getEdgeSource(edge), repairedGraph.getEdgeTarget(edge));
		}
		int posInsIn = insertion.getPosAn();
		String insClassIn = insertion.getEventClass();
		int posInsFin = insertion.getPosAnFin();
		String insClassFin = insertion.getEventClassFin();
		int traceSize = tracesList.get(numTrace).size();
		// first: set E1
		for (int k = posInsFin + 1; k <= traceSize; k++) {
			if(!isInserted(k,numTrace)){
			String eventKClass = findEventClass(String.valueOf(k), numTrace);
			int n = posInsIn - 1;
			String eventNCLass = findEventClass(String.valueOf(n), numTrace);
			ArrayList<String> eventNCRList = CRList.get(eventNCLass);
			// condition: e_n in relation with e_k
			if (eventNCRList != null && eventNCRList.size() > 0)
				if (eventNCRList.contains(eventKClass)
						|| repairedGraph.containsEdge(String.valueOf(n),
								String.valueOf(k))) {
					List<DefaultEdge> path= DijkstraShortestPath.findPathBetween(tempGraph, String.valueOf(posInsFin), String.valueOf(k));
					if(path==null){
						tempGraph.addEdge(String.valueOf(posInsFin),
								String.valueOf(k));
						//Set<DefaultEdge> incomingK= tempGraph.incomingEdgesOf(String.valueOf(k));
						//last condition: no in edge for k, no out edge for posInsFin
//				if(incomingK.size()>0){
//					int validPos=posInsFin;
//					boolean after=false;
//					for(DefaultEdge incK: incomingK){
//						String sourceIncK= tempGraph.getEdgeSource(incK);
//						int posSourceIncK=Integer.parseInt(sourceIncK);
//						if(posSourceIncK > validPos && posSourceIncK < k){
//							after=true;
//							break;
//						}
//					}
//					if(!after){
//						tempGraph.addEdge(String.valueOf(posInsFin),
//								String.valueOf(k));
//					}
//					else{
//						boolean anom=true;
//						ArrayList<Anomaly> anomaliesTrace= anomalies.get(numTrace);
//						for(DefaultEdge incEdge: incomingK){
//							String kSource=tempGraph.getEdgeSource(incEdge);
//							for(Anomaly an: anomaliesTrace){
//								if(an.getPosAn()!=Integer.parseInt(kSource) && an.getPosAnFin()!=Integer.parseInt(kSource))
//									{anom=false;
//									break;
//									}
//							}
//							if(!anom)
//								break;
//						}
//						if(anom)
//							tempGraph.addEdge(String.valueOf(posInsFin),
//									String.valueOf(k));
//						}
//					}
											
						//else if(incomingK.size()==0)
							
					}
				}
		}
		}
		// second: set E2
		for (int k = posInsIn - 1; k > 0; k--) {
			if(!isInserted(k,numTrace)){
			String eventKClass = findEventClass(String.valueOf(k), numTrace);
			ArrayList<String> eventKCRList = CRList.get(eventKClass);
			int n = posInsFin + 1;
			String eventNCLass = findEventClass(String.valueOf(n), numTrace);
			if(eventKCRList!=null)
				if (eventKCRList.contains(eventNCLass)
					|| repairedGraph.containsEdge(String.valueOf(k),
							String.valueOf(n))) {
				List<DefaultEdge> path= DijkstraShortestPath.findPathBetween(tempGraph, String.valueOf(k), String.valueOf(posInsIn));
				if(path==null){
					//Set<DefaultEdge> outgoingK= tempGraph.outgoingEdgesOf(String.valueOf(k));
					//if(outgoingK.size()==0)
						tempGraph.addEdge(String.valueOf(k), String.valueOf(posInsIn));
//					else{
//						int validPos=posInsIn;
//						boolean before=false;
//						for(DefaultEdge outK: outgoingK){
//							String targetIncK= tempGraph.getEdgeTarget(outK);
//							int posTargetOutK=Integer.parseInt(targetIncK);
//							if(posTargetOutK < validPos && posTargetOutK > k){
//								before=true;
//								break;
//							}
//						}
//						if(!before){
//							tempGraph.addEdge(String.valueOf(k), String.valueOf(posInsIn));
//						}
//						else{
//							boolean anom=true;
//							ArrayList<Anomaly> anomaliesTrace= anomalies.get(numTrace);
//							for(DefaultEdge outK: outgoingK){
//								String targetIncK= tempGraph.getEdgeTarget(outK);
//								for(Anomaly an: anomaliesTrace){
//									if(an.getPosAn()!=Integer.parseInt(targetIncK) && an.getPosAnFin()!=Integer.parseInt(targetIncK))
//										{anom=false;
//										break;
//										}
//								}
//								if(!anom)
//									break;
//							}
//							if(anom)
//								tempGraph.addEdge(String.valueOf(k), String.valueOf(posInsIn));
//							}
//						
//					}
				}
			}
		}
		}

		// third: set E3
		for (int k = posInsIn; k < posInsFin; k++) {
			tempGraph.addEdge(String.valueOf(k), String.valueOf(k + 1));
		}
		//before returning the outcome, I have to remove the common edges
		ArrayList<DefaultEdge> removingEdges= new ArrayList<DefaultEdge>();
		for(DefaultEdge tempEdge: tempGraph.edgeSet()){
			if(repairedGraph.containsEdge(tempGraph.getEdgeSource(tempEdge), tempGraph.getEdgeTarget(tempEdge)))
				removingEdges.add(tempEdge);
		}
		for(DefaultEdge edge: removingEdges){
			tempGraph.removeEdge(edge);
		}
		return tempGraph;
	}
	
	//override for compatibility
	private boolean isInserted(int k, int numTrace) {
		// check if the k-th event of a trace is an inserted event
		//note: you have to check if the ORIGINAL position of the event is an insertion!!!
		ArrayList<Anomaly> traceAn= anomalies.get(numTrace);
		boolean inserted=false;
		if(traceAn!=null)
			for(Anomaly anomaly: traceAn){
				int posIn=anomaly.getPosAn();
				int posFin=anomaly.getPosAnFin();
				if(posIn <= k && k <= posFin){
					if(anomaly.getType().equals("insertion"))
					{
						inserted=true;
						break;
					}
				}
			}
		return inserted;
	}
//method used in the new repaired procedure
	private boolean isInserted(
			HashMap<Integer, Integer> mappingAlignedOriginal, int posAligned,
			int numTrace) {
		// check if the k-th event of a trace is an inserted event
		// note: you have to check if the ORIGINAL position of the event is an
		// insertion!!!
		boolean inserted = false;
		Set<Integer> keySet = mappingAlignedOriginal.keySet();
		if (keySet.contains(posAligned)) {
			int k = mappingAlignedOriginal.get(posAligned);
			ArrayList<Anomaly> traceAn = anomalies.get(numTrace);
			if (traceAn != null)
				for (Anomaly anomaly : traceAn) {
					int posIn = anomaly.getPosAn();
					int posFin = anomaly.getPosAnFin();
					if (posIn <= k && k <= posFin) {
						if (anomaly.getType().equals("insertion")) {
							inserted = true;
							break;
						}
					}
				}
		}
		return inserted;
	}

	private ArrayList<DefaultEdge> findRemovingEdgesInsertion(
			DirectedGraph<String, DefaultEdge> repairedGraph,
			Anomaly insertion, String set, int numTrace,
			DirectedGraph<String, DefaultEdge> e1Graph) {
		ArrayList<DefaultEdge> removingEdges = new ArrayList<DefaultEdge>();
		int posInsIn = insertion.getPosAn();
		String insClassIn = insertion.getEventClass();
		int posInsFin = insertion.getPosAnFin();
		String insClassFin = insertion.getEventClassFin();
		switch (set) {
		case "e4":
			for (int k = posInsIn - 1; k > 0; k--) {
				Set<DefaultEdge> edgesOut = repairedGraph
						.outgoingEdgesOf(String.valueOf(k));
				for (DefaultEdge edgeOut : edgesOut) {
					String target = repairedGraph.getEdgeTarget(edgeOut);
					if (Integer.parseInt(target) > posInsFin) {
						// first condition: (e_k, e_i) in E1
						// second condition: (e_fin, e_n) in E1
						if((e1Graph.containsEdge(String.valueOf(k), String.valueOf(posInsIn)))
								&& (e1Graph.containsEdge(String.valueOf(posInsFin), String.valueOf(target)))){
							removingEdges.add(edgeOut);
						}
//							String e1Source = e1Graph.getEdgeSource(e1Edge);
//							if (e1Source.equals(String.valueOf(k))) {
//								
//								//String ei = e1Graph.getEdgeTarget(e1Edge);
////								ArrayList<String> targetList = new ArrayList<String>();
////								for (DefaultEdge e1EdgeIn : e1Graph.edgeSet()) {
////									if (e1Graph.getEdgeSource(e1EdgeIn).equals(
////											ei))
////										targetList.add(e1Graph
////												.getEdgeTarget(e1EdgeIn));
////								}
////								for (String originalTarget : targetList) {
////									removingEdges.add(repairedGraph.getEdge(e1Source,
////											originalTarget));
////								}
//							}
//
//						}

					}
				}
			}
			
			// for(int k=posInsIn-1; k>0; k--){
			// Set<DefaultEdge> edgesOut=
			// repairedGraph.outgoingEdgesOf(String.valueOf(k));
			// for(DefaultEdge edgeOut: edgesOut){
			// String target= repairedGraph.getEdgeTarget(edgeOut);
			// // condition: edge (k,j) with k < posInsIn && j > posInsFin
			// if(Integer.parseInt(target)>posInsFin){
			// removingEdges.add(edgeOut);
			// }
			// }
			// }
			break;
		case "e5":
			for (int k = posInsIn; k < posInsFin + 1; k++) {
				Set<DefaultEdge> edgesOut = repairedGraph
						.outgoingEdgesOf(String.valueOf(k));
				for (DefaultEdge edgeOut : edgesOut) {
					removingEdges.add(edgeOut);
				}
				Set<DefaultEdge> edgesIn = repairedGraph.incomingEdgesOf(String
						.valueOf(k));
				for (DefaultEdge edgeIn : edgesIn) {
					removingEdges.add(edgeIn);
				}
			}
			break;
		}
		return removingEdges;
	}

	private DirectedGraph<String, DefaultEdge> deletionRepair(Anomaly deletion,
			DirectedGraph<String, DefaultEdge> repairedGraph, int numTrace) {
		// E_fin= (E/(E_1 U E_2)) U E_3
		ArrayList<DefaultEdge> e1 = findRemovingEdgesDeletion(repairedGraph,
				deletion, "e1", numTrace);
		ArrayList<DefaultEdge> e2 = findRemovingEdgesDeletion(repairedGraph,
				deletion, "e2", numTrace);
		ArrayList<DefaultEdge> removingEdges = new ArrayList<DefaultEdge>();
		removingEdges.addAll(e1);
		removingEdges.addAll(e2);
		for (DefaultEdge edge : removingEdges) {
			repairedGraph.removeEdge(edge);
		}
		// for e3 I have to take an entire graph, for jgrapht implementation
		DirectedGraph<String, DefaultEdge> e3Graph = findAddingEdgesDeletion(
				repairedGraph, deletion, "e3", numTrace);
		Set<DefaultEdge> e3 = e3Graph.edgeSet();
		for (DefaultEdge addingEdge : e3) {
			repairedGraph.addEdge(e3Graph.getEdgeSource(addingEdge),
					e3Graph.getEdgeTarget(addingEdge));
		}
		return repairedGraph;
	}

	private DirectedGraph<String, DefaultEdge> findAddingEdgesDeletion(
			DirectedGraph<String, DefaultEdge> repairedGraph, Anomaly deletion,
			String string, int numTrace) {
		DirectedGraph<String, DefaultEdge> tempGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		Set<String> vertexSet = repairedGraph.vertexSet();
		for (String vertex : vertexSet) {
			tempGraph.addVertex(vertex);
		}
		Set<DefaultEdge> edgeSet= repairedGraph.edgeSet();
		for(DefaultEdge edgeRG: edgeSet){
			tempGraph.addEdge(repairedGraph.getEdgeSource(edgeRG), repairedGraph.getEdgeTarget(edgeRG));
		}
		String posDel = String.valueOf(deletion.getPosAn());
		String delClassIn = deletion.getEventClass();
		String delClassFin = deletion.getEventClassFin();
		String eventIClass = findEventClass(posDel, numTrace);
		int traceSize = tracesList.get(numTrace).size();
		for (int k = Integer.parseInt(posDel) - 1; k > 0; k--) {
			for (int j = Integer.parseInt(posDel); j < traceSize+1; j++) {
				// I have to check four conditions on the edge (k,j)
				String checkSource = String.valueOf(k);
				String checkTarget = String.valueOf(j);
				// first condition: e_k in relation with e_d1
				String eventKClass = findEventClass(checkSource, numTrace);
				ArrayList<String> eventKCRList = CRList.get(eventKClass);
				if (eventKCRList != null && eventKCRList.size() > 0)
					if (eventKCRList.contains(delClassIn)) {
						// second condition: e_dn in relation with e_j
						String eventJClass = findEventClass(checkTarget,
								numTrace);
						ArrayList<String> eventDNCRList = CRList
								.get(delClassFin);
						if (eventDNCRList.size() > 0)
							if (eventDNCRList.contains(eventJClass)) {
								// third condition: there isn't already a path
								// between ek, ej
								List<DefaultEdge> path = DijkstraShortestPath
										.findPathBetween(tempGraph,
												checkSource, checkTarget);
								if (path == null) {
									//final condition: not both the two nodes are not already
									//involved in other edges
									if(tempGraph.incomingEdgesOf(checkTarget).size()==0
											|| tempGraph.outgoingEdgesOf(checkSource).size()==0)
									tempGraph.addEdge(checkSource, checkTarget);

								}
							}

					}
			}
		}
		// I want to remove thise edges which are already present in the repairedGraph
		ArrayList<DefaultEdge> removingEdges= new ArrayList<DefaultEdge>();
		for(DefaultEdge tempEdge: tempGraph.edgeSet()){
			if(repairedGraph.containsEdge(tempGraph.getEdgeSource(tempEdge), tempGraph.getEdgeTarget(tempEdge))){
				removingEdges.add(tempEdge);
			}
		}
		for(DefaultEdge remEdge: removingEdges){
			tempGraph.removeEdge(remEdge);
		}
		return tempGraph;
	}

	private ArrayList<DefaultEdge> findRemovingEdgesDeletion(
			DirectedGraph<String, DefaultEdge> repairedGraph, Anomaly deletion,
			String set, int numTrace) {
		// TODO Auto-generated method stub
		ArrayList<DefaultEdge> removingEdges = new ArrayList<DefaultEdge>();
		String posDel = String.valueOf(deletion.getPosAn());
		String delClassIn = deletion.getEventClass();
		String delClassFin = deletion.getEventClassFin();
		String eventIClass = findEventClass(posDel, numTrace);
		switch (set) {
		case "e1":
			Set<DefaultEdge> edges = repairedGraph.incomingEdgesOf(posDel);
			for (DefaultEdge edge : edges) {
				String source = repairedGraph.getEdgeSource(edge);
				String eventKClass = findEventClass(source, numTrace);
				ArrayList<String> eventCR = CRList.get(eventKClass);
				if (eventCR.size() > 0){
					// first condition: e_k has a successor between k+1 and e_din
					//if (eventCR.contains(delClassIn)) {
						// second condition: e_dn in a relation with e_i
						ArrayList<String> delEventCR = CRList.get(delClassFin);
						if (delEventCR.size() > 0)
							if (delEventCR.contains(eventIClass)){
								boolean hasSucc=false;
								if(eventCR.contains(delClassIn))
									// if delIn is a succ of k, hasSucc is imm. true
									hasSucc=true;
								else{
									//otherwise one has to check the events between k and i
									int k= Integer.parseInt(source);
									for(int j=k+1; j< deletion.getPosAn(); j++){
										String eventJClass= findEventClass(String.valueOf(j), numTrace);
										if(eventCR.contains(eventJClass)){
											hasSucc=true;
											break;
										}
									}
								}
								if(hasSucc)
									removingEdges.add(edge);
							}
				}
					//}
			}
			break;
		case "e2":
			for (int i = Integer.parseInt(posDel)-1; i > 0; i--) {
				Set<DefaultEdge> edgesOut = repairedGraph
						.outgoingEdgesOf(String.valueOf(i));
				for (DefaultEdge egdeOut : edgesOut) {
					String target = repairedGraph.getEdgeTarget(egdeOut);
					if (Integer.parseInt(target) > Integer.parseInt(posDel)) {
						String eventKClass = findEventClass(String.valueOf(i),
								numTrace);
						ArrayList<String> eventKCRList = CRList
								.get(eventKClass);
						// first condition: e_k in relation with e_d1
						if (eventKCRList.size() > 0)
							if (eventKCRList.contains(delClassIn)) {
								// second condition: e_dn in relation with e_i
								ArrayList<String> eventDNCRList = CRList
										.get(delClassFin);
								if (eventDNCRList.size() > 0)
									if (eventDNCRList.contains(eventIClass)){
										//final condition: exists e_l, such that (e_l, e_j) in E
										Set<DefaultEdge> otherTargetEdges= repairedGraph.incomingEdgesOf(target);
										if(otherTargetEdges.size()>0){
											for(DefaultEdge otherEdge: otherTargetEdges){
												String otherEdgeSource= repairedGraph.getEdgeSource(otherEdge);
												if(!otherEdgeSource.equals(String.valueOf(i))){
													removingEdges.add(egdeOut);
													break;
												}
											}
										}
									}
							}
					}
				}
			}
			break;
		}
		return removingEdges;
	}

	private String findEventClass(String source, int numTrace) {
		// find the class of an event starting from its position and its trace
		// number
		String eventClass = "";
		ArrayList<Event> events=tracesList.get(numTrace);
		for (Event event : events) {
			String posEvent = String.valueOf(event.getPos());
			if (posEvent.equals(source)) {
				eventClass = event.getEventclass();
				break;
			}

		}
		return eventClass;
	}

	private int createSubdueTable(InstanceGraph graph) {
		//
		//I have to recover the previous maximum id_wf, rather than using numTrace
		//to face with possible disconnected graphs
		String query_idWf="SELECT max(id_wf) as id_wf FROM nodes";
		String maxId=dbManager.executeQuery(query_idWf, "id_wf").get(0);
		if(maxId==null){
			maxId="1";
		}
		else{
			maxId= String.valueOf(Integer.parseInt(maxId)+1);
		}
		// The number of nodes have to be updated: SUBDUE CANNOT DEAL WITH NOT ORDERED/MISSING NODES
		DirectedGraph<String, DefaultEdge> graphUpdatedPositions=adjustNodesOrder(graph);
		setNodesTable(graph, maxId, graphUpdatedPositions);
		setEdgesTable(graph, maxId, graphUpdatedPositions);
		ArrayList<String> updateId= new ArrayList<String>();
		if(traceIdMappingDisconnected.containsKey(graph.getNumTrace())){
			updateId= traceIdMappingDisconnected.get(graph.getNumTrace());
			traceIdMappingDisconnected.remove(graph.getNumTrace());
		}
		updateId.add(maxId);
		traceIdMappingDisconnected.put(graph.getNumTrace(), updateId);
		return Integer.parseInt(maxId);
	}

	private DirectedGraph<String, DefaultEdge> adjustNodesOrder(InstanceGraph graph) {
		mapping.clear();
		DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		DirectedGraph<String, DefaultEdge> directedGraphOld=graph.getGraph();
		ArrayList<String> orderedNodesSet= new ArrayList<String>();
		Set<String> nodesSet= directedGraphOld.vertexSet();
		orderedNodesSet.addAll(nodesSet);
		Collections.sort(orderedNodesSet, new NumericStringComparator());
		HashMap<String, String> mappningOldNew= new HashMap<String, String>();
		int count=1;
		//update 22/06
		for(String node: orderedNodesSet){
			directedGraph.addVertex(String.valueOf(count));
			//new node value [key] --> old node value [value]
			mapping.put(String.valueOf(count),node);
			//old node value [key] --> new node value [value]. Only used in this method.
			mappningOldNew.put(node, String.valueOf(count));
			count++;
		}
		Set<DefaultEdge> edgesSet= directedGraphOld.edgeSet();
		for(DefaultEdge edge: edgesSet){
			
			String oldSource= directedGraphOld.getEdgeSource(edge);
			String oldTarget=directedGraphOld.getEdgeTarget(edge);
			String newSource=mappningOldNew.get(oldSource);
			String newTarget=mappningOldNew.get(oldTarget);
			directedGraph.addEdge(newSource, newTarget);
		}
		return directedGraph;
	}

	private void setEdgesTable(InstanceGraph instanceGraph, String maxId, DirectedGraph<String, DefaultEdge> graphUpdatedPositions) {
		DirectedGraph<String, DefaultEdge> graph = graphUpdatedPositions;
		int numTrace = instanceGraph.getNumTrace();
		String idTrace = instanceGraph.getTraceId();
		Set<DefaultEdge> edgesSet = graph.edgeSet();
		for (DefaultEdge edge : edgesSet) {
			String id1 = graph.getEdgeSource(edge);
			String id2 = graph.getEdgeTarget(edge);
			String label = setEdgeLabel(id1, id2, graph, numTrace,1,null);
			String dir = "e";
			String statementInsert = "INSERT INTO edges(id1,id2,label,dir,id_wf) VALUES(?,?,?,?,?)";
			ArrayList<String> fieldList = new ArrayList<String>(Arrays.asList(
					id1, id2, label, dir, maxId));
			dbManager.executeStatement(statementInsert, fieldList);
		}
	}

	private String setEdgeLabel(String id1, String id2, DirectedGraph<String, DefaultEdge> graph,
			int numTrace, int flag, HashMap<String, String> labelsMap) {
		// seq= sequence; s=split; j=join
		// an edge is a sequence only if: the source node has only one outcome
		// edge and the
		// the target node has only one income edge

		String edgeLabel = "";
		String edgeType = "";
		if (graph.outgoingEdgesOf(id1).size() > 1)
			edgeType = "s";
		else if (graph.incomingEdgesOf(id2).size() > 1)
			edgeType = "j";
		else
			edgeType = "seq";
		String id1Label = "";
		String id2Label = "";
		if(flag==1){
		String oldId1=mapping.get(id1);
		// recover the name of the source/target events
		String queryId1Label = "SELECT class FROM alignedevent WHERE trace="
				+ numTrace + " AND posTrace=" + oldId1;
		ArrayList<String> labelListId1 = dbManager.executeQuery(queryId1Label,
				"class");
		if (labelListId1 != null)
			id1Label = labelListId1.get(0);
		
		else
			System.out.println("event class not found");
		}
		else{
			id1Label=labelsMap.get(id1);
			/*
			String queryId1Label = "SELECT class FROM alignedevent WHERE trace="
					+ numTrace + " AND posTrace=" + id1;
			ArrayList<String> labelListId1 = dbManager.executeQuery(queryId1Label,
					"class");
			if (labelListId1 != null)
				id1Label = labelListId1.get(0);
			
			else
				System.out.println("event class not found");*/
		}
		if(flag==1){
			String oldId2=mapping.get(id2);
		String queryId2Label = "SELECT class FROM alignedevent WHERE trace="
				+ numTrace + " AND posTrace=" + oldId2;
		ArrayList<String> labelListId2 = dbManager.executeQuery(queryId2Label,
				"class");
		if (labelListId2 != null)
			id2Label = labelListId2.get(0);
		else
			System.out.println("event class not found");
		}
		else{
			id2Label=labelsMap.get(id2);
	/*	String queryId2Label = "SELECT class FROM alignedevent WHERE trace="
				+ numTrace + " AND posTrace=" + id2;
		ArrayList<String> labelListId2 = dbManager.executeQuery(queryId2Label,
				"class");
		if (labelListId2 != null)
			id2Label = labelListId2.get(0);
		else
			System.out.println("event class not found");*/
		}
		//edgeLabel = edgeType + "_" + id1Label + "_" + "_" + id2Label;
		edgeLabel = id1Label + "_" + "_" + id2Label;
		return edgeLabel;
	}

	private void setNodesTable(InstanceGraph instanceGraph, String maxId, DirectedGraph<String, DefaultEdge> graphUpdatedPositions) {
		DirectedGraph<String, DefaultEdge> graph = graphUpdatedPositions;
		int numTrace = instanceGraph.getNumTrace();
		String idTrace = instanceGraph.getTraceId();
		Set<String> vertexes = graph.vertexSet();
		Iterator<String> it = vertexes.iterator();
		while (it.hasNext()) {
			String vertex = (String) it.next();
			String nodeLabel = "";
			String nodeResource = "";
			String startTime = "";                          //modificato
			String queryNodeLabel = "SELECT class FROM event WHERE trace="
					+ numTrace + " AND posTrace=" + vertex;
			ArrayList<String> labelList = dbManager.executeQuery(
					queryNodeLabel, "class");
			if (labelList != null)
				nodeLabel = labelList.get(0);
			else
				System.out.println("event class not found");
			String queryNodeResource = "SELECT resource FROM event WHERE trace="
					+ numTrace + " AND posTrace=" + vertex;
			ArrayList<String> resourceList = dbManager.executeQuery(
					queryNodeResource, "resource");
			if (resourceList != null)
				nodeResource = resourceList.get(0);
			else
				System.out.println("event resource not found");
			String queryTime = "SELECT startTime FROM event WHERE trace="
					+ numTrace + " AND posTrace=" + vertex;
			ArrayList<String> timeList = dbManager.executeQuery(
					queryTime, "startTime");
			if (timeList != null)
				startTime = timeList.get(0);
			else
				System.out.println("event time not found");
			String queryInsert = "INSERT INTO nodes(id,label,id_wf,resource,startTime) VALUES(?,?,?,?,?)";
			ArrayList<String> fieldList = new ArrayList<String>(Arrays.asList(
					vertex, nodeLabel, maxId, nodeResource, startTime));
			dbManager.executeStatement(queryInsert, fieldList);
		}
		// just to store the id of the trace, together with its position in the
		// log.
		// Useful to recover and analyze a specific trace --> shift in another
		// method
		// String
		// queryInsertMapping="INSERT INTO traceid(numTrace,idTrace) VALUES(?,?)";
		// ArrayList<String> fieldListMap = new
		// ArrayList<String>(Arrays.asList(String.valueOf(numTrace),idTrace));
		// dbManager.executeStatement(queryInsertMapping, fieldListMap);

	}

	private HashMap<String, ArrayList<String>> setCRList() {
		// this method stores in the global variable CRList the causal relations
		// set
		HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		String queryCR1 = "SELECT distinct source FROM causalrel";
		ArrayList<String> classList = dbManager
				.executeQuery(queryCR1, "source");
		if (classList.size() > 0)
			for (String eventClass : classList) {
				String queryCR2 = "SELECT distinct target FROM causalrel WHERE source LIKE  '"
						+ eventClass + "'";
				ArrayList<String> targetList = dbManager.executeQuery(queryCR2,
						"target");
				result.put(eventClass, targetList);
			}
		return result;
	}

	private ArrayList<Event> findEventSucc(Event event, ArrayList<Event> trace,
			int numTrace, int posEvent) {
		// for each event in trace
		// 1) finds the list of successors for which a CR exists
		// 2) checks the two conditions of the IG building procedure
		int traceEnd = trace.size();
		ArrayList<Event> causalSucc = checkCausalRel(trace, event, posEvent,
				numTrace);
		ArrayList<Event> targetNodes = checkOrConditions(posEvent, causalSucc,
				trace, numTrace);
		return targetNodes;
	}

	private ArrayList<Event> checkOrConditions(int posEvent,
			ArrayList<Event> causalSucc, ArrayList<Event> trace, int numTrace) {
		// instance ordering implementation: between posEvent and its follower:
		// 1) first condition: no causal succ of posEvent
		// 2) second condition: no causal pred of the follower
		ArrayList<Event> result = new ArrayList<Event>();
		
		//find the first of the events which are causalSucc of event in posEvent
		int firstSucc = findFirstSucc(causalSucc, numTrace);
		for (Event candidateFollower : causalSucc) {
			boolean firstCheck = false;
			boolean secondCheck = false;
			// first checke
			if (Integer.parseInt(candidateFollower.getPos()) != firstSucc)
				firstCheck = true;
			// second check
			if (firstCheck) {
				int endList=Integer.parseInt(candidateFollower.getPosWithAlignments()) - 1;
				List<Event> eventsBetweenFromTrace = trace.subList(posEvent,
						endList);
				//Also for the second check, I don't have to consider the inserted events
				ArrayList<Event> eventsBetween= new ArrayList<Event>();
				for(Event eventFromTrace: eventsBetweenFromTrace){
					eventsBetween.add(eventFromTrace);
				}
				/* Since in the new repaired procedure I remove the inserted events, this check is no more necessary. It leads problems in
				 * the new procedure, so DO NOT REMOVE THE COMMENT, unless you want to restore the old procedure.
				ArrayList<Event> causalPredInserted= new ArrayList<Event>();
				for(Event evBet: eventsBetween){
					int posPred=Integer.parseInt(evBet.getPos());
					boolean predInserted=isInserted(posPred, numTrace);
					if(predInserted)
						causalPredInserted.add(evBet);
				}
				eventsBetween.removeAll(causalPredInserted);
				*/
				//
				for (Event eventBetween : eventsBetween) {
					ArrayList<String> eventCR = CRList.get(eventBetween
							.getEventclass());
					if (eventCR != null && eventCR.size() > 0)
						if (eventCR.contains(candidateFollower.getEventclass())) {
							secondCheck = true;
							break;
						}
				}
			}
			if (!firstCheck || !secondCheck)
				result.add(candidateFollower);
		}
		return result;
	}

	private int findFirstSucc(ArrayList<Event> causalSucc, int numTrace) {
		int tempPos = -1;
		ArrayList<Event> causalSuccTemp= new ArrayList<Event>();
		for(Event event:causalSucc){
			causalSuccTemp.add(event);
		}
		//I have to remove from the causalSucc list the inserted events --> with the updated version, this should not be needed any more
		for (Event event : causalSuccTemp) {
			if (tempPos == -1)
				tempPos = Integer.parseInt(event.getPos());
			else {
				if (Integer.parseInt(event.getPos()) < tempPos)
					tempPos = Integer.parseInt(event.getPos());
			}
		}
		return tempPos;
	}

	private ArrayList<Event> checkCausalRel(ArrayList<Event> trace,
			Event event, int posEvent, int numTrace) {
		ArrayList<Event> result = new ArrayList<Event>();
		List<Event> reducedTrace = trace.subList(posEvent, trace.size());
		for (Event eventTrace : reducedTrace) {
			String followerClass = eventTrace.getEventclass();
			ArrayList<String> eventCR = CRList.get(event.getEventclass());
			if (eventCR!= null && eventCR.size() > 0) {
				if (eventCR.contains(followerClass))
					result.add(eventTrace);
			} else {
				System.out.println("no followers for " + event.getEventclass() + " " + followerClass + " " + numTrace + " " +posEvent);
			}
		}
		return result;
	}

	private HashMap<Integer, ArrayList<Anomaly>> setAnomalies() {
		HashMap<Integer, ArrayList<Anomaly>> result = new HashMap<Integer, ArrayList<Anomaly>>();
		int numTrace = 1;
		for (XTrace trace : xlog) {
			String traceId = trace.getAttributes().get("concept:name")
					.toString();
			String alignment = checkTrace(traceId);
			if (alignment != "") {
				ArrayList<Anomaly> anomaliesList = new ArrayList<Anomaly>();
				anomaliesList = updateAnomalies(alignment, numTrace);
				// anomaliesList=updateAnomaliesList(numTrace);
				result.put(numTrace, anomaliesList);
			}
			System.out.println("done "+numTrace);
			numTrace++;
			
		}
		return result;
	}

	// private ArrayList<Anomaly> updateAnomaliesList(int numTrace) {
	// ArrayList<Anomaly> result= new ArrayList<Anomaly>();
	// String
	// queryAnomalies="SELECT posEvent,type,eventclass FROM anomalies WHERE numTrace="+numTrace;
	// ArrayList<String>
	// anomalies=dbManager.executeQuery(queryAnomalies,"posEvent;type;eventclass");
	// if(anomalies.size()>0){
	// for(String anomalyTokens: anomalies){
	// String[] tokens=anomalyTokens.split(";");
	// int posEvent=Integer.parseInt(tokens[0]);
	// String type=tokens[1];
	// String eventclass=tokens[2];
	// Anomaly anomaly= new Anomaly(type, eventclass,posEvent);
	// result.add(anomaly);
	// }
	// }
	// return result;
	// }
	//

	private ArrayList<Anomaly> updateAnomalies(String alignment, int numTrace) {
		ArrayList<Anomaly> anomaliesList = new ArrayList<Anomaly>();
		// We have two main cases: insertion and deletion
		String[] token = alignment.split("\\|");
		// to implement the multiple deletion occurrence I have to remove the
		// invisible transaction
		ArrayList<String> listToken = new ArrayList<String>(
				Arrays.asList(token));
		Iterator<String> iterator = listToken.iterator();
		while (iterator.hasNext()) {
			String currentString = iterator.next();
			if (currentString.contains("[M-INVI]")) {
				iterator.remove();
			}
			// other operations
		}
		int cont = 0;
		// for dealing with the new repairing algorithm, where I need to put
		// also deleted events in the trace when building the graph, I use a
		// different counter
		int contWA = 0;
		int i = 0;
		String curr_in_del = "";
		String curr_fin_del = "";
		for (String eventAligned : listToken) {
			String[] eventTokens = eventAligned.split("]");
			String eventInfo = eventTokens[0].replace("[", "");
			String eventclass = eventTokens[1].replaceAll(" ", "");
			String type = "";
			// here I have to change RIMETTI with START and END event
			// respectively for those logs
			// in which we use the artificial added events
			if (!eventclass.equals("RIMETTI") && !eventclass.equals("RIMETTI")) {
				if (eventInfo.equals("L/M")) {
					type = "correct";
					cont++;
					contWA++;
				} else if (eventInfo.equals("L")) {
					type = "insertion";

					// new part
					//Anomaly anomaly = new Anomaly(type, eventclass, contWA + 1);
					//14/12: rimetto il contatore normale invece di contWA
					Anomaly anomaly = new Anomaly(type, eventclass, cont + 1);
					int temp = i + 1;
					String finalClass = "";
					while (temp < listToken.size()
							&& listToken.get(temp).contains("[L]")) {
						String[] finalToken = listToken.get(temp).split("]");
						finalClass = finalToken[1].replaceAll(" ", "");
						anomaly.setEventClassFin(finalClass);
						anomaly.setPosAnFin(anomaly.getPosAnFin() + 1);
						temp++;
					}
					boolean isPresent = checkInsertionPresence(anomaly,
							anomaliesList);
					if (!isPresent)
						anomaliesList.add(anomaly);
					cont++;
					contWA++;

				} else if (eventInfo.equals("M-REAL")) {
					type = "deletion";
					// check if a multiple deletion started
					if (listToken.size() > i + 1 && i > 1) {
						if (!listToken.get(i - 1).contains("[M-REAL]")
								&& listToken.get(i + 1).contains("[M-REAL]")
								&& curr_in_del.equals("")) {
							curr_in_del = eventclass;
							curr_fin_del = "";
						}
						// check if a multiple deletion terminated
						else if (listToken.get(i - 1).contains("[M-REAL]")
								&& !listToken.get(i + 1).contains("[M-REAL]")
								&& !curr_in_del.equals("")) {
							curr_fin_del = eventclass;
							changedPred.put(cont + 1, curr_in_del);
							curr_fin_del = "";
							curr_in_del = "";
						}
					}
					//Anomaly anomaly = new Anomaly(type, eventclass, contWA + 1);
					//Modifica 14/12 rimetto cont normale
					Anomaly anomaly = new Anomaly(type, eventclass, cont+ 1);
					int temp = i + 1;
					String finalClass = "";
					while (listToken.size() > temp
							&& listToken.get(temp).contains("[M-REAL]")) {
						String[] finalToken = listToken.get(temp).split("]");
						finalClass = finalToken[1].replaceAll(" ", "");
						anomaly.setEventClassFin(finalClass);
						anomaly.setPosAnFin(anomaly.getPosAnFin() + 1);
						temp++;
					}
					boolean isPresent = checkInsertionPresence(anomaly,
							anomaliesList);
					if (!isPresent)
						anomaliesList.add(anomaly);
					contWA++;
				} else if (eventInfo.equals("M-INVI")) {
					type = "tau";
				} else {
					System.out.println("Ignoto:" + eventInfo);
				}
				if (!type.equals("") && !type.equals("correct")
						&& !type.equals("tau") && !type.equals("deletion")) {
					String posEvent = String.valueOf(cont);
					String posEventWA = String.valueOf(contWA);
					String trace = String.valueOf(numTrace);
					
						String stm = "INSERT IGNORE INTO anomalies(posEvent,numTrace,type,eventclass) VALUES(?,?,?,?)";
						//Modifiche 14/12: rimetto posEvent
						/*ArrayList<String> fields = new ArrayList<String>(
								Arrays.asList(posEventWA, trace, type,
										eventclass));*/
						ArrayList<String> fields = new ArrayList<String>(
								Arrays.asList(posEvent, trace, type,
										eventclass));
						dbManager.executeStatement(stm, fields);
					
				} else if (type.equals("deletion") && curr_in_del.equals("")
						&& curr_fin_del.equals("")) {
					String posEvent = String.valueOf(cont + 1);
					String posEventWA = String.valueOf(contWA);
					String trace = String.valueOf(numTrace);
			
						String stm = "INSERT IGNORE INTO anomalies(posEvent,numTrace,type,eventclass) VALUES(?,?,?,?)";
						//Modifiche 14/12: rimetto posEvent
						/*ArrayList<String> fields = new ArrayList<String>(
								Arrays.asList(posEventWA, trace, type,
										eventclass));*/
						ArrayList<String> fields = new ArrayList<String>(
								Arrays.asList(posEvent, trace, type,
										eventclass));

						dbManager.executeStatement(stm, fields);
					
				}
			}
			i++;
		}
		return anomaliesList;
	}

	private boolean checkInsertionPresence(Anomaly anomaly,
			ArrayList<Anomaly> anomaliesList) {
		// check if an insertion anomaly has been already considered
		int anInPos = anomaly.getPosAn();
		int anFinPos = anomaly.getPosAnFin();
		boolean present = false;
		for (Anomaly comparingAn : anomaliesList) {
			if (comparingAn.getType().equals(anomaly.getType())) {
				int compIn = comparingAn.getPosAn();
				int compFin = comparingAn.getPosAnFin();
				if ((compIn <= anInPos && anInPos <= compFin)
						&& (compIn <= anFinPos && anFinPos <= compFin)) {
					present = true;
					break;
				}
			}
		}
		return present;
	}

	private HashMap<Integer, String> setTraceId() {
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
	
	//only for parsing the cov graph file
	String getElementId(NodeList elementList, int i){
		String elementId=null;
		Node node=elementList.item(i);
		elementId=node.getAttributes().getNamedItem("id").getNodeValue();
		return elementId;
	}
	
	public void convertSubdueTables2Subdue(DBManager dbManager, String filePath){
		FileWriter writer=null;
		//String folderPath = "C:/Users/Laura/Desktop/GrafiCorrectRepaired/graph";
		//count the number of graph
		String queryCount="SELECT count(distinct id_wf) as num FROM nodes";
		ArrayList<String> countList=dbManager.executeQuery(queryCount, "num");
		int numbGraphs=Integer.parseInt(countList.get(0));
		try{
		writer = new FileWriter(filePath, false);
		for(int i=0; i<numbGraphs;i++){
		int id_wf=i+1;
		String queryNodes= "SELECT id,label FROM nodes WHERE id_wf="+id_wf;
		ArrayList<String> nodesList= dbManager.executeQuery(queryNodes, "id;label");
		String queryEdges="SELECT id1,id2,label FROM edges WHERE id_wf="+id_wf;
		ArrayList<String> edgesList=dbManager.executeQuery(queryEdges, "id1;id2;label");
		String queryIdTrace="SELECT idTrace FROM traceid WHERE numTrace="+id_wf;
		ArrayList<String> idTraceList=dbManager.executeQuery(queryIdTrace, "idtrace");
		String idTrace="NA";
		if(idTraceList!=null && idTraceList.size()>0)
			idTrace=idTraceList.get(0);
		//writer.write("% Graph"+id_wf+" trace "+idTrace+"\n");
		writer.write("XP\n");
		for(String node: nodesList){
				String[] nodesToken=node.split(";");
				String nodeId=nodesToken[0];
				String nodeLabel=nodesToken[1];
				writer.write("v "+nodeId + " "+nodeLabel+"\n");
			}
			//writer.write("\n");
			for(String edge: edgesList){
				String[] edgeTokens=edge.split(";");
				String id1= edgeTokens[0];
				String id2= edgeTokens[1];
				String edgeLabel= edgeTokens[2];
				writer.write("e "+id1+" "+ id2+"  "+ edgeLabel+"\n");
			}
			writer.write("\n");
			
		}
		writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		
	}
	public HashMap<Integer, String> getTraceIdMapping() {
		return traceIdMapping;
	}
	public void setTraceIdMapping(HashMap<Integer, String> traceIdMapping) {
		this.traceIdMapping = traceIdMapping;
	}
	public HashMap<Integer, ArrayList<Anomaly>> getAnomalies() {
		return anomalies;
	}
	public void setAnomalies(HashMap<Integer, ArrayList<Anomaly>> anomalies) {
		this.anomalies = anomalies;
	}
	public HashMap<Integer, ArrayList<Event>> getTracesList() {
		return tracesList;
	}
	public void setTracesList(HashMap<Integer, ArrayList<Event>> tracesList) {
		this.tracesList = tracesList;
	}
	public HashMap<Integer, ArrayList<Event>> getTracesAlignedList() {
		return tracesAlignedList;
	}
	public void setTracesAlignedList(
			HashMap<Integer, ArrayList<Event>> tracesAlignedList) {
		this.tracesAlignedList = tracesAlignedList;
	}
	public HashMap<String, ArrayList<String>> getCRList() {
		return CRList;
	}
	public void setCRList(HashMap<String, ArrayList<String>> cRList) {
		CRList = cRList;
	}
}
