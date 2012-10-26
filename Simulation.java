package com.salathegroup.contagion_vs_homophily;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import java.util.ArrayList;
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

    ArrayList<Person> nonAdopters = new ArrayList<Person>();
    ArrayList<Person> tempAdopters = new ArrayList<Person>();
    ArrayList<Person> adopters = new ArrayList<Person>();


    boolean opinionIsSpreading = false;
    int socialTimestep = 0;
    int numberOfAdopters = 0;

    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        simulation.run();
    }

    public void run() {
        this.initGraph();
        this.runSocialTimesteps(this.assortedG, this.assortedPeople, this.assortedConnections);
        this.generateControlGraph(this.assortedG);
        this.runSocialTimesteps(this.randomG, this.randomPeople, this.randomConnections);
    }

    private void initGraph() {
        Set components;
        int k = SimulationSettings.getInstance().getK();
        int ySpan = SimulationSettings.getInstance().getYspan();
        double assortativity = SimulationSettings.getInstance().getAssortativity();
        double rewireProbability = SimulationSettings.getInstance().getRewiringProbability();
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        this.assortedPeople = new Person[numberOfPeople];
        do {
            this.assortedG = new SparseGraph<Person, Connection>();
            for (int i = 0; i < numberOfPeople; i++) {
                Person person = new Person(Integer.toString(i), this.random.nextInt(ySpan));
                this.assortedPeople[i] = person;
                this.nonAdopters.add(person);
                this.assortedG.addVertex(person);
            }
            for (int i = 0; i < numberOfPeople; i++) {
                for (int ii = 0; ii < k; ii++) {
                    int diff = ii/2 + 1;
                    if (ii%2 == 1) diff *= -1;
                    int newIndex = i + diff;
                    if (newIndex < 0) newIndex += numberOfPeople;
                    if (newIndex >= numberOfPeople) newIndex -= numberOfPeople;
                    this.assortedG.addEdge(new Connection(0, this.assortedPeople[i], this.assortedPeople[newIndex]), this.assortedPeople[i], this.assortedPeople[newIndex]);
                }
            }
            int edgeCounter = 0;
            this.assortedConnections = new Connection[this.assortedG.getEdgeCount()];
            for (Connection edge:this.assortedG.getEdges()) {
                if (this.random.nextDouble() < rewireProbability) {
                    Person source = this.assortedG.getEndpoints(edge).getFirst();
                    int sourceY = source.getY();
                    Person newDestination;
                    do {
                        newDestination = this.assortedPeople[this.random.nextInt(numberOfPeople)];
                        if (this.random.nextDouble() < assortativity) {
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

    private void runSocialTimesteps(Graph<Person, Connection> g, Person[] people, Connection[] connections) {
        double endThreshold = SimulationSettings.getInstance().getEndThreshold();
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        while(this.numberOfAdopters < numberOfPeople*endThreshold) {
            //System.out.println("@t=" + this.socialTimestep + ":  " + this.nonAdopters.size() + " --> " + this.adopters.size());
            if (this.socialTimestep==0) this.opinionIsSpreading = true;
            if (this.opinionIsSpreading) {
                this.generalExposure(g, people, connections);
                this.socialContagion(g, people, connections);
                this.adoptionCheck(g, people, connections);
            }
            this.socialTimestep++;
        }
        this.opinionIsSpreading = false;
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
        int T = SimulationSettings.getInstance().getT();
        if (omega == 0) return;
        for (Person person:this.adopters) {
            for (Person neighbour:g.getNeighbors(person)) {
                if (neighbour.getAdopted()==false) {
                    if (person.getExposureList().contains(neighbour.getIntID())) continue;
                    else if (this.random.nextDouble() < omega) {
                        if (person.getExposureList().size() < T)  {
                            person.increaseGeneralExposures(neighbour.getIntID(), this.socialTimestep);
                        }
                    }
                }
            }
        }
    }

    private void adoptionCheck(Graph<Person, Connection> g, Person[] people, Connection[] connections) {
        int T = SimulationSettings.getInstance().getT();
        int ySpan = SimulationSettings.getInstance().getYspan();
        for (Person person:this.nonAdopters) {
            if ((person.getNumberOfExposures() >= T) && (this.random.nextInt(ySpan) < person.getY())) {
                this.tempAdopters.add(person);
                person.setTempValue(true);
            }
        }
        for (Person person:this.tempAdopters) {
            if (person.getTempValue()) {
                person.setTempValue(false);
                this.setAdoption(person);
                this.assignCauseOfAdoption(person);
            }
        }
        this.tempAdopters.clear();
    }

    private void setAdoption(Person person) {
        if (this.opinionIsSpreading ) {
            if (person.getAdopted() == true) return;
            person.setAdopted(true);
            this.nonAdopters.remove(person);
            this.adopters.add(person);
            this.numberOfAdopters++;
        }
    }

    private void assignCauseOfAdoption(Person person) {
        int T = SimulationSettings.getInstance().getT();
        int idThreshold = SimulationSettings.getInstance().getNumberOfPeople();
        if (person.getAdopted()==false) return;
        if (T == 2) {
            int exposerONE = person.getExposureList().get(0);
            int exposerTWO = person.getExposureList().get(1);
            if (exposerONE > idThreshold && exposerTWO > idThreshold) {
                person.setAdoptionCause(Person.GENERAL);
            }
            if (exposerONE > idThreshold && exposerTWO < idThreshold) {
                person.setAdoptionCause(Person.GEN_FIRST);
            }
            if (exposerONE < idThreshold && exposerTWO > idThreshold) {
                person.setAdoptionCause(Person.SOC_FIRST);
            }
            if (exposerONE < idThreshold && exposerTWO < idThreshold) {
                person.setAdoptionCause(Person.SOCIAL);
            }
        }
        if (T==1) {
            int exposer = person.getExposureList().get(0);
            if (exposer > idThreshold) {
                person.setAdoptionCause(Person.GENERAL);
            }
            if (exposer < idThreshold ) {
                person.setAdoptionCause(Person.SOCIAL);
            }
        }
    }

    public void generateControlGraph(Graph<Person, Connection> assortedGraph) {
        this.numberOfAdopters = 0;
        this.socialTimestep = 0;
        this.nonAdopters.clear();
        this.tempAdopters.clear();
        this.adopters.clear();

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
            this.nonAdopters.add(person);
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
    }
}
