package ExpositoTOP.src.top;

import Exposito.src.exposito.utilities.ExpositoUtilities;
import java.util.ArrayList;
import java.util.Arrays;

public class TOPTW {
    private int nodes;
    private double[] x;
    private double[] y;
    private double[] score;
    private double[] readyTime;
    private double[] dueTime;
    private double[] serviceTime;
    private int vehicles;
    private int depots;
    private double maxTimePerRoute;
    private double maxRoutes;
    private double[][] distanceMatrix;

    public TOPTW(int nodes, int routes) {
        this.nodes = nodes;
        this.depots = 0;
        this.x = new double[this.nodes + 1];
        this.y = new double[this.nodes + 1];
        this.score = new double[this.nodes + 1];
        this.readyTime = new double[this.nodes + 1];
        this.dueTime = new double[this.nodes + 1];
        this.serviceTime = new double[this.nodes + 1];
        this.distanceMatrix = new double[this.nodes + 1][this.nodes + 1];
        for (int i = 0; i < this.nodes + 1; i++) {
            for (int j = 0; j < this.nodes + 1; j++) {
                this.distanceMatrix[i][j] = 0.0;
            }
        }
        this.maxRoutes = routes;
        this.vehicles = routes;
    }

    public boolean isDepot(int a) {
        if(a > this.nodes) {
            return true;
        }
        return false;
    }

    public double getDistance(int[] route) {
        double distance = 0.0;
        for (int i = 0; i < route.length - 1; i++) {
            int node1 = route[i];
            int node2 = route[i + 1];
            distance += this.getDistance(node1, node2);
        }
        return distance;
    }

    public double getDistance(ArrayList<Integer> route) {
        double distance = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            int node1 = route.get(i);
            int node2 = route.get(i + 1);
            distance += this.getDistance(node1, node2);
        }
        return distance;
    }

    public double getDistance(ArrayList<Integer>[] routes) {
        double distance = 0.0;
        for (ArrayList<Integer> route : routes) {
            distance += this.getDistance(route);
        }
        return distance;
    }


    public void calculateDistanceMatrix() {
        for (int i = 0; i < this.nodes + 1; i++) {
            for (int j = 0; j < this.nodes + 1; j++) {
                if (i != j) {
                    double diffXs = this.x[i] - this.x[j];
                    double diffYs = this.y[i] - this.y[j];
                    this.distanceMatrix[i][j] = Math.sqrt(diffXs * diffXs + diffYs * diffYs);
                    this.distanceMatrix[j][i] = this.distanceMatrix[i][j];
                } else {
                    this.distanceMatrix[i][j] = 0.0;
                }
            }
        }
    }

    public double getMaxTimePerRoute() {
        return maxTimePerRoute;
    }

    public void setMaxTimePerRoute(double maxTimePerRoute) {
        this.maxTimePerRoute = maxTimePerRoute;
    }

    public double getMaxRoutes() {
        return maxRoutes;
    }

    public void setMaxRoutes(double maxRoutes) {
        this.maxRoutes = maxRoutes;
    }

    public int getPOIs() {
        return this.nodes;
    }

    public double getDistance(int i, int j) {
        if(this.isDepot(i)) { i=0; }
        if(this.isDepot(j)) { j=0; }
        return this.distanceMatrix[i][j];
    }

    public double getTime(int i, int j) {
        if(this.isDepot(i)) { i=0; }
        if(this.isDepot(j)) { j=0; }
        return this.distanceMatrix[i][j];
    }

    public int getNodes() {
        return this.nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public double getX(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.x[index];
    }

    public void setX(int index, double x) {
        this.x[index] = x;
    }

    public double getY(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.y[index];
    }

    public void setY(int index, double y) {
        this.y[index] = y;
    }

    public double getScore(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.score[index];
    }

    public double[] getScore() {
        return this.score;
    }

    public void setScore(int index, double score) {
        this.score[index] = score;
    }

    public double getReadyTime(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.readyTime[index];
    }

    public void setReadyTime(int index, double readyTime) {
        this.readyTime[index] = readyTime;
    }

    public double getDueTime(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.dueTime[index];
    }

    public void setDueTime(int index, double dueTime) {
        this.dueTime[index] = dueTime;
    }

    public double getServiceTime(int index) {
        if(this.isDepot(index)) { index=0; }
        return this.serviceTime[index];
    }

    public void setServiceTime(int index, double serviceTime) {
        this.serviceTime[index] = serviceTime;
    }

    public int getVehicles() {
        return this.vehicles;
    }

    @Override
    public String toString() {
        final int COLUMN_WIDTH = 15;
        String text = "Nodes: " + this.nodes + "\n";
        String[] strings = new String[]{"CUST NO.", "XCOORD.", "YCOORD.", "SCORE", "READY TIME", "DUE DATE", "SERVICE TIME"};
        int[] width = new int[strings.length];
        Arrays.fill(width, COLUMN_WIDTH);
        text += ExpositoUtilities.getFormat(strings, width) + "\n";
        for (int i = 0; i < this.nodes; i++) {
            strings = new String[strings.length];
            int index = 0;
            strings[index++] = "" + i;
            strings[index++] = "" + this.x[i];
            strings[index++] = "" + this.y[i];
            strings[index++] = "" + this.score[i];
            strings[index++] = "" + this.readyTime[i];
            strings[index++] = "" + this.dueTime[i];
            strings[index++] = "" + this.serviceTime[i];
            text += ExpositoUtilities.getFormat(strings, width);
            text += "\n";
        }
        text += "Vehicles: " + this.vehicles + "\n";
        strings = new String[]{"VEHICLE", "CAPACITY"};
        width = new int[strings.length];
        Arrays.fill(width, COLUMN_WIDTH);
        text += ExpositoUtilities.getFormat(strings, width) + "\n";
        return text;
    }

    public int addNode() {
        this.nodes++;
        return this.nodes;
    }

    public int addNodeDepot() {
        this.depots++;
        return this.depots;
    }

	/**
	 * @param vehicles the vehicles to set
	 */
	public void setVehicles(int vehicles) {
		this.vehicles = vehicles;
	}


}
