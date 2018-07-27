package Server.ServerImplementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.javafx.collections.MappingChange.Map;

import Models.Record;
import Models.Student;
import Models.Teacher;

public class test {

	static HashMap<String, List<Record>> recordsMap = new HashMap<String, List<Record>>();

	public static void main(String... strings) {

		DCMS_backup obj = new DCMS_backup();

		String teacherID = "TR";
		String firstName = "mas";
		String lastname = "mas";
		String address = "mas";
		String phone = "mas";
		String specialization = "mas";
		String location = "mas";
		String requestID = "mas";
		Teacher teacherObj = new Teacher("MTL", teacherID, firstName, lastname, address, phone, specialization,
				location);
		String key = lastname.substring(0, 1);
		String message = addRecordToHashMap(key, teacherObj, null);

		key = lastname.substring(0, 1);
		String lastname1 = "tim";
		String teacherID1 = "TR";
		String firstName1 = "mas";
		String address1 = "mas";
		String phone1 = "mas";
		String specialization1 = "mas";
		String location1 = "mas";
		String requestID1 = "mas";
		Teacher teacherObj1 = new Teacher("MTL", teacherID1, firstName1, lastname1, address1, phone1, specialization1,
				location1);
		String key1 = lastname1.substring(0, 1);
		String message1 = addRecordToHashMap(key1, teacherObj1, null);

		DCMS_backup.backupMap(recordsMap);

	}

	public static String addRecordToHashMap(String key, Teacher teacher, Student student) {
		String message = "Error";
		if (teacher != null) {
			List<Record> recordList = recordsMap.get(key);
			if (recordList != null) {
				recordList.add(teacher);
			} else {
				List<Record> records = new ArrayList<Record>();
				records.add(teacher);
				recordList = records;
			}
			recordsMap.put(key, recordList);
			message = "success";
		}
		return message;
	}

}
