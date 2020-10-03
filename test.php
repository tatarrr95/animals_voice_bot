<?php

    $template_file_name = 'test.docx';
 
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
            $message = str_replace("Hello",      "test",       $message);
            
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
?>