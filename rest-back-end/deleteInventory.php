<?php
$servername = "localhost";
$username = "narped5_admin";
$password = "02040816";
$dbname = "narped5_PhoTreBien";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
$item=$_REQUEST['item'];
// sql to delete a record
$sql = "DELETE FROM inventory WHERE item='$item'";

if ($conn->query($sql) === TRUE) {
    echo "Record deleted successfully";
  
} else {
    echo "Error deleting record: " . $conn->error;
}

$conn->close();
?>