package src.algorithm.ea;

/**
 * Snapshot of one generation's statistics.
 * Collected every generation and saved to CSV for report graphs.
 */
public class GenerationStats {
    public final int    generation;
    public final int    best;
    public final double avg;
    public final int    worst;

    public GenerationStats(int generation, int best, double avg, int worst) {
        this.generation = generation;
        this.best       = best;
        this.avg        = avg;
        this.worst      = worst;
    }

    /** CSV header line */
    public static String csvHeader() {
        return "generation,best,avg,worst";
    }

    /** This object as a CSV row */
    public String toCsvRow() {
        return generation + ";  " + best + ";   " + String.format("%.2f", avg) + "; " + worst;
    }
}