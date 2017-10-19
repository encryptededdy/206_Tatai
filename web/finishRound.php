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

if (isset($_POST['username']) && isset($_POST['authID']) && isset($_POST['roundID']) && isset($_POST['score'])) {
    $username = $conn->real_escape_string($_POST['username']);
    $authID = $conn->real_escape_string($_POST['authID']);
    $roundid = $conn->real_escape_string($_POST['roundID']);
    $score = $conn->real_escape_string($_POST['score']);

    $result = $conn->query("SELECT * FROM users WHERE username='$username' AND authID='$authID' LIMIT 1");

    if (mysqli_num_rows($result) > 0) {
        // The user does exist and we're authenticated, so try to upload the score
        // Find whomst is whomst
        $usernames = $conn->query("SELECT hostName, clientName FROM challenge WHERE id='$roundid'");
        if (mysqli_num_rows($usernames) > 0) {
            $usernameRow = mysqli_fetch_array($usernames, MYSQLI_ASSOC);
            // If user is the host
            if ($usernameRow["hostName"] == $_POST['username']) {
                $otherName = $usernameRow["clientName"];
                $isHost = true;
            } else {
                $otherName = $usernameRow["hostName"];
                $isHost = false;
            }
        } else {
            exit(json_encode(array('status' => "OK", 'finished' => false, 'otherUser' => "", 'otherScore' => "")));
        }

        // Now we upload the score.
        if ($isHost) {
            $conn->query("UPDATE challenge SET hostScore='$score' WHERE id='$roundid'");
        } else {
            $conn->query("UPDATE challenge SET clientScore='$score' WHERE id='$roundid'");
        }

        // Real slow polling hours
        $count = 0;
        while ($count < 60) {
            if ($result2 = $conn->query("SELECT hostScore, clientScore FROM challenge WHERE id='$roundid'")) {
                if (mysqli_num_rows($result2) > 0) {
                    // Round found
                    $row = mysqli_fetch_array($result2, MYSQLI_ASSOC);
                    if ($row["hostScore"] != 0 && $row["clientScore"] != 0) {
                        // The game has finished!
                        if ($isHost) {
                            $otherScore = $row["clientScore"];
                        } else {
                            $otherScore = $row["hostScore"];
                        }
                        // Record the game as finished
                        $conn->query("UPDATE challenge SET finished=1, clientName='$username' WHERE id='$roundid'");
                        exit(json_encode(array('status' => "OK", 'finished' => true, 'otherUser' => $otherName, 'otherScore' => $otherScore)));
                    } else {
                        // otherwise...
                        sleep(1);
                        $count++;
                    }
                } else {
                    exit(json_encode(array('status' => "OK", 'finished' => false, 'otherUser' => "", 'otherScore' => "")));
                }
            } else {
                exit(json_encode(array('status' => "Error", 'finished' => false, 'otherUser' => "", 'otherScore' => "")));
            }
        }
        exit(json_encode(array('status' => "Timeout", 'finished' => false, 'otherUser' => "", 'otherScore' => "")));
    } else {
        // User doesn't exist or bad auth
        exit(json_encode(array('status' => "BadAuth", 'finished' => false, 'otherUser' => "", 'otherScore' => "")));
    }

} else {
    sendTelegram($ip." just made a bad request to finishRound.php");
    exit(json_encode(array('status' => "Bad", 'finished' => false, 'otherUser' => "", 'otherScore' => "")));
}
?>