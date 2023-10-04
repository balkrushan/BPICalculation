package mars.JCI.Project.Fault_Page_Action;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;

import com.relevantcodes.extentreports.ExtentTest;

public class PvtExecution extends RDR_Fault_Widget_Page_Action{

	public PvtExecution(WebDriver driver, ExtentTest logger) {
		super(driver, logger);

	}


	static double multiplier=Math.pow(10,2);
	public static JSONParser parser;
	final static String PVTScanData="C:/MARS_FRAMEWORK/MARS_Automation_Framework_Projects/mars/JCI/Project/OBRDR/Configuration/PVT.json";
	private static final String String = null;
	public static FileReader file;

	/////////////////////////  Calculation of metasysVersion BPI score //////////////////////////

	public static void Metasys_server_version_is_upto_date() throws Exception {

		int penaltyScore = 0;
		int bpiScore = 0;
		int idealScore = 10;

		String selectedOperator = Select_metasysServerOperator();
		Integer selectedThreshold = Select_metasysThreshold();
		Integer selectedIdealScore = selctIdealScore();
		try {
			parser = new JSONParser();
			file = new FileReader(PVTScanData);
			Object obj = parser.parse(file);
			JSONArray jsonarray = (JSONArray) obj;

			for (Object obj1 : jsonarray) {

				JSONObject jsonObject1 = (JSONObject) obj1;
				String metasysVersion = (String) jsonObject1.get("metasysVersion");
				String[] version = metasysVersion.split("\\.");
				for (String v : version) {
					int Firstletter_MetaVersion = Integer.parseInt(v);
					if (selectedOperator.equals("<")) {
						if (Firstletter_MetaVersion < selectedThreshold) {
							bpiScore=selectedIdealScore  + bpiScore;
							penaltyScore=selectedIdealScore-bpiScore;

							System.out.println("Selected metasys Version is updated : " + metasysVersion + ", BPI Score of metays is :" + bpiScore );
							Excelsheet.ValueEntryInExcel(1,3,bpiScore);
							Excelsheet.ValueEntryInExcel(1,2,penaltyScore);

						} else {

							penaltyScore = selectedIdealScore  + penaltyScore;
							bpiScore = selectedIdealScore  - penaltyScore;
							System.out.println("Selected metasys Version is outdated: " + metasysVersion + ", BPI Score of metays is :" + bpiScore);
							Excelsheet.ValueEntryInExcel(1,3,bpiScore);
							Excelsheet.ValueEntryInExcel(1,2,penaltyScore);



						}
					}else {
						System.out.println("Selected operator is not equal to <");
					}


					break;
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/////////////////////////  Calculation of Supervisory - Offline Controller //////////////////////////

	public static void Supervisory_OfflineController() throws Exception {



		double SC_bpiScore = 0;
		int falseCount=0;
		Integer allocatedScore=Select_offlineControllerIdealScore();
		int total_SC = 0;






		try {
			parser = new JSONParser();
			file =new  FileReader(PVTScanData);
			Object obj=parser.parse(file);
			JSONArray jsonarray = (JSONArray) obj;

			// Iterate through the array elements
			for (Object obj1 : jsonarray) {

				JSONObject jsonObject1 = (JSONObject) obj1;
				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
				total_SC=SupervisoryDevices.size();
				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
				for(Object obj2 :SupervisoryDevices) {
					JSONObject jsonObject2 = (JSONObject)obj2;
					boolean supervisoryController=(boolean) jsonObject2.get("isOnline");
					if (supervisoryController==false) {
						falseCount++;
					} 
				}

			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}  
		double SC_penaltyScore=((double) allocatedScore/total_SC)*falseCount;
		SC_bpiScore=allocatedScore-SC_penaltyScore;

		System.out.println("BPI Score of Offline Supervisory Controller:" + SC_bpiScore);
		System.out.println("Penalty Score of Offline Supervisory Controller:" + SC_penaltyScore);
		Excelsheet.ValueEntryInExcel(2,2,SC_penaltyScore);
		Excelsheet.ValueEntryInExcel(2,3,SC_bpiScore);

	}

	//////////////////////////////////////Supervisory_FirmwareVersion//////////////////////////////////////////

	public static void Supervisory_FirmwareVersion() throws ClassCastException, Exception {

		double allocatedScore=Select_supervisoryFirmwareVersionIdealScore();
		int SC_fmV_threshold=Select_supervisoryThresholdValue();
		String operator=Select_supervisoryFirmwareVersionOperator();
		double SC_fmV_bpiScore = 0;
		int total_SC = 0;
		String NA_firwarVersion="NA";
		double penaltyScore=0;
		int SC_outdatedFirmwareVersionCount=0;
		int SC_firwareVersionUptoDate=0;


		try {
			JSONParser parser = new JSONParser();
			FileReader file =new  FileReader("C:/MARS_FRAMEWORK/MARS_Automation_Framework_Projects/mars/JCI/Project/OBRDR/Configuration/PVT.json");
			Object obj=parser.parse(file);
			JSONArray jsonarray = (JSONArray) obj;

			// Iterate through the array elements
			for (Object obj1 : jsonarray) {

				JSONObject jsonObject1 = (JSONObject) obj1;
				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
				total_SC=SupervisoryDevices.size();
				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
				for(Object obj2 :SupervisoryDevices) {
					JSONObject jsonObject2 = (JSONObject)obj2;
					String firmwareVersion=(String)jsonObject2.get("firmwareVersion");
					if(!firmwareVersion.equals(NA_firwarVersion)) {
						String[] SC_firmwareVersion=firmwareVersion.split("\\.");
						for(String v:SC_firmwareVersion) {
							int firstTwoDiditofFirmwareVersion=Integer.parseInt(v);
							if (operator.equals(">")) {
								if(firstTwoDiditofFirmwareVersion > SC_fmV_threshold) {
									SC_firwareVersionUptoDate++;
								}else {
									SC_outdatedFirmwareVersionCount++;

								}

								penaltyScore=((double)allocatedScore/total_SC)*SC_outdatedFirmwareVersionCount;
								SC_fmV_bpiScore= allocatedScore-penaltyScore;

								Excelsheet.ValueEntryInExcel(3,2,penaltyScore);
								Excelsheet.ValueEntryInExcel(3,3,SC_fmV_bpiScore);
								System.out.println("BPI Score of  Supervisory Controller firware version:" + SC_fmV_bpiScore);
								System.out.println("Penalty Score of  Supervisory Controller firware version:" + penaltyScore);

							}

							else {
								System.out.println("operator is not equal to > ");
							}
							if (operator.equals("<")) {
								if(firstTwoDiditofFirmwareVersion < SC_fmV_threshold) {
									SC_firwareVersionUptoDate++;
								}else {
									SC_outdatedFirmwareVersionCount++;

								}
								penaltyScore=((double)allocatedScore/total_SC)*SC_outdatedFirmwareVersionCount;
								SC_fmV_bpiScore= allocatedScore-penaltyScore;

								Excelsheet.ValueEntryInExcel(3,2,penaltyScore);
								Excelsheet.ValueEntryInExcel(3,3,SC_fmV_bpiScore);
								System.out.println("BPI Score of  Supervisory Controller firware version:" + SC_fmV_bpiScore);
								System.out.println("Penalty Score of  Supervisory Controller firware version:" + penaltyScore);

							}

							else {
								System.out.println("operator is not equal to < ");
							}
							if (operator.equals("=")) {
								if(firstTwoDiditofFirmwareVersion == SC_fmV_threshold) {
									SC_firwareVersionUptoDate++;
								}else {
									SC_outdatedFirmwareVersionCount++;

								}


								penaltyScore=((double)allocatedScore/total_SC)*SC_outdatedFirmwareVersionCount;
								SC_fmV_bpiScore= allocatedScore-penaltyScore;
								Excelsheet.ValueEntryInExcel(3,2,penaltyScore);
								Excelsheet.ValueEntryInExcel(3,3,SC_fmV_bpiScore);
								System.out.println("BPI Score of  Supervisory Controller firware version:" + SC_fmV_bpiScore);
								System.out.println("Penalty Score of  Supervisory Controller firware version:" + penaltyScore);

							}

							else {
								System.out.println("operator is not equal to =");
							}
							if (operator.equals(">=")) {
								if(firstTwoDiditofFirmwareVersion >= SC_fmV_threshold) {
									SC_firwareVersionUptoDate++;
								}else {
									SC_outdatedFirmwareVersionCount++;

								}
								penaltyScore=((double)allocatedScore/total_SC)*SC_outdatedFirmwareVersionCount;
								SC_fmV_bpiScore= allocatedScore-penaltyScore;

								Excelsheet.ValueEntryInExcel(3,2,penaltyScore);
								Excelsheet.ValueEntryInExcel(3,3,SC_fmV_bpiScore);
								System.out.println("BPI Score of  Supervisory Controller firware version:" + SC_fmV_bpiScore);
								System.out.println("Penalty Score of  Supervisory Controller firware version:" + penaltyScore);

							}

							else {
								System.out.println("operator is not equal to >=");
							}
							if (operator.equals("<=")) {
								if(firstTwoDiditofFirmwareVersion <= SC_fmV_threshold) {
									SC_firwareVersionUptoDate++;
								}else {
									SC_outdatedFirmwareVersionCount++;

								}
								penaltyScore=((double)allocatedScore/total_SC)*SC_outdatedFirmwareVersionCount;
								SC_fmV_bpiScore= allocatedScore-penaltyScore;

								Excelsheet.ValueEntryInExcel(3,2,penaltyScore);
								Excelsheet.ValueEntryInExcel(3,3,SC_fmV_bpiScore);
								System.out.println("BPI Score of  Supervisory Controller firware version:" + SC_fmV_bpiScore);
								System.out.println("Penalty Score of  Supervisory Controller firware version:" + penaltyScore);

							}

							else {
								System.out.println("operator is not equal to <=");
							}

							break;
						}

					}else {

						System.out.println(" Some firmware Version is NA");
					}

				}

			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		} 

	}



	/////////////////////////  Calculation of Supervisory - AvgMemoryUsage //////////////////////////

	public static void Supervisory_AvgMemoryUsage() throws ClassCastException, Exception {


		double allocatedScore=10;
		double SC_avgMemoryUsage_threshold=75;
		String operator=">";
		double SC_avgMemoryUsage_bpiScore = 0;
		int total_SC = 0;
		double penaltyScore=0;
		int SC_avgMemoryUsage=0;



		try {
			JSONParser parser = new JSONParser();
			FileReader file =new  FileReader("C:/MARS_FRAMEWORK/MARS_Automation_Framework_Projects/mars/JCI/Project/OBRDR/Configuration/PVT.json");
			Object obj=parser.parse(file);
			JSONArray jsonarray = (JSONArray) obj;

			// Iterate through the array elements
			for (Object obj1 : jsonarray) {

				JSONObject jsonObject1 = (JSONObject) obj1;
				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
				total_SC=SupervisoryDevices.size();
				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
				for(Object obj2 :SupervisoryDevices) {
					JSONObject jsonObject2 = (JSONObject)obj2;
					Double SC_memoryUsage=(Double)jsonObject2.get("memoryUsage");
					if(SC_memoryUsage != null) {

						double roundedValueMemoryUsage = Math.round(SC_memoryUsage * multiplier)/multiplier;
						if (operator.equals(">")) {
							if(roundedValueMemoryUsage > SC_avgMemoryUsage_threshold) {

							}else {
								SC_avgMemoryUsage++;

							}
							penaltyScore=(allocatedScore/total_SC)*SC_avgMemoryUsage;
							SC_avgMemoryUsage_bpiScore= allocatedScore-penaltyScore;
							System.out.println("BPI Score of  Supervisory Controller Avg memory usage:" + SC_avgMemoryUsage_bpiScore);
							System.out.println("Penalty Score of  Supervisory Controller Avg memory usage:" + penaltyScore);

						}

						else {
							System.out.println("operator is not equal to > ");
						}

						if (operator.equals("<")) {
							///if condition will satisfy then BPI score will increase either penalty score will increase.
							if(roundedValueMemoryUsage < SC_avgMemoryUsage_threshold) {

							}else {
								SC_avgMemoryUsage++;

							}
							penaltyScore=(allocatedScore/total_SC)*SC_avgMemoryUsage;
							SC_avgMemoryUsage_bpiScore= allocatedScore-penaltyScore;
							System.out.println("BPI Score of  Supervisory Controller Avg memory usage:" + SC_avgMemoryUsage_bpiScore);
							System.out.println("Penalty Score of  Supervisory Controller Avg memory usage:" + penaltyScore);
						}

						else {
							System.out.println("operator is not equal to < ");
						}



						if (operator.equals(">=")) {
							///if condition will satisfy then BPI score will increase either penalty score will increase.

							if(SC_memoryUsage >= SC_avgMemoryUsage_threshold) {

							}else {
								SC_avgMemoryUsage++;

							}
							penaltyScore=((double)allocatedScore/total_SC)*SC_avgMemoryUsage;
							SC_avgMemoryUsage_bpiScore= allocatedScore-penaltyScore;
							System.out.println("BPI Score of  Supervisory Controller Avg memory usage:" + SC_avgMemoryUsage_bpiScore);
							System.out.println("Penalty Score of  Supervisory Controller Avg memory usage:" + penaltyScore);

						}

						else {
							System.out.println("operator is not equal to >= ");
						}

						if (operator.equals("=")) {
							///if condition will satisfy then BPI score will increase either penalty score will increase.

							if(SC_memoryUsage == SC_avgMemoryUsage_threshold) {

							}else {
								SC_avgMemoryUsage++;

							}
							penaltyScore=((double)allocatedScore/total_SC)*SC_avgMemoryUsage;
							SC_avgMemoryUsage_bpiScore= allocatedScore-penaltyScore;
							System.out.println("BPI Score of  Supervisory Controller Avg memory usage:" + SC_avgMemoryUsage_bpiScore);
							System.out.println("Penalty Score of  Supervisory Controller Avg memory usage:" + penaltyScore);

						}

						else {
							System.out.println("operator is not equal to == ");
						}

						if (operator.equals("<=")) {
							///if condition will satisfy then BPI score will increase either penalty score will increase.

							if(SC_memoryUsage >= SC_avgMemoryUsage_threshold) {

							}else {
								SC_avgMemoryUsage++;

							}
							penaltyScore=((double)allocatedScore/total_SC)*SC_avgMemoryUsage;
							SC_avgMemoryUsage_bpiScore= allocatedScore-penaltyScore;
							System.out.println("BPI Score of  Supervisory Controller Avg memory usage:" + SC_avgMemoryUsage_bpiScore);
							System.out.println("Penalty Score of  Supervisory Controller Avg memory usage:" + penaltyScore);

						}

						else {
							System.out.println("operator is not equal to <= ");
						}




					}
					else {

						System.out.println("Avg Memory Usage is Null");
					}

				}

			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		} 



	}




	/////////////////////////  Calculation of Supervisory - battery Fault  //////////////////////////

	public static void Supervisory_BatteryFault() throws ClassCastException, Exception {




		double SC_bpiScore = 0;
		int batteryFaultTrueCount=0;
		Integer allocatedScore=9;
		int total_SC = 0;






		try {
			parser = new JSONParser();
			file =new  FileReader(PVTScanData);
			Object obj=parser.parse(file);
			JSONArray jsonarray = (JSONArray) obj;

			// Iterate through the array elements
			for (Object obj1 : jsonarray) {

				JSONObject jsonObject1 = (JSONObject) obj1;
				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
				total_SC=SupervisoryDevices.size();
				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
				for(Object obj2 :SupervisoryDevices) {
					JSONObject jsonObject2 = (JSONObject)obj2;
					boolean batteryFault=(boolean) jsonObject2.get("batteryFault");

					///////////If battery fault==true then battery will be faulty and if batteryFault==false then battery is healthy///
					if (batteryFault==true) {
						batteryFaultTrueCount++;
					} 
				}

			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}  
		double SC_penaltyScore=((double) allocatedScore/total_SC)*batteryFaultTrueCount;
		SC_bpiScore=allocatedScore-SC_penaltyScore;

		System.out.println("BPI Score of Battery Fault Supervisory Controller:" + SC_bpiScore);
		System.out.println("Penalty Score of Battery Fault Supervisory Controller:" + SC_penaltyScore);





	}





	/////////////////////////  Calculation of FieldController - Offline Field Controller  //////////////////////////

	public static void Offline_FieldController() throws ClassCastException, Exception {






		double FC_bpiScore = 0;
		int offlineFieldControllerCount=0;
		Integer allocatedScore=15;
		int total_FC = 0;
		int total_SC=0;
		double FC_penaltyScore=0;






		try {
			parser = new JSONParser();
			file =new  FileReader(PVTScanData);
			Object obj=parser.parse(file);
			JSONArray jsonarray = (JSONArray) obj;

			// Iterate through the array elements


			for (Object obj1 : jsonarray) {

				JSONObject jsonObject1 = (JSONObject) obj1;
				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
				total_SC=SupervisoryDevices.size();
				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
				for(Object obj2 :SupervisoryDevices) {
					JSONObject jsonObject2 = (JSONObject)obj2;
					JSONArray fieldBusesCount=(JSONArray)jsonObject2.get("fieldBuses");
					for(Object obj3 :fieldBusesCount) {
						JSONObject jsonObject3 = (JSONObject)obj3;
						JSONArray fieldControllersCount=(JSONArray)jsonObject3.get("fieldControllers");
						total_FC+=fieldControllersCount.size();
						for(Object obj4 :fieldControllersCount) {
							JSONObject jsonObject4 =(JSONObject)obj4;
							boolean isFieldControllerOnline=(boolean)jsonObject4.get("isOnline");
							if(isFieldControllerOnline==false) {
								offlineFieldControllerCount++;
							}
						}
					}



				}

			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}  

		System.out.println("OfflineFieldControllerCount :" + offlineFieldControllerCount);
		FC_penaltyScore=((double) allocatedScore/total_FC)*offlineFieldControllerCount;
		FC_bpiScore=allocatedScore-FC_penaltyScore;


		System.out.println("Total field Controllers in PVT site :" + total_FC);
		System.out.println("BPI Score of Offline Field Controller:" + Math.round(FC_bpiScore*multiplier)/multiplier);
		System.out.println("Penalty Score of Offline Field Controller:" + Math.round(FC_penaltyScore*multiplier)/multiplier);





	}


	/////////////////////////  Calculation of FieldController - FirmwareVersion  //////////////////////////

	public static void FirmwareVersion_FieldController() throws ClassCastException, Exception {



		double FC_fmV_bpiScore = 0;
		double allocatedScore=15;
		int total_FC = 0;
		int total_SC=0;
		double FC_penaltyScore=0;
		String NA_firwarVersion="NA";
		String operator=">";
		int FC_fmV_threshold=7;
		int FC_firwareVersionUptoDate=0;
		int FC_outdatedFirmwareVersionCount=0;
		int countOfNAFCFrimwareVersion=0;






		try {
			parser = new JSONParser();
			file =new  FileReader(PVTScanData);
			Object obj=parser.parse(file);
			JSONArray jsonarray = (JSONArray) obj;

			// Iterate through the array elements


			for (Object obj1 : jsonarray) {

				JSONObject jsonObject1 = (JSONObject) obj1;
				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
				total_SC=SupervisoryDevices.size();
				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
				for(Object obj2 :SupervisoryDevices) {
					JSONObject jsonObject2 = (JSONObject)obj2;
					JSONArray fieldBusesCount=(JSONArray)jsonObject2.get("fieldBuses");
					for(Object obj3 :fieldBusesCount) {
						JSONObject jsonObject3 = (JSONObject)obj3;
						JSONArray fieldControllersCount=(JSONArray)jsonObject3.get("fieldControllers");
						total_FC+=fieldControllersCount.size();
						for(Object obj4 :fieldControllersCount) {
							JSONObject jsonObject4 =(JSONObject)obj4;
							String firmwareVersion=(String)jsonObject4.get("firmwareVersion");
							if(!firmwareVersion.equals(NA_firwarVersion)) {
								String[] SC_firmwareVersion=firmwareVersion.split("\\.");
								for(String v:SC_firmwareVersion) {
									int firstTwoDiditofFirmwareVersion=Integer.parseInt(v);
									if (operator.equals(">")) {
										if(firstTwoDiditofFirmwareVersion >  FC_fmV_threshold) {
											FC_firwareVersionUptoDate++;
										}else 
										{
											FC_outdatedFirmwareVersionCount++;

										}


									}

									else {
										System.out.println("operator is not equal to > ");
									}

									if (operator.equals("<")) {
										if(firstTwoDiditofFirmwareVersion <  FC_fmV_threshold) {
											FC_firwareVersionUptoDate++;
										}else 
										{
											FC_outdatedFirmwareVersionCount++;

										}


									}

									else {
										System.out.println("operator is not equal to < ");
									}

									if (operator.equals("=")) {
										if(firstTwoDiditofFirmwareVersion ==  FC_fmV_threshold) {
											FC_firwareVersionUptoDate++;
										}else 
										{
											FC_outdatedFirmwareVersionCount++;

										}


									}

									else {
										System.out.println("operator is not equal to = ");
									}

									if (operator.equals(">=")) {
										if(firstTwoDiditofFirmwareVersion >=  FC_fmV_threshold) {
											FC_firwareVersionUptoDate++;
										}else 
										{
											FC_outdatedFirmwareVersionCount++;

										}


									}

									else {
										System.out.println("operator is not equal to >= ");
									}
									if (operator.equals("<=")) {
										if(firstTwoDiditofFirmwareVersion <=  FC_fmV_threshold) {
											FC_firwareVersionUptoDate++;
										}else 
										{
											FC_outdatedFirmwareVersionCount++;

										}


									}

									else {
										System.out.println("operator is not equal to <= ");
									}
									break;
								}
							} else {
								countOfNAFCFrimwareVersion++;
							}



						}

					}
				}
			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}  


		FC_penaltyScore=(allocatedScore/total_FC)*FC_outdatedFirmwareVersionCount;
		FC_fmV_bpiScore= allocatedScore-FC_penaltyScore;

		System.out.println("Count pf NA FC firmware Version" + countOfNAFCFrimwareVersion);

		System.out.println("Count pf Outdated FC firmware Version" + FC_outdatedFirmwareVersionCount);

		System.out.println("BPI Score of  Field Controller firware version:" + FC_fmV_bpiScore);
		System.out.println("Penalty Score of  Field Controller firware version:" + FC_penaltyScore);

		
		
		
		
		
		
	}


//////////////////////////////////////////////////////Sensors Alarm/////////////////////////////////////////////////////////
	
	public static void sensorsAlarm() throws ClassCastException, Exception {
		
		double FC_bpiScore = 0;
	//	int offlineFieldControllerCount=0;
		Integer allocatedScore=10;
	//	int total_FC = 0;
		int total_SC=0;
		double FC_penaltyScore=0;
		int countofSupervisoryPoints=0;
		int countofFieldControllerPoints=0;
        int countofPointsInAlarm=0;





		try {
			parser = new JSONParser();
			file =new  FileReader(PVTScanData);
			Object obj=parser.parse(file);
			JSONArray jsonarray = (JSONArray) obj;

			// Iterate through the array elements


			for (Object obj1 : jsonarray) {

				JSONObject jsonObject1 = (JSONObject) obj1;
				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
				total_SC=SupervisoryDevices.size();
				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
				for(Object obj2 :SupervisoryDevices) {
					JSONObject jsonObject2 = (JSONObject)obj2;
					JSONArray SupervisoryPoints=(JSONArray)jsonObject2.get("supervisoryPoints");
					 countofSupervisoryPoints+=SupervisoryPoints.size();
					 JSONArray pointsInAlarm =(JSONArray)jsonObject2.get("pointsInAlarm");
					 countofPointsInAlarm +=pointsInAlarm.size();
					JSONArray fieldBusesCount=(JSONArray)jsonObject2.get("fieldBuses");
					for(Object obj3 :fieldBusesCount) {
						JSONObject jsonObject3 = (JSONObject)obj3;
						JSONArray fieldControllersCount=(JSONArray)jsonObject3.get("fieldControllers");
				//		total_FC+=fieldControllersCount.size();
						for(Object obj4 :fieldControllersCount) {
							JSONObject jsonObject4 =(JSONObject)obj4;
							JSONArray FieldControllerPoints=(JSONArray)jsonObject4.get("fieldControllerPoints");
							countofFieldControllerPoints += FieldControllerPoints.size();
						}
					}



				}

			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}  
		int summationTotalpointsInSupervisoryAndFieldController= countofSupervisoryPoints+countofFieldControllerPoints;
		System.out.println("Sum of total points in supervisory and Field controller :" + summationTotalpointsInSupervisoryAndFieldController);
		System.out.println("point In Alarm :" + countofPointsInAlarm );

		
		FC_penaltyScore=((double) allocatedScore/summationTotalpointsInSupervisoryAndFieldController)*countofPointsInAlarm;
		FC_bpiScore=allocatedScore-FC_penaltyScore;

		System.out.println("BPI Score of Offline Field Controller:" + Math.round(FC_bpiScore*multiplier)/multiplier);
		System.out.println("Penalty Score of Offline Field Controller:" + Math.round(FC_penaltyScore*multiplier)/multiplier);
	}



//////////////////////////////////////////////////////Override Points/////////////////////////////////////////////////////////
	
	public static void isOverridePoints() throws ClassCastException, Exception {
		
		double FC_bpiScore = 0;
		Integer allocatedScore=10;
		double FC_penaltyScore=0;
		int countofSupervisoryPoints=0;
		int countofFieldControllerPoints=0;
        int countofPointsInAlarm=0;
        int countOfOverridePoint=0;





		try {
			parser = new JSONParser();
			file =new  FileReader(PVTScanData);
			Object obj=parser.parse(file);
			JSONArray jsonarray = (JSONArray) obj;

			// Iterate through the array elements


			for (Object obj1 : jsonarray) {

				JSONObject jsonObject1 = (JSONObject) obj1;
				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
				for(Object obj2 :SupervisoryDevices) {
					JSONObject jsonObject2 = (JSONObject)obj2;
					JSONArray SupervisoryPoints=(JSONArray)jsonObject2.get("supervisoryPoints");
					 countofSupervisoryPoints+=SupervisoryPoints.size();
					 for(Object points:SupervisoryPoints) {
						 JSONObject pointObject=(JSONObject)points;
						 boolean isoverride=(boolean)pointObject.get("isOverride");
						 if(isoverride==true) {
							 countOfOverridePoint++;
						 }
					 }
					 JSONArray pointsInAlarm =(JSONArray)jsonObject2.get("pointsInAlarm");
					 countofPointsInAlarm +=pointsInAlarm.size();
					JSONArray fieldBusesCount=(JSONArray)jsonObject2.get("fieldBuses");
					for(Object obj3 :fieldBusesCount) {
						JSONObject jsonObject3 = (JSONObject)obj3;
						JSONArray fieldControllersCount=(JSONArray)jsonObject3.get("fieldControllers");
				//		total_FC+=fieldControllersCount.size();
						for(Object obj4 :fieldControllersCount) {
							JSONObject jsonObject4 =(JSONObject)obj4;
							JSONArray FieldControllerPoints=(JSONArray)jsonObject4.get("fieldControllerPoints");
							countofFieldControllerPoints += FieldControllerPoints.size();
							 for(Object points:FieldControllerPoints) {
								 JSONObject pointObject=(JSONObject)points;
								 boolean isoverride=(boolean)pointObject.get("isOverride");
								 if(isoverride==true) {
									 countOfOverridePoint++;
								 }
							 }
						}
					}



				}

			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}  
		int summationTotalpointsInSupervisoryAndFieldController= countofSupervisoryPoints+countofFieldControllerPoints;
		System.out.println("Sum of total points in supervisory and Field controller :" + summationTotalpointsInSupervisoryAndFieldController);
		System.out.println("point In Alarm :" + countofPointsInAlarm );
		System.out.println("overridePoints :" + countOfOverridePoint );

		
		FC_penaltyScore=((double) allocatedScore/summationTotalpointsInSupervisoryAndFieldController)*countOfOverridePoint;
		FC_bpiScore=allocatedScore-FC_penaltyScore;

		System.out.println("BPI Score of Offline Field Controller:" + Math.round(FC_bpiScore*multiplier)/multiplier);
		System.out.println("Penalty Score of Offline Field Controller:" + Math.round(FC_penaltyScore*multiplier)/multiplier);
		
	}
	
	

	public static void main(String[] args) throws Exception, NumberFormatException,ClassCastException{
		
//		
//		
//		
//		double FC_bpiScore = 0;
//			Integer allocatedScore=10;
//			double FC_penaltyScore=0;
//			int countofSupervisoryPoints=0;
//			int countofFieldControllerPoints=0;
//	        int countofPointsInAlarm=0;
//	        int countOfOverridePoint=0;
//
//
//
//
//
//			try {
//				parser = new JSONParser();
//				file =new  FileReader(PVTScanData);
//				Object obj=parser.parse(file);
//				JSONArray jsonarray = (JSONArray) obj;
//
//				// Iterate through the array elements
//
//
//				for (Object obj1 : jsonarray) {
//
//					JSONObject jsonObject1 = (JSONObject) obj1;
//					JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
//					for(Object obj2 :SupervisoryDevices) {
//						JSONObject jsonObject2 = (JSONObject)obj2;
//						JSONArray SupervisoryPoints=(JSONArray)jsonObject2.get("supervisoryPoints");
//						 countofSupervisoryPoints+=SupervisoryPoints.size();
//						 for(Object points:SupervisoryPoints) {
//							 JSONObject pointObject=(JSONObject)points;
//							 boolean isoverride=(boolean)pointObject.get("isOverride");
//							 if(isoverride==true) {
//								 countOfOverridePoint++;
//							 }
//						 }
//						 JSONArray pointsInAlarm =(JSONArray)jsonObject2.get("pointsInAlarm");
//						 countofPointsInAlarm +=pointsInAlarm.size();
//						JSONArray fieldBusesCount=(JSONArray)jsonObject2.get("fieldBuses");
//						for(Object obj3 :fieldBusesCount) {
//							JSONObject jsonObject3 = (JSONObject)obj3;
//							JSONArray fieldControllersCount=(JSONArray)jsonObject3.get("fieldControllers");
//					//		total_FC+=fieldControllersCount.size();
//							for(Object obj4 :fieldControllersCount) {
//								JSONObject jsonObject4 =(JSONObject)obj4;
//								JSONArray FieldControllerPoints=(JSONArray)jsonObject4.get("fieldControllerPoints");
//								countofFieldControllerPoints += FieldControllerPoints.size();
//								 for(Object points:FieldControllerPoints) {
//									 JSONObject pointObject=(JSONObject)points;
//									 boolean isoverride=(boolean)pointObject.get("isOverride");
//									 if(isoverride==true) {
//										 countOfOverridePoint++;
//									 }
//								 }
//							}
//						}
//
//
//
//					}
//
//				}
//			}
//			catch (IOException | ParseException e) {
//				e.printStackTrace();
//			}  
//			int summationTotalpointsInSupervisoryAndFieldController= countofSupervisoryPoints+countofFieldControllerPoints;
//			System.out.println("Sum of total points in supervisory and Field controller :" + summationTotalpointsInSupervisoryAndFieldController);
//			System.out.println("point In Alarm :" + countofPointsInAlarm );
//			System.out.println("overridePoints :" + countOfOverridePoint );
//
//			
//			FC_penaltyScore=((double) allocatedScore/summationTotalpointsInSupervisoryAndFieldController)*countOfOverridePoint;
//			FC_bpiScore=allocatedScore-FC_penaltyScore;
//
//			System.out.println("BPI Score of Offline Field Controller:" + Math.round(FC_bpiScore*multiplier)/multiplier);
//			System.out.println("Penalty Score of Offline Field Controller:" + Math.round(FC_penaltyScore*multiplier)/multiplier);
//		
//		
//	}
		
		
	///////////////////////////////////// SensorsAlarm/////////////////////////////////////////	
		

	//	double FC_bpiScore = 0;
	//	int offlineFieldControllerCount=0;
	//	Integer allocatedScore=10;
	//	int total_SC=0;
	//	int total_FC = 0;
//		double FC_penaltyScore=0;
//		int countofSupervisoryPoints=0;
//		int countofFieldControllerPoints=0;
//        int countofPointsInAlarm=0;
//
//
//
//
//
//		try {
//			parser = new JSONParser();
//			file =new  FileReader(PVTScanData);
//			Object obj=parser.parse(file);
//			JSONArray jsonarray = (JSONArray) obj;
//
//			// Iterate through the array elements
//
//
//			for (Object obj1 : jsonarray) {
//
//				JSONObject jsonObject1 = (JSONObject) obj1;
//				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
//				total_SC=SupervisoryDevices.size();
//				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
//				for(Object obj2 :SupervisoryDevices) {
//					JSONObject jsonObject2 = (JSONObject)obj2;
//					JSONArray SupervisoryPoints=(JSONArray)jsonObject2.get("supervisoryPoints");
//					 countofSupervisoryPoints+=SupervisoryPoints.size();
//					 JSONArray pointsInAlarm =(JSONArray)jsonObject2.get("pointsInAlarm");
//					 countofPointsInAlarm +=pointsInAlarm.size();
//					JSONArray fieldBusesCount=(JSONArray)jsonObject2.get("fieldBuses");
//					for(Object obj3 :fieldBusesCount) {
//						JSONObject jsonObject3 = (JSONObject)obj3;
//						JSONArray fieldControllersCount=(JSONArray)jsonObject3.get("fieldControllers");
//					//	total_FC+=fieldControllersCount.size();
//						for(Object obj4 :fieldControllersCount) {
//							JSONObject jsonObject4 =(JSONObject)obj4;
//							JSONArray FieldControllerPoints=(JSONArray)jsonObject4.get("fieldControllerPoints");
//							countofFieldControllerPoints += FieldControllerPoints.size();
//						}
//					}
//
//
//
//				}
//
//			}
//		}
//		catch (IOException | ParseException e) {
//			e.printStackTrace();
//		}  
//		int summationTotalpointsInSupervisoryAndFieldController= countofSupervisoryPoints+countofFieldControllerPoints;
//		System.out.println("Sum of total points in supervisory and Field controller :" + summationTotalpointsInSupervisoryAndFieldController);
//		System.out.println("point In Alarm :" + countofPointsInAlarm );
//
//		
//		FC_penaltyScore=((double) allocatedScore/summationTotalpointsInSupervisoryAndFieldController)*countofPointsInAlarm;
//		FC_bpiScore=allocatedScore-FC_penaltyScore;
//
//		System.out.println("BPI Score of Offline Field Controller:" + Math.round(FC_bpiScore*multiplier)/multiplier);
//		System.out.println("Penalty Score of Offline Field Controller:" + Math.round(FC_penaltyScore*multiplier)/multiplier);
//
//}
		
		
		
		
		
		
		
		
		
		
		
		
		
		

/////////////////////////////////////////// Field Controller -- Firmware Version //////////////////////////////////////////////

		double FC_fmV_bpiScore = 0;
		double allocatedScore=15;
		int total_FC = 0;
		int total_SC=0;
		double FC_penaltyScore=0;
		String NA_firwarVersion="NA";
		String operator=">";
		int FC_fmV_threshold=7;
		int FC_firwareVersionUptoDate=0;
		int FC_outdatedFirmwareVersionCount=0;
		int countOfNAFCFrimwareVersion=0;






		try {
			parser = new JSONParser();
			file =new  FileReader(PVTScanData);
			Object obj=parser.parse(file);
			JSONArray jsonarray = (JSONArray) obj;

			// Iterate through the array elements


			for (Object obj1 : jsonarray) {

				JSONObject jsonObject1 = (JSONObject) obj1;
				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
				total_SC=SupervisoryDevices.size();
				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
				for(Object obj2 :SupervisoryDevices) {
					JSONObject jsonObject2 = (JSONObject)obj2;
					JSONArray fieldBusesCount=(JSONArray)jsonObject2.get("fieldBuses");
					for(Object obj3 :fieldBusesCount) {
						JSONObject jsonObject3 = (JSONObject)obj3;
						JSONArray fieldControllersCount=(JSONArray)jsonObject3.get("fieldControllers");
						total_FC+=fieldControllersCount.size();
						for(Object obj4 :fieldControllersCount) {
							JSONObject jsonObject4 =(JSONObject)obj4;
							String firmwareVersion=(String)jsonObject4.get("firmwareVersion");
							if(!firmwareVersion.equals(NA_firwarVersion)) {
								//double fmVersion=Double.parseDouble(firmwareVersion);
								String[] SC_firmwareVersion=firmwareVersion.split("\\.");
								for(String v:SC_firmwareVersion) {
									int firstTwoDiditofFirmwareVersion=Integer.parseInt(v);
									if (operator.equals(">")) {
										if(firstTwoDiditofFirmwareVersion >  FC_fmV_threshold) {
											FC_firwareVersionUptoDate++;
										}else 
										{
											FC_outdatedFirmwareVersionCount++;

										}


									}

									else {
										System.out.println("operator is not equal to > ");
									}

									if (operator.equals("<")) {
										if(firstTwoDiditofFirmwareVersion <  FC_fmV_threshold) {
											FC_firwareVersionUptoDate++;
										}else 
										{
											FC_outdatedFirmwareVersionCount++;

										}


									}

									else {
										System.out.println("operator is not equal to < ");
									}

									if (operator.equals("=")) {
										if(firstTwoDiditofFirmwareVersion ==  FC_fmV_threshold) {
											FC_firwareVersionUptoDate++;
										}else 
										{
											FC_outdatedFirmwareVersionCount++;

										}


									}

									else {
										System.out.println("operator is not equal to = ");
									}

									if (operator.equals(">=")) {
										if(firstTwoDiditofFirmwareVersion >=  FC_fmV_threshold) {
											FC_firwareVersionUptoDate++;
										}else 
										{
											FC_outdatedFirmwareVersionCount++;

										}


									}

									else {
										System.out.println("operator is not equal to >= ");
									}
									if (operator.equals("<=")) {
										if(firstTwoDiditofFirmwareVersion <=  FC_fmV_threshold) {
											FC_firwareVersionUptoDate++;
										}else 
										{
											FC_outdatedFirmwareVersionCount++;

										}


									}

									else {
										System.out.println("operator is not equal to <= ");
									}
									break;
								}
							} else {
								countOfNAFCFrimwareVersion++;
							}



						}

					}
				}
			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}  


		FC_penaltyScore=(allocatedScore/total_FC)*FC_outdatedFirmwareVersionCount;
		FC_fmV_bpiScore= allocatedScore-FC_penaltyScore;

		System.out.println("Count pf NA FC firmware Version" + countOfNAFCFrimwareVersion);

		System.out.println("Count pf Outdated FC firmware Version" + FC_outdatedFirmwareVersionCount);

		System.out.println("BPI Score of  Field Controller firware version:" + FC_fmV_bpiScore);
		System.out.println("Penalty Score of  Field Controller firware version:" + FC_penaltyScore);






	






	/////////////////////////////////// Offline Field Controller //////////////////////////////





	//		double FC_bpiScore = 0;
	//		int offlineFieldControllerCount=0;
	//		Integer allocatedScore=15;
	//		int total_FC = 0;
	//		int total_SC=0;
	//		double FC_penaltyScore=0;
	//
	//
	//
	//
	//
	//
	//		try {
	//			parser = new JSONParser();
	//			file =new  FileReader(PVTScanData);
	//			Object obj=parser.parse(file);
	//			JSONArray jsonarray = (JSONArray) obj;
	//
	//			// Iterate through the array elements
	//			for (Object obj1 : jsonarray) {
	//
	//				JSONObject jsonObject1 = (JSONObject) obj1;
	//				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
	//				total_SC=SupervisoryDevices.size();
	//				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
	// 				for(Object obj2 :SupervisoryDevices) {
	//					JSONObject jsonObject2 = (JSONObject)obj2;
	//					JSONArray fieldBusesCount=(JSONArray)jsonObject2.get("fieldBuses");
	//					            for(Object obj3 :fieldBusesCount) {
	//					            	JSONObject jsonObject3 = (JSONObject)obj3;
	//					            	JSONArray fieldControllersCount=(JSONArray)jsonObject3.get("fieldControllers");
	//					            	 total_FC+=fieldControllersCount.size();
	//					            	for(Object obj4 :fieldControllersCount) {
	//					            		   JSONObject jsonObject4 =(JSONObject)obj4;
	//					            		   boolean isFieldControllerOnline=(boolean)jsonObject4.get("isOnline");
	//					            		   if(isFieldControllerOnline==false) {
	//					            			   offlineFieldControllerCount++;
	//					            		   }
	//					            	}
	//					            }
	//					
	//					
	//					
	//				}
	//
	//			}
	//		}
	//		catch (IOException | ParseException e) {
	//			e.printStackTrace();
	//		}  
	//		
	//		System.out.println("OfflineFieldControllerCount :" + offlineFieldControllerCount);
	//		FC_penaltyScore=((double) allocatedScore/total_FC)*offlineFieldControllerCount;
	//		FC_bpiScore=allocatedScore-FC_penaltyScore;
	//		
	//		
	//         System.out.println("Total field Controllers in PVT site :" + total_FC);
	//		System.out.println("BPI Score of Offline Field Controller:" + Math.round(FC_bpiScore*multiplier)/multiplier);
	//		System.out.println("Penalty Score of Offline Field Controller:" + Math.round(FC_penaltyScore*multiplier)/multiplier);
	//		


	////////////////////////////////////  SC__Battery Fault	/////////////////////////////////////


	//		double SC_bpiScore = 0;
	//		int batteryFaultTrueCount=0;
	//		Integer allocatedScore=9;
	//		int total_SC = 0;
	//
	//
	//
	//
	//
	//
	//		try {
	//			parser = new JSONParser();
	//			file =new  FileReader(PVTScanData);
	//			Object obj=parser.parse(file);
	//			JSONArray jsonarray = (JSONArray) obj;
	//
	//			// Iterate through the array elements
	//			for (Object obj1 : jsonarray) {
	//
	//				JSONObject jsonObject1 = (JSONObject) obj1;
	//				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
	//				total_SC=SupervisoryDevices.size();
	//				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
	//				for(Object obj2 :SupervisoryDevices) {
	//					JSONObject jsonObject2 = (JSONObject)obj2;
	//					boolean batteryFault=(boolean) jsonObject2.get("batteryFault");
	//					
	//					///////////If battery fault==true then battery will be faulty and if batteryFault==false then battery is healthy///
	//					if (batteryFault==true) {
	//						batteryFaultTrueCount++;
	//					} 
	//				}
	//
	//			}
	//		}
	//		catch (IOException | ParseException e) {
	//			e.printStackTrace();
	//		}  
	//		double SC_penaltyScore=((double) allocatedScore/total_SC)*batteryFaultTrueCount;
	//		SC_bpiScore=allocatedScore-SC_penaltyScore;
	//
	//		System.out.println("BPI Score of Battery Fault Supervisory Controller:" + SC_bpiScore);
	//		System.out.println("Penalty Score of Battery Fault Supervisory Controller:" + SC_penaltyScore);
	//		





	///////////////////////////// SC_AvgMemoryUsage/////////////////////////////////////





	//		double allocatedScore=10;
	//		double SC_avgMemoryUsage_threshold=75;
	//		String operator=">";
	//		double SC_avgMemoryUsage_bpiScore = 0;
	//		int total_SC = 0;
	//		double penaltyScore=0;
	//		int SC_avgMemoryUsage=0;
	//		 
	//
	//
	//		try {
	//			JSONParser parser = new JSONParser();
	//			FileReader file =new  FileReader("C:/MARS_FRAMEWORK/MARS_Automation_Framework_Projects/mars/JCI/Project/OBRDR/Configuration/PVT.json");
	//			Object obj=parser.parse(file);
	//			JSONArray jsonarray = (JSONArray) obj;
	//
	//			// Iterate through the array elements
	//			for (Object obj1 : jsonarray) {
	//
	//				JSONObject jsonObject1 = (JSONObject) obj1;
	//				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
	//				total_SC=SupervisoryDevices.size();
	//				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
	//				for(Object obj2 :SupervisoryDevices) {
	//					JSONObject jsonObject2 = (JSONObject)obj2;
	//					Double SC_memoryUsage=(Double)jsonObject2.get("memoryUsage");
	//					if(SC_memoryUsage != null) {
	//						double multiplier=Math.pow(10,2);
	//						double roundedValueMemoryUsage = Math.round(SC_memoryUsage * multiplier)/multiplier;
	//							if (operator.equals(">")) {
	//								if(roundedValueMemoryUsage > SC_avgMemoryUsage_threshold) {
	//									
	//								}else {
	//									SC_avgMemoryUsage++;
	//
	//								}
	//								penaltyScore=(allocatedScore/total_SC)*SC_avgMemoryUsage;
	//								SC_avgMemoryUsage_bpiScore= allocatedScore-penaltyScore;
	//								System.out.println("BPI Score of  Supervisory Controller Avg memory usage:" + SC_avgMemoryUsage_bpiScore);
	//								System.out.println("Penalty Score of  Supervisory Controller Avg memory usage:" + penaltyScore);
	//
	//							}
	//
	//							else {
	//								System.out.println("operator is not equal to > ");
	//							}
	//							
	//							if (operator.equals("<")) {
	//								///if condition will satisfy then BPI score will increase either penalty score will increase.
	//								if(roundedValueMemoryUsage < SC_avgMemoryUsage_threshold) {
	//									
	//								}else {
	//									SC_avgMemoryUsage++;
	//
	//								}
	//								penaltyScore=(allocatedScore/total_SC)*SC_avgMemoryUsage;
	//								SC_avgMemoryUsage_bpiScore= allocatedScore-penaltyScore;
	//								System.out.println("BPI Score of  Supervisory Controller Avg memory usage:" + SC_avgMemoryUsage_bpiScore);
	//								System.out.println("Penalty Score of  Supervisory Controller Avg memory usage:" + penaltyScore);
	//							}
	//
	//							else {
	//								System.out.println("operator is not equal to < ");
	//							}
	//							
	//							
	//							
	//							if (operator.equals(">=")) {
	//								///if condition will satisfy then BPI score will increase either penalty score will increase.
	//								
	//								if(SC_memoryUsage >= SC_avgMemoryUsage_threshold) {
	//									
	//								}else {
	//									SC_avgMemoryUsage++;
	//
	//								}
	//								penaltyScore=((double)allocatedScore/total_SC)*SC_avgMemoryUsage;
	//								SC_avgMemoryUsage_bpiScore= allocatedScore-penaltyScore;
	//								System.out.println("BPI Score of  Supervisory Controller Avg memory usage:" + SC_avgMemoryUsage_bpiScore);
	//								System.out.println("Penalty Score of  Supervisory Controller Avg memory usage:" + penaltyScore);
	//
	//							}
	//
	//							else {
	//								System.out.println("operator is not equal to >= ");
	//							}
	//							
	//							if (operator.equals("=")) {
	//								///if condition will satisfy then BPI score will increase either penalty score will increase.
	//								
	//								if(SC_memoryUsage == SC_avgMemoryUsage_threshold) {
	//									
	//								}else {
	//									SC_avgMemoryUsage++;
	//
	//								}
	//								penaltyScore=((double)allocatedScore/total_SC)*SC_avgMemoryUsage;
	//								SC_avgMemoryUsage_bpiScore= allocatedScore-penaltyScore;
	//								System.out.println("BPI Score of  Supervisory Controller Avg memory usage:" + SC_avgMemoryUsage_bpiScore);
	//								System.out.println("Penalty Score of  Supervisory Controller Avg memory usage:" + penaltyScore);
	//
	//							}
	//
	//							else {
	//								System.out.println("operator is not equal to == ");
	//							}
	//							
	//							if (operator.equals("<=")) {
	//								///if condition will satisfy then BPI score will increase either penalty score will increase.
	//								
	//								if(SC_memoryUsage >= SC_avgMemoryUsage_threshold) {
	//									
	//								}else {
	//									SC_avgMemoryUsage++;
	//
	//								}
	//								penaltyScore=((double)allocatedScore/total_SC)*SC_avgMemoryUsage;
	//								SC_avgMemoryUsage_bpiScore= allocatedScore-penaltyScore;
	//								System.out.println("BPI Score of  Supervisory Controller Avg memory usage:" + SC_avgMemoryUsage_bpiScore);
	//								System.out.println("Penalty Score of  Supervisory Controller Avg memory usage:" + penaltyScore);
	//
	//							}
	//
	//							else {
	//								System.out.println("operator is not equal to <= ");
	//							}
	//							
	//							
	//
	//							
	//						}
	//					else {
	//
	//						System.out.println("Avg Memory Usage is Null");
	//					}
	//
	//				}
	//
	//			}
	//		}
	//		catch (IOException | ParseException e) {
	//			e.printStackTrace();
	//		} 
	//		
	//		




	/////////////////////////////Supervisory_FirmwareVersion//////////////////////////////////

	//		double allocatedScore=Select_supervisoryFirmwareVersionIdealScore();
	//		int SC_fmV_threshold=Select_supervisoryThresholdValue();
	//		String operator=Select_supervisoryFirmwareVersionOperator();
	//		double SC_fmV_bpiScore = 0;
	//		int total_SC = 0;
	//		String NA_firwarVersion="NA";
	//		double penaltyScore=0;
	//		int SC_outdatedFirmwareVersionCount=0;
	//		int SC_firwareVersionUptoDate=0;
	//
	//
	//		try {
	//			JSONParser parser = new JSONParser();
	//			FileReader file =new  FileReader("C:/MARS_FRAMEWORK/MARS_Automation_Framework_Projects/mars/JCI/Project/OBRDR/Configuration/PVT.json");
	//			Object obj=parser.parse(file);
	//			JSONArray jsonarray = (JSONArray) obj;
	//
	//			// Iterate through the array elements
	//			for (Object obj1 : jsonarray) {
	//
	//				JSONObject jsonObject1 = (JSONObject) obj1;
	//				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
	//				total_SC=SupervisoryDevices.size();
	//				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
	//				for(Object obj2 :SupervisoryDevices) {
	//					JSONObject jsonObject2 = (JSONObject)obj2;
	//					String firmwareVersion=(String)jsonObject2.get("firmwareVersion");
	//					if(!firmwareVersion.equals(NA_firwarVersion)) {
	//						String[] SC_firmwareVersion=firmwareVersion.split("\\.");
	//						for(String v:SC_firmwareVersion) {
	//							int firstTwoDiditofFirmwareVersion=Integer.parseInt(v);
	//							if (operator.equals(">")) {
	//								if(firstTwoDiditofFirmwareVersion > SC_fmV_threshold) {
	//									SC_firwareVersionUptoDate++;
	//								}else {
	//									SC_outdatedFirmwareVersionCount++;
	//
	//								}
	//								penaltyScore=((double)allocatedScore/total_SC)*SC_outdatedFirmwareVersionCount;
	//    							SC_fmV_bpiScore= allocatedScore-penaltyScore;
	//								System.out.println("BPI Score of  Supervisory Controller firware version:" + SC_fmV_bpiScore);
	//								System.out.println("Penalty Score of  Supervisory Controller firware version:" + penaltyScore);
	//
	//							}
	//
	//							else {
	//								System.out.println("operator is not equal to > ");
	//							}
	//							if (operator.equals("<")) {
	//								if(firstTwoDiditofFirmwareVersion < SC_fmV_threshold) {
	//									SC_firwareVersionUptoDate++;
	//								}else {
	//									SC_outdatedFirmwareVersionCount++;
	//
	//								}
	//								penaltyScore=((double)allocatedScore/total_SC)*SC_outdatedFirmwareVersionCount;
	//    							SC_fmV_bpiScore= allocatedScore-penaltyScore;
	//								System.out.println("BPI Score of  Supervisory Controller firware version:" + SC_fmV_bpiScore);
	//								System.out.println("Penalty Score of  Supervisory Controller firware version:" + penaltyScore);
	//
	//							}
	//
	//							else {
	//								System.out.println("operator is not equal to < ");
	//							}
	//							if (operator.equals("=")) {
	//								if(firstTwoDiditofFirmwareVersion == SC_fmV_threshold) {
	//									SC_firwareVersionUptoDate++;
	//								}else {
	//									SC_outdatedFirmwareVersionCount++;
	//
	//								}
	//								penaltyScore=((double)allocatedScore/total_SC)*SC_outdatedFirmwareVersionCount;
	//    							SC_fmV_bpiScore= allocatedScore-penaltyScore;
	//								System.out.println("BPI Score of  Supervisory Controller firware version:" + SC_fmV_bpiScore);
	//								System.out.println("Penalty Score of  Supervisory Controller firware version:" + penaltyScore);
	//
	//							}
	//
	//							else {
	//								System.out.println("operator is not equal to =");
	//							}
	//							if (operator.equals(">=")) {
	//								if(firstTwoDiditofFirmwareVersion >= SC_fmV_threshold) {
	//									SC_firwareVersionUptoDate++;
	//								}else {
	//									SC_outdatedFirmwareVersionCount++;
	//
	//								}
	//								penaltyScore=((double)allocatedScore/total_SC)*SC_outdatedFirmwareVersionCount;
	//    							SC_fmV_bpiScore= allocatedScore-penaltyScore;
	//								System.out.println("BPI Score of  Supervisory Controller firware version:" + SC_fmV_bpiScore);
	//								System.out.println("Penalty Score of  Supervisory Controller firware version:" + penaltyScore);
	//
	//							}
	//
	//							else {
	//								System.out.println("operator is not equal to >=");
	//							}
	//							if (operator.equals("<=")) {
	//								if(firstTwoDiditofFirmwareVersion <= SC_fmV_threshold) {
	//									SC_firwareVersionUptoDate++;
	//								}else {
	//									SC_outdatedFirmwareVersionCount++;
	//
	//								}
	//								penaltyScore=((double)allocatedScore/total_SC)*SC_outdatedFirmwareVersionCount;
	//    							SC_fmV_bpiScore= allocatedScore-penaltyScore;
	//								System.out.println("BPI Score of  Supervisory Controller firware version:" + SC_fmV_bpiScore);
	//								System.out.println("Penalty Score of  Supervisory Controller firware version:" + penaltyScore);
	//
	//							}
	//
	//							else {
	//								System.out.println("operator is not equal to <=");
	//							}
	//
	//							break;
	//						}
	//
	//					}else {
	//
	//						System.out.println(" Some firmware Version is NA");
	//					}
	//
	//				}
	//
	//			}
	//		}
	//		catch (IOException | ParseException e) {
	//			e.printStackTrace();
	//		} 
	//
	//		





	////////////////////Supr_OfflineController////////////////
	//		double SC_bpiScore = 0;
	//		int falseCount=0;
	//		double allocatedScore=15;
	//		int total_SC = 0;
	//
	//
	//
	//
	//
	//
	//		try {
	//			JSONParser parser = new JSONParser();
	//			FileReader file =new  FileReader("C:/MARS_FRAMEWORK/MARS_Automation_Framework_Projects/mars/JCI/Project/OBRDR/Configuration/PVT.json");
	//			Object obj=parser.parse(file);
	//			JSONArray jsonarray = (JSONArray) obj;
	//
	//			// Iterate through the array elements
	//			for (Object obj1 : jsonarray) {
	//
	//				JSONObject jsonObject1 = (JSONObject) obj1;
	//				JSONArray SupervisoryDevices=(JSONArray) jsonObject1.get("supervisoryDevices");
	//				total_SC=SupervisoryDevices.size();
	//				System.out.println("Total Supervisory Controllers in PVT site : " + total_SC);
	//				for(Object obj2 :SupervisoryDevices) {
	//					JSONObject jsonObject2 = (JSONObject)obj2;
	//					boolean supervisoryController=(boolean) jsonObject2.get("isOnline");
	//					if (supervisoryController==false) {
	//						falseCount++;
	//					} 
	//				}
	//				 
	//			}
	//		}
	//		catch (IOException | ParseException e) {
	//			e.printStackTrace();
	//		}  
	//		double SC_penaltyScore=(allocatedScore/total_SC)*falseCount;
	//		SC_bpiScore=allocatedScore-SC_penaltyScore;
	//
	//		System.out.println("BPI Score of Offline Supervisory Controller:" + SC_bpiScore);
	//		System.out.println("Penalty Score of Offline Supervisory Controller:" + SC_penaltyScore);
	//		 //Excelsheet.ValueEntryInExcel(2,8,total_SC);
	//		// Excelsheet.ValueEntryInExcel(2,7,SC_bpiScore);
	//



	}

}






