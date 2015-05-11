/*
 * Implements a Perceptron Neural Network
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 4/13/15
 * Last Modified: 5/10/15
 * 
 */

import java.util.*;

public class NeuralNetwork extends Perceptron
{   

    private int inputNodes;       //Algorithm specific variables
    public int numCorrect;        //Keeps track of performance
    private double totalError;
    private int numWeights;

    /**
     * Constructor
     */
    public NeuralNetwork(int numAttr) {
        this(numAttr, null);
    }
    

    //Maintain N input nodes for each attribute to increase diversity. N^2 total nodes.
    public NeuralNetwork(int numAttr, double[] initialWeights) {
        super();
        this.inputNodes = (int) Math.pow(numAttr, 2);
        if (initialWeights == null) super.initWeights(this.inputNodes);
        else super.initWeights(this.inputNodes, initialWeights);

        this.numWeights = this.inputNodes * this.inputNodes * OUT_NODES;
        // this.numWeights = this.inputNodes * OUT_NODES;

    }

    //Main function for NN. Runs perceptron NN on a given problem
    public double run(Problem prob) {
        
        this.numCorrect = 0;
        this.totalError = 0.0;
        
        ListIterator<Clause> lit = prob.getIterator();
        while (lit.hasNext()) {
            double[] target = new double[OUT_NODES];
            double[] output = new double[OUT_NODES];
            
            Clause temp = lit.next();
            target[temp.getQuality()] = 1;
            
            for (int oID = 0; oID < OUT_NODES; oID++) {
                
                //**********Calculate sum of weighted inputs**********//
                //Use a given attribute N (number of attribute) times
                double weightedInputs = 0.0;
                int iID = 0;

                for (Double val : temp.getAttributes()) {
                    for (int i = 0; i < prob.getNumAttributes(); i++) {
                        weightedInputs += super.getWeightedInput(iID + i, oID, val);
                    } 
                    iID++;
                }
                
                //**********Calculate error and output value**********//
                output[oID] = calculateActivation(weightedInputs);
                double error = calculateError(oID, output[oID], target);

                // System.out.println(error);
                totalError+=error;
            }
            calculateResults(output, target);
        }
        //return (double)this.numCorrect;
        return totalError;
    }

    // Calculates activation function for inputs. Derivative is g(in) * (1 - g(in))
    private double calculateActivation(double input) {
        double denominator = 1 + Math.exp(SIGMOID_CONSTANT - input);
        return 1.0 / denominator;
    }
    
    
    //Calculate error for a given output node, output value and target value.
    private double calculateError(int outID, double outVal, double[] target) {
        return ((target[outID] - outVal)*(target[outID] - outVal));
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
        if (target[highIndex] == 1.0) { 
            this.numCorrect++;
            // System.out.println("FOUND ONE");
        }
    }

    public void printResults(Problem prob) {
        System.out.println("*_*_*_*_* NEURAL NETWORK RESULTS *_*_*_*_*");
         double percentCorrect = 100 * (double)this.numCorrect / (double)prob.getNumProblems();
         System.out.println(String.format("Percent Correct: %.1f%%", percentCorrect));
    }
    
    public int getNumCorrect() { return this.numCorrect; }
    public int getNumWeights() { return this.numWeights; }
}