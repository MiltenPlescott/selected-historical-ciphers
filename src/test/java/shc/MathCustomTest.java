package shc;

//import shc.MathCustom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MathCustomTest {

	private static Map<Integer, Set<Integer>> map = new HashMap<>(14);

	@BeforeClass
	public static void setUpClass() {
		map.put(-1, null);
		map.put(0, null);
		map.put(1, new HashSet<>(Arrays.asList(1)));
		map.put(2, new HashSet<>(Arrays.asList(2)));
		map.put(3, new HashSet<>(Arrays.asList(3)));
		map.put(4, new HashSet<>(Arrays.asList(2, 4)));
		map.put(5, new HashSet<>(Arrays.asList(5)));
		map.put(6, new HashSet<>(Arrays.asList(2, 3, 6)));
		map.put(7, new HashSet<>(Arrays.asList(7)));
		map.put(8, new HashSet<>(Arrays.asList(2, 4, 8)));
		map.put(9, new HashSet<>(Arrays.asList(3, 9)));
		map.put(10, new HashSet<>(Arrays.asList(2, 5, 10)));
		map.put(11, new HashSet<>(Arrays.asList(11)));
		map.put(12, new HashSet<>(Arrays.asList(2, 3, 4, 6, 12)));
	}

	@Test
	public void getDivisorsShouldReturnSetOfDivisorsOfPositiveInteger() {
		for (int i : map.keySet()) {
			assertEquals(map.get(i), MathCustom.getDivisors(i));
		}
	}

}
