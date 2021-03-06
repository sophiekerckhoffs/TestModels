package optimizationModelTwoZones;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


import ilog.concert.*;
import ilog.cplex.*;



public class TestModel2 {
	public static void main(String[] args) {
		
		// Start reader
		
		String inputFileA = "/home/sophie/Downloads/TestCSVA.csv";
		String inputFileB = "/home/sophie/Downloads/TestCSVB.csv";
		BufferedReader brA = null;
		BufferedReader brB= null;
		String line = "";
		ArrayList<String> hourlyDataA = new ArrayList<String>();
		ArrayList<String> hourlyDataB = new ArrayList<String>();
		
		try{
			
			brA = new BufferedReader(new FileReader(inputFileA));
			
			while((line = brA.readLine()) != null){
				hourlyDataA.add(line);
			}
			
			brB = new BufferedReader(new FileReader(inputFileB));
			
			while((line = brB.readLine()) != null){
				hourlyDataB.add(line);
			}
			
				
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			if (brA != null) {
				try {
					brA.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
				
			}
			if (brB != null) {
				try {
					brB.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
				
			}
		}
		
		double[] hourlyDataDoubleA = new double[hourlyDataA.size()];
		for(int i = 0; i < hourlyDataDoubleA.length; i++){
			hourlyDataDoubleA[i] = Double.parseDouble(hourlyDataA.get(i));
		}
		
		double[] hourlyDataDoubleB = new double[hourlyDataB.size()];
		for(int i = 0; i < hourlyDataDoubleB.length; i++){
			hourlyDataDoubleB[i] = Double.parseDouble(hourlyDataB.get(i));
		}
		
		
		// Start Optimization model
		
		int HOURS = hourlyDataDoubleA.length; // amount of hours in simulation
		int[] zone = {1, 2};
		int ZONES = zone.length;
		double[] loadA = hourlyDataDoubleA; // load per Hour zone A
		double[] loadB = hourlyDataDoubleB; // load per Hour zone B
		double maxEnergycontentStorage = 10;
		double minEnergycontentStorage = 0;
		double initialStorage = 0;
		double maxStorageFlowIn = 5;
		double minStorageFlowIn = 0;
		double maxStorageFlowOut = 5;
		double minStorageFlowOut = 0;
		double maxMarketFlowIn = 10;
		double minMarketFlowIn = 0;
		double maxMarketFlowOut = 10;
		double minMarketFlowOut = 0;
		double minInterCap = -5;
		double maxInterCap = 5;
		double n = 0.90; // Storage efficiency
		double nInv = 1/n; // Inverse efficiency
		 
		
		try {
			// define new model
			IloCplex cplex = new IloCplex();
			
			// define variables
			
			IloNumVar[][] P = new IloNumVar[ZONES][HOURS]; // load in zone j at time i
			for (int j =0; j < ZONES; j++){
				P[j] = cplex.numVarArray(HOURS, zone[j], zone[j]);
				if (j == 0){
					for (int i = 0; i < HOURS; i++) {
						P[j][i] = cplex.numVar(loadA[i], loadA[i]);
					}
				}
				if (j == 1){
					for (int i = 0; i < HOURS; i++){
						P[j][i] = cplex.numVar(loadB[i], loadB[i]);
					}
				}
			}
			
						
			IloNumVar[][] E = new IloNumVar[ZONES][HOURS]; // Energy storage content in Zone j at time i
			for (int j = 0; j < ZONES; j++) {
				E[j] = cplex.numVarArray(HOURS, zone[j], zone[j]);
				for (int i = 0; i < HOURS; i++) {
					E[j][i] = cplex.numVar(minEnergycontentStorage, maxEnergycontentStorage);
				}
			}
			
					
			IloNumVar[][] mIn = new IloNumVar[ZONES][HOURS]; // Power outflow of market j at time i
			for (int j = 0; j < ZONES; j++) {
				mIn[j] = cplex.numVarArray(HOURS, zone[j], zone[j]);
				for (int i = 0; i < HOURS; i++) {
					mIn[j][i] = cplex.numVar(minMarketFlowIn, maxMarketFlowIn);
				}
			}
			
			IloNumVar[][] mOut = new IloNumVar[ZONES][HOURS]; // Power inflow of market j at time i
			for (int j = 0; j < ZONES; j++) {
				mOut[j] = cplex.numVarArray(HOURS, zone[j], zone[j]);
				for (int i = 0; i < HOURS; i++){
					mOut[j][i] = cplex.numVar(minMarketFlowOut, maxMarketFlowOut);
				}
			}
			
				
			IloNumVar[][] sIn = new IloNumVar[ZONES][HOURS]; // Storage inflow in storage j at time i
			for (int j = 0; j < ZONES; j++){
				sIn[j] = cplex.numVarArray(HOURS, zone[j], zone[j]);
				for (int i = 0; i < HOURS; i++){
					sIn[j][i] = cplex.numVar(minStorageFlowIn, maxStorageFlowIn);
				}
			}
			
			IloNumVar[][] sOut = new IloNumVar[ZONES][HOURS]; // Storage outflow of storage j at time i
			for (int j = 0; j < ZONES; j++){
				sOut[j] = cplex.numVarArray(HOURS, zone[j], zone[j]);
				for (int i = 0; i < HOURS; i++){
					sOut[j][i] = cplex.numVar(minStorageFlowOut, maxStorageFlowOut);
				}
			}
			
						
			IloNumVar[] I = new IloNumVar[HOURS]; // Power flow from zone A to B
			for (int i = 0; i < HOURS; i++){
				I[i] = cplex.numVar(minInterCap, maxInterCap);
			}
			
			// define expressions
			
			IloLinearNumExpr[][] storageContent = new IloLinearNumExpr[ZONES][HOURS];
			for (int j = 0; j < ZONES; j++){
				for (int i = 1; i < HOURS; i++) {
					storageContent[j][i] = cplex.linearNumExpr();
					storageContent[j][i].addTerm(1.0, E[j][i - 1]);
					storageContent[j][i].addTerm(n, sIn[j][i - 1]);
					storageContent[j][i].addTerm(-nInv, sOut[j][i-1]);
				}
			}
			
							
			
			IloLinearNumExpr[][] marketOutflow = new IloLinearNumExpr[ZONES][HOURS];
			for (int j = 0; j < ZONES; j++){
				for (int i = 0; i < HOURS; i++) {
					if (j == 0){
						marketOutflow[j][i] = cplex.linearNumExpr();
						marketOutflow[j][i].addTerm(1, sIn[j][i]);
						marketOutflow[j][i].addTerm(1, I[i]);
					}
					if (j == 1){
						marketOutflow[j][i] = cplex.linearNumExpr();
						marketOutflow[j][i].addTerm(1, sIn[j][i]);
						marketOutflow[j][i].addTerm(-1, I[i]);
					}
				}
			}
			
			IloLinearNumExpr[][] marketInflow = new IloLinearNumExpr[ZONES][HOURS];
			for (int j = 0; j < ZONES; j ++){
				for (int i = 0; i < HOURS; i++) {
					if (j == 0){
						marketInflow[j][i] = cplex.linearNumExpr();
						marketInflow[j][i].addTerm(1, sOut[j][i]);
						marketInflow[j][i].addTerm(-1, I[i]);
					}
					if (j == 1){
						marketInflow[j][i] = cplex.linearNumExpr();
						marketInflow[j][i].addTerm(1, sOut[j][i]);
						marketInflow[j][i].addTerm(1, I[i]);
					}
				}
			}
			
			
			
			IloLQNumExpr objective = cplex.lqNumExpr();
			for (int j = 0; j < ZONES; j++){
				for (int i = 0; i < HOURS; i++) {
					objective.addTerm(1, P[j][i], P[j][i]);
					objective.addTerm(1, mIn[j][i], mIn[j][i]);
					objective.addTerm(1, mOut[j][i], mOut[j][i]);
					objective.addTerm(2, P[j][i], mIn[j][i]);
					objective.addTerm(-2, P[j][i], mOut[j][i]);
					objective.addTerm(-2, mIn[j][i], mOut[j][i]);
				}
			}
			
			// define objective function
			cplex.addMinimize(objective);
			
			// define constraints
			
			for (int j = 0; j < ZONES; j++) {
				cplex.addEq(E[j][0], initialStorage);
			}	
			
			for (int j = 0; j < ZONES; j++) {
				cplex.addLe(sOut[j][0], E[j][0]);
			}
			
			for (int j = 0; j < ZONES; j++) {
				for (int i = 1; i < HOURS; i++) {
					cplex.addEq(storageContent[j][i], E[j][i]);
				}
			}
			
			for (int j = 0; j < ZONES; j++) {
				for (int i = 1; i < HOURS; i++) {
					cplex.addEq(storageContent[j][i], E[j][i]);
				}
			}
	
			for (int j =0; j < ZONES; j++) {
				for(int i = 0; i < HOURS; i++) {
					cplex.or(cplex.addEq(mOut[j][i], marketOutflow[j][i]), cplex.addEq(mIn[j][i], marketInflow[j][i]));
				}
			}
			
						
			// solve model
			if (cplex.solve()) {
				System.out.println("Objective function is: " + cplex.getObjValue());
				for (int j = 0; j < ZONES; j++) {
					for (int i = 0; i < HOURS; i++) {
						System.out.println("Hour is: " + (i + 1));
						System.out.println("Storage in : " + j + cplex.getValue(sIn[j][i]));
						System.out.println("Storage out : " + j + cplex.getValue(sOut[j][i]));
						System.out.println("Storage : " + j + cplex.getValue(E[j][i]));
						System.out.println("I: " + cplex.getValue(I[i]));
						System.out.println("Market in : " + j + cplex.getValue(mIn[j][i]));
						System.out.println("Market out : " + j + cplex.getValue(mOut[j][i]));
					
					}
				}
			}
			else {
				System.out.println("Model did not solve");
			}
			
			//close cplex
			cplex.end();
			
		}
		catch(IloException exc) {
			exc.printStackTrace();
		}
		
		
	}

}
