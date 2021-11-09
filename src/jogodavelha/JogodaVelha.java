package jogodavelha;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class JogodaVelha extends javax.swing.JFrame implements ActionListener{

	    private JButton[] b = new JButton[9];
	    private GridLayout game_layout; 
	    private BorderLayout main_layout;
	    private JLabel	labelPlacar = new JLabel("Cliente: 0  Servidor:0");
	    private int	gameCounter = 0;
	    
	    public JogodaVelha() {        
	    	//Configura botoes
	    	for (int i=0; i < 9; i++){
	    		b[i] = new javax.swing.JButton();	        		    
	    		b[i].setText("");
	    		b[i].addActionListener(this);
	    	}

	    	//Define o layout do jogo da velha
	    	JPanel panel_game = new JPanel();
	    	game_layout = new GridLayout( 3, 3 );
	    	panel_game.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
	    	panel_game.setLayout(game_layout);
	    	//Adiciona os botoes ao layout do jogo
	    	for (int i=0; i < 9; i++){
	    		panel_game.add(b[i],0);
	    	}
	    	
	    	//Define o layou do score
	    	JPanel panel_score = new JPanel();	
	    	panel_score.add(labelPlacar);
	    	
	    	
	    	//Define o layout principal
	    	//Jogo da velha no centro, placar ao lado direito	    	
	        main_layout = new BorderLayout();
	        getContentPane().setLayout(main_layout);
	        add(panel_game, BorderLayout.CENTER);
	        add(panel_score, BorderLayout.EAST);
		}
	
	    	    
	    public void actionPerformed(ActionEvent e) {
	    	
	    	for (int i=0; i < 9; i++){
	    		if (e.getSource() == b[i]) {
	    			System.out.println("Botao " + i + " pressionado")  			;
	    		}
	    	}
	    }  
	    
	    public void init(){	       
            }
	   	    	    
	    public static void main(String[] args) {
	    	JogodaVelha jogo = new JogodaVelha();
	    	jogo.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    	jogo.setSize( 300, 200 ); // set frame size
	    	jogo.setVisible( true );
	    	jogo.setTitle("Servidor");
	    	jogo.init();
	    }   


}