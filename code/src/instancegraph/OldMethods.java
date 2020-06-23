package instancegraph;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XEvent;

public class OldMethods {
	
	private DBManager dbManager;
	
	//old method, for previous check implementation.
	private String checkTrace(XEvent sourceEvent, int posEvent,
			List<XEvent> trace) {
		// this method checks if the trace implements the caus. rel. correctly
		String result="correct;none";
		boolean isSwapped=false;
		isSwapped=checkEventSwapping(sourceEvent,posEvent,trace);
		if(isSwapped)
			result="anomalous;swap";
		return result;
	}

	
	private boolean checkEventSwapping(XEvent sourceEvent, int posEvent,
			List<XEvent> trace) {
		// an event is swapped if it is the direct follower of an event which should be its target
		
		int pos=posEvent-2;
		boolean result=false;
		if(pos >= 0){
		String followerEventName = sourceEvent.getAttributes().get("concept:name").toString();
		String followerEventType = sourceEvent.getAttributes().get("lifecycle:transition")
				.toString();
		String followerEventClass = followerEventName+ "+"+ followerEventType;
		followerEventClass=followerEventClass.replace(" ", "");
		
		
		XEvent sourceCandidate= trace.get(pos);
		String sourceEventName = sourceCandidate.getAttributes().get("concept:name").toString();
		String sourceEventType = sourceCandidate.getAttributes().get("lifecycle:transition")
					.toString();
		String sourceEventClass = sourceEventName+"+"+sourceEventType;
		sourceEventClass=sourceEventClass.replace(" ", "");
		String queryCheck="SELECT source FROM causalrel WHERE source LIKE '"+followerEventClass+"' AND target LIKE '"+sourceEventClass+"'";
		ArrayList<String> causalrellist=dbManager.executeQuery(queryCheck, "source");
		if(causalrellist != null && causalrellist.size()>0){
			//Check for the 2 lengths loop
			String queryCheck2L="SELECT source FROM causalrel WHERE source LIKE '"+sourceEventClass+"' AND target LIKE '"+followerEventClass+"'";
			ArrayList<String> length2lrel=dbManager.executeQuery(queryCheck2L, "source");
			if(length2lrel==null || length2lrel.size() ==0)
				result=true;
		}
		}
		return result;
	}
}
