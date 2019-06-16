package ir.justro.commons.code_gen.model.resource;

import ir.viratech.commons.code_gen.hsynch.Constants;
import ir.viratech.commons.code_gen.model.ModelGenerationConfigurationHolder;
import ir.viratech.commons.code_gen.model.util.HSUtil;
import ir.viratech.commons.code_gen.model.util.Synchronizer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.io.IOUtils;

public class ResourceManager {

	//private static final String PATH_TEMPLATES = Constants.HS_PATH_BASE+"/"+Constants.TEMPLATES_DIR_NAME;
	//private static final String PATH_SNIPPETS = Constants.HS_PATH_BASE+"/"+Constants.SNIPPETS_DIR_NAME;
	//private static final String TEMPLATE_LOC = "/"+Constants.TEMPLATES_DIR_NAME+"/";
	//private static final String SNIPPET_LOC = "/"+Constants.SNIPPETS_DIR_NAME+"/";

	private static String[] ALL_TEMPLATES = {
			Synchronizer.TEMPLATE_BASE_VALUE_OBJECT,
			Synchronizer.TEMPLATE_VALUE_OBJECT,
			Synchronizer.TEMPLATE_VALUE_OBJECT_PROXY,
			Synchronizer.TEMPLATE_VALUE_OBJECT_PROXY_CONTENTS,
			Synchronizer.TEMPLATE_VALUE_OBJECT_PK,
			Synchronizer.TEMPLATE_BASE_VALUE_OBJECT_PK,
			Synchronizer.TEMPLATE_BASE_ROOT_DAO,
			Synchronizer.TEMPLATE_ROOT_DAO,
			Synchronizer.TEMPLATE_BASE_DAO,
			Synchronizer.TEMPLATE_DAO,
			Synchronizer.TEMPLATE_BASE_MGR,
			Synchronizer.TEMPLATE_MGR,
			Synchronizer.TEMPLATE_IDAO,
	};

	/*
	private static String[] NON_REQUIRED_TEMPLATES = {
		Synchronizer.TEMPLATE_SPRING_CONFIG,
	};
	*/

	private static String[] ALL_SNIPPETS = {
			// TODO add all of the snippets
			"BaseDAOImports",
			"BaseDAOClassComments",
			"BaseDAOClassDefinition",
			"BaseDAOQueryNames",
			"BaseDAOInstanceMethod",
			"BaseDAORequiredMethods",
			"BaseDAOCustomContents",
			"BaseDAOClassConstructors",
			"BaseDAOFinderMethods",
			"BaseDAOActionMethods",

			"BaseMGRImports",
			"BaseMGRClassComments",
			"BaseMGRClassDefinition",
			"BaseMGRInstanceMethod",
			"BaseMGRDAODefinition",
			"BaseMGRDAOMethods",
			"BaseMGRCustomContents",
			"BaseMGRClassConstructors",
			
			"BaseRootDAOImports",
			"BaseRootDAOClassComments",
			"BaseRootDAOClassDefinition",
			"BaseRootDAOClassConstructors",
			"BaseRootDAOGetterSetter",
			"BaseRootDAORequiredMethods",
			"BaseRootDAOCustomContents",
			"BaseRootDAOFinderMethods",
			"BaseRootDAOActionMethods",
			"BaseRootDAOSessionMethods",

			"BaseValueObjectClassComments",
			"BaseValueObjectClassDefinition",
			"BaseValueObjectConstructor",
			"BaseValueObjectCustomContents",
			"BaseValueObjectEqualityMethods",
			"BaseValueObjectGetterSetter",
			"BaseValueObjectIdGetterSetter",
			"BaseValueObjectImports",
			"BaseValueObjectStaticProperties",
			"BaseValueObjectToString",
			"BaseValueObjectVariableDefinitions",
			"c_CustomProperties",
			"c_Getter",
			"c_Setter",
			"ValueObjectClassComments",
			"ValueObjectConstructor",
			"ValueObjectClassDefinition",
			"ValueObjectCustomContents",
			"ValueObjectImports",
			"ValueObjectPKConstructor",
			"ValueObjectPKCustomContents",
			"ValueObjectPKImports",
			"ValueObjectPKClassDefinition",
			"RootDAOClassComments",
			"RootDAOClassConstructors",
			"RootDAOClassDefinition",
			"RootDAOImports",
			"DAOClassComments",
			"DAOClassConstructors",
			"DAOCustomContents",
			"DAOClassDefinition",
			"DAOImports",

			"MGRClassComments",
			"MGRClassConstructors",
			"MGRCustomContents",
			"MGRClassDefinition",
			"MGRImports",

			"DAOCustomInterfaceContents",			
			"BaseValueObjectPKClassDefinition",
			"BaseValueObjectPKClassComments",
			"BaseValueObjectPKConstructor",
			"BaseValueObjectPKCustomContents",
			"BaseValueObjectPKEqualityMethods",
			"BaseValueObjectPKGetterSetter",
			"BaseValueObjectPKImports",
			"BaseValueObjectPKVariableDefinitions",
			"SpringDatasourceConfig",
			"SpringCustomConfig",
			"SpringFactoryConfig",
			"SpringHibernateConfig",
			"SpringHibernateProperties"
	};

	private static Map<String, Template> workspaceTemplatesMap;
	private static Map<String, Snippet> workspaceSnippetsMap;
	private static List<Template> workspaceTemplates;
	private static List<Snippet> workspaceSnippets;

	private Map<String, Snippet> projectSnippets;
	private List<Snippet> projectSnippetsList;
	private Map<String, Template> projectTemplates;
	private List<Template> projectTemplatesList;
	private List<Snippet> allSnippetsList;
	private Map<String, Snippet> allSnippetsMap;
	private List<Template> allTemplatesList;
	private Map<String, Template> allTemplatesMap;
	private List<TemplateLocation> templateLocations;
	private Map<String, TemplateLocation> templateLocationMap;
	private Properties templateParametersMap;
	
	/*
	private static File getProjectFile(String path) {
		return ModelGenerationConfigurationHolder.getModelGenerationConfiguration().getProjectFile(path);
	}
	*/
	
	private static File getConfigurationDirFile() {
		return ModelGenerationConfigurationHolder.getModelGenerationConfiguration().getConfigurationDirectory();
	}
	
	private static File getTemplatesDirFile() {
		return new File(getConfigurationDirFile(), Constants.TEMPLATES_DIR_NAME);
	}
	private static File getSnippetsDirFile() {
		return new File(getConfigurationDirFile(), Constants.SNIPPETS_DIR_NAME);
	}


	/**
	 * Private constructor for project singleton functionality
	 */
	private ResourceManager () {}
	
	private static ResourceManager instance = null;
	/**
	 * Return the project-level singleton ResourceManager
	 */
	public static ResourceManager getInstance () {
		if (null == instance) {
			instance = new ResourceManager();
			if (null == workspaceSnippets || null == workspaceTemplates) {
				reloadWorkspaceCache();
			}
			try {
				getConfigurationDirFile().mkdirs();
				getTemplatesDirFile().mkdirs();
				getSnippetsDirFile().mkdirs();
				instance.reloadProjectCache();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	/**
	 * Initialize workspace resources when a new version of the plug-in is being run for the first time
	 * @throws Exception
	 */
	public static void initializePluginResources () {
		reloadWorkspaceCache();
		initializePluginTemplates();
		initializePluginSnippets();
		reloadWorkspaceCache();
	}
	
	static {
		initializePluginResources();
	}
	
	/**
	 * Load the templates from the jar and save them to the workspace
	 * @throws Exception
	 */
	private static void initializePluginTemplates () {
	}

	public static String getTemplateContents (String templateName) throws Exception {
		return IOUtils.toString(ResourceManager.class.getClassLoader().getResourceAsStream(templateName));
	}
	
	/**
	 * Load the snippets from the jar and save them to the workspace
	 * @throws Exception
	 */
	private static void initializePluginSnippets () {
	}

	/**
	 * Reload the resource instance project cache if a change occured
	 */
	public void reloadProjectCache () {
		try {
			// project templates
			projectTemplates = new HashMap<>();
			projectTemplatesList = new ArrayList<>();
			File folder = getTemplatesDirFile();
			File[] members = folder.listFiles();
			for (int i=0; i<members.length; i++) {
				if (members[i] instanceof File) {
					File file = (File) members[i];
					if (file.getName().endsWith(Constants.EXTENSION_TEMPLATE)) {
						Template template = new Template();
						template.load(file);
						projectTemplates.put(template.getName(), template);
						projectTemplatesList.add(template);
					}
				}
			}

			// project snippets
			projectSnippets = new HashMap<>();
			projectSnippetsList = new ArrayList<>();
			folder = getSnippetsDirFile();
			members = folder.listFiles();
			for (int i=0; i<members.length; i++) {
				if (members[i] instanceof File) {
					File file = (File) members[i];
					if (file.getName().endsWith(Constants.EXTENSION_SNIPPET)) {
						Snippet snippet = new Snippet();
						snippet.load(file);
						projectSnippets.put(snippet.getName(), snippet);
						projectSnippetsList.add(snippet);
					}
				}
			}

			// load up template cache
			allTemplatesMap = new HashMap<>();
			for (Iterator<Template> i=workspaceTemplates.iterator(); i.hasNext(); ) {
				Template t = i.next();
				allTemplatesMap.put(t.getName(), t);
			}
			for (Iterator<Template> i=projectTemplates.values().iterator(); i.hasNext(); ) {
				Template t = i.next();
				allTemplatesMap.put(t.getName(), t);
			}
			allTemplatesList = new ArrayList<>(allTemplatesMap.size());
			for (Iterator<Template> i=allTemplatesMap.values().iterator(); i.hasNext(); ) {
				Template t = i.next();
				allTemplatesList.add(t);
			}
			Collections.sort(allTemplatesList);

			// load up snippet cache
			allSnippetsMap = new HashMap<>();
			for (Iterator<Snippet> i=workspaceSnippets.iterator(); i.hasNext(); ) {
				Snippet t = (Snippet) i.next();
				allSnippetsMap.put(t.getName(), t);
			}
			for (Iterator<Snippet> i=projectSnippets.values().iterator(); i.hasNext(); ) {
				Snippet t = (Snippet) i.next();
				allSnippetsMap.put(t.getName(), t);
			}
			allSnippetsList = new ArrayList<>(allSnippetsMap.values());
			Collections.sort(allSnippetsList);
			
			// load template locations cache
			refreshTemplateLocations();
			
			// load the statically defined template parameters
			refreshTemplateParameters();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static Collection<URL> getAllResources(Class<?> cl, String path) {
		/* Old version:
		File dir = new File("//E:/workspaces/vira-templates/RestProj/genSrc/ir/viratech/commons/code_gen/model/resource", path);
		File[] files = dir.listFiles();
		List<URL> res = new ArrayList<>();
		for (File file : files) {
			if (!file.isDirectory()) {
				try {
					res.add(file.toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		return res;
		*/
		try {
			// Normalize path
			if (path.matches("^/")) { // absolute path
				path = new URI(path.substring(1) + "/").normalize().getPath();
			} else { // relative path
				String me = cl.getName().replace(".", "/");
				path = new URI(me + "/../" + path + "/").normalize().getPath();
			}
			
			Set<URL> urlList = new HashSet<URL>();
			Enumeration<URL> urlEnumeration = null;
			urlEnumeration = cl.getClassLoader().getResources(path);
			for (URL dirUrl : EnumerationUtils.toList(urlEnumeration)) {
				if (dirUrl.getProtocol().equals("file")) {
					File[] fileList = new File(dirUrl.toURI()).listFiles();
					for (File file : fileList) {
						if (!file.isDirectory()) {
							urlList.add(new URL(dirUrl, file.getName()));
						}
					}
				} else if (dirUrl.getProtocol().equals("jar")) {
					String jarPath = dirUrl.getPath().substring(5, dirUrl.getPath().indexOf("!")); //strip out only the JAR file
					try (JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
						Enumeration<JarEntry> entries = jarFile.entries(); //gives ALL entries in jar
						while (entries.hasMoreElements()) {
							String name = entries.nextElement().getName();
							if (name.startsWith(path)) { //filter according to the path
								String entry = name.substring(path.length());
								int checkSubdir = entry.indexOf("/");
								if (entry.length() != 0 && checkSubdir == -1) {// we only need files, not subdirectories
									urlList.add(new URL(dirUrl, entry));
								}
							}
						}
					}
				}
			}
			return urlList;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}

	}
	
	private static String getUrlName(URL url) {
		return HSUtil.getUrlFileName(url);
	}
	
	/**
	 * Reload the workspace project cache
	 */
	public static void reloadWorkspaceCache () {
		try {
			// workspace templates
			workspaceTemplatesMap = new HashMap<>();
			workspaceTemplates = new ArrayList<>();
			Collection<URL> templateUrls = getAllResources(ResourceManager.class, "../"+Constants.TEMPLATES_DIR_NAME);
			for (URL templateUrl : templateUrls) {
				String fileName = getUrlName(templateUrl);
				if (fileName.endsWith(Constants.EXTENSION_TEMPLATE)) {
					Template template = new Template();
					template.load(templateUrl);
					workspaceTemplatesMap.put(template.getName(), template);
					workspaceTemplates.add(template);
				}
			}
			Collections.sort(workspaceTemplates);

			// workspace snippets
			workspaceSnippetsMap = new HashMap<>();
			workspaceSnippets = new ArrayList<>();
			Collection<URL> snippetUrls = getAllResources(ResourceManager.class, "../"+Constants.SNIPPETS_DIR_NAME);
			for (URL snippetUrl : snippetUrls) {
				String fileName = getUrlName(snippetUrl);
				if (fileName.endsWith(Constants.EXTENSION_SNIPPET)) {
					Snippet snippet = new Snippet();
					snippet.load(snippetUrl);
					workspaceSnippetsMap.put(snippet.getName(), snippet);
					workspaceSnippets.add(snippet);
				}
			}
			Collections.sort(workspaceSnippets);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	

	/**
	 * Return the requestes snippet by looking in the project and then the workspace
	 * @param name the snippet name
	 */
	public Snippet getSnippet (String name) {
		return allSnippetsMap.get(name);
	}

	/**
	 * Return all snippets associated with the project and workspace
	 * @return a list of Snippet objects
	 */
	public List<Snippet> getSnippets () {
		return allSnippetsList;
	}

	/**
	 * Return all snippets associated with the project
	 * @return a list of Snippet objects
	 */
	public List<Snippet> getProjectSnippets () {
		return projectSnippetsList;
	}

	/**
	 * Return the requested template by looking in the project and then in the workspace
	 * @param name the template name
	 */
	public Template getTemplate (String name) {
		return allTemplatesMap.get(name);
	}


	/**
	 * Return the template parameter value related to the given name or null if it does not exist
	 * @param name the parameter name
	 */
	public String getTemplateParameter (String name) {
		return templateParametersMap.getProperty(name);
	}
	
	/**
	 * Return all project template parameters
	 */
	public Properties getTemplateParameters () {
		return templateParametersMap;
	}
	
	public Map<Object, Object> getTemplateParametersMap () {
		return (Map<Object, Object>)templateParametersMap;
	}
	
	/**
	 * Return the names of all template parameters sorted in alphabetical order
	 * @return a List of String objects
	 */
	public List<String> getTemplateParameterNames () {
		List<String> sortedNames = new ArrayList<>(templateParametersMap.size());
		for (Iterator<?> i=templateParametersMap.keySet().iterator(); i.hasNext(); ) {
			sortedNames.add((String)i.next());
		}
		Collections.sort(sortedNames);
		return sortedNames;
	}
	
	private void refreshTemplateParameters () {
		templateParametersMap = new Properties();
		String ptString = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_TEMPLATE_PARAMETERS);
		if (null != ptString && ptString.trim().length() > 0) {
			StringTokenizer st = new StringTokenizer(ptString, "&");
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				int index = s.indexOf("=");
				try {
					String key = URLDecoder.decode(s.substring(0, index), "UTF-8");
					String value = URLDecoder.decode(s.substring(index+1, s.length()), "UTF-8");
					templateParametersMap.put(key, value);
				}
				catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Return all template (enabled and disabled) for the current project
	 * @return a List of TemplateLocation objects
	 */
	public List<TemplateLocation> getTemplateLocations () {
		return templateLocations;
	}

	private void refreshTemplateLocations () {
		templateLocationMap = new HashMap<>();
		templateLocations = new ArrayList<>();

		String ptString = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_PROJECT_TEMPLATE_LOCATIONS);
		if (null != ptString) {
			StringTokenizer st = new StringTokenizer(ptString, ";");
			while (st.hasMoreTokens()) {
				try {
					TemplateLocation templateLocation = new TemplateLocation(st.nextToken());
					if (templateLocation.isValid()) {
						templateLocations.add(templateLocation);
						templateLocationMap.put(templateLocation.getTemplate().getName(), templateLocation);
					}
				}
				catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}
			}
		}
	}

	public TemplateLocation getTemplateLocation (String templateName) {
		return (TemplateLocation) templateLocationMap.get(templateName);
	}
	
	public static List<Snippet> getWorkspaceSnippets () {
		return workspaceSnippets;
	}
	
	public static Snippet getWorkspaceSnippet (String name) {
		return (Snippet) workspaceSnippetsMap.get(name);
	}

	public static List<Template> getWorkspaceTemplates () {
		return workspaceTemplates;
	}

	public static List<Template> getNonRequiredWorkspaceTemplates () {
		List<Template> nonRequiredWorkspaceTemplates = new ArrayList<>();
		for (Iterator<Template> i=getWorkspaceTemplates().iterator(); i.hasNext(); ) {
			Template t = i.next();
			if (!isRequiredResource(t)) nonRequiredWorkspaceTemplates.add(t);
		}
		return nonRequiredWorkspaceTemplates;
	}

	public List<Template> getProjectTemplates () {
		return projectTemplatesList;
	}
	
	public List<Template> getTemplates () {
		return allTemplatesList;
	}

	public static Template getWorkspaceTemplate (String name) {
		return (Template) workspaceTemplatesMap.get(name);
	}

	public static boolean isRequiredResource (Resource resource) {
		if (resource instanceof Snippet) {
			for (int i=0; i<ALL_SNIPPETS.length; i++) {
				if (resource.getName().equals(ALL_SNIPPETS[i])) return true;
			}
			return false;
		}
		else if (resource instanceof Template) {
			for (int i=0; i<ALL_TEMPLATES.length; i++) {
				if (resource.getName().equals(ALL_TEMPLATES[i])) return true;
			}
			return false;
		}
		else return false;
	}
}