package tatai.app.questions;

import java.util.HashMap;

public class Translator {
    // Translates integers into Maori
    private static final HashMap<Integer, String> _maoriDict;
    private static final String _tensJoiner = "mā";
    static {
        _maoriDict = new HashMap<>();
        _maoriDict.put(0, "kore");
        _maoriDict.put(1, "tahi");
        _maoriDict.put(2, "rua");
        _maoriDict.put(3, "toru");
        _maoriDict.put(4, "whā");
        _maoriDict.put(5, "rima");
        _maoriDict.put(6, "ono");
        _maoriDict.put(7, "whitu");
        _maoriDict.put(8, "waru");
        _maoriDict.put(9, "iwa");
        _maoriDict.put(10, "tekau");
    }

    public String toMaori(int number){
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
}
