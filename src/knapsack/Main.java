package knapsack;

public class Main {
	public static void main(String[] args) {
		long[] executionTimesMethod1 = new long[1]; // 1 eh o nmr de iteracoes
		for (int i = 0; i < executionTimesMethod1.length; i++) {
			long startTime = System.nanoTime();

			KnapsackGAMethod2 ga = new KnapsackGAMethod2();
			ga.run();

			long endTime = System.nanoTime();
			executionTimesMethod1[i] = endTime - startTime;
			System.out.println("\n" + (endTime - startTime) + "\n");
		}
	}
}
