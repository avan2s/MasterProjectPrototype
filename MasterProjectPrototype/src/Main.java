import java.util.ArrayList;
import java.util.List;

import ilog.concert.*;
import ilog.cplex.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main.exampleFarmer();
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
			
			// Minimierungsproblem
			cplex.addMaximize(objective);
			
			
			List<IloRange> constraints = new ArrayList<IloRange>();	
			
			// Constraints definieren
			// x + y >= maximale zu bepflanzende Acker
			IloLinearIntExpr cAcker = cplex.linearIntExpr();
			cAcker.addTerm(1, x);
			cAcker.addTerm(1, y);
			constraints.add(cplex.addLe(cAcker,maxAcker));
			System.out.println(constraints.get(0));
			
			// 2x + y >= maxStunden
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
				System.out.println("Wahrscheinlicher Umsatz = " + cplex.getObjValue());
				System.out.println("x (Getreide) = " + Math.round(cplex.getValue(x)));
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
