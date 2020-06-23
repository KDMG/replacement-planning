package utility;

public class PetriArc {
	private String id;
	private String from;
	private String to;
	private boolean visited;
	public PetriArc(String id, String from, String to, boolean visited) {
		super();
		this.id = id;
		this.from = from;
		this.to = to;
		this.visited=visited;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
}
