<?php
    $con = mysqli_connect("localhost", "narped5_admin", "02040816", "narped5_PhoTreBien");
    
    $fullname = $_POST["fullname"];
    $user_name      = $_POST["user_name"];
    $user_password       = $_POST["user_password"];
    $user_type       = $_POST["user_type"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM user WHERE user_name = ? AND user_password = ?");
    
    mysqli_stmt_bind_param($statement, "ss", $user_name, $user_password);
    
    mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $user_id,  $fullname, $user_name, $user_password, $user_type);
    
    $response = array();
    $response["success"] = false;  
    
    while(mysqli_stmt_fetch($statement)){
        $response["success"] = true;  
        
        $response["fullname"] = $fullname;
        $response["user_name"] = $user_name;
        $response["user_password"] = $user_password;
        $response["user_type"] = $user_type;
    }
    
    echo json_encode($response);
?>