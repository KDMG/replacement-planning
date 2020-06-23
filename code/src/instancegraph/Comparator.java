package instancegraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//this class computes the metrics used to compare the results obtained by building the instance graphs
//using different algorithms. Actually, the differences computation is performed by means of
//the graph matcher of subdue: it returns a textual file as outcome, from which we can extract the
//needed information
public class Comparator {
	private String[] fileList;

	public Comparator(String[] fileList) {
		super();
		this.fileList = fileList;
	}

	public String[] getFileList() {
		return fileList;
	}

	public void setFileList(String[] fileList) {
		this.fileList = fileList;
	}
	
	public void compareGraphs(){
		for(int i=0; i<fileList.length; i++){
			ArrayList<String> differentGraphs= new ArrayList<String>();
			String inputFile=fileList[i];
			int numDiff=0;
			int numTot=0;
			double costTot=0;
			try {
				BufferedReader reader = new BufferedReader(new FileReader(new File(inputFile)));
				String strLine;
				String graph="";
				while ((strLine = reader.readLine()) != null) {
					if(strLine.startsWith("graphs")){
						graph=strLine.substring(strLine.indexOf("/")+1, strLine.indexOf("."));
						numTot++;
					}
					else if(strLine.startsWith("Match")){
						String[] token=strLine.split("=");
						double cost=Double.parseDouble(token[1]);
						if(cost>0){
							numDiff++;
							costTot=costTot+cost;
							differentGraphs.add(graph);
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double avgCost= (double) costTot / (double) numTot;
			System.out.println("result of file"+fileList[i]);
			System.out.println("Number of different Graphs: "+numDiff);
			System.out.println("Total Matching Cost"+costTot);
			System.out.println("Avg. matching Cost: "+ avgCost);
			for(String g: differentGraphs){
				System.out.println(g);
			}
		}
	
		
	}
}
