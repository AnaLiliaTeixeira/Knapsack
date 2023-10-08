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

			
			for (int i = 1; i <= ITERATIONS; i++) {
				StringBuilder header = new StringBuilder("Sequencial,");
				StringBuilder line = new StringBuilder();
				long durationMethodSequencial = runKnapsackGASequencial();
				for (int currentThread : new int[]{2, 4}) {
					// long durationMethod1 = runKnapsackGAMethod1();
					// header.append("Method 1:Thread " + currentThread + ",");
					// long durationMethod2 = runKnapsackGAMethod2();
					// header.append("Method 2:Thread " + currentThread + ",");

					long durationMethod3 = runKnapsackGAMethod3(currentThread);
					header.append("Method 3:Thread " + currentThread + ",");
					line.append(durationMethodSequencial + "," + durationMethod3 + ",");

				}
				csvWriter.write(header.toString());
				csvWriter.write(line.toString());
				// csvWriter.write(durationMethodSequencial + "," +/*durationMethod1 + "," + durationMethod2 + "," + */ + durationMethod3 + "\n");
				System.out.println("Results saved to results.csv");

			}

			csvWriter.close();
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

	private static long runKnapsackGAMethod1 () {
		long startTime = System.nanoTime();
		KnapsackGAMethod1 method = new KnapsackGAMethod1();
		method.run();
		long endTime = System.nanoTime();
		return endTime - startTime;
	}

	private static long runKnapsackGAMethod2 () {
		long startTime = System.nanoTime();
		KnapsackGAMethod2 method = new KnapsackGAMethod2();
		method.run();
		long endTime = System.nanoTime();
		return endTime - startTime;
	}

	private static long runKnapsackGAMethod3 (int numThreads) {
		long startTime = System.nanoTime();
		KnapsackGAMethod3 method = new KnapsackGAMethod3(numThreads);
		method.run();
		long endTime = System.nanoTime();
		return endTime - startTime;
	}
}
