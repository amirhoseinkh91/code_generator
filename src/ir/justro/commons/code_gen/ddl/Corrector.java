package ir.justro.commons.code_gen.ddl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Corrector {
	
	public static class BadInputException extends Exception {
		private static final long serialVersionUID = 1L;

		public BadInputException() {
			super();
		}

		public BadInputException(String message, Throwable cause) {
			super(message, cause);
		}

		public BadInputException(String message) {
			super(message);
		}

		public BadInputException(Throwable cause) {
			super(cause);
		}
		
	}
	
	private static final String commands[] = {"create", "drop", "alter", "select"};
	
	private static final String CREATE_TABLE = "create table";
	private static final String CREATE_SEQUENCE = "create sequence";
	
	private static final String[][] replacements = {
			{CREATE_TABLE, "blob", "longblob"},
			{CREATE_TABLE, "int4", "bigint"},
			{CREATE_TABLE, "int8", "bigint"}
		};

	
	
	private static boolean startsWithCommand(String str) {
		for (String command : commands) {
			if (str.startsWith(command)) {
				return true;
			}
		}
		return false;
	}
	
	private static String noWhite(String str) {
		int i = 0;
		for (; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				break;
			}
		}
		return str.substring(i);
	}

	private static String replaceAll(String str, String first, String second) {
		return str.replace(first, second);
	}
	
	
	private static boolean isTokenEnd(char c) {
		if (Character.isWhitespace(c))
			return true;
		switch (c) {
		case ')':
		case '(':
		case ',':
			return true;
		}
		return false;
	}
	
	private static String nextToken(String s, int beginIndex) {
		String r = "";
		for (int i = beginIndex; i < s.length() && !isTokenEnd(s.charAt(i)); i++) {
			r += s.charAt(i);
		}
		return r;
	}
	private static String nextToken(String s) {
		return nextToken(s, 0);
	}
	
	
	
	private List<String> lines = new ArrayList<String>();
	private List<String> statements = new ArrayList<String>();
	
	
	
	private String getColName(String str, int i) {
		String pre = str.substring(0, i);
		pre = StringUtils.reverse(pre);
		String colName = nextToken(noWhite(pre));
		colName = StringUtils.reverse(colName);
		return colName;
	}
	
	private static final String GEOMETRY_COLUMN = "AddGeometryColumn";
	private static final String GEOMETRY_COLUMN_BEGIN = "[[";
	private static final String GEOMETRY_COLUMN_END = "]]";
	
	
	private void replaceGeomColumns(String str, List<String> res) throws BadInputException {
		if (!str.startsWith(CREATE_TABLE)) {
			res.add(str);
			return;
		}

		String tableName = nextToken(noWhite(str.substring(CREATE_TABLE.length())));
		//System.err.println(tableName);
		boolean[] b = new boolean[str.length()];
		Arrays.fill(b, true);
		
		List<String> addGeomCols = new ArrayList<String>();
		//System.err.println(str);
		
		for (int i = 0; i < str.length(); i++) {
			if (b[i] && str.startsWith(GEOMETRY_COLUMN, i)) {
				String columnName = getColName(str, i);
				//System.err.println(columnName);
				
				int gcb = str.indexOf(GEOMETRY_COLUMN_BEGIN, i);
				if (gcb < 0) {
					throw new BadInputException("Could not find '"+GEOMETRY_COLUMN_BEGIN+"' in '"+str+"' after index "+i+".");
				}
				gcb += GEOMETRY_COLUMN_BEGIN.length();
				
				int gce = str.indexOf(GEOMETRY_COLUMN_END, gcb);
				if (gce < 0) {
					throw new BadInputException("Could not find '"+GEOMETRY_COLUMN_END+"' in '"+str+"' after index "+gcb+".");
				}
				//System.err.println(gcb+" "+gce);
				
				String data = str.substring(gcb, gce);
				//System.err.println(data);
				
				String addColStmt = "select AddGeometryColumn('"+tableName.toLowerCase()+"', '"+columnName+"', "+data+")\n";
				//System.err.println(addColStmt);
				addGeomCols.add(addColStmt);

				int delb = i;
				while (delb>=0 && str.charAt(delb)!=',' && str.charAt(delb)!='(')
					delb--;
				
				int dele = gce+GEOMETRY_COLUMN_END.length();
				while (dele<str.length() && str.charAt(dele)!=',' && str.charAt(dele)!=')')
					dele++;

				if (delb>=0 && str.charAt(delb)==',') {
					dele--;
				} else {
					delb++;
					if (dele>=str.length() || str.charAt(dele)==')')
						dele--;
				}
				
				for (int j = delb; j <= dele; j++) {
					b[j] = false;
				}
				
				/*
				for (boolean bb : b)
					System.err.print(BooleanUtils.toInteger(bb));
				System.err.println();
				//*/
			}
		}
		
		String newstr = "";
		for (int i = 0; i < str.length(); i++) {
			if (b[i]) {
				newstr += str.charAt(i);
			}
		}
		//System.err.println();
		
		res.add(newstr);
		for (String addGeomCol : addGeomCols) {
			res.add(addGeomCol);
		}
	}
	
	// ****************************
	
	
	public void readLines() {
		Scanner scanner;
		scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			lines.add(noWhite(scanner.nextLine()));
		}
		scanner.close();
		/*
		for (String line : lines) {
			System.err.println(line);
		}
		//*/
	}
	
	
	public void makeStmts() {
		String s = "";
		for (int i = lines.size() - 1; i >= 0; i--) {
			String l = lines.get(i);
			s = l + "\n" + s;
			if (startsWithCommand(l)) {
				statements.add(s);
				s = "";
			}
		}
		Collections.reverse(statements);
	}
	
	
	public void doReplacements() {
		for (int i = 0; i < statements.size(); i++) {
			String stmt = statements.get(i);
			for (String[] replacement : replacements) {
				if (stmt.startsWith(replacement[0])) {
					stmt = replaceAll(stmt, replacement[1], replacement[2]);
				}
			}
			statements.set(i, stmt);
		}
	}

	public void correctCreate() {
//		for (int i = 0; i < statements.size(); i++) {
//			String stmt = statements.get(i);
//			if (stmt.startsWith("create table"))
//				statements.set(i, stmt+"type=innoDB CHARSET=utf8 ");
//		}
	}

	public void replaceGeomColumns() throws BadInputException {
		List<String> res = new ArrayList<String>();
		for (String stmt : statements) {
			replaceGeomColumns(stmt, res);
		}
		statements = res;
	}
	
	
	private void addIdSequenceStatement(String str, List<String> res) {
		if (!str.startsWith(CREATE_SEQUENCE)) {
			res.add(str);
			return;
		}

		String sequenceName = nextToken(noWhite(str.substring(CREATE_SEQUENCE.length())));
//		System.err.println("seq name: '"+sequenceName+"'");
		
		int dotIndex = sequenceName.indexOf('.');
		String schema = (dotIndex<0) ? null : sequenceName.substring(0, dotIndex);
		String seq = (dotIndex<0) ? sequenceName : sequenceName.substring(dotIndex+1);
		
		//TODO: implement it more generally by reading the HBM files, supporting optimizers other than 'pooled'
		
		final String seqTablePrefix = "IDSEQ_";
		final String seqTableSuffix = "";
		
		if (!seq.startsWith(seqTablePrefix) || !seq.endsWith(seqTableSuffix)) {
			res.add(str);
			return;
		}
		
		String tableName = seq.substring(seqTablePrefix.length(), seq.length()-seqTableSuffix.length());
//		System.err.println("tableName: '"+tableName+"'");
		
		final String increment = " increment ";
		int incIndex = str.indexOf(increment);
		if (incIndex<0) {
			res.add(str);
			res.add("select setval('"+sequenceName+"', (select coalesce(max(id)+1, 1) FROM "+schema+"."+tableName+"))\n");
			return;
		}
		
		String incrStr = "";
		for (int i=incIndex+increment.length(); i<str.length() && Character.isDigit(str.charAt(i)); i++) {
			incrStr += str.charAt(i);
		}
		
		if (incrStr.isEmpty()) {
			res.add(str);
			return;
		}
		
		final String minvalueStr = " minvalue ";
		if (!str.contains(minvalueStr))
			str = str.substring(0, incIndex)+minvalueStr+"0"+str.substring(incIndex);
		
		res.add(str);
		res.add("select setval('"+sequenceName+"', (select cast (coalesce(trunc(max(id)/"+incrStr+"+1)*"+incrStr+", 0) as bigint) FROM "+schema+"."+tableName+"))\n");
	}

	private void addIdSequenceStatements() {
		List<String> res = new ArrayList<String>();
		for (String stmt : statements) {
			addIdSequenceStatement(stmt, res);
		}
		statements = res;
//		for (String string : res) {
//			System.err.println("'"+string+"'");
//		}
		
	}


	public void addSemicolons() {
		for (int i = 0; i < statements.size(); i++) {
			String stmt = statements.get(i);
			statements.set(i, stmt.substring(0, stmt.length() - 1) + ";");
		}
	}
	
	
	public void addMoreNewLines() {
		for (int i = 0; i < statements.size(); i++) {
			int s = 0;
			char[] stmt = statements.get(i).toCharArray();
			
			for (int j = 0; j < stmt.length; j++) {
				if (s > 100 && stmt[j] == ' ') {
					s = 0;
					stmt[j] = '\n';
				} else {
					s++;
				}
			}
			statements.set(i, String.valueOf(stmt));
		}
	}
	
	
	public void output() {
		for (String stmt : statements) {
			System.out.println(stmt);
		}
	}
	
	
	protected void run() throws BadInputException {
		this.readLines();
		this.makeStmts();
		this.doReplacements();
		this.correctCreate();
		this.replaceGeomColumns();
		this.addIdSequenceStatements();
		this.addSemicolons();
		this.addMoreNewLines();
		this.output();
	}
	
	public static void main(String[] arg) throws IOException {
		System.err.println("Correction Started...");
		if (arg.length != 1) {
			System.err.println("Usage: corrector <config-file-name>");
			System.exit(1);
		}
		File file = new File(arg[0]);
		if (!file.canRead()) {
			System.err.println("Bad config file: "+arg[0]);
			System.exit(2);
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Corrector corrector = objectMapper.readValue(file, Corrector.class);
			corrector.run();
		} catch (BadInputException e) {
			System.err.println("Bad input: " + e.getMessage());
			System.exit(10);
		}
		System.err.println("Correction Done.");
	}

}