package src.algorithm.ea.mutation;

import java.util.Random;

/**
 * Swap Mutation — picks two random positions and swaps the jobs there.
 *
 * Example from task PDF:
 *   Original:  5 6 7 1 2 3 4
 *   Swap pos 1 and 5:
 *   Mutated:   5 3 7 1 2 6 4
 *
 * Pm interpretation (individual-level):
 *   Pm = 0.1 means 10% of individuals in the population get mutated.
 *   The GeneticAlgorithm class decides WHETHER to mutate;
 *   this class performs the actual swap when called.
 */
public class SwapMutation {

    private final Random rng;

    public SwapMutation(Random rng) {
        this.rng = rng;
    }

    /**
     * Mutates the given job order in-place by swapping two random positions.
     * @param jobOrder the permutation to mutate (modified directly)
     */
    public void mutate(int[] jobOrder) {
        int n  = jobOrder.length;
        int i  = rng.nextInt(n);
        int j  = rng.nextInt(n);
        while (j == i) j = rng.nextInt(n); // ensure two different positions

        int tmp    = jobOrder[i];
        jobOrder[i] = jobOrder[j];
        jobOrder[j] = tmp;
    }
}