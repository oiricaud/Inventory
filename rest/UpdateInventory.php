<?php
    $host='localhost';
    $uname='narped5_admin';
    $pwd='02040816';
    $db="narped5_PhoTreBien";

    $con = mysql_connect($host,$uname,$pwd) or die("connection failed");
    mysql_select_db($db,$con) or die("db selection failed");
     
    $id=$_REQUEST['id'];
    $item=$_REQUEST['item'];
    $category=$_REQUEST['category'];
    $curr_qty=$_REQUEST['curr_qty'];
    $min_qty=$_REQUEST['min_qty'];
    $max_qty=$_REQUEST['max_qty'];
    $last_time_updated =$_REQUEST['last_time_updated'];
     
    $flag['code']=0;
     
    if($r=mysql_query("UPDATE inventory SET curr_qty = '$curr_qty', last_time_updated = '$last_time_updated'  WHERE id ='$id'",$con))
    {
        $flag['code']=1;
    }
     
    print(json_encode($flag));
    mysql_close($con);
?>