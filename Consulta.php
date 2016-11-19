<?php
$host="localhost";
$username="root";
$password="";
$db_name="tacos";


$con=@mysql_connect($host, $username, $password)or die("connot connect");

mysql_select_db($db_name)or die("cannot select DB");

$sql="SELECT id,Nombre,Telefono,Grados,Latitud FROM taquerias";
$result = mysql_query($sql,$con);
$json = array();

if(mysql_num_rows($result)){
 while($row=mysql_fetch_object($result)){
   $json[]=$row;
 }

}

mysql_close($con);
echo json_encode($json);
?>