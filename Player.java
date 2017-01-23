/**
 *
 * @author Lloyd Cloer
 */

package connectfour;

import java.util.ArrayList;

public interface Player {
    public ArrayList<String> difficulties();
    public abstract String name();
    public abstract int move(State s, String difficulty);
}
