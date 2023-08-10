
<?php
session_start();
/* Starts the session */
	
	$msg="";

if(isset($_POST['Forgot'])){
	$Username = isset($_POST['Username']) ? $_POST['Username'] : '';
	if ($Username == "") $msg="<span style='color:red'>Enter your username</span>";
	else{
		header("location:sendpassword.php/?username=$Username");
		exit;
	}
}	


//$msg="<span style='color:red'>*</span>";
/* Check Login form submitted */	
if(isset($_POST['Submit'])){

	/* Check and assign submitted Username and Password to new variable */

	$Username = isset($_POST['Username']) ? $_POST['Username'] : '';

	$Password = isset($_POST['Password']) ? $_POST['Password'] : '';

	/* Check Username and Password existence in defined array */
	// Connect to the database server     
    $mysqli = new mysqli('localhost', 'uem', 'password', 'uem');  
    if ($mysqli->errno) {
        printf("Unable to connect to the database:<br /> %s", $mysqli->error);
        exit();
    }else{
        
		if ((!($Password==""))&&(!($Username==""))){
			// Create the query
			$query = "SELECT * FROM users WHERE user_email='$Username' and user_password='$Password'";
			
			// Send the query to MySQL
			$result = $mysqli->query($query);
		
			//if (isset($logins[$Username]) && $logins[$Username] == $Password){
			if ($result->num_rows>0){
				/* Success: Set session variables and redirect to Protected page  */
				$_SESSION['UserData']['Username']=$Username;
				header("location:adminhome.php");
				exit;
			} else {
				/*Unsuccessful attempt: Set error message */
				$msg="<span style='color:red'>Invalid Login Details</span>";
			}
		}
		else{
			$msg="<span style='color:red'>Username and password can not be empty</span>";
		}
	}
}
?>

<!doctype html>

<html>
<head>

<title>Login page</title>

</head>

<body>

<form action="" method="post" name="Login_Form">

  <table width="400" border="0" align="center" cellpadding="5" cellspacing="1" class="Table">

	
    <tr>
		<td>&nbsp;</td>
		<td colspan="2" align="left" valign="top"><h2>IoT Server</h2></td>

    </tr>
	
    <tr>
		<td>&nbsp;</td>
		<td colspan="2" align="left" valign="top"><h3>Login</h3></td>

    </tr>

    <tr>

		<td align="right" valign="top">Username</td>

		<td><input name="Username" type="text" class="Input"></td>

    </tr>

    <tr>

		<td align="right">Password</td>

		<td><input name="Password" type="password" class="Input"></td>

    </tr>

	<?php if(isset($msg)){ ?>

    <tr>

      <td colspan="2" align="center" valign="top"> <?php echo $msg;?> </td>

    </tr>

  <?php } ?>
	
    <tr>

      <td>&nbsp;</td>

      <td><input name="Submit" type="submit" value="Login" class="Button3"></td>

    </tr>
  <tr>
          <td>&nbsp;</td>
           <td> <a href='registration.php'> Registration</a></td>
  </tr>

  
  
 </table>

</form>

</body>

</html>
