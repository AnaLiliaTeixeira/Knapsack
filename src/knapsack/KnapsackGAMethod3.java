package knapsack;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class KnapsackGAMethod3 {
	private static final int N_GENERATIONS = 500;
	private static final int POP_SIZE = 100000;
	private static final double PROB_MUTATION = 0.5;
	private static final int TOURNAMENT_SIZE = 3;
    private static final int NUM_THREADS = 4;
    private static final Thread[] threads = new Thread[NUM_THREADS];

	private ThreadLocalRandom r = ThreadLocalRandom.current();

	private Individual[] population = new Individual[POP_SIZE];

	public KnapsackGAMethod3() {
		populateInitialPopulationRandomly();
	}

	private void populateInitialPopulationRandomly() {
		/* Creates a new population, made of random individuals */
		Parallelyze.parallelyze((start, end) -> {
			for (int i= start; i < end; i++) {
				population[i] = Individual.createRandom(r);

			}
        }, POP_SIZE, NUM_THREADS, threads, 0);
	}
	
	public void run() {
				   
		// final Phaser phaser = new Phaser() {
		//      protected boolean onAdvance(int phase, int registeredParties) {
		//        return phase >= N_GENERATIONS || registeredParties == 0;
		//      }
		//    };

		   
		//    phaser.register(); // register for the first barrier with parties = 1
		for (int generation = 0; generation < N_GENERATIONS; generation++) {

			// Task 1 - Calculate Fitness
			// phaser.register();
			// Thread fitnessThread = new Thread(() -> {
				// do {
					measureFitness();
					// phaser.arriveAndAwaitAdvance();
				// } while (!phaser.isTerminated());
			// });
			// fitnessThread.start();
			
			// Task2 - Print the best individual so far.			
			// phaser.register();
			final int generationFinal = generation;
			// Thread bestOfPopulationThread = new Thread(() -> {
				// do {
					Individual best = bestOfPopulation();
					System.out.println("Best at generation " + generationFinal + " is " + best + " with "
					+ best.fitness);
					// phaser.arriveAndAwaitAdvance();
				// } while (!phaser.isTerminated());

			// });
			// bestOfPopulationThread.start();

			// Step3 - Find parents to mate (cross-over)
			
			// phaser.register();
			Individual[] newPopulation = new Individual[POP_SIZE];
			newPopulation[0] = population[0]; // The best individual remains
			// Thread performCrossoverThread = new Thread(() -> {
				// do {
					synchronized(newPopulation) {
						performeCrossover(newPopulation);
					}
					// phaser.arriveAndAwaitAdvance();
				// } while (!phaser.isTerminated());

			// });
			// performCrossoverThread.start();

			// Step4 - Mutate
			// phaser.register();
			// Thread performMutationThread = new Thread(() -> {
				// do {
					synchronized(newPopulation) {
						performMutation(newPopulation);
					}
					// phaser.arriveAndAwaitAdvance();
				// } while (!phaser.isTerminated());

			// });
			
			// performMutationThread.start();

			// phaser.arriveAndDeregister(); // releases the first barrier


			// Wait for all slave threads to wait
			// try {
            //     fitnessThread.join();
			// 	bestOfPopulationThread.join();
            //     performCrossoverThread.join();
            //     performMutationThread.join();
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }
		}
	}

	// Step1 - Calculate Fitness
	private void measureFitness() {
		Parallelyze.parallelyze((start, end) -> {
			for (int i = start; i < end; i++) {			
				population[i].measureFitness();
			}
		}, POP_SIZE, NUM_THREADS, threads, 0);
	}

	private void performeCrossover(Individual[] newPopulation) {
		Parallelyze.parallelyze((start, end) -> {
			for (int i = start; i < end; i++) {			// We select two parents, using a tournament.
			Individual parent1 = tournament(TOURNAMENT_SIZE, r);
			Individual parent2 = tournament(TOURNAMENT_SIZE, r);

			newPopulation[i] = parent1.crossoverWith(parent2, r);
			}
		}, POP_SIZE, NUM_THREADS, threads, 1);
	}

	private void performMutation(Individual[] newPopulation) {
		Parallelyze.parallelyze((start, end) -> {
			for (int i = start; i < end; i++) {
				if (r.nextDouble() < PROB_MUTATION) {
					newPopulation[i].mutate(r);
				}
		}
		}, POP_SIZE, NUM_THREADS, threads, 1);
		population = newPopulation;
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
