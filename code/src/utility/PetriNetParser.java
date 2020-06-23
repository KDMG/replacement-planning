package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/*This class reads a pnml file and builds the causal ordering table*/
public class PetriNetParser extends Parser {
	private File pnFile;
	private ArrayList<String> endEvents=new ArrayList<String>();
	private ArrayList<String> startEvents= new ArrayList<String>();
	
	public ArrayList<String> getEndEvents() {
		return endEvents;
	}

	public void setEndEvents(ArrayList<String> endEvents) {
		this.endEvents = endEvents;
	}

	public ArrayList<String> getStartEvents() {
		return startEvents;
	}

	public void setStartEvents(ArrayList<String> startEvents) {
		this.startEvents = startEvents;
	}



	public PetriNetParser(File pnFile, DBManager dbManager) {
		this.pnFile = pnFile;
		this.dbManager=dbManager;
	}
	
	public void getOrderingRelations(String[] args) {
		// read the petri net file
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			//initializeDB();
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(pnFile);
			doc.getDocumentElement().normalize();
			// get places
			String startPlace = null;
			String endPlace = null;
			NodeList placesList = doc.getElementsByTagName("place");
			ArrayList<Node> placesListFiltered=removeAdditionalPlaces(placesList);
			ArrayList<String> placesIds = new ArrayList<String>();
			ArrayList<String> tauTranId = new ArrayList<String>();
			for (int i = 0; i < placesListFiltered.size(); i++) {
				placesIds.add(getPlaceId(placesListFiltered, i));
				String value = placesListFiltered.get(i).getTextContent();
				if (value.contains("Start"))
					startPlace = getPlaceId(placesListFiltered, i);
				else if (value.contains("End"))
					endPlace = getPlaceId(placesListFiltered, i);
				// add the place to the table
				String queryInsert = "INSERT INTO place(id,name) VALUES(?,?)";
				ArrayList<String> fieldList = new ArrayList<String>(
						Arrays.asList(getPlaceId(placesListFiltered, i), value));
				dbManager.executeStatement(queryInsert, fieldList);
			}
			// get transitions
			NodeList transitionsList = doc.getElementsByTagName("transition");
			ArrayList<String> transitionsIds = new ArrayList<String>();
			for (int i = 0; i < transitionsList.getLength(); i++) {
				transitionsIds.add(getElementId(transitionsList, i));
				String queryInsert = "INSERT INTO transition(id,name) VALUES(?,?)";
				String name = transitionsList.item(i).getTextContent()
						.replace("\n", "");
				name=name.replace(" ", "");
				name=name.replace("'", "");
				// Update June 2016
				boolean invisible=false;
				if(name.contains("tau") || name.contains("Inv")){
					Node node=transitionsList.item(i);
					NodeList children= node.getChildNodes();
					boolean toolSpec=false;
					for(int c=0; c<children.getLength();c++){
						Node child=children.item(c);
						String childName=child.getNodeName();
						if(childName.contains("toolspecific")){
							toolSpec=true;
							NamedNodeMap childAttr=child.getAttributes();
							for(int a=0; a<childAttr.getLength(); a++){
								Node attr=childAttr.item(a);
								if(attr.getNodeName().equals("activity")){
									if(attr.getTextContent().contains("invisible"))
										invisible=true;
								}
							}
						}
						
					}
					//if the node has no tool specific attribute, we can only use the name
					//da rimettere
					//if(!toolSpec)
						invisible=true;
					name=name+""+getElementId(transitionsList, i);
				}
				name = name.replace(" ", "");
				ArrayList<String> fieldList = new ArrayList<String>(
						Arrays.asList(getElementId(transitionsList, i), name));
				dbManager.executeStatement(queryInsert, fieldList);
				// check if it is a tau trans
				if (name.contains("tau") || name.contains("Inv")){
					if(invisible)
						tauTranId.add(name);
				}
					
			}

			// get arcs: each pair of arcs represents a connection that I want
			// to store in my outcome
			// update: deal with silent transitions
			ArrayList<PetriArc> arcsList = new ArrayList<PetriArc>();
			NodeList arcNodesList = doc.getElementsByTagName("arc");
			for (int i = 0; i < arcNodesList.getLength(); i++) {
				String id = getElementId(arcNodesList, i);
				String source = arcNodesList.item(i).getAttributes()
						.getNamedItem("source").getNodeValue();
				String target = arcNodesList.item(i).getAttributes()
						.getNamedItem("target").getNodeValue();
				PetriArc arc = new PetriArc(id, source, target, false);
				arcsList.add(arc);
				String type = "";
				// update table
				if (!arc.getFrom().equals(startPlace)
						&& !arc.getTo().equals(endPlace)) {
					String queryInsert = "INSERT INTO edge(id,source,target,visited) VALUES(?,?,?,?)";
					ArrayList<String> fieldList = new ArrayList<String>(
							Arrays.asList(id, source, target, "0"));
					dbManager.executeStatement(queryInsert, fieldList);
				} else if (arc.getFrom().equals(startPlace)) {
					String startEvent = getTransitionName(target);
					type = "start";
					String queryInsert = "INSERT INTO startend(event,type) VALUES(?,?)";
					ArrayList<String> fieldList = new ArrayList<String>(
							Arrays.asList(startEvent, type));
					dbManager.executeStatement(queryInsert, fieldList);
				} else if (arc.getTo().equals(endPlace)) {
					String endEvent = getTransitionName(source);
					type = "end";
					String queryInsert = "INSERT INTO startend(event,type) VALUES(?,?)";
					ArrayList<String> fieldList = new ArrayList<String>(
							Arrays.asList(endEvent, type));
					dbManager.executeStatement(queryInsert, fieldList);
				}
			}
			
				String queryInsert = "INSERT INTO causalrel(source,target)"
						+ " ( select  t1.name as source, t2.name as target "
						+ "FROM edge as e1, edge as e2, transition as t1, transition as t2 "
						+ "	WHERE e1.target=e2.source and e1.source=t1.id and e2.target=t2.id)";
				dbManager.queryUpdate(queryInsert);
				if(tauTranId.size()>0){
					// udpdate 07/06: new deleteTauFromRel method
					if(args.length>3)
						deleteTauFromRelUsingAttributes(Integer.parseInt(args[3]), Integer.parseInt(args[4]),tauTranId);									//deleteTauFromRelUsingAttributes(Integer.parseInt(args[3]), Integer.parseInt(args[4]),tauTranId);
					else
						deleteTauFromRelUsingAttributes(0, 0, tauTranId);
				}
		
			/*
			 * Replaced by table for (PetriArc arc : arcsList) { if
			 * (!arc.isVisited()) { // if the arc is the first or the last one,
			 * it is discarded if (!arc.getFrom().equals(startPlace) &&
			 * !arc.getTo().equals(endPlace)) { PetriArc coupledArc =
			 * findCoupledArc(arc,arcsList); String from = arc.getFrom(); String
			 * to = coupledArc.getTo(); causalOT.put(from, to);
			 * coupledArc.setVisited(true); } arc.setVisited(true); } } Iterator
			 * it = causalOT.entrySet().iterator(); while (it.hasNext()) {
			 * Map.Entry pairs = (Map.Entry)it.next(); String
			 * t1=getTransaction(pairs.getKey(),transitionName); String
			 * t2=getTransaction(pairs.getValue(),transitionName);
			 * System.out.println(t1+ " -> " + t2); it.remove(); // avoids a
			 * ConcurrentModificationException }
			 */
				String checkWoped="SELECT source FROM causalrel LIMIT 1";
				String resCheckWoped=dbManager.executeQuery(checkWoped, "source").get(0);
				if(resCheckWoped.endsWith("011")){
					String queryCleanSource="update  `causalrel` set source=SUBSTRING(source FROM 1 FOR CHAR_LENGTH(source) - 3) ";
					String queryCleanTarget="update  `causalrel` set target=SUBSTRING(target FROM 1 FOR CHAR_LENGTH(target) - 3) ";
					dbManager.queryUpdate(queryCleanSource);
					dbManager.queryUpdate(queryCleanTarget);
				}
			
			System.out.println("fatto");
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

	}
	private ArrayList<Node> removeAdditionalPlaces(NodeList placesList) {
		ArrayList<Node> result= new ArrayList<Node>();
		int numNodes=placesList.getLength();
		for(int i=0; i<numNodes; i++){
			Node node=placesList.item(i);
			NamedNodeMap att=node.getAttributes();
			if(att.getNamedItem("id")!=null)
				result.add(node);
		}
		
		return result;
	}

	//method used in ESWA
	private void deleteTauFromRel(int from, int to) {
		// 
		String querySelect="select  source,target "
				+ "FROM causalrel";
		ArrayList<String> rowList=dbManager.executeQuery(querySelect, "source;target");
		int flag=0;
		//for(int numRow=from; numRow<to; numRow++){
		for(String row: rowList){
			//String row=rowList.get(numRow);
			flag++;
			System.out.println(flag);
			ArrayList<String> visited= new ArrayList<String>();
			String[] token=row.split(";");
			String sourceName=token[0];
			String targetName=token[1];
			if(!visited.contains(sourceName+";"+targetName)){
			if(!sourceName.contains("tau") && !targetName.contains("tau")){
//				String queryUpdate="UPDATE edge SET visited=1 WHERE source LIKE'"+sourceId+"' AND "
//						+ " target LIKE '"+targetId+"'";
//				dbManager.queryUpdate(queryUpdate);
			}
			else{
				String tauTran="";
				int type=-1;
				if(targetName.contains("tau")){
					tauTran=targetName;
					type=0;
				}
				else if(sourceName.contains("tau")){
					tauTran=sourceName;
					type=1;
				}
				String newTran="";
				if(type!=1)
				{ 	
					newTran=findTransition(tauTran,type, visited, new ArrayList<String>());
					System.out.println("tran");
				}
				//should be optimized, I only have to check if the tau is a target
				String[] tokenTran=newTran.split(";");
				if(tokenTran.length>0)
				{
					for(int i=0; i<tokenTran.length;i++){
						if(!tokenTran[i].equals("")){
							//18-12 aggiungi un controllo per gli eventuali duplicati
							String queryInsert="INSERT INTO causalrel(source,target) VALUES(?,?)";
							String newSource="";
							String newTarget="";
							if(type==0){
								// the target has been changed
								newTarget=tokenTran[i];
								newSource=sourceName;
							}
							else if(type==1){
								// the source has been changed
								newTarget=targetName;
								newSource=tokenTran[i];
							}
							ArrayList<String> fieldQuery=new ArrayList<String>(Arrays.asList(newSource, newTarget));
							//check if the relation already exists
							String queryCheck="SELECT source,target FROM causalrel WHERE source LIKE '"+newSource+"' AND target LIKE '"+newTarget+"'";
							ArrayList<String> checkResult=dbManager.executeQuery(queryCheck, "source;target");
							if(checkResult.size()==0)
								dbManager.executeStatement(queryInsert, fieldQuery);
						}
					}
				}
			}
			}
		}
		//At the end we remove from causalrel the tau causal rel.
		String queryRemoveTau="DELETE FROM causalrel WHERE source LIKE '%tau%' OR target LIKE '%tau%'";
		dbManager.queryUpdate(queryRemoveTau);
	}
	
	//update 07/06: some transitions might contain tau in their name. This method takes this
	// situation into account using the attribute activity in "toolspecific" tag
	
	private void deleteTauFromRelUsingAttributes(int from, int to, ArrayList<String> tauTranId) {
		// 
		String querySelect="select  source,target "
				+ "FROM causalrel";
		ArrayList<String> rowList=dbManager.executeQuery(querySelect, "source;target");
		int flag=0;
		//for(int numRow=from; numRow<to; numRow++){
		for(String row: rowList){
			//String row=rowList.get(numRow);
			flag++;
			//System.out.println(flag);
			ArrayList<String> visited= new ArrayList<String>();
			String[] token=row.split(";");
			String sourceName=token[0];
			String targetName=token[1];
			boolean sourceInTau=false;
			boolean targetInTau=false;
			if((sourceName.contains("tau") || sourceName.contains("Inv")) && tauTranId.contains(sourceName))
				sourceInTau=true;
			if((targetName.contains("tau") || targetName.contains("Inv")) && tauTranId.contains(targetName))
				targetInTau=true;
			if(!visited.contains(sourceName+";"+targetName)){
			if(!sourceInTau && !targetInTau){
//				String queryUpdate="UPDATE edge SET visited=1 WHERE source LIKE'"+sourceId+"' AND "
//						+ " target LIKE '"+targetId+"'";
//				dbManager.queryUpdate(queryUpdate);
			}
			else{
				String tauTran="";
				int type=-1;
				if(targetInTau){
					tauTran=targetName;
					type=0;
				}
				else if(sourceInTau){
					tauTran=sourceName;
					type=1;
				}
				String newTran="";
				if(type!=1)
				{ 	
					newTran=findTransition(tauTran,type, visited,tauTranId);
					//System.out.println("tran");
				}
				//should be optimized, I only have to check if the tau is a target
				String[] tokenTran=newTran.split(";");
				if(tokenTran.length>0)
				{
					for(int i=0; i<tokenTran.length;i++){
						if(!tokenTran[i].equals("")){
							//18-12 aggiungi un controllo per gli eventuali duplicati
							String queryInsert="INSERT INTO causalrel(source,target) VALUES(?,?)";
							String newSource="";
							String newTarget="";
							if(type==0){
								// the target has been changed
								newTarget=tokenTran[i];
								newSource=sourceName;
							}
							else if(type==1){
								// the source has been changed
								newTarget=targetName;
								newSource=tokenTran[i];
							}
							ArrayList<String> fieldQuery=new ArrayList<String>(Arrays.asList(newSource, newTarget));
							//check if the relation already exists
							String queryCheck="SELECT source,target FROM causalrel WHERE source LIKE ? AND target LIKE ?";
							//PreparedStatement ps = dbManager..prepareStatement(query);
							//String queryCheck="SELECT source,target FROM causalrel WHERE source LIKE '"+newSource+"' AND target LIKE '"+newTarget+"'";
							ArrayList<String> fields=new ArrayList<String>();
							fields.add(newSource);
							fields.add(newTarget);
							ArrayList<String> checkResult=dbManager.executeSelectStatement(queryCheck, fields, ";");
									//dbManager.executeQuery(queryCheck, "source;target");
							if(checkResult.size()==0)
								dbManager.executeStatement(queryInsert, fieldQuery);
						}
					}
				}
			}
			}
		}
		//At the end we remove from causalrel the tau causal rel.
		String querySelectRel="SELECT source,target FROM causalrel";
		ArrayList<String> crList=dbManager.executeQuery(querySelectRel, "source;target");
		for(String pair:crList){
			String[] pairTokens=pair.split(";");
			if(((pairTokens[0].contains("tau") || pairTokens[0].contains("Inv")) && tauTranId.contains(pairTokens[0])) || 
					((pairTokens[1].contains("tau") || pairTokens[1].contains("Inv")) && tauTranId.contains(pairTokens[1]))){
				
				String queryRemoveTau="DELETE FROM causalrel WHERE source LIKE ? AND target LIKE ?";
				ArrayList<String> fieldsqueryRemoveTau=new ArrayList<String>();
				fieldsqueryRemoveTau.add(pairTokens[0]);
				fieldsqueryRemoveTau.add(pairTokens[1]);
				dbManager.executeStatement(queryRemoveTau, fieldsqueryRemoveTau);

			}
		}
		
	}
// update 07/06; I also have to change this method
	private String findTransition(String candidateId, int type, ArrayList<String> visited, ArrayList<String> tauTranId) {
		String resultTran="";
		int newType=-1;
		if(!(candidateId.contains("tau") || candidateId.contains("Inv")) || !tauTranId.contains(candidateId))
			resultTran=candidateId;
		else{
			String where=" WHERE";
			String field="";
			if(type==0){
				where=where+" source LIKE '"+candidateId+"'";
				field="target";
				newType=0;
			}
			else if(type==1){
				where=where+" target LIKE '"+candidateId+"'";
				field="source";
				newType=0;
			}
			String queryNewCandidate="SELECT "+field+" FROM causalrel "+where;
			ArrayList<String> candList=dbManager.executeQuery(queryNewCandidate, field);
			if(candList.size()>0){
				for(String cand: candList){
					// if the rel has been already visited or if the source has been already considered, skip
					if(visited.contains(candidateId+","+cand) || alreadyChecked(visited, cand)){
						resultTran=resultTran+"";
					}
					
					else{
					String oldSource="";
					String oldTarget="";
					if(type==0){
						oldSource=candidateId;
						oldTarget=cand;
					}
					else if(type==1)
					{
						oldSource=cand;
						oldTarget=candidateId;
					}
					visited.add(oldSource+";"+oldTarget);
//					String queryDelete="DELETE FROM causalrel WHERE source LIKE '"+oldSource+"' AND target LIKE '"+oldTarget+"'";
//					dbManager.queryUpdate(queryDelete);
					resultTran=resultTran+";"+findTransition(cand,newType, visited, tauTranId);
					
				}
			}
			}
		}
		return resultTran;
	}

	private boolean alreadyChecked(ArrayList<String> visited, String cand) {
		boolean result=false;
		for(String edge: visited){
			String[] token=edge.split(";");
			if(token.length>0){
				String source=token[0];
				if(source.equals(cand)){
					result=true;
					break;
				}
			}
		}
		return result;
	}

	private String getTransitionName(String source) {
		String tName=null;
		String selectName="SELECT name FROM transition WHERE id='"+source+"'";
		ArrayList<String> result=dbManager.executeQuery(selectName, "name");
		if(result.size()>0)
			tName=result.get(0);
		else
			System.out.println("error");
		return tName;
	}

	

	private String getTransaction(Object key, HashMap transitionName) {
		String name=null;
		name=(String) transitionName.get(key);
		return name;
	}

	private PetriArc findCoupledArc(PetriArc arc, ArrayList<PetriArc> arcsList) {
		// two arcs are coupled if the target of the first is the source of the second
		PetriArc coupledArc=null;
		for(PetriArc candidateArc: arcsList){
			if(candidateArc.getFrom().equals(arc.getTo())){
				coupledArc=candidateArc;
				break;
			}
		}
		return coupledArc;
	}

	String getElementId(NodeList elementList, int i){
		String elementId=null;
		Node node=elementList.item(i);
		elementId=node.getAttributes().getNamedItem("id").getNodeValue();
		return elementId;
	}
	
	String getPlaceId(ArrayList<Node> placesListFiltered, int i){
		String elementId=null;
		Node node=placesListFiltered.get(i);
		elementId=node.getAttributes().getNamedItem("id").getNodeValue();
		return elementId;
	}
}
