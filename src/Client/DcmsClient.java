package Client;

import DcmsApp.*;

import Conf.Constants;
import Conf.LogManager;
import Conf.ServerCenterLocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DcmsClient perform operations for MTL, DDO and LVL locations by sending the
 * request calls to the server.
 * 
 * Receives the Success/Failure message for the corresponding operations and
 * forwards them to the client.
 * 
 */

public class DcmsClient {
	static Dcms dcmsImplMTL, dcmsImplLVL, dcmsImplDDO;
	static LogManager logManager;

	/**
	 * Client code to get the inputs from the user for the operations and sends
	 * calls to the corresponding server locations
	 * 
	 * @param args[]
	 *            port number and IP address
	 * 
	 */
	public static void main(String args[]) throws IOException {
		ClientImp serverloc = null;
		while (true) {
			try {
				Pattern validate = Pattern.compile("([0-9]*)");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Please enter the Client's ManagerID");
				String managerID = br.readLine().trim();
				String manager_number = managerID.substring(3, 6);
				Matcher matchID = validate.matcher(manager_number);
				if (managerID.length() != 7) {
					System.out.println("Too many characters in the manager ID. "
							+ "please enter in (LOCXXXX) format, where LOC={MTL,DDO,LVL}");
					continue;
				} else if (!matchID.matches()) {
					System.out.println("Invalid character in MangerID.please "
							+ "enter in (LOCXXXX) format,where XXXX can only be numbers");
					continue;
				}
				new File(Constants.LOG_DIR + managerID).mkdir();
				logManager = new LogManager(managerID);
				logManager.logger.log(Level.INFO, "Log client started!");
				if (managerID.contains("MTL")) {
					serverloc = new ClientImp(args, ServerCenterLocation.MTL, managerID);
				} else if (managerID.contains("LVL")) {
					serverloc = new ClientImp(args, ServerCenterLocation.LVL, managerID);
				} else if (managerID.contains("DDO")) {
					serverloc = new ClientImp(args, ServerCenterLocation.DDO, managerID);
				} else {
					System.out.println("wrong manager ID.please enter again");
					continue;
				}
				int i = 1;
				while (i != 0) {
					System.out.println("choose the operation");
					System.out.println("1) Create the Teacher record");
					System.out.println("2) Create the Student record");
					System.out.println("3) Get the record count");
					System.out.println("4) Edit the record");
					System.out.println("5) Transfer the record");
					System.out.println("6) Kill the Primary Server");
					System.out.println("7) Logout manager");

					try {
						Integer choice = Integer.parseInt(br.readLine().trim());

						switch (choice) {
						case 1:
							/*
							 * Getting input for Creating the Teacher record
							 * operation
							 */
							System.out.println("Enter the first name of the teacher");
							String firstNameT = br.readLine().trim();

							System.out.println("Enter the last name of the teacher");
							String lastNameT = br.readLine().trim();

							System.out.println("Enter the address of the teacher");
							String addressT = br.readLine().trim();
							String phoneNumber = null;
							String phoneT;

							while (true) {
								System.out.println("Enter the Phone number in 123-456-7689 format");
								phoneNumber = br.readLine().trim();
								Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
								Matcher matcher = pattern.matcher(phoneNumber);
								if (matcher.matches()) {
									phoneT = phoneNumber;
									break;
								} else {
									System.out.println("Invalid phone number ...exiting the program");
									logManager.logger.log(Level.INFO, "Validation Failed for "
											+ "phone number and exiting the program(ManagerID:" + managerID + ")");
									continue;
								}
							}
							String specilizationT = null;
							while (true) {
								System.out.println("Enter the specialization of the teacher");
								specilizationT = br.readLine().trim();
								if (specilizationT.matches("[0-9]*$")) {
									System.out.println(" specialization of the teacher Contains Number");
									continue;
								} else
									break;

							}
							String locationT;
							String location = null;
							while (true) {
								System.out.println("Enter the Location(MTL/LVL/DDO)");
								location = br.readLine().trim();

								logManager.logger.log(Level.INFO,
										"Validating the status" + " entered (ManagerID:" + managerID + ")");
								if (location.equalsIgnoreCase("LVL") || location.equalsIgnoreCase("MTL")
										|| location.equalsIgnoreCase("DDO")) {
									locationT = location;
									break;
								} else {
									System.out.println("Invalid Location ...exiting the program");
									logManager.logger.log(Level.INFO,
											"Validation Failed for location and exiting the program(ManagerID:"
													+ managerID + ")");
									continue;
								}
							}
							System.out.println(serverloc.createTRecord(managerID, firstNameT + "," + lastNameT + ","
									+ addressT + "," + phoneT + "," + specilizationT + "," + locationT));
							break;
						case 2:
							/*
							 * Getting input for creating the Student record
							 * operation
							 */
							System.out.println("Enter the first name of the student");
							String firstNameS = br.readLine().trim();
							System.out.println("Enter the last name of the student");
							String lastNameS = br.readLine().trim();
							System.out.println("Enter the number of courses registered by the student");
							int coursesCount = Integer.parseInt(br.readLine().trim());
							System.out.println(
									"Enter the " + coursesCount + " courses(one per line) registered by the student");
							String courses = null;
							for (int n = 0; n < coursesCount; n++) {
								String course = br.readLine().trim();
								if (n == 0)
									courses = course;
								else
									courses = courses + "/" + course;
							}

							String status = null;
							String statusDate = null;

							while (true) {
								System.out.println("Enter the status of student (Active/Inactive)");
								status = br.readLine().trim();
								if ((status.toUpperCase().equals("ACTIVE")))
									break;
								else if ((status.toUpperCase().equals("INACTIVE")))
									break;
								else {
									System.out.println("Status assigned Invalid!");
									status = "Invalid Status";
									continue;
								}
							}

							if ((status.toUpperCase().equals("ACTIVE"))) {
								while (true) {
									System.out.println(
											"Enter the date when the student became active(Format :: 29-02-2018)");
									Pattern datePattern = Pattern.compile("([0-3][0-9])-([0-1][1-9])-([0-9]{4})");
									statusDate = br.readLine().trim();
									Matcher matcherDate = datePattern.matcher(statusDate);
									if (matcherDate.matches())
										break;
									else {
										System.out.println("Invalid Date Format - enter in correct format");
										continue;
									}
								}
							} else if ((status.toUpperCase().equals("INACTIVE"))) {
								while (true) {
									System.out.println(
											"Enter the date when the student became inactive(Format :: 29-02-2018)");
									Pattern datePattern = Pattern.compile("([0-3][0-9])-([0-1][1-9])-([0-9]{4})");
									statusDate = br.readLine().trim();
									Matcher matcherDate = datePattern.matcher(statusDate);
									if (matcherDate.matches())
										break;
									else {
										System.out.println("Invalid Date Format - enter in correct format");
										continue;
									}
								}
							}

							System.out.println(serverloc.createSRecord(managerID,
									firstNameS + "," + lastNameS + "," + courses + "," + status + "," + statusDate));
							break;
						case 3:
							/*
							 * Get the record count of all the server operation
							 */
							System.out.println("Total Record Count from all " + Constants.TOTAL_SERVERS_COUNT
									+ " servers is :: " + serverloc.getRecordCounts(managerID));
							break;
						case 4:
							/*
							 * Getting the record ID as input for Edit record
							 * operation
							 */
							System.out.println("Enter the Record ID");
							String recordID = br.readLine().trim();
							String type = recordID.substring(0, 2);
							String fieldName = null;
							String newCourses = null;
							int fieldNum = 0;
							if (type.equals("TR")) {
								System.out.println(
										"Enter the  field number  to be updated (1.address 2.phone or 3.location)");
								try {

									fieldNum = Integer.parseInt((br.readLine().trim()));
								} catch (NumberFormatException e) {
									System.out.println("wrong field number!!...please try again");
									continue;
								}
								if (fieldNum == 1)
									fieldName = "Address";
								else if (fieldNum == 2)
									fieldName = "Phone";
								else if (fieldNum == 3)
									fieldName = "Location";
								else
									System.out.print("Wrong selection of input to edit record");
							} else if (type.equals("SR")) {

								System.out.println(
										"Enter field number to be updated (1.CoursesRegistered 2.status or 3.statusDate)");
								fieldNum = Integer.parseInt((br.readLine().trim()));
								if (fieldNum == 1)
									fieldName = "CoursesRegistered";
								else if (fieldNum == 2)
									fieldName = "Status";
								else if (fieldNum == 3)
									fieldName = "StatusDate";
								else
									System.out.print("Wrong selection of input to edit record");

							} else {
								System.out.println("wrong record ID !..please try again with correct details!!");
								continue;
							}
							if (fieldName.equals("CoursesRegistered")) {
								System.out.println("Enter the number of courses registered by the student");
								coursesCount = Integer.parseInt(br.readLine().trim());
								String NewCourses = null;
								System.out.println("Enter the new courses registered by the student");
								for (int n = 0; n < coursesCount; n++) {
									String temp = br.readLine().trim();
									if (n == 0)
										NewCourses = temp;
									else
										NewCourses = NewCourses + "/" + temp;
								}
								System.out.println(serverloc.editRecord(managerID, recordID, fieldName, NewCourses));
							} else {
								System.out.println("Enter the value of the field to be updated");
								String newValue = null;

								if (fieldName.equals("Phone")) {
									while (true) {
										System.out.println("Enter the new Phone number in 123-456-7689 format");
										phoneNumber = br.readLine();
										Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
										Matcher matcher = pattern.matcher(phoneNumber);
										if (matcher.matches()) {
											newValue = phoneNumber;
											break;
										} else {
											System.out.println("Invalid new phone number ...exiting the program");
											logManager.logger.log(Level.INFO,
													"Validation Failed for new phone number and exiting the program(ManagerID:"
															+ managerID + ")");
											continue;
										}
									}
								}

								else if (fieldName.equals("Location")) {
									while (true) {
										System.out.println("Enter the new Location(MTL/LVL/DDO)");
										location = br.readLine().trim();
										if (location.equalsIgnoreCase("LVL") || location.equalsIgnoreCase("MTL")
												|| location.equalsIgnoreCase("DDO")) {
											newValue = location;
											break;
										} else {
											System.out.println("Invalid new Location ...exiting the program");
											logManager.logger.log(Level.INFO,
													"Validation Failed for new location and exiting the program(ManagerID:"
															+ managerID + ")");
											continue;
										}
									}

								} else if (fieldName.equals("Status")) {
									while (true) {
										System.out.println("Enter the status of student (Active/Inactive)");
										newValue = br.readLine().trim();
										status = newValue;
										if ((status.toUpperCase().equals("ACTIVE")))
											break;
										else if ((status.toUpperCase().equals("INACTIVE")))
											break;
										else {
											System.out.println("Status assigned Invalid!");
											status = "Invalid Status";
											continue;
										}
									}
								} else if (fieldName.equals("StatusDate")) {
									while (true) {
										System.out.println("Enter the new Date (Format :: 29-02-2018)");
										Pattern datePattern = Pattern.compile("([0-3][0-9])-([0-1][1-9])-([0-9]{4})");
										statusDate = br.readLine().trim();
										newValue = statusDate;
										Matcher matcherDate = datePattern.matcher(statusDate);
										if (matcherDate.matches())
											break;
										else {
											System.out.println("Invalid Date Format - enter in correct format");
											continue;
										}
									}
								} else {
									newValue = br.readLine().trim();

								}
								System.out.println(serverloc.editRecord(managerID, recordID, fieldName, newValue));
							}
							break;
						case 5:
							/*
							 * Getting input for transfer record operation
							 */
							System.out.println("Enter the record ID");
							recordID = br.readLine().trim();
							System.out.println("Enter the location(MTL/LVL/DDO)");
							location = br.readLine().trim();
							while (!location.equalsIgnoreCase("MTL") && !location.equalsIgnoreCase("LVL")
									&& !location.equalsIgnoreCase("DDO")) {
								System.out.println("Invalid loaction! Please try again");
								location = br.readLine().trim();
							}
							serverloc.transferRecord(managerID, recordID, location);
							break;
						case 6:
							while (true) {
								System.out.println("Enter the Location (MTL/LVL/DDO)");
								location = br.readLine();
								if (!location.equalsIgnoreCase("MTL") && !location.equalsIgnoreCase("LVL")
										&& !location.equalsIgnoreCase("DDO")) {
									continue;
								} else {
									System.out.println(serverloc.killServer(location));
									break;
								}

							}
							break;
						case 7:
							i = 0;
							break;
						default:
							System.out.println("Invalid choice! Please try again");
							break;
						}
					} catch (NumberFormatException e) {
						System.out.println("Invalid Input!!...Please enter values from 1-6 only...Try again!!");
						continue;
					} catch (Exception e) {
						System.out.println("Exception in Client :::: "+e.getMessage());
						System.out.println("Invalid Input!!.....Try again!!");
						continue;
					}

				}
				System.out.println("Manager with " + managerID + "is logged Out");
			}

			catch (StringIndexOutOfBoundsException ez) {
				System.out.println("Invalid ManagerID!!.....Try again!!");

			}
		}
	}
}