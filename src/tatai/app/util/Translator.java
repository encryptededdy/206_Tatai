package tatai.app.util;

import java.util.HashMap;

/**
 * Translates integers into Maori strings, supports 0 through 99
 *
 * @author Edward
 */
public class Translator {
    private static final HashMap<Integer, String> _maoriDict;
    private static final String _tensJoiner = "maa";
    static {
        _maoriDict = new HashMap<>(); // data for the translator
        _maoriDict.put(0, "kore");
        _maoriDict.put(1, "tahi");
        _maoriDict.put(2, "rua");
        _maoriDict.put(3, "toru");
        _maoriDict.put(4, "whaa");
        _maoriDict.put(5, "rima");
        _maoriDict.put(6, "ono");
        _maoriDict.put(7, "whitu");
        _maoriDict.put(8, "waru");
        _maoriDict.put(9, "iwa");
        _maoriDict.put(10, "tekau");
    }

    /**
     * Performs the translation from int to String (Maori)
     * @param number    the integer to be translated, must be between 0 and 99
     * @return The number, translated to maori
     */
    public static String toMaori(int number){
        if (number < 0) {
            throw new UnsupportedOperationException("Negative numbers unsupported");
        } else if (number <= 10) {
            return _maoriDict.get(number);
        } else if (number < 100) {
            int tens = number/10;
            int ones = number%10;
            String tensOp;
            if (tens == 1) {
                tensOp = _maoriDict.get(10);
            } else {
                tensOp = _maoriDict.get(tens) + " " + _maoriDict.get(10);
            }

            if (ones == 0) {
                return tensOp;
            } else {
                return tensOp + " " + _tensJoiner + " " + _maoriDict.get(ones);
            }

        } else {
            throw new UnsupportedOperationException("Numbers over 100 unsupported");
        }
    }

    /**
     * Translate to Maori, in the displayable format
     * @param number The number to translate
     * @return String in Maori
     */
    public static String toMaoriDisplayable(int number) {
        return toDisplayable(toMaori(number));
    }

    /**
     * Convert maori into displayable format, correctly using macrons
     * @param input The phrase to make displayable
     * @return Maori displayable format
     */
    public static String toDisplayable(String input){
        return input.replace("aa", "ā");
    }
}
