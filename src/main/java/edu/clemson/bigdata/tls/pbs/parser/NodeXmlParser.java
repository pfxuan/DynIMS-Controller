package edu.clemson.bigdata.tls.pbs.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.input.CharSequenceInputStream;
import org.xml.sax.SAXException;

import edu.clemson.bigdata.tls.pbs.model.Node;

/**
 * XML SAX parser for nodes.
 *
 */
public class NodeXmlParser implements Parser<String, List<Node>> {

    /*
     * (non-Javadoc)
     * @see com.tupilabs.pbs.parser.Parser#parse(java.lang.Object)
     */
    @Override
    public List<Node> parse(String xml) throws ParseException {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();
            final NodeXmlHandler handler = new NodeXmlHandler();

            saxParser.parse(new CharSequenceInputStream(xml, Charset.defaultCharset()), handler);

            return handler.getNodes();
        } catch (IOException ioe) {
            throw new ParseException(ioe);
        } catch (SAXException e) {
            throw new ParseException(e);
        } catch (ParserConfigurationException e) {
            throw new ParseException(e);
        }
    }

}
