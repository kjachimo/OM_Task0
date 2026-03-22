package src.algorithm;

import java.util.Random;

import src.problem.PFSPInstance;
import src.problem.Solution;

public class RandomSearch {

    private final PFSPInstance instance;
    private final int maxEvaluations;
    private final Random rng;

    private Solution bestSolution;
    private int evaluationsDone;

    public RandomSearch(PFSPInstance instance, int maxEvaluations, Random rng) {
        this.instance       = instance;
        this.maxEvaluations = maxEvaluations;
        this.rng            = rng;
    }
    public void run() {
        bestSolution    = null;
        evaluationsDone = 0;

        for (int i = 0; i < maxEvaluations; i++) {
            Solution candidate = Solution.createRandom(instance, rng);
            int fitness = candidate.getFitness();
            evaluationsDone++;

            if (bestSolution == null || fitness < bestSolution.getFitness()) {
                bestSolution = candidate;
            }
        }
    }
    public Solution getBestSolution()  { return bestSolution; }
    public int getEvaluationsDone()    { return evaluationsDone; }
}