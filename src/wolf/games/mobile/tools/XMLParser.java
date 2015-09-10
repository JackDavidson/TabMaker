package wolf.games.mobile.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import wolf.games.mobile.shared.TabPortion;

import android.util.Log;

public class XMLParser {
	private Map<String, String> elementsMap;

	public XMLParser() {
		elementsMap = new HashMap<String, String>(40);
	}

	public void loadFile(String fileString) {

		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				String currentContents = "";
				String currentQName = "";

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					currentQName = qName;
					currentContents = "";
					Log.e("stuff", "Start Element :" + qName);
				}

				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					elementsMap.put(qName, currentContents);
					Log.e("stuff", qName + " : " + elementsMap.get(qName));
				}

				@Override
				public void characters(char ch[], int start, int length) throws SAXException {
					// if (!new String(ch, start, length).equals("\n")){
					currentContents += new String(ch, start, length);
					// }
				}
			};

			saxParser.parse(new File(fileString), handler);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String writeToString() {
		// stub
		return null;
	}

	public String getElement(String elementName) {
		return elementsMap.get(elementName);
	}

	public void replaceElement(String elementName, String replacement) {
		// stub
	}

	public void appendElement(String elementName, String appendment) {
		// stub
	}

	public void deleteElement(String elementName, String replacement) {
		// stub
	}
}