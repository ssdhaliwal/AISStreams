package elsu.ais.resources;

public class Utilities {

	public static boolean floatCompare(Float one, Float two) {
		if (one.compareTo(two) == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean doubleCompare(Double one, Double two) {
		if (one.compareTo(two) == 0) {
			return true;
		} else {
			return false;
		}
	}
}
