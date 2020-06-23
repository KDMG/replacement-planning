package utility;

public class Path {
	
	private String eventLog;
	private String petriNet;
	private String replayResult;
	private String graphDirectory;
	
	public Path(String eventLog, String petriNet, String replayResult,
			String graphDirectory) {
		super();
		this.eventLog = eventLog;
		this.petriNet = petriNet;
		this.replayResult = replayResult;
		this.graphDirectory = graphDirectory;
	}

	public String getEventLog() {
		return eventLog;
	}

	public void setEventLog(String eventLog) {
		this.eventLog = eventLog;
	}

	public String getPetriNet() {
		return petriNet;
	}

	public void setPetriNet(String petriNet) {
		this.petriNet = petriNet;
	}

	public String getReplayResult() {
		return replayResult;
	}

	public void setReplayResult(String replayResult) {
		this.replayResult = replayResult;
	}

	public String getGraphDirectory() {
		return graphDirectory;
	}

	public void setGraphDirectory(String graphDirectory) {
		this.graphDirectory = graphDirectory;
	}
	
	
}
