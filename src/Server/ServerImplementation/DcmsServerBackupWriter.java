package Server.ServerImplementation;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import Models.Record;

public class DcmsServerBackupWriter {
	/**
	 * Writes to the filename if file exists, If file does not exist, creates a
	 * file and then writes the contents
	 * 
	 * @param filename
	 * @param content
	 */
	File file;
	FileWriter fw;
	BufferedWriter bw;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date;
	public DcmsServerBackupWriter() {
	
	}

	
	/**
	 * Retrieves the current Date of the system
	 * and checks for existence of the file. If no,
	 * a new file is created and the timestamp is written to it
	 *  
	 * @param filename
	 * 					Has the name of the Filename stored in it.
	 */					
	
	public DcmsServerBackupWriter(String filename) {
		super();
		date = new Date();
		String timestamp = dateFormat.format(date).toString();
		this.file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				bw.write(timestamp + " : " + "DCMS_Backup");
				bw.newLine();
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	
	/**
	 * Receives the HashMap as a parameter and
	 * writes it to the file as a backup 
	 * along with the current timestamp
	 * 
	 * @param temp
	 * 				HashMap that is the repository of a server				
	 */

	public void backupMap(HashMap<String, List<Record>> temp) {
		try {

			date = new Date();
			String timestamp = dateFormat.format(date).toString();

			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(timestamp + " : ");
			bw.newLine();
			print(bw, temp);
			bw.close();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void print(BufferedWriter bw, HashMap<String, List<Record>> temp) throws IOException {
		for (String name : temp.keySet()) {

			String key = name.toString();
			String value = temp.get(name).toString();
			bw.write(key + ": " + value);
			bw.newLine();

			// System.out.println(key + " " + value);

		}

	}

}
