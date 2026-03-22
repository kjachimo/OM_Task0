package src.problem;

import java.io.*;
import java.util.*;

/**
 * Holds the raw data for one PFSP instance.
 *
 * File format (Taillard):
 *   Line 1: <numJobs> <numMachines> <seed> <upperBound> <lowerBound>
 *   Then a matrix of size numMachines x numJobs
 *   where processingTime[m][j] = time of job j on machine m
 */
public class PFSPInstance {

    private int numJobs;
    private int numMachines;
    private int[][] processingTime; // [machine][job], both 0-indexed

    // Private constructor — use the static factory method below
    private PFSPInstance() {}

    /**
     * Reads an instance from a Taillard-format file.
     * Usage: PFSPInstance instance = PFSPInstance.loadFromFile("tai20_5_0.txt");
     */
    public static PFSPInstance loadFromFile(String filePath) throws IOException {
        PFSPInstance inst = new PFSPInstance();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Skip comment/header lines until we find the line with two integers
            // (numJobs and numMachines). Taillard files have a header like:
            // "number of jobs, number of machines, ..."
            // followed by the actual numbers on the next line.
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // The line we want starts with a digit
                if (!line.isEmpty() && Character.isDigit(line.charAt(0))) {
                    Scanner sc = new Scanner(line);
                    inst.numJobs     = sc.nextInt();
                    inst.numMachines = sc.nextInt();
                    // remaining tokens on this line (seed, ub, lb) are ignored
                    sc.close();
                    break;
                }
            }

            // Skip the "processing times :" header line
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && Character.isDigit(line.charAt(0))) {
                    // This is the first row of the matrix — parse it right away
                    inst.processingTime = new int[inst.numMachines][inst.numJobs];
                    parseMachineRow(inst.processingTime, 0, inst.numJobs, line);
                    break;
                }
            }

            // Read the remaining numMachines-1 rows
            for (int m = 1; m < inst.numMachines; m++) {
                line = br.readLine();
                if (line == null) throw new IOException("Unexpected end of file at machine row " + m);
                parseMachineRow(inst.processingTime, m, inst.numJobs, line.trim());
            }
        }

        return inst;
    }

    /** Parses one row of the processing-time matrix into processingTime[machineIndex][*]. */
    private static void parseMachineRow(int[][] matrix, int machineIndex, int numJobs, String line) {
        Scanner sc = new Scanner(line);
        for (int j = 0; j < numJobs; j++) {
            matrix[machineIndex][j] = sc.nextInt();
        }
        sc.close();
    }

    // -------------------------------------------------------------------------
    // The core calculation: completion time of job sequence x
    // -------------------------------------------------------------------------

    /**
     * Computes the TOTAL FLOW TIME (objective function value) for a given job order.
     *
     * Formula from the task description:
     *   c(xj, m) = p(xj, m)                                      if j=0 and m=0
     *            = c(xj, m-1) + p(xj, m)                         if j=0 and m>0
     *            = c(xj-1, m) + p(xj, m)                         if j>0 and m=0
     *            = max(c(xj-1, m), c(xj, m-1)) + p(xj, m)        otherwise
     *
     * Total flow time = sum of c(xj, lastMachine) for all j.
     *
     * @param jobOrder  array of job indices (0-based), length == numJobs
     * @return          total flow time (lower is better)
     */
    public int evaluate(int[] jobOrder) {
        // completionTime[m][j] — we only need the previous job's row, so we use a 1D array
        // and update it in place column by column.
        int[] c = new int[numMachines]; // c[m] = completion time of the CURRENT job on machine m

        int totalFlowTime = 0;

        for (int j = 0; j < numJobs; j++) {
            int job = jobOrder[j];  // which job is at position j

            for (int m = 0; m < numMachines; m++) {
                int p = processingTime[m][job];

                if (j == 0 && m == 0) {
                    c[m] = p;
                } else if (j == 0) {
                    // Only depends on the previous machine for this same job
                    c[m] = c[m - 1] + p;
                } else if (m == 0) {
                    // c[m] still holds the completion time of the PREVIOUS job on machine 0
                    c[m] = c[m] + p;
                } else {
                    // max of: previous job on this machine (c[m]) vs this job on previous machine (c[m-1])
                    c[m] = Math.max(c[m], c[m - 1]) + p;
                }
            }

            // The last machine's completion time for this job contributes to total flow time
            totalFlowTime += c[numMachines - 1];
        }

        return totalFlowTime;
    }

    /**
     * Same as evaluate() but only considers the first 'length' jobs in jobOrder.
     * Used by the Greedy algorithm to score a partial schedule.
     */
    public int evaluatePartial(int[] jobOrder, int length) {
        int[] c = new int[numMachines];
        int totalFlowTime = 0;

        for (int j = 0; j < length; j++) {
            int job = jobOrder[j];
            for (int m = 0; m < numMachines; m++) {
                int p = processingTime[m][job];
                if      (j == 0 && m == 0) c[m] = p;
                else if (j == 0)           c[m] = c[m - 1] + p;
                else if (m == 0)           c[m] = c[m] + p;
                else                       c[m] = Math.max(c[m], c[m - 1]) + p;
            }
            totalFlowTime += c[numMachines - 1];
        }
        return totalFlowTime;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public int getNumJobs()     { return numJobs; }
    public int getNumMachines() { return numMachines; }

    /** processingTime[machine][job], both 0-indexed */
    public int getProcessingTime(int machine, int job) {
        return processingTime[machine][job];
    }

    @Override
    public String toString() {
        return "PFSPInstance{jobs=" + numJobs + ", machines=" + numMachines + "}";
    }
}