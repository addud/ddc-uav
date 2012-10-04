<?php
if(empty($_GET['filename']) || empty($_GET['content'])){
	exit;
}

// Sanitizing the filename:
$content = $_GET['content'];
$filename = preg_replace('/[^a-z0-9\-\_\.]/i','',$_GET['filename']);

// Outputting headers:
header("Cache-Control: ");
header("Content-Type:text/xml");
header('Content-Disposition: attachment; filename="'.$filename.'.xml"');

echo $_GET['content'];
?>
