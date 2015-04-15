import ilog.concert.*;
import ilog.cplex.*;

public class optimizerTester {
	
	public static void main(String[] args) {
		
	
			
		int HOUR = 10; //hours in a year in this test
		int ZONES = 2; // number of zones in model
		int[] zones = {1, 2}; // zones in model
		double[] loadA = {1, 10, 1, 10, 1, 10, 1, 10, 1, 10}; // load per Hour zone A
		double[] loadB = {2, 10, 2, 10, 2, 10, 2, 10, 2, 10}; // load per Hour zone B
		double maxEnergycontentStorage = 10;
		double minEnergycontentStorage = 0;
		double initialStorage = 0;
		double maxStorageFlowIn = 1.5;
		double minStorageFlowIn = 0;
		double maxStorageFlowOut = 2;
		double minStorageFlowOut = 0;
		double maxMarketFlowIn = 10;
		double minMarketFlowIn = 0;
		double maxMarketFlowOut = 10;
		double minMarketFlowOut = 0;
		double minInterCap = 0;
		double maxInterCap = 5;
		double n = 0.90; // Storage efficiency
		double nInv = 1/n; // Inverse efficiency
		
		
		try {
			//define new model
			IloCplex cplex = new IloCplex();
			
			// define variables // i is zones & j is hours
			IloNumVar[][] sIn = new IloNumVar[][];
			for (int i = 0; i < ZONES; i++) {
				sIn[i] = cplex.numVarArray(HOUR, ZONES[i], ZONES[i]);
				for (int j = 0; j < HOUR; j++){
					sIn[i][j] = cplex.numVar(minStorageFlowIn, maxStorageFlowIn);
				}
			}
			
			IloNumVar[][] sOut = new IloNumVar[][];
			for (int i = 0; i < ZONES; i++) {
				sOut[i] = cplex.NumVarArray(HOUR, ZONES[i], ZONES[i]);
				for (int j = 0; j < HOUR; j++){
					sOut[i][j] = cplex.numVar(minStorageFlowOut, maxStorageFlowOut);
				}
			}
			
			IloNumVar[][] E = new IloNumVar[][];
			e[0] = cplex.numVar(initialStorage, initialStorage);
			for (int i = 0; i < ZONES; i++) {
				E[i] = cplex.NumVarArray(HOUR, ZONES[i], ZONES[i]);
				for (int j = 1; j < HOUR; j++ ){
					E[i][j] = cplex.numVar(minEnergycontentStorage, maxEnergycontentStorage);
				}
			}
							
			
			IloNumVar[][] P = new IloNumVar[][];
			for (int i = 0; i < ZONES; i++) {
				P[i] = cplex.NumVarArray(HOUR, ZONES[i], ZONES[i]);
				if(i == 0){
					for (int j = 0; j < HOUR; j++) {
						P[i][j] = cplex.NumVar(loadA[j], loadA[j])
					}
				}
				if (i == 1){
					for (int j = 0; j < HOUR; j++){
						P[i][j] = cplex.NumVar(loadB[j], loadB[j])
					}
				}
			}
			
			IloNumVar[][] mIn = new IloNumVar[][];
			for (int i = 0; i < ZONES; i++){
				mIn[i] = cplex.NumVarArray(HOUR, ZONES[i], ZONES[i]);
				for (int j = 0; j < HOUR; j++){
					mIn[i][j] = cplex.numVar(minMarketFlowIn, maxMarketFlowIn);
				}
			}
			
			IloNumVar[][] mOut = new IloNumVar[][];
			for (int i = 0; i < ZONES; i++){
				mOut[i] = cplex.NumVarArray(HOUR, ZONES[i], ZONES[i]);
				for (int j = 0; j < HOUR; j++){
					mOut[i][j] = cplex.NumVarArray(minMarketFlowOut, maxMarketFlowOut);
				}
			}
			
			IloNumVar[] I = new IloNumVar[];
			for (int j = 0; j < HOUR; j++){
				I[j] = cplex.NumVar(maxInterCap, minInterCap);
			}
			
			IloNumVar[] Hour = new IloNumVar[HOUR];
			for (int i = 0; i < HOUR; i++){
				Hour[i] = cplex.numVar(i, i);
			}
			
			// define expressions
			IloLinearNumExpr[][] storageInflow = new IloLinearNumExpr[][];
			for (int i = 0; i < ZONES; i++){
				for (int j = 0; j < HOUR; j++){
					storageInflow[i][j] = cplex.linearNumExpr();
					storageInflow[i][j].addTerm(1.0, sOut[i][j]);
					storageInflow[i][j].addTerm(1.0, I[j]);
				}
			}
			
			IloLinearNumExpr[][] storageOutflow = new IloLinearNumExpr[][];
			for (int i = 0; i < ZONES; i++){
				for(int j = 0; j < HOUR; j ++){
					storageOutflow[i][j] = cplex.linearNumExpr();
					storageOutflow[i][j].addTerm(1.0, sIn[i][j]);
					storageOutflow[i][j].addTerm(-1.0, I[j]);
				}
			}
			
			IloLinearNumExpr[][] marketInflow = new IloLinearNumExpr[][];
			for (int i = 0; i < ZONES; i++){
				for (int j = 0; j < HOUR; j++){
					marketInflow[i][j] = cplex.linearNumExpr();
					marketInflow[i][j].addTerm(1.0, I[j]);
					marketInflow[i][j].addTerm(1.0, )
				}
			}
			
			IloLinearNumExpr[] exprStorageContent = new IloLinearNumExpr[HOUR];
			for (int i = 1; i < HOUR; i++) {
				exprStorageContent[i] = cplex.linearNumExpr();
				exprStorageContent[i].addTerm(1.0, E[i - 1]);
				exprStorageContent[i].addTerm(n, sIn[i - 1]);
				exprStorageContent[i].addTerm(-nInv, sOut[i - 1]);
				}
			
			
			IloLQNumExpr objective = cplex.lqNumExpr();
			for (int i = 0; i < HOUR; i++) {
				objective.addTerm(1, P[i], P[i]);
				objective.addTerm(1, sIn[i], sIn[i]);
				objective.addTerm(1, sOut[i], sOut[i]);
				objective.addTerm(2, P[i], sIn[i]);
				objective.addTerm(-2, P[i], sOut[i]);
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
