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

if (isset($_POST['gamemode'])) {
    $gamemode = $conn->real_escape_string($_POST['gamemode']);

    $result = $conn->query("SELECT username, lastupdate, score FROM highscores WHERE gamemode='$gamemode' ORDER BY score DESC LIMIT 20");

    $place = 1;
    if (mysqli_num_rows($result) > 0) {
        while ($row = mysqli_fetch_array($result, MYSQLI_ASSOC)) {
            // Custom key naming and format conversion
            $acqRow["place"] = $place;
            $acqRow["username"] = $row["username"];
            $acqRow["score"] = $row["score"];
            $acqRow["datetime"] = strtotime($row["lastupdate"]);
            $data[] = $acqRow;
            $place++;
        }
        exit(json_encode(array('status' => "OK", 'scores' => $data)));
    } else {
        // Only one entry
        exit(json_encode(array('status' => "OK", 'scores' => null)));
    }

} else {
    exit(json_encode(array('status' => "Bad", 'scores' => null)));
}
?>
