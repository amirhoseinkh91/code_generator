package ir.justro.commons.code_gen.model.util;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.AbstractContext;
import org.apache.velocity.context.Context;


public class HSUtil {

	public static String getStaticName (String s) {
		boolean wasLastSpace = false;
		if (null == s) return null;
		if (s.toUpperCase().equals(s)) return s;
		else {
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<s.toCharArray().length; i++) {
				char c = s.toCharArray()[i];
				if (Character.isLetterOrDigit(c)) {
					if (c == ' ' || c == '-') {
						sb.append("_");
						wasLastSpace = true;
					}
					else if (Character.isUpperCase(c) || i == 0) {
						if (sb.length() > 0 && !wasLastSpace) sb.append("_");
						sb.append(Character.toUpperCase(c));
						wasLastSpace = false;
					}
					else {
						sb.append(Character.toUpperCase(c));
						wasLastSpace = false;
					}
				}
				else if (Character.isWhitespace(c) || c == '.' || c == '-' || c == '_') {
					if (!wasLastSpace) {
						sb.append("_");
						wasLastSpace = true;
					}
				}
			}
			return sb.toString();
		}
	}

	public static String firstLetterUpper (String s) {
		if (null == s) return null;
		else if (s.length() == 0) return s;
		else {
			return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
		}
	}

	public static String firstLetterLower (String s) {
		if (null == s) return null;
		else if (s.length() == 0) return s;
		else {
			return s.substring(0, 1).toLowerCase() + s.substring(1, s.length());
		}
	}

	public static String getClassPart (String fullClassName) {
		if (null == fullClassName) return null;
		int index = fullClassName.lastIndexOf(".");
		if (index == -1) return fullClassName;
		else return fullClassName.substring(index + 1, fullClassName.length());
	}

	private static final char[] vowelList = {'A', 'E', 'I', 'O', 'U'};
	public static String getPropName (String tableName) {
		if (tableName.toUpperCase().equals(tableName)) {
			boolean vowelsFound = false;
			for (int i=0; i<tableName.toCharArray().length; i++) {
				char c = tableName.toCharArray()[i];
				for (int j=0; j<vowelList.length; j++) {
					if (c == vowelList[j]) vowelsFound = true;
				}
			}
			if (vowelsFound) {
				tableName = tableName.toLowerCase();
			}
		}
		return getJavaNameCap(tableName);
	}

	public static String getJavaName (String s) {
		if (null == s) return null;
		else return firstLetterLower(HSUtil.getJavaNameCap(s));
	}

	public static String getJavaNameCap (String s) {
		if (s.indexOf("_") < 0 && s.indexOf("-") < 0) {
			return firstLetterUpper(s);
		}
		else {
			StringBuffer sb = new StringBuffer();
			boolean upper = true;
			char[] arr = s.toCharArray();
			for (int i=0; i<arr.length; i++) {
				if (arr[i] == '_' || arr[i] == '-') upper = true;
				else if (upper) {sb.append(Character.toUpperCase(arr[i])); upper = false;}
				else sb.append(Character.toLowerCase(arr[i]));
			}
			return sb.toString();
		}
	}

	public static String getPluralName (String s) {
		if (null == s) return null;
		if (s.endsWith("s")) return s + "es";
		else return s + "s";
	}

	public static String getPropDescription (String s) {
		if (null == s) return null;
		StringBuffer sb = new StringBuffer();
		boolean upper = false;
		char[] arr = s.toCharArray();
		for (int i=0; i<arr.length; i++) {
			if (i == 0) {
				sb.append(Character.toUpperCase(arr[i]));
				if (Character.isUpperCase(arr[i])) upper = true;
			}
			else if (Character.isUpperCase(arr[i])) {
				if (!upper) sb.append(" ");
				sb.append(arr[i]);
				upper = true;
			}
			else {
				sb.append(arr[i]);
				upper = false;
			}
		}
		return sb.toString();
	}

	public static String getPackagePart (String fullClassName) {
		if (null == fullClassName) return null;
		int index = fullClassName.lastIndexOf(".");
		if (index == -1) return "";
		else return fullClassName.substring(0, index);
	}

	public static String addPackageExtension (String packageStr, String extension) {
		if (null == packageStr || packageStr.trim().length() == 0) return extension;
		else return packageStr + "." + extension;
	}

	public static MarkerContents getMarkerContents(String content, String marker) {
		int startIndex = content.indexOf("[" + marker + " BEGIN]");
		if (startIndex > 0) {
			char[] arr = content.toCharArray();
			char compCharN = '\n';
			char compCharR = '\r';
			while (startIndex < arr.length) {
				char c = arr[startIndex];
				if (c == compCharN) {
					if (arr[startIndex + 1] == compCharR) startIndex ++;
					startIndex ++;
					break;
				}
				startIndex ++;
			}
			if (startIndex < arr.length) {
				int endIndex = content.indexOf("[" + marker + " END]");
				if (endIndex > startIndex) {
					while (endIndex > startIndex) {
						char c = arr[endIndex];
						if (c == compCharN) {
							// if (arr[endIndex - 1] == compCharR) endIndex --;
							endIndex ++;
							break;
						}
						endIndex --;
					}
					String previousContent = content.substring(0, startIndex);
					String postContent = content.substring(endIndex, content.length());
					String midContent = content.substring(startIndex, endIndex);
					MarkerContents mc = new MarkerContents();
					mc.setPreviousContents(previousContent);
					mc.setPostContents(postContent);
					mc.setContents(midContent);
					mc.setStartPosition(startIndex);
					mc.setEndPosition(endIndex);
					return mc;
				}
			}
		}
		return null;
	}

	/**
	 * Utility method for finding all the keys in a Context
	 */
	public static Collection<String> getKeys(Context ctx) {
		Collection<String> res= new HashSet<>();
		
		for (Object object : ctx.getKeys()) {
			res.add((String)object);
		}
		if (ctx instanceof AbstractContext) {
			Context chainedContext = ((AbstractContext)ctx).getChainedContext();
			if (chainedContext != null)
				res.addAll(getKeys(chainedContext));
		}
		return res;
	}

	public static String getUrlFileName(URL url) {
		String path = url.getPath();
		int i = path.lastIndexOf('/');
		return (i<0) ? path : path.substring(i+1);
	}

	
	public static String addSchemaName(String schemaName, String tableName) {
		if (!StringUtils.isEmpty(schemaName))
			tableName = schemaName +"."+ tableName; 
		return tableName;
	}
	
	
	public static boolean isTrue(String s) {
		if (s==null)
			return false;
		return s.trim().toLowerCase().equals("true");
	}
	
	public static boolean isFalse(String s) {
		if (s==null)
			return false;
		return s.trim().toLowerCase().equals("false");
	}
}
