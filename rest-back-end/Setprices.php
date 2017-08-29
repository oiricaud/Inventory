<?php
    $connect = mysqli_connect("localhost", "narped5_admin", "02040816", "narped5_PhoTreBien");
    
    $item = $_POST["item"];
    $pricePerQty      = $_POST["pricePerQty"];
    $seller       = $_POST["seller"];

     function addItem() {
        global $connect, $item, $pricePerQty, $seller, $max_qty, $distributor, $price;
        $statement = mysqli_prepare($connect, "INSERT INTO prices (item, pricePerQty, seller) VALUES (?, ?, ?)");
        mysqli_stmt_bind_param($statement, "sss", $item, $pricePerQty, $seller);
        mysqli_stmt_execute($statement);
        mysqli_stmt_close($statement);     
    }
    function itemAvailable() {
        global $connect, $user_name;
        $statement = mysqli_prepare($connect, "SELECT * FROM prices WHERE item = ?"); 
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
    if (itemAvailable() ){
        addItem();
        $response["success"] = true;  
    }
    echo json_encode($response)
?>