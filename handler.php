<?php
    $fp = fopen("php://input", 'r+');
    file_put_contents($hash.'.txt', $fp);
    fclose($fp);
    $body = file_get_contents("https://sms.ru/sms/send?api_id=c9817452-91ee-0b54-d145-f890edd9420b&to=79146627877&msg=привет"."&json=1"); # Если приходят крякозябры, то уберите iconv и оставьте только urlencode("Привет!")
    $json = json_decode($body);
    print_r($json); // Для дебага
?>