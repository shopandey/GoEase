<?php 

session_start(); // Starts the session

if(!isset($_SESSION['UserData']['Username'])){
	header("location:index.php");
	exit;
}
else
{
	$username = $_SESSION['UserData']['Username'];
	/*
	if (!($username == "shopan222@gmail.com"))
	{
		header("location:adminhome.php");
		exit;
	}
	*/
}

?>

<?php

	$node_name = $node_password = "";
	$id = "";
	$id = $_GET['iotnode_id'];
	
	error_log("Node ID: ".$id);
	
	$output = "";
	if (empty($id)) {
		$id = $_SESSION['UserData']['Nodeid'];
	}
	else{
		$_SESSION['UserData']['Nodeid']=$id;
	}
	// Connect to the database server     
    $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
    if ($mysqli->errno) {
        printf("Unable to connect to the database:<br /> %s", $mysqli->error);
        exit();
    }else{
		$query = "SELECT * FROM node_info WHERE id=$id and username='$username'";
		$result = $mysqli->query($query);
		if ($result->num_rows > 0){
            $info = $result->fetch_object();
			$device_name 		= $info->name;
			$device_id 			= $info->device_id;
			$device_key 		= $info->device_key;
			$input_data			= $info->input_data;
			$device_status 		= $info->device_status;
			$output_data 		= $info->output_data;

			$data_arr = explode(',',str_replace( array("[", "]"), '', $output_data));
			
			
		}
		else {
            $output = 'Error:('.$mysqli->errno.')'.$mysqli->error;
        }
	}
	
function test_input($data) {
   $data = trim($data);
   $data = stripslashes($data);
   $data = htmlspecialchars($data);
   return $data;
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
	if (isset($_POST['b1'])){

		if ($data_arr[0] == 1){
			$data_arr[0] = 0;
		}
		else{
			$data_arr[0] = 1;
		}
		
		$data_output=$data_arr[0].','.$data_arr[1].','.$data_arr[2].','.$data_arr[3];
		//echo $data_output;
		// Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            printf("Unable to connect to the database:<br /> %s", $mysqli->error);
                exit();
        }else{
			if ((strlen($id) != 0))
			{
				// Create the query
				 $query = "UPDATE node_info SET output_data='$data_output' WHERE id=$id";
				 // Send the query to MySQL
				 $results = $mysqli->query($query);
				 if ($results){
					 $output = "Success! record updated";
					 header("location:nodedata.php?iotnode_id=".$id);
					exit;
				 }
				 else {
					 $output = 'Error:('.$mysqli->errno.')'.$mysqli->error;
				 }
			}
			else {$output = "Node ID is required";}
        }
    }
	
	if (isset($_POST['b2'])){

		if ($data_arr[1] == 1){
			$data_arr[1] = 0;
		}
		else{
			$data_arr[1] = 1;
		}
		
		$data_output=$data_arr[0].','.$data_arr[1].','.$data_arr[2].','.$data_arr[3];
		//echo $data_output;
		// Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            printf("Unable to connect to the database:<br /> %s", $mysqli->error);
                exit();
        }else{
			if ((strlen($id) != 0))
			{
				// Create the query
				 $query = "UPDATE node_info SET output_data='$data_output' WHERE id=$id";
				 // Send the query to MySQL
				 $results = $mysqli->query($query);
				 if ($results){
					 $output = "Success! record updated";
					 header("location:nodedata.php?iotnode_id=".$id);
					exit;
				 }
				 else {
					 $output = 'Error:('.$mysqli->errno.')'.$mysqli->error;
				 }
			}
			else {$output = "Node ID is required";}
        }
    }
	
	if (isset($_POST['b3'])){

		if ($data_arr[2] == 1){
			$data_arr[2] = 0;
		}
		else{
			$data_arr[2] = 1;
		}
		
		$data_output=$data_arr[0].','.$data_arr[1].','.$data_arr[2].','.$data_arr[3];
		//echo $data_output;
		// Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            printf("Unable to connect to the database:<br /> %s", $mysqli->error);
                exit();
        }else{
			if ((strlen($id) != 0))
			{
				// Create the query
				 $query = "UPDATE node_info SET output_data='$data_output' WHERE id=$id";
				 // Send the query to MySQL
				 $results = $mysqli->query($query);
				 if ($results){
					 $output = "Success! record updated";
					 header("location:nodedata.php?iotnode_id=".$id);
					exit;
				 }
				 else {
					 $output = 'Error:('.$mysqli->errno.')'.$mysqli->error;
				 }
			}
			else {$output = "Node ID is required";}
        }
    }
	
	if (isset($_POST['b4'])){

		if ($data_arr[3] == 1){
			$data_arr[3] = 0;
		}
		else{
			$data_arr[3] = 1;
		}
		
		$data_output=$data_arr[0].','.$data_arr[1].','.$data_arr[2].','.$data_arr[3];
		//echo $data_output;
		// Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            printf("Unable to connect to the database:<br /> %s", $mysqli->error);
                exit();
        }else{
			if ((strlen($id) != 0))
			{
				// Create the query
				 $query = "UPDATE node_info SET output_data='$data_output' WHERE id=$id";
				 // Send the query to MySQL
				 $results = $mysqli->query($query);
				 if ($results){
					 $output = "Success! record updated";
					 header("location:nodedata.php?iotnode_id=".$id);
					exit;
				 }
				 else {
					 $output = 'Error:('.$mysqli->errno.')'.$mysqli->error;
				 }
			}
			else {$output = "Node ID is required";}
        }
    }
}
?>

<html>
<head>
	<title>Internet of Things</title>
	<style>

		table, th, td {

			border: 1px solid white;
			border -collapse: collapse;

			}

			th, td {

				bordercolor: white;
				padding: 2px;
			}

		</style>
</head>
<body>
<form method="post" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>">
	<table border="0" align="left" cellpadding="5" cellspacing="1" class="Table">
		<tr>
			<th colspan="2" align="left" valign="top"><h2>Device Control</h2></th>
		</tr>
		<tr>
			<th align="left" valign="top">Device Name:</th>
			<td><?php echo $device_name ?></th>
		</tr>
		<tr>
			<th align="left" valign="top">Device ID:</th>
			<td><?php echo $device_id ?></th>
		</tr>
		<tr>
			<th align="left" valign="top">Security Key:</th>
			<td><?php echo $device_key ?></th>
		</tr>
		<tr>
			<th align="left" valign="top">Input Data:</th>
			<td><?php echo $input_data ?></th>
		</tr>
		<tr>
			<th align="left" valign="top">Ouput Data:</th>
			<td><?php echo ($data_arr[0] == 1) ? 'On ' : 'Off '; ?>,<?php echo ($data_arr[1] == 1) ? 'On ' : 'Off '; ?>,<?php echo ($data_arr[2] == 1) ? 'On ' : 'Off '; ?>,<?php echo ($data_arr[3] == 1) ? 'On ' : 'Off '; ?></th>
		</tr>
		<tr>
			<th align="left" valign="top">Status:</th>
			<td><?php echo $device_status ?></th>
		</tr>
		<tr>
			<th colspan="2" align="center" valign="top">
				<br>
				<input name="b1" type="submit" value="<?php echo ($data_arr[0] == 1) ? 'Turn Off' : 'Turn On'; ?>" style="width: 100px; height: 30px;"/>
				<input name="b2" type="submit" value="<?php echo ($data_arr[1] == 1) ? 'Turn Off' : 'Turn On'; ?>" style="width: 100px; height: 30px;"/>
				<input name="b3" type="submit" value="<?php echo ($data_arr[2] == 1) ? 'Turn Off' : 'Turn On'; ?>" style="width: 100px; height: 30px;"/>
				<input name="b4" type="submit" value="<?php echo ($data_arr[3] == 1) ? 'Turn Off' : 'Turn On'; ?>" style="width: 100px; height: 30px;"/>
			</th>
		</tr>
		<tr>
			<th colspan="2" align="left" valign="top"> <br> <br><a href="/adminhome.php">back</a> </th>
		</tr>
		<tr>
			<th colspan="2" align="left" valign="top"> <?php echo $output; ?> </th>
		</tr>
	</table>
</form>
</body>
</html>