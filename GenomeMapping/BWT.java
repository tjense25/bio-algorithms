package GenomeMapping;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by tjense25 on 11/9/17.
 */
public class BWT {
    private static String USAGE = "ERROR. USAGE:\n" +
            "java BWM [-option] input_data.txt output_file.txt\n" +
            "Options:\n" +
            "-b: produces the Burow-Wheeler Transform of a given String\n" +
            "-r: reconstruct string given the Burrow_wheeler Transform\n +" +
            "-l2f: implement Last to First Mapping for a given Burrow Wheeler Transform and an int i\n " +
            "-bwm: implement BWMatching given a Burrow-Wheeler Transform and a list of patterns to be matched\n" +
            "-pm: implement Multiple Pattern Matching using an optimized BWMatching\n";

    private String text;
    private Set<String> M;
    private String bwt;
    private List<String> first_col = new ArrayList<>();
    private List<String> last_col = new ArrayList<>();
    private int[] lastToFirst;
    private Map<Character, int[]> count_matrix = new HashMap<>();
    private Map<Character, Integer> firstOccurance = new HashMap<>();

    public BWT(String text, boolean is_text) {
        if(is_text) {
            this.text = text;
            setCyclicRotationsMatrix();
        }
        else {
            this.bwt = text;
        }

    }

    private void setCyclicRotationsMatrix() {
        this.M = new TreeSet<>();
        for(int i = 0; i < text.length(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(text.substring(text.length() - i));
            sb.append(text.substring(0, text.length() - i));
            M.add(sb.toString());
        }
    }

    private void setBWT() {
        StringBuilder sb = new StringBuilder();
        for(String rotation : M) {
            sb.append(rotation.charAt(rotation.length() - 1));
        }
        this.bwt = sb.toString();
    }

    private int lastToFirst(int i) {
        return lastToFirst[i];
    }

    private void setFirstLastColumn() {
        Map<Character, Integer> base_count = Util.getBaseCountMap();
        Set<String> base_set = new TreeSet<>(new BaseComp());
        for(Character base : base_count.keySet()) {
            count_matrix.put(base, new int[bwt.length() + 1]);
        }
        for(int i = 0; i < bwt.length(); i++) {
            for(Character countbase : base_count.keySet()) {
                count_matrix.get(countbase)[i] = base_count.get(countbase);
            }
            char base = bwt.charAt(i);
            if(base == '$') {
                last_col.add("$");
                base_set.add("$");
                continue;
            }
            int count = base_count.get(base);
            last_col.add(String.format("%c%d", base, count));
            base_set.add(String.format("%c%d", base, count));
            base_count.replace(base, count + 1);
        }
        for(Character countbase : base_count.keySet()) {
            count_matrix.get(countbase)[bwt.length()] = base_count.get(countbase);
        }
        for(String base : base_set) {
            first_col.add(base);
            if(firstOccurance.containsKey(base.charAt(0))) continue;
            firstOccurance.put(base.charAt(0), first_col.size() - 1);
        }
        lastToFirst = new int[last_col.size()];
        for(int i = 0; i < last_col.size(); i++) {
            if(last_col.get(i).equals("$")) lastToFirst[i] = 0;
            else {
                char base = last_col.get(i).charAt(0);
                int rank = Integer.parseInt(last_col.get(i).substring(1));
                lastToFirst[i] = firstOccurance.get(base) + rank;
            }
        }
    }

    private void setText() {
        setFirstLastColumn();
        StringBuilder sb = new StringBuilder();
        String current_base = "$";
        while(sb.length() < bwt.length()) {
            sb.append(current_base.charAt(0));
            int next_pos = last_col.indexOf(current_base);
            current_base = first_col.get(next_pos);
        }
        sb.append("$");
        String cyclic_rotation = sb.toString();
        this.text = cyclic_rotation.substring(1);
    }

    public int BWMatching(String pattern) {
        StringBuilder pat = new StringBuilder(pattern);
        int top = 0;
        int bottom = last_col.size() - 1;
        while(top <= bottom) {
            if(pat.length() != 0) {
                char symbol = pat.charAt(pat.length() - 1);
                pat = pat.deleteCharAt(pat.length() - 1);
                top = firstOccurance.get(symbol) + count_matrix.get(symbol)[top];
                bottom = firstOccurance.get(symbol) + count_matrix.get(symbol)[bottom + 1] - 1;
            }
            else return bottom - top + 1;
        }
        return 0;
    }

    public String getBWT() {
        return bwt;
    }

    public String getText() {
        return text;
    }

    public static void main(String[] args) {
        if (args.length != 3) System.out.println(USAGE);
        else {
            try {
                if(args[0].equals("-b")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    String text = scan.next() + "$";
                    boolean is_text = true;
                    BWT bwt = new BWT(text, is_text);
                    bwt.setBWT();
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    out.print(bwt.getBWT());
                    scan.close();
                    out.close();
                }
                else if(args[0].equals("-r")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    String bwt = scan.next();
                    boolean is_text = false;
                    BWT Bwt = new BWT(bwt, is_text);
                    Bwt.setText();
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    out.print(Bwt.getText());
                    scan.close();
                    out.close();
                }
                else if(args[0].equals("-l2f")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    String bwt = scan.next();
                    int i = scan.nextInt();
                    boolean is_text = false;
                    BWT Bwt = new BWT(bwt, is_text);
                    Bwt.setFirstLastColumn();
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    out.print(Bwt.lastToFirst(i));
                    scan.close();
                    out.close();
                }
                else if(args[0].equals("-bwm")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    String bwt = scan.next();
                    boolean is_text = false;
                    BWT Bwt = new BWT(bwt, is_text);
                    Bwt.setFirstLastColumn();
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    while(scan.hasNext()) {
                        String pattern = scan.next();
                        out.print(String.format("%d ", Bwt.BWMatching(pattern)));
                    }
                    scan.close();
                    out.close();
                }
                else if(args[0].equals("-pm")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    String text = scan.next();
                    boolean is_text = true;
                    BWT bwt = new BWT(text, is_text);
                    bwt.setBWT();
                    bwt.setFirstLastColumn();
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    out.print(bwt.getBWT());
                    scan.close();
                    out.close();
                }
                else { System.out.println(USAGE); }
            } catch (IOException e) {
                System.out.println("ERROR: NOT A VALID FILE NAME\n" + USAGE);
            }
        }
    }

    class BaseComp implements Comparator<String>{

        @Override
        public int compare(String s1, String s2) {
            if(s1.charAt(0) != s2.charAt(0) || s1.length() == s2.length()) return s1.compareTo(s2);
            else if(s1.length() < s2.length()) return -1;
            else return 1;
        }
    }
}


