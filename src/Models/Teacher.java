package Models;

import java.io.Serializable;

/**
 * Teacher Model Class that holds the student's attributes
 * extends the base record class, to enable easy marshalling 
 * and unmarshalling between client and server has the getters and setters for the attributes
 */
public class Teacher extends Record implements Serializable {
	String firstName;
	String lastName;
	String Address;
	String phone;
	String specilization;
	String location;
	String TeacherID;
	String ManagerID;

	public Teacher() {

	}

	public Teacher(String managerID, String teacherID, String firstName,
			String lastname, String address, String phone, String Specialization,
			String location) {
		super(teacherID, firstName, lastname);
		this.setManagerID(managerID);
		this.setAddress(address);
		this.setPhone(phone);
		this.setSpecilization(Specialization);
		this.setLocation(location);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSpecilization() {
		return specilization;
	}

	public void setSpecilization(String specilization) {
		this.specilization = specilization;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTeacherID() {
		return TeacherID;
	}

	public void setTeacherID(String teacherID) {
		TeacherID = teacherID;
	}

	public String getManagerID() {
		return ManagerID;
	}

	public void setManagerID(String managerID) {
		ManagerID = managerID;
	}

	@Override
	public String toString() {
		return this.getManagerID() + "," + this.getRecordID() + ","
				+ this.getFirstName() + "," + this.getLastName() + ","
				+ this.getAddress() + "," + this.getPhone() + ","
				+ this.getSpecilization() + "," + this.getLocation();
	}

	public String serialize() {
		return "Teacher" + getManagerID() + getRecordID() + "," + getFirstName()
				+ "," + getLastName() + "," + getAddress() + "," + getPhone() + ","
				+ getSpecilization() + "," + getLocation();
	}

}
