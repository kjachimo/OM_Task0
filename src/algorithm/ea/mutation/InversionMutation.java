package src.algorithm.ea.mutation;

import java.util.Random;

/**
 * Inversion Mutation — picks two random positions and reverses the segment between them.
 *
 * Example from task PDF:
 *   Original:  5 6 7 1 2 3 4
 *   Invert positions 1..5:
 *   Mutated:   5 3 2 1 7 6 4
 *
 * This tends to preserve more of the relative ordering of jobs compared to swap,
 * which can help the GA explore the neighborhood more smoothly.
 */
public class InversionMutation {

    private final Random rng;

    public InversionMutation(Random rng) {
        this.rng = rng;
    }

    /**
     * Mutates the given job order in-place by reversing a random segment.
     * @param jobOrder the permutation to mutate (modified directly)
     */
    public void mutate(int[] jobOrder) {
        int n  = jobOrder.length;
        int i  = rng.nextInt(n);
        int j  = rng.nextInt(n);
        if (i > j) { int tmp = i; i = j; j = tmp; }

        // Reverse the segment jobOrder[i..j]
        while (i < j) {
            int tmp    = jobOrder[i];
            jobOrder[i] = jobOrder[j];
            jobOrder[j] = tmp;
            i++; j--;
        }
    }
}