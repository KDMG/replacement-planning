package instancegraph;

public class Anomaly {
	private String type;
	private String eventClass;
	private int posAn;
	private String eventClassFin;
	private int posAnFin;
	public Anomaly(String type, String eventClass, int posAn) {
		super();
		this.type = type;
		this.eventClass = eventClass;
		this.posAn=posAn;
		this.eventClassFin=this.eventClass;
		this.posAnFin=this.posAn;
	}
	
	public int getPosAnFin() {
		return posAnFin;
	}

	public void setPosAnFin(int posAnFin) {
		this.posAnFin = posAnFin;
	}

	public String getEventClassFin() {
		return eventClassFin;
	}

	public void setEventClassFin(String eventClassFin) {
		this.eventClassFin = eventClassFin;
	}

	public int getPosAn() {
		return posAn;
	}

	public void setPosAn(int posAn) {
		this.posAn = posAn;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEventClass() {
		return eventClass;
	}
	public void setEventClass(String eventClass) {
		this.eventClass = eventClass;
	}
	
	
}
