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

if (isset($_POST['username']) && isset($_POST['authID'])) {
    $username = $conn->real_escape_string($_POST['username']);
    $authID = $conn->real_escape_string($_POST['authID']);

    $result = $conn->query("SELECT * FROM users WHERE username='$username' AND authID='$authID' LIMIT 1");

    if (mysqli_num_rows($result) > 0) {
        // The user does exist and we're authenticated
        exit(json_encode(array('status' => "OK", 'authOK' => true)));
    } else {
        // User doesn't exist or bad auth
        exit(json_encode(array('status' => "OK", 'authOK' => false)));
    }

} else {
    sendTelegram($ip." just made a bad request to uploadHighScore.php");
    exit(json_encode(array('status' => "Bad", 'authOK' => false)));
}
?>