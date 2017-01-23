
package connectfour;

import java.util.ArrayList;
/**
 *
 * @author Lloyd Cloer
 */
public class Human implements Player{
    public int action = -1;
    
    @Override
    public ArrayList<String> difficulties(){
        ArrayList<String> d = new ArrayList<String>();
        d.add("-");
        return d;
    }
    
    @Override
    public String name(){
        return "Human";
    }
    @Override
    public int move(State s, String difficulty){
        int a = action; // action = -1 signifies Human has moved since they last gave input.
        action = -1;
        return a;
    }
}
