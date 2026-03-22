package src.problem;

import java.util.*;

public class Solution {

    private int[] jobOrder;       // the permutation, 0-indexed job IDs
    private int fitness;          // cached objective value (lower = better)
    private boolean evaluated;    // whether fitness is up to date

    private final PFSPInstance instance;

    public Solution(PFSPInstance instance, int[] jobOrder) {
        this.instance  = instance;
        this.jobOrder  = Arrays.copyOf(jobOrder, jobOrder.length);
        this.evaluated = false;
    }
    public static Solution createRandom(PFSPInstance instance, Random rng) {
        int n = instance.getNumJobs();
        int[] order = new int[n];
        for (int i = 0; i < n; i++) order[i] = i;

        // Fisher-Yates shuffle
        for (int i = n - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = order[i]; order[i] = order[j]; order[j] = tmp;
        }
        return new Solution(instance, order);
    }

    public int getFitness() {
        if (!evaluated) {
            fitness   = instance.evaluate(jobOrder);
            evaluated = true;
        }
        return fitness;
    }

    /**
     * Call this after directly modifying the job order array,
     * so the cached fitness is discarded.
     */
    public void invalidate() {
        evaluated = false;
    }

    // -------------------------------------------------------------------------
    // Copying
    // -------------------------------------------------------------------------

    /**
     * Returns a full (deep) copy of this solution.
     * Use this when you want to modify a copy without affecting the original.
     */
    public Solution deepCopy() {
        Solution copy = new Solution(instance, this.jobOrder);
        if (this.evaluated) {
            copy.fitness   = this.fitness;
            copy.evaluated = true;
        }
        return copy;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns the internal job-order array. Do NOT modify it without calling invalidate(). */
    public int[] getJobOrder() { return jobOrder; }

    /** Swaps two positions in the job order and invalidates fitness. */
    public void swap(int i, int j) {
        int tmp = jobOrder[i];
        jobOrder[i] = jobOrder[j];
        jobOrder[j] = tmp;
        invalidate();
    }

    /** Reverses the sub-sequence from index i to j (inclusive) and invalidates fitness. */
    public void invert(int i, int j) {
        while (i < j) {
            swap(i, j);
            i++; j--;
        }
        // swap() already calls invalidate(), but let's be explicit:
        invalidate();
    }

    public int size() { return jobOrder.length; }

    @Override
    public String toString() {
        return "Solution{fitness=" + getFitness() + ", order=" + Arrays.toString(jobOrder) + "}";
    }
}