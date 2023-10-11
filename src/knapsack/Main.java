package knapsack;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private final static int ITERATIONS = 30;
	public static void main(String[] args) {

		try {
			FileWriter csvWriter = new FileWriter("results.csv");
			
			int maxThreads = Runtime.getRuntime().availableProcessors();

			List<Integer> threads = new ArrayList<>();
			int j = 2;
			while (j <= maxThreads) {
				threads.add(j);
				j *= 2;
			}

			// StringBuilder header = new StringBuilder("Sequential");
			StringBuilder header = new StringBuilder();
			for (int t : threads) {
				header.append("Parallelize:Thread " + t);
				// header.append(", Parallelize:Thread " + t);
				header.append(", Synchronized:Thread " + t);
				header.append(", Phaser:Thread " + t);
			}
			csvWriter.write(header.toString() + "\n");
			
			for (int i = 1; i <= ITERATIONS; i++) {
				StringBuilder line = new StringBuilder();
				// System.out.println("Sequential method iteration " + i);
				// long durationMethodSequencial = runKnapsackGASequencial();
				// line.append(durationMethodSequencial);

				long durationParallelize = 0;
				long durationSynchronized = 0;
				long durationPhaser = 0;

				for (int currentThread : threads) {
					System.out.println("Parallelize:Thread" + currentThread + " iteration " + i);
					durationParallelize = runKnapsackGAParallelize(currentThread);

					System.out.println("Synchronized:Thread" + currentThread + " iteration " + i);
					durationSynchronized = runKnapsackGASynchronized(currentThread);

					System.out.println("Phaser:Thread" + currentThread + " iteration " + i);
					durationPhaser = runKnapsackGAPhaser(currentThread);			

					line.append("," + durationParallelize + "," + durationSynchronized + "," + durationPhaser);

				}
				csvWriter.write(line.toString() + "\n");
				System.out.println("Iteration " + i + " completed \n");				
			}			
			csvWriter.close();
			System.out.println("Results saved to results.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static long runKnapsackGASequencial () {
		long startTime = System.nanoTime();
		KnapsackGA method = new KnapsackGA();
		method.run();
		long endTime = System.nanoTime();
		return endTime - startTime;
	}

	private static long runKnapsackGAParallelize (int numThreads) {
		long startTime = System.nanoTime();
		KnapsackGAParallelize method = new KnapsackGAParallelize(numThreads);
		method.run();
		long endTime = System.nanoTime();
		return endTime - startTime;
	}
	
	private static long runKnapsackGASynchronized (int numThreads) {
		long startTime = System.nanoTime();
		KnapsackGASynchronized method = new KnapsackGASynchronized(numThreads);
		method.run();
		long endTime = System.nanoTime();
		return endTime - startTime;
	}
	
	private static long runKnapsackGAPhaser (int numThreads) {
		long startTime = System.nanoTime();
		KnapsackGAPhaser method = new KnapsackGAPhaser(numThreads);
		method.run();
		long endTime = System.nanoTime();
		return endTime - startTime;
	}

}