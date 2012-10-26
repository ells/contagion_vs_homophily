package com.salathegroup.contagion_vs_homophily;

import java.util.ArrayList;

public class Person {

    private String id;
    private int y;
    private boolean tempValue = false;
    private boolean adopted;
    private ArrayList<Integer> exposures;
    private ArrayList<Integer> exposureTimestamps;

    public Person(String id, int y) {
        this.id = id;
        this.y = y;
        this.adopted = false;
        this.exposures = new ArrayList<Integer>();
        this.exposureTimestamps = new ArrayList<Integer>();
    }

    public int getY() {
        return this.y;
    }

    public boolean getAdopted() {
        return this.adopted;
    }

    public void setAdopted(boolean adopted) {
        this.adopted = adopted;
    }

    public void resetY(int y) {
        this.y = y;
    }

    public void resetExposures() {
        ArrayList<Integer> exposures;
        ArrayList<Integer> exposureTimestamps;

        exposures = new ArrayList<Integer>();
        exposureTimestamps = new ArrayList<Integer>();

        this.exposures = exposures;
        this.exposureTimestamps = exposureTimestamps;
    }

    public String toString() {
        return this.id;
    }

    public String getID() {
        return this.id;
    }

    public Integer getIntID() {
        return Integer.parseInt(this.id);
    }

    public int getNumberOfExposures() {
        return this.exposures.size();
    }

    public ArrayList<Integer> getExposureList() {
        return this.exposures;
    }

    public void increaseGeneralExposures(Integer exposureSource, Integer exposureTimestamp) {
        this.exposures.add(exposureSource);
        this.exposureTimestamps.add(exposureTimestamp);
    }

    public ArrayList<Integer> getExposureTimestamps() {
        return this.exposureTimestamps;
    }

    public void setTempValue(boolean b) {
        this.tempValue = b;
    }

    public boolean getTempValue() {
        return this.tempValue;
    }
}