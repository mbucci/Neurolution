/*
 * GA algorithm
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

	private static NeuralNetwork perceptron;
	private static LayeredNetwork layeredNet;
	private static int networkType;

	private static int numIndividuals;
	private static double[][] individuals;
	private static int iterations;

	private static double mutationProb;
	private static double crossoverProb;
	private static int numInputs;

	private static double[] scores;
	private static double[] rankings;
	private static Random rand = new Random();

	private static int bestIteration = 0;
	private static double bestScore = Double.MAX_VALUE;
	private static int bestNumCorrect = 0;
	private	static double[] bestIndividual;
	private static int numSat = 0;

	private static int numWeights;

	public GA() {

	}

	public GA(int numIndv, double mutProb, int iters, double crossProb, int numIn, String type) {
		numIndividuals = numIndv;
		mutationProb = mutProb;
		iterations = iters;
		crossoverProb = crossProb;
		numInputs = numIn;

		rankings = new double[numIndividuals];
		if (type.equals("l")) {
			networkType = LAYERED;
			layeredNet = new LayeredNetwork(numIn);
			numWeights = layeredNet.getNumWeights();
			// numWeights = (numIn + 10) * layeredNet.getNumHiddenNodes();
		} else {
			networkType = PERCEPTRON;
			perceptron = new NeuralNetwork(numIn);
			numWeights = perceptron.getNumWeights();
		}
	}

	// Performs crossover between two parents, and returns two children
	private static double[][] crossover(double[] parent1, double[] parent2) {
		int randomNum;
		double[][] children = new double[2][numWeights+1];

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

	// loop through individual and randomly mutate
	private static double[] mutate(double[] indiv) {
		double randomNum;
		for (int i = 0; i < numWeights; i++) {
			randomNum = rand.nextDouble();
			if (randomNum <= mutationProb) {
				if (networkType == LAYERED) {
					indiv[i] = layeredNet.getRandomWeight();
					continue;
				}
				indiv[i] = perceptron.getRandomWeight();
			}
		}
		return indiv;
	}

	// Called by Rank selection method
	private static int chooseSpecified(int num, int[] array) {
		// sees where specified num falls in the array
		for (int i = 0; i < array.length; i++) {
			if (num < array[i]) {
				return i;
			}
		}
		System.out.println("ERROR HERE BAD, looking for "+num);
		return 0;
	}

	private static int getRanking(double score) {
		int i = 0;
		while (rankings[i] != score) {
			i++;
		}
		return i + 1;
	}

	private static void printranks() {
		for (int i = 0; i < numIndividuals; i++) {
			System.out.print(" " + rankings[i] + " :");
		}
	}

	// Weigh individuals based on rank + choose randomly
	private static double[] selectionByRanking() {
		int randomNum;
		int[] sums = new int[numIndividuals];
		int currSum = 0;

		for (int i = 0; i < numIndividuals; i++) {
			currSum += getRanking(scores[i]);
			sums[i] = currSum;
		}

		randomNum = rand.nextInt(currSum);
		int chosenOne = chooseSpecified(randomNum, sums);
		// System.out.println("Chosen = " + chosenOne);
		return individuals[chosenOne];
	}

	private static void printIndividual(double[] indiv) {
		for (int i = 0; i < numWeights; i++) {
			if (i == 0) System.out.print(indiv[i]);
			else System.out.print(", " + indiv[i]);	
		}
		System.out.println();
	}

	private static void initPopulation() {
		individuals = new double[numIndividuals][numWeights];
		// Initialize all variables to true or false randomly
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 0; j < numWeights; j++) {
                //do weight initiliazation from weight ranges  
                if (networkType == LAYERED) {
					individuals[i][j] = layeredNet.getRandomWeight();
					continue;
				}
				individuals[i][j] = perceptron.getRandomWeight();
			}
		}
	}

	public static void printResults(String fileName, int numProblems) {
		double percent = bestNumCorrect / (double)numProblems;
		System.out.println("--------------------------------------");
		System.out.println("Results found for file: " + fileName);
		System.out.println("Number of Input nodes: " + numInputs);
		System.out.println("Number of Problems: " + numProblems);
		System.out.println("--------------------------------------");
		System.out.printf("Error: %.1f\n", bestScore);
		System.out.printf("Correctly Classified: %d ---> %%%.1f\n", bestNumCorrect, percent*100.);
		// System.out.println("Assignment of weights: ");
		// printIndividual(bestIndividual);
		System.out.println("Found in iteration: " + bestIteration);
		System.out.println("I gave iterations of: " + iterations);
		System.out.println("--------------------------------------");
	}

	// Replaces old generation with new generation
	private static void replaceGeneration(double[][] newGeneration) {
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 0; j < numWeights; j++) {
				individuals[i][j] = newGeneration[i][j];
			}
		}
	}

	public static void runGA(Problem problem) {
		int generationCount = 1;
		double score = 0.;
		int num_correct = 0;
		double[][] newGeneration = new double[numIndividuals][numWeights];
		scores = new double[numIndividuals];
		double randomNum;

		initPopulation();
		while (generationCount <= iterations) {
			// System.out.println("Bestscore = " + bestNumCorrect);
			// Evaluate each individual according to the fitness funciton
			// System.out.printf("Error: %.1f, Num: %d\n", bestScore, bestNumCorrect);
			for (int i = 0; i < numIndividuals; i++) {
				if (networkType == LAYERED) {
					LayeredNetwork temp = new LayeredNetwork(numInputs, individuals[i]);
					score = temp.run(problem);
					num_correct = temp.getNumCorrect();
					// layeredNet.changeWeights(numInputs, individuals[i]);
					// score = layeredNet.run(problem);
					// num_correct = layeredNet.getNumCorrect();
					// System.out.println(num_correct);
				} else {
					NeuralNetwork temp = new NeuralNetwork(numInputs, individuals[i]);
					score = temp.run(problem);
					num_correct = temp.getNumCorrect();
					// perceptron.changeWeights(numInputs, individuals[i]);
					// score = perceptron.run(problem);
					// num_correct = perceptron.getNumCorrect();
					// System.out.println(num_correct);

				}

				// if (score <= bestScore) {
				// 	bestIteration = generationCount;
				// 	bestScore = score;
					
				// 	bestIndividual = individuals[i];
				// 	bestNumCorrect = num_correct;
				// }

				// scores[i] = score;
				// rankings[i] = score;

				if (num_correct > bestNumCorrect || 
					(num_correct == bestNumCorrect && score < bestScore)) {
					bestIteration = generationCount;
					bestScore = score;
					
					bestIndividual = individuals[i];
					bestNumCorrect = num_correct;
				}

				scores[i] = (double)num_correct;
				rankings[i] = (double)num_correct;
			}

			//sorts the ranking array
			Arrays.sort(rankings);
			// printranks();

			// Create the new generation
			for (int i = 0; i < numIndividuals; i+=2) {
				double[] parent1 = selectionByRanking();
				double[] parent2 = selectionByRanking();
				
				// preform crossover
				randomNum = rand.nextDouble();
				if (randomNum <= crossoverProb) {
					double[][] children = crossover(parent1, parent2);
					newGeneration[i] = children[0];
					newGeneration[i+1] = children[1];
				} else {
					newGeneration[i] = parent1;
					newGeneration[i+1] = parent2;
				}

				// Perform mutation
				newGeneration[i] = mutate(newGeneration[i]);
				newGeneration[i+1] = mutate(newGeneration[i+1]);
			}

			replaceGeneration(newGeneration);
			generationCount++;
		}
	}

	public static void main(String args[]) throws Exception {

	}
}