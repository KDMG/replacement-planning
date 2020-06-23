package socialnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedMultigraph;

import com.univocity.parsers.conversions.IntegerConversion;

import utility.DBManager;

public class Matrix {
	
	private ArrayList<String> resource;
	private BiHashMap<String, String, ArrayList<Handover>> matrix= new BiHashMap<String, String, ArrayList<Handover>>();
	private DBManager dbManager;
	
	public Matrix(DBManager dbManager) {
		super();
		this.dbManager = dbManager;
	}
	
	public static void main(String[] args) {
		
		Properties prop = new Properties();
		String fileName = "C:\\Users\\ciott\\Desktop\\Tesi\\Replacement\\src\\config.txt";
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
		
		
		String dbUrl=prop.getProperty("dbUrl");
		String dbName=prop.getProperty("dbName");
		String username=prop.getProperty("username");
		String password=prop.getProperty("password");
		
		DBManager dbManager=new DBManager(dbUrl, dbName, username, password);
		
		String filePath=prop.getProperty("graphsFolder");
		
		Matrix orgMatrix = new Matrix(dbManager);
		orgMatrix.importValue();
		Integer[] perc = new Integer[]{0, 20, 25, 30, 35, 40}; 
		for (Integer param : perc) {

			String Path = filePath + "matrix_" + param + "perc.dot";
			if (param == 0) Path = filePath + "matrix.dot";
			orgMatrix.createSocialGraph(Path, param);
		}
		System.out.println("Done");
		
	}
	
	public Matrix(ArrayList<String> resource, BiHashMap<String, String, ArrayList<Handover>> matrix, DBManager dbManager) {
		
		super();
		this.resource = resource;
		this.matrix = matrix;
		this.dbManager = dbManager;
		
	}
	
	public ArrayList<String> getResource() {
		return resource;
	}
	public void setResource(ArrayList<String> resource) {
		this.resource = resource;
	}
	public BiHashMap<String,String,ArrayList<Handover>> getMatrix() {
		return matrix;
	}
	public void setMatrix(BiHashMap<String, String, ArrayList<Handover>> matrix) {
		this.matrix = matrix;
	}
	
	public void importValue () {
		
		String query="SELECT DISTINCT resource FROM event WHERE resource IS NOT NULL";
		this.resource = dbManager.executeQuery(query, "resource");
		//int maxWorkload = 8;
		double maxWorkload=1;
		double currentWorkload = 0;
		
		for (String resX : resource) {
			
			//currentWorkload = (int) (Math.random() * maxWorkload);
			String queryResource = "INSERT INTO resource(name,maxWorkload,currWorkload) VALUES(?,?,?)";
			ArrayList<String> field = new ArrayList<String>(Arrays.asList(
					resX, String.valueOf(maxWorkload), String.valueOf(currentWorkload)));
			dbManager.executeStatement(queryResource, field);
			
			for (String resY : resource) {
				/*
			if (resX.equals(resY)) {
					System.out.println("Stessa risorsa");
					}
				else {*/
				//System.out.println(resX + " " + resY);
				ArrayList<Handover> values = new ArrayList<Handover>();
				values = getTriple(resX, resY);
				matrix.put(resX, resY, values);
				
				for (Handover item : values){
					
					String queryInsert = "INSERT INTO matrix(res1,res2,activity1,activity2,handover,occurrences,time) VALUES(?,?,?,?,?,?,?)";
					ArrayList<String> fieldList = new ArrayList<String>(Arrays.asList(
							resX, resY, item.getFirstActivity(), item.getSecondActivity(),String.valueOf(item.getHandoverOfWork()),
							String.valueOf(item.getOccurrences()), item.getTime()));
					dbManager.executeStatement(queryInsert, fieldList);
					
					/*
					System.out.println(item.getFirstActivity());
					System.out.println(item.getSecondActivity());
					System.out.println(String.valueOf(item.getHandoverOfWork()));
					*/
				}
			}
		}
	} 
	
	public ArrayList<Handover> getTriple(String x, String y) {
		
		/* La funzione getTriple @return la lista di HandoverTriple a partire da le due risorse x e y prese in input
		 * I parametri a e b rappresentano i label dei nodi ovvero la descrizione delle attivit� svolte rispettivamente
		 * da x e da y; allo stesso modo id1 e id2 sono gli id dei nodi
		 * handover rappresenta il coefficiente di Handover of Work
		 */
		ArrayList<Handover> values = new ArrayList<Handover>();
		String a= null;
		String b= null;
		Integer id1 = 0;
		Integer id2 = 0;
		Float[] handover = null;
		String time = null;
		
		String query = "SELECT DISTINCT e.id from edges as e JOIN nodes as n JOIN nodes as no "
				+ "WHERE n.resource='" + x + "' AND no.resource='" + y + "' AND e.id1 = n.id AND e.id2 = no.id "
				+ "AND e.id_wf = n.id_wf AND e.id_wf = no.id_wf ";
		ArrayList<String> result = dbManager.executeQuery(query, "id");
		ArrayList<String> done = new ArrayList<String>();
		
		for (String i: result) {
			String queryID1 = "SELECT n.label as label, n.id as id FROM edges AS e JOIN nodes AS n where e.id1 = n.id AND e.id_wf = n.id_wf AND e.id = " + Integer.parseInt(i);
			String queryID2 = "SELECT n.label as label, n.id as id FROM edges AS e JOIN nodes AS n where e.id2 = n.id AND e.id_wf = n.id_wf AND e.id = " + Integer.parseInt(i);
			
			ArrayList<String> aList=dbManager.executeQuery(queryID1, "id;label");
			String aField = aList.get(0);
			String[] aTokens=aField.split(";");
			id1= Integer.parseInt(aTokens[0]);
			a = aTokens[1];
			
			ArrayList<String> bList=dbManager.executeQuery(queryID2, "id;label");
			String bField = bList.get(0);
			String[] bTokens=bField.split(";");
			id2= Integer.parseInt(bTokens[0]);
			b = bTokens[1];
			
			String edge = a.concat(b);
			Boolean flag = false;
			
			if (!done.isEmpty()) {
				for (String e : done) {
					if (e.equals(edge))
						flag = true;
				}
			}
			
			//System.out.println("Flag: " + flag + " Edge: " + edge);
			
			//System.out.println(a + b + x + y + String.valueOf(id1) +  String.valueOf(id2));
			
			if (!flag) {
				handover = calculateHandover(x, y, a, b);
				time = calculateTime(x, y, a, b);
				Handover nple = new Handover(a, b, handover[0], Math.round(handover[1]), time);
				values.add(nple);
				done.add(edge);
			}
		}
		
		return values;
		
	}
	
	public Float[] calculateHandover(String res1, String res2, String a, String b) {
		Float handover=(float) 0;
		Float nominatore=(float) 0;
		Float denominatore=(float) 0;
		
		String queryNom = "SELECT COUNT(DISTINCT e.id) as result from edges as e "
				+ "join nodes as n "
				+ "join nodes as no "
				+ "where e.id_wf = n.id_wf AND e.id_wf = no.id_wf "
				+ "AND n.label = '" + a + "' AND n.resource = '" + res1 + "' AND no.label = '" + b + "' AND no.resource = '" + res2  + "' "
						+ "AND e.label = '" + a + "__" + b + "'";
		String queryDen = "SELECT COUNT(DISTINCT e.id) as result from edges as e "
				+ "WHERE e.label = '" + a + "__" + b + "'";
		
		
		ArrayList<String> nomList = dbManager.executeQuery(
				queryNom, "result");
		if (nomList != null)
			nominatore = Float.parseFloat(nomList.get(0));
		else  System.out.println("Errore: nominatore non calcolato correttamente");
		
		ArrayList<String> denList = dbManager.executeQuery(
				queryDen, "result");
		if (denList != null)
			denominatore = Float.parseFloat(denList.get(0));
		else  System.out.println("Errore: denominatore non calcolato correttamente");
		
		if (denominatore==0) System.out.println("Errore: denominatore uguale a 0");
		
		//System.out.println("Nominatore: " + String.valueOf(nominatore));
		//System.out.println("Denominatore: " + String.valueOf(denominatore));
		
		handover= nominatore/denominatore;
		Float[] values = {handover, nominatore};
		return values;
	}
	

	
	public void createSocialGraph(String filePath, Integer percent) {
		WeightedMultigraph<String, HandoverEdge> socialGraph =new WeightedMultigraph<String, HandoverEdge>(HandoverEdge.class);
		
		for (String res :resource) socialGraph.addVertex(res);
		
		
		for (String resX : resource) {
			
			for (String resY : resource) {
				if (!resX.equals(resY)) {
					
						HandoverEdge edge = new HandoverEdge(this.matrix.get(resX, resY));
						Float param = (float) percent/100;
						edge.filter(param);									//se si vuole filtrare con un valore o un percentuale di handover
						socialGraph.addEdge(resX, resY, edge);

					
				}
			}
		}
		
		try{
			
			System.out.println(filePath);
			File matrix = new File(filePath);
			matrix.createNewFile();
			FileWriter writer = new FileWriter(matrix, false);
			writer.write("digraph G { \n");
			for(String node: resource){
				writer.write(node + " [label=\"" + node
						+ "\",color=black,fontcolor=black];\n");
			}
			for(HandoverEdge edge: socialGraph.edgeSet()){
				
				String edgeLabel= edge.toString();
				if (!edgeLabel.isEmpty()) {
					writer.write(socialGraph.getEdgeSource(edge)
							+ "->" + socialGraph.getEdgeTarget(edge)
							+ "[label=\" "+ edgeLabel + ""
							+ "\",color=black,fontcolor=black];\n");
					}
			}
			writer.write("}\n");
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public String calculateTime(String res1, String res2, String a, String b) {
		ArrayList<String> seconds;
		Date averageTime = new Date();
		DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
		String averageT = null;
		String queryTime = "SELECT TIMESTAMPDIFF(second, n.startTime, no.startTime) as result from edges as e "
				+ "join nodes as n "
				+ "join nodes as no "
				+ "where e.id_wf = n.id_wf AND e.id_wf = no.id_wf "
				+ "AND n.label = '" + a + "' AND n.resource = '" + res1 + "' AND no.label = '" + b + "' AND no.resource = '" + res2  + "' "
						+ "AND e.label = '" + a + "__" + b + "'";
		
		seconds = dbManager.executeQuery(
				queryTime, "result");
		if (seconds != null) {
			long sumTime = 0;
			for (String s : seconds) {
				
				sumTime += Math.abs(Long.parseLong(s)); 
			}
			long average = sumTime / seconds.size();
			System.out.println(seconds.size());
			//averageTime = new Date(average);
			//averageT = formatter.format(averageTime);
			averageT = String.valueOf(average);
		}
		else  System.out.println("Errore: tempo non calcolato correttamente");
		
		return averageT;
	}
	
}
