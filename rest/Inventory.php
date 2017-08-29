<?php
    $connect = mysqli_connect("localhost", "narped5_admin", "02040816", "narped5_PhoTreBien");
    
    $item           = $_POST["item"];
    $category       = $_POST["category"];
    $curr_qty       = $_POST["curr_qty"];
    $min_qty        = $_POST["min_qty"];
    $max_qty        = $_POST["max_qty"];
    $last_time_updated    = $_POST["last_time_updated"];


     function addItem() {
        global  $connect, 
                $item, 
                $category,
                $curr_qty,
                $min_qty,
                $max_qty,
                $last_time_updated;

        $statement = mysqli_prepare($connect,
                     "INSERT INTO inventory (item, category, curr_qty, min_qty, max_qty, last_time_updated) 
                                     VALUES (?,    ?,        ?,        ?,       ?,        ?)");
        mysqli_stmt_bind_param($statement, "ssssss", $item, $category, $curr_qty, $min_qty, $max_qty, $last_time_updated);
        mysqli_stmt_execute($statement);
        mysqli_stmt_close($statement);     
    }
    function itemAvailable() {
        global $connect, $user_name;
        $statement = mysqli_prepare($connect, "SELECT * FROM inventory WHERE item = ?"); 
        mysqli_stmt_bind_param($statement, "s", $item);
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
    if (itemAvailable()){
        addItem();
        $response["success"] = true;  
    }
    echo json_encode($response)
?>