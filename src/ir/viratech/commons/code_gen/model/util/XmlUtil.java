package ir.viratech.commons.code_gen.model.util;

import java.util.Iterator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil {

	public static Iterator<Node> getAttributesIterator(Node node) {
		final NamedNodeMap attributes = node.getAttributes();
		return new Iterator<Node>() {
			private int i = 0;
			
			@Override
			public boolean hasNext() {
				return i<attributes.getLength();
			}
			@Override
			public Node next() {
				return attributes.item(i++);
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public static Iterable<Node> getAttributesIterable(final Node node) {
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator() {
				return getAttributesIterator(node);
			}
		};
	}
	
	
	
	public static String getAttributeValue(Node node, String attributeName) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes == null)
			return null;
		Node namedItem = attributes.getNamedItem(attributeName);
		if (namedItem == null)
			return null;
		return namedItem.getNodeValue();
	}
	
	
	
	
	public static Iterator<Node> getChildrenIterator(Node node) {
		final NodeList childNodes = node.getChildNodes();
		
		return new Iterator<Node>() {
			private int i = 0;
			
			@Override
			public boolean hasNext() {
				return i<childNodes.getLength();
			}
			@Override
			public Node next() {
				return childNodes.item(i++);
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	
	public static Iterable<Node> getChildrenIterable(final Node node) {
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator() {
				return getChildrenIterator(node);
			}
		};
	}
	
}
