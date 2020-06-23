package socialnet;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import ilog.concert.*;
import ilog.cplex.*;

import org.deckfour.xes.extension.std.XConceptExtension;


import utility.DBManager;

public class Replace {
	
	private ArrayList<String> resource;
	private ArrayList<String> candidates;
	private ArrayList<Affinity> affinity = new ArrayList<Affinity>();
	private DBManager dbManager;
	private String lastActivity;
	private String r;
	private ArrayList<String> actSet = new ArrayList<String>();
	private HashMap<String,Double> actAvgWorkload=new HashMap<String,Double>();
	
	
	public Replace(DBManager dbManager) {
		super();
		this.dbManager = dbManager;
	}
	
	public static void main(String[] args) {
		
		long inizio = System.currentTimeMillis();
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
		
		
		String dbUrl=prop.getProperty("dbUrl");
		String dbName=prop.getProperty("dbName");
		String username=prop.getProperty("username");
		String password=prop.getProperty("password");
		String dbCreato=prop.getProperty("dbCreato");
	
		DBManager dbManager=new DBManager(dbUrl, dbName, username, password,dbCreato);
		
		String folder=prop.getProperty("folder");
		
		String avgTimeLastActivity = prop.getProperty("avgTime");
		
		String w0 = prop.getProperty("w0");
		String w1 = prop.getProperty("w1");
		String w2 = prop.getProperty("w2");
		String w3 = prop.getProperty("w3");

		float[] w = new float[4];
		w[0] = Float.parseFloat(w0);
		w[1] = Float.parseFloat(w1);
		w[2] = Float.parseFloat(w2);
		w[3] = Float.parseFloat(w3);
		
		float beta = Float.parseFloat(prop.getProperty("beta"));
		Replace replace = new Replace(dbManager);
		
		//Set the avg workload for activity
		replace.setAvgActivityWorkload_forTests();
		
		//Prendo tutte le risorse
		ArrayList<String> resources=replace.getAllResources();
		replace.setResource(resources);
		
		//For each resource
		for(String res:resources) {
			if(!res.equals("10629"))
				continue;
			//skip the resource 112
			if(res.equals("112"))
				continue;
			if(Integer.parseInt(res)<=0)
				continue;
			
			//String resourcename = prop.getProperty("resource");

			//set the last activity of the process
			replace.setLastActivity(prop.getProperty("lastActivity"));
			//set as end the last activity of the processes
			replace.setEnd();
			//set the resource to replace - XXX
			//replace.setR(resourcename);
			replace.setR(res);
			
			
			//For each day - at most 10 times
			ArrayList<String> dates=replace.getDatesByResource(res);
			int iterations=0;
			ArrayList<String> alreadyAnalysedDays=new ArrayList<String>();
			for(String day:dates) {
				//continue if the day has already been analysed
				if(alreadyAnalysedDays.contains(day))
					continue;
				alreadyAnalysedDays.add(day);
				
				//reset the db
				replace.deleteValue();
				//
				//at most 10 iterations
				if(iterations++>9)
					break;
				
				replace.setCandidates(resources);
				replace.removeCandidate(res);
				replace.removeCandidate("112");
				
				//Get the set of activity for res and set the actSet ArrayList
				HashMap<String,Integer> actNum=replace.getActivitesByRes_Day(res, day);
				ArrayList<String> actSet=new ArrayList<String>();
				for(String k:actNum.keySet()) {
					if(k.equals(prop.getProperty("lastActivity")))
						continue;
					int num=actNum.get(k);
					for(int i=0;i<num;i++)
						actSet.add(k);
				}
				replace.setActSet(actSet);
				
				//set the avg time per activity
				replace.updateActivityTable(avgTimeLastActivity);

				//Compute the affinity - only used for thesis evaluation
				replace.getAffinity(w);
				
				//Compute the cij costs for the model
				replace.getCost(w);
 				
				//Compute the lij costs
				for(String candidate:resources) {
					//skip if candidate=112 or candidate=res
					if(candidate.equals(res) || candidate.equals("112"))
						continue;
					
					double avgDayWorkload=0;
					HashMap<String,Integer> actNum4Candidate=replace.getActivitesByRes_Day(candidate, day);
					//compute the l for the specific resource
					for(String act: actNum4Candidate.keySet()) {
						double avgTime=replace.actAvgWorkload.get(act);
						avgDayWorkload=avgDayWorkload+(avgTime*actNum4Candidate.get(act));
					}
					if(avgDayWorkload>=1) {
						avgDayWorkload=1;
						replace.removeCandidate(candidate);
					}
					replace.updateWorkloadForResource(candidate,avgDayWorkload);
				}
				
				//replace.exportToDB();
				
				//Compute the gamma and mu parameters, generate the ILP model and call CPLEX
				replace.runReplacement(folder, beta);

				long fine = System.currentTimeMillis();
				System.out.println("Done");
				System.out.println("Tempo di esecuzione: " + (fine-inizio)/1000 + " secondi");
			}
		}
	}
	
	private void removeCandidate(String candidate) {
		this.candidates.remove(candidate);
		
	}

	private void setCandidates(ArrayList<String> resources) {
		candidates=new ArrayList<String>();
		for(String s:resources)
			this.candidates.add(s);
		
	}

	private void setAvgActivityWorkload_forTests() {
		actAvgWorkload.put("A_CANCELLED", 0.867925);
		actAvgWorkload.put("A_DECLINED", 0.309375);
		actAvgWorkload.put("A_PREACCEPTED", 0.204545);
		actAvgWorkload.put("W_Afhandelenleads", 0.183024);
		actAvgWorkload.put("W_Beoordelenfraude", 0.291667);
		actAvgWorkload.put("W_Completerenaanvraag", 0.120442);
		
	}

	private void updateWorkloadForResource(String candidate, double avgDayWorkload) {
		String query="INSERT INTO workloads_forTest VALUES ('"+candidate+"',1,"+avgDayWorkload+")";
		dbManager.queryUpdate(query);
	}

	public String getLastActivity() {
		return lastActivity;
	}

	public void setLastActivity(String lastActivity) {
		this.lastActivity = lastActivity;
	}

	public void setActSet(ArrayList<String> actSet) {
		this.actSet=actSet;
	}
	
	
	public HashMap<String,Integer> getActivityAvgTime(){
		HashMap<String, Integer> results=new HashMap<String,Integer>();
		String query="SELECT CONCAT(label,':',avgTime) as avgTime FROM activity";
		ArrayList<String> queryR = dbManager.executeQuery(query, "avgTime");
		for(String s:queryR) {
			String activity=s.split(":")[0];
			int time=Integer.parseInt(s.split(":")[1]);
			results.put(activity, time);
		}
		return results;
	}
	
	
	private void deleteValue() {
		
		String sql = "TRUNCATE TABLE cost";
		dbManager.queryUpdate(sql);
		sql = "TRUNCATE TABLE affinity";
		dbManager.queryUpdate(sql);
		sql = "TRUNCATE TABLE activity";
		dbManager.queryUpdate(sql);
		sql = "TRUNCATE TABLE workloads_forTest";
		dbManager.queryUpdate(sql);
		
	}
	

	public String getR() {
		return r;
	}

	public void setR(String r) {
		this.r = r;
	}

	public ArrayList<String> getResource() {
		return resource;
	}

	public void setResource(ArrayList<String> resource) {
		this.resource = resource;
	}

	public DBManager getDbManager() {
		return dbManager;
	}

	public void setDbManager(DBManager dbManager) {
		this.dbManager = dbManager;
	}

	public String getEnd() {
		return lastActivity;
	}

	public void setEnd() {
		String query="SELECT event FROM startend WHERE type = 'end'";
		ArrayList<String> r = dbManager.executeQuery(query, "event");
		if (r.size()!=0) this.lastActivity = r.get(0);
	}

	public void setAffinity(ArrayList<Affinity> affinity) {
		this.affinity = affinity;
	}

	public void getAffinity(float[] w) {
		
		//String query="SELECT DISTINCT resource FROM event WHERE resource IS NOT NULL";
		//this.resource = dbManager.executeQuery(query, "resource");
		
		String res = r;
			
			for (String candidate : resource) {
				
				if (!res.equals(candidate)) {
					ArrayList<String> commActivities = new ArrayList<String>();
					
					int commonAct = 0;
					int totalAct = 0;
					int commonRes = 0;
					int totalRes = 0;
					int espAct = 0;
					int timeAct = 0;
					float[] weight = w;
					
					/*Viene presa dal db la lista di attivit� svolte da @res e per ogni attvit� viene controllato se � stata svolta anche da candidate. Se si commonAct viene aumentato di 1*/
					String queryTotAct = "SELECT DISTINCT class as result FROM event WHERE resource = '" + res + "'";
					ArrayList<String> totAct = dbManager.executeQuery(queryTotAct, "result");
					totalAct = totAct.size();
					for (String act : totAct) {
						String queryComm = "SELECT id FROM event WHERE resource = '" + candidate + "' AND class = '" + act + "'";
						ArrayList<String> commAct = dbManager.executeQuery(queryComm, "id");
						if (commAct.size() != 0) {
							commonAct++;
							commActivities.add(act);
						}
					}
					
					/*Stessa cosa viene fatta per le risorse*/
					String queryTotRes = "SELECT DISTINCT res2 FROM matrix WHERE res1 = '" + res + "'";
					ArrayList<String> totRes = dbManager.executeQuery(queryTotRes, "res2");
					totalRes = totRes.size();
					for (String res2 : totRes) {
						String queryComm = "SELECT DISTINCT id FROM `matrix` WHERE res1 = '" + candidate + "' AND res2 = '" + res2 + "'";
						ArrayList<String> commRes = dbManager.executeQuery(queryComm, "id");
						
						if (commRes.size() != 0) commonRes++;
					}
					
					
					/*Per ogni attivit� stabiliamo i tempi medi e il numero di occorrenze totali e li confrontiamo con quelli del candidato*/
					for (String act : commActivities) {
						
						if (!act.equals(this.lastActivity)) {
						
							String queryOcc = "SELECT SUM(occurrences) as result FROM `matrix` WHERE res1 = '" + res + "' AND activity1 = '" + act + "'";
							ArrayList<String> result1 = dbManager.executeQuery(queryOcc, "result");
							int occ1 = 0;
							System.out.println("Risorsa "+ res + ", attività "+ act);
							if (result1.get(0)!=null) occ1 = Integer.parseInt(result1.get(0));
							
							String queryOcc2 = "SELECT SUM(occurrences) as result FROM `matrix` WHERE res1 = '" + candidate + "' AND activity1 = '" + act + "'";
							ArrayList<String> result2 = dbManager.executeQuery(queryOcc2, "result");
							int occ2 = 0;
							if (result2.get(0)!=null) occ2 = Integer.parseInt(result2.get(0));
							int threshold = occ1 / 100;
							
							/*
							System.out.println("Numero di occorrenze dell'attivita " + act + " da parte di " + res + ": " + occ1);
							System.out.println("Numero di occorrenze dell'attivita " + act + " da parte di " + candidate + ": " + occ2);
							System.out.println("Valore della soglia: " + threshold);*/
	
							if (occ2 >= (occ1 - threshold)) {
								espAct ++;
								//System.out.println("Aumentato di 1");
								}
							
							String queryTime = "SELECT Cast(AVG(time)as UNSIGNED) as result FROM `matrix` WHERE res1 = '" + res + "' AND activity1 = '" + act + "'";
							ArrayList<String> res1 = dbManager.executeQuery(queryTime, "result");
							int time1 = 0;
							if (res1.get(0)!=null) time1 = Integer.parseInt(res1.get(0));
							
							String queryTime2 = "SELECT Cast(AVG(time)as UNSIGNED) as result FROM `matrix` WHERE res1 = '" + candidate + "' AND activity1 = '" + act + "'";
							ArrayList<String> res2 = dbManager.executeQuery(queryTime2, "result");
							int time2 = 0;
							if (res2.get(0)!=null) time2 = Integer.parseInt(res2.get(0));
							int threshold2 = time1 / 100;
							
							/*
							System.out.println("Tempi medi dell'attivita " + act + " da parte di " + res + ": " + time1);
							System.out.println("Tempi medi dell'attivita " + act + " da parte di " + candidate + ": " + time2);
							System.out.println("Valore della soglia: " + threshold2);*/
							
							if (time2 <= (time1 + threshold2)) {
								timeAct++;
								//System.out.println("Aumentato di 1");
							}
						}
					}
					
					Affinity aff = new Affinity(res, candidate, commonAct, totalAct, commonRes, totalRes,
							 espAct, timeAct, weight);
					this.affinity.add(aff);
					
					/*
					System.out.println("Risorsa " + res + ", Candidato " + candidate + ", Attivit� in comune: " + commActivities.toString() + ", "
							+ commonAct + " su " + totalAct + ", Risorse comuni: " + commonRes + " su " + totalRes + ", "
									+ "Attivit� con exp: " + espAct + ", Attivit� con time: " + timeAct);
									*/
				
			}	
		}
		
	}
	
	public void exportToDB() {
		
		for (Affinity aff : this.affinity) {
			String queryInsert = "INSERT IGNORE INTO affinity(resource,candidate,activityFactor,collaborationFactor,experience,speed,total) VALUES(?,?,?,?,?,?,?)";
			ArrayList<String> fieldList = new ArrayList<String>(Arrays.asList(aff.getResource(), aff.getCandidate(), String.valueOf(aff.getActivityFactor()), String.valueOf(aff.getCollaborationFactor()),
			String.valueOf(aff.getEsperience()), String.valueOf(aff.getSpeed()), String.valueOf(aff.getTotalScore())));
			dbManager.executeStatement(queryInsert, fieldList);
		}
	}
	
	public void runReplacement(String folder, float beta) {
		
		File replaceFile= new File(folder+"/replace" + r + ".csv");
		try {
			if (replaceFile.createNewFile()) System.out.println("File creato");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//String newLineChar = "\n";
		//char commaSeparator = ',';
		//XConceptExtension ce = XConceptExtension.instance();
		
		//StringBuilder sb = new StringBuilder();
		
		String res = r;
		
		/*sb.append("Risorsa da sostituire: " + res);
		sb.append(newLineChar);

		sb.append("Risorsa Candidata" + commaSeparator + "ActivityFactor" + commaSeparator + "CollaborationFactor" + commaSeparator + "Experience"
				+ commaSeparator + "Speed" + commaSeparator + "Affinità");
		
		sb.append(newLineChar);
		
		String query = "SELECT candidate, activityFactor, collaborationFactor, experience, speed, total "
				+ "FROM `affinity` WHERE resource = '" + res + "' ORDER BY affinity.total DESC ";
		ArrayList<String> result = dbManager.executeQuery(query, "candidate;activityFactor;collaborationFactor;experience;speed;total");
		
		for (String s : result) {
			
			String[] sTokens = s.split(";");
			float aff = Float.parseFloat(sTokens[5]);
			int a = (int) (aff*100);
			sb.append(sTokens[0] + commaSeparator + sTokens[1] + commaSeparator + sTokens[2] + commaSeparator + sTokens[3]
					+ commaSeparator + sTokens[4] + commaSeparator + String.valueOf(a) + "%");
			sb.append(newLineChar);

		}
		sb.append(newLineChar);			
		*/
		
		//int n = this.resource.size()-1;
		int n=candidates.size();
		int m;
		float[][] C = null;
		double[] D = new double[n];
		double[] W = null;
		double[] Dmax = new double[n];         
		
		
		String querym = "SELECT DISTINCT class as result FROM event WHERE resource = '" + res + "'";
		ArrayList<String> activitiesOfRes = dbManager.executeQuery(querym, "result");
		//System.out.println("Attività che svolge " +  res + ":");
		/*for (String act : activities) {
			System.out.println(act + " ");
		}*/
		//currWorkload
		
		//Legge da tastiera le activities da sostituire --> da inserire automaticamente
		/*System.out.println("Inserisci le attività per cui " + res + " deve essere sostituito ('ok' per terminare):");
		Scanner scan = new Scanner(System.in);
		ArrayList<String> actSet = new ArrayList<String>();
		String input = scan.next();
		while (!input.equals("ok")) {
			boolean flag = false;
			boolean ins = false;
			for (String act : activities) {
				if (act.equals(input)) flag = true;
			}
			if (flag) actSet.add(input);
			else System.out.println("L'attività " + input + " inserita non è corretta. Riprova.");
			input = scan.next();
		}*/
		//
		
		m = actSet.size();
		W = new double[m];
		C = new float[n][m];
		int i = 0;
		
		
		for (String act : actSet) {
			//if (i == 0)
			//	sb.append("Candidato" + commaSeparator);
			//sb.append(act);
			//if (i != m-1) {
			//	sb.append(commaSeparator);}
			
			
			//SOSTITUISCO CON I TEMPI MEDI
			/*String queryW = "SELECT avgTime FROM activity WHERE label = '" + act + "'";
			ArrayList<String> avgTime = dbManager.executeQuery(queryW, "avgTime");
			W[i] = Integer.parseInt(avgTime.get(0));*/
			W[i]=actAvgWorkload.get(act);
			i++;
		}
		
		
		//Compute the D and Dmax come rate
		i = 0;
		for (String r : candidates) {
			//if(!candidates.contains(r))
			//	continue;
			if (!r.equals(res)) {
				String queryD = "SELECT maxWorkload-currWorkload as result FROM `workloads_forTest` WHERE res = '" + r + "'";
				ArrayList<String> avail = dbManager.executeQuery(queryD, "result");
				//D[i] = Integer.parseInt(avail.get(0))*3600;
				D[i] = Double.parseDouble(avail.get(0));
				
				String queryDmax = "SELECT maxWorkload as result FROM `workloads_forTest` WHERE res = '" + r + "'";
				ArrayList<String> dmax = dbManager.executeQuery(queryDmax, "result");
				Dmax[i] = Double.parseDouble(dmax.get(0));
				
				i++;
			}
		}
		
		
		//Retrieve the costs Cij
		i=0;
		int j =0;
		for (String r : candidates) {
			//if(!candidates.contains(r))
			//	continue;
			j = 0;
			if (!r.equals(res)) {
				for (String act : actSet) {
					String queryC = "SELECT total as result FROM `cost` WHERE resource = '" + res + "' AND candidate = '" + r + "' AND activity = '" + act + "'";
					ArrayList<String> cost = dbManager.executeQuery(queryC, "result");
					try {
					C[i][j] = Float.parseFloat(cost.get(0));
					}
					catch(Exception e) {
						System.out.println("******* res="+res+" candidate="+r+" act="+act);
						System.exit(0);
					}
					j++;
				}
				i++;
			}
		}
		
		//sb.append(newLineChar);
		//10188
		int[][] val = this.solveModel(candidates.size(), m, C, D, W, Dmax, beta);
		
		//i=0;
		//j=0;
		//for (String r : this.resource) {
			//j=0;
			//if (!r.equals(res)) {
				//sb.append(r);
				//sb.append(commaSeparator);
				//for (String act : actSet) {
					//sb.append(String.valueOf(val[i][j]));
					//if (j != m-1) sb.append(commaSeparator);
					//j++;
				//}
				//sb.append(newLineChar);
				//i++;
			//}
		//}
		
		//sb.append(newLineChar);
		//sb.append(newLineChar);
		
	
		
		/*BufferedWriter bw ;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(replaceFile)));
			bw.write(sb.toString());
			bw.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/	
	}
	
	
	
	
	//la n rappresenta il numero di risorse, la m il numero delle attivit�, C � la matrice dei costi, D � il vettore delle disponibilit�,
	//W � vettore del carico di lavoro delle attivit�
	public int[][] solveModel(int numResources, int numActivitiesToAssign, float[][] C, double[] D, double[] W, double[] Dmax, float beta) {
		
		int[][] output = new int[numResources][numActivitiesToAssign];
		
		try {
			
			IloCplex cplex = new IloCplex();
			
			//genera una matrice nxm di variabili booleane con dominio 0,1
			IloNumVar[][] x = new IloNumVar[numResources][numActivitiesToAssign];
			
			for (int i=0; i<numResources; i++) {
				x[i] = cplex.boolVarArray(numActivitiesToAssign);
			}
			
			//genera la funzione obiettivo
			IloLinearNumExpr expr = cplex.linearNumExpr();
			
			for (int i=0; i<numResources; i++) {
				for (int j=0; j<numActivitiesToAssign; j++) {
					expr.addTerm(C[i][j], x[i][j]);
				}
			}

			cplex.addMinimize(expr);
			
			//vincoli
		
			for (int j=0; j<numActivitiesToAssign; j++) {
				IloLinearNumExpr constr = cplex.linearNumExpr();
				for (int i=0; i<numResources; i++) {
					constr.addTerm(1, x[i][j]);
				}
				cplex.addEq(constr, 1);				//vincolo di eguaglianza
				//cplex.addGe(constr, 1);				//vincolo maggiore uguale
			}	
			
			for (int i=0; i<numResources; i++) {
				IloLinearNumExpr constr = cplex.linearNumExpr();
				for (int j=0; j<numActivitiesToAssign; j++) {
					//double li = 1 - beta*(D[i]/Dmax[i]);
					double li=D[i];
					if(li<0)
						li=0;
					constr.addTerm(W[j]*li, x[i][j]);
				}
				cplex.addLe(constr, D[i]);
			}
			
			long startSolve = System.currentTimeMillis();
			if ( cplex.solve() ) {
				long duration = System.currentTimeMillis()-startSolve;
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value= " + cplex.getObjValue());
				
				File file = new File("risultatiFinali.txt");
				FileWriter fr;
				try {
					fr = new FileWriter(file, true);
					fr.write(this.r+";"+candidates.size()+";"+actSet.size()+";"+duration+";"+cplex.getStatus()+"\n");
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			for (int i=0;i<numResources;i++) {
				
				for (int j=0;j<numActivitiesToAssign;j++) {
					output[i][j] = (int) cplex.getValue(x[i][j]);
					}
				}
			
			cplex.end();
			}

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
		
	}
	
	//Get all resources from the event log
	public ArrayList<String> getAllResources(){
		String query="SELECT distinct res from supporto_test order by res";
		ArrayList<String> resources = dbManager.executeQuery(query, "res");
		return resources;
	}
	
	//Get all the days in which a resource worked
	public ArrayList<String> getDatesByResource(String resource){
		String query="SELECT distinct date from supporto_test where res='"+resource+"'";
		ArrayList<String> dates = dbManager.executeQuery(query, "date");
		return dates;
	}
	
	//Get all activities done in a specific day for a resource
	public HashMap<String,Integer> getActivitesByRes_Day(String resource, String date){
		HashMap<String,Integer> results=new HashMap<String,Integer>();
		String query="SELECT concat(activity,':',num) as act from supporto_test where res='"+resource+"' and date='"+date+"'";
		ArrayList<String> arr = dbManager.executeQuery(query, "act");
		for(String s:arr) {
			String activity=s.split(":")[0];
			int num=Integer.parseInt(s.split(":")[1]);
			results.put(activity, num);
		}
		return results;
	}

	public void updateActivityTable(String avgTimeLastActivity) {
			
			ArrayList<String> activities = new ArrayList<String>();
			
			String query = "SELECT DISTINCT class FROM event";
			activities = dbManager.executeQuery(query, "class");
			for (String a : activities) {
				
				if (a.equals(this.lastActivity)) {
					String queryInsert = "INSERT IGNORE INTO activity(label,avgTime) VALUES(?,?)";
					ArrayList<String> fieldList = new ArrayList<String>(
						Arrays.asList(a, avgTimeLastActivity));
					dbManager.executeStatement(queryInsert, fieldList);
				}
				else {
					String queryTime = "SELECT cast(avg(time) as UNSIGNED) as result FROM `matrix` WHERE activity1 = '" + a + "'";
					ArrayList<String> res1 = dbManager.executeQuery(queryTime, "result");
					int time1 = Integer.parseInt(res1.get(0));
					
					String queryInsert = "INSERT IGNORE INTO activity(label,avgTime) VALUES(?,?)";
					ArrayList<String> fieldList = new ArrayList<String>(
						Arrays.asList(a, String.valueOf(time1)));
					dbManager.executeStatement(queryInsert, fieldList);
				}
				
			}
	}
	
	public void getCost(float[] w) {
			
		String res = r;
			
			for (String candidate : resource) {
				
				if (!res.equals(candidate)) {
					
					String query = "SELECT DISTINCT class as result FROM event WHERE resource = '" + res + "'";
					ArrayList<String> activities = dbManager.executeQuery(query, "result");
					
					for (String act : activities) {
					
						float collaboration = 0;
						float experience = 0;
						float speed = 0;
						float total = 1;
						float[] weight = w;
						
						if (!act.equals(this.lastActivity)) {
						
							String query2 = "SELECT id FROM event WHERE resource = '" + candidate + "' AND class = '" + act + "'";
							ArrayList<String> result = dbManager.executeQuery(query2, "id");
							
							if (result.size()!=0) {
								
								float commonRes = 0;
								float totalRes = 0;
								
								String queryTotRes = "SELECT DISTINCT res2 FROM matrix WHERE res1 = '" + res + "' AND activity1 = '" + act + "'";
								ArrayList<String> totRes = dbManager.executeQuery(queryTotRes, "res2");
								totalRes = totRes.size();
								for (String res2 : totRes) {
									String queryComm = "SELECT DISTINCT id FROM `matrix` WHERE res1 = '" + candidate + "' AND res2 = '" + res2 + "' AND activity1 = '" + act + "'";
									ArrayList<String> commRes = dbManager.executeQuery(queryComm, "id");
									
									if (commRes.size() != 0) commonRes++;
								}
								
								if (totalRes == 0) collaboration = 0;
								else collaboration = commonRes/totalRes;
								
								String queryOcc = "SELECT COUNT(DISTINCT id) as result FROM `event` WHERE resource = '" + res + "' AND class = '" + act + "'";
								ArrayList<String> result1 = dbManager.executeQuery(queryOcc, "result");
								float occ1 = Float.parseFloat(result1.get(0));
								
								String queryOcc2 = "SELECT COUNT(DISTINCT id) as result FROM `event` WHERE resource = '" + candidate + "' AND class = '" + act + "'";
								ArrayList<String> result2 = dbManager.executeQuery(queryOcc2, "result");
								float occ2 = Float.parseFloat(result2.get(0));
								
								if (occ2 >= occ1) {experience = 1;}
								else {if (occ1 == 0) experience = 0;
									else experience = occ2/occ1;}
								
								String queryTime = "SELECT Cast(AVG(time)as UNSIGNED) as result FROM `matrix` WHERE res1 = '" + res + "' AND activity1 = '" + act + "'";
								ArrayList<String> res1 = dbManager.executeQuery(queryTime, "result");
								float time1 = 0;
								if (res1.get(0)!=null) time1 = Float.parseFloat(res1.get(0));
								
								String queryTime2 = "SELECT Cast(AVG(time)as UNSIGNED) as result FROM `matrix` WHERE res1 = '" + candidate + "' AND activity1 = '" + act + "'";
								ArrayList<String> res2 = dbManager.executeQuery(queryTime2, "result");
								float time2 = 0;
								if (res2.get(0)!=null) time2 = Float.parseFloat(res2.get(0));
								
								if (time2 <= time1) speed = 1;
								else {if (time2 == 0) speed = 0;
									else speed = time1/time2;}
								
								float max = weight[0] + weight[1] + weight[2];
								total = collaboration * weight[0] + experience * weight[1] + speed * weight[2];
								total = total / max;
								total = 1 - total;
									
							}
						}
						
						else {
							String queryOcc = "SELECT COUNT(DISTINCT id) as result FROM `event` WHERE resource = '" + res + "' AND class = '" + act + "'";
							ArrayList<String> result1 = dbManager.executeQuery(queryOcc, "result");
							float occ1 = Float.parseFloat(result1.get(0));
							
							String queryOcc2 = "SELECT COUNT(DISTINCT id) as result FROM `event` WHERE resource = '" + candidate + "' AND class = '" + act + "'";
							ArrayList<String> result2 = dbManager.executeQuery(queryOcc2, "result");
							float occ2 = Float.parseFloat(result2.get(0));
							
							if (occ2 >= occ1) {experience = 1;}
							
							else {experience = occ2/occ1;}
							
							float max = weight[1];
							total = collaboration * weight[0] + experience * weight[1] + speed * weight[2];
							total = total / max;
							total = 1 - (total);
						}
					
						String queryInsert = "INSERT INTO cost(resource,candidate,activity,collaboration,experience,speed,total) VALUES(?,?,?,?,?,?,?)";
						ArrayList<String> fieldList = new ArrayList<String>(Arrays.asList(res, candidate, act, String.valueOf(collaboration),
								String.valueOf(experience), String.valueOf(speed), String.valueOf(total)));
						dbManager.executeStatement(queryInsert, fieldList);
					
				} 
			}
		}
	}
}