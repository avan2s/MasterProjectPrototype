package warehouse.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
	private String name;
	private int demand;
	private List<Warehouse> warehouses;
	private List<Transport> transports;
	
	public Customer(String name, int demand){
		this.warehouses = new ArrayList<Warehouse>();
		this.name = name;
		this.demand = demand;
	}
	
	public void addTransport(Transport t){
		this.transports.add(t);
		if(!this.warehouses.contains(t.getFrom()))
			this.warehouses.add(t.getFrom());
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public int getDemand(){
		return this.demand;
	}
	
	public void setDemand(int demand){
		this.demand = demand;
	}

	public List<Warehouse> getWarehouses() {
		return warehouses;
	}

	public List<Transport> getTransports() {
		return transports;
	}	
	
}
