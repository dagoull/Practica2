package ExpositoTOP.src.top;

import Exposito.src.exposito.utilities.ExpositoUtilities;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

public class TOPTWSolution {
    public static final int NO_INITIALIZED = -1;
    private TOPTW problem;
    private int[] predecessors;
    private int[] successors;
    private double[] waitingTime;
    private int[] positionInRoute;

    private int[] routes;
    private int availableVehicles;
    private double objectiveFunctionValue;

    public TOPTWSolution(TOPTW problem) {
        this.problem = problem;
        this.availableVehicles = this.problem.getVehicles();
        this.predecessors = new int[this.problem.getPOIs()+this.problem.getVehicles()];
        this.successors = new int[this.problem.getPOIs()+this.problem.getVehicles()];
        this.waitingTime = new double[this.problem.getPOIs()];
        this.positionInRoute = new int[this.problem.getPOIs()];
        Arrays.fill(this.predecessors, TOPTWSolution.NO_INITIALIZED);
        Arrays.fill(this.successors, TOPTWSolution.NO_INITIALIZED);
        Arrays.fill(this.waitingTime, TOPTWSolution.NO_INITIALIZED);
        Arrays.fill(this.positionInRoute, TOPTWSolution.NO_INITIALIZED);
        this.routes = new int[this.problem.getVehicles()];
        this.objectiveFunctionValue = TOPTWEvaluator.NO_EVALUATED;
    }

    public void initSolution() {
        this.predecessors = new int[this.problem.getPOIs()+this.problem.getVehicles()];
        this.successors = new int[this.problem.getPOIs()+this.problem.getVehicles()];
        Arrays.fill(this.predecessors, TOPTWSolution.NO_INITIALIZED);
        Arrays.fill(this.successors, TOPTWSolution.NO_INITIALIZED);
        this.routes = new int[this.problem.getVehicles()];
        Arrays.fill(this.routes, TOPTWSolution.NO_INITIALIZED);
        this.routes[0] = 0;
        this.predecessors[0] = 0;
        this.successors[0] = 0;
        this.availableVehicles = this.problem.getVehicles() - 1;
    }

    public boolean isDepot(int c) {
        for(int i = 0; i < this.routes.length; i++) {
            if(c==this.routes[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(TOPTWSolution otherSolution) {
        for (int i = 0; i < this.predecessors.length; i++) {
            if (this.predecessors[i] != otherSolution.predecessors[i]) {
                return false;
            }
        }
        return true;
    }

    public int getAvailableVehicles() {
        return this.availableVehicles;
    }

    public int getCreatedRoutes() {
        return this.problem.getVehicles() - this.availableVehicles;
    }

    public double getDistance(int x, int y) {
        return this.problem.getDistance(x, y);
    }

    public void setAvailableVehicles(int availableVehicles) {
        this.availableVehicles = availableVehicles;
    }

    public int getPredecessor(int customer) {
        return this.predecessors[customer];
    }

    public int[] getPredecessors() {
        return this.predecessors;
    }

    public TOPTW getProblem() {
        return this.problem;
    }

    public double getObjectiveFunctionValue() {
        return this.objectiveFunctionValue;
    }

    public int getPositionInRoute(int customer) {
        return this.positionInRoute[customer];
    }

    public int getSuccessor(int customer) {
        return this.successors[customer];
    }

    public int[] getSuccessors() {
        return this.successors;
    }

    public int getIndexRoute(int index) {
        return this.routes[index];
    }

    public double getWaitingTime(int customer) {
        return this.waitingTime[customer];
    }

    public void setObjectiveFunctionValue(double objectiveFunctionValue) {
        this.objectiveFunctionValue = objectiveFunctionValue;
    }

    public void setPositionInRoute(int customer, int position) {
        this.positionInRoute[customer] = position;
    }

    public void setPredecessor(int customer, int predecessor) {
        this.predecessors[customer] = predecessor;
    }

    public void setSuccessor(int customer, int succesor) {
        this.successors[customer] = succesor;
    }

    public void setWaitingTime(int customer, int waitingTime) {
        this.waitingTime[customer] = waitingTime;
    }

    public String getInfoSolution() {
        final int COLUMN_WIDTH = 15;
        String text = "\n"+"NODES: " + this.problem.getPOIs() + "\n" + "MAX TIME PER ROUTE: " + this.problem.getMaxTimePerRoute() + "\n" + "MAX NUMBER OF ROUTES: " + this.problem.getMaxRoutes() + "\n";
        String textSolution = "\n"+"SOLUTION: "+"\n";
        double costTimeSolution = 0.0, fitnessScore = 0.0;
        boolean validSolution = true;
        for(int k = 0; k < this.getCreatedRoutes(); k++) { // rutas creadas
            String[] strings = new String[]{"\n" + "ROUTE " + k };
            int[] width = new int[strings.length];
            Arrays.fill(width, COLUMN_WIDTH);
            text += ExpositoUtilities.getFormat(strings, width) + "\n";
            strings = new String[]{"CUST NO.", "X COORD.", "Y. COORD.", "READY TIME", "DUE DATE", "ARRIVE TIME", " LEAVE TIME", "SERVICE TIME"};
            width = new int[strings.length];
            Arrays.fill(width, COLUMN_WIDTH);
            text += ExpositoUtilities.getFormat(strings, width) + "\n";
            strings = new String[strings.length];
            int depot = this.getIndexRoute(k);
            int pre=-1, suc=-1;
            double costTimeRoute = 0.0, fitnessScoreRoute = 0.0;
            pre = depot;
            int index = 0;
            strings[index++] = "" + pre;
            strings[index++] = "" + this.getProblem().getX(pre);
            strings[index++] = "" + this.getProblem().getY(pre);
            strings[index++] = "" + this.getProblem().getReadyTime(pre);
            strings[index++] = "" + this.getProblem().getDueTime(pre);
            strings[index++] = "" + 0;
            strings[index++] = "" + 0;
            strings[index++] = "" + this.getProblem().getServiceTime(pre);
            text += ExpositoUtilities.getFormat(strings, width);
            text += "\n";
            do {                // recorremos la ruta
                index = 0;
                suc = this.getSuccessor(pre);
                textSolution += pre+" - ";
                strings[index++] = "" + suc;
                strings[index++] = "" + this.getProblem().getX(suc);
                strings[index++] = "" + this.getProblem().getY(suc);
                strings[index++] = "" + this.getProblem().getReadyTime(suc);
                strings[index++] = "" + this.getProblem().getDueTime(suc);
                costTimeRoute += this.getDistance(pre, suc);
                if(costTimeRoute < (this.getProblem().getDueTime(suc))) {
                    if(costTimeRoute < this.getProblem().getReadyTime(suc)) {
                        costTimeRoute = this.getProblem().getReadyTime(suc);
                    }
                    strings[index++] = "" + costTimeRoute;
                    costTimeRoute +=  this.getProblem().getServiceTime(suc);
                    strings[index++] = "" + costTimeRoute;
                    strings[index++] = "" + this.getProblem().getServiceTime(pre);
                    if(costTimeRoute > this.getProblem().getMaxTimePerRoute()) { validSolution = false; }
                    fitnessScoreRoute += this.problem.getScore(suc);
                } else { validSolution = false; }
                pre = suc;
                text += ExpositoUtilities.getFormat(strings, width);
                text += "\n";
            } while(suc != depot);
            textSolution += suc+"\n";
            costTimeSolution += costTimeRoute;
            fitnessScore += fitnessScoreRoute;
        }
        textSolution += "FEASIBLE SOLUTION: "+validSolution+"\n"+"SCORE: "+fitnessScore+"\n"+"TIME COST: "+costTimeSolution+"\n";
        return textSolution+text;
    }

    public JSONObject getSolutionJSON()
    {
    	JSONObject object = new JSONObject();
    	object.put("number of nodes", this.problem.getPOIs());
    	object.put("max time per route", this.problem.getMaxTimePerRoute());
    	object.put("max number of routes", this.problem.getMaxRoutes());
    	JSONObject routes = new JSONObject();
    	boolean validSolution = true;
    	for (int i = 0; i<this.getCreatedRoutes(); i++)
    	{
    		ArrayList<JSONObject> nodelist = new ArrayList<JSONObject>();
    		JSONObject node = new JSONObject();
    		String[] strings = new String[]{"CUST NO.", "X COORD.", "Y. COORD.", "READY TIME", "DUE DATE", "ARRIVE TIME", " LEAVE TIME", "SERVICE TIME"};
    		int depot = this.getIndexRoute(i);
            int pre=-1, suc=-1;
            double costTimeRoute = 0.0, fitnessScoreRoute = 0.0;
            pre = depot;
    		int index = 0;
    		strings[index++] = "" + pre;
    		node.put("cust no.", strings[0]);
            strings[index++] = "" + this.getProblem().getX(pre);
            node.put("x coord.", strings[1]);
            strings[index++] = "" + this.getProblem().getY(pre);
            node.put("y coord.", strings[2]);
            strings[index++] = "" + this.getProblem().getReadyTime(pre);
            node.put("ready time", strings[3]);
            strings[index++] = "" + this.getProblem().getDueTime(pre);
            node.put("due date", strings[4]);
            strings[index++] = "" + 0;
            node.put("arrive time", strings[5]);
            strings[index++] = "" + 0;
            node.put("leave time", strings[6]);
            strings[index++] = "" + this.getProblem().getServiceTime(pre);
            node.put("service time", strings[7]);
            nodelist.add(node);
            do {                // recorremos la ruta
            	JSONObject nodek = new JSONObject();
            	index = 0;
                suc = this.getSuccessor(pre);
                strings[index++] = "" + suc;
                nodek.put("cust no.", strings[0]);
                strings[index++] = "" + this.getProblem().getX(suc);
                nodek.put("x coord.", strings[1]);
                strings[index++] = "" + this.getProblem().getY(suc);
                nodek.put("y coord.", strings[2]);
                strings[index++] = "" + this.getProblem().getReadyTime(suc);
                nodek.put("ready time", strings[3]);
                strings[index++] = "" + this.getProblem().getDueTime(suc);
                nodek.put("due date", strings[4]);
                costTimeRoute += this.getDistance(pre, suc);
                if(costTimeRoute < (this.getProblem().getDueTime(suc))) {
                    if(costTimeRoute < this.getProblem().getReadyTime(suc)) {
                        costTimeRoute = this.getProblem().getReadyTime(suc);
                    }
                    strings[index++] = "" + costTimeRoute;
                    nodek.put("arrive time", strings[5]);
                    costTimeRoute +=  this.getProblem().getServiceTime(suc);
                    strings[index++] = "" + costTimeRoute;
                    nodek.put("leave time", strings[6]);
                    strings[index++] = "" + this.getProblem().getServiceTime(pre);
                    nodek.put("service time", strings[7]);
                    nodelist.add(nodek);
                    if(costTimeRoute > this.getProblem().getMaxTimePerRoute()) { validSolution = false; }
                    fitnessScoreRoute += this.problem.getScore(suc);
                } else { validSolution = false; }
                pre = suc;
            } while(suc != depot);
            JSONObject[] nodes = new JSONObject[nodelist.size()];
            for (int j = 0; j<nodes.length; j++)
            {
            	nodes[j] = nodelist.get(j);
            }

            routes.put("route_"+i, nodes);
            nodelist.clear();
    	}
    	object.put("solution", routes);
    	return object;
    }

    public double evaluateFitness() {
        double objectiveFunction = 0.0;
        double objectiveFunctionPerRoute = 0.0;
        for(int k = 0; k < this.getCreatedRoutes(); k++) {
            int depot = this.getIndexRoute(k);
            int pre=depot, suc = -1;
            do {
                suc = this.getSuccessor(pre);
                objectiveFunctionPerRoute = objectiveFunctionPerRoute + this.problem.getScore(suc);
                pre = suc;
            } while((suc != depot));
            objectiveFunction = objectiveFunction + objectiveFunctionPerRoute;
            objectiveFunctionPerRoute = 0.0;
        }
        return objectiveFunction;
    }

    public int addRoute() {
        int depot = this.problem.getPOIs();
        depot++;
        int routePos = 1;
        for(int i = 0; i < this.routes.length; i++) {
            if(this.routes[i] != -1 && this.routes[i] != 0) {
                depot = this.routes[i];
                depot++;
                routePos = i+1;
            }
        }
        this.routes[routePos] = depot;
        this.availableVehicles--;
        this.predecessors[depot] = depot;
        this.successors[depot] = depot;
        this.problem.addNodeDepot();
        return depot;
    }

    public double printSolution() {
        for(int k = 0; k < this.getCreatedRoutes(); k++) {
                int depot = this.getIndexRoute(k);
                int pre=depot, suc = -1;
                do {
                    suc = this.getSuccessor(pre);
                    System.out.print(pre+" - ");
                    pre = suc;
                } while((suc != depot));
                System.out.println(suc+"  ");
        }
        double fitness = this.evaluateFitness();
        System.out.println("SC="+fitness);
        return fitness;
    }

}
