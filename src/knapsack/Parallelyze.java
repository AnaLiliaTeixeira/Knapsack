package knapsack;

import java.util.function.Consumer;

public class Parallelyze {
    
    static void parallelyze(Consumer<Integer> task, int popSize, int numThreads, Thread[] threads, int k) {
		int chunkSize = (popSize - k) / numThreads;
		for (int tid = 0; tid < numThreads; tid++) {
			int start = tid * chunkSize + k;
			int end = tid < (numThreads - 1) ? start + chunkSize : popSize;
			threads[tid] = new Thread(() -> {
				for (int i = start; i < end; i++) {
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
