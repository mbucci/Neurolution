/*
 * Implements a Perceptron Network
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 4/13/15
 * Last Modified: 5/10/15
 * 
 */

import java.util.*;

public class Perceptron
{
    private static final double WEIGHT_HIGH = 0.5;
    private static final double WEIGHT_OFF = 0.5;

    protected static final double SIGMOID_CONSTANT = 0.0;
    protected static final int OUT_NODES = 10;

    private Random rand;
    private Map<Integer, List<Edge>> network;    //Perceptron data structure
    private int biasNodeID;                      //ID of the bias node

   
    /**
     * Constructor. If newNet is true, weights will be a high for the weigth range, otherwise it will
     * be specific values for the edge weights. 
     */ 
    public Perceptron() {
        this.network = new HashMap<Integer, List<Edge>>();
        this.rand = new Random();
    }

    //Use this when initializing from scratch
    public void initWeights(int numInput) {
        this.initWeights(numInput, null);
    }

    //Use this to change the weights of an existing network
    public void changeWeights(int numInput, double[] weights) {
        this.initWeights(numInput, weights, false);
    }

    //Don't use this
    public void initWeights(int numInput, double[] weights, boolean newNet) {

        if (newNet) this.biasNodeID = rand.nextInt(numInput);

        for (int i = 0; i < numInput; i++) {
            
            List<Edge> edgeList = new ArrayList<Edge>();
            for (int j = 0; j < OUT_NODES; j++) {
                double value;
                if (weights == null) value = getRandomWeight();      //initialize from weight ranges
                else value = weights[OUT_NODES*i + j];

                if (!newNet) {
                    this.setWeight(i, j, value);
                    return;
                }

                Edge newEdge = new Edge(i, j, value);
                edgeList.add(newEdge);
            }
            this.network.put(i, edgeList);
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

    public void initWeights(int numInput, double[] weights) {
        
        this.biasNodeID = rand.nextInt(numInput);
        for (int i = 0; i < numInput; i++) {
            
            List<Edge> edgeList = new ArrayList<Edge>();
            for (int j = 0; j < OUT_NODES; j++) {
                double value;
                if (weights == null) {
                    //do weight initiliazation from weight ranges   
                    value = this.rand.nextDouble() * WEIGHT_HIGH;
                    value -= WEIGHT_OFF;
                } else value = weights[OUT_NODES*i + j];

                Edge newEdge = new Edge(i, j, value);
                edgeList.add(newEdge);
            }
            this.network.put(i, edgeList);
        }
    }

    public double getRandomWeight() {
        double value = this.rand.nextDouble() * WEIGHT_HIGH;
        value -= WEIGHT_OFF;
        return value;
    }

    private void setWeight(int inID, int outID, double newWeight) {
        this.network.get(inID).get(outID).setWeight(newWeight);
    }
}
