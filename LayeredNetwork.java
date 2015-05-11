/*
 * Implements a Single Layer Neural Network
 * 
 * Nikki Morin, Max Bucci, Megan Maher
 * Created: 4/13/15
 * Last Modified: 5/10/15
 * 
 */

import java.util.*;

public class LayeredNetwork extends Network
{   
    private int inputNodes;       //Algorithm specific variables
    private int numCorrect;       //Keeps track of performance
    private int numWeights;
    
    /**
     * Constructor
     */
    public LayeredNetwork(int numAttr) {
        this(numAttr, null);
    }
    
    //Maintain N input nodes for each attribute to increase diversity. N^2 total nodes.
    public LayeredNetwork(int numAttr, double[] initialWeights) {
        super();
        this.inputNodes = numAttr;
        // this.inputNodes = (int) Math.pow(numAttr, 2);
        if (initialWeights == null) super.initWeights(this.inputNodes);
        else super.initWeights(this.inputNodes, initialWeights, true);

        this.numWeights = (this.inputNodes + OUT_NODES) * super.getNumHiddenNodes();
    }
    
    
    //Main function for NN. Runs perceptron NN on a given problem
    public double run(Problem prob) {
        double totalError = 0.;
        this.numCorrect = 0;
        ListIterator<Clause> lit = prob.getIterator();
        while (lit.hasNext()) {
            double[] target = new double[OUT_NODES];
            double[] outputHidden = new double[super.getNumHiddenNodes()];
            double[] outputFinal = new double[OUT_NODES];
            double[] attrError = new double[OUT_NODES];
            
            // Get next wine/attribute list frm problem, set the corresponding quality
            // index of the target output vector to 1
            Clause temp = lit.next();
            target[temp.getQuality()] = 1;

            // For every hidden layer node, calculate the sum of the weighted inputs
            // @TODO change to 144? rn is twelve...
            for (int hidID = 0; hidID < super.getNumHiddenNodes(); hidID++) {

                // Calculate sum of weighted inputs for every hidden node
                double weightedInputs = 0.0;
                int iID = 0;
                
                for (Double val : temp.getAttributes()) {
                    weightedInputs += super.getWeightedInput(iID, hidID, val, "hidden");
                    iID++;
                }

                outputHidden[hidID] = calculateActivation(weightedInputs);
            }

            // For every output node, calculate the sum of the weighted inputs
            for (int oID = 0; oID < OUT_NODES; oID++) {
                // Calculate sum of weighted inputs
                double weightedInputs = 0.0;
                for (int hidID = 0; hidID < super.getNumHiddenNodes(); hidID++) {
                    weightedInputs += super.getWeightedInput(hidID, oID, outputHidden[hidID], "output");
                }

                // Calculate activation and error
                outputFinal[oID] = calculateActivation(weightedInputs);
                double error = calculateError(oID, outputFinal[oID], target);
                totalError+=error;
                // System.out.println("Error: " + error);

            }

            calculateResults(outputFinal, target);
        }
        return totalError;
        //return (double)this.numCorrect;
    }
    
    
    // Calculates activation function for inputs. Derivative is g(in) * (1 - g(in))
    private double calculateActivation(double input) {
        double denominator = 1 + Math.exp(SIGMOID_CONSTANT - input);
        return 1.0 / denominator;
    }
    
    
    //Calculate error for a given output node, output value and target value.
    private double calculateError(int outID, double outVal, double[] target) {
        return (target[outID] - outVal)*(target[outID] - outVal);
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

    public void printResults(Problem prob) {
        System.out.println("*_*_*_*_* LAYERED NETWORK RESULTS *_*_*_*_*");
         double percentCorrect = 100 * (double)this.numCorrect / (double)prob.getNumProblems();
         System.out.println(String.format("Percent Correct: %.1f%%", percentCorrect));
    }

    public int getNumWeights() { return this.numWeights; }
    public int getNumCorrect() { return this.numCorrect; }
}
