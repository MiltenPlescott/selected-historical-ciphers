//package shc.fersen.attack;

//import shc.fersen.attack.Table;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Method;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import org.junit.BeforeClass;
//
//public class TableTestCompareTo {
//
//	private static Class<?> rowClass;
//	private static Class<?> cellClass;
//
//	private static Object row1;
//	private static Object row2;
//	private static Object row3;
//
//	private static Object cell1;
//	private static Object cell2;
//	private static Object cell3;
//
//	@BeforeClass
//	public static void setUpClass() throws Exception {
//		Table table = new Table(5, "ABCDEFGH");
//		rowClass = Class.forName("shc.FersenAttackTable$Row");
//		Constructor<?> rowConstructor = rowClass.getDeclaredConstructors()[0];
//		rowConstructor.setAccessible(true);
//
//		row1 = rowConstructor.newInstance(table, 1, 'a', 'b');
//		row2 = rowConstructor.newInstance(table, 2, 'a', 'b');
//		row3 = rowConstructor.newInstance(table, 2, 'a', 'b');
//
//		cellClass = Class.forName("shc.FersenAttackTable$Row$Cell");
//		Constructor<?> cellConstructor = cellClass.getDeclaredConstructors()[1];
//		cellConstructor.setAccessible(true);
//
//		cell1 = cellConstructor.newInstance(row1, 'a', 'b');
//		cell2 = cellConstructor.newInstance(row1, 'c', 'b');
//		cell3 = cellConstructor.newInstance(row1, 'a', 'd');
//	}
//
//	@Test
//	public void testCompareToRow() throws Exception {
//		Method compareToRow = rowClass.getDeclaredMethod("compareTo", rowClass);
//		compareToRow.setAccessible(true);
//
//		Object retValue;
//		retValue = compareToRow.invoke(row1, row2);
//		assertEquals(-1, (int) retValue);
//		retValue = compareToRow.invoke(row2, row1);
//		assertEquals(1, (int) retValue);
//		retValue = compareToRow.invoke(row2, row3);
//		assertEquals(0, (int) retValue);
//	}
//
//	@Test
//	public void testCompareToCell() throws Exception {
//		Method compareToCell = cellClass.getDeclaredMethod("compareTo", cellClass);
//		compareToCell.setAccessible(true);
//
//		Object retValue = compareToCell.invoke(cell1, cell2);
//		assertEquals(-1, (int) retValue);
//		retValue = compareToCell.invoke(cell2, cell1);
//		assertEquals(1, (int) retValue);
//		retValue = compareToCell.invoke(cell1, cell3);
//		assertEquals(0, (int) retValue);
//		retValue = compareToCell.invoke(cell3, cell1);
//		assertEquals(0, (int) retValue);
//		retValue = compareToCell.invoke(cell2, cell3);
//		assertEquals(1, (int) retValue);
//		retValue = compareToCell.invoke(cell3, cell2);
//		assertEquals(-1, (int) retValue);
//	}
//
//}
