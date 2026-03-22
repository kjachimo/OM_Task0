package src.algorithm.ea;


public class GAConfig {

    public int    popSize        = 100;   // number of individuals in population
    public int    generations    = 100;   // number of generations to run
    public double crossoverProb  = 0.7;   // Px — probability of crossover
    public double mutationProb   = 0.1;   // Pm — probability of mutation per individual
    public int    tournamentSize = 5;     // Tour — contestants in tournament selection
    public int    elitismCount   = 1;     // how many best individuals pass unchanged
    public String crossoverType  = "OX";  // "OX" or "PMX"
    public String mutationType   = "SWAP"; // "SWAP" or "INVERSION"

    public GAConfig(int popSize, int generations, double crossoverProb,
                    double mutationProb, int tournamentSize, int elitismCount,
                    String crossoverType, String mutationType) {
        this.popSize        = popSize;
        this.generations    = generations;
        this.crossoverProb  = crossoverProb;
        this.mutationProb   = mutationProb;
        this.tournamentSize = tournamentSize;
        this.elitismCount   = elitismCount;
        this.crossoverType  = crossoverType;
        this.mutationType   = mutationType;
    }

    public GAConfig() {}

    @Override
    public String toString() {
        return String.format(
            "GAConfig{pop=%d, gen=%d, Px=%.2f, Pm=%.2f, tour=%d, elitism=%d, cross=%s, mut=%s}",
            popSize, generations, crossoverProb, mutationProb,
            tournamentSize, elitismCount, crossoverType, mutationType);
    }
}