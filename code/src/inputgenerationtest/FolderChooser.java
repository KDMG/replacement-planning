package inputgenerationtest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utility.AlignedTrace;
import utility.Constants;
import utility.CSVParser;
import utility.DBManager;
import utility.LogManager;
import utility.PetriNetParser;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

import instancegraph.IGBuilderUpdated;

public class FolderChooser extends JPanel implements ActionListener {
	JButton go;
	JFileChooser chooser;
	String choosertitle;
	String[] mainArgs;
	LogManager logManager;
	
	  public FolderChooser(String[] args, LogManager logManager) {
		  	this.mainArgs=args;
		  	this.logManager=logManager;
		  	JFrame frame = new JFrame("JButton Demo");
		  	JPanel panel = new JPanel();
		    go = new JButton("Do it");
		    go.addActionListener(this);
		    panel.add(go);
		    frame.getContentPane().add(panel);
		    frame.pack();
		    frame.setVisible(true);
		   }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle(choosertitle);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //
	    // disable the "All files" option.
	    //
	    chooser.setAcceptAllFileFilterUsed(false);
	    //    
	    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
	    	this.startGraphGeneration();
	      System.out.println("getCurrentDirectory(): " 
	         +  chooser.getCurrentDirectory());
	      System.out.println("getSelectedFile() : " 
	         +  chooser.getSelectedFile());
	      }
	    else {
	      System.out.println("No Selection ");
	      }

	}

	private void startGraphGeneration() {
		String mainFolder = chooser.getSelectedFile().getAbsolutePath();
		String petriNetPath = "";
		//da rimettere Constants.MODEL = petriNetPath;
		File file = new File(mainFolder);
		String[] logFilesDir = file.list();
		File expDir = null;
		File confDir = null;
		File graphDir = null;
		for (int f = 0; f < logFilesDir.length; f++) {
			if (!logFilesDir[f].startsWith(".")
					&& logFilesDir[f].contains("Log") && logFilesDir[f].endsWith("xes")) {
				// for each log in the main folder, I create an exp directory if
				// not exists
				expDir = new File(mainFolder + "/" + logFilesDir[f].replace(".xes", "")+"ExpHeuristic");
				confDir = new File(expDir.getAbsolutePath() + "/Conformance");
				if (!expDir.exists()) {
					boolean result = false;

					try {
						expDir.mkdir();
						result = true;
					} catch (SecurityException se) {
						// handle it
					}
					if (result) {
						System.out.println("DIR exp created");
					}
				}

				if (!confDir.exists()) {
					boolean result = false;

					try {
						confDir.mkdir();
						result = true;
					} catch (SecurityException se) {
						// handle it
					}
					if (result) {
						System.out.println("DIR conf created");
					}
				}
				// I copy the log in the exp directory
				File source = new File(mainFolder +"/"+ logFilesDir[f]);
				File dest = new File(expDir.getAbsolutePath() +"/"+ logFilesDir[f]);
				try {
					FileUtils.copyFile(source, dest);
					//FileUtils.copyDirectory(source, dest);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		XLog xlog = null;
		for (String dir : directories) {
			//da rimettere Constants.CONFORMANCE = confDir.getAbsolutePath();
			File dirFile = new File(mainFolder + "/" + dir);
			XesXmlParser xesParser = new XesXmlParser();
			File[] logFiles = null;
			try {
				logFiles = dirFile.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".xes");
					}
				});
				if (logFiles.length > 1)
					System.out.println("check " + dir);
				List<XLog> xlogList = xesParser.parse(logFiles[0]);
				// da rimettere Constants.EVENTLOG = logFiles[0].getAbsolutePath();
				xlog = xlogList.get(0);
				logManager = new LogManager(xlog);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// first: heuristic miner, with and without repairing
			InputGenerator inputGen = new InputGenerator(xlog,
					dirFile.getAbsolutePath() + "/", "heuristic");
			inputGen.generateInput();

			// generate Grafi directory for heuristic rep
			File dirGraphRep = new File(mainFolder + "/" + dir
					+ "/GrafiHeuristicRep/");
			// if exists, delete
			if (dirGraphRep.exists()) {
				deleteDir(dirGraphRep);
			}
			try {
				dirGraphRep.mkdir();
			} catch (SecurityException se) {
				// handle it
			}
			//da rimettere Constants.GRAPHPATH = dirGraphRep.getAbsolutePath();
			DBManager dbManagerFolderRep = new DBManager("","","","");
			dbManagerFolderRep.createDB();
			CSVParser csvParserFolder = new CSVParser(Constants.CONFORMANCE,
					dbManagerFolderRep);
			PetriNetParser parserFolder = new PetriNetParser(new File(
					Constants.MODEL), dbManagerFolderRep);
			parserFolder.getOrderingRelations(this.mainArgs);
			ArrayList<AlignedTrace> conformanceResult = csvParserFolder.parse();

			IGBuilderUpdated igRep = new IGBuilderUpdated(xlog,
					dbManagerFolderRep, conformanceResult, true);
			igRep.createIGSet();

			// not repaired
			// generate Grafi directory
			File dirGraphNoRep = new File(mainFolder + "/" + dir
					+ "/GrafiHeuristicNoRep/");
			// if exists, delete
			if (dirGraphNoRep.exists()) {
				deleteDir(dirGraphNoRep);
			}
			try {
				dirGraphNoRep.mkdir();
			} catch (SecurityException se) {
				// handle it
			}
			// da rimettere Constants.GRAPHPATH = dirGraphNoRep.getAbsolutePath();
			DBManager dbManagerFolderNoRep = new DBManager("","","","");
			dbManagerFolderNoRep.createDB();

			IGBuilderUpdated igNoRep = new IGBuilderUpdated(xlog,
					dbManagerFolderNoRep, conformanceResult, false);
			igNoRep.createIGSet();
			for (int f = 0; f < logFilesDir.length; f++) {
				if (!logFilesDir[f].startsWith(".")
						&& logFilesDir[f].contains("Log") && logFilesDir[f].endsWith("xes")) {
			// after I obtained the results for Heuristic Miner, I have to generate again the input
			//for the inductive miner. Maybe a different folder is useful. This code is terrible.
			expDir = new File(mainFolder + "/" + logFilesDir[f].replace(".xes", "")+"ExpInductive");
			confDir = new File(expDir.getAbsolutePath() + "/Conformance");
			if (!expDir.exists()) {
				boolean result = false;

				try {
					expDir.mkdir();
					result = true;
				} catch (SecurityException se) {
					// handle it
				}
				if (result) {
					System.out.println("DIR exp inductive created");
				}
			}

			if (!confDir.exists()) {
				boolean result = false;

				try {
					confDir.mkdir();
					result = true;
				} catch (SecurityException se) {
					// handle it
				}
				if (result) {
					System.out.println("DIR conf inductive created");
				}
			}
			// I copy the log in the exp directory
			File source = new File(mainFolder +"/"+ logFilesDir[f]);
			File dest = new File(expDir.getAbsolutePath() +"/"+ logFilesDir[f]);
			try {
				FileUtils.copyFile(source, dest);
				//FileUtils.copyDirectory(source, dest);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		}
	directories = file.list(new FilenameFilter() {
		@Override
		public boolean accept(File current, String name) {
			return new File(current, name).isDirectory();
		}
	});
	xlog = null;
	for (String dir : directories) {
		// da rimettere Constants.CONFORMANCE = confDir.getAbsolutePath();
		File dirFile = new File(mainFolder + "/" + dir);
		XesXmlParser xesParser = new XesXmlParser();
		File[] logFiles = null;
		try {
			logFiles = dirFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".xes");
				}
			});
			if (logFiles.length > 1)
				System.out.println("check " + dir);
			List<XLog> xlogList = xesParser.parse(logFiles[0]);
			// da rimettre Constants.EVENTLOG = logFiles[0].getAbsolutePath();
			xlog = xlogList.get(0);
			logManager = new LogManager(xlog);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// first: heuristic miner, with and without repairing
		InputGenerator inputGen = new InputGenerator(xlog,
				dirFile.getAbsolutePath() + "/", "inductive");
		inputGen.generateInput();

		// generate Grafi directory for heuristic rep
		File dirGraphRep = new File(mainFolder + "/" + dir
				+ "/GrafiInductiveRep/");
		// if exists, delete
		if (dirGraphRep.exists()) {
			deleteDir(dirGraphRep);
		}
		try {
			dirGraphRep.mkdir();
		} catch (SecurityException se) {
			// handle it
		}
		// da rimettere Constants.GRAPHPATH = dirGraphRep.getAbsolutePath();
		DBManager dbManagerFolderRep = new DBManager("","","","");
		dbManagerFolderRep.createDB();
		CSVParser csvParserFolder = new CSVParser(Constants.CONFORMANCE,
				dbManagerFolderRep);
		PetriNetParser parserFolder = new PetriNetParser(new File(
				Constants.MODEL), dbManagerFolderRep);
		parserFolder.getOrderingRelations(this.mainArgs);
		ArrayList<AlignedTrace> conformanceResult = csvParserFolder.parse();

		IGBuilderUpdated igRep = new IGBuilderUpdated(xlog,
				dbManagerFolderRep, conformanceResult, true);
		igRep.createIGSet();

		// not repaired
		// generate Grafi directory
		File dirGraphNoRep = new File(mainFolder + "/" + dir
				+ "/GrafiInductiveNoRep/");
		// if exists, delete
		if (dirGraphNoRep.exists()) {
			deleteDir(dirGraphNoRep);
		}
		try {
			dirGraphNoRep.mkdir();
		} catch (SecurityException se) {
			// handle it
		}
		// da rimettere Constants.GRAPHPATH = dirGraphNoRep.getAbsolutePath();
		DBManager dbManagerFolderNoRep = new DBManager("","","","");
		dbManagerFolderNoRep.createDB();

		IGBuilderUpdated igNoRep = new IGBuilderUpdated(xlog,
				dbManagerFolderNoRep, conformanceResult, false);
		igNoRep.createIGSet();

		}

	}

	private static void deleteDir(File dir) {
		File[] contents=dir.listFiles();
		if(contents!=null){
			for (File file: contents){
				deleteDir(file);
			}
		}
		dir.delete();
	}
	
}
