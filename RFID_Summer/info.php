<?php
	$host='127.0.0.1';
	$uname='minhye';
	$pwd='1234';
	$db="RFID";

	$con = mysqli_connect($host,$uname,$pwd, $db) or die("connection failed");
	mysqli_set_charset($con,"utf8");
	mysqli_query($con,"SET NAMES UTF8");
 
	$epc=$_POST["epc"];
	$user=$_POST["user"];
	$tid=$_POST["tid"];
	$trans_p=$_POST["trans_p"];
	$rssi=$_POST["rssi"];
	$count=$_POST["count"];

	$tag = array(
		array('E20170524F102725527201F42507E001', 'D20170524F102725527201F42507D001', 
'E20034140121010165380E58'), 
		array('E20170524F102725527201F42507E002', 'D20170524F102725527201F42507D002', 
'E20034140122010165380E54'), 
		array('E20170524F102725527201F42507E003', 'D20170524F102725527201F42507D003',
'E2003414011C010165382060'), 
		array('E20170524F102725527201F42507E004', 'D20170524F102725527201F42507D004',
'E2003414011A010165381DCC'), 		
		array('E20170524F102725527201F42507E005', 'D20170524F102725527201F42507D005',
'E2003414011C01016537ECE3'), 
		array('E20170524F102725527201F42507E006', 'D20170524F102725527201F42507D006',
'E2003414011F01016537E7BF'), 
		array('E20170524F102725527201F42507E007', 'D20170524F102725527201F42507D007',
'E2003414012201016537F720'), 
		array('E20170524F102725527201F42507E008', 'D20170524F102725527201F42507D008',
'E2003414011B01016537DD82'), 
		array('E20170524F102725527201F42507E009', 'D20170524F102725527201F42507D009',
'E2003414012301016537FED0'), 
		array('E20170524F102725527201F42507E010', 'D20170524F102725527201F42507D010',
'E20034140122010165381DD0')
	);

	$epc_suc = 0;
	$user_suc = 0;
	$tid_suc = 0;
	
	for($row = 0; $row < 10; $row++) {
		$tag_epc = $tag[$row][0];
		$tag_user = $tag[$row][1];
		$tag_tid = $tag[$row][2];
		if(strcasecmp($tag_epc,$epc)==0) $epc_suc = 1;		
		if(strcasecmp($tag_user,$user)==0) $user_suc = 1;
		if(strcasecmp($tag_tid, substr($tid,0,24)) == 0) $tid_suc = 1;
	}	

	$line=file('C:\Users\Min Hye\Downloads\processing-3.3.4-windows64\processing-3.3.4\Temperature\temperature.txt');
	
	$temperature = implode(" ", $line);

	$query = "INSERT INTO TagInfo(EPC, TID, USER, transmission, temperature, RSSI, DATE, EPC_SUC, TID_SUC, USER_SUC) VALUES ('$epc', '$tid', '$user', '$trans_p', '$temperature', '$rssi', sysdate(), '$epc_suc', '$tid_suc', '$user_suc')";

	$row = mysqli_query($con, $query);

	$count_num = (int)$count;
	
?>
 