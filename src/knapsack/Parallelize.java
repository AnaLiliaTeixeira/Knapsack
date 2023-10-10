package knapsack;

import java.util.function.BiConsumer;

public class Parallelize {
    
    static void parallelize(BiConsumer<Integer, Integer> task, int popSize, int numThreads, Thread[] threads, int k) {
		int chunkSize = (popSize - k) / numThreads;
		for (int tid = 0; tid < numThreads; tid++) {
			int start = tid * chunkSize + k;
			int end = tid < (numThreads - 1) ? start + chunkSize : popSize;
			threads[tid] = new Thread(() -> {
					task.accept(start, end);
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
