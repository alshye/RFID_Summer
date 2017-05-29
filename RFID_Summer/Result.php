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
		<th width="30"> No. </th> <th width="100"> EPC </th> 
		<th width="100"> TID </th> <th width="100"> USER </th>
		<th width="50"> ACCP </th> <th width="50"> KILP </th>
		<th width="5"> EPC_SUC </th> <th width="5"> TID_SUC </th>
		<th width="5"> USER_SUC </th> <th width="5"> ACCP_SUC </th>
		<th width="5"> KILP_SUC </th> <th width="150"> DATE </th> </tr>';	

	while($row = mysqli_fetch_array($result)) {
		$id = $row["ID"];
		$epc = $row["EPC"];
		$tid = $row["TID"];
		$user = $row["USER"];
		$accp = $row["ACCP"];
		$kilp = $row["KILP"];
		$date = $row["DATE"];
		$epc_suc = $row["EPC_SUC"];
		$tid_suc = $row["TID_SUC"];
		$user_suc = $row["USER_SUC"];
		$accp_suc = $row["ACCP_SUC"];
		$kilp_suc = $row["KILP_SUC"];
		echo '<tr> <td>' .$id. '</td> <td>' .$epc. '</td> <td>' .$tid. '</td> <td>'
		.$user. '</td> <td>' .$accp. '</td> <td>' .$kilp. '</td> <td>'
		.$epc_suc. '</td> <td>' .$tid_suc. '</td> <td>' .$user_suc. '</td> <td>'
		.$accp_suc. '</td> <td>' .$kilp_suc. '</td> <td>' .$date. '</td> </tr>';
	}
	echo '</table> </body>';
?>
