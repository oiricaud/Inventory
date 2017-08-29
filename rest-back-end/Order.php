<?php
    $connect = mysqli_connect("localhost", "narped5_admin", "02040816", "narped5_PhoTreBien");
    
    $item             = $_POST["item"];
    $distributor      = $_POST["distributor"];
    $price            = $_POST["price"];
    $qty              = $_POST["qty"];
    $place_order_time = $_POST["place_order_time"];
    $ticket_number    = $_POST["ticket_number"];
    $order_status           = $_POST["order_status"];
    $who_placed_order = $_POST["who_placed_order"];


     function addItem() {
        global $connect,
               $item, 
               $distributor, 
               $price, 
               $qty, 
               $place_order_time, 
               $ticket_number,
               $order_status,
               $who_placed_order;

        $statement = mysqli_prepare($connect, 
            "INSERT INTO myorder (item, distributor, price, qty, place_order_time, ticket_number, order_status, who_placed_order) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        mysqli_stmt_bind_param($statement, "ssssssss", $item, $distributor, $price, $qty, $place_order_time, $ticket_number, $order_status, $who_placed_order);
        mysqli_stmt_execute($statement);
        mysqli_stmt_close($statement);     
    }
    function itemAvailable() {
        global $connect, $user_name;
        $statement = mysqli_prepare($connect, "SELECT * FROM myorder WHERE item = ?"); 
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