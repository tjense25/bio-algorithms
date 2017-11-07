package GenomeMapping;

/**
 * Created by tjense25 on 11/7/17.
 */
public class Util {

    public static int BaseToInt(char B) {
        switch(B) {
            case 'A': return 0;
            case 'C': return 1;
            case 'G': return 2;
            case 'T': return 3;
            default: return -1;
        }
    }

    public static String IntToBase(int i) {
        switch(i) {
            case 0: return "A";
            case 1: return "C";
            case 2: return "G";
            case 3: return "T";
            default: return "";
        }
    }
}
