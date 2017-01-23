

package connectfour;

import java.util.HashSet;
import java.util.Set;

/**@author Lloyd Cloer
 */

public class State{
    public int[][] board;
    public final int n_rows = 6;
    public final int n_columns = 7;
    public boolean turn;
    public Set<Integer> legal_actions;
    public boolean full;
    public int win;
    public boolean terminal;
    //public int action; // the action that created this state
    public int[] last_move;
    
    public State(int[][] board, boolean turn){
        this.board = board;
        this.turn = turn;
        this.last_move = new int[]{-1, -1};
        possibleActions();
        full = legal_actions.isEmpty();   // If there are no legal actions, board is full.
        win = checkForWin();
        terminal = full || (win != 0); // is a terminal state if board is full or there is a win.
    }

    public State Result(int act){ // returns state resulting from action
        assert legal_actions.contains(act); // assumes action is valid.
        //this.action = act; // save the action that created this state
        int[][] b = new int[n_rows][n_columns]; // create new board.
        for (int i = 0; i < this.board.length; i++) // deep copy.
            b[i] = this.board[i].clone();
        int piece;              // encode turn to piece
        if (this.turn) piece = 1;  
        else piece = -1;
        int row = n_rows - 1;   // start a bottom.
        while (b[row][act] != 0) // find first unoccupied row
            row--;
        b[row][act] = piece; // place piece
        State s  = new State(b, !this.turn);
        s.last_move[0] = row;
        s.last_move[1] = act;
        return s;
    }

    public void possibleActions(){
        Set<Integer> move_set = new HashSet<>();
        for (int col = 0; col < n_columns; col++){
            if (this.board[0][col] == 0)
                move_set.add(col);
        }
        this.legal_actions = move_set;
    }
    public boolean isFull(){ return full; }
    public boolean isTerminal(){ return terminal; }
    
    public int checkForWin(){ // returns 1 if red wins, -1 if black wins, 0 if neither win.
        int sum = 0;
        // check for horizontal win
        for (int row = 0; row < n_rows; row++){ // for each row
            for (int col = 0; col < n_columns-3; col++){ // for first 4 columns
                for (int i = 0; i < 4; i++){
                    sum += this.board[row][col + i];
                }
                if (sum == 4) return 1;
                if (sum == -4) return -1;
                sum = 0;
            }
        } // check for vertical win
        for (int row = 0; row < n_rows - 3; row++){ //  for first 3 rows
            for (int col = 0; col < n_columns; col++){ // for each column
                for (int i = 0; i < 4; i++){
                    sum += this.board[row + i][col];
                }
                if (sum == 4) return 1;
                if (sum == -4) return -1;
                sum = 0;
            }
        } // check for downward diagonal win, top-left to bottom right
        for (int row = 0; row < n_rows - 3; row++){ // first 3 rows
            for (int col = 0; col < n_columns - 3; col++){ // first 4 columns
                for (int i = 0; i < 4; i++){
                    sum += this.board[row+i][col+i];
                }
                if (sum == 4) return 1;
                if (sum == -4) return -1;
                sum = 0;
            }
        } // check for upward diagonal win, bottom-left to top right
        for (int row = 3; row < n_rows; row++){ // first 3 rows
            for (int col = 0; col < n_columns - 3; col++){ // first 4 columns
                for (int i = 0; i < 4; i++){
                    sum += this.board[row-i][col+i];
                }
                if (sum == 4) return 1;
                if (sum == -4) return -1;
                sum = 0;
            }
        }
        return 0;
    }
}

