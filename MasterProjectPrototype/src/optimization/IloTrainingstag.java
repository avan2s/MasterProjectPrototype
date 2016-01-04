package optimization;

import ilog.concert.IloIntVar;

public class IloTrainingstag {
	private int tag;
	private String einheit;
	private IloIntVar iloBoolVar; 

	public IloTrainingstag(int tag, String einheit, IloIntVar iloBoolVar) {
		super();
		this.tag = tag;
		this.einheit = einheit;
		this.iloBoolVar = iloBoolVar;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public String getEinheit() {
		return einheit;
	}

	public void setEinheit(String einheit) {
		this.einheit = einheit;
	}

	public IloIntVar getIloBoolVar() {
		return iloBoolVar;
	}

	public void setIloBoolVar(IloIntVar iloBoolVar) {
		this.iloBoolVar = iloBoolVar;
	}

}
