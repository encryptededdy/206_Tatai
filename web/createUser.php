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

if (isset($_POST['username'])) {
    $username = $conn->real_escape_string($_POST['username']);
    $authID = $conn->real_escape_string(md5(microtime().rand()));

    $result = $conn->query("SELECT * FROM users WHERE username='$username' LIMIT 1");

    if (mysqli_num_rows($result) > 0) {
        sendTelegram($ip." just tried to create a user ".$username.", which was declined (Duplicate)");
        exit(json_encode(array('status' => "Duplicate", 'created' => false, 'authID' => "")));
    }

    if ($conn->query("INSERT into users (username, authID, registerIP) VALUES ('$username', '$authID', '$ip')")) {
        // Send telegram msg
        sendTelegram($ip." just created a user ".$username.", which was accepted. Generated authID: ".$authID);

        exit(json_encode(array('status' => "OK", 'created' => true, 'authID' => $authID)));
    } else {
        sendTelegram($ip." just tried to create a user ".$username.", which was declined (SQL Error)");
        exit(json_encode(array('status' => "Error", 'created' => false, 'authID' => "")));
    }
} else {
    sendTelegram($ip." just made a bad request to createUser.php");
    die(json_encode(array('status' => "Bad", 'created' => false, 'authID' => "")));
}
?>