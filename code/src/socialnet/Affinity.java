package socialnet;

public class Affinity {
	
	private String resource;						//nome della risorsa da sostituire
	private String candidate;						//nome dell risorsa candidata a sostituirlo
	
	private int commonAct;							//il numero di attività in comune
	private int totalAct;							//il numero di attività totali compiute da @resource
	
	private int commonRes;							//il numero di risorse (nel grafo) in comune
	private int totalRes;							//il n. totale di risorse con cui @resource ha collaborato
	
	private int expAct;								//il numero di attività in comune che sono state svolte da @candidate le stesse o il maggior numero di volte di @resource
	private int timeAct;							//il numero di attività in comune che sono state svolte da @candidate con tempi medi minori o uguali di @resource
	
	private float activityFactor;					//rappresenta il rapporto fra @commonAct e @totalAct
	private float collaborationFactor;				//rappresenta il rapporto fra @commonRes e @totalRes
	private float experience;						//rappresenta il rapporto fra @expAct e @commonAct
	private float speed;							//rappresenta il rapporto fra @timeAct e @commonAct
	
	private float[] weight;							//array dei pesi
	
	private float totalScore;						//punteggio totale ottenuto dalla somma dei fattori precedenti opportunamente pesati, in rapporto col massimo punteggio


	public Affinity(String resource, String candidate, int commonAct, int totalAct, int commonRes, int totalRes,
			int espAct, int timeAct, float[] weight) {
		super();
		this.resource = resource;
		this.candidate = candidate;
		this.commonAct = commonAct;
		this.totalAct = totalAct;
		this.commonRes = commonRes;
		this.totalRes = totalRes;
		this.expAct = espAct;
		this.timeAct = timeAct;
		this.weight = weight;
		calculate();
	}
	
	private void calculate() {
		
		if (this.totalAct != 0) {
			this.activityFactor = (float) this.commonAct / (float) this.totalAct;
			}
		else {
			System.out.println("Numero di attività totali svolte da " + this.resource + " uguale a 0");
			this.activityFactor = 0;
			}
		
		if (this.totalRes != 0) {
			this.collaborationFactor = (float) this.commonRes / (float) this.totalRes;
			}
		else {
			System.out.println("Numero di risorse totali con cui ha collaborato " + this.resource + " uguale a 0");
			this.collaborationFactor = 0;
			}
		
		if (this.commonAct != 0) {
			this.experience = (float) this.expAct / (float) this.commonAct;
			}
		else {
			System.out.println("Numero di attività in comune tra " + this.resource + " e " + this.candidate + " uguale a 0");
			this.experience = 0;
			}
		
		if (this.commonAct != 0) {
			this.speed = (float) this.timeAct / (float) this.commonAct;
			}
		else {
			System.out.println("Numero di attività in comune tra " + this.resource + " e " + this.candidate + " uguale a 0");
			this.speed = 0;
			}
		
		if (this.weight.length >= 4) {
			/*Il punteggio totale è compreso tra 0 e 1*/
			float max = this.weight[0] + this.weight[1] + this.weight[2] + this.weight[3];
			this.totalScore = this.activityFactor * this.weight[0] + this.collaborationFactor * this.weight[1] 
					+ this.experience * this.weight[2] + this.speed * this.weight[3];
			this.totalScore = this.totalScore / max;
			
		}
		else {
			System.out.println("Pesi non corretti");
			this.totalScore = 0;
			}
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getCandidate() {
		return candidate;
	}

	public void setCandidate(String candidate) {
		this.candidate = candidate;
	}

	public int getCommonAct() {
		return commonAct;
	}

	public void setCommonAct(int commonAct) {
		this.commonAct = commonAct;
	}

	public int getTotalAct() {
		return totalAct;
	}

	public void setTotalAct(int totalAct) {
		this.totalAct = totalAct;
	}

	public int getCommonRes() {
		return commonRes;
	}

	public void setCommonRes(int commonRes) {
		this.commonRes = commonRes;
	}

	public int getTotalRes() {
		return totalRes;
	}

	public void setTotalRes(int totalRes) {
		this.totalRes = totalRes;
	}

	public int getEspAct() {
		return expAct;
	}

	public void setEspAct(int espAct) {
		this.expAct = espAct;
	}

	public int getTimeAct() {
		return timeAct;
	}

	public void setTimeAct(int timeAct) {
		this.timeAct = timeAct;
	}

	public float getActivityFactor() {
		return activityFactor;
	}

	public void setActivityFactor(float activityFactor) {
		this.activityFactor = activityFactor;
	}

	public float getCollaborationFactor() {
		return collaborationFactor;
	}

	public void setCollaborationFactor(float collaborationFactor) {
		this.collaborationFactor = collaborationFactor;
	}

	public float getEsperience() {
		return experience;
	}

	public void setEsperience(float esperience) {
		this.experience = esperience;
	}

	public float getAbility() {
		return speed;
	}

	public void setAbility(float ability) {
		this.speed = ability;
	}

	public float[] getWeight() {
		return weight;
	}

	public void setWeight(float[] weight) {
		this.weight = weight;
	}

	public float getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(float totalScore) {
		this.totalScore = totalScore;
	}

	public int getExpAct() {
		return expAct;
	}

	public void setExpAct(int expAct) {
		this.expAct = expAct;
	}

	public float getExperience() {
		return experience;
	}

	public void setExperience(float experience) {
		this.experience = experience;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	

}
