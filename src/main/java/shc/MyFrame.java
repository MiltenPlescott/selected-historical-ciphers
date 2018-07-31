/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;

/**
 *
 */
public class MyFrame extends JFrame {

	/**
	 * Screen width in pixels.
	 */
	public static final int SCREEN_WIDTH = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;

	/**
	 * Screen width in pixels.
	 */
	public static final int SCREEN_HEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;

	/**
	 * Creates new frame and sets its name.
	 */
	public MyFrame() {
		super("Selected Historical Ciphers");
		prepareFrame();
	}

	/**
	 * Sets the defual properties of the frame.
	 */
	private void prepareFrame() {
		this.setBackground(Colors.GREY);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// this.setMinimumSize(new Dimension(750, SCREEN_HEIGHT / 3));
		this.setMinimumSize(new Dimension(900, 700));
		
		// this.setBounds(SCREEN_WIDTH / 4, SCREEN_HEIGHT / 9, SCREEN_WIDTH / 2, (int) (SCREEN_HEIGHT / 1.2)); // x, y, width, height
		// setBounds() replaced by setSize() and setLocationRelativeTo()
		this.setSize(SCREEN_WIDTH / 2, (int) (SCREEN_HEIGHT / 1.2));
		this.setLocationRelativeTo(null); // centers the window on the screen; makes specifying x, y coordinate in JFrame.setSize(x,y,w,h) method unnecessary
	}
}
