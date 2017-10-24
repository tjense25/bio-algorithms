package GenomeAssembly;

/**
 * Created by tjense25 on 9/26/17.
 */
public class Util {
    public static String getPrefix(String s) {
        return s.substring(0, s.length() - 1);
    }

    public static String getSuffix(String s) {
        return s.substring(1, s.length());
    }
}
