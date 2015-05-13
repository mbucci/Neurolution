/*
 * Implements a layered Network
 * 
 * Nikki Morin, Max Bucci, Megan Maher
 * Created: 4/13/15
 * Last Modified: 5/10/15
 * 
 */

import java.util.*;

public class LayeredNetwork extends NetworkLayer
{
    private NetworkLayer hiddenLayer;   // Hidden nodes and their input edges
    private NetworkLayer outputLayer;   // Output nodes and their input edges

    private Random rand;
    private int biasNodeID;                         // ID of the bias node
    private int numHiddenNodes;                     // Number of hidden neurons
   

    public LayeredNetwork() {
        this.hiddenLayer = new NetworkLayer();
        this.outputLayer = new NetworkLayer();
        this.rand = new Random();
    }


    @Override public void initWeights(int numInput) {
        this.initWeights(numInput, null, true);
    }

    @Override public void initWeights(int numInput, double[] weights) {
        this.initWeights(numInput, weights, true);
    }

    @Override public void changeWeights(int numInput, double[] weights) {
        initWeights(numInput, weights, false);
    }

    @Override public void initWeights(int numInput, double[] weights, boolean newNet) {
        
        this.biasNodeID = rand.nextInt(numInput);
        // Set the number of hidden nodes to be avg of numInput and OUT_NODES
        numHiddenNodes = (numInput + OUT_NODES) / 2; 

        //Initialize hidden and output layers
        this.hiddenLayer.initWeights(numInput, numHiddenNodes, weights, newNet);
        this.outputLayer.initWeights(numHiddenNodes, weights, newNet);
    }
    

    //Given an input node ID, output node ID and input value, calculates the weighted input for
    //that edge
    public double getWeightedInput(int inID, int outID, double inVal, String layer) {
        // bias node always has an input value of 1
        double weight;
        if (layer.equals("hidden")) {
            if (inID == this.biasNodeID) inVal = 1.0;
            weight = this.hiddenLayer.getWeight(inID, outID);
        } else {
            weight = this.outputLayer.getWeight(inID, outID);
        }
        return weight * inVal;
    }
    
    public int getNumHiddenNodes() { return this.numHiddenNodes; }
}
