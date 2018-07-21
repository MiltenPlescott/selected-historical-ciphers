/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc.fersen.attack;

import shc.FersenGui;
import java.util.List;

public class Text {

	public static final List<String> ORIG_PARAS = FersenGui.loadOriginalTextParagraphs(FersenGui.Case.UPPER, FersenGui.TextType.WHOLE);
	public static final String FIRST_PARA = ORIG_PARAS.get(0);

	private String solvedSoFar = ""; // this branch's decrypted text from firstPara.0 to the end of current position of brute force index
	public final Mask mask = new Mask(FIRST_PARA.length()); // mask of first paragraph

	public Text() {
	}

	public void addNextSolvedPart(String nextSolvedPart) {
		this.solvedSoFar += nextSolvedPart;
	}

	public String getSolvedSoFar() {
		return solvedSoFar;
	}

	public void setSolvedSoFar(String solvedSoFar) {
		this.solvedSoFar = solvedSoFar;
	}

}
