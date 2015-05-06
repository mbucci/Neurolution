/**
 * Implements a Perceptron Neural Network
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 4/13/15
 * Last Modified: 4/13/15
 * 
 */

import java.util.*;

public class NeuralNetwork extends Perceptron
{   
    //Perceptron constants
    private static final double SIGMOID_CONSTANT = 0.0;
    private static final double WEIGHT_HIGH = 0.3;
    private static final double WEIGHT_OFFSET = WEIGHT_HIGH / 2.0;
    private static final int OUT_NODES = 10;

    //Algorithm specific variables
    private int inputNodes;
    
    //Keeps track of performance
    private int numCorrect;
    
    /**
     * Constructor
     */
    public NeuralNetwork(int numInputs) {
        this(numInputs, null);
    }
    
    //Maintain N input nodes for each attribute to increase diversity
    public NeuralNetwork(int numInputs, double[] initialWeights) {
        super();
        this.inputNodes = numInputs;
        if (initialWeights == null) super.initWeights(numInputs, OUT_NODES);
        else super.initWeights(numInputs, OUT_NODES, initialWeights);
    }
    
    
    //Main function for NN. Runs perceptron NN on a given problem
    public int run(Problem prob) {
        
        this.numCorrect = 0;
        ListIterator<Clause> lit = prob.getIterator();
        while (lit.hasNext()) {
            double[] target = new double[OUT_NODES];
            double[] output = new double[OUT_NODES];
            
            Clause temp = lit.next();
            target[temp.getQuality()] = 1;
            
            for (int oID = 0; oID < OUT_NODES; oID++) {
                
                //**********Calculate sum of weighted inputs**********//
                double weightedInputs = 0.0;
                int iID = 0;
                for (Double val : temp.getAttributes()) {
                    weightedInputs += super.getWeightedInput(iID, oID, val);
                    iID++;
                }
                
                //**********Calculate error and output value**********//
                output[oID] = calculateActivation(weightedInputs);
                double error = calculateError(oID, output[oID], target);
            }
            calculateResults(output, target);
        }
        return this.numCorrect;
    }
    
    
    // Calculates activation function for inputs. Derivative is g(in) * (1 - g(in))
    private double calculateActivation(double input) {
        double denominator = 1 + Math.exp(SIGMOID_CONSTANT - input);
        return 1.0 / denominator;
    }
    
    
    //Calculate error for a given output node, output value and target value.
    private double calculateError(int outID, double outVal, double[] target) {
        return target[outID] - outVal;
    }
    
    
    //Calculate the results of a given problem clause. Keeps track of total correctly 
    //estimated clauses. 
    public void calculateResults(double[] output, double[] target) {

        double currHigh = 0.0;
        int highIndex = 0;
        for (int i = 0; i < target.length; i++) {
            if (output[i] > currHigh) {
                currHigh = output[i];
                highIndex = i;
            }
        }
        if (target[highIndex] == 1.0) this.numCorrect++;
    }
}