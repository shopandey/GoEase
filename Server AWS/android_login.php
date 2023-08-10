<?php

$output = "";

if ((!empty($_POST["username"]))&&(!empty($_POST["password"])))
{
	
	$username = $_POST['username'];
	$password = $_POST['password'];
	
	
	// Check Username and Password existence in defined array //
	// Connect to the database server     
    $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
    if ($mysqli->errno) {
        error_log("Unable to connect to the database:<br /> %s", $mysqli->error);
        exit();
    }else{
        
		if ((!($password==""))&&(!($username==""))){
			// Create the query
			$query = "SELECT * FROM users WHERE user_email='$username' and user_password='$password'";
			
			// Send the query to MySQL
			$result = $mysqli->query($query);
		
			//if (isset($logins[$Username]) && $logins[$Username] == $Password){
			if ($result->num_rows>0){
				
				$info = $result->fetch_object();
				$name = $info->user_name;
				
				$output = $output.'true!'.$name.'!';
				
				$query = "SELECT id, name, device_id, device_key, input_data, output_data, device_status FROM node_info WHERE username='$username'";
				//print($query);
				// Send the query to MySQL

				$data = $mysqli->query($query, MYSQLI_STORE_RESULT);
				while(list($id, $name, $device_id, $device_key, $input_data, $output_data, $device_status) = $data->fetch_row())
				$output = $output.$name.','.$device_id.','.$device_key.','.$input_data.','.$output_data.','.$device_status.';';
				$output = $output.'!';
				
				//exit;
			} else {
				//Unsuccessful attempt: Set error message
				$msg="Invalid Login Details";
			}
		}
		else{
			$msg="Username and password can not be empty";
		}
	}
	
	
	error_log($username);
	error_log($password);
	echo($output);

}

?>