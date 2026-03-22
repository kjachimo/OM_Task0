package src.algorithm.ea.crossover;

import java.util.Random;

/**
 * PMX (Partially Matched Crossover) for permutations.
 *
 * How it works:
 *   1. Pick two cut points
 *   2. Copy the segment from parent1 into offspring1 (and parent2 into offspring2)
 *   3. For positions outside the segment, use the mapping defined by the segment
 *      to avoid duplicates
 *
 * Example from task PDF:
 *   P1 = |1 2 3 | 4 5 6 | 7 8 9|
 *   P2 = |4 3 1 | 2 8 7 | 5 6 9|
 *   Mapping: 4<->2, 5<->8, 6<->7
 *   O1 = |1 _ 3 | 2 8 7 | _ _ 9|  then fill using mapping
 *   O1 = |1 4 3 | 2 8 7 | 5 6 9|
 */
public class PMXCrossover {

    private final Random rng;

    public PMXCrossover(Random rng) {
        this.rng = rng;
    }

    /**
     * Produces one offspring from two parents using PMX.
     */
    public int[] cross(int[] parent1, int[] parent2) {
        int n = parent1.length;

        // position[job] = index of that job in parent2
        int[] posInParent2 = new int[n];
        for (int i = 0; i < n; i++) posInParent2[parent2[i]] = i;

        // Pick two cut points
        int point1 = rng.nextInt(n);
        int point2 = rng.nextInt(n);
        if (point1 > point2) { int tmp = point1; point1 = point2; point2 = tmp; }

        int[] offspring = new int[n];
        boolean[] inOffspring = new boolean[n];

        // Step 1: copy segment from parent1
        for (int i = point1; i <= point2; i++) {
            offspring[i]          = parent1[i];
            inOffspring[parent1[i]] = true;
        }

        // Step 2: fill positions outside segment using PMX mapping
        for (int i = 0; i < n; i++) {
            if (i >= point1 && i <= point2) continue; // skip segment

            int candidate = parent2[i];

            // Follow the mapping chain until we find a job not already in offspring
            while (inOffspring[candidate]) {
                // Find where 'candidate' appears in parent1 segment,
                // then look at the corresponding position in parent2
                int posInP1 = findInSegment(parent1, candidate, point1, point2);
                candidate   = parent2[posInP1];
            }

            offspring[i]         = candidate;
            inOffspring[candidate] = true;
        }

        return offspring;
    }

    /** Returns the index of 'job' within parent1[point1..point2], or -1 if not found. */
    private int findInSegment(int[] parent1, int job, int point1, int point2) {
        for (int i = point1; i <= point2; i++) {
            if (parent1[i] == job) return i;
        }
        return -1; // should never happen in a valid permutation
    }
}