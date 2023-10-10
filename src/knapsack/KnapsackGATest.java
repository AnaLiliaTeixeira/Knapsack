package knapsack;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class KnapsackGATest {
    private static final int N_GENERATIONS = 500;
    private static final int POP_SIZE = 100000;
    private static final double PROB_MUTATION = 0.5;
    private static final int TOURNAMENT_SIZE = 3;

    private int NUM_THREADS;
    private final Thread[] threads;

    private ThreadLocalRandom r = ThreadLocalRandom.current();

    private Individual[] population = new Individual[POP_SIZE];

    public KnapsackGATest(int n) {
        this.NUM_THREADS = n;
        this.threads = new Thread[NUM_THREADS];
        populateInitialPopulationRandomly();
    }

    private void populateInitialPopulationRandomly() {
        /* Creates a new population, made of random individuals */
        for (int i = 0; i < POP_SIZE; i++) {
            population[i] = Individual.createRandom(r);
        }
    }

    public void run() {
        final Phaser phaser = new Phaser() {
            protected boolean onAdvance(int phase, int registeredParties) {
                return phase >= NUM_THREADS || registeredParties == 0;
            }
        };

        for (int generation = 0; generation < N_GENERATIONS; generation++) {
            // phaser.register();
            final int gen = generation;
            Individual[] newPopulation = population;
            // newPopulation[0] = population[0]; // The best individual remains

            Parallelyze.parallelyze((start, end) -> {
                phaser.register(); // Register for the first barrier with parties = 1

                // Step1 - Calculate Fitness
                // phaser.register();
                for (int i = start; i < end; i++) {
					population[i].measureFitness();
                }
                phaser.arriveAndAwaitAdvance(); // End of step 1
                // phaser.arriveAndDeregister(); // End of the barrier

                // Step2 - Print the best individual so far
                Individual[] best = new Individual[1];
                // phaser.register();
                best[0] = bestOfPopulation();

                if (end == POP_SIZE) {
                    System.out.println("Best at generation " + gen + " is " + best[0] + " with "
                            + best[0].fitness);
                }
                phaser.arriveAndAwaitAdvance(); // End of step 2

                // Step3 - Find parents to mate (cross-over)
                // phaser.register();

                for (int i = start + 1; i < end; i++) {
                    // We select two parents, using a tournament.
                    Individual parent1 = null;
                    Individual parent2 = null;
                    // synchronized (newPopulation) {
                        parent1 = tournament(TOURNAMENT_SIZE, r);
                        parent2 = tournament(TOURNAMENT_SIZE, r);
                        newPopulation[i] = parent1.crossoverWith(parent2, r);
                    // }
                }
                phaser.arriveAndAwaitAdvance(); // End of step 3

                // Step4 - Mutate
                // phaser.register();
                for (int i = start + 1; i < end; i++) {
                    if (r.nextDouble() < PROB_MUTATION) {
                        // synchronized(newPopulation) {
                            newPopulation[i].mutate(r);
                        // }
                    }
                }
                phaser.arriveAndAwaitAdvance(); // End of step 4
                
                population = newPopulation;

                phaser.arriveAndDeregister(); // End of the barrier
            }, POP_SIZE, NUM_THREADS, threads, 0);

            // Wait for all threads to complete this generation
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
        Individual best = population[0];
        for (Individual other : population) {
            if (other.fitness > best.fitness) {
                best = other;
            }
        }
        return best;
    }
}
