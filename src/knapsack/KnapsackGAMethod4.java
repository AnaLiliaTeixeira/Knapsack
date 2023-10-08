package knapsack;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class KnapsackGAMethod4 {
	private static final int N_GENERATIONS = 500;
	private static final int POP_SIZE = 100000;
	private static final double PROB_MUTATION = 0.5;
	private static final int TOURNAMENT_SIZE = 3;
    private static final int NUM_THREADS = 4;
    private static final Thread[] threads = new Thread[NUM_THREADS];

	private ThreadLocalRandom r = ThreadLocalRandom.current();

	private Individual[] population = new Individual[POP_SIZE];

	public KnapsackGAMethod4() {
		populateInitialPopulationRandomly();
	}

	private void populateInitialPopulationRandomly() {
		/* Creates a new population, made of random individuals */
		Parallelyze.parallelyze((index) -> {
			population[index] = Individual.createRandom(r);
        }, POP_SIZE, NUM_THREADS, threads, 0);
	}
	
	public void run() {
				   
		final Phaser phaser = new Phaser(NUM_THREADS) {
			protected boolean onAdvance(int phase, int registeredParties) {
			  return phase >= POP_SIZE || registeredParties == 0;
			}
		  };

		   
		//    phaser.register();
		   for (int generation = 0; generation < N_GENERATIONS; generation++) {
			// phaser.register();

			// Task 1 - Calculate Fitness

			// phaser.register();
			// new Thread() {
			// 	public void run() {
				Parallelyze.parallelyze((index) -> {
					// do {
						population[index].measureFitness();
						phaser.arriveAndAwaitAdvance();

					// } while (!phaser.isTerminated());
					}, POP_SIZE, NUM_THREADS, threads, 0);
				// }
			// }.start();

			
			// Task2 - Print the best individual so far.			
			final int generationFinal = generation;

			// Individual best = bestOfPopulation();
			Individual[] best = {population[0]};
			// phaser.register();
			// new Thread() {
			// 	public void run() {
			// 		do {
				Parallelyze.parallelyze((index) -> {
						if (population[phaser.getPhase()].fitness > best[0].fitness) {
							best[0] = population[phaser.getPhase()];
						}
						phaser.arriveAndAwaitAdvance();
				}, POP_SIZE, NUM_THREADS, threads, 0);

			// 		} while (!phaser.isTerminated());
			// 	}
			// }.start();
			System.out.println("Best at generation " + generationFinal + " is " + best + " with "
			+ best[0].fitness);


			// Step3 - Find parents to mate (cross-over)
			
			Individual[] newPopulation = new Individual[POP_SIZE];
			newPopulation[0] = population[0]; // The best individual remains
			synchronized(newPopulation) {
				// phaser.register();
				// new Thread() {
				// 	public void run() {
				// 		do {
					Parallelyze.parallelyze((index) -> {

							// We select two parents, using a tournament.
							Individual parent1 = tournament(TOURNAMENT_SIZE, r);
							Individual parent2 = tournament(TOURNAMENT_SIZE, r);
				
							newPopulation[phaser.getPhase()] = parent1.crossoverWith(parent2, r);
							phaser.arriveAndAwaitAdvance();
					}, POP_SIZE, NUM_THREADS, threads, 1);

				// 		} while (!phaser.isTerminated());
				// 	}
				// }.start();
			}

			// Step4 - Mutate
			synchronized(newPopulation) {
				// phaser.register();
				// new Thread() {
				// 	public void run() {
				// 		do {
					Parallelyze.parallelyze((index) -> {

							if (r.nextDouble() < PROB_MUTATION) {
								newPopulation[index].mutate(r);
							}
							phaser.arriveAndAwaitAdvance();
						}, POP_SIZE, NUM_THREADS, threads, 1);

				// 		} while (!phaser.isTerminated());
				// 	}
				// }.start();
				population = newPopulation;

			}
			phaser.arriveAndDeregister(); // releases the first barrier
		}
	}

	private Individual tournament(int tournamentSize, Random r) {
		/*
		 * In each tournament, we select tournamentSize individuals at random, and we
		 * keep the best of those.
		 */
		// final Phaser phaser = new Phaser() {
		// 	protected boolean onAdvance(int phase, int registeredParties) {
		// 	  return phase >= tournamentSize || registeredParties == 0;
		// 	}
		//   };
		// phaser.register();
		// phaser.register();
		Individual best[] = {population[r.nextInt(POP_SIZE)]};
		for (int i = 0; i < POP_SIZE; i++) {
		// new Thread() {
		// 	public void run() {
		// 		do {
					Individual other = population[r.nextInt(POP_SIZE)];
					if (other.fitness > best[0].fitness) {
						best[0] = other;
					}
					// phaser.arriveAndAwaitAdvance();
		    //      } while (!phaser.isTerminated());
		    //    }
		    //  }.start();
		   }
		//    phaser.arriveAndDeregister(); // releases the first barrier
		// }
		return best[0];
	}

	// private Individual bestOfPopulation() {
	// 	/*
	// 	 * Returns the best individual of the population.
	// 	 */

	// 	return best;
	// }
}
