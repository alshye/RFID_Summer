
/**
 * Copyright 2006 Alien Technology Corporation. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1)	Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p>
 * 2)	Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
 * 3)	Neither the name of Alien Technology Corporation nor the names of any
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ALIEN TECHNOLOGY CORPORATION OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * For further information, contact :
 * <p>
 * Alien Technology
 * 18220 Butterfield Blvd.
 * Morgan Hill, CA 95037
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alien.enterpriseRFID.reader.*;
import com.alien.enterpriseRFID.tags.*;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

/**
 * Connects to a Reader on COM port #1 and asks it to read tags.
 *
 * @version 1.2 Feb 2004
 * @author David Krull
 */


public class AlienClass1ReaderTest {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;

	private static final int sizeOfIntInHalfBytes = 8;							// To convert Decimal to Hexadecimal
	private static final int numberOfBitsInAHalfByte = 4;
	private static final int halfByte = 0x0F;
	private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	/**
	 * Constructor
	 */
	public AlienClass1ReaderTest() throws AlienReaderException {

		// AlienClass1Reader reader = new AlienClass1Reader();

		AlienClassBPTReader reader = new AlienClassBPTReader();

		reader.setConnection("COM3");							
		
		// To connect to a networked reader instead, use the following:
		/*
		 * reader.setConnection("10.1.60.107", 23); reader.setUsername("alien");
		 * reader.setPassword("password");
		 */

		// Open a connection to the reader
		reader.open();

		reader.setTagMask("");						// Initialize Mask

		// Connect to the Database (MySQL)
		SQLConnection();

		// Define the custom format
		String customFormatStr = "ID=%i; RSSI=${RSSI}, XPC=${XPC}";
		reader.setTagStreamFormat(reader.CUSTOM_FORMAT);
		reader.setTagListCustomFormat(customFormatStr);
		TagUtil.setCustomFormatString(customFormatStr);
		reader.setTagListFormat(reader.CUSTOM_FORMAT);
		System.out.println(reader.getTagStreamFormat());
		reader.setRFAttenuation(0);

		// Ask the reader to read tags and print them
		Tag tagList[] = reader.getCustomTagList();
		HashMap<String, Double> epc_rssi = new HashMap<String, Double>();

		// Get a RFAttenuation
		int transmission = (int) (30 - 0.1 * reader.getRFAttenuation());

		if (tagList == null) {
			System.out.println("No Tags Found");
			reader.close();
		} else {
			System.out.println("Tag found:");

			for (int i = 0; i < tagList.length; i++) {
				Tag tag = tagList[i];
				epc_rssi.put(tag.getTagID(), tag.getRSSI());			
			}

			reader.close();
			reader.open();

			Iterator it = epc_rssi.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();

				reader.setG2TagMask(pair.getKey().toString());			// Set a Mask 

				byte[] userdata = reader.g2Read(3, 0, 8);				// Read USER Bank
				String user = "";
				for (int i = 0; i < userdata.length; i++) {
					String u = "";
					u += userdata[i];
					user += decToHex(Integer.parseInt(u));
				}

				byte[] tiddata = reader.g2Read(2, 0, 12);				// Read TID Bank
				String tid = "";
				for (int i = 0; i < tiddata.length; i++) {
					String t = "";
					t += tiddata[i];
					tid += decToHex(Integer.parseInt(t));
				}

				System.out.println("EPC:" + pair.getKey().toString().replace(" ", "") + ", USER: " + user + ", TID: "
						+ tid.substring(0, 24) + ", Transmission Power:  " + transmission + ", RSSI: "
						+ pair.getValue());
				
				SQLInsertion(pair.getKey().toString(), user, tid.substring(0, 24), transmission,
						Double.parseDouble(pair.getValue().toString()), getTemperature(),
						CheckEPC(pair.getKey().toString()), CheckUSER(pair.getKey().toString(), user), CheckTID(pair.getKey().toString(), tid.substring(0, 24)));
			}
		}

		reader.close();
	}

	/**
	 * @author Min Hye Jun
	 *
	 * Convert decimal to Hexadecimal
	 */
	public static String decToHex(int dec) {

		StringBuilder hexBuilder = new StringBuilder(sizeOfIntInHalfBytes);
		hexBuilder.setLength(sizeOfIntInHalfBytes);
		for (int i = sizeOfIntInHalfBytes - 1; i >= 0; --i) {
			int j = dec & halfByte;
			hexBuilder.setCharAt(i, hexDigits[j]);
			dec >>= numberOfBitsInAHalfByte;
		}
		return hexBuilder.substring(6, 8);
	}
	
	/**
	 * @author Min Hye Jun
	 *
	 * Check if EPC value is correct
	 * by comparing EPC value read from RFID with Tag EPC Information 
	 */
	public int CheckEPC(String epc) {
		int success = 0;

		ArrayList<String> tagList = new ArrayList<>();

		tagList.add("E201 7052 4F10 2725 5272 01F4 2507 E001");
		tagList.add("E201 7052 4F10 2725 5272 01F4 2507 E002");
		tagList.add("E201 7052 4F10 2725 5272 01F4 2507 E003");
		tagList.add("E201 7052 4F10 2725 5272 01F4 2507 E004");
		tagList.add("E201 7052 4F10 2725 5272 01F4 2507 E005");
		tagList.add("E201 7052 4F10 2725 5272 01F4 2507 E006");
		tagList.add("E201 7052 4F10 2725 5272 01F4 2507 E007");
		tagList.add("E201 7052 4F10 2725 5272 01F4 2507 E008");
		tagList.add("E201 7052 4F10 2725 5272 01F4 2507 E009");
		tagList.add("E201 7052 4F10 2725 5272 01F4 2507 E010");

		for (int i = 0; i < tagList.size(); i++)
			if (epc.equals(tagList.get(i))) {
				success = 1;
				break;
			}

		return success;
	}

	/**
	 * @author Min Hye Jun
	 *
	 * Check if USER value is correct
	 * by comparing USER value read from RFID with Tag USER Information based on EPC 
	 */
	public int CheckUSER(String epc, String user) {
		int success = 0;

		HashMap<String, String> epc_user = new HashMap<String, String>();

		epc_user.put("E201 7052 4F10 2725 5272 01F4 2507 E001", "D20170524F102725527201F42507D001");
		epc_user.put("E201 7052 4F10 2725 5272 01F4 2507 E002", "D20170524F102725527201F42507D002");
		epc_user.put("E201 7052 4F10 2725 5272 01F4 2507 E003", "D20170524F102725527201F42507D003");
		epc_user.put("E201 7052 4F10 2725 5272 01F4 2507 E004", "D20170524F102725527201F42507D004");
		epc_user.put("E201 7052 4F10 2725 5272 01F4 2507 E005", "D20170524F102725527201F42507D005");
		epc_user.put("E201 7052 4F10 2725 5272 01F4 2507 E006", "D20170524F102725527201F42507D006");
		epc_user.put("E201 7052 4F10 2725 5272 01F4 2507 E007", "D20170524F102725527201F42507D007");
		epc_user.put("E201 7052 4F10 2725 5272 01F4 2507 E008", "D20170524F102725527201F42507D008");
		epc_user.put("E201 7052 4F10 2725 5272 01F4 2507 E009", "D20170524F102725527201F42507D009");
		epc_user.put("E201 7052 4F10 2725 5272 01F4 2507 E010", "D20170524F102725527201F42507D010");

		Iterator it = epc_user.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			if(epc.equals(pair.getKey()))
				if(user.equals(pair.getValue())) {
					success = 1;
					break;
				}
		}
	
		return success;
	}

	/**
	 * @author Min Hye Jun
	 *
	 * Check if TID value is correct
	 * by comparing TID value read from RFID with Tag TID Information based on EPC 
	 */
	public int CheckTID(String epc, String tid) {
		int success = 0;

		HashMap<String, String> epc_tid = new HashMap<String, String>();

		epc_tid.put("E201 7052 4F10 2725 5272 01F4 2507 E001", "E20034140121010165380E58");
		epc_tid.put("E201 7052 4F10 2725 5272 01F4 2507 E002", "E20034140122010165380E54");
		epc_tid.put("E201 7052 4F10 2725 5272 01F4 2507 E003", "E2003414011C010165382060");
		epc_tid.put("E201 7052 4F10 2725 5272 01F4 2507 E004", "E2003414011A010165381DCC");
		epc_tid.put("E201 7052 4F10 2725 5272 01F4 2507 E005", "E2003414011C01016537ECE3");
		epc_tid.put("E201 7052 4F10 2725 5272 01F4 2507 E006", "E2003414011F01016537E7BF");
		epc_tid.put("E201 7052 4F10 2725 5272 01F4 2507 E007", "E2003414012201016537F720");
		epc_tid.put("E201 7052 4F10 2725 5272 01F4 2507 E008", "E2003414011B01016537DD82");
		epc_tid.put("E201 7052 4F10 2725 5272 01F4 2507 E009", "E2003414012301016537FED0");
		epc_tid.put("E201 7052 4F10 2725 5272 01F4 2507 E010", "E20034140122010165381DD0");

		Iterator it = epc_tid.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			if(epc.equals(pair.getKey()))
				if(tid.equals(pair.getValue())) {
					success = 1;
					break;
				}
		}

		return success;
	}
	
	/**
	 * @author Min Hye Jun
	 *
	 * Connect to MySQL
	 */
	public void SQLConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String connectionUrl = "jdbc:mysql://localhost:3306/RFID";
			String connectionUser = "minhye";
			String connectionPassword = "1234";
			conn = (Connection) DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author Min Hye Jun
	 *
	 * Insert Tag data into MySQL 
	 */
	public void SQLInsertion(String epc, String user, String tid, int transmission, double RSSI, String temperature,
			int epc_suc, int user_suc, int tid_suc) {
		epc = epc.replaceAll("\\s", "");

		try {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO TagInfo_9650(EPC, USER, TID, Transmission, RSSI, Temperature, DATE, EPC_SUC, USER_SUC, TID_SUC) VALUES ('"
							+ epc + "', '" + user + "', '" + tid + "', " + transmission + ", '" + RSSI + "', '"
							+ temperature + "', sysdate(), " + epc_suc + ", " + user_suc + ", " + tid_suc + ")");
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @author Min Hye Jun
	 *
	 * Get temperature in a file
	 * Arduino measures temperature and writes it in a file
	 */
	public String getTemperature() {
		File file = new File("C:\\Users\\Min Hye\\Documents\\Processing\\Temperature\\temperature.txt");		
		BufferedReader reader = null;
		String temperature = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			temperature = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		return temperature;
	}

	/**
	 * Main
	 */
	public static final void main(String args[]) {
		while (true) {
			try {
				new AlienClass1ReaderTest();
				Thread.sleep(18500);
			} catch (AlienReaderException | InterruptedException e) {
				System.out.println("Error: " + e.toString());
			}
		}
	}

} // End of class AlienClass1ReaderTest