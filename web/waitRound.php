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
        $count = 0;
        while ($count < 60) {
            if ($result2 = $conn->query("SELECT started FROM challenge WHERE id='$roundid'")) {
                if (mysqli_num_rows($result2) > 0) {
                    // Round found
                    $row = mysqli_fetch_array($result2, MYSQLI_ASSOC);
                    if ($row["started"] == 1) {
                        // The game has started!
                        exit(json_encode(array('status' => "OK", 'started' => true)));
                    } else {
                        // otherwise...
                        sleep(1);
                        $count++;
                    }
                } else {
                    exit(json_encode(array('status' => "OK", 'started' => false)));
                }
            } else {
                exit(json_encode(array('status' => "Error", 'started' => false)));
            }
        }
        exit(json_encode(array('status' => "Timeout", 'started' => false)));
    } else {
        // User doesn't exist or bad auth
        exit(json_encode(array('status' => "BadAuth", 'started' => false)));
    }

} else {
    sendTelegram($ip." just made a bad request to waitRound.php");
    exit(json_encode(array('status' => "Bad", 'started' => false)));
}
?>