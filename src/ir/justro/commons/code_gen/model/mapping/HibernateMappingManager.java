package ir.justro.commons.code_gen.model.mapping;

import ir.justro.commons.code_gen.model.exception.HibernateDocumentLoadException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HibernateMappingManager {

	private List<File> mappingFiles = null;
	private Map<String, HibernateClass> projectMappings = new HashMap<>();
	private Map<String, HibernateDocument> projectDocuments = new HashMap<>();
	private Map<String, HibernateDocument> classDocuments = new HashMap<>();
	private Map<String, HibernateClass> tableClasses = new HashMap<>();

	private HibernateMappingManager() {
	}
	
	private static HibernateMappingManager instance = null;
	public static HibernateMappingManager getInstance() {
		if (instance == null) 
			instance = new HibernateMappingManager();
		return instance;
	}
	
	/**
	 * Return the HibernateClass associated with the given classname
	 * @param className
	 * @return
	 */
	public HibernateClass getHibernateClass (String className) {
		return (HibernateClass) projectMappings.get(className);
	}

	/**
	 * Return the loaded document associated with the file given
	 * @param file
	 * @return
	 * @throws HibernateDocumentLoadException 
	 */
	public HibernateDocument getHibernateDocument (File file) {
		return (HibernateDocument) projectDocuments.get(file.getName());
	}

	/**
	 * Return the loaded document associated with the fully qualified class name given
	 * @param file
	 * @return
	 */
	public HibernateDocument getHibernateDocument (String className) {
		return (HibernateDocument) classDocuments.get(className);
	}

	/**
	 * Return the HibernateClass associated with the given table name
	 */
	public HibernateClass getHibernateClassByTableName (String table) {
		return (HibernateClass) tableClasses.get(table);
	}

	/**
	 * Return all knows mapping files 
	 * @return a list of File objects
	 */
	public List<File> getFiles () {
		List<File> files = new ArrayList<File>();
		Collection<HibernateDocument> values = projectDocuments.values();
		for (HibernateDocument doc : values) {
			files.add(doc.getFile());
		}
		return files;
	}

	/**
	 * Return all knows mapping files as documents
	 * @return a list of HibernateDocument objects
	 */
	public List<HibernateDocument> getDocuments () {
		List<HibernateDocument> documents = new ArrayList<>();
		for (HibernateDocument doc : projectDocuments.values()) {
			documents.add(doc);
		}
		return documents;
	}

	/**
	 * Return all knows hibernate classes
	 * @return a list of HibernateClasses objects
	 */
	public List<HibernateClass> getClasses () {
		List<HibernateClass> classes = new ArrayList<>();
		for (HibernateClass hc : tableClasses.values()) {
			classes.add(hc);
		}
		return classes;
	}

	/**
	 * Load all mappping files in the same directory as the one given
	 * @param file
	 * @throws HibernateDocumentLoadException 
	 */
	public void load () throws HibernateDocumentLoadException {
			List<HibernateDocument> subclassDocs = new ArrayList<HibernateDocument>();
			List<File> resources = getMappingFiles();
			if (null != resources) {
				for (File file : resources) {
						HibernateDocument document = new HibernateDocument(file);
						if (document.hasTopLevelSubclassNodes()) subclassDocs.add(document);
						cacheDocument(document, file);
				}
				while (subclassDocs.size() > 0) {
					List<HibernateDocument> unloadedSubclasses = new ArrayList<HibernateDocument>();
					boolean subclassAdded = false;
					for (HibernateDocument doc : subclassDocs)
						if (!doc.loadTopLevelSubclasses(projectMappings)) {
							unloadedSubclasses.add(doc);
						}
						else {
							subclassAdded = true;
							cacheDocument(doc, doc.getFile());
						}
					if (!subclassAdded) break;
					else {
						subclassDocs = unloadedSubclasses;
					}
				}
		}
	}

	public void makeMappingFiles(File file) {
		this.resetMappingFiles();
		this.addToMappingFiles(file);		
	}
	
	public void makeMappingFiles(Collection<File> fileList) {
		this.resetMappingFiles();
		for (File file : fileList)
			this.addToMappingFiles(file);
	}
	
	private void resetMappingFiles() {
		if (mappingFiles == null)
			mappingFiles = new ArrayList<File>();
		else
			if (!mappingFiles.isEmpty())
				mappingFiles.clear();
	}
	
	private void addToMappingFiles(File file) {
		mappingFiles.add(file);
	}
	
	/**
	 * Return all mapping resources within the mappings folder
	 * @throws CoreException
	 */
	private List<File> getMappingFiles () {
		return mappingFiles;
	}

	/**
	 * Cache the document as associated HibernateClass objects for re-use
	 * @param document the document
	 * @param file the file
	 */
	private void cacheDocument(HibernateDocument document, File file) {
		List<HibernateClass> classes = document.getClasses();
		if (classes.size() > 0)
			projectDocuments.put(file.getName(), document);
		for (HibernateClass hc : classes) {
			projectMappings.put(hc.getAbsoluteValueObjectClassName(), hc);
			classDocuments.put(hc.getAbsoluteValueObjectClassName(), document);
			tableClasses.put(hc.getTableName(), hc);
		}
	}

	/**
	 * Used for asynchronous processing
	public class Runner extends Thread {

		public Runner () {
		}
		
		public void run () {
			HibernateMappingManager.getInstance(project).load();
		}
	}

	public class Resetter extends Thread {
		public void run () {
			try {
				Thread.sleep(Plugin.STARTUP_WAIT_TIME);
			}
			catch (Exception e) {}
			projectMappings = new HashMap();
			projectDocuments = new HashMap();
			Map classDocuments = new HashMap();
			tableClasses = new HashMap();
			loaded = false;
			loading = false;
		}
	}
	*/
}