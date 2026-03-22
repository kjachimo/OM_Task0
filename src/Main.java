package src;

import src.algorithm.GreedyAlgorithm;
import src.algorithm.RandomSearch;
import src.algorithm.ea.GAConfig;
import src.algorithm.ea.GeneticAlgorithm;
import src.algorithm.ts.TSConfig;
import src.algorithm.ts.TabuSearch;
import src.algorithm.ea.GenerationStats;
import src.problem.PFSPInstance;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

public class Main {
 
    static final int POP_SIZE    = 100;
    static final int GENERATIONS = 100;
    static final int MAX_EVALS   = POP_SIZE * GENERATIONS; // 10,000 — shared budget
 
    public static void main(String[] args) throws Exception {
 
        String filePath     = args.length > 0 ? args[0] : "instances/tai20_5_0.txt";
        String instanceName = filePath.replaceAll(".*/|.*\\\\", "").replace(".txt", "");
 
        System.out.println("Loading: " + filePath);
        PFSPInstance instance = PFSPInstance.loadFromFile(filePath);
        System.out.println("Instance: " + instance + "\n");
 
        // ── Random Search ─────────────────────────────────────────────────────
        RandomSearch rs = new RandomSearch(instance, MAX_EVALS, new Random(42));
        long t0 = System.currentTimeMillis();
        rs.run();
        long rsTime = System.currentTimeMillis() - t0;
 
        // ── Greedy Algorithm ──────────────────────────────────────────────────
        GreedyAlgorithm greedy = new GreedyAlgorithm(instance);
        t0 = System.currentTimeMillis();
        greedy.run();
        long greedyTime = System.currentTimeMillis() - t0;
 
        // ── Genetic Algorithm ─────────────────────────────────────────────────
        GAConfig gaConfig = new GAConfig(POP_SIZE, GENERATIONS, 0.7, 0.1, 5, 1, "OX", "SWAP");
        GeneticAlgorithm ga = new GeneticAlgorithm(instance, gaConfig, new Random(42));
        t0 = System.currentTimeMillis();
        ga.run();
        long gaTime = System.currentTimeMillis() - t0;
        saveGaCsv(ga.getStats(), "results/" + instanceName + "_ga_stats.csv");
 
        // ── Tabu Search ───────────────────────────────────────────────────────
        // maxIterations * neighborhoodSize ≈ total evaluations
        // 500 * 20 = 10,000 — same budget as GA and Random Search
        TSConfig tsConfig = new TSConfig(500, 7, 20, "SWAP");
        TabuSearch ts = new TabuSearch(instance, tsConfig, new Random(42));
        System.out.println("Running Tabu Search...");
        t0 = System.currentTimeMillis();
        ts.run();
        long tsTime = System.currentTimeMillis() - t0;
        saveTsCsv(ts.getIterationStats(), "results/" + instanceName + "_ts_stats.csv");
 
        // ── Comparison table ──────────────────────────────────────────────────
        System.out.println();
        System.out.println("GA  config: " + gaConfig);
        System.out.println("TS  config: " + tsConfig);
        System.out.println();
        System.out.println("==============================================");
        System.out.printf("%-30s %10s  %8s%n", "Method", "Fitness", "Time(ms)");
        System.out.println("----------------------------------------------");
        System.out.printf("%-30s %10d  %8d%n",
                "Random Search [" + MAX_EVALS + " evals]",
                rs.getBestSolution().getFitness(), rsTime);
        System.out.printf("%-30s %10d  %8d%n",
                "Greedy Algorithm",
                greedy.getBestSolution().getFitness(), greedyTime);
        System.out.printf("%-30s %10d  %8d%n",
                "GA [" + gaConfig.crossoverType + "+" + gaConfig.mutationType + "]",
                ga.getBestSolution().getFitness(), gaTime);
        System.out.printf("%-30s %10d  %8d%n",
                "Tabu Search [" + tsConfig.moveType + "]",
                ts.getBestSolution().getFitness(), tsTime);
        System.out.println("==============================================");
    }
 
    // ── CSV helpers ───────────────────────────────────────────────────────────
 
    private static void saveGaCsv(List<GenerationStats> stats, String path) {
        try {
            new java.io.File("results").mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
                pw.println(GenerationStats.csvHeader());
                for (GenerationStats s : stats) pw.println(s.toCsvRow());
            }
            System.out.println("GA  stats -> " + path);
        } catch (Exception e) { System.err.println("CSV error: " + e.getMessage()); }
    }
 
    private static void saveTsCsv(List<int[]> stats, String path) {
        try {
            new java.io.File("results").mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
                pw.println("iteration,best");
                for (int[] row : stats) pw.println(row[0] + "," + row[1]);
            }
            System.out.println("TS  stats -> " + path);
        } catch (Exception e) { System.err.println("CSV error: " + e.getMessage()); }
    }
}