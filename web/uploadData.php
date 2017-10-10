<?php
// Get the MySQL credentials
include 'credentials.php';

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if (mysqli_connect_errno()) {
    die("Connection failed: " . mysqli_connect_error());
}
$ip = $conn->real_escape_string($_SERVER['REMOTE_ADDR']);

if (isset($_POST['version']) && isset($_POST['data'])) {
    $version = $conn->real_escape_string($_POST['version']);
    $data = $conn->real_escape_string($_POST['data']);

    if ($conn->query("INSERT into arbdata (version, data) VALUES ('$version', '$data')")) {
        // Send telegram msg
        //sendTelegram($ip." just uploaded a score for user ".$username." (mode: ".$gamemode."), which was accepted. Uploaded score: ".$highscore);
        exit(json_encode(array('status' => "OK", 'saved' => true)));
    } else {
        //sendTelegram($ip." just tried to upload a score for user ".$username.", which was declined (SQL Error)");
        exit(json_encode(array('status' => "Error", 'saved' => false)));
    }

} else {
    sendTelegram($ip." just made a bad request to uploadData.php");
    exit(json_encode(array('status' => "Bad", 'saved' => false)));
}
?>