<html>
<head>
<style>
.error {color: #FF0000;}
</style>
</head>
<body>
<title> Registration </title>
<?php
// define variables and set to empty values
$nameErr = $emailErr = $passwordErr = "";
$name =  $email = $password = $output = "";

if ($_SERVER["REQUEST_METHOD"] == "POST") {
	
   if (empty($_POST["name"])) {
     $nameErr = "Name is required";
   } else {
     $name = test_input($_POST["name"]);
     // check if name only contains letters and whitespace
     if (!preg_match("/^[a-zA-Z ]*$/",$name)) {
       $nameErr = "Only letters and white space allowed";
	   $name = "";
     }
   }

   if (empty($_POST["password"])) {
     $passwordErr = "Password is required";
   } else {
     $password = test_input($_POST["password"]);
   }
   
   if (empty($_POST["email"])) {
     $emailErr = "Email is required";
   } else {
     $email = test_input($_POST["email"]);
     // check if e-mail address is well-formed
     if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
       $emailErr = "Invalid email format";
	   $email = "";
     }
   }


  // Connect to the database server     
  $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
  if ($mysqli->errno) {
      printf("Unable to connect to the database:<br /> %s", $mysqli->error);
      exit();
  }
  else{
      //if ((strlen($name) != 0)&&(strlen($dept) != 0)&&((strlen($year) != 0)&&(strlen($email) != 0)&&(strlen($mobile) != 0)&&(strlen($address) != 0)&&(strlen($gender) != 0))
      if ((strlen($name) != 0)&&(strlen($email) != 0)&&(strlen($password) != 0))
      {
            // Create the query $result = 
            $query = "INSERT INTO users (user_name , user_password, user_email ) VALUES ('$name', '$password', '$email');";

            // Send the query to MySQL

            $mysqli->query($query);
            //while(list($roll, $name, $dept, $year, $email, $mobile, $address, $gender) = $result->fetch_row())

                  //printf("%s %s %s %s %s %s %s %s <br>", $roll, $name, $dept, $year, $email, $mobile, $address, $gender);
            $output = "Data Uploaded";
            $name = $email = $password =  "";
      }
	  else
	  {
		  $output = "Data Empty !!!";
	  }
  }
}

function test_input($data) {
   $data = trim($data);
   $data = stripslashes($data);
   $data = htmlspecialchars($data);
   return $data;
}
?>


<form method="post" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>">

	<table width="400" border="0" align="center" cellpadding="5" cellspacing="1" class="Table">
		<tr>
			<td>&nbsp;</td>
			<td colspan="2" align="left" valign="top"><h3>Registration</h3></td>
		</tr>
		
		<tr>
			<td align="left" valign="top">Name</td>
			<td><input type="text" name="name" value="<?php echo $name;?>"><span class="error">* <?php echo $nameErr;?></span></td>
		</tr>
		
		<tr>
			<td align="left" valign="top">Password</td>
			<td><input type="text" name="password" value="<?php echo $password;?>"><span class="error">* <?php echo $passwordErr;?></span></td>
		</tr>
		
		<tr>
			<td align="left" valign="top">E-mail</td>
			<td><input type="text" name="email" value="<?php echo $email;?>"><span class="error">* <?php echo $emailErr;?></span></td>
		</tr>
		
		<tr>
			<td>&nbsp;</td>
			<td colspan="2" align="left" valign="top"><p><span class="error">* required field.</span></p></td>
		</tr>
		
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit" name="submit" value="Submit"></td>
		</tr>
		
		<tr>
			<td>&nbsp;</td>
			<td><a href="index.php">back</a></td>
		</tr>
		
		<tr>
			<td>&nbsp;</td>
			<td><?php echo "$output"; ?></td>
		</tr>
	</table>
</form>

</body>

</html>
