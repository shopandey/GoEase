<?php
//Creates new record as per request

    //Get current date and time
    date_default_timezone_set('Asia/Kolkata');
    $d = date("Y-m-d");
    //echo " Date:".$d."<BR>";
    $t = date("H:i:s");
	$log_time = $d."_".$t;
	$outputdata = "";
	
    if(!empty($_POST['id']) && !empty($_POST['key']) && !empty($_POST['output_data']))
    {
    	$device_id = $_POST['id'];
    	$device_key = $_POST['key'];
		$device_output = $_POST['output_data'];
		
		error_log($log_time);
		error_log($device_id);
		error_log($device_key);
		error_log($device_output);
		
		// Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            error_log('Unable to connect to the database: '.$mysqli->error);
			
            exit();
        }else{
			 // Create the query
             $query = "SELECT * FROM node_info WHERE device_id='$device_id' and device_key='$device_key'";
             // Send the query to MySQL
             $results = $mysqli->query($query);

             if ($results->num_rows > 0){
				
				$nodeinfo = $results->fetch_object();
				$username = $nodeinfo->username;
				$nodename = $nodeinfo->name;
				$inputdata = $nodeinfo->input_data;
				
				error_log("auth ok ".$nodename);
				
				
				$query = "UPDATE node_info SET output_data = '$device_output' WHERE device_id='$device_id'";
				$results = $mysqli->query($query);
				if ($results) 
				{
					$output = "@Success! record saved";
					error_log($output);
				}
				else 
				{
					$output = '@Error:('.$mysqli->errno.')'.$mysqli->error;
					error_log($output);
				}
				
			 }
             else {
                $output = '@Error:('.$mysqli->errno.')'.$mysqli->error;
				error_log("auth error");
				error_log($output);
             }
		}
		error_log($outputdata);
		echo("true,".$outputdata);

	}
?>