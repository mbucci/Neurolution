/*
 * GA algorithm performed on a Perceptron and a Layered Neural Network
 *
 * NIC - Professor Majercik
 * Megan Maher, Nikki Morin, Max Bucci
 * Created: 5/01/15
 * Last Modified: 5/10/15
 *
 */

import java.io.*;
import java.util.*;

public class GA {	

	private static final int PERCEPTRON = 1;
	private static final int LAYERED = 2;
	private static final int CHUNKED = 3;
	private static final int NOT_CHUNKED = 4;
	private static final int PRINT_INTERVAL = 5;

	private static Perceptron perceptron;			// our perceptron network
	private static TwoLayerPerceptron layeredNet;	// our layered network
	private static int networkType;					// will determine which network we want
	private static int crossoverChunk;				// will determine if we want to chunk weights together in crossover

	private static int numIndividuals;			// number of individuals in our population
	private static double[][] individuals;		// Our population: array of individuals
	private static int iterations;				// Number of times we run our GA

	private static double mutationProb;			// Probability that a chromosone is mutated
	private static double crossoverProb;		// Prob. that we perform crossover on an individual

	private static int numWeights;				// Number of weights in our NN
	private static int numInputs;				// Number of input nodes we want in our NN
	private static int numOutput;				// Number of output nodes in our NN

	private static double[] scores;				// Holds current fitnesses of individuals
	private static double[] rankings;			// Holds sorted fitnesses of individuals
	private static Random rand = new Random();

	private static int bestIteration = 0;		// Holds the iteration when we found our best individual
	private static int bestNumCorrect = 0;		// Holds the best fitness: number correctly classified w/ best weights
	private static double smallestError = Double.MAX_VALUE;		// Holds smallest error w/ best weights
	
	private	static double[] bestIndividual;		// Our best individual found so far


	// Class Constructor
	public GA(int numIndv, double mutProb, int iters, double crossProb, int numIn, String ntype, String ctype) {
		numIndividuals = numIndv;
		mutationProb = mutProb;
		iterations = iters;
		crossoverProb = crossProb;
		numInputs = numIn;

		// Arrays initialized to be used later
		rankings = new double[numIndividuals];
		scores = new double[numIndividuals];

		// Determines whether we are running a layered network or a perceptron
		if (ntype.equals("l")) {
			networkType = LAYERED;
			layeredNet = new TwoLayerPerceptron(numIn);
			//Get the number of weights from the network
			numWeights = layeredNet.getNumWeights();
			numOutput = layeredNet.getNumOutput();
		} else {
			networkType = PERCEPTRON;
			perceptron = new Perceptron(numIn);
			//Get the number of weights from the network
			numWeights = perceptron.getNumWeights();
			numOutput = perceptron.getNumOutput();
		}

		// Determines if we want to chunk weights together in crossover or not
		if (ctype.equals("c")) {
			crossoverChunk = CHUNKED;
		} else {
			crossoverChunk = NOT_CHUNKED;
		}
	}


	// Prints out an individual
	private static void printIndividual(double[] indiv) {
		for (int i = 0; i < numWeights; i++) {
			if (i == 0) System.out.print(indiv[i]);
			else System.out.print(", " + indiv[i]);	
		}
		System.out.println();
	}


	// Prints out results!
	public static void printResults(String fileName, int numProblems) {
		double percent = bestNumCorrect / (double)numProblems;
		System.out.println("\n--------------------------------------");
		System.out.println("Results found for file: " + fileName);
		System.out.println("Number of Input nodes: " + numInputs);
		System.out.println("Number of Problems: " + numProblems);
		System.out.println("Num weights "+ numWeights);
		System.out.println("--------------------------------------");
		System.out.printf("Error: %.1f\n", smallestError);
		System.out.printf("Correctly Classified: %d ---> %%%.1f\n", bestNumCorrect, percent*100.);
		System.out.println("Found in iteration: " + bestIteration);
		System.out.println("I gave iterations of: " + iterations);
		System.out.println("--------------------------------------");
		System.out.println("Assignment of weights: ");
		printIndividual(bestIndividual);
		System.out.println("--------------------------------------");
	}


	// Prints the rankings
	private static void printranks() {
		for (int i = 0; i < numIndividuals; i++) {
			System.out.print(" " + rankings[i] + " :");
		}
	}


	// Loops through array until we find the score, returns rank = index + 1
	private static int getRanking(double score) {
		int i = 0;
		while (rankings[i] != score) {
			i++;
		}
		return i + 1;
	}


	// Called by Rank selection method
	private static int chooseSpecified(int num, int[] array) {
		// Sees where specified num falls in the array
		// We want to choose the element such that num <= element but > previous element
		// EX: if num = 3 and array is [2, 5, 6], would choose array[1]
		for (int i = 0; i < array.length; i++) {
			if (num < array[i]) {
				return i;
			}
		}
		// Should never get to this point... will only happen if there is an error
		System.out.println("ERROR HERE BAD, looking for "+num);
		return 0;
	}


	// Weigh individuals based on Rank + choose randomly
	private static double[] selectionByRanking() {
		int randomNum;

		// Holds current sum of ranks ->
		// If ranks are [3, 1, 2], the sum array would be: [3, 4, 6]
		int[] sums = new int[numIndividuals];
		int currSum = 0;

		for (int i = 0; i < numIndividuals; i++) {
			currSum += getRanking(scores[i]);
			sums[i] = currSum;
		}

		randomNum = rand.nextInt(currSum);

		// A probabilistic way of selecting individuals, described in method
		int chosenOne = chooseSpecified(randomNum, sums);
		return individuals[chosenOne];
	}

	private static void printMe(double[] a) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i] + ", ");
		}
	}

	// Replaces indicies from a certain parent 
	private static double[] getChunkFromParent(double[] parent, double[] child, int inNodes, int outNodes, int chunk) {
		int index = chunk;
		while (index < (inNodes * outNodes)) {
			child[index] = parent[index];
			index += outNodes;
		}
		return child;
	}


	// Performs crossover between two parents, and returns two children
	private static double[][] chunkCrossover(double[] parent1, double[] parent2, int inNodes, int outNodes) {

		double[][] children = new double[2][inNodes*outNodes];
		int randomNum;

		// Each element has a 50% chance of being from one parent or the other
		// Loop through all the chunks = num output nodes
		for (int i = 0; i < outNodes; i++) {
			randomNum = rand.nextInt(2) + 1;
			if (randomNum == 1) {
				children[0] = getChunkFromParent(parent1, children[0], inNodes, outNodes, i);
				children[1] = getChunkFromParent(parent2, children[1], inNodes, outNodes, i);
			}
			else {
				children[0] = getChunkFromParent(parent2, children[0], inNodes, outNodes, i);
				children[1] = getChunkFromParent(parent1, children[1], inNodes, outNodes, i);
			}
		}
		
		return children;
	}

	private static double[][] multiLayerChunkCrossover(double[] parent1, double[] parent2, int outNodes, int numHidden) {
		int firstLayer = numInputs * numHidden;
		int secondLayer = numHidden * outNodes;

		double[] parent1_1 = new double[firstLayer];
		double[] parent1_2 = new double[secondLayer];
		double[] parent2_1 = new double[firstLayer];
		double[] parent2_2 = new double[secondLayer];

		System.arraycopy(parent1, 0, parent1_1, 0, firstLayer);
		System.arraycopy(parent1, 0, parent1_2, 0, secondLayer);
		System.arraycopy(parent2, 0, parent2_1, 0, firstLayer);
		System.arraycopy(parent2, 0, parent2_2, 0, secondLayer);

		double[][] firstHalfChildren = chunkCrossover(parent1_1, parent2_1, numInputs, numHidden);
		double[][] secondHalfChildren = chunkCrossover(parent1_2, parent2_2, numHidden, outNodes);

		double[][] children = new double[2][numWeights];
		System.arraycopy(firstHalfChildren[0], 0, children[0], 0, firstLayer);
		System.arraycopy(firstHalfChildren[1], 0, children[1], 0, firstLayer);
		System.arraycopy(secondHalfChildren[0], 0, children[0], firstLayer, secondLayer);
		System.arraycopy(secondHalfChildren[1], 0, children[1], firstLayer, secondLayer);

		return children;
	}


	// Performs crossover between two parents, and returns two children
	private static double[][] crossover(double[] parent1, double[] parent2) {

		double[][] children = new double[2][numWeights];
		int randomNum;

		// Each element has a 50% chance of being from one parent or the other
		for (int i = 0; i < numWeights; i++) {
			randomNum = rand.nextInt(2) + 1;
			if (randomNum == 1) {
				children[0][i] = parent1[i];
				children[1][i] = parent2[i];
			}
			else {
				children[0][i] = parent2[i];
				children[1][i] = parent1[i];
			}
		}
		
		return children;
	}


	// Loop through individual and randomly mutate
	private static double[] mutate(double[] indiv) {

		double randomNum;
		for (int i = 0; i < numWeights; i++) {

			randomNum = rand.nextDouble();
			if (randomNum <= mutationProb) {
				// If we want to mutate, generate a random value from the range
				//    specified for the layered or perceptron network
				if (networkType == LAYERED) {
					// Get random value from layered network
					indiv[i] = layeredNet.getWeightFromRange();
					continue;
				}
				// Get random value from perceptron
				indiv[i] = perceptron.getWeightFromRange();
			}
		}

		return indiv;
	}


	// Replaces old generation with new generation
	private static void replaceGeneration(double[][] newGeneration) {
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 0; j < numWeights; j++) {
				individuals[i][j] = newGeneration[i][j];
			}
		}
	}


	// Initializes the population randomly
	private static void initPopulation() {
		individuals = new double[numIndividuals][numWeights];
		// Initialize all variables to true or false randomly
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 0; j < numWeights; j++) {
				// Do weight initiliazation from weight ranges depending on network type
				if (networkType == LAYERED) {
					individuals[i][j] = layeredNet.getWeightFromRange();
					continue;
				}
				// Perceptron initialization
				individuals[i][j] = perceptron.getWeightFromRange();
			}
		}
	}


	// Runs the genetic algorithm on the given problem
	public static void runGA(Problem problem) {

		double randomNum;
		double error = 0.;
		int num_correct = 0;
		int generationCount = 1;

		// We will slowly create the new generation which will
		//    eventually replace the old generation
		double[][] newGeneration = new double[numIndividuals][numWeights];

		// Randomly initializes the population
		initPopulation();

		// Run the GA for as many times as specified by the user input
		while (generationCount <= iterations) {
			for (int i = 0; i < numIndividuals; i++) {

				// Run either layered network or perceptron to get error 
				// and number of problems correctly classified
				if (networkType == LAYERED) {
					TwoLayerPerceptron temp = new TwoLayerPerceptron(numInputs, individuals[i]);
					error = temp.run(problem);
					num_correct = temp.getNumCorrect();
				} else {
					Perceptron temp = new Perceptron(numInputs, individuals[i]);
					error = temp.run(problem);
					num_correct = temp.getNumCorrect();
				}

				// Want to replace best individual + best values if:
				//		- has strictly better fitness
				//		- has same fitness and a smaller error
				if (num_correct > bestNumCorrect || 
					(num_correct == bestNumCorrect && error < smallestError)) {

					bestIteration = generationCount;
					smallestError = error;
					
					bestIndividual = individuals[i];
					bestNumCorrect = num_correct;
				}

				// Keeps track of all the fitnesses when they are already
				// stored so we don't have to recalculate later on
				scores[i] = (double)num_correct;

				// Same as the scores, except this array will be sorted later
				rankings[i] = (double)num_correct;
			}

			// Sorts the ranking array to be used later in rank selection
			Arrays.sort(rankings);

			// Create the new generation, two individuals at a time
			for (int i = 0; i < numIndividuals; i+=2) {

				// Select the two new parents with rank selection
				double[] parent1 = selectionByRanking();
				double[] parent2 = selectionByRanking();
				
				// Perform crossover to get new children with probability
				// 		   or else place parents in new generation
				randomNum = rand.nextDouble();
				if (randomNum <= crossoverProb) {
					// Decide if we want to "chunk" the weights together according
					// to which output node they go to
					double[][] children = null;
					if (crossoverChunk == NOT_CHUNKED) {
						children = crossover(parent1, parent2);
					} else if (networkType == PERCEPTRON) {
						children = chunkCrossover(parent1, parent2, numInputs, numOutput);
					} else if (networkType == LAYERED) {
						children = multiLayerChunkCrossover(parent1, parent2, numOutput, layeredNet.getNumHiddenNodes());
					} else {
						System.out.println("ERROR");
					}

					newGeneration[i] = children[0];
					newGeneration[i+1] = children[1];
				} else {
					newGeneration[i] = parent1;
					newGeneration[i+1] = parent2;
				}

				// Perform mutation on the two new individuals
				newGeneration[i] = mutate(newGeneration[i]);
				newGeneration[i+1] = mutate(newGeneration[i+1]);
			}

			// Replaces the old generation with the new generation
			replaceGeneration(newGeneration);
			generationCount++;

			if (generationCount%PRINT_INTERVAL == 0)
				System.out.println("Generation: " + generationCount + " --> " + bestNumCorrect);
		}
	}
}