package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.junit.runners.Parameterized.Parameter;
import com.mysql.jdbc.Field;

public class ReadRuleDict {
	
	private Map<String, String> map;

	public ReadRuleDict() {
		map = new HashMap<>(); 
	}
	
	protected void initReadMapLocal(String local_path) throws IOException {
	
		File file = new File(local_path);
		/**
		 * jar run will use path /User/aaron/..
		 * */	
		
		String current = new java.io.File( "." ).getCanonicalPath();
		if (file.exists()) {
			file = new File(local_path);
			System.err.println("[1] Read success -- " + current + "/snortplus.txt");
		} else {
			file = new File(current + "/snortplus.txt");   // <-----------
			if (file.exists()) {
				System.err.println("[2] Read success -- " + current + "/support_file/snortplus.txt");
			} 
			else {	
				String currentDir = System.getProperty("user.dir");
				file = new File(currentDir + "/support_file/snortplus.txt");  // <-----------
				if (file.exists()) {
					System.err.println("[3] Read -- " + currentDir + "/support_file/snortplus.txt");
				} else {	
					System.err.println("======= USE BACKUP RULE FILE IN /support_file/backuo.txt ========");
					file = new File(currentDir + "/support_file/backup.txt");
				}
			}
		}
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line, key = "", value = "";		
		System.err.println("\n\n--$ ReadRuleDict.java  --- initReadMapLocal() by '/latest_rule/snortplus.txt' file $---- ");
		
		while ((line = br.readLine()) != null) {	
			if (line.contains("# Rule -")) {
				String[] arr = line.split(" - ");
				key = arr[1];
			} else if (line.contains("# $Family_comment")) {
				String[] arr = line.split(" -");
				value += arr[1];
			} else if (line.contains("# $Variant_comment")) {
				String[] arr = line.split(" -");
				value += arr[1];
				System.out.println("[" + key + ", " + value + "]");
				map.put(key, value);
				value = "";
			}
		}
		System.out.println("--$------------------------------$--");
	}

/*	
	protected void initReadMapOnline(String url_path) throws IOException {	
		URL oracle = new URL(url_path);
		BufferedReader in = new BufferedReader( new InputStreamReader(oracle.openStream()) );
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println(line);
		}
		in.close();
	}
*/
		
	protected String getCommentByRule(String ruleKey) {
		if (ruleKey == null || ruleKey.isEmpty()) return null;		
		if (!map.containsKey(ruleKey)) return "> No such rule. Comment not available yet.";
		return map.get(ruleKey);
	}
	
	protected Map<String, String> getMap() {
		return map;
	}
	
	public static void main(String[] args) throws IOException {
		ReadRuleDict rd = new ReadRuleDict();	
		rd.initReadMapLocal("/latest_rule/snortplus.txt");
		// "C:\\Users\\pankaj\\Desktop\\test.txt"
		// rd.initReadMapOnline("https://github.com/newskyinc/Tool_for_Snort/blob/master/snortplus/snortplus.txt");
		System.out.println( rd.getCommentByRule("IOTExploit.Hikvision.ACBypass") );
	}
}
