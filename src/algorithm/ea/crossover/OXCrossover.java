package src.algorithm.ea.crossover;

import java.util.Random;

/**
 * OX (Ordered Crossover) for permutations.
 *
 * How it works:
 *   1. Pick two random cut points in parent1
 *   2. Copy the segment between them directly into the offspring
 *   3. Fill remaining positions with jobs from parent2, in order,
 *      skipping jobs already in the offspring
 *
 * Example from task PDF:
 *   P1 = |1 2 3 4 5 6 7 8 9|  cut points at 2 and 5
 *   P2 = |5 7 4 9 1 3 6 2 8|
 *   O  = |7 9 3 4 5 6 1 2 8|  (segment 3,4,5 from P1, rest filled from P2)
 */
public class OXCrossover {

    private final Random rng;

    public OXCrossover(Random rng) {
        this.rng = rng;
    }

    /**
     * Produces one offspring from two parents.
     * @param parent1 first parent permutation
     * @param parent2 second parent permutation
     * @return new offspring permutation (same length as parents)
     */
    public int[] cross(int[] parent1, int[] parent2) {
        int n = parent1.length;
        int[] offspring = new int[n];
        boolean[] inOffspring = new boolean[n]; // inOffspring[job] = true if job already placed

        // Pick two cut points (point1 < point2)
        int point1 = rng.nextInt(n);
        int point2 = rng.nextInt(n);
        if (point1 > point2) { int tmp = point1; point1 = point2; point2 = tmp; }

        // Step 1: copy segment from parent1 into offspring
        for (int i = point1; i <= point2; i++) {
            offspring[i] = parent1[i];
            inOffspring[parent1[i]] = true;
        }

        // Step 2: fill remaining positions from parent2 in order
        int fillPos = (point2 + 1) % n;  // start filling after the copied segment
        int p2pos   = (point2 + 1) % n;  // start reading parent2 after the segment too

        int filled = 0;
        int total  = n - (point2 - point1 + 1); // how many positions still need filling

        while (filled < total) {
            int job = parent2[p2pos];
            if (!inOffspring[job]) {
                offspring[fillPos] = job;
                inOffspring[job]   = true;
                fillPos = (fillPos + 1) % n;
                filled++;
            }
            p2pos = (p2pos + 1) % n;
        }

        return offspring;
    }
}