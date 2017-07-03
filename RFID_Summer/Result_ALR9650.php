<?php
$host='127.0.0.1';
$uname='minhye';
$pwd='1234';
$db="RFID";

$con = mysqli_connect($host,$uname,$pwd, $db) or die("connection failed");
mysqli_set_charset($con,"utf8");
mysqli_query($con,"SET NAMES UTF8");

$query = "Select * from TagInfo_9650";
$result = mysqli_query($con, $query);

echo '<html> <head> <style type ="text/css">
		table.gridtable {
			font-family: verdana;
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
		.button {
    			background-color: white; 
    			border-color: #A2D9CE;
    			color: #A2D9CE;
    			padding: 7px 12px;
			border-radius: 12px;
    			text-align: center;
    			text-decoration: none;
    			display: inline-block;
    			font-size: 12px;
			font-family: verdana;
			font-weight: bold;
		}

		</style> <meta http-equiv="refresh" content=20;> </head> <body>	
		<h1> <center> <p> <font color = #95A5A6> Tag Information </font> </p> </center> </h1> 
		<form id="save" name="save" method="post" action="http://localhost/SavaData.php">
		<input type="submit" align="right" class="button" name="save" value="Save" /> 
		<p> <input type="text" name="startIndex" style="border:2px solid #A2D9CE" size=1/> &nbsp;&nbsp;&nbsp;
		<input type="text" name="endIndex" style="border:2px solid #A2D9CE" size=1/>
</form> </p>
		<table class="gridtable"> <tr>
		<th width="30"> No. </th> <th width="300"> EPC Memory </th>
		<th width="300"> USER Memory </th> <th width="250"> TID Memory </th>
		<th width="100"> POWER </th> <th width="100"> RSSI </th>
		<th width="100"> TEMP </th> <th width="30"> EPC_SUC </th>
		<th width="30"> USER_SUC </th> <th width="30"> TID_SUC </th>
		<th width="300"> DATE </th> </tr>';

	while($row = mysqli_fetch_array($result)) {
		$id = $row["ID"];
		$epc = $row["EPC"];
		$user = $row["USER"];
		$tid = $row["TID"];
		$power = $row["Transmission"];
		$temp = $row["Temperature"];
		$rssi = $row["RSSI"];
		$date = $row["DATE"];
		$epc_suc = $row["EPC_SUC"];	
		$user_suc = $row["USER_SUC"];
		$tid_suc = $row["TID_SUC"];
		echo '<tr> <td>' .$id. '</td> <td>' .$epc. '</td> <td>' .$user. '</td> <td>' .$tid. '</td> <td>' .$power. '</td> <td>-' .$rssi. '</td> <td>' .$temp.'&deg;F</td> <td>'
		.$epc_suc. '</td> <td>' .$user_suc. '</td> <td>' .$tid_suc. '</td> <td>' .$date. '</td> </tr>';
	}
	echo '</table> </body>';
?>
