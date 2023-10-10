package knapsack;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private final static int ITERATIONS = 1;
	public static void main(String[] args) {

		try {
			FileWriter csvWriter = new FileWriter("results.csv");
			
			// Cabeçalhos das colunas
            // csvWriter.write("Sequencial,Method 3\n");
			int max_threads = Runtime.getRuntime().availableProcessors();

			List<Integer> threads = new ArrayList<>();
			int j = 2;
			while (j <= max_threads) {
				threads.add(j);
				j *= 2;
			}

			StringBuilder header = new StringBuilder("Sequencial");
			for (int t : threads) {
				header.append(", Method 1:Thread " + t);
				header.append(", Method 2:Thread " + t);
				header.append(", Method 3:Thread " + t);
			}
			csvWriter.write(header.toString() + "\n");
			
			for (int i = 1; i <= ITERATIONS; i++) {
				StringBuilder line = new StringBuilder();
				// System.out.println("Sequencial method iteration " + i);
				// long durationMethodSequencial = runKnapsackGASequencial();
				// line.append(durationMethodSequencial);
				long durationMethod1 = 0;
				long durationMethod2 = 0;
				long durationMethod3 = 0;
				long durationMethodTest = runKnapsackGAMethodTest(2);
				// for (int currentThread : threads) {
				// 	System.out.println("Method 1:Thread" + currentThread + " iteration " + i);
				// 	// durationMethod1 = runKnapsackGAMethod1(currentThread);

				// 	System.out.println("Method 2:Thread" + currentThread + " iteration " + i);
				// 	// durationMethod2 = runKnapsackGAMethod2(currentThread);

				// 	System.out.println("Method 3:Thread" + currentThread + " iteration " + i);
				// 	// durationMethod3 = runKnapsackGAMethod3(currentThread);
				// 	line.append("," + durationMethod1 + "," + durationMethod2 + "," + durationMethod3);

				// }
				csvWriter.write(line.toString() + "\n");
				System.out.println("Iteration " + i + " completed \n");
				// csvWriter.write(durationMethodSequencial + "," + durationMethod1 + "," + durationMethod2 + "," + durationMethod3 + "\n");
				
			}
			System.out.println("Results saved to results.csv");

			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static long runKnapsackGASequencial () {
		long startTime = System.nanoTime();
		KnapsackGA method = new KnapsackGA();
		method.run();
		// System.out.println("Sequencial method");
		long endTime = System.nanoTime();
		return endTime - startTime;
	}

	private static long runKnapsackGAMethod1 (int numThreads) {
		long startTime = System.nanoTime();
		KnapsackGAMethod1 method = new KnapsackGAMethod1(numThreads);
		method.run();
		// System.out.println(" method 1");

		long endTime = System.nanoTime();
		return endTime - startTime;
	}
	private static long runKnapsackGAMethodTest (int numThreads) {
		long startTime = System.nanoTime();
		KnapsackGATest method = new KnapsackGATest(numThreads);
		method.run();
		long endTime = System.nanoTime();
		return endTime - startTime;
	}

	private static long runKnapsackGAMethod2 (int numThreads) {
		long startTime = System.nanoTime();
		KnapsackGAMethod2 method = new KnapsackGAMethod2(numThreads);
		method.run();
		// System.out.println(" method 2");
		long endTime = System.nanoTime();
		return endTime - startTime;
	}

	private static long runKnapsackGAMethod3 (int numThreads) {
		long startTime = System.nanoTime();
		KnapsackGAMethod3 method = new KnapsackGAMethod3(numThreads);
		method.run();
		// System.out.println(" method 3");
		long endTime = System.nanoTime();
		return endTime - startTime;
	}
}
