package optimization;

import ilog.concert.IloIntVar;

public class Trainingsfactor {
	private SpielerTrainingsteilnahme teilnahme;
	private IloTrainingstag trainingstag;
	private IloIntVar iloBoolVar;
	
	public Trainingsfactor(SpielerTrainingsteilnahme teilnahme, IloTrainingstag trainingstag, IloIntVar iloBoolVar) {
		this.teilnahme = teilnahme;
		this.trainingstag = trainingstag;
		this.iloBoolVar = iloBoolVar;
	}

	public SpielerTrainingsteilnahme getTeilnahme() {
		return teilnahme;
	}

	public void setTeilnahme(SpielerTrainingsteilnahme teilnahme) {
		this.teilnahme = teilnahme;
	}

	public IloTrainingstag getTrainingstag() {
		return trainingstag;
	}

	public void setTrainingstag(IloTrainingstag trainingstag) {
		this.trainingstag = trainingstag;
	}

	public IloIntVar getIloBoolVar() {
		return iloBoolVar;
	}

	public void setIloBoolVar(IloIntVar iloBoolVar) {
		this.iloBoolVar = iloBoolVar;
	}
	
	
}
