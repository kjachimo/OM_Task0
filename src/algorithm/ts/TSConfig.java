package src.algorithm.ts;

/**
 * All Tabu Search parameters in one place.
 *
 * Key parameters:
 *   maxIterations    - stop condition (match GA's popSize * generations for fair comparison)
 *   tabuTenure       - how many iterations a move stays forbidden
 *   neighborhoodSize - how many neighbors to evaluate per iteration
 *   moveType         - "SWAP" or "INVERSION"
 */
public class TSConfig {

    public int    maxIterations    = 1000;
    public int    tabuTenure       = 7;
    public int    neighborhoodSize = 20;
    public String moveType         = "SWAP";

    public TSConfig() {}

    public TSConfig(int maxIterations, int tabuTenure, int neighborhoodSize, String moveType) {
        this.maxIterations    = maxIterations;
        this.tabuTenure       = tabuTenure;
        this.neighborhoodSize = neighborhoodSize;
        this.moveType         = moveType;
    }

    @Override
    public String toString() {
        return String.format(
            "TSConfig{iter=%d, tenure=%d, neighborhood=%d, move=%s}",
            maxIterations, tabuTenure, neighborhoodSize, moveType);
    }
}