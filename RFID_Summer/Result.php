<?php
$host='127.0.0.1';
$uname='minhye';
$pwd='1234';
$db="RFID";

$con = mysqli_connect($host,$uname,$pwd, $db) or die("connection failed");
mysqli_set_charset($con,"utf8");
mysqli_query($con,"SET NAMES UTF8");

$query = "Select * from TagInfo";
$result = mysqli_query($con, $query);

echo '<html> <head> <style type ="text/css">
		table.gridtable {
			font-family: verdana,arial,sans-serif;
			font-size: 11px;
			color:#333333;
			border-width: 1px;
			border-color: #999999;
			border-collapse: collapse;
		}
		table.gridtable th {
			color:white;
			border-width: 1px;
			padding: 8px;
			border-style: solid;
			border-color: #A2D9CE;
			background-color: #A2D9CE;
		}
		table.gridtable td {
			color:#95A5A6;
			text-align:center;
			border-width: 1px;
			padding: 8px;
			border-style: solid;
			border-color: white;
			background-color: white;
		}
		</style> </head> <body>
		<h1> <center> <p> <font color = #95A5A6> Tag Information </font> </p> <center> </h1>
		<table class="gridtable"> <tr>
		<th width="30"> No. </th> <th width="100"> EPC Memory </th>
		<th width="100"> USER Memory </th> <th width="100"> TID Memory </th>
		<th width="50"> POWER </th> <th width="50"> TEMP </th>
		<th width="50"> RSSI </th> <th width="5"> EPC_SUC </th>
		<th width="5"> TID_SUC </th> <th width="5"> USER_SUC </th>
		<th width="150"> DATE </th> </tr>';

	while($row = mysqli_fetch_array($result)) {
		$id = $row["ID"];
		$epc = $row["EPC"];
		$user = $row["USER"];
		$tid = $row["TID"];
		$power = $row["transmission"];
		$temp = $row["temperature"];
		$rssi = $row["RSSI"];
		$date = $row["DATE"];
		$epc_suc = $row["EPC_SUC"];
		$tid_suc = $row["TID_SUC"];
		$user_suc = $row["USER_SUC"];
		echo '<tr> <td>' .$id. '</td> <td>' .$epc. '</td> <td>' .$user. '</td> <td>'.$tid. '</td> <td>' .$power. '</td> <td>' .$temp. 		'</td> <td>' .$rssi. '</td> <td>'
		.$epc_suc. '</td> <td>' .$tid_suc. '</td> <td>' .$user_suc. '</td> <td>'	.$date. '</td> </tr>';
	}
	echo '</table> </body>';
?>
