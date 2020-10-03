<?php
    $hash = bin2hex(random_bytes(16));
    $fp = fopen("php://input", 'r+');
    file_put_contents($hash.'.txt', $fp);
    fclose($fp);
    $post_request = file_get_contents($hash.'.txt');
    $phone = $post_request['phone'];

    $template_file_name = 'template.docx'; 
    $rand_no = rand(111111, 999999);
    $fileName = "results_" . $rand_no . ".docx";
    $folder   = "results_";
    $full_path = $folder . '/' . $fileName;
    try
    {
        if (!file_exists($folder))
        {
            mkdir($folder);
        }       
            
        //Copy the Template file to the Result Directory
        copy($template_file_name, $full_path);
    
        // add calss Zip Archive
        $zip_val = new ZipArchive;
    
        //Docx file is nothing but a zip file. Open this Zip File
        if($zip_val->open($full_path) == true)
        {
            // In the Open XML Wordprocessing format content is stored.
            // In the document.xml file located in the word directory.
            
            $key_file_name = 'word/document.xml';
            $message = $zip_val->getFromName($key_file_name);                
                        
            $timestamp = date('d-M-Y H:i:s');
            
            // this data Replace the placeholders with actual values
            $message = str_replace("Zayav_text",      $post_request['free_text'],       $message);
            
            //Replace the content with the new content created above.
            $zip_val->addFromString($key_file_name, $message);
            $zip_val->close();
        }
    }
    catch (Exception $exc) 
    {
        $error_message =  "Error creating the Word Document";
        var_dump($exc);
    }

    $ch = curl_init("https://sms.ru/sms/send");
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_TIMEOUT, 30);
    curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(array(
        "api_id" => "c9817452-91ee-0b54-d145-f890edd9420b",
        "to" => "79025786850", // До 100 штук до раз
        "msg" => "Ваше заявление здесь https://alto-ai.ru/animal_get_doc?docname=".$fileName."&town=".$post_request['town'], // Если приходят крякозябры, то уберите iconv и оставьте только "Привет!",
        /*
        // Если вы хотите отправлять разные тексты на разные номера, воспользуйтесь этим кодом. В этом случае to и msg нужно убрать.
        "multi" => array( // до 100 штук за раз
            "79146627877"=> iconv("windows-1251", "utf-8", "Привет 1"), // Если приходят крякозябры, то уберите iconv и оставьте только "Привет!",
            "74993221627"=> iconv("windows-1251", "utf-8", "Привет 2") 
        ),
        */
        "json" => 1 // Для получения более развернутого ответа от сервера
    )));
    $body = curl_exec($ch);
    curl_close($ch);

    // $phone = "";
    // $msg = "";
    // $body = file_get_contents("https://sms.ru/sms/send?api_id=c9817452-91ee-0b54-d145-f890edd9420b&to=".$phone."&msg=Ваше заявление здесь https://alto-ai.ru/animal_get_doc?docname=".$fileName."&town=".$post_request['town']."&json=1"); # Если приходят крякозябры, то уберите iconv и оставьте только urlencode("Привет!")
    // $json = json_decode($body);
    // print_r($json); // Для дебага
?>