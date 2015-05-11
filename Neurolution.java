/*
 * 
 * Megan Maher, Nikki Morin, Max Bucci
 * Created: 5/7/15
 * Last Modified: 5/10/15
 * 
 */

import java.util.*;
import java.io.*;

public class Neurolution
{
	private static GA ga;

	private static final double MUTATION_PROB = .15;
	private static final double CROSSOVER_PROB = .7;
	private static final int NUM_INPUT = 11;
	private static final int NUM_IND = 100;

    private static Problem problem;
    private static File testFile;
    private static int numAttr;
    private static int generations;
    private static String networkType;

    private static double numWeights;

    /**
     * Main Function
     */
    public static void main (String[] args) {
                
        testFile = new File(args[0]);
        generations = Integer.parseInt(args[1]);
        networkType = args[2];
        
        problem = new Problem(testFile);
        problem.splitIntoTrainAndTest();
        numAttr = problem.getNumAttributes();
        // System.out.println("Num Attributes: " + numAttr);
        // System.out.println("Num Problems: " + problem.getNumProblems());

        ga = new GA(NUM_IND, MUTATION_PROB, generations, CROSSOVER_PROB, NUM_INPUT, networkType);
        ga.runGA(problem);
        ga.printResults(args[0], problem.getNumProblems());   
    }
}

