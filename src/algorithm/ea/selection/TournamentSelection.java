package src.algorithm.ea.selection;

import java.util.List;
import java.util.Random;

import src.problem.Solution;

/**
 * Tournament Selection.
 *
 * Randomly picks 'tournamentSize' individuals from the population,
 * returns the one with the best (lowest) fitness.
 *
 * Effect of tournament size:
 *   tournamentSize = 1  → random selection, no pressure at all
 *   tournamentSize = 2  → mild pressure (default starting point)
 *   tournamentSize = 5  → moderate pressure (suggested in task hints)
 *   tournamentSize = popSize → always picks the best (very high pressure,
 *                              population converges fast → local optima risk)
 */
public class TournamentSelection {

    private final int tournamentSize;
    private final Random rng;

    public TournamentSelection(int tournamentSize, Random rng) {
        this.tournamentSize = tournamentSize;
        this.rng            = rng;
    }

    /**
     * Selects one individual from the population via tournament.
     * @param population list of current solutions
     * @return the winner (best fitness among the randomly chosen contestants)
     */
    public Solution select(List<Solution> population) {
        int popSize = population.size();
        Solution best = null;

        for (int i = 0; i < tournamentSize; i++) {
            Solution contestant = population.get(rng.nextInt(popSize));
            if (best == null || contestant.getFitness() < best.getFitness()) {
                best = contestant;
            }
        }

        return best;
    }
}