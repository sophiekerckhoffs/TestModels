import ilog.concert.*;
import ilog.cplex.*;


public class OptimizationModelTest {
	public static void main(String[] args) {
		
		
		
		int HOUR = 10; //hours in a year in this test
		double[] load = {1, 10, 1, 10, 1, 10, 1, 10, 1, 10}; // load per Hour
		double maxEnergycontentStorage = 10;
		double minEnergycontentStorage = 0;
		double initialStorage = 0;
		double maxStorageFlowIn = 1.5;
		double minStorageFlowIn = 0;
		double maxStorageFlowOut = 2;
		double minStorageFlowOut = 0;
		double n = 0.90; // Storage efficiency
		double nInv = 1/n; // Inverse efficiency
		
		
		try {
			//define new model
			IloCplex cplex = new IloCplex();
			
			// define variables
			IloNumVar[] sIn = new IloNumVar[HOUR];
			for (int i = 0; i < HOUR; i++) {
				sIn[i] = cplex.numVar(minStorageFlowIn, maxStorageFlowIn);
			};
			
			IloNumVar[] sOut = new IloNumVar[HOUR];
			for (int i = 0; i < HOUR; i++) {
				sOut[i] = cplex.numVar(minStorageFlowOut, maxStorageFlowOut);
			}
			
			IloNumVar[] e = new IloNumVar[HOUR];
			e[0] = cplex.numVar(initialStorage, initialStorage);
			for (int i = 1; i < HOUR; i++) {
				e[i] = cplex.numVar(minEnergycontentStorage, maxEnergycontentStorage);
			}
			
			IloNumVar[] loadPerHour = new IloNumVar[HOUR];
			for (int i = 0; i < HOUR; i++) {
				loadPerHour[i] = cplex.numVar(load[i], load[i]);
			}
			
			IloNumVar[] Hour = new IloNumVar[HOUR];
			for (int i = 0; i < HOUR; i++){
				Hour[i] = cplex.numVar(i, i);
			}
			
			// define expressions
			IloLinearNumExpr[] exprStorageContent = new IloLinearNumExpr[HOUR];
			
			for (int i = 1; i < HOUR; i++) {
				exprStorageContent[i] = cplex.linearNumExpr();
				exprStorageContent[i].addTerm(1.0, e[i - 1]);
				exprStorageContent[i].addTerm(n, sIn[i - 1]);
				exprStorageContent[i].addTerm(-nInv, sOut[i - 1]);
				}
			
			
			IloLQNumExpr objective = cplex.lqNumExpr();
			for (int i = 0; i < HOUR; i++) {
				objective.addTerm(1, loadPerHour[i], loadPerHour[i]);
				objective.addTerm(1, sIn[i], sIn[i]);
				objective.addTerm(1, sOut[i], sOut[i]);
				objective.addTerm(2, loadPerHour[i], sIn[i]);
				objective.addTerm(-2, loadPerHour[i], sOut[i]);
				objective.addTerm(-2, sIn[i], sOut[i]);
				
				
			}
			
			// define objective function
			cplex.addMinimize(objective);
			
			//define constraints
			cplex.addEq(e[0], initialStorage);
			cplex.addLe(sOut[0], e[0]);
			for(int i = 1; i < HOUR; i++){
				cplex.addEq(exprStorageContent[i], e[i]);
			}
			
			// solve model
			if (cplex.solve()) {
				System.out.println("objective function is:");
				System.out.println(cplex.getObjValue());
				for (int i =0; i < HOUR; i++) {
					System.out.println("Hour" + i);
					System.out.println("Storage in " + cplex.getValue(sIn[i]));
					System.out.println("Storage out " + cplex.getValue(sOut[i]));
					System.out.println("Storage " + cplex.getValue(e[i]));
				}
				
				}
			else {
				System.out.println("model did not sovle");
			}
			
			// close cplex
			cplex.end(); 
			
			
		}
		catch (IloException exc) {
			exc.printStackTrace();
		}
	
}


}
