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

if (isset($_POST['username']) && isset($_POST['authID']) && isset($_POST['gamemode']) && isset($_POST['highscore'])) {
    $username = $conn->real_escape_string($_POST['username']);
    $authID = $conn->real_escape_string($_POST['authID']);
    $gamemode = $conn->real_escape_string($_POST['gamemode']);
    $highscore = $conn->real_escape_string($_POST['highscore']);

    $result = $conn->query("SELECT * FROM users WHERE username='$username' AND authID='$authID' LIMIT 1");

    if (mysqli_num_rows($result) > 0) {
        // The user does exist and we're authenticated, so try to insert the high score
        if ($conn->query("INSERT into highscores (username, gamemode, score) VALUES ('$username', '$gamemode', '$highscore') ON DUPLICATE KEY UPDATE score = GREATEST(score, VALUES(score))")) {
            // Send telegram msg
            sendTelegram($ip." just uploaded a score for user ".$username." (mode: ".$gamemode."), which was accepted. Uploaded score: ".$highscore);
            exit(json_encode(array('status' => "OK", 'saved' => true)));
        } else {
            sendTelegram($ip." just tried to upload a score for user ".$username.", which was declined (SQL Error)");
            exit(json_encode(array('status' => "Error", 'saved' => false)));
        }
    } else {
        // User doesn't exist or bad auth
        sendTelegram($ip." just tried to upload a score for user ".$username.", which was declined (user doesn't exist or bad authID)");
        exit(json_encode(array('status' => "BadAuth", 'saved' => false)));
    }

} else {
    sendTelegram($ip." just made a bad request to uploadHighScore.php");
    exit(json_encode(array('status' => "Bad", 'saved' => false)));
}
?>