package connectfour;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * One game mode: play against AI. You go first.
 */

public class ConnectFour extends JFrame implements ActionListener{
    private State current_state;
    private final int n_rows = 6;
    private final int n_columns = 7;
    //private ConnectorAI AI;
    private ArrayList<State> history;
    public Player[] current_players = new Player[2];
    public HashMap<String, Player> players;
    public JComboBox[] difficulty_selectors;
    public String[] current_difficulties = new String[2];
    public Player[] player_list;
    
    public static void main(String[] args){
        ConnectFour app = new ConnectFour();
    }
    
    public ConnectFour(){
        // ** Init Model ** //
        // Board State
        current_state = new State(new int[n_rows][n_columns], false);
        history = new ArrayList<State>();
        history.add(current_state);
        // players
        player_list = new Player[]{new Human(), new ConnectorAI()}; // to add new AI's just add them to this list.
        players = new HashMap<String, Player>();
        for (Player p : player_list){
            players.put(p.name(), p);
        }
        current_players[0] = players.get("Human");
        current_players[1] = players.get("Human");
        
        
        // ** Init GUI ** //
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
        // Spaces
        for(int row = 0; row < n_rows; row++){
            for (int col = 0; col < n_columns; col++){
                Space s = new Space(row, col);
                board_panel.add(s);
            }
        }
        
        // Control Panel //
        JPanel control_panel = new JPanel();
        control_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        control_panel.setLayout(new BoxLayout(control_panel, BoxLayout.X_AXIS));
        master_panel.add(control_panel, BorderLayout.NORTH);
        
        // Button Panel
        JPanel button_panel = new JPanel();
        button_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.Y_AXIS));
        control_panel.add(button_panel, BorderLayout.NORTH);
        // Reset Button
        JButton reset_button = new JButton("Reset");
        button_panel.add(reset_button);
        reset_button.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e){
                reset();
            }
        });
        // Back Button
        JButton back_button = new JButton("Back ");
        button_panel.add(back_button);
        back_button.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e){
                if (history.size() > 1){
                    history.remove(history.size()-1);
                    current_state = history.get(history.size()-1);
                    repaint();
                }
            }
        });
        
        // Player Panel
        JPanel player_panel = new JPanel();
        player_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        player_panel.setLayout(new BoxLayout(player_panel, BoxLayout.Y_AXIS));
        control_panel.add(player_panel, BorderLayout.NORTH);
        // difficulty selector
        
        difficulty_selectors = new DifficultySelector[2];
        for (int i = 0; i < 2; i++){
            JPanel player1_panel = new JPanel();
            player1_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            player1_panel.setLayout(new BoxLayout(player1_panel, BoxLayout.X_AXIS));
            player_panel.add(player1_panel, BorderLayout.NORTH);
            String s = "Player " + Integer.toString(i+1);
            if (i==0){ s += " (Red)     ";}
            else {s += " (Black)  ";}
            
            player1_panel.add(new JLabel(s));
            DifficultySelector ds  = new DifficultySelector(i);
            difficulty_selectors[i] = ds;
            player1_panel.add(new PlayerSelector(i));
            String d = "        Player " + Integer.toString(i+1) + " Difficulty   ";
            player1_panel.add(new JLabel(d));
            player1_panel.add(ds);
        }
        // Timer
        int delay = 1000;
        javax.swing.Timer timer = new javax.swing.Timer(delay, this);
        timer.setRepeats(true);
        timer.start();
        
        setVisible(true);
    }
    
    @Override // Main class's action for timer
    public void actionPerformed(ActionEvent e){
        Player player = current_players[current_state.turn? 1 : 0];
        if (current_state.terminal) return;
        if (!(player instanceof Human)){ // if player isn't human
            makeMove(); // make AI move.
        }
    }
    private class DifficultySelector extends JComboBox{
        int box_number;
        public DifficultySelector(int box_number){
            super();
            this.box_number = box_number;
            this.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    JComboBox cb = (JComboBox) e.getSource();
                    String d = (String) cb.getSelectedItem();
                    current_difficulties[box_number] = d;
                }
            });
        }
    }
    private class PlayerSelector extends JComboBox{
        int box_number;
        public PlayerSelector(int box_number){
            super();
            this.box_number = box_number;
            for (Player p : player_list){
                this.addItem(p.name());
            } 
            current_players[box_number] = players.get((String) getSelectedItem());
            this.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    JComboBox cb = (JComboBox) e.getSource();
                    String player_name = (String) cb.getSelectedItem(); // Get player e.g. Human, AI
                    current_players[box_number] = players.get(player_name); // Set current player
                    JComboBox ds = difficulty_selectors[box_number];
                    ds.removeAllItems();
                    for (String s : players.get(player_name).difficulties()){
                        ds.addItem(s);
                    }
                    ds.setSelectedIndex(0);
                }
            });
            this.setSelectedIndex(0);
        }
    }
    public void reset(){
        current_state = new State(new int[n_rows][n_columns], false);
        history.clear();
        history.add(current_state);
        repaint();
    }
    
    public void makeMove(){ // Whoever's turn it is, make them move.
        int current_turn = current_state.turn? 1 : 0;
        int a = current_players[current_turn].move(current_state, current_difficulties[current_turn]); // find out where player moves
        // Place Piece
        current_state = current_state.Result(a); // update gamestate
        history.add(current_state);  // add this state to history
        repaint(); 
        if (current_state.isTerminal()) announceEndGame(); // Check if game has ended.
    }
    
    private class MoveMaker implements ActionListener{  // Action listener for space buttons to mediate human moves.
        @Override
        public void actionPerformed(ActionEvent e){
            Space s = (Space) e.getSource();
            Player player = current_players[current_state.turn? 1 : 0];
            if (player instanceof Human){ // if current player is human.
                Human hplayer = (Human) player;
                hplayer.action = s.col; // report action human took.
                makeMove(); // make human move.
            }
        }
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
        reset();
    }
}
