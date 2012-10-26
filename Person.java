package com.salathegroup.contagion_vs_homophily;

import java.util.ArrayList;

public class Person {

    private String id;
    private int y;
    private boolean tempValue = false;
    private boolean adopted;
    private int adoptionCause;
    private ArrayList<Integer> exposures;
    private ArrayList<Integer> exposureTimestamps;
    public static final int NONE = 1 ;
    public static final int SOCIAL = 2;
    public static final int GEN_FIRST = 3;
    public static final int SOC_FIRST = 4;
    public static final int GENERAL = 5 ;

    public Person(String id, int y) {
        this.id = id;
        this.y = y;
        this.adopted = false;
        this.adoptionCause = Person.NONE;
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

    public void setAdoptionCause(int adoptionCause) {
        this.adoptionCause = adoptionCause;
    }

    public int getAdoptionCause() {
        return this.adoptionCause;
    }

    public boolean isNONE() {
        return this.adoptionCause==Person.NONE;
    }

    public boolean isSOCIAL() {
        return this.adoptionCause==Person.SOCIAL;
    }

    public boolean isGENERAL() {
        return this.adoptionCause==Person.GENERAL;
    }

    public boolean isGEN_FIRST() {
        return this.adoptionCause==Person.GEN_FIRST;
    }

    public boolean isSOC_FIRST() {
        return this.adoptionCause==Person.SOC_FIRST;
    }
}