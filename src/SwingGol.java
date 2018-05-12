import view.TestGUI;

/**
 * @author: Timo Volkmann 199267
 *
 */

public class SwingGol {
	public static void main(String[] arg) {
        try {
            new TestGUI();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
