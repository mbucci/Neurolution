/**
 * 
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 4/13/15
 * Last Modified: 4/13/15
 * 
 */

import java.util.*;
import java.io.*;

public class Neurolution
{

    private static NNRunner runner;
    private static Problem trainProb;
    private static Problem testProb;
    
    private static File trainFile;
    private static File testFile;
    
    private static int outputNodes;
    private static int epochs;
    private static double learningRate;
    
    /**
     * Main Function
     */
    public static void main (String[] args) {
        
        if (args.length != 5) {
            System.out.println("Bad Input: Wrong number of arguments");
            System.exit(0);
        }
        
        outputNodes = Integer.parseInt(args[0]);
        epochs = Integer.parseInt(args[1]);
        learningRate = Double.parseDouble(args[2]);
        trainFile = new File(args[3]);
        testFile = new File(args[4]);
        
        if (outputNodes != 1 && outputNodes != 10) {
            System.out.println("Bad Input: " + outputNodes + ". Value must be 1 or 10");
            System.exit(1);
        }
        
        NNRunner runner = new NNRunner(outputNodes, epochs, learningRate);
        trainProb = new Problem(trainFile);
        testProb = new Problem(testFile);
        
        runner.run(trainProb);
        runner.run(testProb);
    }
}