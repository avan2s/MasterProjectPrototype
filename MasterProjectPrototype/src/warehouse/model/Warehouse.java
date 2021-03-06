package warehouse.model;

import java.util.ArrayList;
import java.util.List;

public class Warehouse {
	
	private String name;
	private int maxSupplies; // Maximale m�gliche Lieferungen
	private List<Customer> customers;
	private List<Transport> transports;
	
	public Warehouse(String name, int maxSupplies){
		this.customers = new ArrayList<Customer>();
		this.transports = new ArrayList<Transport>();
		this.maxSupplies = maxSupplies;
		this.name = name;
	}
	
	public void addTransport(Transport t){
		if(!transports.contains(t))
			this.transports.add(t);
		if(!this.customers.contains(t.getTo()))
			this.customers.add(t.getTo());
	}
	
	public void removeTransport(Transport t){
		Customer c = t.getTo();
		boolean removeCustomer = true;
		if(transports.contains(t))
			transports.remove(t);
		for(Transport tD : this.transports){
			if(tD.getTo().equals(c)){
				removeCustomer = false;
				break;
			}
		}
		if(removeCustomer)
			this.customers.remove(c);
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public int getMaxSupplies() {
		return maxSupplies;
	}

	public void setMaxSupplies(int maxSupplies) {
		this.maxSupplies = maxSupplies;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public List<Transport> getTransports() {
		return transports;
	}
	
}
