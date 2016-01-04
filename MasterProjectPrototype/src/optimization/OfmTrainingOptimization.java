package optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.cplex.IloCplex;
import warehouse.model.Warehouse;

public class OfmTrainingOptimization{

	private IloCplex cplex;
	private List<SpielerTrainingsteilnahme> xTeilnahmen;
	private List<IloTrainingstag> xTrainingstage;
	private List<Spieler> spielerliste;
	private List<Trainingsfactor> xTrainFactors;
	private String[] trainingseinheiten;
	
	public OfmTrainingOptimization(List<Spieler> spielerliste, String[] trainingseinheiten) {
		this.xTeilnahmen = new ArrayList<>();
		this.xTrainingstage = new ArrayList<>();
		this.xTrainFactors = new ArrayList<>();
		this.spielerliste = spielerliste;
		this.trainingseinheiten = trainingseinheiten;
	}

	public void createModel(){
		try{
			this.cplex = new IloCplex();
			// Decision Variables erstellen
			// Decision Variables Trainingsteilnahmen
			for(Spieler s : this.spielerliste){
				String varName = "X".concat(s.getName());
				SpielerTrainingsteilnahme stt = new SpielerTrainingsteilnahme(s,cplex.boolVar(varName));
				this.xTeilnahmen.add(stt);
			}
			
			// Decision Variables xTrainingstage
			for(int tag=0;tag<5;tag++){
				for(String einheit : trainingseinheiten){
					StringBuilder sb = new StringBuilder("X");
					sb.append(tag).append(";").append(einheit);
					IloTrainingstag tt = new IloTrainingstag(tag, einheit, cplex.boolVar(sb.toString()));
					this.xTrainingstage.add(tt);
				}
				
			}
			
			// Trainingsfaktoren
			for(SpielerTrainingsteilnahme teilnahme : this.xTeilnahmen){
				Spieler s = teilnahme.getSpieler();
				for(IloTrainingstag trainingstag : this.xTrainingstage){
					StringBuilder sb = new StringBuilder("X");
					sb.append(s.getName()).append(";").append(trainingstag.getTag()).append(";").append(trainingstag.getEinheit());
					Trainingsfactor tFactor = new Trainingsfactor(teilnahme, trainingstag, cplex.boolVar(sb.toString()));
					this.xTrainFactors.add(tFactor);
				}
			}
			// objective
			
			
			// objective
			IloLinearIntExpr objective = cplex.linearIntExpr();
			for(Trainingsfactor trainFactor : this.xTrainFactors){
				Spieler s = trainFactor.getTeilnahme().getSpieler();
				IloTrainingstag tTag = trainFactor.getTrainingstag();
				objective.addTerm(trainFactor.getIloBoolVar(),s.getTpGewinn(tTag.getEinheit()));
			}
			System.out.println(cplex.addMaximize(objective));
			
			// Nebenbedingung - Ein Training pro Tag
			Map<Integer,IloLinearIntExpr> day2conditionOneTrainingPerDay = new HashMap<>();
			for (IloTrainingstag trainingstag : this.xTrainingstage){
				int day = trainingstag.getTag();
				if(day2conditionOneTrainingPerDay.containsKey(day)){
					day2conditionOneTrainingPerDay.get(day).addTerm(1, trainingstag.getIloBoolVar());
				}
				else{
					day2conditionOneTrainingPerDay.put(day, cplex.linearIntExpr());
					day2conditionOneTrainingPerDay.get(day).addTerm(1,trainingstag.getIloBoolVar());
				}
				//conditionExpr.addTerm(1, trainingstag.getIloBoolVar());
			}
			for(Map.Entry<Integer, IloLinearIntExpr> entry : day2conditionOneTrainingPerDay.entrySet()){
				System.out.println(cplex.addLe(entry.getValue(),1));
			}
			
			
			// Nebenbedingung - Trainingsfaktor richtig setzen	
			for(Trainingsfactor trainFactor : this.xTrainFactors){
				IloIntVar xTeilnahme = trainFactor.getTeilnahme().getIloBoolVar();
				IloIntVar xTrainingstag = trainFactor.getTrainingstag().getIloBoolVar();
				IloIntVar xTrainfactor = trainFactor.getIloBoolVar();
				
				// xTrainfactor <= XTeilnahme --> xTrainfactor - XTeilnahme <= 0 
				IloLinearIntExpr conditionTrainingsfactorExpr1 = cplex.linearIntExpr();
				conditionTrainingsfactorExpr1.addTerm(xTrainfactor, 1);
				conditionTrainingsfactorExpr1.addTerm(xTeilnahme, -1);
				System.out.println(cplex.addLe(conditionTrainingsfactorExpr1, 0));
				
				// xTrainingsfactor <= XTrainingstag
				IloLinearIntExpr conditionTrainingsfactorExpr2 = cplex.linearIntExpr();
				conditionTrainingsfactorExpr2.addTerm(xTrainfactor, 1);
				conditionTrainingsfactorExpr2.addTerm(xTrainingstag, -1);
				System.out.println(cplex.addLe(conditionTrainingsfactorExpr2, 0));
				
				// xTrainingsfactor >= XTeilnahme + xTrainingstag - 1 --> 0 >= XTeilnahme + xTrainingstag - 1 - xTrainingsfactor
				IloLinearIntExpr conditionTrainingsfactorExpr3 = cplex.linearIntExpr();
				conditionTrainingsfactorExpr3.addTerm(xTeilnahme, 1);
				conditionTrainingsfactorExpr3.addTerm(xTrainingstag, 1);
				conditionTrainingsfactorExpr3.addTerm(xTrainfactor, -1);
				System.out.println(conditionTrainingsfactorExpr3.getConstant());
				conditionTrainingsfactorExpr3.addTerm(cplex.intVar(1, 1), -1);
				System.out.println(cplex.addLe(conditionTrainingsfactorExpr3, 0));
				
			}
			
			// Nebenbedingungen für Frischeverlust
			Map<Spieler,IloLinearIntExpr> player2conditionFrische = new HashMap<>(); 
			for(Trainingsfactor trainFactor : this.xTrainFactors){
				Spieler s = trainFactor.getTeilnahme().getSpieler();
				IloTrainingstag tTag = trainFactor.getTrainingstag();
				if(player2conditionFrische.containsKey(s)){
					player2conditionFrische.get(s).addTerm(trainFactor.getIloBoolVar(),-s.frischeVerlust(tTag.getEinheit()));
				}
				else{
					player2conditionFrische.put(s, cplex.linearIntExpr());
					player2conditionFrische.get(s).addTerm(cplex.intVar(s.getFrische(), s.getFrische()), 1);
					player2conditionFrische.get(s).addTerm(trainFactor.getIloBoolVar(),-s.frischeVerlust(tTag.getEinheit()));
				}
			}
			
			for(Map.Entry<Spieler, IloLinearIntExpr> entry : player2conditionFrische.entrySet()){
				System.out.println(cplex.addGe(entry.getValue(),20));
			}
			
			// lösen
			if(cplex.solve()){
				System.out.println("TP-Gesamtgewinn (objective) = " + cplex.getObjValue());
				System.out.println("Perfektes Training");
				for(IloTrainingstag trainingstag : this.xTrainingstage){
					if(cplex.getValue(trainingstag.getIloBoolVar())==1){
						System.out.println("Tag: " + trainingstag.getTag() + "\tEinheit: " + trainingstag.getEinheit() /*+ "\t Im Trainingsplan?" + cplex.getValue(trainingstag.getIloBoolVar())*/);
					}
				}
				System.out.println("\nTeilnehmer an Trainingswoche:");
				for(SpielerTrainingsteilnahme tt : this.xTeilnahmen){
					if(cplex.getValue(tt.getIloBoolVar())==1){
						System.out.println("Spieler: " + tt.getSpieler().getName() /*+ cplex.getValue(tt.getIloBoolVar())*/);
					}
				}
			}
			else{
				System.out.println("Model not solved");
			}
			cplex.end();
		}
		catch(IloException ex){
			
		}
	}
}
