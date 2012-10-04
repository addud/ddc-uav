<?php
// open this directory 
$myDirectory = opendir("upload/");

// get each entry
//while($entryName = readdir($myDirectory)) {
//	$dirArray[] = $entryName;
//}
while($entryName = readdir($myDirectory)) {
	$dirArray[$entryName]=date("F d Y H:i",filemtime("upload/".$entryName));
}


// close directory
closedir($myDirectory);

//	count elements in array
$indexCount	= count($dirArray);

// sort 'em
arsort($dirArray);

// print 'em
print("<TABLE border=0 cellpadding=5 cellspacing=0 class=sofT>\n");
print("<TR><TH>Filename</TH><th>Date</th><th>Display</th><th>Remove</th></TR>\n");
// loop through the array of files and print them all

foreach($dirArray as $key=>$value){
 	if (substr("$key", 0, 1) != "." && stristr($key, '.xml') != FALSE){ // don't list hidden files
		$tildaKey='"'.$key.'"';
		print("<TR><TD class=helpHed>$key</td>");
		print("<td class=helpHed>");
		print(date("F d Y H:i",filemtime("upload/".$key)));
		print("</td>");
		print("<td><button class='button' onclick='putCoordinates($tildaKey);'>display</button></td>");
		print("<td><button class='button' onclick='removeFile($tildaKey);'>remove</button></td>");
		print("</TR>\n");
	}

} 
/*
for($index=0; $index < $indexCount; $index++) {
        if (substr("$dirArray[$index]", 0, 1) != "."){ // don't list hidden files
		print("<TR><TD class=helpHed>$dirArray[$index]</td>");
		print("<td class=helpHed>");
		print(date("F d Y H:i",filemtime("upload/".$dirArray[$index])));
		print("</td>");
		print("<td><button class='button'>display</button></td>");
		print("</TR>\n");
	}

}*/
print("</TABLE>\n");
?>
