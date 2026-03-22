package src.algorithm;

import java.util.ArrayList;
import java.util.List;

import src.problem.PFSPInstance;
import src.problem.Solution;

public class GreedyAlgorithm {

    private final PFSPInstance instance;
    private Solution bestSolution;

    public GreedyAlgorithm(PFSPInstance instance) {
        this.instance = instance;
    }

    public void run() {
        int n = instance.getNumJobs();
        int M = instance.getNumMachines();

        List<Integer> unscheduled = new ArrayList<>(n);
        for (int j = 0; j < n; j++) unscheduled.add(j);

        int[] currentOrder  = new int[n];
        int   currentLength = 0;
        
        int[] completionTime = new int[M];

        while (!unscheduled.isEmpty()) {
            int bestJob            = -1;
            int bestCOnLastMachine = Integer.MAX_VALUE;

            for (int job : unscheduled) {
                int cPrev = 0;
                for (int m = 0; m < M; m++) {
                    int p = instance.getProcessingTime(m, job);
                    // Can start on machine m only after:
                    //   (a) finished on previous machine -> cPrev
                    //   (b) machine m is free -> completionTime[m]
                    cPrev = Math.max(completionTime[m], cPrev) + p;
                }
                // cPrev is now c(job, lastMachine) — the criterion from the task
                if (cPrev < bestCOnLastMachine) {
                    bestCOnLastMachine = cPrev;
                    bestJob = job;
                }
            }

            // Commit bestJob — update completionTime[] incrementally
            int cPrev = 0;
            for (int m = 0; m < M; m++) {
                int p = instance.getProcessingTime(m, bestJob);
                cPrev = Math.max(completionTime[m], cPrev) + p;
                completionTime[m] = cPrev;
            }

            currentOrder[currentLength++] = bestJob;
            unscheduled.remove(Integer.valueOf(bestJob));
        }

        bestSolution = new Solution(instance, currentOrder);
    }

    public Solution getBestSolution() { return bestSolution; }
}