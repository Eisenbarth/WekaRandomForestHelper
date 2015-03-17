public class Utils {

    public static String join(String[] strArray, String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArray.length; i++) {
            sb.append(strArray[i]);
            if (i != strArray.length - 1)
                sb.append(delim);
        }
        return sb.toString();
    }
}
