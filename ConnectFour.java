package connectfour;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * One game mode: play against AI. You go first.
 */

public class ConnectFour extends JFrame{
    private State current_state;
    private final int n_rows = 6;
    private final int n_columns = 7;
    private ConnectorAI AI;
    
    //private final int cutoff_depth = 7;
    
    public static void main(String[] args){
        ConnectFour app = new ConnectFour();
    }
    
    public ConnectFour(){
        //initModel();
        AI = new ConnectorAI();
        current_state = new State(new int[n_rows][n_columns], false);
        // Frame
        setTitle("Connect Four");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Master Panel
        JPanel master_panel = (JPanel) this.getContentPane();
        master_panel.setLayout(new BorderLayout());
        // Board Panel
        JPanel board_panel = new JPanel();
        board_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        board_panel.setLayout(new GridLayout(n_rows, n_columns, 0, 0));
        master_panel.add(board_panel, BorderLayout.CENTER);
        // Control Panel
        JPanel control_panel = new JPanel();
        control_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        control_panel.setLayout(new BoxLayout(control_panel, BoxLayout.X_AXIS));
        master_panel.add(control_panel, BorderLayout.NORTH); //
        //control_panel.add(new Button("Helloa"));
            // Buttons to do: undo, restart, score, 
            // Other GUI to do: difficult, first move, 1 vs 2 player.
        // Spaces
        for(int row = 0; row < n_rows; row++){
            for (int col = 0; col < n_columns; col++){
                Space s = new Space(row, col);
                board_panel.add(s);
            }
        }
        setVisible(true);
    }
    
    private class MoveMaker implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Space s = (Space) e.getSource();
            if (!current_state.turn) moveHuman(s.col);
        }
    }
    
    private void move(int col){ // Makes a move at given column.
        current_state = current_state.Result(col); // update gamestate
        repaint();
        if (current_state.isTerminal()) announceEndGame(); // Check if game has ended.
    }
    
    private void moveHuman(int col){ // places human's move. Prompts AI to move.
        if (!current_state.legal_actions.contains(col)) return; // if move is invalid, do nothing.
        move(col);
        if (!current_state.turn) return; // if it's still human's move, they won last move.
        move(AI.alphaBetaSearch(current_state)); // AI moves.
    }
    
    public class Space extends JButton{
        public int row;
        public int col;
        public Space(int row, int col){
            super();
            this.row = row;
            this.col = col;
            addActionListener(new MoveMaker());
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            switch (current_state.board[row][col]) {
                case 0:
                    g.setColor(Color.GRAY);
                    break;
                case -1:
                    g.setColor(Color.RED);
                    break;
                case 1:
                    g.setColor(Color.BLACK);
                    break;
                default:
                    throw new Error();
            }
            Dimension d = getSize();
            g.fillOval(5, 5, d.width-10, d.height-10);
            if (current_state.last_move[0] == row && current_state.last_move[1] == col){
                g.setColor(Color.YELLOW);
                ((Graphics2D) g).setStroke(new BasicStroke(5));
                g.drawOval(5, 5, d.width-10, d.height-10);
            }  
        }
    }
    
    private void announceEndGame(){
        if (current_state.win == 1)
            JOptionPane.showMessageDialog(this, "Black Wins!");
        else if (current_state.win == -1)
            JOptionPane.showMessageDialog(this, "Red Wins!");
        else if (current_state.isFull())
            JOptionPane.showMessageDialog(this, "Draw!");
        current_state = new State(new int[n_rows][n_columns], false);
        repaint();
    }
}
