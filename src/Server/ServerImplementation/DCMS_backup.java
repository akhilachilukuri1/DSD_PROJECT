package Server.ServerImplementation;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
public class DCMS_backup {
	
	/**
	 * Writes to the filename if file exists, 
	 * If file does not exist, creates a file and then writes the contents
	 * 
	 * @param filename
	 * @param content
	 */

	public static void backup(String filename, String content) {
		try {

			File file = new File(filename);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String timestamp = dateFormat.format(date).toString();
			
			// if file does not exists, then create it
			if (!file.exists()) {
				file.createNewFile();
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(timestamp+ " : ");
				bw.write(content);
				bw.newLine();
				bw.close();
			} else {
				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(timestamp+ " : ");
				bw.write(content);
				bw.newLine();
				bw.close();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
