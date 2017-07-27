<?php
	$startIndex = $_POST ["startIndex"];
	$endIndex = $_POST ["endIndex"];

	$filename = "ReadingRate_Log.csv";
	$fh = fopen ( $filename, "a" );
	
	$host = '127.0.0.1';
	$uname = 'minhye';
	$pwd = '1234';
	$db = "RFID";
	
	$con = mysqli_connect ( $host, $uname, $pwd, $db ) or die ( "connection failed" );
	mysqli_set_charset ( $con, "utf8" );
	mysqli_query ( $con, "SET NAMES UTF8" );
	
	$query = "Select * from Tag_Count Where ID >= ".$startIndex." And ID <= ".$endIndex;
	$result = mysqli_query ( $con, $query );
	
	while ( $row = mysqli_fetch_array ( $result ) ) {
		$id = $row ["id"];
		$epc = $row ["EPC"];
		$count = $row ["count"];
		$date = $row ["Date"];

		$data = $id.",".$epc.",".$count.",".$date."\r\n";
		fwrite ( $fh, $data);
	}
	
	fclose ( $fh );

	echo '<script language="javascript"> alert("Save data successfully!") </script>';


	echo '<form name="myform"method="post" action="http://localhost/ReadingRate.php">
	<script language="JavaScript">document.myform.submit();</script></form>';
?>

