package ir.justro.commons.code_gen.hsynch;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class HSynchUtil {

	public static final String[] HBM_EXTENSIONS = {"hbm.xml"};
	
	@SuppressWarnings("unchecked")
	public static Collection<File> findHbmFiles(File hbmBaseDirectory) {
		System.out.println(hbmBaseDirectory);
		return (Collection<File>) FileUtils.listFiles(hbmBaseDirectory, HBM_EXTENSIONS, true);
	}
	
}
