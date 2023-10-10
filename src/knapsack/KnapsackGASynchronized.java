package knapsack;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class KnapsackGASynchronized {
	private static final int N_GENERATIONS = 500;
	private static final int POP_SIZE = 100000;
	private static final double PROB_MUTATION = 0.5;
	private static final int TOURNAMENT_SIZE = 3;

    private int NUM_THREADS;
    private final Thread[] threads;

	private ThreadLocalRandom r = ThreadLocalRandom.current();

	private volatile Individual[] population = new Individual[POP_SIZE];

	public KnapsackGASynchronized(int n) {
		this.NUM_THREADS = n;
		this.threads = new Thread[NUM_THREADS];
		populateInitialPopulationRandomly();
	}

	private void populateInitialPopulationRandomly() {
		/* Creates a new population, made of random individuals */
		Parallelize.parallelize((start, end) -> {
			for (int i= start; i < end; i++) {
				population[i] = Individual.createRandom(r);

			}
        }, POP_SIZE, NUM_THREADS, threads, 0);
	}
	
	public void run() {
		for (int generation = 0; generation < N_GENERATIONS; generation++) {
			// Task 1 - Calculate Fitness
			Parallelize.parallelize((start, end) -> {
				synchronized (population) {
					for (int i = start; i < end; i++) {
						population[i].measureFitness();
					}
				}
			}, POP_SIZE, NUM_THREADS, threads, 0);
			
			// Task2 - Print the best individual so far
			final int generationFinal = generation;
			synchronized (population) {
				Individual best = bestOfPopulation();
				System.out.println("Best at generation " + generationFinal + " is " + best + " with "
						+ best.fitness);
			}
	
			// Step3 - Find parents to mate (cross-over)
			Individual[] newPopulation = new Individual[POP_SIZE];
			synchronized (newPopulation) {
				newPopulation[0] = population[0]; // The best individual remains
				Parallelize.parallelize((start, end) -> {
					for (int i = start; i < end; i++) {            // We select two parents, using a tournament.
						Individual parent1 = tournament(TOURNAMENT_SIZE, r);
						Individual parent2 = tournament(TOURNAMENT_SIZE, r);
						newPopulation[i] = parent1.crossoverWith(parent2, r);
					}
				}, POP_SIZE, NUM_THREADS, threads, 1);
			}
	
			// Step4 - Mutate
			synchronized (newPopulation) {
				Parallelize.parallelize((start, end) -> {
					for (int i = start; i < end; i++) {
						if (r.nextDouble() < PROB_MUTATION) {
							newPopulation[i].mutate(r);
						}
					}
				}, POP_SIZE, NUM_THREADS, threads, 1);
	
				population = newPopulation;
			}
		}
	}
	


	private Individual tournament(int tournamentSize, Random r) {
		/*
		 * In each tournament, we select tournamentSize individuals at random, and we
		 * keep the best of those.
		 */
		Individual best = population[r.nextInt(POP_SIZE)];
		for (int i = 0; i < tournamentSize; i++) {
			Individual other = population[r.nextInt(POP_SIZE)];
			if (other.fitness > best.fitness) {
				best = other;
			}
		}
		return best;
	}

	private Individual bestOfPopulation() {
		/*
		 * Returns the best individual of the population.
		 */

		Individual best[] = {population[r.nextInt(POP_SIZE)]};
		Parallelize.parallelize((start, end) -> {
			for (int i = start; i < end; i++) {
				if (population[i].fitness > best[0].fitness) {
					best[0] = population[i];
				}
			}
		}, POP_SIZE, NUM_THREADS, threads, 0);

		return best[0];
	}
}
