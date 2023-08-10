<?php session_start(); /* Starts the session */

if(!isset($_SESSION['UserData']['Username'])){
	header("location:index.php");
	exit;
}
else
{
    $username = $_SESSION['UserData']['Username'];
	/*
	$username = $_SESSION['UserData']['Username'];
	if (!($username == "shopan222@gmail.com"))
	{
	    header("location:adminhome.php");
	    exit;
	}
	*/
}
?>

<?php
// define variables and set to empty values
$nameErr = $emailErr = $password = "";
$name = $email = $output = $password = "";


 // Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            printf("Unable to connect to the database:<br /> %s", $mysqli->error);
                exit();
        }else{
             // Create the query

             $query = "SELECT * FROM users WHERE user_email='$username'";
             //print($query);
             // Send the query to MySQL

             $result = $mysqli->query($query);

             if ($result->num_rows > 0){
                  $data = $result->fetch_object();
                  //$name = $data->name;
                  //print($name);
                 
                  $name = $data->user_name;
                 
                
                   $email = $data->user_email;
                 
                  $password = $data->user_password;
             }
             else { $usernameErr = "Data not found for [user: $username]";}
        }
?>

<?php
// define variables and set to empty values
$output = $node_id = $device_name = $device_key = $device_id = $input_data = $output_data = $device_status = "";
$node_idErr = $device_nameErr = $device_keyErr = $device_idErr = $input_dataErr = $output_dataErr = $device_statusErr = "";
//$node_device_id = generateRandomString();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
	//find_node
	if (isset($_POST['find_node'])){
		
		if (empty($_POST["device_id"])) {
            $device_idErr = "Device ID is required";
        } else {
            $device_id = (string)test_input($_POST["device_id"]);
            // check if name only contains letters and whitespace
			if (!preg_match("/^[a-zA-Z0-9]*$/",$device_id)) {
				$device_idErr = "Only letters and number is allowed";
				$device_id = "";
			}
		}
		
		// Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            printf("Unable to connect to the database:<br /> %s", $mysqli->error);
                exit();
        }else{
			if ((strlen($device_id) != 0))
			{
				// Create the queryn
				$query = "SELECT * from node_info where device_id='$device_id'";
				// Send the query to MySQL
				$result = $mysqli->query($query);
				if ($result->num_rows > 0){
					$info = $result->fetch_object();
					$device_name 		= $info->name;
					//$device_id 			= $info->device_id;
					$device_key 		= $info->device_key;
					$input_data			= $info->input_data;
					$device_status 		= $info->device_status;
					$output_data 		= $info->output_data;
					
					$output = "Success! record found";
				}
				else { 
					$output = 'Error:('.$mysqli->errno.')'.$mysqli->error;
				}
				
			}
			else {$output = "Enter all data";}
        }
		
	}
	
	if (isset($_POST['add_node'])){
		
		if (empty($_POST["device_name"])) {
            $device_nameErr = "Device name is required";
        } else {
            $device_name = (string)test_input($_POST["device_name"]);
            // check if name only contains letters and whitespace
			if (!preg_match("/^[a-zA-Z0-9 ]*$/",$device_name)) {
				$device_nameErr = "Only letters,number and white space allowed";
				$device_name = "";
			}
		}
		
		if (empty($_POST["device_key"])) {
            $device_keyErr = "Device key is required";
        } else {
            $device_key = (string)test_input($_POST["device_key"]);
            // check if name only contains letters and whitespace
			if (!preg_match("/^[a-zA-Z0-9]*$/",$device_key)) {
				$device_keyErr = "Only letters and number allowed";
				$device_key = "";
			}
		}
		
		if (empty($_POST["device_id"])) {
            $device_idErr = "Device ID is required";
        } else {
            $device_id = (string)test_input($_POST["device_id"]);
            // check if name only contains letters and whitespace
			if (!preg_match("/^[a-zA-Z0-9]*$/",$device_id)) {
				$device_idErr = "Only letters and number allowed";
				$device_id = "";
			}
		}
		
		if (empty($_POST["input_data"])) {
            $input_dataErr = "Please enter Input Data";
        } else {
            $input_data = (string)test_input($_POST["input_data"]);
		}
		
		if (empty($_POST["output_data"])) {
            $output_dataErr = "Please enter Output Data";
        } else {
            $output_data = (string)test_input($_POST["output_data"]);
		}
		
		if (empty($_POST["device_status"])) {
            $device_statusErr = "Device status is required";
        } else {
            $device_status = (string)test_input($_POST["device_status"]);
		}
		
		// Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            printf("Unable to connect to the database:<br /> %s", $mysqli->error);
                exit();
        }else{
			if ((strlen($device_name) != 0)&&(strlen($device_key) != 0) &&(strlen($device_id) != 0)&&(strlen($input_data) != 0)&&(strlen($output_data) != 0)&&(strlen($device_status) != 0))
			{
				// Create the queryn
				$query = "INSERT INTO node_info (name, username, device_id, device_key, input_data, output_data, device_status) VALUES ('$device_name', '$username', '$device_id', '$device_key', '$input_data', '$output_data', '$device_status')";
				// Send the query to MySQL
				$result = $mysqli->query($query);
				
				if ($result){
					$output = "Success! record created";
				}
				else { 
					$output = 'Error:('.$mysqli->errno.')'.$mysqli->error;
				}
				
			}
			else {$output = "Enter all data";}
        }
	}
	
	if (isset($_POST['edit_node'])){
		
		if (empty($_POST["device_name"])) {
            $device_nameErr = "Device name is required";
        } else {
            $device_name = (string)test_input($_POST["device_name"]);
            // check if name only contains letters and whitespace
			if (!preg_match("/^[a-zA-Z0-9 ]*$/",$device_name)) {
				$device_nameErr = "Only letters,number and white space allowed";
				$device_name = "";
			}
		}
		
		if (empty($_POST["device_key"])) {
            $device_keyErr = "Device key is required";
        } else {
            $device_key = (string)test_input($_POST["device_key"]);
            // check if name only contains letters and whitespace
			if (!preg_match("/^[a-zA-Z0-9]*$/",$device_key)) {
				$device_keyErr = "Only letters and number allowed";
				$device_key = "";
			}
		}
		
		if (empty($_POST["device_id"])) {
            $device_idErr = "Device ID is required";
        } else {
            $device_id = (string)test_input($_POST["device_id"]);
            // check if name only contains letters and whitespace
			if (!preg_match("/^[a-zA-Z0-9]*$/",$device_id)) {
				$device_idErr = "Only letters and number allowed";
				$device_id = "";
			}
		}
		
		if (empty($_POST["input_data"])) {
            $input_dataErr = "Please enter Input Data";
        } else {
            $input_data = (string)test_input($_POST["input_data"]);
		}
		
		if (empty($_POST["output_data"])) {
            $output_dataErr = "Please enter Output Data";
        } else {
            $output_data = (string)test_input($_POST["output_data"]);
		}
		
		if (empty($_POST["device_status"])) {
            $device_statusErr = "Device status is required";
        } else {
            $device_status = (string)test_input($_POST["device_status"]);
		}
		
		 // Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            printf("Unable to connect to the database:<br /> %s", $mysqli->error);
                exit();
        }else{
			if ((strlen($device_name) != 0)&&(strlen($device_key) != 0) &&(strlen($device_id) != 0)&&(strlen($input_data) != 0)&&(strlen($output_data) != 0)&&(strlen($device_status) != 0))
			{
				// Create the query 
				$query = "UPDATE node_info SET name='$device_name', device_key = '$device_key', input_data = '$input_data', output_data = '$output_data', device_status = '$device_status' WHERE device_id = '$device_id'";

				// Send the query to MySQL
				$result = $mysqli->query($query);
				
				if ($result){
					$output = "Success! record updated";
				}
				else { 
					$output = 'Error:('.$mysqli->errno.')'.$mysqli->error;
				}
				
			}
			else {$output = "Enter all data";}
        }
	}
	
	if (isset($_POST['del_node'])){
		
        if (empty($_POST["device_id"])) {
            $device_idErr = "Device ID is required";
        } else {
            $device_id = (string)test_input($_POST["device_id"]);
            // check if name only contains letters and whitespace
			if (!preg_match("/^[a-zA-Z0-9]*$/",$device_id)) {
				$device_idErr = "Only letters and number is allowed";
				$device_id = "";
			}
		}
		
		// Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            printf("Unable to connect to the database:<br /> %s", $mysqli->error);
                exit();
        }else{
			if ((strlen($device_id) != 0))
			{
				// Create the query
				 $query = "DELETE FROM node_info WHERE device_id='$device_id'";
				 // Send the query to MySQL
				 $results = $mysqli->query($query);
				 if ($results){
					 $output = "Success! record delated";
				 }
				 else {
					 $output = 'Error:('.$mysqli->errno.')'.$mysqli->error;
				 }
			}
			else {$output = "Device ID is required";}
        }
    }
}

function generateRandomString($length = 10) {
    $characters = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $charactersLength = strlen($characters);
    $randomString = '';
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $characters[rand(0, $charactersLength - 1)];
    }
    return $randomString;
}

function test_input($data) {
   $data = trim($data);
   $data = stripslashes($data);
   $data = htmlspecialchars($data);
   return $data;
}
?>

<html>
<head>
	<title>Internet Of Things</title>
	<style>
	.error {color: #FF0000;}
	table, th, td {

			border: 2px solid white;

			border -collapse: collapse;

			}

			th, td {

				padding: 2px;
			}
	</style>
</head>
<body>

	<table border="0" align="left" cellpadding="5" cellspacing="1" class="Table">
	
	<form method="post" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>">
		<tr>
			<td colspan="3" align="left" valign="top"><h2>Device Management</h2></td>
		</tr>
		<tr>
			<td colspan="3" align="left" valign="top"><a href='#'>User report [only for admin]</a></td>
		</tr>
		<tr>
			<th colspan="2" align="left" valign="top"><?php print("User: ".$name." ");?></th>
			<td align="left" valign="top"><a href='logout.php'>Log out</a></td>
		</tr>
		<tr>
			<th align="left" valign="top">Device Name: </th>
			<td>
				<input type="text" name="device_name" value="<?php echo $device_name;?>">
				<span class="error">* <?php echo $device_nameErr;?></span>
			</td>
		</tr>
		<tr>
			<th align="left" valign="top"> Device ID: </th>
			<td> 
				<input type="text" name="device_id" value="<?php echo $device_id;?>"> 
				<span class="error">* <?php echo $device_idErr;?></span>
			</td> 
			<td>
				<input name="find_node" type="submit" value="Find" style="width: 50px; height: 20px;"/>
			</td>
		</tr>
		
		<tr>
			<th align="left" valign="top">Device Key: </th>
			<td>
				<input type="text" name="device_key" value="<?php echo $device_key;?>">
				<span class="error">* <?php echo $device_keyErr;?></span>
			</td>
		</tr>

		<tr>
			<th align="left" valign="top"> Input Data:  </th>
			<td> 
				<input type="text" name="input_data" value="<?php echo $input_data;?>">
				<span class="error">* <?php echo $input_dataErr;?></span>
			</td> 
		</tr>
		
		<tr>
			<th align="left" valign="top"> Output Data:  </th>
			<td> 
				<input type="text" name="output_data" value="<?php echo $output_data;?>">
				<span class="error">* <?php echo $output_dataErr;?></span>
			</td> 
		</tr>
		
		<tr>
			<th align="left" valign="top"> Status:  </th>
			<td> 
				<input type="text" name="device_status" value="<?php echo $device_status;?>">
				<span class="error">* <?php echo $device_statusErr;?></span>
			</td> 
		</tr>
		
		<tr>
			<td><span class="error">* Required Field</span> <br> <?php echo $output;?></td>
			<br><br>
		</tr>

		<tr>
			<td align="center" valign="top"><input name="add_node" type="submit" value="ADD Device" style="width: 150px; height: 40px;"/></td>
			<td align="center" valign="top"><input name="edit_node" type="submit" value="Update Device" style="width: 150px; height: 40px;"/></td>
			<td align="center" valign="top"><input name="del_node" type="submit" value="Delete Device" style="width: 150px; height: 40px;"/></td>
			
			<td>
			<br><br>
			</td>
		</tr>
		
	</form>
	
	</table>
	<?php

 // Connect to the database server     
        $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
        if ($mysqli->errno) {
            printf("Unable to connect to the database:<br /> %s", $mysqli->error);
                exit();
        }else{
             // Create the query

             $query = "SELECT id, name, device_id, device_key, input_data, output_data, device_status FROM node_info WHERE username='$username'";
             //print($query);
             // Send the query to MySQL

			 $data = $mysqli->query($query, MYSQLI_STORE_RESULT);
        }
?>

	<table style="width:100%" align="left" >
		<tr>
			<th align="left" >Device Name </th>
			<th align="left" >Device ID </th>
			<th align="left" >Security Key</th>
			<th align="left" >Input</th>
			<th align="left" >Output</th>
			<th align="left" >Last login</th>
		</tr>
		<?php
			// Iterate through the result set
			while(list($id, $name, $device_id, $device_key, $input_data, $output_data, $device_status) = $data->fetch_row())
			printf("<tr> <td align='left' ><a href='/nodedata.php/?iotnode_id=%s'>%s</a> <td align='left'>%s</td> <td align='left'>%s</td> <td align='left'>%s</td> <td align='left'>%s</td> <td align='left'>%s</td> </tr>", $id, $name, $device_id, $device_key, $input_data, $output_data, $device_status);
		?>
	</table>
	<br><br>
</body>
</html>
