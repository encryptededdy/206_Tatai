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

if (isset($_POST['version'])) {
    $version = $conn->real_escape_string($_POST['version']);

    // Limit to 10k results
    $result = $conn->query("SELECT data FROM arbdata WHERE version='$version' LIMIT 10000");

    if (mysqli_num_rows($result) > 0) {
        while ($row = mysqli_fetch_array($result, MYSQLI_ASSOC)) {
            $data[] = json_decode($row["data"]);
        }
        exit(json_encode(array('status' => "OK", 'data' => $data)));
    } else {
        // no entries
        exit(json_encode(array('status' => "OK", 'data' => null)));
    }

} else {
    exit(json_encode(array('status' => "Bad", 'data' => null)));
}
?>
