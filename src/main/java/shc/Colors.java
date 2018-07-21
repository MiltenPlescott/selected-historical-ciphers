package shc;

import java.awt.Color;
import javax.swing.JButton;

import javax.swing.UIManager;

/**
 * Class containing all colors used in the program.
 */
public final class Colors {

	/**
	 * Standard black color.
	 */
	public static final Color BLACK = new Color(0, 0, 0);

	/**
	 * Light grey color used for backgrounds.
	 */
	public static final Color GREY = new Color(240, 240, 240);

	/**
	 * Color of dark shadow of {@link JButton}
	 */
	public static final Color GREYISH = UIManager.getColor("Button.darkShadow");	// R:122 G:138 B:153

	/**
	 * Subtle blue color.
	 */
	public static final Color BLUISH = new Color(88, 139, 193);

	/**
	 * Color used for highlighting.
	 */
	public static final Color GREENISH = new Color(156, 255, 147);

	/**
	 * Almost black.
	 */
	public static final Color DARK_GREY = new Color(51, 51, 51);

	/**
	 * No one is supposed to ever create an object of this class.
	 */
	private Colors() {
		throw new AssertionError();
	}
}
