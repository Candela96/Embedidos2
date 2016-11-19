<?php
$host="localhost";
$user="root";
$pass="";
$dbname="tacos";
$db = new mysqli($host, $user, $pass, $dbname);


$sql="SELECT id,Nombre,Telefono,Grados,Latitud FROM taquerias where Nombre = ?";
$searchName = $_GET['nombre'];

if($stmt = $db->prepare($sql)){
    $stmt->bind_param('s',$searchName);
    $stmt->execute();
    $result = $stmt->get_result();
    $json = array();	
    while($row = $result->fetch_assoc()){
	$json[]=$row;

    }

    $stmt->free_result();
    $stmt->close();
    
    echo json_encode($json);

}else die("Failed to prepare!");

?>