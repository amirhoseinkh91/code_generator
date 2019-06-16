package ir.viratech.commons.code_gen.ddl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

public class DdlGenerator {
	
	private static final transient Logger logger = Logger.getLogger(DdlGenerator.class);
	
	public void run(Collection<File> hbmFiles, DdlGenerationConfiguration ddlGenerationConfiguration) throws Exception {

		Class<?> HibernateConfigurationClass = Class.forName("org.hibernate.cfg.Configuration");
		Class<?> schemaExportClass = Class.forName("org.hibernate.tool.hbm2ddl.SchemaExport");
		
		Object configuration = HibernateConfigurationClass.newInstance();
		String dialect = ddlGenerationConfiguration.getHibernateDialect();
		logger.debug("using dialect: "+dialect);
		configuration.getClass().getMethod("setProperty", String.class, String.class).invoke(configuration, "hibernate.dialect", dialect);
		for (File file : hbmFiles) {
			logger.debug("adding file "+file);
			configuration.getClass().getMethod("addFile", File.class).invoke(configuration, file);
		}

		Object schemaExport = schemaExportClass.getDeclaredConstructor(configuration.getClass()).newInstance(configuration);

		String outputFileLocation = ddlGenerationConfiguration.getOutputFileLocation();
		logger.debug("outputFileLocation: " + outputFileLocation);
		schemaExportClass.getMethod("setOutputFile", String.class).invoke(schemaExport, outputFileLocation);

		schemaExportClass.getMethod("setFormat", boolean.class).invoke(schemaExport, false);

		Method method_create = schemaExportClass.getMethod("create", boolean.class, boolean.class);
		// method.invoke(schemaExport, Boolean.FALSE, Boolean.FALSE);
		// Kian: The first parameter was FALSE,
		// but there is bug in Hibernate 4.1.0Final, which forced me to make it TRUE.
		method_create.invoke(schemaExport, Boolean.TRUE, Boolean.FALSE);
		
		@SuppressWarnings("unchecked")
		List<Exception> exceptions = (List<Exception>) schemaExportClass.getMethod("getExceptions").invoke(schemaExport);
		
		if (exceptions != null && !exceptions.isEmpty()) {
			Exception last = null;
			for (Exception ex : exceptions) {
				logger.error("", ex);
				last = ex;
			}
			throw last;
		}
	}

	public static void main(String... args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: "+DdlGenerator.class.getSimpleName()+" <project-dir> <config-file>");
			System.exit(1);
		}
		logger.info("The following resources must be in the classpath\n" +
					"\t- cglib\n" +
					"\t- commons-collections\n" +
					"\t- commons-logging\n" +
					"\t- dom4j\n" +
					"\t- odmg\n" +
					"\t- hibernate");
		DdlGenerationConfiguration ddlGenerationConfiguration = DdlGenerationConfiguration.create(args[0], args[1]);
		new DdlGenerator().run(ddlGenerationConfiguration.getHbmFiles(), ddlGenerationConfiguration);
		System.out.println("OK");
	}
}
