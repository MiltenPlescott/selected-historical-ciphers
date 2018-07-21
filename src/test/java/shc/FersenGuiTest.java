/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

//import shc.FersenGui;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class FersenGuiTest {

	private static final String EXPECTED_WHOLE = "RENONPUNRE";
	private static final String EXPECTED_CT = "RNNUR";
	private static final String EXPECTED_PT = "EOPNE";

	@BeforeClass
	public static void setUpClass() {
	}

	@Before
	public void setUp() {
	}

	@Test
	public void testLoadOriginalTextParagraphs() {
		assertEquals(EXPECTED_WHOLE, FersenGui.loadOriginalTextParagraphs(FersenGui.Case.UPPER, FersenGui.TextType.WHOLE).get(0).substring(0, 10));
		assertEquals(EXPECTED_CT, FersenGui.loadOriginalTextParagraphs(FersenGui.Case.UPPER, FersenGui.TextType.CT).get(0).substring(0, 5));
		assertEquals(EXPECTED_PT, FersenGui.loadOriginalTextParagraphs(FersenGui.Case.UPPER, FersenGui.TextType.PT).get(0).substring(0, 5));

		assertEquals(EXPECTED_WHOLE.toLowerCase(), FersenGui.loadOriginalTextParagraphs(FersenGui.Case.LOWER, FersenGui.TextType.WHOLE).get(0).substring(0, 10));
		assertEquals(EXPECTED_CT.toLowerCase(), FersenGui.loadOriginalTextParagraphs(FersenGui.Case.LOWER, FersenGui.TextType.CT).get(0).substring(0, 5));
		assertEquals(EXPECTED_PT.toLowerCase(), FersenGui.loadOriginalTextParagraphs(FersenGui.Case.LOWER, FersenGui.TextType.PT).get(0).substring(0, 5));
	}

}
