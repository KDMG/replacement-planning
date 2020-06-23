package utility;

public class Event implements Comparable<Event> {
	private String pos;
	private String eventclass;
	private String posWithAlignments;
	public String getPosWithAlignments() {
		return posWithAlignments;
	}
	public void setPosWithAlignments(String posWithAlignments) {
		this.posWithAlignments = posWithAlignments;
	}
	public Event(String pos, String eventclass) {
		super();
		this.pos = pos;
		this.eventclass = eventclass;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getEventclass() {
		return eventclass;
	}
	public void setEventclass(String eventclass) {
		this.eventclass = eventclass;
	}
	@Override
	public int compareTo(Event o) {
		if(Integer.parseInt(this.getPos())<Integer.parseInt(o.getPos()))
			return -1;
		else if(Integer.parseInt(this.getPos())==Integer.parseInt(o.getPos()))
			return 0;
		else
			return 1;
	}
	
}
