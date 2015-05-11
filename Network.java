/*
 * Implements a layered Network
 * 
 * Nikki Morin, Max Bucci, Megan Maher
 * Created: 4/13/15
 * Last Modified: 5/10/15
 * 
 */

import java.util.*;

public class Network
{
    private static final double WEIGHT_HIGH = 0.5;
    private static final double WEIGHT_OFF = WEIGHT_HIGH/2.0;

    protected static final double SIGMOID_CONSTANT = 0.0;
    protected static final int OUT_NODES = 10;

    private Map<Integer, List<Edge>> hiddenLayer;   // Hidden nodes and their input edges
    private Map<Integer, List<Edge>> outputLayer;   // Output nodes and their input edges

    private Random rand;
    private int biasNodeID;                         //ID of the bias node
    private int numHiddenNodes;                     // Number of hidden neurons
   
    public Network() {
        this.hiddenLayer = new HashMap<Integer, List<Edge>>();
        this.outputLayer = new HashMap<Integer, List<Edge>>();
        this.rand = new Random();
    }

    public void initWeights(int numInput) {
        this.initWeights(numInput, null, true);
    }

    public void changeWeights(int numInput, double[] weights) {
        initWeights(numInput, weights, false);
    }

    public void initWeights(int numInput, double[] weights, boolean newNet) {
        // Set the number of hidden nodes to be avg of numInput and OUT_NODES
        numHiddenNodes = (numInput + OUT_NODES) / 2; 
        // First init weights between input and hidden layers
        // Then init weights between hidden and output layers

        // Test weight initiations: genetic chromosome is either 
        // an array of every single weight in the network,
        // or else genetic chromosome is a set of input weights for a node

        this.biasNodeID = rand.nextInt(numInput);
        int count = 0;      // Keeps track of index of array

        // First initialize weights between hidden and input layers
        for (int i = 0; i < numInput; i++) {
            List<Edge> edgeList = new ArrayList<Edge>();
            for (int j = 0; j < numHiddenNodes; j++) {
                double value;
                if (weights == null) {
                    //do weight initiliazation from weight ranges   
                    value = getRandomWeight();
                } else {
                    // Get the correct index frm the list of weights
                    // by calculating the offset; for every input node,
                    // there will be numHiddenNodes edges; effectively
                    // divides double[] weights into small subarrays/chunks
                    int index = numHiddenNodes*i + j;
                    value = weights[index];
                    count++;
                }
                if (newNet) {
                    // Make new edge
                    Edge newEdge = new Edge(i, j, value);
                    edgeList.add(newEdge);
                } else {
                    // Get the corresponding edgelist and edge
                    hiddenLayer.get(i).get(j).setWeight(value);
                }
                
            } 
            if (newNet) { this.hiddenLayer.put(i, edgeList); }
        }

        // Then initialize weights between hidden and output layers
        for (int i = 0; i < numHiddenNodes; i++) {
            List<Edge> edgeList = new ArrayList<Edge>();
            for (int j = 0; j < OUT_NODES; j++) {
                double value;
                if (weights == null) {
                    //do weight initiliazation from weight ranges   
                    value = getRandomWeight();
                } else {
                    // Get the correct index frm the list of weights
                    // by calculating the offset; for every input node,
                    // there will be OUT_NODES edges; effectively
                    // divides double[] weights into small subarrays/chunks
                    // Count is the offset @TODO doublecheck count value!
                    int index = OUT_NODES*i + j;
                    value = weights[count + index];
                }
                if (newNet) {
                    // Make new edge
                    Edge newEdge = new Edge(i, j, value);
                    edgeList.add(newEdge);
                } else {
                    // Get the corresponding edgelist and edge
                    outputLayer.get(i).get(j).setWeight(value);
                }
            }
            if (newNet) { this.outputLayer.put(i, edgeList); }
        }
    }
    
    //Given an input node ID, output node ID and input value, calculates the weighted input for
    //that edge
    public double getWeightedInput(int inID, int outID, double inVal, String layer) {
        // bias node always has an input value of 1
        if (inID == this.biasNodeID) inVal = 1.0;
        double weight;
        if (layer.equals("hidden")) {
            weight = getWeight(inID, outID, "hidden");
        } else {
            weight = getWeight(inID, outID, "output");
        }
        return weight * inVal;
    }
    
    public double getWeight(int inID, int outID, String layer) {
        if (layer.equals("hidden")) { return this.hiddenLayer.get(inID).get(outID).getWeight(); }
        else { return this.outputLayer.get(inID).get(outID).getWeight(); }
    }

    public int getNumHiddenNodes() { return this.numHiddenNodes; }

    public double getRandomWeight() {
        double value = this.rand.nextDouble() * WEIGHT_HIGH;
        value -= WEIGHT_OFF;
        return value;
    }
}
