package MotifSearch;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by tjense25 on 9/18/17.
 */
public class RandomizedMotifSearch {
    private static String USAGE = "USAGE: java Main input_file\n input_file is the path to a file with given input";
    private int k;
    private int t;
    private String[] dna;
    private int current_overall_score;

    public RandomizedMotifSearch(String[] dna, int k, int t) {
        this.k = k;
        this.t = t;
        this.dna = dna;
    }

    public static void main(String[] args) {
        if (args.length != 1) System.out.println(USAGE);
        else {
            try {
                Scanner scan = new Scanner(new FileReader(args[0]));
                int k = scan.nextInt();
                int t = scan.nextInt();
                String[] dna = new String[t];
                for (int i = 0; i < t; i++) {
                    dna[i] = scan.next();
                }
                RandomizedMotifSearch main = new RandomizedMotifSearch(dna, k, t);
                List<String> best_motifs = main.iterateRandomizedMotifSearch(10000);
                for (int i = 0; i < best_motifs.size(); i++) {
                    System.out.println(best_motifs.get(i));
                }

            } catch (FileNotFoundException e) {
                System.out.println(USAGE);
            }
        }
    }

    public List<String> iterateRandomizedMotifSearch(int repetitions) {
        List<String> best_motifs = new ArrayList<>();
        int best_score = 100;
        for(int i = 0; i < repetitions; i++) {
            List<String> motifs = runRandomizedMotifSearch();
            if(current_overall_score < best_score) {
                best_score = current_overall_score;
                best_motifs = motifs;
            }
        }

        return best_motifs;
    }


    public List<String> runRandomizedMotifSearch() {
        List<String> current_motifs = new ArrayList<>();
        List<String> best_motifs = new ArrayList<>();
        GreedyMotifSearch motifSearch = new GreedyMotifSearch(dna, k, t);
        Random rand = new Random();
        for(int i = 0; i < t; i++) {
            int rand_pos = rand.nextInt(dna[i].length() - k);
            current_motifs.add(dna[i].substring(rand_pos, rand_pos + k));
        }
        best_motifs = current_motifs;
        int best_score = motifSearch.scoreMatrix(best_motifs);
        boolean forever = true;

        while(forever) {
            current_motifs = getProfileProbableMotifs(current_motifs, motifSearch);
            int current_score = motifSearch.scoreMatrix(current_motifs);
            if(current_score < best_score) {
                best_motifs = current_motifs;
                best_score = current_score;
            }
            else {
                this.current_overall_score = best_score;
                return best_motifs;
            }
        }

        return best_motifs;
    }

    public List<String> getProfileProbableMotifs(List<String> old_motifs, GreedyMotifSearch motifSearch) {
        List<String> new_motifs = new ArrayList<>();
        for(int i = 0; i < t; i++) {
            double[][] profile = motifSearch.createProfileMatrix(old_motifs);
            MotifFinder finder = new MotifFinder(dna[i], k, profile);
            new_motifs.add(finder.findMotif());
        }

        return new_motifs;
    }



}
