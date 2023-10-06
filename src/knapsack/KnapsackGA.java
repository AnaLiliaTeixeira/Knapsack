package knapsack;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class KnapsackGA {
	private static final int N_GENERATIONS = 500;
	private static final int POP_SIZE = 100000;
	private static final double PROB_MUTATION = 0.5;
	private static final int TOURNAMENT_SIZE = 3;
	// private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
	private static final int NUM_THREADS = 2;

	private static Thread[] threads = new Thread[NUM_THREADS];

	private ThreadLocalRandom r = ThreadLocalRandom.current();

	private Individual[] population = new Individual[POP_SIZE];

	public KnapsackGA() {
		populateInitialPopulationRandomly();
	}

	private void populateInitialPopulationRandomly() {
		/* Creates a new population, made of random individuals */
			parallelize((index) -> { population[index] = Individual.createRandom(r); }, 0);
	}

	public static void startTasks(List<Runnable> tasks) {
		
	}

	public void run() {

		final Phaser phaser = new Phaser() {
		    protected boolean onAdvance(int phase, int registeredParties) {
		      return phase >= N_GENERATIONS || registeredParties == 0;
		    }
		};

		phaser.register();

		   // Registre todas as partes (threads) antes do loop de gerações
		// for (int i = 0; i < NUM_THREADS; i++) {
		// 	phaser.register();
		// }
		for (int generation = 0; generation < N_GENERATIONS; generation++) {

			// phaser.register();

			// Step1 - Calculate Fitness	
			do {
				parallelize((index) -> { 
					population[index].measureFitness(); 
				}, 0);
				
				phaser.arriveAndAwaitAdvance(); //end of step 1
				
				
				// Step2 - Print the best individual so far.
				Individual best = bestOfPopulation(); //preciso do phaser aqui?
				System.out.println("Best at generation " + generation + " is " + best + " with "
				+ best.fitness);
				
				// Step3 - Find parents to mate (cross-over)
				Individual[] newPopulation = new Individual[POP_SIZE];
				best = population[0]; // The best individual remains
				
				// do {
				parallelize((index) -> {
					// We select two parents, using a tournament.
					Individual parent1 = tournament(TOURNAMENT_SIZE, r);
					Individual parent2 = tournament(TOURNAMENT_SIZE, r);
					
					newPopulation[index] = parent1.crossoverWith(parent2, r);
				}, 1);
				phaser.arriveAndAwaitAdvance();
				
			// } while (!phaser.isTerminated());

				phaser.arriveAndAwaitAdvance(); // end of step 3
			
			
			// Step4 - Mutate
			// do {
				parallelize((index) -> {
					if (r.nextDouble() < PROB_MUTATION) {
						synchronized(newPopulation) {
							newPopulation[index].mutate(r);
						}
					}
				}, 0);
				population = newPopulation;
				phaser.arriveAndAwaitAdvance();
				// } while (!phaser.isTerminated());
				
			}	while (!phaser.isTerminated());
		}
		phaser.arriveAndDeregister(); //end of user of phaser
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
		final Individual[] bestIndividual = {population[0]}; // Initialize with the first individual
	
		parallelize((index) -> {
			synchronized(bestIndividual) {
				if (population[index].fitness > bestIndividual[0].fitness) {
					bestIndividual[0] = population[index];
				}
			}
		}, 0);
	
		return bestIndividual[0];
	}
	

	private static void parallelize(Consumer<Integer> task, int begining) {
		int chunkSize = (POP_SIZE - begining) / NUM_THREADS;
		for (int tid = 0; tid < NUM_THREADS; tid++) {
			int start = tid * chunkSize;
			int end = tid < NUM_THREADS ? (tid + 1) * chunkSize : POP_SIZE;
			threads[tid] = new Thread(() -> {
				for (int i = start + begining; i < end; i++) {
					final int index = i;
					task.accept(index);
				}
			});
			threads[tid].start();
		}

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
