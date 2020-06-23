package LogGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Place implements Comparable {
	private ArrayList<String> followers;
	private HashMap<String,Double> followerProbability;
	private int id;
	private int path;

	public Place(ArrayList<String> followers, int id, String prob) {
		super();
		this.followers = followers;
		this.id = id;
		setFollowersProbability(prob);
	}
	
	public int getPath() {
		return path;
	}

	public void setPath(int path) {
		this.path = path;
	}


	private void setFollowersProbability(String type) {
		followerProbability= new HashMap<String,Double>();
		double numFoll=followers.size();
		switch(type){
		case "uniform":
			for(String follower: followers){
				double prob= 1/ numFoll;
				followerProbability.put(follower,prob);
			}
			break;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<String> getFollowers() {
		return followers;
	}

	public void setFollowers(ArrayList<String> followers) {
		this.followers = followers;
	}
	
	public String picksAFollower(){
		String result="";
		Random rand = new Random();
		int pos=rand.nextInt(followers.size());
		result= followers.get(pos);
		return result;
	}

	@Override
	public int compareTo(Object arg0) {
		Place competitor= (Place) arg0;
		if(this.id > competitor.id)
			return 1;
		else
			return -1;
	}
}
