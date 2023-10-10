package knapsack;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class KnapsackGAPhaser {
    private static final int N_GENERATIONS = 500;
    private static final int POP_SIZE = 100000;
    private static final double PROB_MUTATION = 0.5;
    private static final int TOURNAMENT_SIZE = 3;

    private int NUM_THREADS;
    private final Thread[] threads;

    private ThreadLocalRandom r = ThreadLocalRandom.current();

    private Individual[] population = new Individual[POP_SIZE];

    public KnapsackGAPhaser(int n) {
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
            // Individual[] newPopulation = new Individual[POP_SIZE];
            // newPopulation[0] = population[0]; // The best individual remains
            Individual[] newPopulation = population;

            Parallelize.parallelize((start, end) -> {
                phaser.register(); // Register for the first barrier with parties = 1

                // Step1 - Calculate Fitness
                for (int i = start; i < end; i++) {
					population[i].measureFitness();
                }
        
                phaser.arriveAndAwaitAdvance(); // End of step 1

                // Step2 - Print the best individual so far  
                Individual[] best = new Individual[1];

                best[0] = bestOfPopulation(start, end);
                if (end == POP_SIZE) {
                    System.out.println("Best at generation " + gen + " is " + best[0] + " with "
                            + best[0].fitness);
                }
                phaser.arriveAndAwaitAdvance(); // End of step 2

                // Step3 - Find parents to mate (cross-over)
                //falta skipar o [0]
                // newPopulation[0] = population[0]; // The best individual remains
                for (int i = start; i < end; i++) {

                    // We select two parents, using a tournament.
                    Individual parent1 = tournament(TOURNAMENT_SIZE, r);
                    Individual parent2 = tournament(TOURNAMENT_SIZE, r);
                    newPopulation[i] = parent1.crossoverWith(parent2, r);
                }
                phaser.arriveAndAwaitAdvance(); // End of step 3

                // Step4 - Mutate
                for (int i = start; i < end; i++) {
                    if (r.nextDouble() < PROB_MUTATION) {
                        newPopulation[i].mutate(r);
                    }
                }           
                phaser.arriveAndAwaitAdvance(); // End of step 4

                // synchronized(population) {}
                population = newPopulation;
                
                phaser.arriveAndDeregister(); // End of the barrier
            }, POP_SIZE, NUM_THREADS, threads, 0);
            // System.arraycopy(newPopulation, 0, population, 0, POP_SIZE);

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

    // se paralelizar esta função fica mais lento
    // private Individual bestOfPopulation(Individual[] newPopulation) {
    //     /*
    //      * Returns the best individual of the population.
    //      */
    //     Individual best = newPopulation[0];
    //     for (Individual other : newPopulation) {
    //         if (other.fitness > best.fitness) {
    //             best = other;
    //         }
    //     }
    //     return best;
    // }

    // Updated method to find the best individual in a specific range
    private Individual bestOfPopulation(int start, int end) {
        Individual best = population[start];
        for (int i = start + 1; i < end; i++) {
            if (population[i].fitness > best.fitness) {
                best = population[i];
            }
        }
        return best;
    }
}
