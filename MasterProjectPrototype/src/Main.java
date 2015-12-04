import java.util.ArrayList;
import java.util.List;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import warehouse.controller.TransportOptimization;
import warehouse.model.Customer;
import warehouse.model.Transport;
import warehouse.model.Warehouse;

public class Main {

	public static void main(String[] args) {
		//Main.exampleFarmer();
		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		Warehouse w1 = new Warehouse("W1",70);
		Warehouse w2 = new Warehouse("W2",50);
		Warehouse w3 = new Warehouse("W3",50);
		Warehouse w4 = new Warehouse("W4",50);
		warehouses.add(w1);
		warehouses.add(w2);
		warehouses.add(w3);
		warehouses.add(w4);
		
		List<Customer> customers = new ArrayList<Customer>();
		Customer c1 = new Customer("C1",30);
		Customer c2 = new Customer("C2",30);
		Customer c3 = new Customer("C3",30);
		Customer c4 = new Customer("C4",30);
		Customer c5 = new Customer("C5",30);
		Customer c6 = new Customer("C6",30);
		customers.add(c1);
		customers.add(c2);
		customers.add(c3);
		customers.add(c4);
		customers.add(c5);
		customers.add(c6);
		
		List<Transport> possibleTransports = new ArrayList<Transport>();
		int costs[][] = {{15,35,25,20,30,40},{10,50,35,30,25,45},{20,55,40,20,25,35},{25,40,30,35,20,25}};
		System.out.println(costs[3][5]);
		int w_index = 0;
		int c_index = 0;
		for(Warehouse w : warehouses){
			c_index = 0;
			for(Customer c: customers){
				Transport t = new Transport(w, c, costs[w_index][c_index]);
				possibleTransports.add(t);
				c_index++;
			}
			w_index++;
		}
		
		TransportOptimization optimizer = new TransportOptimization(possibleTransports);
		optimizer.solve();
		System.out.println("Ende");
	}

	public static void exampleFarmer(){
		try{
			IloCplex cplex = new IloCplex();
			double preisHafer = 30;
			double preisGetreide = 40;
			double hourHafer = 1;
			double hourGetreide = 2;
			int maxAcker = 240;
			int maxStunden = 320;
			
			// variables
			//IloNumVar x = cplex.numVar(0,Double.MAX_VALUE,"Getreide"); // x >= 0;
			IloIntVar x = cplex.intVar(0, Integer.MAX_VALUE, "Getreide");
			//IloNumVar y = cplex.numVar(0,Double.MAX_VALUE,"Hafer"); // y >= 0;
			IloIntVar y = cplex.intVar(0, Integer.MAX_VALUE, "Hafer");
			
			// Objective-Funktion definieren
			IloLinearNumExpr objective = cplex.linearNumExpr();
			// 40x + 30y
			objective.addTerm(preisGetreide, x);
			objective.addTerm(preisHafer, y);
			IloObjective f1 = cplex.getObjective();
			
			// Minimierungsproblem
			cplex.addMaximize(objective);
			
			
			List<IloRange> constraints = new ArrayList<IloRange>();	
			
			// Constraints definieren
			// x + y <= maximale zu bepflanzende Acker
			IloLinearIntExpr cAcker = cplex.linearIntExpr();
			cAcker.addTerm(1, x);
			cAcker.addTerm(1, y);
			constraints.add(cplex.addLe(cAcker,maxAcker));
			System.out.println(constraints.get(0));
			
			// 2x + y <= maxStunden
			IloLinearNumExpr cHours = cplex.linearNumExpr();
			cHours.addTerm(hourGetreide,x);
			cHours.addTerm(hourHafer,y);
			constraints.add(cplex.addLe(cHours, maxStunden));
			
			//cplex.addLe(cplex.sum(cplex.prod(hourGetreide, x),cplex.prod(hourHafer, y)),maxStunden);
			
			// nur das Nötigste ausgeben
			cplex.setParam(IloCplex.IntParam.SimDisplay, 0);
			
			// Parameter, ab wann eine Zahl als Integer gilt. 
			// 2. Parameter Anzahl Kommastellen, erhöht aber stark die Laufzeit
			// Standard ist: 10 Nachkommastellen müssen eine 0 vorweisen
			//cplex.setParam(IloCplex.DoubleParam.EpInt, 0);
			
			// solve
			if(cplex.solve()){
				IloObjective ff = cplex.getObjective();
				System.out.println(ff.getExpr());
				System.out.println(ff.getName());
				System.out.println(ff);
				//System.out.println("Wahrscheinlicher Umsatz = " + cplex.getObjValue());
				//System.out.println("x (Getreide) = " + Math.round(cplex.getValue(x)));
				System.out.println("y (Hafer) = " + Math.round(cplex.getValue(y)));
				
			}
			else{
				System.out.println("Model not solved");
			}
			cplex.end();
		}
		catch(IloException ex){
			ex.printStackTrace();
		}
	}
	
	public static void model1(){
		try{
			IloCplex cplex = new IloCplex();
			
			// variables
			IloNumVar x = cplex.numVar(0,Double.MAX_VALUE,"x"); // x >= 0;
			IloNumVar y = cplex.numVar(0,Double.MAX_VALUE,"y"); // y >= 0;
			
			// Objective-Funktion definieren
			IloLinearNumExpr objective = cplex.linearNumExpr();
			// 0.12x + 0.15y
			objective.addTerm(0.12, x);
			objective.addTerm(0.15, y);
			
			// Minimierungsproblem
			cplex.addMinimize(objective);
			
			// Constraints definieren
			// 60x + 60y >= 300
			cplex.addGe(cplex.sum(cplex.prod(60, x),cplex.prod(60, y)),300);
			// 12x + 6y >= 36
			cplex.addGe(cplex.sum(cplex.prod(12, x),cplex.prod(6, y)),36);
			// 10x + 30y >= 90
			cplex.addGe(cplex.sum(cplex.prod(10, x),cplex.prod(30, y)),90);
			
			cplex.setParam(IloCplex.IntParam.SimDisplay, 0);
			// solve
			if(cplex.solve()){
				System.out.println("obj = " + cplex.getObjValue());
				System.out.println("x (Getreide) = " + cplex.getValue(x));
				System.out.println("y (Hafer) = " + cplex.getValue(y));
			}
			else{
				System.out.println("Model not solved");
			}
			cplex.end();
		}
		catch(IloException ex){
			ex.printStackTrace();
		}
		
	}
}
