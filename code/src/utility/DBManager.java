package utility;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class DBManager  {

	private Statement s;
	private Connection conn;
	private static int contatore;
	private String dbUrl;
	private String dbName;
	private String username;
	private String password;
	private int error=-1;


	/**
	 * Fornisce connettività al DB
	 * 
	 * @return Statement
	 */
	
	public DBManager(String dbUrl, String dbName, String username, String password, String dbCreato){
		this.dbUrl=dbUrl;
		this.dbName=dbName;
		this.username=username;
		this.password=password;

		if(dbCreato.equals("0"))
			createDB();
		
		connect();
	}

	
	
	public DBManager(String dbUrl, String dbName, String username, String password){
		this.dbUrl=dbUrl;
		this.dbName=dbName;
		this.username=username;
		this.password=password;
		connect();
	}

	public void createDB(){
		String url = this.dbUrl + "?user=" + this.username + "&password=" + this.password;

		try {
			conn = DriverManager.getConnection(url);
			s = conn.createStatement();
			//check if the DB exists
			//String dbCheck="SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"+dbName+"'";
			//ResultSet res= s.executeQuery(dbCheck);
			//boolean dbExists=false;
			//if (!res.isBeforeFirst()) {    
			//	dbExists=false; 
			//	} 
			//else
			//	dbExists=true;
			//if(!dbExists){
				//drop the old DB if it exists
				s.executeUpdate("DROP DATABASE IF EXISTS "+dbName);
				s = conn.createStatement();
				//create the DB
				String sql = "CREATE DATABASE "+dbName;
				s.executeUpdate(sql);
				connect();
				InputStream in= new FileInputStream("res/bpi.sql");
				populateDB(conn,in);
			//}
			//if the db exists, I have to empty all its tables
			/*else{
				connect();
				DatabaseMetaData md = conn.getMetaData();
				ResultSet rs = md.getTables(null, null, "%", null);
				while (rs.next()) {
				  String tableName=rs.getString(3);
				  String truncateQuery="TRUNCATE "+tableName;
				  s.executeUpdate(truncateQuery);
				}
			}*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	private void populateDB(Connection conn, InputStream in)  throws SQLException {
		// there must be a better way to do this

			Scanner s = new Scanner(in);
			s.useDelimiter("(;(\r)?\n)|(--\n)");
			Statement st = null;
			try
			{
				st = conn.createStatement();
				while (s.hasNext())
				{
					String line = s.next();
					if (line.startsWith("/*!") && line.endsWith("*/"))
					{
						int i = line.indexOf(' ');
						line = line.substring(i + 1, line.length() - " */".length());
					}

					if (line.trim().length() > 0)
					{
						st.execute(line);
					}
				}
			}
			finally
			{
				if (st != null) st.close();
			}
		}
		

	private void connect() {
		try {
			// String url="jdbc:mysql://" + HOST + "/"	+ db+ "?user=" + username+ "";    // M.
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(this.dbUrl + this.dbName,this.username,this.password);
			s = conn.createStatement();
			
		} catch (SQLException e) {
			System.out.println(e.getErrorCode()+" "+e.getMessage());
			e.printStackTrace();
			this.error=e.getErrorCode();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
//	protected static Statement connectOtherDb(String db) {
//
//		Statement s = null;
//		String username = "root";
//		try {
//			//Class.forName("com.mysql.jdbc.Driver").newInstance();
//			Connection conn = DriverManager.getConnection("jdbc:mysql://" + HOST + "/"
//					+ db+ "?user=" + username+ "");
//			s = conn.createStatement();
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//
//		return s;
//
//	}
	
//	protected static ArrayList<String> checkConnection(){
//		ArrayList<String> result=new ArrayList<String>();
//		Statement s=connectOtherDb("information_schema");
//		String query="SELECT id FROM PROCESSLIST WHERE db ='petrinet' AND HOST LIKE 'local'";
//		try{
//			ResultSet r = s.executeQuery(query);
//			while (r.next()) {
//				String resultValue = r.getString("id");
//				int index = result.indexOf(resultValue);
//				if (index < 0)
//					result.add(resultValue);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		finally{
//			try {
//				if (!s.isClosed()){
//					Connection conn=s.getConnection();
//					if(!conn.isClosed()){
//						conn.close();
//					}
//					s.close();
//				}
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return result;
//	}

	public  ArrayList<String> executeQuery(String query,
			String resultField) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			ResultSet r = s.executeQuery(query);
			while (r.next()) {
				if(resultField.contains(";")){
					String[] fieldTokens=resultField.split(";");
					String resultValueTot="";
					int flag=0;
					for(int i=0;i<fieldTokens.length;i++){
						String field=fieldTokens[i];
						String resultValue = r.getString(field);
						int index = result.indexOf(resultValue);
						if(index < 0){
							//per evitare il ; iniziale:
							if(resultValueTot.equals(""))
								resultValueTot=resultValue;
							else
								resultValueTot=resultValueTot+";"+resultValue;
						}
						else{
							flag=1;
						}
						
					}
					
					
					if(flag==0)
						result.add(resultValueTot);
					else
						result.add("problema");
					
				}
				else{
				String resultValue = r.getString(resultField);
				int index = result.indexOf(resultValue);
				if (index < 0)
					result.add(resultValue);
			}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return result;
	}
	//inserimenti tramite PreparedStatement
	public  void executeStatement(String query, ArrayList<String> fieldList){	
		try {
			PreparedStatement prdStm=conn.prepareStatement(query);
			int cont=1;
			for(String field:fieldList){
				prdStm.setString(cont, field);
				cont++;
			}
			//System.out.println(prdStm);
			prdStm.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public ArrayList<String> executeSelectStatement(String query, ArrayList<String> fieldList, String resultField){
		ArrayList<String> result= new ArrayList<String>();
		try{
			PreparedStatement prdStm = conn.prepareStatement(query);
			int cont=1;
			for(String field:fieldList){
				prdStm.setString(cont, field);
				cont++;
			}
			ResultSet r=prdStm.executeQuery();
				while (r.next()) {
					if(resultField.contains(";")){
						String[] fieldTokens=resultField.split(";");
						String resultValueTot="";
						int flag=0;
						for(int i=0;i<fieldTokens.length;i++){
							String field=fieldTokens[i];
							String resultValue = r.getString(field);
							int index = result.indexOf(resultValue);
							if(index < 0){
								//per evitare il ; iniziale:
								if(resultValueTot.equals(""))
									resultValueTot=resultValue;
								else
									resultValueTot=resultValueTot+";"+resultValue;
							}
							else{
								flag=1;
							}
							
						}
						
						
						if(flag==0)
							result.add(resultValueTot);
						else
							result.add("problema");
						
					}
					else{
					String resultValue = r.getString(resultField);
					int index = result.indexOf(resultValue);
					if (index < 0)
						result.add(resultValue);
				}
				}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public  int queryUpdate(String query) {
		int result = 0;
		try {
			result = s.executeUpdate(query);
//			if(result >-1){
//				s.executeQuery("SELECT LAST_INSERT_ID()");
//			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result=-1;
		}
		return result;
	}
	
	public void resetDB() {

		try {
			ArrayList<String> tables=new ArrayList<String>();
			DatabaseMetaData md = s.getConnection().getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
			  tables.add(rs.getString(3));
			}
			for(String table:tables){
				String queryDelete="DELETE FROM "+table;
				s.executeUpdate(queryDelete);
				String queryAlter="ALTER TABLE "+table+" AUTO_INCREMENT=1";
				s.executeUpdate(queryAlter);
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void closeConnection(){
		try {
			if (!s.isClosed()){
				Connection conn=s.getConnection();
				if(!conn.isClosed()){
					conn.close();
				}
				s.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public int getError() {
		return error;
	}
	
}
