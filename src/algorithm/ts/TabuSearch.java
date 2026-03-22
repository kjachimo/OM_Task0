package src.algorithm.ts;

import src.problem.PFSPInstance;
import src.problem.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tabu Search for PFSP.
 *
 * How it works:
 *   1. Start from a random solution (current solution)
 *   2. Generate a set of neighbors by applying moves (swap or inversion)
 *   3. Pick the best neighbor that is NOT tabu (or is tabu but beats the global best — aspiration)
 *   4. Add the move to the tabu list, remove old moves that expired
 *   5. Repeat until maxIterations is reached
 *
 * Tabu list:
 *   Stores moves as int[]{i, j} — the two positions that were swapped/inverted.
 *   A move stays tabu for 'tabuTenure' iterations.
 *
 * Aspiration criterion:
 *   If a tabu move leads to a solution BETTER than the global best,
 *   we allow it anyway (override the tabu status).
 *
 * Stats (best per iteration) are stored for CSV export / console printing.
 */
public class TabuSearch {

    private final PFSPInstance instance;
    private final TSConfig     config;
    private final Random       rng;

    // Results
    private Solution    bestSolution;
    private List<int[]> iterationStats; // iterationStats.get(i) = {iteration, bestFitness}

    public TabuSearch(PFSPInstance instance, TSConfig config, Random rng) {
        this.instance = instance;
        this.config   = config;
        this.rng      = rng;
    }

    // -------------------------------------------------------------------------
    // Main loop
    // -------------------------------------------------------------------------

    public void run() {
        iterationStats = new ArrayList<>();

        int n = instance.getNumJobs();

        // 1. Start from a random solution
        Solution current = Solution.createRandom(instance, rng);
        bestSolution     = current.deepCopy();

        // Tabu list: each entry is {i, j, expiresAtIteration}
        List<int[]> tabuList = new ArrayList<>();

        for (int iter = 1; iter <= config.maxIterations; iter++) {

            // 2. Generate neighbors
            int[]    bestMoveI      = {-1};
            int[]    bestMoveJ      = {-1};
            int      bestNeighborFitness = Integer.MAX_VALUE;
            int[]    bestNeighborOrder   = null;

            for (int k = 0; k < config.neighborhoodSize; k++) {
                // Pick two random positions for the move
                int i = rng.nextInt(n);
                int j = rng.nextInt(n);
                while (j == i) j = rng.nextInt(n);
                if (i > j) { int tmp = i; i = j; j = tmp; } // normalize: i < j

                // Apply move to a copy
                int[] neighborOrder = current.getJobOrder().clone();
                applyMove(neighborOrder, i, j);

                // Evaluate
                Solution neighbor = new Solution(instance, neighborOrder);
                int fitness = neighbor.getFitness();

                // Check if this move is tabu
                boolean isTabu = isMoveTabu(tabuList, i, j, iter);

                // Accept if: not tabu, OR aspiration (beats global best)
                boolean aspiration = fitness < bestSolution.getFitness();

                if (!isTabu || aspiration) {
                    if (fitness < bestNeighborFitness) {
                        bestNeighborFitness  = fitness;
                        bestNeighborOrder    = neighborOrder;
                        bestMoveI[0]         = i;
                        bestMoveJ[0]         = j;
                    }
                }
            }

            // If all moves were tabu (rare), just pick the first random non-tabu
            if (bestNeighborOrder == null) {
                bestNeighborOrder   = current.getJobOrder().clone();
                bestMoveI[0]        = 0;
                bestMoveJ[0]        = 1;
            }

            // 3. Move to best neighbor
            current = new Solution(instance, bestNeighborOrder);

            // Update global best
            if (current.getFitness() < bestSolution.getFitness()) {
                bestSolution = current.deepCopy();
            }

            // 4. Add move to tabu list
            tabuList.add(new int[]{bestMoveI[0], bestMoveJ[0], iter + config.tabuTenure});

            // Remove expired tabu moves
            final int currentIter = iter;
            tabuList.removeIf(entry -> entry[2] < currentIter);

            // Record stats
            iterationStats.add(new int[]{iter, bestSolution.getFitness()});

            // Optional console output every 100 iterations
            if (iter % 100 == 0) {
                System.out.printf("  TS iter %4d | best: %d%n", iter, bestSolution.getFitness());
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Applies swap or inversion move to jobOrder in-place. */
    private void applyMove(int[] jobOrder, int i, int j) {
        if (config.moveType.equalsIgnoreCase("INVERSION")) {
            // Reverse segment [i..j]
            int lo = i, hi = j;
            while (lo < hi) {
                int tmp = jobOrder[lo]; jobOrder[lo] = jobOrder[hi]; jobOrder[hi] = tmp;
                lo++; hi--;
            }
        } else {
            // SWAP
            int tmp = jobOrder[i]; jobOrder[i] = jobOrder[j]; jobOrder[j] = tmp;
        }
    }

    /** Returns true if move (i,j) is currently tabu. */
    private boolean isMoveTabu(List<int[]> tabuList, int i, int j, int currentIter) {
        for (int[] entry : tabuList) {
            if (entry[0] == i && entry[1] == j && entry[2] >= currentIter) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public Solution    getBestSolution()  { return bestSolution; }
    public List<int[]> getIterationStats(){ return iterationStats; }
    public TSConfig    getConfig()        { return config; }
}