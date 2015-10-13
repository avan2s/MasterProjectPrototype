import ilog.concert.*;
import ilog.cplex.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main.model1();
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
				System.out.println("x = " + cplex.getValue(x));
				System.out.println("y = " + cplex.getValue(y));
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
