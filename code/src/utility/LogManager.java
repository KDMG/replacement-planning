package utility;

import java.util.Iterator;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

public class LogManager {
	private XLog xlog;

	public LogManager(XLog xlog) {
		super();
		this.xlog = xlog;
	} 
	
	public XLog changeEventName(String attribute){
		XLog newLog=xlog;
		Iterator<XTrace> it= newLog.iterator();
		while(it.hasNext()){
			XTrace trace= it.next();
			Iterator<XEvent> itEvent=trace.iterator();
			while(itEvent.hasNext()){
				XEvent event= itEvent.next();
				XAttributeMap attributesList=event.getAttributes();
				String eventName=attributesList.get("concept:name").toString();
				String eventAttr="";
				if(!eventName.equals("START") && !eventName.equals("END")){
					eventAttr= attributesList.get(attribute).toString();
//					if(eventGroup.contains("2nd"))
//						eventGroup="2nd";
//					else if(eventGroup.contains("3rd"))
//						eventGroup="3rd";
//					else
//						eventGroup="other";
				}
				attributesList.remove("concept:name");
				String newName= eventName+"+"+eventAttr;
				XAttributeLiteralImpl newAttribute= new XAttributeLiteralImpl("concept:name",newName);
				attributesList.put("concept:name",newAttribute);
				event.setAttributes(attributesList);
				
			}
		}
		return newLog;
	}
}
