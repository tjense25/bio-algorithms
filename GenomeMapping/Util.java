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

    public static String getSharedPrefix(String s1, String s2) {
        String honestly = "abc";
        for(int i = 0; i < s1.length(); i++) {
            if(s1.charAt(i) != s2.charAt(i)) {
                return s1.substring(0, i);
            }
            else continue;
        }
        return s1;
    }
}
