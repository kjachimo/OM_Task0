package src.algorithm.ea;

import src.algorithm.ea.crossover.OXCrossover;
import src.algorithm.ea.crossover.PMXCrossover;
import src.algorithm.ea.mutation.InversionMutation;
import src.algorithm.ea.mutation.SwapMutation;
import src.algorithm.ea.selection.TournamentSelection;
import src.problem.PFSPInstance;
import src.problem.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Genetic Algorithm for PFSP.
 *
 * Follows Pseudocode 2 from the task PDF (individual flow):
 *   - Select two parents via tournament
 *   - Apply crossover with probability Px (otherwise copy parent1)
 *   - Apply mutation with probability Pm
 *   - Add offspring to new population
 *   - Repeat until new population is full
 *   - Apply elitism: best N individuals pass unchanged
 *
 * All parameters are controlled via GAConfig.
 * Statistics (best/avg/worst per generation) are stored for CSV export.
 */
public class GeneticAlgorithm {

    private final PFSPInstance instance;
    private final GAConfig     config;
    private final Random       rng;

    // Operators — chosen based on config
    private final OXCrossover        oxCrossover;
    private final PMXCrossover       pmxCrossover;
    private final SwapMutation       swapMutation;
    private final InversionMutation  inversionMutation;
    private final TournamentSelection selection;

    // Results
    private Solution              bestSolution;
    private List<GenerationStats> stats;

    public GeneticAlgorithm(PFSPInstance instance, GAConfig config, Random rng) {
        this.instance = instance;
        this.config   = config;
        this.rng      = rng;

        this.oxCrossover       = new OXCrossover(rng);
        this.pmxCrossover      = new PMXCrossover(rng);
        this.swapMutation      = new SwapMutation(rng);
        this.inversionMutation = new InversionMutation(rng);
        this.selection         = new TournamentSelection(config.tournamentSize, rng);
    }

    // -------------------------------------------------------------------------
    // Main loop
    // -------------------------------------------------------------------------

    public void run() {
        stats = new ArrayList<>();

        // 1. Initialise population randomly
        List<Solution> population = new ArrayList<>(config.popSize);
        for (int i = 0; i < config.popSize; i++) {
            population.add(Solution.createRandom(instance, rng));
        }

        // Evaluate initial population
        for (Solution s : population) s.getFitness();

        bestSolution = getBest(population).deepCopy();
        recordStats(0, population);

        // 2. Main generational loop
        for (int gen = 1; gen <= config.generations; gen++) {
            List<Solution> newPopulation = new ArrayList<>(config.popSize);

            // Elitism: carry over the best individuals unchanged
            if (config.elitismCount > 0) {
                List<Solution> sorted = new ArrayList<>(population);
                sorted.sort(Comparator.comparingInt(Solution::getFitness));
                for (int e = 0; e < config.elitismCount && e < sorted.size(); e++) {
                    newPopulation.add(sorted.get(e).deepCopy());
                }
            }

            // Fill the rest of the new population
            while (newPopulation.size() < config.popSize) {

                // Select two parents
                Solution parent1 = selection.select(population);
                Solution parent2 = selection.select(population);

                // Crossover
                int[] childOrder;
                if (rng.nextDouble() < config.crossoverProb) {
                    childOrder = applyCrossover(parent1.getJobOrder(), parent2.getJobOrder());
                } else {
                    childOrder = parent1.getJobOrder().clone(); // no crossover → copy parent1
                }

                // Mutation
                if (rng.nextDouble() < config.mutationProb) {
                    applyMutation(childOrder);
                }

                Solution child = new Solution(instance, childOrder);
                child.getFitness(); // evaluate immediately
                newPopulation.add(child);

                // Track global best
                if (child.getFitness() < bestSolution.getFitness()) {
                    bestSolution = child.deepCopy();
                }
            }

            population = newPopulation;
            recordStats(gen, population);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private int[] applyCrossover(int[] p1, int[] p2) {
        return switch (config.crossoverType.toUpperCase()) {
            case "PMX" -> pmxCrossover.cross(p1, p2);
            default    -> oxCrossover.cross(p1, p2);  // OX is default
        };
    }

    private void applyMutation(int[] jobOrder) {
        switch (config.mutationType.toUpperCase()) {
            case "INVERSION" -> inversionMutation.mutate(jobOrder);
            default          -> swapMutation.mutate(jobOrder);  // SWAP is default
        }
    }

    private Solution getBest(List<Solution> population) {
        Solution best = population.get(0);
        for (Solution s : population) {
            if (s.getFitness() < best.getFitness()) best = s;
        }
        return best;
    }

    private void recordStats(int gen, List<Solution> population) {
        int best  = Integer.MAX_VALUE;
        int worst = Integer.MIN_VALUE;
        long sum  = 0;

        for (Solution s : population) {
            int f = s.getFitness();
            if (f < best)  best  = f;
            if (f > worst) worst = f;
            sum += f;
        }

        double avg = (double) sum / population.size();
        stats.add(new GenerationStats(gen, best, avg, worst));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public Solution              getBestSolution() { return bestSolution; }
    public List<GenerationStats> getStats()        { return stats; }
    public GAConfig              getConfig()       { return config; }
}