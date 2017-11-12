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
            "-b: produces the Burow-Wheeler Transform of a given String";
    private String text;
    private Set<String> M;
    private String bwt;

    public BWT(String text, boolean is_text) {
        if(is_text) {
            this.text = text;
            setCyclicRotationsMatrix();
            setBWT();
        }
        else {
            this.bwt = text;
            setText();
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

    private void setText() {
        Map<Character, Integer> base_count = Util.getBaseCountMap();
        Set<String> base_set = new TreeSet<>(new BaseComp());
        List<String> last_row = new ArrayList<>();
        for(int i = 0; i < bwt.length(); i++) {
            char base = bwt.charAt(i);
            if(base == '$') {
                last_row.add("$");
                base_set.add("$");
                continue;
            }
            int count = base_count.get(base);
            last_row.add(String.format("%c%d", base, count));
            base_set.add(String.format("%c%d", base, count));
            base_count.replace(base, count + 1);
        }
        List<String> first_row = new ArrayList<>();
        first_row.addAll(base_set);
        StringBuilder sb = new StringBuilder();
        String current_base = "$";
        while(sb.length() < bwt.length()) {
            sb.append(current_base.charAt(0));
            int next_pos = last_row.indexOf(current_base);
            current_base = first_row.get(next_pos);
        }
        sb.append("$");
        String cyclic_rotation = sb.toString();
        this.text = cyclic_rotation.substring(1);

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
                    String text = scan.next();
                    boolean is_text = true;
                    BWT bwt = new BWT(text, is_text);
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
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    out.print(Bwt.getText());
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


