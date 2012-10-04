<?php
if(empty($_GET['filename'])){
	exit;
}
unlink("upload/".$_GET['filename']);

