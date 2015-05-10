/*
 * GA algorithm
 * Solves and inputted CNF using GA
 *
 * NIC - Professor Majercik
 * Nikki Morin, Megan Maher, Kuangji Chen
 * Created: 2/12/15
 * Last Modified: 2/20/15
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
	private static int[] rankings;
	private static Random rand = new Random();

	private static int bestIteration = 0;
	private static int bestScore = 0;
	private	static int[] bestIndividual;

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
		rankings = new int[numIndividuals];
		network = new NeuralNetwork(problem, null);
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
				indiv[i] = nework.getRandomWeight();
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

	private static int getRanking(int score) {
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

	public static void printResults(String fileName, int numC, int numWeights) {
		double percent = (double)bestScore / (double)numC;
		System.out.println("Results found for file: " + fileName);
		System.out.println("Number of Clauses: " + numC);
		System.out.println("Number of Variables: " + numWeights);
		System.out.println("--------------------------------------");
		System.out.format("Clauses satisfied: %d -> %%%.1f\n", bestScore, percent*100);
		System.out.print("Assignment: ");
		printIndividual(bestIndividual, numWeights);
		System.out.println("Found in iteration: " + bestIteration);
		System.out.println("I gave iterations of: " + iterations);
	}

	// Replaces old generation with new generation
	private static void replaceGeneration(double[][] newGeneration, int numWeights) {
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 1; j <= numWeights; j++) {
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
				network.initWeights(numInputs, numOutputs, individuals[i]);
				score = network.run(problem);
				if (score >= bestScore) {
					bestIteration = generationCount;
					bestScore = score;
					bestIndividual = individuals[i];
				}
				scores[i] = score;
				totalScore += score;
			}

			rankGeneration(cnf, numC);

			// Create the new generation
			for (int i = 0; i < numIndividuals; i+=2) {
				double[] parent1 = selectionByRanking();
				double[] parent2 = selectionByRanking();
				
				// preform crossover
				randomNum = rand.nextDouble();
				if (randomNum <= crossoverProb) {
					int[][] children = crossover(parent1, parent2, numWeights);
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







	// // Choose random pool + choose best from pool
	// private static int[] tournamentRanking(List<CNF> cnf, int numC, int numWeights) {
	// 	int poolSize = (int)((double)numIndividuals * TOURN_SIZE);
	// 	if (poolSize < 1) poolSize = 1;
	// 	int[] chosen = new int[poolSize];
	// 	int[][] pool = new int[poolSize][numWeights+1];
	// 	int randomNum;

	// 	int counter = 0;
	// 	while (counter < poolSize) {
	// 		randomNum = rand.nextInt(numIndividuals);
	// 		if (!Arrays.asList(chosen).contains(randomNum)) {
	// 			chosen[counter] = randomNum;
	// 			pool[counter] = individuals[counter];
	// 			counter++;
	// 		}
	// 	}

	// 	int[] currBestIndiv = pool[0];
	// 	int currBestScore = scores[0];
	// 	int tempScore;
	// 	for (int i = 1; i < poolSize; i++) {
	// 		if (scores[i] > currBestScore) {
	// 			currBestScore = scores[i];
	// 			currBestIndiv = pool[i];
	// 		}
	// 	}
	// 	return currBestIndiv;
	// }

	// // Weigh individuals according to fitness
	// private static int[] boltzmannSelection(List<CNF> cnf, int numC, int numWeights) {
	// 	double randomNum;
	// 	double[] sums = new double[numIndividuals];
	// 	double currSum = 0.;
	// 	double min_value = Math.pow(eValue, ((double)scores[0]/totalScore));
	// 	double max_value = min_value;

	// 	for (int i = 1; i < numIndividuals; i++) {
	// 		currSum += Math.pow(eValue, ((double)scores[i]/totalScore));
	// 		sums[i] = currSum;
	// 		if (currSum < min_value) {
	// 			min_value = currSum;
	// 		}
	// 		if (currSum > max_value) {
	// 			max_value = currSum;
	// 		}
	// 	}

	// 	randomNum = (rand.nextDouble() * (max_value - min_value)) + min_value;
	// 	int chosenOne = chooseSpecifiedB(randomNum, sums);
	// 	return individuals[chosenOne];
	// }

	// // Calls appropriate selection method
	// private static int[] selectParent(int numWeights) {
	// 	int[] parent = new int[numWeights+1];
	// 	switch(selectionMethodInt) {
	// 		case RANK: parent = selectionByRanking(cnf, numC, numWeights); break;
	// 		case TOURNAMENT: parent = tournamentRanking(cnf, numC, numWeights); break;
	// 		case BOLTZMANN: parent = boltzmannSelection(cnf, numC, numWeights); break;
	// 	}
	// 	return parent;
	// }


		// else {
		// 	// Randomly decide crossover point
		// 	randomNum = rand.nextInt(numWeights+1) + 1;
		// 	for (int i = 1; i <= numWeights; i++) {
		// 		if (i<randomNum){
		// 			children[0][i] = parent1[i];
		// 			children[1][i] = parent2[i];
		// 		}
		// 		else {
		// 			children[0][i] = parent2[i];
		// 			children[1][i] = parent1[i];
		// 		}
		// 	}
		// }

		// 