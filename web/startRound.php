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

if (isset($_POST['username']) && isset($_POST['authID']) && isset($_POST['questionset'])) {
    $username = $conn->real_escape_string($_POST['username']);
    $authID = $conn->real_escape_string($_POST['authID']);
    $questionset = $conn->real_escape_string($_POST['questionset']);

    $result = $conn->query("SELECT * FROM users WHERE username='$username' AND authID='$authID' LIMIT 1");

    if (mysqli_num_rows($result) > 0) {
        // The user does exist and we're authenticated, so try to start the round
        if ($conn->query("INSERT into challenge (questionSet) VALUES ('$questionset')")) {
            // Send telegram msg
            sendTelegram($ip." just started a round for user ".$username.", which was accepted. JSON:".$questionset);
            exit(json_encode(array('status' => "OK", 'id' => $conn->insert_id)));
        } else {
            sendTelegram($ip." just tried to start a round for user ".$username.", which was declined (SQL Error)");
            exit(json_encode(array('status' => "Error", 'id' => -1)));
        }
    } else {
        // User doesn't exist or bad auth
        sendTelegram($ip." just tried to start a round for user ".$username.", which was declined (user doesn't exist or bad authID)");
        exit(json_encode(array('status' => "BadAuth", 'id' => -1)));
    }

} else {
    sendTelegram($ip." just made a bad request to startRound.php");
    exit(json_encode(array('status' => "Bad", 'id' => -1)));
}
?>