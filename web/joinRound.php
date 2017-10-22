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

if (isset($_POST['username']) && isset($_POST['authID']) && isset($_POST['roundID'])) {
    $username = $conn->real_escape_string($_POST['username']);
    $authID = $conn->real_escape_string($_POST['authID']);
    $roundid = $conn->real_escape_string($_POST['roundID']);

    $result = $conn->query("SELECT * FROM users WHERE username='$username' AND authID='$authID' LIMIT 1");

    if (mysqli_num_rows($result) > 0) {
        // The user does exist and we're authenticated, so try to join the round
        if ($result2 = $conn->query("SELECT questionSet, hostName FROM challenge WHERE id='$roundid' AND started=0")) {
            if (mysqli_num_rows($result2) > 0) {
                $row = mysqli_fetch_array($result2, MYSQLI_ASSOC);
                // Send telegram msg
                sendTelegram($ip." just joined a round with user ".$username.", which was accepted. Round:".$roundid);
                $conn->query("UPDATE challenge SET started=1, clientName='$username' WHERE id='$roundid'");
                exit(json_encode(array('status' => "OK", 'started' => true, 'host' => $row["hostName"], 'json' => json_decode($row["questionSet"]))));
            } else {
                exit(json_encode(array('status' => "OK", 'started' => false, 'host' => null, 'json' => null)));
            }
        } else {
            sendTelegram($ip." just tried to start a round for user ".$username.", which was declined (SQL Error)");
            exit(json_encode(array('status' => "Error", 'started' => false, 'host' => null, 'json' => null)));
        }
    } else {
        // User doesn't exist or bad auth
        sendTelegram($ip." just tried to start a round for user ".$username.", which was declined (user doesn't exist or bad authID)");
        exit(json_encode(array('status' => "BadAuth", 'started' => false, 'host' => null, 'json' => null)));
    }

} else {
    sendTelegram($ip." just made a bad request to joinRound.php");
    exit(json_encode(array('status' => "Bad", 'started' => false, 'host' => null, 'json' => null)));
}
?>