/*
 * Implements a NetworkLayer Network
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 4/13/15
 * Last Modified: 5/10/15
 * 
 */

import java.util.*;

public class NetworkLayer
{
    protected static final double WEIGHT_HIGH = 0.5;
    protected static final double WEIGHT_OFF = WEIGHT_HIGH/2.0;

    protected static final double SIGMOID_CONSTANT = 0.0;
    protected static final int OUT_NODES = 10;

    protected Random rand;
    protected Map<Integer, List<Edge>> network;    // NetworkLayer data structure
    private int biasNodeID;                        // ID of the bias node

   
    /**
     * Constructor. If newNet is true, weights will be a high for the weigth range, otherwise it will
     * be specific values for the edge weights. 
     */ 
    public NetworkLayer() {
        this.network = new HashMap<Integer, List<Edge>>();
        this.rand = new Random();
    }

    //Use this when initializing from scratch
    public void initWeights(int numInput) {
        this.initWeights(numInput, null, true);
    }

    public void initWeights(int numInput, double[] weights) {
        this.initWeights(numInput, weights, true);
    }

    //Use this to change the weights of an existing network
    public void changeWeights(int numInput, double[] weights) {
        this.initWeights(numInput, weights, false);
    }

    //Don't use this.
    public void initWeights(int numInput, double[] weights, boolean newNet) {
        this.initWeights(numInput, OUT_NODES, weights, newNet);
    }

    //Use this to specify number of output nodes
    public void initWeights(int numInput, int numOutput, double[] weights, boolean newNet) {

        if (newNet) this.biasNodeID = rand.nextInt(numInput);

        for (int i = 0; i < numInput; i++) {
            
            List<Edge> edgeList = new ArrayList<Edge>();
            for (int j = 0; j < numOutput; j++) {
                double value;
                if (weights == null) value = getWeightFromRange();      //initialize from weight ranges
                else value = weights[numOutput*i + j];

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

    public int getNumOutput() { return OUT_NODES; }
    
    protected void setWeight(int inID, int outID, double newWeight) {
        this.network.get(inID).get(outID).setWeight(newWeight);
    }
    protected double getWeight(int inID, int outID) {
        return this.network.get(inID).get(outID).getWeight();
    }

    public double getWeightFromRange() {
        double value = this.rand.nextDouble() * WEIGHT_HIGH;
        value -= WEIGHT_OFF;
        return value;
    }

    //Given an input node ID, output node ID and input value, calculates the weighted input for
    //that edge
    public double getWeightedInput(int inID, int outID, double inVal) {
        // bias node always has an input value of 1
        if (inID == this.biasNodeID) inVal = 1.0;
        return getWeight(inID, outID) * inVal;
    }


    // Calculates activation function for inputs. Derivative is g(in) * (1 - g(in))
    protected double calculateActivation(double input) {
        double denominator = 1 + Math.exp(SIGMOID_CONSTANT - input);
        return 1.0 / denominator;
    }
    
    
    //Calculate error for a given output node, output value and target value.
    protected double calculateError(int outID, double outVal, double[] target) {
        return (target[outID] - outVal)*(target[outID] - outVal);
    }
    
    //Calculate the results of a given problem clause. Keeps track of total correctly 
    //estimated clauses. 
    protected int calculateResults(double[] output, double[] target) {

        boolean correct = false;
        double currHigh = 0.0;
        int highIndex = 0;
        for (int i = 0; i < target.length; i++) {
            if (output[i] > currHigh) {
                currHigh = output[i];
                highIndex = i;
            }
        }
        if ((highIndex >= 1) && (highIndex <= target.length-2)) 
            correct = ((target[highIndex-1] == 1.0) || (target[highIndex+1] == 1.0)) ? true : false;
        else if (highIndex == 0) 
            correct = ((target[highIndex+1] == 1.0) || (target[highIndex+2] == 1.0)) ? true : false;
        else if (highIndex == target.length)
            correct = ((target[highIndex-1] == 1.0) || (target[highIndex-2] == 1.0)) ? true : false;

        if (target[highIndex] == 1.0) correct = true;

        return (correct) ? 1 : 0;
    }
}