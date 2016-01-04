package warehouse.model;

import ilog.concert.IloIntVar;

public class Transport {
	private Warehouse from;
	private Customer to;
	private int cost;
	private IloIntVar iloIntVar; // Liefermenge
	
	public Transport(Warehouse from, Customer to, int cost) {
		this.from = from;
		this.to = to;
		this.cost = cost;
		this.from.addTransport(this);
		this.to.addTransport(this);
	}

	public Warehouse getFrom() {
		return from;
	}

	public void setFrom(Warehouse from) {
		this.from = from;
	}

	public Customer getTo() {
		return to;
	}

	public void setTo(Customer to) {
		this.to = to;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public IloIntVar getIloIntVar() {
		return iloIntVar;
	}

	public void setIloIntVar(IloIntVar iloIntVar) {
		this.iloIntVar = iloIntVar;
	}
	
}
