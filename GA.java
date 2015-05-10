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
	private static NeuralNetwork network;

	private static int numIndividuals;
	private static double[][] individuals;
	private static int iterations;

	private static double mutationProb;
	private static double crossoverProb;
	private static int numInputs;
	private static int numOutputs;

	private static double[] scores;
	private static double[] rankings;
	private static Random rand = new Random();

	private static int bestIteration = 0;
	private static double bestScore = 0.;
	private	static double[] bestIndividual;
	private static int numSat = 0;

	private static double totalScore; //total current score, used to calc percentages

	public GA() {

	}

	public GA(int numIndv, double mutProb, int iters, double crossProb, int numIn, int numOut) {
		numIndividuals = numIndv;
		mutationProb = mutProb;
		iterations = iters;
		crossoverProb = crossProb;
		numInputs = numIn;
		numOutputs = numOut;

		totalScore = 0;
		rankings = new double[numIndividuals];
		network = new NeuralNetwork(numIn);
	}

	// Performs crossover between two parents, and returns two children
	private static double[][] crossover(double[] parent1, double[] parent2, int numWeights) {
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
	private static double[] mutate(double[] indiv, int numWeights) {
		double randomNum;
		for (int i = 0; i < numWeights; i++) {
			randomNum = rand.nextDouble();
			if (randomNum <= mutationProb) {
				indiv[i] = network.getRandomWeight();
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

	private static void rankGeneration() {
		// First clear the previous rankings
		for (int i = 0; i < numIndividuals; i++) {
			rankings[i] = scores[i];
		}
		Arrays.sort(rankings);
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
		return individuals[chosenOne];
	}

	private static void printIndividual(double[] indiv, int numWeights) {
		for (int i = 0; i < numWeights; i++) {
			if (i == 0) System.out.print(indiv[i]);
			else System.out.print(", " + indiv[i]);	
		}
		System.out.println();
	}

	private static void initPopulation(int numWeights) {
		individuals = new double[numIndividuals][numWeights];
		// Initialize all variables to true or false randomly
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 0; j < numWeights; j++) {
                //do weight initiliazation from weight ranges   
                individuals[i][j] = network.getRandomWeight();
			}
		}
	}

	public static void printResults(String fileName, int numWeights, int numProblems) {
		double percent = bestScore / (double)numProblems;
		System.out.println("Results found for file: " + fileName);
		System.out.println("Number of Input nodes: " + numInputs);
		System.out.println("Number of Output nodes: " + numOutputs);
		System.out.println("--------------------------------------");
		System.out.format("Clauses satisfied: %d -> %%%.1f\n", (int)bestScore, percent*100);
		System.out.println("BESTSCORE: " + bestScore);
		System.out.println("Num correct " + numSat);
		// System.out.println("Total: " + )
		System.out.println("Assignment of weights: ");
		printIndividual(bestIndividual, numWeights);
		System.out.println("Found in iteration: " + bestIteration);
		System.out.println("I gave iterations of: " + iterations);
	}

	// Replaces old generation with new generation
	private static void replaceGeneration(double[][] newGeneration, int numWeights) {
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 0; j < numWeights; j++) {
				individuals[i][j] = newGeneration[i][j];
			}
		}
	}

	public static void runGA(Problem problem, int numWeights) {
		int generationCount = 1;
		double score = 0.;
		double[][] newGeneration = new double[numIndividuals][numWeights];
		scores = new double[numIndividuals];
		double randomNum;

		initPopulation(numWeights);
		while (generationCount <= iterations) {
			// Evaluate each individual according to the fitness funciton
			totalScore = 0.;
			for (int i = 0; i < numIndividuals; i++) {
				network.changeWeights(numInputs, numOutputs, individuals[i]);
				score = network.run(problem);
				if (score >= bestScore) {
					bestIteration = generationCount;
					bestScore = score;
					numSat = network.numCorrect;
					bestIndividual = individuals[i];
				}
				scores[i] = score;
				totalScore += score;
			}

			rankGeneration();

			// Create the new generation
			for (int i = 0; i < numIndividuals; i+=2) {
				double[] parent1 = selectionByRanking();
				double[] parent2 = selectionByRanking();
				
				// preform crossover
				randomNum = rand.nextDouble();
				if (randomNum <= crossoverProb) {
					double[][] children = crossover(parent1, parent2, numWeights);
					newGeneration[i] = children[0];
					newGeneration[i+1] = children[1];
				} else {
					newGeneration[i] = parent1;
					newGeneration[i+1] = parent2;
				}

				// Perform mutation
				newGeneration[i] = mutate(newGeneration[i], numWeights);
				newGeneration[i+1] = mutate(newGeneration[i+1], numWeights);
			}

			replaceGeneration(newGeneration, numWeights);
			generationCount++;
		}
	}

	public static void main(String args[]) throws Exception {

	}
}