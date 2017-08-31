package tatai.app.questions;

import java.util.HashMap;

public class Translator {
    private static final HashMap<Integer, String> _maoriDict;
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
        } else {
            throw new UnsupportedOperationException("Numbers over 10 not implemented yet");
        }
    }
}
