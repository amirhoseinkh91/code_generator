package ir.justro.commons.code_gen.model.util;

import ir.justro.commons.code_gen.hsynch.Constants;
import ir.justro.commons.code_gen.model.ModelGenerationConfigurationHolder;
import ir.justro.commons.code_gen.model.mapping.*;
import ir.justro.commons.code_gen.model.resource.*;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@SuppressWarnings({"WeakerAccess", "ConstantConditions", "UnusedReturnValue", "ResultOfMethodCallIgnored", "ForLoopReplaceableByForEach", "BooleanMethodIsAlwaysInverted", "SameParameterValue", "unused", "PointlessNullCheck", "StringOperationCanBeSimplified"})
public class Synchronizer {

    public static final String META_AUTO_SYNC = "sync";

    public static final String PARAM_CLASS = "class";
    public static final String PARAM_CLASSES = "classes";
    public static final String PARAM_CONSTRUCTOR = "constructor";
    public static final String PARAM_CONTENT = "content";
    public static final String PARAM_CUSTOM_PLACEHOLDER = "custom_placeholder";
    public static final String PARAM_PACKAGE = "package";
    public static final String PARAM_MODULE = "moduleName";

    public static final String PARAM_DAO_PACKAGE = "daoPackage";
    public static final String PARAM_MODEL_PACKAGE = "modelPackage";
    public static final String PARAM_MGR_PACKAGE = "mgrPackage";

    public static final String PARAM_MODEL_MODULE = "modelModuleName";
    public static final String PARAM_DAO_MODULE = "daoModuleName";
    public static final String PARAM_MGR_MODULE = "mgrModuleName";


    public static final String PARAM_CLASS_NAME = "className";
    public static final String PARAM_PATH = "path";
    public static final String PARAM_FILE_NAME = "fileName";
    public static final String PARAM_DOCUMENTS = "documents";
    public static final String PARAM_DOCUMENT = "document";
    public static final String PARAM_FILES = "files";
    public static final String PARAM_FILE = "file";
    public static final String PARAM_EXCEPTION_CLASS = "exceptionClass";
    public static final String PARAM_CONTEXT_OBJECT = "obj";
    public static final String PARAM_SNIPPET = "snippet";
    public static final String PARAM_NOW = "now";
    public static final String PARAM_UTIL = "util";

    public static final String SETUP_SNIPPET_CLASS = "SetupClass";

    public static final String MESSAGE_WARNING = "WARNING:";
    public static final String MESSAGE_ERROR = "ERROR:";
    public static final String MESSAGE_FATAL = "FATAL:";

    public static final String TEMPLATE_BASE_VALUE_OBJECT = "BaseValueObject";
    public static final String TEMPLATE_VALUE_OBJECT = "ValueObject";
    public static final String SNIPPET_VALUE_OBJECT_CONSTRUCTOR = "ValueObjectConstructor";
    public static final String TEMPLATE_VALUE_OBJECT_PROXY = "ValueObjectProxy";
    public static final String TEMPLATE_VALUE_OBJECT_PROXY_CONTENTS = "ValueObjectProxyContents";
    public static final String TEMPLATE_VALUE_OBJECT_PK = "ValueObjectPK";
    public static final String SNIPPET_VALUE_OBJECT_PK_CONSTRUCTOR = "ValueObjectPKConstructor";
    public static final String TEMPLATE_BASE_VALUE_OBJECT_PK = "BaseValueObjectPK";
    public static final String TEMPLATE_BASE_ROOT_DAO = "BaseRootDAO";
    public static final String TEMPLATE_ROOT_DAO = "RootDAO";
    public static final String TEMPLATE_BASE_DAO = "BaseDAO";
    public static final String TEMPLATE_DAO = "DAO";
    public static final String TEMPLATE_MGR = "MGR";
    public static final String TEMPLATE_VALUE_MODULE_INFO = "ValueModuleInfoDefinition";
    public static final String TEMPLATE_DAO_MODULE_INFO = "DAOModuleInfoDefinition";
    public static final String TEMPLATE_MGR_MODULE_INFO = "MgrModuleInfoDefinition";
    public static final String TEMPLATE_PROJECT_IML = "ProjectFileIML";

    public static final String MGR_MODULE_NAME = ".mgr";
    public static final String DAO_MODULE_NAME = ".dao";
    public static final String MODEL_MODULE_NAME = ".model";

    public static final String TEMPLATE_BASE_MGR = "BaseMGR";
    public static final String TEMPLATE_IDAO = "IDAO";
    public static final String TEMPLATE_SPRING_CONFIG = "SpringConfig";

    public static final String EXTENSION_JAVA = ".java";

    public static final String MARKER_CONSTRUCTOR = "CONSTRUCTOR MARKER";
    public static final String MARKER_GENERATED_CONTENT = "GENERATED CONTENT MARKER";

    public static final int DIRECTIVE_STOP_PROCESSING_GLOBAL = 0;
    public static final int DIRECTIVE_STOP_PROCESSING_LOCAL = 1;
    public static final int DIRECTIVE_KEEP_PROCESSING = 2;

    private List<HibernateDocument> documents;
    private boolean force;

    File sourceDirectory;

    // cache
    boolean valueObjectGenerationEnabled;
    boolean daoGenerationEnabled;
    boolean ifaceGenerationEnabled;
    boolean customGenerationEnabled;
    boolean modularEnabled;

    public Synchronizer(List<HibernateDocument> documents, File sourceDirectory, boolean force) {
        this.documents = documents;
        this.force = force;
        this.sourceDirectory = sourceDirectory;
        valueObjectGenerationEnabled = ModelGenerationConfigurationHolder.getBooleanProperty(Constants.PROP_GENERATION_VALUE_OBJECT_ENABLED, true);
        daoGenerationEnabled = ModelGenerationConfigurationHolder.getBooleanProperty(Constants.PROP_GENERATION_DAO_ENABLED, true);
        ifaceGenerationEnabled = ModelGenerationConfigurationHolder.getBooleanProperty(Constants.PROP_GENERATION_DAO_IFACE_ENABLED, false);
        customGenerationEnabled = ModelGenerationConfigurationHolder.getBooleanProperty(Constants.PROP_GENERATION_CUSTOM_ENABLED, true);
        modularEnabled = ModelGenerationConfigurationHolder.getBooleanProperty(Constants.PROP_USE_MODULAR_GENERATION, false);
    }

    /**
     * Synchronize all the class templates synchronously
     */
    public void synchronize() {
        if (documents.size() > 0) {
            Object contextObject = getContextObject();
            Context context = Synchronizer.getDefaultContext(contextObject);
            for (HibernateDocument hd : documents) {
                for (HibernateClass hc : hd.getClasses()) {
                    synchronizeClass(hc, hd, new VelocityContext(context));
                    long freeMemory = Runtime.getRuntime().freeMemory();
                    if (freeMemory < 2000000) {
                        // we need to clean up to keep going
                        System.gc();
                    }
                }
            }
        }
    }

    protected static Context createContextWithSingleParam(Context parentContext, String key, Object value) {
        Context context2 = new VelocityContext(parentContext);
        context2.put(key, value);
        return context2;
    }

    /**
     * The the contents for any special directives
     *
     * @param contents the contents
     * @param syncFile the synchronization file (to add any markers)
     * @param context  the current context
     * @return a static DIRECTIVE variable defined in this class
     */
    public static int checkContents(String contents, File syncFile, Context context) {
        if (contents.startsWith(MESSAGE_WARNING)) {
            String message = contents.substring(MESSAGE_WARNING.length(), contents.length());
            String key = MESSAGE_WARNING + syncFile.getName() + message;
            if (null == context.get(key) && null != syncFile) {
                context.put(key, Boolean.TRUE);
            }
        } else if (contents.startsWith(MESSAGE_ERROR)) {
            String message = contents.substring(MESSAGE_ERROR.length(), contents.length());
            String key = MESSAGE_ERROR + syncFile.getName() + message;
            if (null == context.get(key) && null != syncFile) {
                context.put(key, Boolean.TRUE);
                return DIRECTIVE_STOP_PROCESSING_LOCAL;
            }
        } else if (contents.startsWith(MESSAGE_FATAL)) {
            String message = contents.substring(MESSAGE_FATAL.length(), contents.length());
            String key = MESSAGE_FATAL + syncFile.getName() + message;
            if (null == context.get(key) && null != syncFile) {
                context.put(key, Boolean.TRUE);
            }
            return DIRECTIVE_STOP_PROCESSING_GLOBAL;
        }
        return DIRECTIVE_KEEP_PROCESSING;
    }

    private File getPackageDir(String packageName) {
        String packageDirs = packageName.replaceAll("\\Q.\\E", "/");
        return new File(this.sourceDirectory, packageDirs);
    }

    private String getPackageDirs(String packageName) {
        return packageName.replaceAll("\\Q.\\E", "/");
    }

    private File getCreatedPackageDir(String packageName) {
        File packageDir = this.getPackageDir(packageName);
        packageDir.mkdirs();
        return packageDir;
    }

    private File getCreatedModuleDir(String moduleName) {
        File moduleDir = new File(moduleName);
        moduleDir.mkdirs();
        return moduleDir;
    }

    private File getCreatedModulePackageDir(String moduleName, String packageName) {
        File moduleDir = getCreatedModuleDir(moduleName);
        return new File(moduleDir + File.separator + this.sourceDirectory, getPackageDirs(packageName));
    }

    protected File getJavaFile(String packageName, String className) {
        File packageDir = getCreatedPackageDir(packageName);
        String fileName = className + EXTENSION_JAVA;
        return new File(packageDir, fileName);
    }

    protected File getJavaFile(String moduleName, String packageName, String className) {
        File packageDir = getCreatedModulePackageDir(moduleName, packageName);
        String fileName = className + EXTENSION_JAVA;
        return new File(packageDir, fileName);
    }

    protected File getModuleInfoFile(String moduleName) {
        File moduleDir = getCreatedModuleDir(moduleName);
        String fileName = "module-info" + EXTENSION_JAVA;
        return new File(moduleDir + File.separator + this.sourceDirectory, fileName);
    }

    /**
     * Synchronize all files associated with the given HibernateClass
     *
     * @param hc the HibernateClass
     * @param hd the File being synchronized for marker support
     */
    private boolean synchronizeClass(HibernateClass hc, HibernateDocument hd, Context context) {
        File hdFile = hd.getFile();
        String doSync = hc.get(META_AUTO_SYNC);
        if (null != doSync && HSUtil.isFalse(doSync))
            return true;

        context.put(PARAM_CLASSES, HibernateMappingManager.getInstance().getClasses());
        context.put(PARAM_DOCUMENTS, HibernateMappingManager.getInstance().getDocuments());
        context.put(PARAM_FILES, HibernateMappingManager.getInstance().getFiles());
        context.put(PARAM_CLASS, hc);
        context.put(PARAM_DOCUMENT, hd);
        context.put(PARAM_FILE, hdFile);
        context.put(PARAM_MODULE, hc.getModuleName());
        context.put(PARAM_MODEL_MODULE, hc.getModelModuleName());
        context.put(PARAM_DAO_MODULE, hc.getDAOModuleName());
        context.put(PARAM_MGR_MODULE, hc.getMGRModuleName());
        context.put(PARAM_FILE_NAME, hdFile.getName());

        // process initial setup snippet if it exists
        Snippet snippet = ResourceManager.getInstance().getSnippet(SETUP_SNIPPET_CLASS);
        if (null != snippet) {
            String result = snippet.merge(context).trim();
            int rtn = checkContents(result, hdFile, context);
            if (rtn == DIRECTIVE_STOP_PROCESSING_LOCAL)
                return true;
            else if (rtn == DIRECTIVE_STOP_PROCESSING_GLOBAL)
                return false;
        }

        if (modularEnabled) {
            return generateModularCode(hc, hdFile, context);
        } else {
            return generateUnModularCode(hc, hdFile, context);
        }
    }

    private boolean generateModularCode(HibernateClass hc, File hdFile, Context context) {
        if (valueObjectGenerationEnabled && hc.canSyncValueObject()) {
            // create value object and proxy
            generateClassFile(TEMPLATE_BASE_VALUE_OBJECT, new VelocityContext(context), hc.getModelModuleName(), hc.getBaseValueObjectPackage(), hc.getBaseValueObjectClassName(), true);
            generateExtensionClassFile(TEMPLATE_VALUE_OBJECT, SNIPPET_VALUE_OBJECT_CONSTRUCTOR, new VelocityContext(context), hc.getModelModuleName(), hc.getValueObjectPackage(), hc.getValueObjectClassName());
            generateModuleInfo(TEMPLATE_VALUE_MODULE_INFO, new VelocityContext(context), hc.getModelModuleName(), hc.getDAOPackage(), hc.getValueObjectPackage(), hc.getMGRPackage());
            if (hc.hasProxy())
                generateProxyClassFile(TEMPLATE_VALUE_OBJECT_PROXY, TEMPLATE_VALUE_OBJECT_PROXY_CONTENTS, new VelocityContext(context), hc.getProxyPackage(), hc.getValueObjectProxyClassName());

            // class components
            for (Iterator<HibernateComponentClass> iter = hc.getComponentList().iterator(); iter.hasNext(); ) {
                HibernateComponentClass hcc = iter.next();
                if (!hcc.isDynamic()) {
                    Context context2 = createContextWithSingleParam(context, PARAM_CLASS, hcc);
                    generateClassFile(TEMPLATE_BASE_VALUE_OBJECT, context2, hcc.getModelModuleName(), hcc.getBaseValueObjectPackage(), hcc.getBaseValueObjectClassName(), true);
                    generateExtensionClassFile(TEMPLATE_VALUE_OBJECT, SNIPPET_VALUE_OBJECT_CONSTRUCTOR, context2, hcc.getModelModuleName(), hcc.getValueObjectPackage(), hcc.getClassName());
                    generateModuleInfo(TEMPLATE_VALUE_MODULE_INFO, new VelocityContext(context), hcc.getModelModuleName(), hcc.getDAOPackage(), hcc.getValueObjectPackage(), hcc.getMGRPackage());
                }
            }
            for (HibernateClassCollectionProperty hccp : hc.getCollectionList()) {
                for (HibernateComponentClass chc : hccp.getCompositeList()) {
                    Context context2 = createContextWithSingleParam(context, PARAM_CLASS, chc);
                    generateClassFile(TEMPLATE_BASE_VALUE_OBJECT, context2, chc.getModelModuleName(), chc.getBaseValueObjectPackage(), chc.getBaseValueObjectClassName(), true);
                    generateExtensionClassFile(TEMPLATE_VALUE_OBJECT, SNIPPET_VALUE_OBJECT_CONSTRUCTOR, context2, chc.getModelModuleName(), chc.getValueObjectPackage(), chc.getClassName());
                    generateModuleInfo(TEMPLATE_VALUE_MODULE_INFO, new VelocityContext(context), chc.getModelModuleName(), chc.getDAOPackage(), chc.getValueObjectPackage(), chc.getMGRPackage());
                }
            }

            // composite id
            if (null != hc.getId() && hc.getId().isComposite() && hc.getId().hasExternalClass()) {
                generateExtensionClassFile(TEMPLATE_VALUE_OBJECT_PK, SNIPPET_VALUE_OBJECT_PK_CONSTRUCTOR, new VelocityContext(context), hc.getId().getProperty().getPackage(), hc.getId().getProperty()
                        .getClassName());
                generateClassFile(TEMPLATE_BASE_VALUE_OBJECT_PK, new VelocityContext(context), hc.getModelModuleName(), hc.getBaseValueObjectPackage(), "Base" + hc.getId().getProperty().getClassName(), true);
            }

        }
        boolean mgrGenerationEnabled = true;
        // dao's
        if (daoGenerationEnabled && hc.canSyncDAO()) {
            if (!hc.useCustomDAO()) {
                generateClassFile(TEMPLATE_BASE_ROOT_DAO, new VelocityContext(context), hc.getDAOModuleName(), hc.getBaseDAOPackage(), hc.getBaseRootDAOClassName(), true);
            }
            //generateClassFile(TEMPLATE_ROOT_DAO, new VelocityContext(context), hc.getRootDAOPackage(), hc.getRootDAOClassName(), false);
            generateClassFile(TEMPLATE_BASE_DAO, new VelocityContext(context), hc.getDAOModuleName(), hc.getBaseDAOPackage(), hc.getBaseDAOClassName(), true);
            generateClassFile(TEMPLATE_DAO, new VelocityContext(context), hc.getDAOModuleName(), hc.getDAOPackage(), hc.getDAOClassName(), false);
            generateModuleInfo(TEMPLATE_DAO_MODULE_INFO, new VelocityContext(context), hc.getDAOModuleName(), hc.getDAOPackage(), hc.getValueObjectPackage(), hc.getMGRPackage());
            if (this.ifaceGenerationEnabled)
                generateClassFile(TEMPLATE_IDAO, new VelocityContext(context), hc.getDAOModuleName(), hc.getInterfacePackage(), hc.getDAOInterfaceName(), false);
            if (mgrGenerationEnabled) {
                generateClassFile(TEMPLATE_BASE_MGR, new VelocityContext(context), hc.getMGRModuleName(), hc.getBaseMGRPackage(), hc.getBaseMGRClassName(), true);
                generateClassFile(TEMPLATE_MGR, new VelocityContext(context), hc.getMGRModuleName(), hc.getMGRPackage(), hc.getMGRClassName(), false);
                generateModuleInfo(TEMPLATE_MGR_MODULE_INFO, new VelocityContext(context), hc.getMGRModuleName(), hc.getDAOPackage(), hc.getValueObjectPackage(), hc.getMGRPackage());
            }
        }

        // custom templates
        if (customGenerationEnabled && hc.canSyncCustom()) {
            List<TemplateLocation> templateLocations = ResourceManager.getInstance().getTemplateLocations();
            for (TemplateLocation templateLocation : templateLocations) {
                if (!generateCustomFile(templateLocation, hc, new VelocityContext(context), hdFile, force))
                    return false;
            }
        }
        return true;
    }

    private void generateModuleInfo(String velocityTemplate, Context context, String moduleName, String daoPackage, String valueObjectPackage ,String mgrPackage) {
        File unit = getModuleInfoFile(moduleName);
        Template template = ResourceManager.getInstance().getTemplate(velocityTemplate);
        if (!unit.exists() || force) {
            Context context2 = createContextWithSingleParam(context, PARAM_MODULE, moduleName);
            context2.put(PARAM_MODEL_PACKAGE , valueObjectPackage);
            context2.put(PARAM_MGR_PACKAGE , mgrPackage);
            context2.put(PARAM_DAO_PACKAGE , daoPackage);
            String content = template.merge(context2);
            writeCompilationUnit(unit, content);
        }
    }

    private boolean generateUnModularCode(HibernateClass hc, File hdFile, Context context) {
        if (valueObjectGenerationEnabled && hc.canSyncValueObject()) {
            // create value object and proxy
            generateClassFile(TEMPLATE_BASE_VALUE_OBJECT, new VelocityContext(context), hc.getBaseValueObjectPackage(), hc.getBaseValueObjectClassName(), true);
            generateExtensionClassFile(TEMPLATE_VALUE_OBJECT, SNIPPET_VALUE_OBJECT_CONSTRUCTOR, new VelocityContext(context), hc.getValueObjectPackage(), hc.getValueObjectClassName());
            if (hc.hasProxy())
                generateProxyClassFile(TEMPLATE_VALUE_OBJECT_PROXY, TEMPLATE_VALUE_OBJECT_PROXY_CONTENTS, new VelocityContext(context), hc.getProxyPackage(), hc.getValueObjectProxyClassName());

            // class components
            for (Iterator<HibernateComponentClass> iter = hc.getComponentList().iterator(); iter.hasNext(); ) {
                HibernateComponentClass hcc = iter.next();
                if (!hcc.isDynamic()) {
                    Context context2 = createContextWithSingleParam(context, PARAM_CLASS, hcc);
                    generateClassFile(TEMPLATE_BASE_VALUE_OBJECT, context2, hcc.getBaseValueObjectPackage(), hcc.getBaseValueObjectClassName(), true);
                    generateExtensionClassFile(TEMPLATE_VALUE_OBJECT, SNIPPET_VALUE_OBJECT_CONSTRUCTOR, context2, hcc.getValueObjectPackage(), hcc.getClassName());
                }
            }
            for (HibernateClassCollectionProperty hccp : hc.getCollectionList()) {
                for (HibernateComponentClass chc : hccp.getCompositeList()) {
                    Context context2 = createContextWithSingleParam(context, PARAM_CLASS, chc);
                    generateClassFile(TEMPLATE_BASE_VALUE_OBJECT, context2, chc.getBaseValueObjectPackage(), chc.getBaseValueObjectClassName(), true);
                    generateExtensionClassFile(TEMPLATE_VALUE_OBJECT, SNIPPET_VALUE_OBJECT_CONSTRUCTOR, context2, chc.getValueObjectPackage(), chc.getClassName());
                }
            }

            // composite id
            if (null != hc.getId() && hc.getId().isComposite() && hc.getId().hasExternalClass()) {
                generateExtensionClassFile(TEMPLATE_VALUE_OBJECT_PK, SNIPPET_VALUE_OBJECT_PK_CONSTRUCTOR, new VelocityContext(context), hc.getId().getProperty().getPackage(), hc.getId().getProperty()
                        .getClassName());
                generateClassFile(TEMPLATE_BASE_VALUE_OBJECT_PK, new VelocityContext(context), hc.getBaseValueObjectPackage(), "Base" + hc.getId().getProperty().getClassName(), true);
            }

        }
        boolean mgrGenerationEnabled = true;
        // dao's
        if (daoGenerationEnabled && hc.canSyncDAO()) {
            if (!hc.useCustomDAO()) {
                generateClassFile(TEMPLATE_BASE_ROOT_DAO, new VelocityContext(context), hc.getBaseDAOPackage(), hc.getBaseRootDAOClassName(), true);
            }
            //generateClassFile(TEMPLATE_ROOT_DAO, new VelocityContext(context), hc.getRootDAOPackage(), hc.getRootDAOClassName(), false);
            generateClassFile(TEMPLATE_BASE_DAO, new VelocityContext(context), hc.getBaseDAOPackage(), hc.getBaseDAOClassName(), true);
            generateClassFile(TEMPLATE_DAO, new VelocityContext(context), hc.getDAOPackage(), hc.getDAOClassName(), false);
            if (this.ifaceGenerationEnabled)
                generateClassFile(TEMPLATE_IDAO, new VelocityContext(context), hc.getInterfacePackage(), hc.getDAOInterfaceName(), false);
            if (mgrGenerationEnabled) {
                generateClassFile(TEMPLATE_BASE_MGR, new VelocityContext(context), hc.getBaseMGRPackage(), hc.getBaseMGRClassName(), true);
                generateClassFile(TEMPLATE_MGR, new VelocityContext(context), hc.getMGRPackage(), hc.getMGRClassName(), false);
            }
        }

        // custom templates
        if (customGenerationEnabled && hc.canSyncCustom()) {
            List<TemplateLocation> templateLocations = ResourceManager.getInstance().getTemplateLocations();
            for (TemplateLocation templateLocation : templateLocations) {
                if (!generateCustomFile(templateLocation, hc, new VelocityContext(context), hdFile, force))
                    return false;
            }
        }
        return true;
    }

    /**
     * Write the contents that relate to the given TemplateLocation Template
     *
     * @param templateLocation the template location
     * @param context          the Velocity context
     * @param syncFile         the current File that being synchronized
     * @param force            true to overwrite even if the templateLocation is not set to overwrite
     * @return true to keep processing and false to stop
     */
    @SuppressWarnings("UnusedAssignment")
    private boolean generateCustomFile(TemplateLocation templateLocation, HibernateClass hibernateClass, Context context, File syncFile, boolean force) {
        context.remove(PARAM_CUSTOM_PLACEHOLDER);
        try {
            String fileName = templateLocation.getName(context);
            if (templateLocation.getTemplate().isClassTemplate()) {
                // output is a java class
                String className = fileName;
                fileName += EXTENSION_JAVA;
                String packageName = templateLocation.getPackage(context);
                context.put(PARAM_PACKAGE, packageName);
                context.put(PARAM_CLASS_NAME, className);
                String content = templateLocation.getTemplate().merge(context);
                context.remove(PARAM_PACKAGE);
                context.remove(PARAM_CLASS_NAME);
                int rtn = checkContents(content, syncFile, context);
                if (rtn == DIRECTIVE_KEEP_PROCESSING) {
                    File unit = this.getJavaFile(packageName, className);
                    if (!unit.exists() || force || templateLocation.shouldOverride()) {
                        writeCompilationUnit(unit, content);
                    }
                    writeCompilationUnit(unit, content);
                } else if (rtn == DIRECTIVE_STOP_PROCESSING_GLOBAL) {
                    return false;
                }
            } else {
                // output is a resource file
                String pathName = templateLocation.getLocation(context);
                context.put(PARAM_PATH, pathName);
                context.put(PARAM_FILE_NAME, fileName);
                String content = templateLocation.getTemplate().merge(context);
                context.remove(PARAM_PATH);
                context.remove(PARAM_FILE_NAME);
                int rtn = checkContents(content, syncFile, context);
                if (rtn == DIRECTIVE_KEEP_PROCESSING) {
                    writeResourceFile(content, pathName, fileName);
                } else if (rtn == DIRECTIVE_STOP_PROCESSING_GLOBAL) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Generate a class and it's constructor related to the templates given
     *
     * @param templateName the class template name
     * @param context      the Velocity context
     * @param packageName  the package name
     * @param className    the class name
     */
    private void generateExtensionClassFile(String templateName, String constructorSnippetName, Context context, String moduleName, String packageName, String className) {
        File unit = this.getJavaFile(moduleName, packageName, className);
        context.put(PARAM_PACKAGE, packageName);
        if (!unit.exists()) {
            Snippet snippet = ResourceManager.getInstance().getSnippet(constructorSnippetName);
            String constructorContents = snippet.merge(context);
            Context subContext = createContextWithSingleParam(context, PARAM_CONSTRUCTOR, constructorContents);
            Template template = ResourceManager.getInstance().getTemplate(templateName);
            String content = template.merge(subContext);
            writeCompilationUnit(unit, content);
        }
    }

    /**
     * Generate a class and it's constructor related to the templates given
     *
     * @param templateName the class template name
     * @param context      the Velocity context
     * @param packageName  the package name
     * @param className    the class name
     */
    private void generateExtensionClassFile(String templateName, String constructorSnippetName, Context context, String packageName, String className) {
        File unit = this.getJavaFile(packageName, className);
        context.put(PARAM_PACKAGE, packageName);
        if (!unit.exists()) {
            Snippet snippet = ResourceManager.getInstance().getSnippet(constructorSnippetName);
            String constructorContents = snippet.merge(context);
            Context subContext = createContextWithSingleParam(context, PARAM_CONSTRUCTOR, constructorContents);
            Template template = ResourceManager.getInstance().getTemplate(templateName);
            String content = template.merge(subContext);
            writeCompilationUnit(unit, content);
        }
    }

    protected String readFile(File unit) {
        try {
            return FileUtils.readFileToString(unit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create the given proxy class
     *
     * @param templateName        the proxy template name
     * @param contentTemplateName the template name referencing the generated proxy methods
     * @param context             the Velocity context
     * @param packageName         the package name
     * @param className           the name of the proxy interface to be created
     */
    private void generateProxyClassFile(String templateName, String contentTemplateName, Context context, String packageName, String className) {
        context.put(PARAM_PACKAGE, packageName);
        File unit = this.getJavaFile(packageName, className);
        if (unit.exists()) {
            String content = readFile(unit);
            MarkerContents mc = HSUtil.getMarkerContents(content, MARKER_GENERATED_CONTENT);
            if (null != mc) {
                // rewrite all but generated content
                Template template = ResourceManager.getInstance().getTemplate(contentTemplateName);
                String customContent = template.merge(context);
                if (null != mc.getContents() && mc.getContents().trim().equals(customContent.trim()))
                    return;
                else {
                    content = mc.getPreviousContents() + customContent + mc.getPostContents();
                    writeCompilationUnit(unit, content);
                }
            }
        } else {
            // create a new proxy class
            Template template = ResourceManager.getInstance().getTemplate(contentTemplateName);
            String content = template.merge(context);
            Context subContext = createContextWithSingleParam(context, PARAM_CONTENT, content);
            template = ResourceManager.getInstance().getTemplate(templateName);
            content = template.merge(subContext);
            writeCompilationUnit(unit, content);
        }
        context.remove(PARAM_PACKAGE);
    }

    /**
     * Perform the generation with the specified template and save the results
     *
     * @param velocityTemplate the Velocity template name
     * @param context          the Velocity context
     * @param packageName      the name of the package to create the CompilationUnit in
     * @param className        the name of the class to generate
     * @param force            true to overwrite existing content and false to only create new content
     */
    private void generateClassFile(String velocityTemplate, Context context, String packageName, String className, boolean force) {
        File unit = getJavaFile(packageName, className);
        Template template = ResourceManager.getInstance().getTemplate(velocityTemplate);
        if (!unit.exists() || force) {
            Context context2 = createContextWithSingleParam(context, PARAM_PACKAGE, packageName);
            String content = template.merge(context2);
            writeCompilationUnit(unit, content);
        }
    }

    private void generateClassFile(String velocityTemplate, Context context, String moduleName, String packageName, String className, boolean force) {
        File unit = getJavaFile(moduleName, packageName, className);
        Template template = ResourceManager.getInstance().getTemplate(velocityTemplate);
        if (!unit.exists() || force) {
            Context context2 = createContextWithSingleParam(context, PARAM_PACKAGE, packageName);
            String content = template.merge(context2);
            writeCompilationUnit(unit, content);
        }
    }

    /**
     * Overwrite the given contents into the given ICompilationUnit
     *
     * @param currentUnit the unit to replace the contents in (or null if new)
     * @param content     the new contents
     */
    private void writeCompilationUnit(File currentUnit, String content) {
        try {
            FileUtils.writeStringToFile(currentUnit, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write the given contents to the file described by the path and file name for the given project. If the file already exists and the contents are the same as the ones given, do nothing and return
     * false.
     *
     * @param content  the new file contents
     * @param path     the file path without the file name
     * @param fileName the file name
     * @return true if the file was written and false if not
     */
    public boolean writeResourceFile(String content, String path, String fileName) throws IOException {
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(dir, fileName);
        if (!file.exists()) {
            FileUtils.writeStringToFile(file, content);
            return true;
        } else {
            String existingContent = readFile(file);
            if (null != existingContent && existingContent.equals(content))
                return false;
            else {
                FileUtils.writeStringToFile(file, content);
                return true;
            }
        }
    }

    /**
     * Create a context to be used for synchronization
     */
    public static Context getDefaultContext(Object contextObject) {

        Context context = new VelocityContext();
        context.put(PARAM_NOW, new Date());
        context.put("dollar", "$");
        context.put("notDollar", "$!");

        context.put(PARAM_SNIPPET, new SnippetContext());
        boolean useCustomDAO = ModelGenerationConfigurationHolder.getBooleanProperty(Constants.PROP_USE_CUSTOM_ROOT_DAO, false);
        if (useCustomDAO) {
            String daoException = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_BASE_DAO_EXCEPTION);
            if (null != daoException && daoException.trim().length() > 0) {
                context.put(PARAM_EXCEPTION_CLASS, daoException.trim());
            }
        }
        context.put(PARAM_UTIL, new HSUtil());
        Map<Object, Object> properties = ResourceManager.getInstance().getTemplateParametersMap();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            context.put((String) entry.getKey(), entry.getValue());
        }
        if (null != contextObject) {
            if (contextObject instanceof Map) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) contextObject).entrySet()) {
                    if (null != entry.getKey()) {
                        context.put(entry.getKey().toString(), entry.getValue());
                    }
                }
            }
            context.put(PARAM_CONTEXT_OBJECT, contextObject);
        }
        return context;
    }

    /**
     * Return the user defined context object or null if N/A
     *
     * @return the user defined object
     */
    @SuppressWarnings({"deprecation", "CatchMayIgnoreException"})
    public static Object getContextObject() {
        try {
            String coName = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_CONTEXT_OBJECT);
            if (null != coName && coName.length() > 0) {
                coName = coName.trim();
                return Class.forName(coName).newInstance();
            }
        } catch (Exception e) {
        }
        return null;
    }

}