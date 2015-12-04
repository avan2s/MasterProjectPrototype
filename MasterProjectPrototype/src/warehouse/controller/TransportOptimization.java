package warehouse.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;
import warehouse.model.Customer;
import warehouse.model.Transport;
import warehouse.model.Warehouse;

public class TransportOptimization {
	private List<Transport> possibleTransports;
	
	public TransportOptimization(List<Transport> possibleTransports){
		this.possibleTransports = possibleTransports;
	}
	
	public void addPossibleTransport(Transport t){
		this.possibleTransports.add(t);
	}
	public void clearPossibleTransports(){
		this.possibleTransports.clear();
	}
	
	public void solve(){
		try{ 
			IloCplex cplex = new IloCplex();
			
			// decision Variables Xij
			// transport from warehouse i to customer j
			for(Transport t:this.possibleTransports){
				String vName = "X"+ t.getFrom().getName()+";"+t.getTo().getName();
				t.setIloIntVar(cplex.intVar(0, Integer.MAX_VALUE,vName));
			}
			// objective
			IloLinearIntExpr objective = cplex.linearIntExpr();
			for(Transport t:this.possibleTransports){
				// Transportkosten * Entscheidungsvariable für Anzahl an Lieferungen
				objective.addTerm(t.getCost(), t.getIloIntVar());
			}
			
			// Minimierungsproblem
			cplex.addMinimize(objective);
		
			// constraints
			
			// Constraints: Warehouses können nur begrenzt oft liefern
			HashMap<Warehouse,IloLinearIntExpr> wh2Ilo = new HashMap<Warehouse,IloLinearIntExpr>();
			for(Transport t : this.possibleTransports){
				// Warehouse w kann nur begrenzt oft liefern
				Warehouse w = t.getFrom();
				
				// Entscheidungsvariablen für mögliche Transporte für Warehouse w
				if(wh2Ilo.containsKey(w)){
					wh2Ilo.get(w).addTerm(1, t.getIloIntVar());
				}
				else{
					IloLinearIntExpr expr = cplex.linearIntExpr();
					expr.addTerm(1,t.getIloIntVar());
					wh2Ilo.put(w, expr);
				}
				//System.out.println("X w="+ t.getFrom().getName() +";c="+t.getTo().getName());
			}
			
			// Constraints: Customer - Anzahl Lieferungen müssen den Bedarf decken 
			HashMap<Customer,IloLinearIntExpr> c2Ilo = new HashMap<Customer,IloLinearIntExpr>();
			for(Transport t : this.possibleTransports){
				// Warehouse w kann nur begrenzt oft liefern
				Customer c = t.getTo();
				
				// Entscheidungsvariablen für mögliche Transporte für Warehouse w
				if(c2Ilo.containsKey(c)){
					c2Ilo.get(c).addTerm(1, t.getIloIntVar());
				}
				else{
					IloLinearIntExpr expr = cplex.linearIntExpr();
					expr.addTerm(1,t.getIloIntVar());
					c2Ilo.put(c,expr);
				}
				//System.out.println("X w="+ t.getFrom().getName() +";c="+t.getTo().getName());
			}
			// Constraints dem Modell hinzufügen
			// Constraints: Maximale Anzahl möglicher Lieferungen darf nicht überschritten werden
			for(Map.Entry<Warehouse, IloLinearIntExpr> entry : wh2Ilo.entrySet()){
				System.out.println(cplex.addLe(entry.getValue(), entry.getKey().getMaxSupplies()));
			}
			// Constraints: Mindestens den Kundenbedarf decken
			for(Map.Entry<Customer, IloLinearIntExpr> entry : c2Ilo.entrySet()){
				System.out.println(cplex.addGe(entry.getValue(), entry.getKey().getDemand()));
			}
			
			//cplex.setParam(IloCplex.IntParam.SimDisplay, 0);
			// solve
			if(cplex.solve()){
				int i=1;
				for(Transport t : this.possibleTransports){
					System.out.println("(" + i + ")" + t.getFrom().getName() + " -" + cplex.getValue(t.getIloIntVar()) + "-> " + t.getTo().getName());
					i++;
				}
				System.out.println("Kosten (objective) = " + cplex.getObjValue());
				System.out.println("Kosten (objective) = " + cplex.getBestObjValue());
				IloObjective ff = cplex.getObjective();
				
				System.out.println(ff.getExpr());
			}
			else{
				System.out.println("Model not solved");
			}
			cplex.end();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
}
