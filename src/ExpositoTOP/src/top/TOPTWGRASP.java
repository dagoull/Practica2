package ExpositoTOP.src.top;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class TOPTWGRASP {
 public static double NO_EVALUATED = -1.0;

    private TOPTWSolution solution;
    private TOPTWSolution best_solution;
    private int solutionTime;

    public TOPTWGRASP(TOPTWSolution sol){
        this.solution = sol;
        this.best_solution = null;
        this.solutionTime = 0;
    }

    /*procedure GRASP(Max Iterations,Seed)
        1 Read Input();
        2 for k = 1, . . . , Max Iterations do
            3 Solution â†� Greedy Randomized Construction(Seed);
            4 Solution â†� Local Search(Solution);
            5 Update Solution(Solution,Best Solution);
        6 end;
        7 return Best Solution;
    end GRASP*/

    /*procedure Greedy Randomized Construction(Seed)
        Solution â†� âˆ…;
        Evaluate the incremental costs of the candidate elements;
        while Solution is not a complete solution do
            Build the restricted candidate list (RCL);
            Select an element s from the RCL at random;
            Solution â†� Solution âˆª {s};
            Reevaluate the incremental costs;
        end;
        return Solution;
    end Greedy Randomized Construction.*/

    public void GRASP(int maxIterations, int maxSizeRCL) {
        double averageFitness = 0.0;
        double bestSolution = 0.0;
        for(int i = 0; i < maxIterations; i++) {

            this.computeGreedySolution(maxSizeRCL);

            // IMPRIMIR SOLUCION
            double fitness = this.solution.evaluateFitness();
            System.out.println(this.solution.getInfoSolution());
            //System.out.println("Press Any Key To Continue...");
            //new java.util.Scanner(System.in).nextLine();
            averageFitness += fitness;
            if(bestSolution < fitness) {
                bestSolution = fitness;
                this.best_solution=this.solution;
            }
            //double fitness = this.solution.printSolution();

            /******
            *
            * BÃšSQUEDA LOCAL
            *
            */
        }
        averageFitness = averageFitness/maxIterations;
        System.out.println(" --> MEDIA: "+averageFitness);
        System.out.println(" --> MEJOR SOLUCION: "+bestSolution);
    }

    public int aleatorySelectionRCL(int maxTRCL) {
       Random r = new Random();
       int low = 0;
       int high = maxTRCL;
       int posSelected = r.nextInt(high-low) + low;
       return posSelected;
    }

    public int fuzzySelectionBestFDRCL(ArrayList< double[] > rcl) {
        double[] membershipFunction = new double[rcl.size()];
        double maxSc = this.getMaxScore();
        for(int j=0; j < rcl.size(); j++) {
            membershipFunction[j] = 1 - ((rcl.get(j)[4])/maxSc);
        }
        double minMemFunc = Double.MAX_VALUE;
        int posSelected = -1;
        for(int i = 0; i < rcl.size(); i++) {
            if(minMemFunc > membershipFunction[i]) {
                minMemFunc = membershipFunction[i];
                posSelected = i;
            }
        }
        return posSelected;
    }

    public int fuzzySelectionAlphaCutRCL(ArrayList< double[] > rcl, double alpha) {
        ArrayList< double[] > rclAlphaCut = new ArrayList< double[] >();
        ArrayList< Integer > rclPos = new ArrayList< Integer >();
        double[] membershipFunction = new double[rcl.size()];
        double maxSc = this.getMaxScore();
        for(int j=0; j < rcl.size(); j++) {
            membershipFunction[j] = 1 - ((rcl.get(j)[4])/maxSc);
            if(membershipFunction[j] <= alpha) {
                rclAlphaCut.add(rcl.get(j));
                rclPos.add(j);
            }
        }
        int posSelected = -1;
        if(rclAlphaCut.size() > 0) {
            posSelected = rclPos.get(aleatorySelectionRCL(rclAlphaCut.size()));
        } else {
            posSelected = aleatorySelectionRCL(rcl.size());
        }
        return posSelected;
    }

    public void computeGreedySolution(int maxSizeRCL) {
        // inicializaciÃ³n
        this.solution.initSolution();

        // tiempo de salida y score por ruta y cliente
        ArrayList<ArrayList<Double>> departureTimesPerClient = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> init = new ArrayList<Double>();
        for(int z = 0; z < this.solution.getProblem().getPOIs()+this.solution.getProblem().getVehicles(); z++) {init.add(0.0);}
        departureTimesPerClient.add(0, init);

        // clientes
        ArrayList<Integer> customers = new ArrayList<Integer>();
        for(int j = 1; j <= this.solution.getProblem().getPOIs(); j++) { customers.add(j); }

        // Evaluar coste incremental de los elementos candidatos
        ArrayList< double[] > candidates = this.comprehensiveEvaluation(customers, departureTimesPerClient);

        Collections.sort(candidates, new Comparator<double[]>() {
            public int compare(double[] a, double[] b) {
                return Double.compare(a[a.length-2], b[b.length-2]);
            }
        });

        int maxTRCL = maxSizeRCL;
        boolean existCandidates = true;

        while(!customers.isEmpty() && existCandidates) {
            if(!candidates.isEmpty()) {
                //Construir lista restringida de candidatos
                ArrayList< double[] > rcl = new ArrayList< double[] >();
                maxTRCL = maxSizeRCL;
                if(maxTRCL > candidates.size()) { maxTRCL = candidates.size(); }
                for(int j=0; j < maxTRCL; j++) { rcl.add(candidates.get(j)); }

                //SelecciÃ³n aleatoria o fuzzy de candidato de la lista restringida
                int posSelected = -1;
                int selection = 3;
                double alpha = 0.8;
                switch (selection) {
                    case 1:  posSelected = this.aleatorySelectionRCL(maxTRCL);  // SelecciÃ³n aleatoria
                             break;
                    case 2:  posSelected = this.fuzzySelectionBestFDRCL(rcl);   // SelecciÃ³n fuzzy con mejor valor de alpha
                             break;
                    case 3:  posSelected = this.fuzzySelectionAlphaCutRCL(rcl, alpha); // SelecciÃ³n fuzzy con alpha corte aleatoria
                             break;
                    default: posSelected = this.aleatorySelectionRCL(maxTRCL);  // SelecciÃ³n aleatoria por defecto
                             break;
                }

                double[] candidateSelected = rcl.get(posSelected);
                for(int j=0; j < customers.size(); j++) {
                    if(customers.get(j)==candidateSelected[0]) {
                        customers.remove(j);
                    }
                }

                updateSolution(candidateSelected, departureTimesPerClient);

            } else { // No hay candidatos a insertar en la soluciÃ³n, crear otra ruta
                if(this.solution.getCreatedRoutes() < this.solution.getProblem().getVehicles()) {
                    int newDepot = this.solution.addRoute();
                    ArrayList<Double> initNew = new ArrayList<Double>();
                    for(int z = 0; z < this.solution.getProblem().getPOIs()+this.solution.getProblem().getVehicles(); z++) {initNew.add(0.0);}
                    departureTimesPerClient.add(initNew);
                }
                else {
                    existCandidates = false;
                }
            }
            //Reevaluar coste incremental de los elementos candidatos
            candidates.clear();
            candidates = this.comprehensiveEvaluation(customers, departureTimesPerClient);
            Collections.sort(candidates, new Comparator<double[]>() {
                public int compare(double[] a, double[] b) {
                    return Double.compare(a[a.length-2], b[b.length-2]);
                }
            });
        }

    }

    public void updateSolution(double[] candidateSelected, ArrayList< ArrayList< Double > > departureTimes) {
        // InserciÃ³n del cliente en la ruta  return: cliente, ruta, predecesor, coste
        this.solution.setPredecessor((int)candidateSelected[0], (int)candidateSelected[2]);
        this.solution.setSuccessor((int)candidateSelected[0], this.solution.getSuccessor((int)candidateSelected[2]));
        this.solution.setSuccessor((int)candidateSelected[2], (int)candidateSelected[0]);
        this.solution.setPredecessor(this.solution.getSuccessor((int)candidateSelected[0]), (int)candidateSelected[0]);

        // ActualizaciÃ³n de las estructuras de datos y conteo a partir de la posiciÃ³n a insertar
        double costInsertionPre = departureTimes.get((int)candidateSelected[1]).get((int)candidateSelected[2]);
        ArrayList<Double> route = departureTimes.get((int)candidateSelected[1]);
        int pre=(int)candidateSelected[2], suc=-1;
        int depot = this.solution.getIndexRoute((int)candidateSelected[1]);
        do {
            suc = this.solution.getSuccessor(pre);
            costInsertionPre += this.solution.getDistance(pre, suc);

            if(costInsertionPre < this.solution.getProblem().getReadyTime(suc)) {
                costInsertionPre = this.solution.getProblem().getReadyTime(suc);
            }
            costInsertionPre += this.solution.getProblem().getServiceTime(suc);

            if(!this.solution.isDepot(suc))
                route.set(suc, costInsertionPre);
            pre = suc;
        } while((suc != depot));

        // Actualiza tiempos
        departureTimes.set((int)candidateSelected[1], route);
    }

    //return: cliente, ruta, predecesor, coste tiempo, score
    public ArrayList< double[] > comprehensiveEvaluation(ArrayList<Integer> customers, ArrayList< ArrayList< Double > > departureTimes) {
        ArrayList< double[] > candidatesList = new ArrayList< double[] >();
        double[] infoCandidate = new double[5];
        boolean validFinalInsertion = true;
        infoCandidate[0] = -1;
        infoCandidate[1] = -1;
        infoCandidate[2] = -1;
        infoCandidate[3] = Double.MAX_VALUE;
        infoCandidate[4] = -1;

        for(int c = 0; c < customers.size(); c++) { // clientes disponibles
            for(int k = 0; k < this.solution.getCreatedRoutes(); k++) { // rutas creadas
                validFinalInsertion = true;
                int depot = this.solution.getIndexRoute(k);
                int pre=-1, suc=-1;
                double costInsertion = 0;
                pre = depot;
                int candidate = customers.get(c);
                do {                                                // recorremos la ruta
                    validFinalInsertion = true;
                    suc = this.solution.getSuccessor(pre);
                    double timesUntilPre = departureTimes.get(k).get(pre) + this.solution.getDistance(pre, candidate);
                    if(timesUntilPre < (this.solution.getProblem().getDueTime(candidate))) {
                        double costCand = 0;
                        if(timesUntilPre < this.solution.getProblem().getReadyTime(candidate)) {
                            costCand = this.solution.getProblem().getReadyTime(candidate);
                        } else { costCand = timesUntilPre; }
                        costCand +=  this.solution.getProblem().getServiceTime(candidate);
                        if(costCand > this.solution.getProblem().getMaxTimePerRoute()) { validFinalInsertion = false; }

                        // Comprobar TW desde candidate hasta sucesor
                        double timesUntilSuc = costCand + this.solution.getDistance(candidate, suc);
                        if(timesUntilSuc < (this.solution.getProblem().getDueTime(suc))) {
                            double costSuc = 0;
                            if(timesUntilSuc < this.solution.getProblem().getReadyTime(suc)) {
                                costSuc = this.solution.getProblem().getReadyTime(suc);
                            } else { costSuc = timesUntilSuc; }
                            costSuc +=  this.solution.getProblem().getServiceTime(suc);
                            costInsertion = costSuc;
                            if(costSuc > this.solution.getProblem().getMaxTimePerRoute()) { validFinalInsertion = false;}

                            int pre2=suc, suc2 = -1;
                            if(suc != depot)
                                do {
                                    suc2 = this.solution.getSuccessor(pre2);
                                    double timesUntilSuc2 = costInsertion + this.solution.getDistance(pre2, suc2);
                                    if(timesUntilSuc2 < (this.solution.getProblem().getDueTime(suc2))) {
                                        if(timesUntilSuc2 < this.solution.getProblem().getReadyTime(suc2)) {
                                            costInsertion = this.solution.getProblem().getReadyTime(suc2);
                                        } else { costInsertion = timesUntilSuc2; }
                                        costInsertion += this.solution.getProblem().getServiceTime(suc2);
                                        if(costInsertion > this.solution.getProblem().getMaxTimePerRoute()) { validFinalInsertion = false; }
                                    } else { validFinalInsertion = false; }
                                    pre2 = suc2;
                                } while((suc2 != depot) && validFinalInsertion);
                        } else { validFinalInsertion = false; }
                    } else { validFinalInsertion = false; }

                    if(validFinalInsertion==true) { // cliente, ruta, predecesor, coste
                        if(costInsertion < infoCandidate[3]) {
                            infoCandidate[0] = candidate; infoCandidate[1] = k; infoCandidate[2] = pre; infoCandidate[3] = costInsertion; infoCandidate[4] = this.solution.getProblem().getScore(candidate); // cliente, ruta, predecesor, coste, score
                        }
                    }

                    pre = suc;
                } while(suc != depot);
            } //rutas creadas

            // almacenamos en la lista de candidatos la mejor posiciÃ³n de inserciÃ³n para el cliente
            if(infoCandidate[0]!=-1 && infoCandidate[1]!=-1 && infoCandidate[2]!=-1 && infoCandidate[3] != Double.MAX_VALUE && infoCandidate[4]!=-1) {
                double[] infoCandidate2 = new double[5];
                infoCandidate2[0] = infoCandidate[0];  infoCandidate2[1] = infoCandidate[1];
                infoCandidate2[2] = infoCandidate[2];  infoCandidate2[3] = infoCandidate[3];
                infoCandidate2[4] = infoCandidate[4];
                candidatesList.add(infoCandidate2);
            }
            validFinalInsertion = true;
            infoCandidate[0] = -1;  infoCandidate[1] = -1;
            infoCandidate[2] = -1;  infoCandidate[3] = Double.MAX_VALUE;
            infoCandidate[4] = -1;
        } // cliente

        return candidatesList;
    }

    public TOPTWSolution getSolution() {
        return solution;
    }

    public void setSolution(TOPTWSolution solution) {
        this.solution = solution;
    }

    public int getSolutionTime() {
        return solutionTime;
    }

    public void setSolutionTime(int solutionTime) {
        this.solutionTime = solutionTime;
    }

    public double getMaxScore() {
        double maxSc = -1.0;
        for(int i = 0; i < this.solution.getProblem().getScore().length; i++) {
            if(this.solution.getProblem().getScore(i) > maxSc)
                maxSc = this.solution.getProblem().getScore(i);
        }
        return maxSc;
    }

	/**
	 * @return the best_solution
	 */
	public TOPTWSolution getBest_solution() {
		return best_solution;
	}

	/**
	 * @param best_solution the best_solution to set
	 */
	public void setBest_solution(TOPTWSolution best_solution) {
		this.best_solution = best_solution;
	}



}
