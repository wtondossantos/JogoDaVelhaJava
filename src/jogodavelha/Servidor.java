package jogodavelha;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


/*Thread que recebe mensagens do socket*/
class ThreadEscutaServer extends Thread {
	Socket	 		s;
	Servidor 	t;
	ThreadEscutaServer(Socket socket, Servidor talkGUI) {
		s = socket;
		t = talkGUI;
	}
	
	public void run() {
		try {
			while (true) {
				//Aguarda a resposta
				InputStream istream = s.getInputStream();
				InputStreamReader reader = new InputStreamReader(istream); 
				BufferedReader br = new BufferedReader(reader);  
				String msg = br.readLine();
				t.setMsg(msg);
				
				t.addStrTextArea(); 
				Thread.sleep(200);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

public class Servidor extends javax.swing.JFrame implements ActionListener{

    private 	JButton 		jButton1;
    private 	JButton 		jButton2;
    private 	JScrollPane 	jScrollPane1;
    private 	JTextArea 		jTextArea1;
    private 	JTextField 		jTextField1;
    private 	BorderLayout 	layout; 
    private 	Socket  		socket;
    private		OutputStream 	ostream;
    private		String			msg;    //msg precisa ser private para ser usada com invokeLater
    private JButton[] b = new JButton[9];
	    private GridLayout game_layout; 
	    private BorderLayout main_layout;
	    private JLabel	labelPlacar = new JLabel("Cliente: 0  Servidor:0");
	    private int	gameCounter = 0;
    public Servidor(Socket 	s) {        
//		jTextField1 = new javax.swing.JTextField();
//        jScrollPane1 = new javax.swing.JScrollPane();
//        jTextArea1 = new javax.swing.JTextArea();
//        jButton1 = new javax.swing.JButton();
//        jButton2 = new javax.swing.JButton();
		socket = s;
//	    
//        jTextArea1.setColumns(20);
//        jTextArea1.setEditable(false);
//        jTextArea1.setRows(5);
//        jScrollPane1.setViewportView(jTextArea1);
//
//        jButton1.setText("Enviar");
//        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
//            public void mouseClicked(java.awt.event.MouseEvent evt) {
//                jButton1MouseClicked(evt);
//            }
//        });
//
//        jButton2.setText("Desconectar");
//        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
//            public void mouseClicked(java.awt.event.MouseEvent evt) {
//                jButton2MouseClicked(evt);
//            }
//        });
//			
//		layout = new BorderLayout( 5, 5 ); 
//        getContentPane().setLayout(layout);
//		add( jScrollPane1, BorderLayout.CENTER ); 
//		add( jTextField1, BorderLayout.SOUTH ); 
//		add( jButton1, BorderLayout.EAST ); 
//		add( jButton2, BorderLayout.NORTH ); 
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

    private void init() {
    	setTitle("Server");
    	try {
			ostream = socket.getOutputStream();
		} catch (IOException e) {
			System.out.println("Não conseguiu pegar outputstream");
		}
    }
    
//    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {
//    		//Popula a area de texto com o conteundo do campo de texto
//        	setMsg(jTextField1.getText());
//    		addStrTextArea();       
//    		
//    		//Envia Mensagem via socket    		
//    		PrintWriter pw = new PrintWriter(ostream, true); 
//    		pw.println(msg);
//    		
//			//Limpa o campo de texto usando invokeLater
//    		//assim nao tem perigo de conflitarmos com a 
//    		//Thread do swing
//			SwingUtilities.invokeLater(new Runnable() {
//				public void run() {
//					jTextField1.setText(null);
//				}
//			});
//    }
    
    //Necessário para que a threadEscuta possa acessar a variável msg
    public void setMsg(String message) {
    	msg = message;    	
    }

    /* //Atualiza a area de texto com invokeLater
    public void addStrTextArea() {        
        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jTextArea1.append(msg.trim() + "\n");
			}
		});
    }
    */
    
    //Atualiza a area de texto com invokeLater
    public void addStrTextArea() {        
    
    	SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
    	    	@Override
    	    	protected String doInBackground() throws Exception {
    	    		return msg;
    	    	}
    	    	
    	    	// Aqui podemos atualizar a GUI sem problemas
    	    	protected void done() {
		    		String message;
		    		try {
		    			message = get();
		    			jTextArea1.append(message.trim() + "\n");
		    		} catch (Exception e) {		    		
		    		}
    	    	}
    	};    	
    	worker.execute();
    }
        
//    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {
//		//A Fazer
//    }
    
    public static void main(String[] args) {
		try {
			//Cria o socket cliente, o jframe e a thread
			ServerSocket 		ss 	= new ServerSocket(1234);
			JOptionPane.showMessageDialog(null, "Aguardando cliente. Clique Ok!", null, JOptionPane.PLAIN_MESSAGE , null);
			Socket 		 		socket = ss.accept();
			Servidor talkGUI = new Servidor(socket);
			ThreadEscutaServer 	tescuta = new ThreadEscutaServer(socket, talkGUI);
			
			//Configura a janela e torna ela visivel
			talkGUI.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			talkGUI.setSize( 300, 200 ); // set frame size
			talkGUI.init();
	        talkGUI.setVisible( true );
	        //Inicia a thread
			tescuta.start();
		} catch (Exception e) {
			System.out.println("Falha ao conectar com servidor...");
		}     
                
    }   
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
	    	for (int i=0; i < 9; i++){
	    		if (e.getSource() == b[i]) {
	    			System.out.println("Botao " + i + " pressionado")  			;
                                b[i].setText("X");
                                setMsg("X");
	    		}
	    	}
    }
}

