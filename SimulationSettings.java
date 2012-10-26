package com.salathegroup.contagion_vs_homophily;


public class SimulationSettings {

    private static SimulationSettings ourInstance = new SimulationSettings();
    private int numberOfPeople = 5000;
    private int k = 10;
    private double rewiringProbability = 0.10;
    private double rge = 0.001;
    private int T = 2;
    private double omega = 0.01;
    private int ySpan = 1000;
    private double assortativity = 0.5;
    private double endThreshold = 0.995;

    public static SimulationSettings getInstance() {
        return ourInstance;
    }

    private SimulationSettings() {
    }

    public double getEndThreshold() {
        return endThreshold;
    }

    public void setEndThreshold(double endThreshold) {
        this.endThreshold = endThreshold;
    }

    public double getAssortativity() {
        return assortativity;
    }

    public void setAssortativity(double assortativity) {
        this.assortativity = assortativity;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public int getYspan() {
        return ySpan;
    }

    public void setYspan(int ySpan) {
        this.ySpan = ySpan;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public double getRewiringProbability() {
        return rewiringProbability;
    }

    public void setRewiringProbability(double rewiringProbability) {
        this.rewiringProbability = rewiringProbability;
    }

    public double getRge() {
        return rge;
    }

    public void setRge(double rge) {
        this.rge = rge;
    }

    public int getT() {
        return T;
    }

    public void setT(int t) {
        T = t;
    }

    public double getOmega() {
        return omega;
    }

    public void setOmega(double omega) {
        this.omega = omega;
    }
}
