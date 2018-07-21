package shc;

import java.util.HashSet;
import java.util.Set;

public class MathCustom {

	public static Set<Integer> getDivisors(int n) {
		Set<Integer> divisors = new HashSet<>();
		if (n > 0) {
			divisors.add(n);
			for (int i = 2; i <= n / 2; i++) {
				if (n % i == 0) {
					divisors.add(i);
				}
			}
			return divisors;
		}
		else {
			return null;
		}
	}

}
