package velha;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Classe que implementa o parser de XML do Jogo da Velha.
 */
public class VelhaParser {

	/** Indica se o Modo Debug esta' ativado. */
	private static final boolean DEBUG_ENABLED = false;
	
	/* nome do arquivo schema que valida o XML */
	private static final String SCHEMA_FILENAME = "/velha.xsd";
	
	/* contantes que representam as tags do arquivo XML */
	private static final String NODE_JOGODAVELHA = "JogoDaVelha";
	private static final String NODE_ID = "id";
	private static final String NODE_STATUS = "status";
	private static final String NODE_JOGADA = "jogada";
	private static final String NODE_TABULEIRO = "tabuleiro";
	private static final String NODE_TAB_POS[] = {
			"p0", "p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8"
	};
	
	/**
	 * Retorna um objeto @ref VelhaBean a partir de uma string contendo XML.
	 * @param xmlContent String com o conteudo do XML
	 * @return Objeto da classe @ref VelhaBean, ou null se houve erro.
	 */
	public VelhaBean getVelhaBean(String xmlContent) {

		Document doc = null;
		
		try {
	        /* le conteudo do XML a partir da string */
			InputStream iStream =
				new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
			
	        /* instancia um DocumentBuilder e cria um documento */
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			doc = docBuilder.parse(iStream);

		} catch (Exception e) {
			if (DEBUG_ENABLED) e.printStackTrace(); 
			return null;
		}
		
		/* valida o XML na arvore DOM */
		if (!validateXML(doc, SCHEMA_FILENAME)) {
			return null;
		}
		
		VelhaBean velhaBean = new VelhaBean();
		
        /* percorre a arvore DOM e popula o bean */
		Element nodeJogodaVelha = doc.getDocumentElement();

		Element nodeId = getElement(nodeJogodaVelha,NODE_ID);
		String idStr = getElementValue(nodeId);
		if (idStr != null) {
			idStr = idStr.toUpperCase();
		}
		char id = VelhaEngine.JOGADOR_VAZIO;
		if ("X".equals(idStr))
			id = VelhaEngine.JOGADOR_X;
		else if ("O".equals(idStr))
			id = VelhaEngine.JOGADOR_O;
		velhaBean.setId(id);
		
		Element nodeStatus = getElement(nodeJogodaVelha,NODE_STATUS);
		String statusStr = getElementValue(nodeStatus);
		if (statusStr != null) {
			statusStr = statusStr.toLowerCase();
			velhaBean.setStatus(statusStr);
		}
		
		Element nodeJogada = getElement(nodeJogodaVelha,NODE_JOGADA);
		String jogadaStr = getElementValue(nodeJogada);
		int jogada = -1;
		if (jogadaStr != null && !jogadaStr.isEmpty()) {
			jogada = Integer.parseInt(jogadaStr);
		}
		velhaBean.setJogada(jogada);
		
		Element nodeTabuleiro = getElement(nodeJogodaVelha,NODE_TABULEIRO);
		for (int pos = 0; pos < NODE_TAB_POS.length; pos++) {
			char jogador = VelhaEngine.JOGADOR_VAZIO;
			Element nodeTabPos = getElement(nodeTabuleiro,NODE_TAB_POS[pos]);
			String posStr = getElementValue(nodeTabPos);
			if (posStr != null && !posStr.isEmpty()) {
				posStr = posStr.toUpperCase();
				if (posStr.equals("X"))
					jogador = VelhaEngine.JOGADOR_X;
				else if (posStr.equals("O"))
					jogador = VelhaEngine.JOGADOR_O;
			}
			velhaBean.setPosicao(pos, jogador);
		}
		
		return velhaBean;
	}

	/**
	 * Retorna uma string contendo XML a partir de um objeto @ref VelhaBean.
	 * @param velhaBean Objeto da classe @ref VelhaBean
	 * @return String com o conteudo do XML, ou null se houve erro.
	 */
	public String getVelhaXML(VelhaBean velhaBean) {

		Document doc = null;
		try {
			/* instancia um DocumentBuilder e cria um documento */
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			doc = docBuilder.newDocument();

		} catch (Exception e) {
			if (DEBUG_ENABLED) e.printStackTrace(); 
			return null;
		}
        
        Text textValue = null;
         
        /* monta a arvore DOM */
        Element nodeJogodaVelha = doc.createElement(NODE_JOGODAVELHA);
        doc.appendChild(nodeJogodaVelha);
         
        Element nodeId = doc.createElement(NODE_ID);
        nodeJogodaVelha.appendChild(nodeId);
        textValue = doc.createTextNode(String.valueOf(velhaBean.getId()));
        nodeId.appendChild(textValue);
        
        Element nodeStatus = doc.createElement(NODE_STATUS);
        nodeJogodaVelha.appendChild(nodeStatus);
        textValue = doc.createTextNode(velhaBean.getStatus());
        nodeStatus.appendChild(textValue);
        
        Element nodeJogada = doc.createElement(NODE_JOGADA);
        nodeJogodaVelha.appendChild(nodeJogada);
        if (velhaBean.getJogada() >= 0) {
        	textValue =
        		doc.createTextNode(String.valueOf(velhaBean.getJogada()));
        	nodeJogada.appendChild(textValue);
        }
        
        Element nodeTabuleiro = doc.createElement(NODE_TABULEIRO);
        nodeJogodaVelha.appendChild(nodeTabuleiro);
        
        for (int pos = 0; pos < NODE_TAB_POS.length; pos++) {
        	Element nodeTabPos = doc.createElement(NODE_TAB_POS[pos]);
        	nodeTabuleiro.appendChild(nodeTabPos);
        	if (velhaBean.getPosicao(pos) != VelhaEngine.JOGADOR_VAZIO) {
        		textValue =
        			doc.createTextNode(
        					String.valueOf(velhaBean.getPosicao(pos)));
        		nodeTabPos.appendChild(textValue);
        	}
        }

		/* valida o XML na arvore DOM */
		if (!validateXML(doc, SCHEMA_FILENAME)) {
			return null;
		}
        
        /* retorna uma string a partir da arvore DOM */
        return domToString(doc);
	}

	/**
	 * Retorna um elemento pelo nome.
	 * @param parent Elemento pai.
	 * @param name Nome do elemento.
	 * @return Elemento XML.
	 */
	private Element getElement(Element parent, String name) {

		NodeList elementsList = parent.getElementsByTagName(name);

		if (elementsList == null || elementsList.getLength() <= 0) {
			return null;
		}
		return (Element) elementsList.item(0);
	}

	/**
	 * Retorna o valor de um elemento.
	 * @param element Elemento
	 * @return Valor do elemento, ou null se nao ha' valor.
	 */
	private String getElementValue(Element element) {
		Node node = element.getFirstChild();
		return (node != null) ? node.getNodeValue() : null;
	}

	/**
	 * Valida um XML contido numa arvore DOM a partir de um arquivo de schema.
	 * @param doc Objeto Document contendo a arvore DOM
	 * @param schemaFileName Nome do arquivo de schema (XSD)
	 * @return True se validado, false se houve erro de validacao.
	 */
	private boolean validateXML(Document doc, String schemaFileName) {
		try {
			/* faz validacao do XML com base no schema */
			String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			SchemaFactory factory = SchemaFactory.newInstance(language);
			Schema schema =
				factory.newSchema(getClass().getResource(schemaFileName));
			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(doc));
			return true;
			
		} catch (Exception e) {
			if (DEBUG_ENABLED) e.printStackTrace(); 
			return false;
		}
	}

	/**
	 * Escreve uma arvore DOM (document) para uma string.
	 * @param doc Objeto Document contendo a arvore DOM
	 * @return String com o XML, ou null se houve erro.
	 */
	private String domToString(Document doc) {
		try {
			/* instancia um transformer */
	        TransformerFactory transfac = TransformerFactory.newInstance();
	        Transformer trans = transfac.newTransformer();
	        trans.setOutputProperty(OutputKeys.INDENT, "no");
	        trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

	        /* cria uma string a partir da arvore DOM */
	        StringWriter strWriter = new StringWriter();
	        StreamResult result = new StreamResult(strWriter);
	        DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			return strWriter.toString();
			
		} catch (Exception e) {
			if (DEBUG_ENABLED) e.printStackTrace(); 
			return null;
		}
	}
}
