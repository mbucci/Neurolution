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

    private static Problem problem;
    
    private static File testFile;
    
    private static int outputNodes;
    private static int epochs;
    private static double learningRate;
    
    /**
     * Main Function
     */
    public static void main (String[] args) {
        
        // if (args.length != 5) {
        //     System.out.println("Bad Input: Wrong number of arguments");
        //     System.exit(0);
        // }
        
        testFile = new File(args[0]);
        // generations = Integer.parseInt(args[1]);
        // mutationProb = Double.parseDouble(args[2]);
        // crossoverProb = new File(args[3]);
        
        // if (outputNodes != 1 && outputNodes != 10) {
        //     System.out.println("Bad Input: " + outputNodes + ". Value must be 1 or 10");
        //     System.exit(1);
        // }
        
        // NNRunner runner = new NNRunner(outputNodes, epochs, learningRate);
        problem = new Problem(testFile);
        problem.print();
        
        // runner.run(trainProb);
        // runner.run(testProb);
    }
}