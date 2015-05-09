/**
 * Implements a Perceptron Network
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 4/13/15
 * Last Modified: 4/13/15
 * 
 */

import java.util.*;


public class NeurNet
{
    private static final double WEIGHT_HIGH = 0.3;
    private static final double WEIGHT_OFF = 0.15;

    private Map<Integer, List<Edge>> hiddenLayer;   // Hidden nodes and their input edges
    private Map<Integer, List<Edge>> outputLayer;   // Output nodes and their input edges

    private Random rand;
    // private Map<Integer, List<Edge>> network;    //Perceptron data structure
    private int biasNodeID;                      //ID of the bias node
    private int numHiddenNodes;                  // Number of hidden neurons

   
    /**
     * Constructor. If newNet is true, weights will be a high for the weigth range, otherwise it will
     * be specific values for the edge weights. 
     */ 
    public Network() {
        this.hiddenLayer = new HashMap<Integer, List<Edge>>();
        this.outputLayer = new HashMap<Integer, List<Edge>>();
        this.rand = new Random();
    }

    public void initWeights(int numInput, int numOutput) {
        this.initWeights(numInput, numOutput, null);
    }

    public void initWeights(int numInput, int numOutput, double[] weights) {
        // Set the number of hidden nodes to be avg of numInput and numOutput
        numHiddenNodes = numInput + numOutput / 2; 
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
                    value = this.rand.nextDouble() * WEIGHT_HIGH;
                    value -= WEIGHT_OFF;
                } else {
                    // Get the correct index frm the list of weights
                    // by calculating the offset; for every input node,
                    // there will be numHiddenNodes edges; effectively
                    // divides double[] weights into small subarrays/chunks
                    int index = numHiddenNodes*i + j;
                    value = weights[index];
                    count++;
                }
                Edge newEdge = new Edge(i, j, value);
                edgeList.add(newEdge);
            }
            this.hiddenLayer.put(i, edgeList);
        }

        // Then initialize weights between hidden and output layers
        for (int i = 0; i < numHiddenNodes; i++) {
            List<Edge> edgeList = new ArrayList<Edge>();
            for (int j = 0; j < numOutput; j++) {
                double value;
                if (weights == null) {
                    //do weight initiliazation from weight ranges   
                    value = this.rand.nextDouble() * WEIGHT_HIGH;
                    value -= WEIGHT_OFF;
                } else {
                    // Get the correct index frm the list of weights
                    // by calculating the offset; for every input node,
                    // there will be numOutput edges; effectively
                    // divides double[] weights into small subarrays/chunks
                    // Count is the offset @TODO doublecheck count value!
                    int index = numOutput*i + j;
                    value = weights[count + index];
                }
                Edge newEdge = new Edge(i, j, value);
                edgeList.add(newEdge);
            }
            this.outputLayer.put(i, edgeList);
        }

        
    }
    
    //Given an input node ID, output node ID and input value, calculates the weighted input for
    //that edge
    public double getWeightedInput(int inID, int outID, double inVal) {
        // bias node always has an input value of 1
        if (inID == this.biasNodeID) inVal = 1.0;
        return getWeight(inID, outID) * inVal;
    }
    
    public double getWeight(int inID, int outID) {
        return this.network.get(inID).get(outID).getWeight();
    }
}
