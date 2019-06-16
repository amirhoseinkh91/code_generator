package ir.viratech.commons.code_gen.model.mapping;

import ir.viratech.commons.code_gen.model.exception.HibernateDocumentLoadException;
import ir.viratech.commons.code_gen.model.exception.HibernateSynchronizerException;
import ir.viratech.commons.code_gen.model.util.HibernateDOMParser;
import ir.viratech.commons.code_gen.model.util.XmlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class HibernateDocument {
	//public static boolean isHbm2ddl=false;
	
	private Document document;
	private File file;
	private String packageName;
	private String schemaName;
	private List<HibernateClass> classes;
	private List<HibernateQuery> queries;

	private boolean hasTopLevelSubclassNodes;

	/**
	 * Load the document from the file contents
	 * @param file
	 * @throws HibernateDocumentLoadException 
	 */
	public HibernateDocument (File file) throws HibernateDocumentLoadException {
		this.file = file;
		this.load(file);
	}

	/**
	 * Load this document from the given input stream
	 * @param is
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void load (File file) throws HibernateDocumentLoadException {
		classes = new ArrayList<>();
		queries = new ArrayList<>();

		HibernateDOMParser domParser = null; 
		try (InputStream is = new FileInputStream(file)) {
			domParser = new HibernateDOMParser();
			/*
			// I can't use this until I figure out how to get line numbers from the DocumentBuilder
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = dbf.newDocumentBuilder();
			parser.setEntityResolver(new LocalEntityResolver());
			Document doc = parser.parse(is);
			*/
			domParser.parse(new InputSource(is));
			document = domParser.getDocument();
			
			schemaName = null;//TODO: Read the default schema from config 

			Node node = document.getDocumentElement();
			if (null != node) {
				String attrVal = XmlUtil.getAttributeValue(node, "package");
				if (null != attrVal) {
					packageName = attrVal;
					if (packageName.endsWith(".")) {
						packageName = packageName.substring(0, packageName.length() - 1);
					}
				}
				attrVal = XmlUtil.getAttributeValue(node, "schema");
				if (null != attrVal) {
					schemaName = attrVal;
				}
			}
			{
				NodeList nl = document.getElementsByTagName("class");
				classes = new ArrayList<>();
				for (int j=0; j<nl.getLength(); j++) {
					node = nl.item(j);
					classes.add(new HibernateClass(node, packageName, this));
				}
			}
			/*
			if (classes.size() >1) {
			    int parentClasses = 0;
			    HibernateClass parentClass = null;
			    for (Iterator<HibernateClass> i=classes.iterator(); i.hasNext(); ) {
			        HibernateClass hc = i.next();
			        if (!hc.isSubclass()) {
			            parentClass = hc;
			            parentClasses++;
			        }
			    }
			}
			 */
			{
				// subclasses
				NodeList nl = document.getElementsByTagName("subclass");
				hasTopLevelSubclassNodes = (nl.getLength() > 0);
				nl = document.getElementsByTagName("joined-subclass");
				hasTopLevelSubclassNodes = (hasTopLevelSubclassNodes || nl.getLength() > 0);
				nl = document.getElementsByTagName("union-subclass");
				hasTopLevelSubclassNodes = (hasTopLevelSubclassNodes || nl.getLength() > 0);
			}
			NodeList queries = document.getElementsByTagName("query");
			this.queries = new ArrayList<>();
			if (queries.getLength() > 0 && classes.size() > 0) {
				for (int k=0; k<queries.getLength(); k++) {
					Node query = queries.item(k);
					this.queries.add(new HibernateQuery(query, (HibernateClass) classes.get(0)));
				}
			}
			queries = document.getElementsByTagName("sql-query");
			if (queries.getLength() > 0 && classes.size() > 0) {
				for (int k=0; k<queries.getLength(); k++) {
					Node query = queries.item(k);
					this.queries.add(new HibernateQuery(query, (HibernateClass) classes.get(0)));
				}
			}
			if (classes.size() == 1) {
				((HibernateClass) classes.get(0)).setQueries(this.queries);
			}
		} catch (IOException e) {
			throw new HibernateDocumentLoadException(e);
		} catch (SAXException e) {
			throw new HibernateDocumentLoadException(e);
		} catch (HibernateSynchronizerException e) {
			if (null != e.getNode()) {
				Integer lineNumber = domParser.getLineNumber(e.getNode());
				if (null != lineNumber) e.setLineNumber(lineNumber.intValue());
			}
			throw e;
		}
	}

	private boolean loadSubclassByTagName(Map<String, HibernateClass> classes, String tagName) {
		NodeList nl = document.getElementsByTagName(tagName);
		List<Node> unknownSubclasses = new ArrayList<>();
		Map<String, HibernateClass> localClasses = new HashMap<>();
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			String attrVal = XmlUtil.getAttributeValue(node, "extends");
			if (null != attrVal) {
				String parentClassName = attrVal;
				if (parentClassName.indexOf('.') < 0 && null != packageName) {
					parentClassName = getPackageName() + "." + parentClassName;
				}
				HibernateClass hc = (HibernateClass) classes.get(parentClassName);
				if (null == hc) {
					for (Iterator<HibernateClass> iter=this.classes.iterator(); iter.hasNext(); ) {
						HibernateClass tmp = iter.next();
						if (tmp.getAbsoluteValueObjectClassName().equals(parentClassName)) {
							hc = tmp;
							break;
						}
					}
				}
				if (null != hc) {
					HibernateClass subclass = new HibernateClass(node, packageName, hc, true, HibernateClass.TYPE_SUBCLASS, this);
					hc.addSubclass(subclass);
					this.classes.add(subclass);
					localClasses.put(subclass.getAbsoluteValueObjectClassName(), subclass);
				}
				else {
					//unknownSubclasses.add(node);
					return false;
				}
			}
		}
		// load lower level subclasses
		boolean doContinue = true;
		while (doContinue && unknownSubclasses.size() > 0) {
			int addedAmount = 0;
			for (Node node : unknownSubclasses) {
				String attrVal = XmlUtil.getAttributeValue(node, "extends");
				if (null != attrVal) {
					String parentClassName = attrVal;
					if (parentClassName.indexOf('.') < 0 && null != packageName) {
						parentClassName = getPackageName() + "." + parentClassName;
					}
					HibernateClass hc = (HibernateClass) localClasses.get(parentClassName);
					if (null != hc) {
						HibernateClass subclass = new HibernateClass(node, packageName, hc, true, HibernateClass.TYPE_SUBCLASS, this);
						hc.addSubclass(subclass);
						this.classes.add(subclass);
						//classes.put(subclass.getValueObjectClassName(), subclass);
						addedAmount ++;
						unknownSubclasses.remove(node);
						localClasses.put(subclass.getAbsoluteValueObjectClassName(), hc);
					}
				}
			}
			if (addedAmount == 0) doContinue = false;
		}
		return true;
	}

	/**
	 * Load this document from the given input stream
	 * @param is
	 */
	public boolean loadTopLevelSubclasses (Map<String, HibernateClass> classes)  {
		if (!loadSubclassByTagName(classes, "joined-subclass")
			|| !loadSubclassByTagName(classes, "union-subclass")
			|| !loadSubclassByTagName(classes, "subclass"))
			return false;
		
		if (this.classes.size() == 1) {
			NodeList queries = document.getElementsByTagName("query");
			this.queries = new ArrayList<>();
			if (queries.getLength() > 0 && classes.size() > 0) {
				for (int k=0; k<queries.getLength(); k++) {
					Node query = queries.item(k);
					this.queries.add(new HibernateQuery(query, (HibernateClass) this.classes.get(0)));
				}
			}
			queries = document.getElementsByTagName("sql-query");
			if (queries.getLength() > 0 && classes.size() > 0) {
				for (int k=0; k<queries.getLength(); k++) {
					Node query = queries.item(k);
					this.queries.add(new HibernateQuery(query, (HibernateClass) this.classes.get(0)));
				}
			}
			((HibernateClass) this.classes.get(0)).setQueries(this.queries);
		}
		else {
			// see if there is one parent class
			int parentCount = 0;
			HibernateClass singleParent = null;
			for (Iterator<HibernateClass> i=this.classes.iterator(); i.hasNext(); ) {
				HibernateClass hc = (HibernateClass) i.next();
				if (!hc.isSubclass()) {
					parentCount++;
					singleParent = hc;
				}
			}
			if (parentCount == 1) singleParent.setQueries(this.queries);
		}
		return true;
	}

	public List<HibernateClass> getClasses() {
		return classes;
	}

	public List<HibernateQuery> getQueries() {
		return queries;
	}

	/**
	 * @return Returns the file.
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * @return Returns the packageName.
	 */
	public String getPackageName() {
		return packageName;
	}

	public String getSchemaName() {
		return this.schemaName;
	}

	/**
	 * @return Returns the hasTopLevelSubclassNodes.
	 */
	public boolean hasTopLevelSubclassNodes() {
		return hasTopLevelSubclassNodes;
	}

	/**
	 * Return the XML document relating to this mapping file
	 */
	public Document getDocument () {
		return document;
	}
	
	public void addClass (HibernateClass hc) {
	    if (null == classes) classes = new ArrayList<>();
	    classes.add(hc);
	}

	public class LocalEntityResolver implements EntityResolver {
		/* (non-Javadoc)
		 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
		 */
		public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException {
			return new InputSource(getClass().getClassLoader().getResourceAsStream("/hibernate-mapping.dtd"));
		}
	}

	public void postProcess() {
		for (HibernateClass hClass : this.getClasses()) {
			hClass.postProcess();
		}
	}

}