import ilog.concert.*;
import ilog.cplex.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void model1(){
		try{
			IloCplex cplex = new IloCplex();
		}
		catch(IloException ex){
			ex.printStackTrace();
		}
	}
}
