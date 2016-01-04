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
		this.transports = new ArrayList<>();
		this.name = name;
		this.demand = demand;
	}
	
	public void addTransport(Transport t){
		if(!this.transports.contains(t))
			this.transports.add(t);
		if(!this.warehouses.contains(t.getFrom()))
			this.warehouses.add(t.getFrom());
	}
	
	public void removeTransport(Transport t){
		Warehouse w = t.getFrom();
		boolean removeWarehouse = true;
		if(this.transports.contains(t))
			this.transports.remove(t);
		for(Transport tD : this.transports){
			if(tD.getFrom().equals(w)){
				removeWarehouse = false;
				break;
			}
		}
		if(removeWarehouse)
			this.warehouses.remove(w);
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
