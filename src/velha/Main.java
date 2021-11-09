package velha;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class Main {

    /** Porta TCP do servidor. */
    private static final int TCP_PORT = 9123;
    /** Tamanho do buffer da sessao, em bytes. */
    private static final int ACCEPTOR_BUFFER_SIZE = 2048;
    
    public static void main(String[] args) {

    	/* inicia o servidor de sockets */
    	IoAcceptor acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

        acceptor.setHandler(new VelhaServerHandler());
        acceptor.getSessionConfig().setReadBufferSize(ACCEPTOR_BUFFER_SIZE);
            try {
                acceptor.bind(new InetSocketAddress(TCP_PORT));
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        if (VelhaServerHandler.DEBUG_ENABLED) {
        	System.out.println("Servidor do Jogo da Velha iniciado!");
        	System.out.println("Aguardando conexoes...");
        }
    }

}
