package com.salathegroup.contagion_vs_homophily;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

import java.util.Random;
import java.util.Set;

public class Simulation {
    Random random = new Random();

    Graph<Person, Connection> assortedG;
    Person[] assortedPeople;
    Connection[] assortedConnections;

    Graph<Person, Connection> randomG;
    Person[] randomPeople;
    Connection[] randomConnections;

    int socialTimestep = 0;
    boolean opinionIsSpreading = false;
    int numberOfAdopters = 0;


    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        simulation.run();
    }

    public void run() {
        this.initGraph();
        this.runSocialTimesteps(this.assortedG, this.assortedPeople, this.assortedConnections);
        System.out.println(this.socialTimestep + "//" + this.numberOfAdopters);

        this.generateControlGraph(this.assortedG);
        this.runSocialTimesteps(this.randomG, this.randomPeople, this.randomConnections);
        System.out.println(this.socialTimestep + "//" + this.numberOfAdopters);

    }

    private void initGraph() {
        Set components;
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        int k = SimulationSettings.getInstance().getK();
        int ySpan = SimulationSettings.getInstance().getYspan();
        this.assortedPeople = new Person[numberOfPeople];
        do {
            this.assortedG = new SparseGraph<Person, Connection>();
            for (int i = 0; i < numberOfPeople; i++) {
                Person person = new Person(Integer.toString(i), this.random.nextInt(ySpan));
                this.assortedPeople[i] = person;
                this.assortedG.addVertex(person);
            }
            for (int i = 0; i < numberOfPeople; i++) {
                for (int ii = 0; ii < k; ii++) {
                    int diff = ii/2 + 1;
                    if (ii%2 == 1) diff *= -1;
                    int newIndex = i + diff;
                    if (newIndex < 0) newIndex += numberOfPeople;
                    if (newIndex >= numberOfPeople) newIndex -= numberOfPeople;
                    this.assortedG.addEdge(new Connection(0, this.assortedPeople[i], this.assortedPeople[newIndex]), this.assortedPeople[i], this.assortedPeople[newIndex]);    //connection ID set to zero, assigned in the next loop
                }
            }
            int edgeCounter = 0;
            this.assortedConnections = new Connection[this.assortedG.getEdgeCount()];
            for (Connection edge:this.assortedG.getEdges()) {
                if (this.random.nextDouble() < SimulationSettings.getInstance().getRewiringProbability()) {
                    Person source = this.assortedG.getEndpoints(edge).getFirst();
                    Person destination = this.assortedG.getEndpoints(edge).getSecond();
                    int sourceY = source.getY();
                    int destinationY = source.getY();
                    Person newDestination;
                    do {
                        newDestination = this.assortedPeople[this.random.nextInt(numberOfPeople)];
                        if (this.random.nextDouble() < SimulationSettings.getInstance().getAssortativity()) {
                            for (Person newAssortedDestination:this.assortedG.getVertices()) {
                                int newDestinationY = newAssortedDestination.getY();
                                if (newDestinationY != sourceY) continue;
                                else {
                                    newDestination = newAssortedDestination;
                                    break;
                                }
                            }
                        }
                    }
                    while (this.assortedG.isNeighbor(source,newDestination) || source.equals(newDestination));
                    this.assortedG.removeEdge(edge);
                    this.assortedG.addEdge(new Connection(edgeCounter, source, newDestination), source, newDestination);
                    this.assortedG.findEdge(source, newDestination).setRewire();
                    edgeCounter++;
                }
                // assortedConnections array is populated AFTER rewiring
                this.assortedConnections[edgeCounter] = new Connection(edgeCounter, this.assortedG.getSource(edge), this.assortedG.getDest(edge));
            }
            WeakComponentClusterer wcc = new WeakComponentClusterer();
            components = wcc.transform(this.assortedG);
        }
        while (components.size() > 1);


    }

    public void generateControlGraph(Graph<Person, Connection> assortedGraph) {
        this.randomG = new SparseGraph<Person, Connection>();
        for (Person person:assortedG.getVertices()) {
            this.randomG.addVertex(person);
        }
        for (Connection connection:this.assortedG.getEdges()) {
            this.randomG.addEdge(connection, this.assortedG.getIncidentVertices(connection));
        }
        int counter = 0;
        this.randomPeople = new Person[SimulationSettings.getInstance().getNumberOfPeople()];
        for (Person person:this.randomG.getVertices()) {
            person.setAdopted(false);
            person.resetExposures();
            person.resetY(this.random.nextInt(SimulationSettings.getInstance().getYspan()));
            this.randomPeople[counter] = person;
            counter++;
        }
        counter = 0;
        this.randomConnections = new Connection[this.randomG.getEdgeCount()];
        for (Connection connection:this.randomG.getEdges()) {
            this.randomConnections[counter] = connection;
            counter++;
        }
        this.resetSimulation();
    }

    private void runSocialTimesteps(Graph<Person, Connection> g, Person[] people, Connection[] connections) {
        while(true) {
            if (this.socialTimestep==0) this.opinionIsSpreading = true;
            if (this.opinionIsSpreading) {
                this.generalExposure(g, people, connections);
                this.socialContagion(g, people, connections);
                this.adoptionCheck(g, people, connections);
            }
            this.stopSocialTimesteps();
            this.socialTimestep++;
            if (!this.opinionIsSpreading) {
                break;
            }
        }
    }

    public void stopSocialTimesteps() {
        double endThreshold = SimulationSettings.getInstance().getEndThreshold();
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        if (this.numberOfAdopters >= numberOfPeople*endThreshold) {
            this.opinionIsSpreading = false;
        }
    }

    private void generalExposure(Graph<Person, Connection> g, Person[] people, Connection[] connections) {
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        double rge = SimulationSettings.getInstance().getRge();
        int T = SimulationSettings.getInstance().getT();
        double numberOfPeopleToExpose = rge * numberOfPeople;
        while (numberOfPeopleToExpose > 0) {
            if (numberOfPeopleToExpose < 1) {
                if (random.nextDouble() > numberOfPeopleToExpose) break;
            }
            Person nextExposure = people[random.nextInt(numberOfPeople)];
            if (nextExposure.getNumberOfExposures() < T)  {
                nextExposure.increaseGeneralExposures(Integer.MAX_VALUE, this.socialTimestep);
            }
            numberOfPeopleToExpose--;
        }
    }

    private void socialContagion(Graph<Person, Connection> g, Person[] people, Connection[] connections) {
        double omega = SimulationSettings.getInstance().getOmega();
        for (Person person:people) {
            if (omega == 0) continue;
            if (person.getAdopted() == false) continue;
            for (Person neighbour:g.getNeighbors(person)) {
                if (neighbour.getAdopted()==false) {
                    if (person.getExposureList().contains(neighbour.getIntID())) continue;
                    else if (this.random.nextDouble() < omega) {
                        if (person.getExposureList().size() < SimulationSettings.getInstance().getT())  {
                            person.increaseGeneralExposures(neighbour.getIntID(), this.socialTimestep);
                            //TODO social exposure type tracking
                        }
                    }
                }
            }
        }
    }

    private void adoptionCheck(Graph<Person, Connection> g, Person[] people, Connection[] connections) {
        int T = SimulationSettings.getInstance().getT();
        for (Person person:g.getVertices()) {
            if ((person.getNumberOfExposures() >= T) && (random.nextInt(SimulationSettings.getInstance().getYspan()) < person.getY())) {
                person.setTempValue(true);
            }
        }
        for (Person person:g.getVertices()) {
            if (person.getTempValue()) {
                person.setTempValue(false);
                this.setAdoption(person);
            }
        }
    }

    private void setAdoption(Person person) {
        if (this.opinionIsSpreading ) {
            if (person.getAdopted() == true) return;
            person.setAdopted(true);
            this.numberOfAdopters++;
        }
    }

    public void resetSimulation() {
        this.numberOfAdopters = 0;
        this.socialTimestep = 0;
    }



}
