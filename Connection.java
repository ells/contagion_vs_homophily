package com.salathegroup.contagion_vs_homophily;

public class Connection {
    private Boolean rewire;
    private Boolean assortedRewired;
    private Person source;
    private Person destination;
    private int id;

    

    public Connection(int id, Person source, Person destination) {
        this.id = id;
        this.destination = destination;
        this.source = source;
        this.rewire = false;
        this.assortedRewired = false;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setRewire() {
        this.rewire = true;
    }

    public Boolean getRewire() {
        return this.rewire;
    }

    public void setAssortedRewired() {
        this.assortedRewired = true;
    }

    public Boolean getAssortedRewired() {
        return this.assortedRewired;
    }

    public int getID() {
        return this.id;
    }

    public Person getDestination() {
        return this.destination;
    }

    public Person getSource() {
        return this.source;
    }




}