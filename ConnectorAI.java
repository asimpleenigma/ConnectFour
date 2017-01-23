/*
 */
package connectfour;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

/**
 *
 * @author Lloyd Cloer
 */

public class ConnectorAI implements Player{
    private int cutoff_depth = 7;
    
    @Override
    public ArrayList<String> difficulties(){
        ArrayList<String> d = new ArrayList<>();
        for (int i = 7; i >= 1; i--)
            d.add(Integer.toString(i));
        return d;
    }
    
    @Override
    public String name(){
        return "Connector AI";
    }
    @Override
    public int move(State s, String difficulty){
        boolean empty = true;
        for (int r = 0; r < 6; r++){ // check if board is empty
            for (int c = 0 ; c < 7; c++){
                if (s.board[r][c] != 0)
                    empty = false;
            }
        }
        int ran = (int) (Math.random()*7); // if first move, make random move
        if (empty) return ran;
        cutoff_depth = Integer.parseInt(difficulty);
        
        return alphaBetaSearch(s);
    }
    
    private boolean cutoffTest(State s, int depth){
        return (depth >= cutoff_depth) || s.isTerminal();
    }
    
    public int alphaBetaSearch(State state){ // returns an action
        // find which descendant node has highest value.
        AlteredState s = new AlteredState(state);
        int action = (int) (s.legal_actions.toArray())[0]; // best action to take so far.
        if (state.turn){ // black turn
            double value = Double.NEGATIVE_INFINITY; // highest value any action so far has
            for (int a : s.legal_actions){ // for each action,
                AlteredState descendant = s.Result(a); // create decendant node.
                minValue(descendant, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY); // find value of decendant node.
                if (descendant.effective_utility > value){
                    value = descendant.effective_utility;
                    action = a;
                }
            }
        } else{ // red's turn
            double value = Double.POSITIVE_INFINITY; // highest value any action so far has
            for (int a : s.legal_actions){ // for each action,
                AlteredState descendant = s.Result(a); // create decendant node.
                maxValue(descendant, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY); // find value of decendant node.
                if (descendant.effective_utility < value){
                    value = descendant.effective_utility;
                    action = a;
                }
            }
        }
        return action;
    }
    
    /* maxValue is given a State and it's job is to find the effective_utility of that state.
     *      It find the effective value by taking the maximum of the effective values of the descendant states.
            It won't return anything. It will just alter the effective utility field of the state handed to it.
    */
    private void maxValue(AlteredState s, int depth, double alpha, double beta){
        // alpha = Max's best choice so far. beta = Min's best choice so far.
        if (cutoffTest(s, depth)){  // if too deep, estimate value of position.
            s.effective_utility = s.realized_utility; return;
        }
        double v = Double.NEGATIVE_INFINITY; // v = value of this state.
        for (int a : s.legal_actions){ // for each action, 
            AlteredState descendant = s.Result(a); // create decendant node.
            minValue(descendant, depth+1, alpha, beta); // find value of decendant node.
            v = Math.max(v, descendant.effective_utility); // this state is worth at least as much as descendant node.
            if (v >= beta){ // if this node is worth more for Max than min's best choice so far
                s.effective_utility = v; return; // Min won't let Max get to this node.
            }
            alpha = Math.max(alpha, v);
        }
        s.effective_utility = v;
    }
    private void minValue(AlteredState s, int depth, double alpha, double beta){
        if (cutoffTest(s, depth)){ // if too deep, estimate value of position.
            s.effective_utility = s.realized_utility; return;
        } 
        double v = Double.POSITIVE_INFINITY;
        for (int a : s.legal_actions){
            AlteredState descendant = s.Result(a);
            maxValue(descendant, depth+1, alpha, beta); // find value of descendant node.
            v = Math.min(v, descendant.effective_utility); // this state is worth at most as much as descendant
            if (v <= alpha){ // if this node is worth less than the best we've found so far.
                s.effective_utility = v;
                return; // then Max definitely won't choose it.
            }
            beta = Math.min(beta, v);
        }
        s.effective_utility = v;
    }
    public double featureUtility(int[] feature){
        double black_util = 1;
        double red_util = 1;
        for (int i = 0; i < feature.length; i++){ // cycle thru all elements in feature.
            int n = feature[i];
            black_util *= n+1; // -1 -> 0, 0 -> 1, 1 -> 2
            red_util *= -n+1;  // -1 -> 2, 0 -> 1, 1 -> 0
        }
        if (black_util == 16){ 
            return Double.POSITIVE_INFINITY;
        } // Winning state for Red
        if (red_util == 16){
            return Double.NEGATIVE_INFINITY;
        } // Winning state for Black
        return black_util - red_util;
    }
    
    public class AlteredState extends State{ 
        // derived class so my AI can more effectively examine the consequenses of a state.
        public double realized_utility;
        public double effective_utility;
        public Set<AlteredState> branches;
        
        public AlteredState(State s){
            super(s.board, s.turn);
            utility();
            effective_utility = realized_utility;
            branches = new HashSet<>();
        }
        @Override
        public AlteredState Result(int a){
            State s = super.Result(a);
            return new AlteredState(s);
        }
        
        private void utility(){
            // returns inf if black wins, -inf if red wins.
            // returns sum of feature utility.
            double u = 0; // utility so far.
            int[] feature = new int[4]; // to contain each possible 4 in a row, one at a time.
            // check for horizontal utility
            for (int row = 0; row < n_rows; row++){ // for each row
                for (int col = 0; col < n_columns-3; col++){ // for first 4 columns
                    for (int i = 0; i < 4; i++){
                        feature[i] = this.board[row][col + i];
                    }
                    u += featureUtility(feature);
                }
            } // check for vertical utility
            for (int row = 0; row < n_rows - 3; row++){ //  for first 3 rows
                for (int col = 0; col < n_columns; col++){ // for each column
                    for (int i = 0; i < 4; i++){
                        feature[i] = this.board[row + i][col];
                    }
                    u += featureUtility(feature);
                }
            } // check for downward diagonal utility, top-left to bottom right
            for (int row = 0; row < n_rows - 3; row++){ // first 3 rows
                for (int col = 0; col < n_columns - 3; col++){ // first 4 columns
                    for (int i = 0; i < 4; i++){
                        feature[i] = this.board[row+i][col+i];
                    }
                    u += featureUtility(feature);
                }
            } // check for upward diagonal utility, bottom-left to top right
            for (int row = 3; row < n_rows; row++){ // first 3 rows
                for (int col = 0; col < n_columns - 3; col++){ // first 4 columns
                    for (int i = 0; i < 4; i++){
                        feature[i] = this.board[row-i][col+i];
                    }
                    u += featureUtility(feature);
                }
            }
            if (u != Double.POSITIVE_INFINITY && u != Double.NEGATIVE_INFINITY && isFull()) u = 0; // draw
            this.realized_utility = u;
        }
    }
}
