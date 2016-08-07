public class FormatUtil {

    public static String format(String input) {
        String result = "";
        String[] temp = input.split("(?<!^)(?=[A-Z])")
        for (String t : temp) {
            if (result == "") {
                result += t;
            } else {
                result += "_" + t;
            }
        }
        return result.toUpperCase();
    }


    public static String getNameString(String input) {
        String result = "";
        String[] temp = input.split("(?<!^)(?=[A-Z])")
        result = temp[temp.length - 1];
        return result;
    }
}