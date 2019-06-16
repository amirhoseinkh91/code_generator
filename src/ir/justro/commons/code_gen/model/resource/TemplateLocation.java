package ir.justro.commons.code_gen.model.resource;

import ir.justro.commons.code_gen.hsynch.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;



public class TemplateLocation {
	
	public static final String SEPARATOR = ":";
	
	private Template template;
	private String location;
	private String name;
	private boolean override;
	private boolean enabled;

	public TemplateLocation () {}

	/**
	 * Constructor
	 * @param templateInfo the template locaton contents
	 * @param project the Eclipse project
	 * @throws IOException
	 */
	public TemplateLocation (InputStream templateInfo) throws IOException {
		restore(IOUtils.toString(templateInfo));
	}

	/**
	 * Constructor
	 * @param templateInfo the template locaton contents
	 * @param project the Eclipse project
	 * @throws IOException
	 */
	public TemplateLocation (String templateInfo) throws IOException {
		restore(templateInfo);
	}

	/**
	 * Restore this template location given the String input
	 * @param s the string contents to restore from
	 * @throws IOException
	 */
	public void restore (String s) throws IOException {
		int start = 0;
		int index = s.indexOf(SEPARATOR);
		String templateName = s.substring(start, index);
		template = ResourceManager.getInstance().getTemplate(templateName);
		if (null != template) {
			start = index + 1;
			index = s.indexOf(SEPARATOR, start);
			name = s.substring(start, index);
			start = index + 1;
			index = s.indexOf(SEPARATOR, start);
			location = s.substring(start, index);
			int pIndex = location.indexOf("/");
			location = location.substring(pIndex+1, location.length());
			start = index + 1;
			index = s.indexOf(SEPARATOR, start);
			override = new Boolean(s.substring(start, index)).booleanValue();
			start = index + 1;
			enabled = new Boolean(s.substring(start, s.length())).booleanValue();
		}
	}

	/**
	 * Return the string representation of this template location
	 */
	public String toString () {
		StringBuffer sb = new StringBuffer();
		sb.append(template.getName() + SEPARATOR);
		sb.append(name + SEPARATOR);
		sb.append(location + SEPARATOR);
		sb.append(new Boolean(override).toString() + SEPARATOR);
		sb.append(new Boolean(enabled).toString());
		return sb.toString();
	}

	/**
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Return the file path transformed with the given Velocity context
	 * @param context the velocity context
	 * @return the transformed name
	 * @throws IOException
	 * @throws ResourceNotFoundException
	 * @throws MethodInvocationException
	 * @throws ParseErrorException
	 */
	public String getLocation (Context context) throws ParseErrorException, MethodInvocationException, IOException {
		StringWriter sw = new StringWriter();
		if (null != context) {
			try {
				Constants.templateGenerator.evaluate(context, sw, Velocity.class.getName(), location);
			}
			catch (ResourceNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			sw.write(location);
		}
		String s = sw.toString();
		return s;
	}

	/**
	 * @param location
	 */
	public void setLocation(String location) {
		if (null == location) location = "";
		else location = location.trim();
		int dolIndex = location.indexOf('$');
		int slashIndex = location.indexOf('/');
		int index = slashIndex;
		if (index == -1) index = dolIndex;
		if (index == -1) {
			this.location = "";
		}
		else {
			this.location = location;
		}
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the name transformed with the given Velocity context
	 * @param context the velocity context
	 * @return the transformed name
	 * @throws IOException
	 * @throws ResourceNotFoundException
	 * @throws MethodInvocationException
	 * @throws ParseErrorException
	 */
	public String getName (Context context) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
		StringWriter sw = new StringWriter();
		Constants.templateGenerator.evaluate(context, sw, Velocity.class.getName(), name);
		return sw.toString();
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		if (null == name) this.name = null;
		else this.name = name.trim();
	}

	/**
	 * @return
	 */
	public boolean shouldOverride() {
		return override;
	}

	/**
	 * @param override
	 */
	public void setOverride(boolean override) {
		this.override = override;
	}

	/**
	 * @return
	 */
	public Template getTemplate() {
		return template;
	}

	/**
	 * @param template
	 */
	public void setTemplate(Template template) {
		this.template = template;
	}

	/* *
	 * Validate the contents of this location based upon the template that this location references.
	 * A 0 length list means that it is valid
	 * @return a List of String errors
	 * /
	public List validate () {
		List errors = new ArrayList();
		if (null == name || name.length() == 0) {
			errors.add("You must enter the name");
		}
		else if (name.indexOf(':') >= 0) {
			errors.add("The name must not contain a ':'");
		}
		if (null == location || location.trim().length() == 0) {
			location = "";
		}
		else if (location.indexOf(':') >= 0) {
			errors.add("The location must not contain a ':'");
		}
		else {
			if (getTemplate().isClassTemplate()) {
				int index = location.indexOf("$");
				String locationCheck = location;
				if (index >= 0) locationCheck = locationCheck.substring(index+1, locationCheck.length());
				if (locationCheck.indexOf("/") >  0 || locationCheck.indexOf("\\") > 0) {
					errors.add("The package can not have file separators");
				}
			}
		}
		return errors;
	}
	*/
	/*
	public IPackageFragmentRoot getPackageFragmentRoot () {
		String location = null; 
		try {
			location = getLocation(null);
		}
		catch (Exception e) {}
		int index = location.indexOf('$');
		if (index >= 0) location = location.substring(0, index);
		IJavaProject project = JavaCore.create(getOutputProject());
		try {
			String comp = getOutputProjectName() + "/" + location;
			IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
			for (int i=0; i<roots.length; i++) {
				if (null != roots[i].getCorrespondingResource() && roots[i].getJavaProject().equals(project) && !roots[i].isArchive()) {
					String rootName = roots[i].getPath().toPortableString();
					while (rootName.startsWith("/")) rootName = rootName.substring(1, rootName.length());
					if (rootName.equals(comp)) {
						return roots[i];
					}
				}
			}
		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	*/
	public String getPackage (Context context) throws ParseErrorException, MethodInvocationException, IOException {
		String location = getLocation(context);
		int index = location.indexOf('$');
		if (index >= 0) return location.substring(index+1, location.length());
		else return location;
	}

	public boolean isValid () {
		return (null != template);
	}
}