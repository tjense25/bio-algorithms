package MotifSearch;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Created by tjense25 on 9/19/17.
 */
public class GibbsSampler {
    private static String USAGE = "USAGE: java GibbsSampler input_file\n input_file is the path to a file with given input";
    private int k;
    private int t;
    private int N;
    private String[] dna;
    private int current_overall_score;

    public GibbsSampler(String[] dna, int k, int t, int N) {
        this.k = k;
        this.t = t;
        this.N = N;
        this.dna = dna;
    }

    public static void main(String[] args) {
        if (args.length != 1) System.out.println(USAGE);
        else {
            try {
                Scanner scan = new Scanner(new FileReader(args[0]));
                int k = scan.nextInt();
                int t = scan.nextInt();
                int N = scan.nextInt();
                String[] dna = new String[t];
                for (int i = 0; i < t; i++) {
                    dna[i] = scan.next();
                }
                GibbsSampler sampler = new GibbsSampler(dna, k, t, N);
                List<String> motifs = sampler.iterateGibbsSampler(100);
                for (int i = 0; i < motifs.size(); i++) {
                    System.out.println(motifs.get(i));
                }

            } catch (FileNotFoundException e) {
                System.out.println(USAGE);
            }
        }
    }

    public List<String> iterateGibbsSampler(int repetitions) {
        List<String> best_motifs = new ArrayList<>();
        int best_score = 100;
        for(int i = 0; i < repetitions; i++) {
            List<String> current_motifs = runGibbsSampler();
            if(current_overall_score < best_score) {
                best_score = current_overall_score;
                best_motifs = current_motifs;
            }
        }
        return best_motifs;
    }


    public List<String> runGibbsSampler() {
        List<String> current_motifs = new ArrayList<>();
        GreedyMotifSearch motifSearch = new GreedyMotifSearch(dna, k, t);
        Random rand = new Random();
        for(int i = 0; i < t; i++) {
            int rand_pos = rand.nextInt(dna[i].length() - k);
            current_motifs.add(dna[i].substring(rand_pos, rand_pos + k));
        }
        List<String> best_motifs = current_motifs;
        int best_score = motifSearch.scoreMatrix(best_motifs);
        for(int j = 0; j < N; j++) {
            int i = rand.nextInt(t);
            current_motifs.remove(i);
            String selected_dna = dna[i];
            double[][] profile = motifSearch.createProfileMatrix(current_motifs);
            current_motifs.add(i, getProfileRandomMotif(selected_dna, profile));
            int current_score = motifSearch.scoreMatrix(current_motifs);
            if(current_score < best_score) {
                best_score = current_score;
                best_motifs = current_motifs;
            }
        }
        this.current_overall_score = best_score;
        return best_motifs;
    }

    private String getProfileRandomMotif(String dnaText, double[][] profile) {
        List<Double> probability_vector = new ArrayList<>();
        for(int i = 0; i < dnaText.length() - k + 1; i++) {
            String kmer = dnaText.substring(i, i + k);
            double kmer_p = 1;
            for(int j = 0; j < k; j++) {
                char base = kmer.charAt(j);
                kmer_p *= profile[GenomeMapping.Util.BaseToInt(base)][j];
            }
            probability_vector.add(kmer_p);
        }
        int rand_pos = getWeightedRandom(probability_vector);
        return dnaText.substring(rand_pos, rand_pos + k);
    }

    private int getWeightedRandom(List<Double> probability_vector) {
        double total_prob = 0;
        for(int i = 0; i < probability_vector.size(); i++) {
            total_prob += probability_vector.get(i);
            probability_vector.set(i, total_prob);
        }
        double random = Math.random()*total_prob;
        for(int i = 0; i < probability_vector.size(); i++) {
            if(random < probability_vector.get(i)) {
                return i;
            }
        }
        return -1;
    }

}
