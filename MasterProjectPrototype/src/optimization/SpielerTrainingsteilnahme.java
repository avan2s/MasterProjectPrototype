package optimization;

import ilog.concert.IloIntVar;

public class SpielerTrainingsteilnahme {
	private Spieler spieler;
	private IloIntVar iloBoolVar;
	
	public SpielerTrainingsteilnahme(Spieler spieler, IloIntVar iloBoolVar) {
		this.spieler = spieler;
		this.iloBoolVar = iloBoolVar;
	}
	public Spieler getSpieler() {
		return spieler;
	}
	public void setSpieler(Spieler spieler) {
		this.spieler = spieler;
	}
	public IloIntVar getIloBoolVar() {
		return iloBoolVar;
	}
	public void setIloBoolVar(IloIntVar iloBoolVar) {
		this.iloBoolVar = iloBoolVar;
	}
	
	
}
