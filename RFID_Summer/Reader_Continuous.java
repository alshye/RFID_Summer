package com.alien.inventory;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

public class Reader_Continuous {

	/**
	 * Constructor
	 */
	
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	int totalCount = 0;
	int time = 20; // Interval Time

	HashMap<String, Integer> tag_count = new HashMap<>();

	public Reader_Continuous() throws AlienReaderException {

		AlienClass1Reader reader = new AlienClass1Reader();

		tag_count.put("E201 7052 4F10 2725 5272 01F4 2507 E001", 0);
		tag_count.put("E201 7052 4F10 2725 5272 01F4 2507 E002", 0);
		tag_count.put("E201 7052 4F10 2725 5272 01F4 2507 E003", 0);
		tag_count.put("E201 7052 4F10 2725 5272 01F4 2507 E004", 0);
		tag_count.put("E201 7052 4F10 2725 5272 01F4 2507 E005", 0);
		tag_count.put("E201 7052 4F10 2725 5272 01F4 2507 E006", 0);
		tag_count.put("E201 7052 4F10 2725 5272 01F4 2507 E007", 0);
		tag_count.put("E201 7052 4F10 2725 5272 01F4 2507 E008", 0);
		tag_count.put("E201 7052 4F10 2725 5272 01F4 2507 E009", 0);
		tag_count.put("E201 7052 4F10 2725 5272 01F4 2507 E010", 0);

		reader.setConnection("COM3");

		// To connect to a networked reader instead, use the following:
		/*
		 * reader.setConnection("10.1.60.107", 23); reader.setUsername("alien");
		 * reader.setPassword("password");
		 */

		// Open a connection to the reader
		reader.open();
		reader.setTagStreamFormat(reader.TEXT_FORMAT);
		reader.setTagListFormat(reader.TEXT_FORMAT);
		reader.autoModeReset();
		reader.setAutoStartTrigger("0 0");
		reader.setAutoStopTrigger("0 0");
		reader.setAutoMode(reader.ON);
		
		SQLConnection();

		reader.autoModeTriggerNow();

		long startTime = System.currentTimeMillis();

		// Ask the reader to read tags and print them

		while (true) {
			Tag tagList[] = reader.getTagList();

			if (tagList == null) {
			} else {
				for (int i = 0; i < tagList.length; i++) {
					Tag tag = tagList[i];

					Iterator it = tag_count.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						if (tag.getTagID().equals(pair.getKey())) {
							pair.setValue(Integer.parseInt(pair.getValue().toString())+1);
							break;
						}
					}
				}

				totalCount += tagList.length;
			}

			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			if (totalTime / 1000 == time) {
				Iterator it = tag_count.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					SQLInsertion(pair.getKey().toString(), Integer.parseInt(pair.getValue().toString()));
					pair.setValue(0);
				}
				System.out.println(" Count: " + totalCount);
				time += 20;
			}
		}
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
	public void SQLInsertion(String epc, int count) {
		epc = epc.replaceAll("\\s", "");

		try {
			PreparedStatement stmt = conn.prepareStatement
					("INSERT INTO Tag_Count(EPC, count, Date) VALUES "
							+ "('"+ epc + "', " + count + ", sysdate())");
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Main
	 */
	public static final void main(String args[]) {
		try {
			new Reader_Continuous();
		} catch (AlienReaderException e) {
			System.out.println("Error: " + e.toString());
		}
	}

} // End of class AlienClass1ReaderTest