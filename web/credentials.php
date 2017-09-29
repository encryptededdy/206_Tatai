<?php
$servername = "localhost";
$username = "tatai";
$password = "xxx";
$dbname = 'tatai';

$telegramURL = 'https://api.telegram.org/xxx/sendMessage';

function sendTelegram($text) {
    global $telegramURL;
    // Source: https://stackoverflow.com/questions/5647461/how-do-i-send-a-post-request-with-php
    $fields = array(
        'chat_id'=>"xxx",
        'text'=>($text));

    $options = array(
        'http' => array(
            'header'  => "Content-Type: application/json\r\n",
            'method'  => 'POST',
            'content' => json_encode($fields)
        )
    );
    $context  = stream_context_create($options);
    $result = file_get_contents($telegramURL, false, $context);
}
?>