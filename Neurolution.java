/*
 * Handles user input and parameters for running the Genetic Algorithm
 * on a Perceptron and a Two-Layered Neural Network 
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

	// Number of individuals we want in our population in GA
	private static final int NUM_IND = 100;

	// Probability that we mutate a given chromosome in GA
	private static final double MUTATION_PROB = .15;

	// Probability that we perform crossover in GA
	private static final double CROSSOVER_PROB = .7;

	// Number of input nodes we want in our Neural Networks
	private static final int NUM_INPUT = 11;

	// Class that holds the problem we aim to solve
	private static Problem problem;

	// Number of attributes we find in the problem
	private static int numAttr;

	// File from which we read in the problem -> specified by user input
	private static File testFile;

	// Number of generations we want to run in GA -> specified by user input
	private static int generations;

	// If we want to run -> specified by user input
	private static String networkType;

	/**
	 * Main Function
	 */
	public static void main (String[] args) {
				
		testFile = new File(args[0]);
		generations = Integer.parseInt(args[1]);
		networkType = args[2];
		
		problem = new Problem(testFile);
		problem.splitIntoTrainAndTest();		// Split the problem into training and testing data
		numAttr = problem.getNumAttributes();

		// Creates, runs, and prints the results of our Genetic Algorithm
		ga = new GA(NUM_IND, MUTATION_PROB, generations, CROSSOVER_PROB, NUM_INPUT, networkType);
		ga.runGA(problem);
		ga.printResults(args[0], problem.getNumProblems());   
	}
}

