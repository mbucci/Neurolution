/**
 * Implements a Perceptron Neural Network
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 4/13/15
 * Last Modified: 4/13/15
 * 
 */

import java.util.*;

public class LayeredNetwork extends Network
{   
    //Perceptron constants
    private static final double SIGMOID_CONSTANT = 0.0;
    private static final int OUT_NODES = 10;

    private int inputNodes;       //Algorithm specific variables
    private int numCorrect;       //Keeps track of performance
    
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
        if (initialWeights == null) super.initWeights(this.inputNodes, OUT_NODES);
        else super.initWeights(this.inputNodes, OUT_NODES, initialWeights);
    }
    
    
    //Main function for NN. Runs perceptron NN on a given problem
    public int run(Problem prob) {

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
            
            // System.out.println("\n *************** START **************");
            // temp.print();

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
                // for (Double val : temp.getAttributes()) {
                //     for (int i = 0; i < prob.getNumAttributes(); i++) {
                //         weightedInputs += super.getWeightedInput(iID + i, hidID, val, "hidden");
                //     } 
                //     iID++;
                // }
                // System.out.println("WeightedInputs: " + weightedInputs);


                outputHidden[hidID] = calculateActivation(weightedInputs);
            }

            // System.out.println("OutputHidden: ");
            // for (int i = 0; i < outputHidden.length; i++) {
            //     System.out.print(outputHidden[i] + " ");
            // }
            // System.out.println("\nTarget: ");
            // for (int i = 0; i < target.length; i++) {
            //     System.out.print(target[i] + " ");
            // }

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
                // System.out.println("Error: " + error);

            }
            // System.out.println("Outputfinal: ");
            // for (int i = 0; i < outputFinal.length; i++) {
            //     System.out.print(outputFinal[i] + " ");
            // }
            
            // System.out.println("\n*_*_*_*_*_*_*_*_*_*  END  *_*_*_*_*_*_*_*_*_\n");

            calculateResults(outputFinal, target);
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

    public void printResults(Problem prob) {
        System.out.println("*_*_*_*_* LAYERED NETWORK RESULTS *_*_*_*_*");
         double percentCorrect = 100 * (double)this.numCorrect / (double)prob.getNumProblems();
         System.out.println(String.format("Percent Correct: %.1f%%", percentCorrect));
    }



}
