package socialnet;

import java.util.Date;

public class Handover {
	
	private String FirstActivity;
	private String SecondActivity;
	private Float handoverOfWork;
	private Integer occurrences;
	private String time;
	
	public Handover(String a, String b, Float handoverOfWork, Integer occurrences, String time) {
		super();
		FirstActivity = a;
		SecondActivity = b;
		this.handoverOfWork = handoverOfWork;
		this.occurrences = occurrences;
		this.time = time;
	}
	
	public String getTime() {
		return time;
	}



	public void setTime(String time) {
		this.time = time;
	}



	public Integer getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(Integer occurrences) {
		this.occurrences = occurrences;
	}

	public String getFirstActivity() {
		return FirstActivity;
	}

	public void setFirstActivity(String a) {
		FirstActivity = a;
	}

	public String getSecondActivity() {
		return SecondActivity;
	}

	public void setSecondActivity(String b) {
		SecondActivity = b;
	}

	public Float getHandoverOfWork() {
		return handoverOfWork;
	}

	public void setHandoverOfWork(Float handoverOfWork) {
		this.handoverOfWork = handoverOfWork;
	}

}
