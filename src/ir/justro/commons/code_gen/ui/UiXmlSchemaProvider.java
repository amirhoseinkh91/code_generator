package ir.justro.commons.code_gen.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class UiXmlSchemaProvider {
	
	private static final String schemaResourcePath = "uiModel.xsd";

	public static URL getSchemaUrl() {
		return UiXmlSchemaProvider.class.getResource(schemaResourcePath);
	}
	
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: "+UiXmlSchemaProvider.class.getSimpleName()+" <schema-file-location>");
			System.exit(1);
		}
		
		try (InputStream is = getSchemaUrl().openStream();
			OutputStream os = new FileOutputStream(args[0])) {
			IOUtils.copy(is, os);
		}
	}

}
