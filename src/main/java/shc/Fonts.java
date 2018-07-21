/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

import java.awt.Font;

/**
 * Class containing all fonts used in the program.
 *
 */
public final class Fonts {

	/**
	 * Plain sans serif font of size 18.
	 */
	public static final Font SANS = new Font(Font.SANS_SERIF, Font.PLAIN, 18);

	/**
	 * Plain monospaced font of size 18.
	 */
	public static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 18);

	/**
	 * Bold monospaced font of size 18.
	 */
	public static final Font MONO18B = new Font(Font.MONOSPACED, Font.BOLD, 18);

	/**
	 * Plain monospaced font of size 20.
	 */
	public static final Font MONOT = new Font(Font.MONOSPACED, Font.PLAIN, 20);

	/**
	 * Bold monospaced font of size 23.
	 */
	public static final Font MONOH = new Font(Font.MONOSPACED, Font.BOLD, 23);

	/**
	 * Bold monospaced font of size 30.
	 */
	public static final Font MONOB = new Font(Font.MONOSPACED, Font.BOLD, 30);

	/**
	 * No one is supposed to ever create an object of this class.
	 */
	private Fonts() {
		throw new AssertionError();
	}
}
