<?php
    $connect = mysqli_connect("localhost", "narped5_admin", "02040816", "narped5_PhoTreBien");
    
    $fullname = $_POST["fullname"];
    $user_name      = $_POST["user_name"];
    $user_password       = $_POST["user_password"];
    $user_type       = $_POST["user_type"];

     function registerUser() {
        global $connect, $fullname, $user_name, $user_password, $user_password, $user_type;
        $statement = mysqli_prepare($connect, "INSERT INTO user (fullname, user_name, user_password, user_type) VALUES (?, ?, ?, ?)");
        mysqli_stmt_bind_param($statement, "ssss", $fullname, $user_name, $user_password, $user_type);
        mysqli_stmt_execute($statement);
        mysqli_stmt_close($statement);     
    }
    function usernameAvailable() {
        global $connect, $user_name;
        $statement = mysqli_prepare($connect, "SELECT * FROM user WHERE user_name = ?"); 
        mysqli_stmt_bind_param($statement, "s", $user_name);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_close($statement); 
        if ($count < 1){
            return true; 
        }else {
            return false; 
        }
    }
    $response = array();
    $response["success"] = false;  
    if (usernameAvailable()){
        registerUser();
        $response["success"] = true;  
    }
    echo json_encode($response)
?>