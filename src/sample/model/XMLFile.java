package sample.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XMLFile {
    private final String filePath;

    public XMLFile(String filePath) {
        this.filePath = filePath;
    }

    public void writeFile(String username, String password, String url) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element root = document.createElement("settings");
        Element user = document.createElement("config");
        Element pass = document.createElement("config");
        Element adr = document.createElement("config");
        user.setAttribute("name", "username");
        user.setAttribute("value", username);
        pass.setAttribute("name", "password");
        pass.setAttribute("value", password);
        adr.setAttribute("name", "url");
        adr.setAttribute("value", url);
        root.appendChild(user);
        root.appendChild(pass);
        root.appendChild(adr);
        document.appendChild(root);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(filePath));
        transformer.transform(domSource, streamResult);
    }

    public Map<String, String> readFile() throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(filePath));
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        NodeList nodes = root.getElementsByTagName("config");

        Map<String, String> dictionary = new HashMap<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Element tmp = (Element) nodes.item(i);
            dictionary.put(tmp.getAttribute("name"), tmp.getAttribute("value"));
        }

        return dictionary;
    }
}
