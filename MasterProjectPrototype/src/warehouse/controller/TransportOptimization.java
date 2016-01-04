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
	private StringBuilder sb;
	private String problem;
	private String solution;
	
	public TransportOptimization(List<Transport> possibleTransports){
		this.possibleTransports = possibleTransports;
		this.problem = "";
		this.solution = "";
		this.sb = new StringBuilder();
	}
	
	public void addPossibleTransport(Transport t){
		this.possibleTransports.add(t);
	}
	public void clearPossibleTransports(){
		this.possibleTransports.clear();
	}
	
	@SuppressWarnings("finally")
	public boolean solve(){
		boolean isSolved = false;
		this.sb.setLength(0);
		try{ 
			IloCplex cplex = new IloCplex();
			sb.append("Optimization Model:\n");
			// decision Variables Xij
			// transport from warehouse i to customer j
			for(Transport t:this.possibleTransports){
				String vName = "X"+ t.getFrom().getName()+";"+t.getTo().getName();
				t.setIloIntVar(cplex.intVar(0, Integer.MAX_VALUE,vName));
				sb.append("Generate Decsision Variable: ").append(vName).append("\n");
			}
			// objective
			IloLinearIntExpr objective = cplex.linearIntExpr();
			for(Transport t:this.possibleTransports){
				// Transportkosten * Entscheidungsvariable für Anzahl an Lieferungen
				objective.addTerm(t.getCost(), t.getIloIntVar());
			}
			
			// Minimierungsproblem
			sb.append("\nGenerate Objective:\n").append(cplex.addMinimize(objective)).append("\n\n");
		
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
			sb.append("Warehouse-Constraints (Maximale Anzahl möglicher Lieferungen darf nicht überschritten werden):\n");
			for(Map.Entry<Warehouse, IloLinearIntExpr> entry : wh2Ilo.entrySet()){
				sb.append(cplex.addLe(entry.getValue(), entry.getKey().getMaxSupplies())).append("\n");
			}
			// Constraints: Mindestens den Kundenbedarf decken
			sb.append("\nCustomer-Constraints (Mindestens den Kundenbedarf decken):\n");
			for(Map.Entry<Customer, IloLinearIntExpr> entry : c2Ilo.entrySet()){
				sb.append(cplex.addGe(entry.getValue(), entry.getKey().getDemand())).append("\n");
			}
			
			//cplex.setParam(IloCplex.IntParam.SimDisplay, 0);
			this.problem = sb.toString();
			sb.setLength(0);
			// solve
			sb.append("Solution:\n");
			if(cplex.solve()){
				for(Transport t : this.possibleTransports){
					double value = cplex.getValue(t.getIloIntVar());
					if(value>0)
						sb.append(t.getFrom().getName() + " -" + value + "-> " + t.getTo().getName()).append("\n");
				}
				sb.append("Kosten (objective) = " + cplex.getObjValue());
				IloObjective ff = cplex.getObjective();
				this.solution = sb.toString();
				System.out.println(ff.getExpr());
				isSolved = true;
			}
			else{
				sb.append("Model not solved");
				this.solution = sb.toString();
			}
			cplex.end();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
			sb.setLength(0);
			return isSolved;
		}
		
	}

	public String getProblem() {
		return problem;
	}

	public String getSolution() {
		return solution;
	}
	
	
	
}
