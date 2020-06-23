package utility;
// this class represents a trace for which some anomaly has been detected
public class AlignedTrace {
	private String traceId;
	private String alingment;
	public AlignedTrace(String traceId, String alingment) {
		super();
		this.traceId = traceId;
		this.alingment = alingment;
	}
	public String getTraceId() {
		return traceId;
	}
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
	public String getAlingment() {
		return alingment;
	}
	public void setAlingment(String alingment) {
		this.alingment = alingment;
	}
	
}
