/**
 * 
 * 
 * Max Bucci, Nikki Morin, Megan Maher
 * Created: 5/7/15
 * Last Modified: 5/7/15
 * 
 */

import java.util.*;
import java.io.*;

public class Neurolution
{

    private static Problem problem;
    private static File testFile;
    private static int numAttr;
    
    private static int generations;
    private static double mutationProb;
    private static double crossoverProb;
    
    /**
     * Main Function
     */
    public static void main (String[] args) {
        
        // if (args.length != 4) {
        //     System.out.println("Bad Input: Wrong number of arguments");
        //     System.exit(0);
        // }
        
        testFile = new File(args[0]);
        // generations = Integer.parseInt(args[1]);
        // mutationProb = Double.parseDouble(args[2]);
        // crossoverProb = new File(args[3]);
        
        problem = new Problem(testFile);
        problem.splitIntoTrainAndTest();
        numAttr = problem.getNumAttributes();
        System.out.println("Num Attributes: " + numAttr);
        System.out.println("Num Problems: " + problem.getNumProblems());

        LayeredNetwork ln = new LayeredNetwork(numAttr); 
        ln.run(problem);
        ln.printResults(problem); 

        NeuralNetwork nn = new NeuralNetwork(numAttr);
        nn.run(problem);
        nn.printResults(problem);     
    }
}

