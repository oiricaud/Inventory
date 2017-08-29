<?php

/* array for JSON response */
$response = array();

/* CONNECTION SETTINGS */
$DB_HOST = 'localhost';
$DB_UNAME = 'narped5_admin';
$DB_PWD = '02040816';
$DB_DATABASE = 'narped5_PhoTreBien';

/* Connecting to mysql database */
$mysqli = new mysqli($DB_HOST, $DB_UNAME, $DB_PWD, $DB_DATABASE);

if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}

/* CONSTRUCT THE QUERY */
$query = "SELECT * FROM prices";
$result = $mysqli->query($query) or die($mysqli->error.__LINE__);

if ($result === false)  {
    trigger_error('Wrong SQL: ' . $sql . ' Error: ' . $conn->error, E_USER_ERROR);
} else  {

    $response["stuff"] = array();

    while($row = $result->fetch_assoc())    {

        $stuff= array();

        /* ADD THE TABLE COLUMNS TO THE JSON OBJECT CONTENTS */
        $stuff["id"] = $row['id'];
        $stuff["item"] = $row['item'];
        $stuff["pricePerQty"] = $row['pricePerQty'];
        $stuff["seller"] = $row['seller'];
        
        array_push($response["stuff"], $stuff);

        // $response[] = $row;
    }
    // success
    $response["success"] = 1;

    echo(json_encode($response));
}

/* CLOSE THE CONNECTION */
mysqli_close($mysqli);
?>