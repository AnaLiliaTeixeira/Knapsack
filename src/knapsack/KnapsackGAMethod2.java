package knapsack;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class KnapsackGAMethod2 {
	private static final int N_GENERATIONS = 500;
	private static final int POP_SIZE = 100000;
	private static final double PROB_MUTATION = 0.5;
	private static final int TOURNAMENT_SIZE = 3;
    private static final int NUM_THREADS = 4;
    private static final Thread[] threads = new Thread[NUM_THREADS];

	private ThreadLocalRandom r = ThreadLocalRandom.current();

	private Individual[] population = new Individual[POP_SIZE];

	public KnapsackGAMethod2() {
		populateInitialPopulationRandomly();
	}

	private void populateInitialPopulationRandomly() {
		/* Creates a new population, made of random individuals */
		Parallelyze.parallelyze((start, end) -> {
			for (int i = start; i < end; i++) {	
				population[i] = Individual.createRandom(r);
			}
        }, POP_SIZE, NUM_THREADS, threads, 0);
	}

	public void run() {
		final Phaser phaser = new Phaser() {
			protected boolean onAdvance(int phase, int registeredParties) {
			  return phase >= N_GENERATIONS || registeredParties == 0;
			}
		  };
		// Parallelyze.parallelyze((generation) -> {
			phaser.register();
			phaser.register();
		    new Thread() {
				public void run() {
					do {

						// Step1 - Calculate Fitness
						for (int i = 0; i < POP_SIZE; i++) {
							population[i].measureFitness();
						}

						// Step2 - Print the best individual so far.
						Individual best = bestOfPopulation();
						System.out.println("Best at generation " + /*generation*/ phaser.getPhase() + " is " + best + " with "
								+ best.fitness);

						// Step3 - Find parents to mate (cross-over)
						Individual[] newPopulation = new Individual[POP_SIZE];
						newPopulation[0] = population[0]; // The best individual remains

						for (int i = 1; i < POP_SIZE; i++) {
							// We select two parents, using a tournament.
							Individual parent1 = tournament(TOURNAMENT_SIZE, r);
							Individual parent2 = tournament(TOURNAMENT_SIZE, r);

							newPopulation[i] = parent1.crossoverWith(parent2, r);
						}

						// Step4 - Mutate
						for (int i = 1; i < POP_SIZE; i++) {
							if (r.nextDouble() < PROB_MUTATION) {
								newPopulation[i].mutate(r);
							}
						}
						population = newPopulation;
						phaser.arriveAndAwaitAdvance();
					} while (!phaser.isTerminated());
				}
	  		}.start();
	//  }
		phaser.arriveAndDeregister(); // releases the first barrier
	
		// , N_GENERATIONS, NUM_THREADS, threads, 0);
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
		Individual best = population[0];
		for (Individual other : population) {
			if (other.fitness > best.fitness) {
				best = other;
			}
		}
		return best;
	}
}
