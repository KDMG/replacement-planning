package initializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nl.tue.astar.AStarException;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.petrinet.replayresult.SwappedMove;
import org.processmining.plugins.petrinet.replayresult.ViolatingSyncMove;
import org.processmining.plugins.pnml.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

//this class takes an event log, indications about process discovery technique, and output folder and generates:
//the Petri Net; the Conformance Checker Results; the folders needed to the IG algorithm
public class InputGenerator {
	private XLog log;
	private String outputFolder;
	//variables for replayer
	private NumberFormat nf = NumberFormat.getInstance();
	private static int RELIABLEMIN = 0;
	private static int RELIABLEMAX = 1;
	private static int MIN = 2;
	private static int MAX = 3;
	private static int SVAL = 4;
	private static int SVALRELIABLE = 5;
	private static int MVAL = 6;
	private static int MVALRELIABLE = 7;
	private static int PERFECTCASERELIABLECOUNTER = 8;
	private int numReliableSynchronized = 0;
	private int numReliableModelOnlyInvi = 0;
	private int numReliableModelOnlyReal = 0;
	private int numReliableLogOnly = 0;
	private int numReliableViolations = 0;
	private int numSwapViolations = 0;
	private int numReplaceViolations = 0;

	private int numCaseInvolved = 0;
	private int numReliableCaseInvolved;
	
	private String petriNetPath;// = null;
	private PNRepResult replayerRes; // = null;
	private UIContext mainContext=new UIContext();
	private UIPluginContext plugContext=mainContext.getMainPluginContext();
	
	
	
	public InputGenerator(XLog log, String petriNetPath, String outputFolder) {
		super();
		this.log = log;
		this.petriNetPath=petriNetPath;
		this.outputFolder=outputFolder;
	}
	
	
	public void generateInput() throws IOException{
		// DA QUA
		// PetrinetImpl petriNet=null;
		XLogInfo logInfo =  XLogInfoFactory.createLogInfo(log);
		Collection<XEventClassifier> classifiers= logInfo.getEventClassifiers();
		Iterator<XEventClassifier> it= classifiers.iterator();
		XEventClassifier usedClassifier=null;
		boolean flag=false;
		while(it.hasNext() && !flag){
			XEventClassifier classifier=it.next();
			if(classifier.toString().contains("Event Name")){
				usedClassifier=classifier;
				flag=true;
			}
		}
		Pnml pnml=null;
		PnmlImportUtils importUtils= new  PnmlImportUtils();
		
		
		try {
			pnml= importUtils.importPnmlFromStream(null, new FileInputStream(petriNetPath), petriNetPath, new File(petriNetPath).getTotalSpace());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//PetrinetGraph net = PetrinetFactory.newPetrinet(pnml.getLabel());
		PetrinetImpl petriNet = (PetrinetImpl) PetrinetFactory.newPetrinet(pnml.getLabel());
		Object[] res=connectNet(this.plugContext, pnml, petriNet);
		
		Marking initialMarking=getInitialMarking((PetrinetImpl)petriNet);

		
		
		PNLogReplayer replayer= new PNLogReplayer();
		// PNRepResult replayerRes=null;
		try {
			//create the mapping
			XEventClass dummyEventClass= new XEventClass("DUMMY", -1);
			TransEvClassMapping mapping=null;
			mapping= new TransEvClassMapping(usedClassifier, dummyEventClass);
			
			mapEventsToTransition(mapping, petriNet, dummyEventClass);
			// create parameter
			CostBasedCompleteParam parameter = new CostBasedCompleteParam
					(this.getEventClasses(),
							dummyEventClass, petriNet.getTransitions(), 1, 1);
			parameter.setGUIMode(false);
			parameter.setCreateConn(false);
			Marking finalMarking=getFinalMarking(petriNet);
			parameter.setInitialMarking(initialMarking);
			parameter.setFinalMarkings(finalMarking);
			parameter.setMaxNumOfStates(200000);
			
			PetrinetReplayerWithoutILP selectedAlg = new PetrinetReplayerWithoutILP();
			this.replayerRes=replayer.replayLog(null, petriNet, this.log, mapping, 
					selectedAlg, parameter);
		
			File repFile= new File(this.outputFolder+"/Conformance/alignment.csv");
			if (repFile.createNewFile()) System.out.println("File creato");
			this.exportToFile(replayerRes,repFile, petriNet);
			//this.createFile(repFile);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AStarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

		

	private XLog checkEventLog() {
		//remove the lifecycle attribute
		XLog newLog=log;
		Iterator<XTrace> it= newLog.iterator();
		while(it.hasNext()){
			XTrace trace= it.next();
			XAttributeMap traceAtt=trace.getAttributes();
			Iterator<XEvent> itEvent=trace.iterator();
			while(itEvent.hasNext()){
				XEvent event= itEvent.next();
				XAttributeMap attributesMap=event.getAttributes();
				String lifecycle="";
				try{
					lifecycle=attributesMap.get("lifecycle:transition").toString();
				}
				catch(NullPointerException e){
					System.out.println("lifecycle not present");
				}
				if(!lifecycle.equals(""))
					attributesMap.remove("lifecycle:transition");
				event.setAttributes(attributesMap);
								}
				}
		OutputStream output=null;
		try {
			output = new FileOutputStream("logChanged.xes");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XesXmlSerializer serializer= new XesXmlSerializer() ;
		try {
			serializer.serialize(newLog, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		newLog=null;
		XesXmlParser xesParser= new XesXmlParser();
		java.util.List<XLog> xlogList=null;
		try {
			xlogList = xesParser.parse(new File("logChanged.xes"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			newLog=xlogList.get(0);	
			return newLog;
		}
		
	


	


	private void mapEventsToTransition(TransEvClassMapping mapping,
			PetrinetImpl petriNet, XEventClass dummy) {
		Collection<Transition> transitionList=petriNet.getTransitions();
		Collection<XEventClass> eventClasses=this.getEventClasses();
		ArrayList<Transition> unmapped= new ArrayList<Transition>();
		for(Transition t: transitionList){
			boolean mapped=false;
			String tName=t.getLabel();
			for(XEventClass evClass: eventClasses){
				if(evClass.getId() != null && evClass.getId().equals(tName))
				{
					mapping.put(t, evClass);
					mapped=true;
					break;
				}
			}
			if(!mapped)
				unmapped.add(t);
		}
		for(Transition t: unmapped){
			mapping.put(t, dummy);
		}
		
	}

	private Collection<XEventClass> getEventClasses() {
		XLogInfo info=null;
		if(this.log.getClassifiers().size()>0)
			info=this.log.getInfo(this.log.getClassifiers().get(0));
		Collection<XEventClass> eventClasses= new ArrayList<XEventClass>();
		if(info!=null){
			Collection<XEventClass>  eventClassesOriginal=info.getEventClasses().getClasses();
			for(XEventClass evClass: eventClassesOriginal){
				String evClassName=evClass.toString();
				if(evClassName.contains("+")){
					String[] tokens= evClassName.split("\\+");
					XEventClass newEvClass= new XEventClass(tokens[0], evClass.getIndex());
					eventClasses.add(newEvClass);
				}
			}
			
		}
		else{
			int cont=1;
			
			for(XTrace trace: this.log){
				for(XEvent event: trace){
					String eventClassName=event.getAttributes().get("concept:name").toString();
					XEventClass eventClass= new XEventClass(eventClassName, cont);
					if(!eventClasses.contains(eventClass)){
						eventClasses.add(eventClass);
						cont++;
					}
					
				}
			}
			
		}
		return eventClasses;
		
	}

	private Marking getFinalMarking(PetrinetImpl petriNet) {
		Collection<Place> places=petriNet.getPlaces();
		Collection<Place> endMarkingPlaces= new ArrayList<Place>();
		for(Place place: places){
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outputEdges=petriNet.getOutEdges(place);
			if(outputEdges==null || outputEdges.size()==0){
				endMarkingPlaces.add(place);
				break;
			}
		}
		Marking finMarking= new Marking(endMarkingPlaces);
		return finMarking;
	}

	private Marking getInitialMarking(PetrinetImpl petriNet) {
		Collection<Place> places=petriNet.getPlaces();
		Collection<Place> initMarkingPlaces= new ArrayList<Place>();
		for(Place place: places){
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inputEdges=petriNet.getInEdges(place);
			if(inputEdges==null || inputEdges.size()==0){
				initMarkingPlaces.add(place);
				break;
			}
		}
		Marking initMarking= new Marking(initMarkingPlaces);
		return initMarking;
	}
	
	public void exportToFile( PNRepResult repResult, File file, PetrinetImpl petriNet) throws IOException {
	System.gc();
	nf.setMinimumFractionDigits(2);
	nf.setMaximumFractionDigits(2);


	// export  to file
	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
	bw.write(createStringRep(repResult, petriNet));
	bw.close();

}

public String createStringRep(PNRepResult repResult, PetrinetGraph net) {
	String newLineChar = "\n";
	char commaSeparator = ',';
	XConceptExtension ce = XConceptExtension.instance();

	StringBuilder sb = new StringBuilder();
	sb.append("Result of replaying ");
	sb.append(net.getLabel());
	sb.append(" on ");
	sb.append(ce.extractName(log));
	sb.append(newLineChar);
	sb.append(newLineChar);

	sb.append("Index" + commaSeparator + "Case IDs" + commaSeparator + "NumOfCases" + commaSeparator + "IsReliable"
			+ commaSeparator);

	Iterator<SyncReplayResult> it = repResult.iterator();
	SyncReplayResult firstReplayResult = it.next();

	if (firstReplayResult != null) {
		// information for later
		Map<String, Double[]> calculations = new HashMap<String, Double[]>();

		// get all information exist in replay result
		Map<String, Double> infos = firstReplayResult.getInfo();
		Set<String> infoKeys = infos.keySet();
		String[] infoKeysArr = infoKeys.toArray(new String[infoKeys.size()]);
		for (String info : infoKeysArr) {
			sb.append("\"");
			sb.append(info);
			sb.append("\"");
			sb.append(commaSeparator);
		}

		sb.append("Match");
		sb.append(newLineChar);

		// now, create table content
		int indexCounter = 1;

		sb.append(indexCounter);
		sb.append(commaSeparator);

		String separatorStr = "";
		sb.append("\"");
		for (Integer index : firstReplayResult.getTraceIndex()) {
			sb.append(separatorStr);
			sb.append(ce.extractName(log.get(index)));
			separatorStr = "|";
		}
		sb.append("\"");

		sb.append(commaSeparator);

		sb.append(firstReplayResult.getTraceIndex().size());
		sb.append(commaSeparator);

		sb.append(firstReplayResult.isReliable() ? "Yes" : "No");
		sb.append(commaSeparator);

		for (String infoKey : infoKeysArr) {
			Double val = infos.get(infoKey);
			sb.append("\"");
			sb.append(nf.format(val));
			sb.append("\"");
			updateCalculations(firstReplayResult, infoKey, calculations, firstReplayResult.getTraceIndex().size(),
					val);
			sb.append(commaSeparator);
		}

		sb.append(generateMatchString(firstReplayResult));
		sb.append(newLineChar);

		indexCounter++;

		// add stats for first result
		numCaseInvolved += firstReplayResult.getTraceIndex().size();
		if (firstReplayResult.isReliable()) {
			numReliableCaseInvolved += numCaseInvolved;
		}

		while (it.hasNext()) {
			SyncReplayResult syncRepResult = it.next();
			int caseIDSize = syncRepResult.getTraceIndex().size();

			sb.append(indexCounter);
			sb.append(commaSeparator);

			separatorStr = "";
			sb.append("\"");
			for (Integer index : syncRepResult.getTraceIndex()) {
				sb.append(separatorStr);
				sb.append(ce.extractName(log.get(index)));
				separatorStr = "|";
			}
			sb.append("\"");
			sb.append(commaSeparator);

			sb.append(caseIDSize);
			sb.append(commaSeparator);

			sb.append(syncRepResult.isReliable() ? "Yes" : "No");
			sb.append(commaSeparator);

			Map<String, Double> allInfo = syncRepResult.getInfo();
			for (String infoKey : infoKeysArr) {
				Double val = allInfo.get(infoKey);
				sb.append("\"");
				sb.append(nf.format(val));
				sb.append("\"");
				updateCalculations(syncRepResult, infoKey, calculations, caseIDSize, val);
				sb.append(commaSeparator);
			}

			sb.append(generateMatchString(syncRepResult));
			sb.append(newLineChar);

			indexCounter++;

			// add stats
			numCaseInvolved += caseIDSize;
			if (syncRepResult.isReliable()) {
				numReliableCaseInvolved += caseIDSize;
			}

		}

		sb.append(newLineChar);
		sb.append(newLineChar);

		sb.append("ONLY RELIABLE RESULTS");

		sb.append(newLineChar);
		sb.append(newLineChar);

		// update based on calculations
		sb.append("Property");
		sb.append(commaSeparator);
		sb.append("Value");
		sb.append(newLineChar);

		sb.append("#Reliable cases replayed");
		sb.append(commaSeparator);
		sb.append(numReliableCaseInvolved);
		sb.append(newLineChar);

		sb.append("#Synchronous ev.class (log+model)");
		sb.append(commaSeparator);
		sb.append(numReliableSynchronized);
		sb.append(newLineChar);

		sb.append("#Skipped ev.class");
		sb.append(commaSeparator);
		sb.append(numReliableModelOnlyReal);
		sb.append(newLineChar);

		sb.append("#Unobservable ev.class");
		sb.append(commaSeparator);
		sb.append(numReliableModelOnlyInvi);
		sb.append(newLineChar);

		sb.append("#Inserted ev.class");
		sb.append(commaSeparator);
		sb.append(numReliableLogOnly);
		sb.append(newLineChar);
		
		if (numSwapViolations > 0){
			sb.append("#Swapped Violations");
			sb.append(commaSeparator);
			sb.append(Integer.valueOf(numSwapViolations));
			sb.append(newLineChar);
		}
		
		if (numReplaceViolations > 0){
			sb.append("#Replacements Violations");
			sb.append(commaSeparator);
			sb.append(Integer.valueOf(numReplaceViolations));
			sb.append(newLineChar);
		}

		Map<String, Object> info = repResult.getInfo();
		if (info != null) {
			for (String key : info.keySet()) {
				sb.append(key);
				sb.append(commaSeparator);
				sb.append(info.get(key));
				sb.append(newLineChar);
			}
		}

		sb.append(newLineChar);
		sb.append(newLineChar);

		// update based on calculations
		sb.append("Property");
		sb.append(commaSeparator);
		sb.append("Average");
		sb.append(commaSeparator);
		sb.append("Minimum");
		sb.append(commaSeparator);
		sb.append("Maximum");
		sb.append(commaSeparator);
		sb.append("Std. Deviation");
		sb.append(commaSeparator);
		sb.append("Number of cases with value 1.00");
		sb.append(newLineChar);

		// property based, only reliable results
		for (String property : calculations.keySet()) {
			Double[] values = calculations.get(property);
			sb.append("\"");
			sb.append(property);
			sb.append("\"");
			sb.append(commaSeparator);

			sb.append("\"");
			sb.append(numReliableCaseInvolved == 0 ? "<NaN>" : nf.format(values[MVALRELIABLE]));
			sb.append("\"");
			sb.append(commaSeparator);

			sb.append("\"");
			sb.append(numReliableCaseInvolved == 0 ? "<NaN>" : nf.format(values[RELIABLEMIN]));
			sb.append("\"");
			sb.append(commaSeparator);

			sb.append("\"");
			sb.append(numReliableCaseInvolved == 0 ? "<NaN>" : nf.format(values[RELIABLEMAX]));
			sb.append("\"");
			sb.append(commaSeparator);

			double reliabledev = numReliableCaseInvolved == 0 ? 0 : Math.sqrt(values[SVALRELIABLE]
					/ (numReliableCaseInvolved - 1));
			sb.append("\"");
			sb.append(Double.compare(reliabledev, Double.NaN) == 0 ? "<NaN>" : nf.format(reliabledev));
			sb.append("\"");
			sb.append(commaSeparator);

			sb.append("\"");
			sb.append(nf.format(values[PERFECTCASERELIABLECOUNTER]));
			sb.append("\"");
			sb.append(newLineChar);
		}

	}
	return sb.toString();
}

private void updateCalculations(SyncReplayResult res, String property, Map<String, Double[]> calculations,
		int caseIDSize, double value) {
	// use it to calculate property
	Double[] oldValues = calculations.get(property);
	if (oldValues == null) {
		oldValues = new Double[] { Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, 0.00000,
				0.00000, 0.00000, 0.00000, 0.00000 };
		calculations.put(property, oldValues);
	}

	if (Double.compare(oldValues[MIN], value) > 0) {
		oldValues[MIN] = value;
	}
	if (Double.compare(oldValues[MAX], value) < 0) {
		oldValues[MAX] = value;
	}

	int counterCaseIDSize = 0;
	if (numCaseInvolved == 0) {
		oldValues[MVAL] = value;
		oldValues[SVAL] = 0.0000;
		counterCaseIDSize++;
	}
	for (int i = counterCaseIDSize; i < caseIDSize; i++) {
		double oldMVal = oldValues[MVAL];
		// AA: previous average calculation is wrong. Fix this
		// oldValues[MVAL] += ((value - oldValues[MVAL]) / (i + numCaseInvolved));
		oldValues[MVAL] += ((value - oldValues[MVAL]) / (i + numCaseInvolved + 1));
		oldValues[SVAL] += ((value - oldMVal) * (value - oldValues[MVAL]));
	}

	if (res.isReliable()) {
		if (Double.compare(oldValues[RELIABLEMIN], value) > 0) {
			oldValues[RELIABLEMIN] = value;
		}
		if (Double.compare(oldValues[RELIABLEMAX], value) < 0) {
			oldValues[RELIABLEMAX] = value;
		}

		counterCaseIDSize = 0;
		if (numReliableCaseInvolved == 0) {
			oldValues[MVALRELIABLE] = value;
			oldValues[SVALRELIABLE] = 0.0000;
			counterCaseIDSize++;
		}
		for (int i = counterCaseIDSize; i < caseIDSize; i++) {
			double oldMVal = oldValues[MVALRELIABLE];
			// AA: previous average calculation is wrong. Fix this
			// oldValues[MVALRELIABLE] += ((value - oldValues[MVALRELIABLE]) / (i + numReliableCaseInvolved));
			oldValues[MVALRELIABLE] += ((value - oldValues[MVALRELIABLE]) / (i + numReliableCaseInvolved + 1));
			oldValues[SVALRELIABLE] += ((value - oldMVal) * (value - oldValues[MVALRELIABLE]));
		}

		if (Double.compare(value, 1.0000000) == 0) {
			oldValues[PERFECTCASERELIABLECOUNTER] += caseIDSize;
		}
	}
}

private Object generateMatchString(SyncReplayResult res) {
	StringBuilder sb = new StringBuilder();
	String limiter = "";
	java.util.List<Object> nodeInstance = res.getNodeInstance();

	int counter = 0;
	int size = res.getTraceIndex().size();
	for (StepTypes stepType : res.getStepTypes()) {
		sb.append(limiter);
		switch (stepType) {
			case L :
				sb.append("[L]");
				sb.append(nodeInstance.get(counter));
				if (res.isReliable()) {
					numReliableLogOnly += size;
				}
				;
				break;
			case MINVI :
				sb.append("[M-INVI]");
				sb.append(((Transition) nodeInstance.get(counter)).getLabel());
				if (res.isReliable()) {
					numReliableModelOnlyInvi += size;
				}
				;
				break;
			case MREAL :
				sb.append("[M-REAL]");
				sb.append(((Transition) nodeInstance.get(counter)).getLabel());
				if (res.isReliable()) {
					numReliableModelOnlyReal += size;
				}
				;
				break;
			case LMNOGOOD :
				sb.append("[L/M-NOGOOD]");
				sb.append(((Transition) nodeInstance.get(counter)).getLabel());
				if (res.isReliable()) {
					numReliableViolations += size;
				}
				;
				break;
			case LMREPLACED :
				sb.append("[L/M-REPLACED]");
				ViolatingSyncMove violation = (ViolatingSyncMove) nodeInstance.get(counter);
				sb.append("Trans:" + violation.getTransition() + "-->" + violation.getEventClass().getId());
				if (res.isReliable()) {
					numReplaceViolations += size;
				}
				break;
			case LMSWAPPED :
				sb.append("[L/M-SWAPPED]");
				SwappedMove swapMove = (SwappedMove) nodeInstance.get(counter);
				sb.append("Trans:" + swapMove.getOccurTrans().getLabel() + " insteadOf " + swapMove.getInsteadOf().getLabel());
				if (res.isReliable()) {
					numSwapViolations += size;
				}
				break;
			default :
				sb.append("[L/M]");
				sb.append(((Transition) nodeInstance.get(counter)).getLabel());
				if (res.isReliable()) {
					numReliableSynchronized += size;
				}
				;
		}
		limiter = "|";
		counter++;
	}
	return sb.toString();
}

	public XLog getLog() {
		return log;
	}
	public void setLog(XLog log) {
		this.log = log;
	}
	public String getOutputFolder() {
		return outputFolder;
	}
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	

	public String getPetriNetPath() {
		// TODO Auto-generated method stub
		return this.petriNetPath;
		
	}
	
	
	public PNRepResult getPNRep(){
		return this.replayerRes;
	}
	
	private static Object[] connectNet(UIPluginContext context, Pnml pnml,
			PetrinetGraph net) {
		
		/*
		 * Create fresh marking(s) and layout.
		 */
		Marking marking = new Marking();
		Collection<Marking> finalMarkings = new HashSet<Marking>();
		GraphLayoutConnection layout = new GraphLayoutConnection(net);

		/*
		 * Initialize the Petri net, marking(s), and layout from the PNML
		 * element.
		 */
		pnml.convertToNet(net, marking, layout);

		/*
		 * Add a connection from the Petri net to the marking(s) and layout.
		 */
		context.addConnection(new InitialMarkingConnection(net, marking));
		for (Marking finalMarking : finalMarkings) {
			context.addConnection(new FinalMarkingConnection(net, finalMarking));
		}
		context.addConnection(layout);

		
		Object[] objects = new Object[2];
		objects[0] = net;
		objects[1] = marking;
		return objects;
	}
	
	public void createFile(File file) throws IOException {
		System.gc();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);


		// export  to file
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bw.write(createStringRep2());
		bw.close();

	}
	
	public String createStringRep2() {
		String newLineChar = "\n";
		char commaSeparator = ',';
		XConceptExtension ce = XConceptExtension.instance();

		StringBuilder sb = new StringBuilder();
		sb.append("Result of replaying ");
		sb.append(newLineChar);
		sb.append(newLineChar);

		sb.append("Index" + commaSeparator + "Case IDs" + commaSeparator + "NumOfCases" + commaSeparator + "IsReliable"
				+ commaSeparator + "Match");
		
		sb.append(newLineChar);
		
		ArrayList<Integer> tracedone = new ArrayList<Integer>();
		int indexCounter = 1;
		for (XTrace trace : this.log) {
			// now, create table content
			Integer id = Integer.parseInt(trace.getAttributes().get("concept:name").toString());
			
			if (tracedone.contains(id)) {}
			else {
				ArrayList<String> caseIds = new ArrayList<String>();
				ArrayList<String> match = new ArrayList<String>();
				
				for (XEvent event : trace){
					match.add(event.getAttributes().get("concept:name").toString());
					}
				
					for (XTrace ids : this.log) {
						ArrayList<String> string = new ArrayList<String>();
						for (XEvent event : ids){
							string.add(event.getAttributes().get("concept:name").toString());
							}
						if (string.equals(match)) {
							String caseid = ids.getAttributes().get("concept:name").toString();
							caseIds.add(caseid);
							tracedone.add(Integer.parseInt(caseid));
						}
					}
	
					sb.append(indexCounter);
					sb.append(commaSeparator);
		
					String separatorStr = "";
					sb.append("\"");
					for (Integer index = 0; index<caseIds.size(); index++) {
						sb.append(separatorStr);
						sb.append(caseIds.get(index));
						separatorStr = "|";
					}
					sb.append("\"");
		
					sb.append(commaSeparator);
		
					sb.append(caseIds.size());
					sb.append(commaSeparator);
		
					sb.append("Yes");
					sb.append(commaSeparator);
					
					String conformance = "[L/M]";
					for (Integer index = 0; index<match.size(); index++) {
						
						sb.append(conformance);
						sb.append(separatorStr);
						sb.append(match.get(index));
						separatorStr = "|";
						
					}
		
					sb.append(newLineChar);
		
					indexCounter++;
				}
	
			}

			sb.append(newLineChar);
			sb.append(newLineChar);

			sb.append("ONLY RELIABLE RESULTS");

			sb.append(newLineChar);
			sb.append(newLineChar);

			// update based on calculations
			sb.append("Property");
			sb.append(commaSeparator);
			sb.append("Value");
			sb.append(newLineChar);

			sb.append(newLineChar);
			sb.append(newLineChar);

			// update based on calculations
			sb.append("Property");
			sb.append(commaSeparator);
			sb.append("Average");
			sb.append(commaSeparator);
			sb.append("Minimum");
			sb.append(commaSeparator);
			sb.append("Maximum");
			sb.append(commaSeparator);
			sb.append("Std. Deviation");
			sb.append(commaSeparator);
			sb.append("Number of cases with value 1.00");
			sb.append(newLineChar);

		
		return sb.toString();
	}
	
	
}
