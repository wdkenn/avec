package kennedy.walk.avec;

import java.util.Comparator;

public class IntComparable implements Comparator<Integer>{

	@Override
	public int compare(Integer lhs, Integer rhs) {
		if (lhs == rhs) {
			return 0;
		} else if (lhs > rhs) {
			return -1;
		} else {
			return 1;
		}
		
	}

}
