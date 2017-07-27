<?php
	$startIndex = $_POST ["startIndex"];
	$endIndex = $_POST ["endIndex"];

	$filename = "LogFile.txt";
	$fh = fopen ( $filename, "a" );
	
	$host = '127.0.0.1';
	$uname = 'minhye';
	$pwd = '1234';
	$db = "RFID";
	
	$con = mysqli_connect ( $host, $uname, $pwd, $db ) or die ( "connection failed" );
	mysqli_set_charset ( $con, "utf8" );
	mysqli_query ( $con, "SET NAMES UTF8" );
	
	$query = "Select * from TagInfo_9650 Where ID >= ".$startIndex." And ID <= ".$endIndex;
	$result = mysqli_query ( $con, $query );
	
	while ( $row = mysqli_fetch_array ( $result ) ) {
		$id = $row ["ID"];
		$epc = $row ["EPC"];
		$user = $row ["USER"];
		$tid = $row ["TID"];
		$power = $row ["Transmission"];
		$rssi = $row ["RSSI"];
		$temp = $row ["Temperature"];
		$date = $row ["DATE"];

		$data = $id."\r\t".$epc."\r\t".$user."\r\t".$tid."\r\t".$power."\r\t".$rssi."\r\t".$temp."\r\t".$date."\r\n";
		fwrite ( $fh, $data);
	}
	
	fclose ( $fh );

	echo '<script language="javascript"> alert("Save data successfully!") </script>';


	echo '<form name="myform"method="post" action="http://localhost/Result_ALR9650.php">
	<script language="JavaScript">document.myform.submit();</script></form>';
?>

