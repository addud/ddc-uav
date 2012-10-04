<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0
   Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="content-type" content="text/html;
    charset=utf-8"/>
    <title>Navigation panel for UAV</title>
    <style type="text/css">
		@import "domtab.css";
		@import "table.css";
    </style>
    <style type="text/css">
        styled-button {
	-webkit-box-shadow: rgba(0, 0, 0, 0.2) 0px 1px 0px 0px;
	background-color: #7cceee;
	border-radius: 5px;
	border-bottom-color: #333;
	border: 1px solid #61c4ea;
	box-shadow: rgba(0, 0, 0, 0.2) 0px 1px 0px 0px;
	color: #333;
	font-family: 'Verdana', Arial, sans-serif;
	font-size: 14px;
	text-shadow: #b2e2f5 0px 1px 0px;
	padding: 5px;
         }
</style>
<!--[if gt IE 6]>
	<style type="text/css">
		html>body ul.domtabs a:link,
		html>body ul.domtabs a:visited,
		html>body ul.domtabs a:active,
		html>body ul.domtabs a:hover{
			height:3em;
		}
	</style>
<![endif]-->
    <script type="text/javascript" src="domtab.js"></script>
    <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=AIzaSyDI7AL_CmjTMZNBea4M7VSEA45mV54101A"
      type="text/javascript"></script>
    <script type="text/javascript" src="jquery.js"></script>
    <script type="text/javascript" src="ajaxfileupload.js"></script>

    <script type="text/javascript">
    //<![CDATA[

function displayData()
{
 $("#result").load("displayData.php");
}

function ajaxFileUpload()
	{
		$("#loading")
		.ajaxStart(function(){
			$(this).show();
		})
		.ajaxComplete(function(){
			$(this).hide();
		});

		$.ajaxFileUpload
		(
			{
				url:'doajaxfileupload2.php',
				secureuri:false,
				fileElementId:'fileToUpload',
				dataType: 'json',
				data:{name:'logan', id:'id'},
				success: function (data, status)
				{
					if(typeof(data.error) != 'undefined')
					{
						if(data.error != '')
						{
							alert(data.error);
						}else
						{
							/*alert(data.msg);*/
						}
					}
				displayData();
				},
				error: function (data, status, e)
				{
					alert(e);
				}
			}
		)
		
		return false;

	}




	function fc(point,map,markerList)
	{
 	var html = "";
 	html += html + "<font color='black'><b>Latitude, Longitude</b><br>" + point.toUrlValue(6)+"</font>";

 	var baseIcon = new GIcon();
 	baseIcon.iconSize=new GSize(32,37);
 	/*baseIcon.shadowSize=new GSize(56,32);*/
 	baseIcon.iconAnchor=new GPoint(16,34);
 	baseIcon.infoWindowAnchor=new GPoint(16,10);
 	/*var thisicon = new GIcon(baseIcon, "http://itouchmap.com/i/blue-dot.png", null, null);*/
	if (document.getElementById('specify_label').innerHTML=='ON')
	{
	var thisicon = new GIcon(baseIcon, "http://localhost/images/airport_terminal_red.png", null, null);
	document.getElementById('specify_label').innerHTML='OFF';
	document.getElementById('waypoints_label').innerHTML='ON';
	}
	else
	{
	var thisicon = new GIcon(baseIcon, "http://localhost/images/helicopter.png", null, null);
	document.getElementById('specify_label').innerHTML='OFF';
	document.getElementById('waypoints_label').innerHTML='ON';
	}
 	var marker = new GMarker(point,thisicon);
 	GEvent.addListener(marker, "click", function() {marker.openInfoWindowHtml(html);});
 	map.addOverlay(marker);
	markerList.push(marker);
	}
function fc2(point,status,picture,data,waypoint)
	{
 	var html = "";
 	html += html + "<font color=black><b>Latitude, Longitude:</b><br>" + point.toUrlValue(6)+
			"<br>Picture:<br><img width='30%' src="+"http://localhost/new/upload/"+picture+"></img>"+
			"<br>Data:"+data+
			"<br>Altitude:"+waypoint.altitude+
			"<br>Tag:"+waypoint.tag+
			"<br>Hold Time:"+waypoint.holdtime+
			"<br>Tolerance Radius:"+waypoint.toleranceradius+"</font><br>";
	
 	var baseIcon = new GIcon();
 	baseIcon.iconSize=new GSize(32,37);
 	/*baseIcon.shadowSize=new GSize(56,32);*/
 	baseIcon.iconAnchor=new GPoint(16,34);
 	baseIcon.infoWindowAnchor=new GPoint(16,10);
 	/*var thisicon = new GIcon(baseIcon, "http://itouchmap.com/i/blue-dot.png", null, null);*/
	if (status=='base')
	{
	var thisicon = new GIcon(baseIcon, "http://localhost/images/airport_terminal_red.png", null, null);
	document.getElementById('specify_label').innerHTML='OFF';
	document.getElementById('waypoints_label').innerHTML='ON';
	}
	else
	{
	var thisicon = new GIcon(baseIcon, "http://localhost/images/helicopter.png", null, null);
	document.getElementById('specify_label').innerHTML='OFF';
	document.getElementById('waypoints_label').innerHTML='ON';
	}
 	var marker = new GMarker(point,thisicon);
 	GEvent.addListener(marker, "click", function() {marker.openInfoWindowHtml(html);});
 	map.addOverlay(marker);
	markerList.push(marker);
	}
	function findVertex(point)
	{

		for(i=0;i<polyline.getVertexCount();i++)
			{
			/*alert('coordinate point:('+point.lng()+','+point.lat()+',('+getVertex(i).lng()+','+point.lat()+')');*/
				if(point.lat()==polyline.getVertex(i).lat() && point.lng()==polyline.getVertex(i).lng())
				{
					/*alert('index'+i);*/
					return i;
				}
			}
		return -1;
	}

	function findMarker(vertex)
	{

		for (var i=0;i<markerList.length;i++)
			{
				/*alert((markerList[i] instanceof GMarker));*/
				if(vertex.lat()==markerList[i].getLatLng().lat() && vertex.lng()==markerList[i].getLatLng().lng())
				{return markerList[i];}
			}
		return -1;
	}


 var map,polyline, markerList,waypointList;
    function load() {
      displayData();
      if (GBrowserIsCompatible()) {
        /*var map = new GMap2(
        document.getElementById("map"));*/
	map = new GMap2(
        document.getElementById("map"));
        map.addControl(new GSmallMapControl());
        /*map.setCenter(new GLatLng(37.4419, -122.1419), 13);*/
	map.setCenter(new GLatLng(59.8586, 17.6389), 13);
	var points = [];
	markerList=[];
	waypointList = new Array();
	polyline = new GPolyline(points,"#00ff00",5);
	map.addOverlay(polyline);
	var baseMarker;
       GEvent.addListener(map, 'click', function(overlay,point) {
		/*alert('coordinate:'+point.lng()+','+point.lat());*/
		/*points.push(new GLatLng(point.lat(), point.lng()));*/
		
		if (findVertex(point)==-1)
		{
		//alert("heyson");
		/*inputbox ask for other tasks*/
			var altitude=prompt("Please enter altitude:","");
			var tag=prompt("Please enter tag","");
			var holdtime=prompt("Please enter hold time","");
			var toleranceradius=prompt("Please enter tolerance radius","");	

			var waypoint = {
		    	altitude: "",
		    	tag: "",
		    	holdtime:"",
		    	toleranceradius:"",
		    	getInfo: function () {
	           				return "waypoint_test";
    						 }
			    };
			waypoint.altitude=altitude;
			waypoint.tag=tag;
			waypoint.holdtime=holdtime;
			waypoint.toleranceradius=toleranceradius;
		
		if (document.getElementById('specify_label').innerHTML=='ON')
		{
	
			if (polyline.getLength()!=0)
			{
				var marker=findMarker(polyline.getVertex(0));
				if (marker != -1)
					{map.removeOverlay(marker);}
			}
			waypointList[0]=waypoint;
			polyline.deleteVertex(0);
			polyline.insertVertex(0, new GLatLng(point.lat(), point.lng()));

		}
		else
		{
			waypointList.push(waypoint);
			polyline.insertVertex(polyline.getVertexCount(), new GLatLng(point.lat(), point.lng()));
		}
			
		fc(point,map,markerList);
		map.setCenter(point);
		}
		}
		);

	GEvent.addListener(map, 'singlerightclick', function(point,src,overlay) {
	
		if (overlay) 
		{
		var index=findVertex(overlay.getLatLng());
		polyline.deleteVertex(index);
		if(index!=-1) {markerList.splice(index, 1);waypointList.splice(index,1);}		
		map.removeOverlay(overlay);
		
		/*delete points[new GLatLng(point.lat(), point.lng())];*/
	
		/*document.getElementById("latbox").value='';*/
		/*document.getElementById("latboxm").value='';*/
		}
		else 
		{}
		});
	/*
        function createMarker(point, text, title) {
          var marker =
          new GMarker(point,{title:title});
          GEvent.addListener(
          marker, "click", function() {
            marker.openInfoWindowHtml(text);
          });	
          return marker;
        }
	
        <?php
        $points = Array(
        1 => "37.4389, -122.1389",
        2 => "37.4419, -122.1419",
        3 => "37.4449, -122.1449");
        foreach ($points as $key => $point) {
        ?>
        var marker = createMarker(
        new GLatLng(<?php echo $point ?>),
        'Marker text <?php echo $key ?>',
        'Example Title text <?php echo $key ?>');
        map.addOverlay(marker);
        <?php } ?>
	*/
      }
    }

function createXmlDocument(string)
{
    var doc;
    if (window.DOMParser)
    {
        parser = new DOMParser();
        doc = parser.parseFromString(string, "application/xml");
    }
    else // Internet Explorer
    {
        doc = new ActiveXObject("Microsoft.XMLDOM");
        doc.async = "false";
        doc.loadXML(string); 
    }
    return doc;
}


// Simple helper function creates a new element from a name, so you don't have to add the brackets etc.
$.createElement = function(name)
{
    return $('<'+name+' />');
};

// JQ plugin appends a new element created from 'name' to each matched element.
$.fn.appendNewElement = function(name)
{
    this.each(function(i)
    {
        $(this).append('<'+name+' />');
    });
    return this;
}

function getCoordinates()
{
var $root = $('<XMLDocument />');
var $coordinates = $.createElement('coordinates');
//alert(markerList.length);
for (var i=0;i<markerList.length;i++)
	{
		var $newCoordinate = $.createElement('coordinate');
		$newCoordinate.append($('<id />').text(i));
		$newCoordinate.append($.createElement('lat').text(markerList[i].getLatLng().lat()));
		$newCoordinate.append($.createElement('lng').text(markerList[i].getLatLng().lng()));
		$newCoordinate.append($.createElement('picture').text(''));
		$newCoordinate.append($.createElement('data').text(''));
		$newCoordinate.append($.createElement('altitude').text(waypointList[i].altitude));
		$newCoordinate.append($.createElement('tag').text(waypointList[i].tag));
		$newCoordinate.append($.createElement('holdtime').text(waypointList[i].holdtime));
		$newCoordinate.append($.createElement('toleranceradius').text(waypointList[i].toleranceradius));
		$coordinates.append($newCoordinate);
	}
$root.append($coordinates);
//alert($root.html());
$(window.location).attr('href', 'getXML2.php?filename=UAV&content='+$root.html());
//$("#result").load('getXML2.php?filename=dsad&content=sdfsdf');
//$.get("getXML.php",{filename: "john.txt", content: "merhaba" },
//function(data) {
//     alert("Data Loaded: " + data);
//   }
//);
}

function putCoordinates(fileName)
{
//alert(fileName);
//alert(polyline);
map.clearOverlays();
//alert(polyline);

var points = [];
markerList=[];
polyline = new GPolyline(points,"#00ff00",5);
map.addOverlay(polyline);
$.ajax({
        type: "GET",
	url: "upload/"+fileName,
	dataType: "xml",
	success: function(xml) {
		//alert('file fetched!');
		$(xml).find('coordinate').each(function(){
			var id = parseInt($(this).find('id').text());
 			var lat = parseFloat($(this).find('lat').text());
 			var lng = parseFloat($(this).find('lng').text());
		 	var mypicture = $(this).find('picture').text();
			var mydata = $(this).find('data').text();
                        var waypoint = {
		    	altitude: "",
		    	tag: "",
		    	holdtime:"",
		    	toleranceradius:"",
		    	getInfo: function () {
	           				return "waypoint_test";
    				             }
			};
			waypoint.altitude=$(this).find('altitude').text();
			waypoint.tag=$(this).find('tag').text();
			waypoint.holdtime=$(this).find('holdtime').text();
			waypoint.toleranceradius=$(this).find('toleranceradius').text();
			
			//alert(lat+"-"+lng+"-"+mydata);	 
			base='normal';			
			if (id==0){base='base';}		
			fc2(new GLatLng(lat,lng),base,mypicture,mydata,waypoint);
			waypointList.push(waypoint);
			polyline.insertVertex(id, new GLatLng(lat, lng));
		}); 
	}
});



}

function removeFile(fileName)
{
$.get("doajaxfiledelete.php",{filename: fileName+""});
displayData();
}
function myupload(){
$('.input').fileUploader();return false;
}
    //]]>
    </script>
  </head>


  <body onload="load()" onunload="GUnload()">

<div id="other" class="domtab doprevnext" style="position: fixed; top: 10px; left: 10px;width:400px">
	<ul class="domtabs">
		<li><a href="#t1">Plan Route & Send to UAV&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a></li>
		<li><a href="#t2">Load data from UAV and show</a></li>
	</ul>
	<div>
		<h2><a name="t1" id="t1">1.Specify Base</a></h2>
		<p>you are able to decide where UAV takes off/lands with left click, if Specify Base status is ON.</p>
		<button type="button" id="specify" class="styled-button" value="specify_start" onclick="if(document.getElementById('specify_label').innerHTML=='ON'){document.getElementById('specify_label').innerHTML='OFF';document.getElementById('waypoints_label').innerHTML='ON'} else {document.getElementById('specify_label').innerHTML='ON';document.getElementById('waypoints_label').innerHTML='OFF'}">Specify Base:<label id="specify_label">ON</label></button>
		<h2><a name="t1" id="t1">2.Specify waypoints</a></h2>
		<p>you are able to draw waypoints with left click if Specify Waypoints status is ON and remove waypoints with right click.</p>
		<button type="button" id="specify" class="styled-button" disabled="disabled" value="specify_start">Specify waypoints:<label id="waypoints_label">OFF</label></button>
		<h2><a name="t1" id="t1">3.Get coordinates in UAV's format</a></h2>
		<p>you are able to obtain coordinates of the waypoints you specified in UAV's format</p>
		<button type="button" id="coordinate" class="styled-button" onclick="getCoordinates();">Get Coordinates</button>
	</div>
	<div>
		<h2><a name="t2" id="t2">Upload Data</a></h2>
		<p>you are able to upload data from UAV and display it on the map.</p>
		<!--<form id="myForm" enctype="multipart/form-data" action="map.php" method="POST">
 			Please choose a file: <input name="uploaded" type="file" /><br />
 			<input name="submit" type="submit" value="Upload" />
 		</form>-->
		
		<form name="myform" action="doajaxfileupload2.php" method="POST" enctype="multipart/form-data">
			<input type="file" size="45" name="fileToUpload" id="fileToUpload" class="input">
			<button class="button" id="buttonUpload" onclick="return ajaxFileUpload();" >Upload</button>
		</form>
		<h2><a name="t2" id="t2">Display Data</a></h2>
		<p>you are able to display data on the map which UAV collected.</p>
		<p id="result" style="overflow: extend;height:250px;overflow-x: hidden;"></p>
		<p><a href="#top">back to menu</a></p>
	</div>
</div>
     <div id="outDIV" style="position:fixed; top:10px;left:435px;background-color: #669; padding-left: 30px;padding-right:30px;padding-bottom:30px;">
	<p style="height:28px;margin-left:-196px;background-color: #669;"><input onclick="this.value='';" onkeypress="if ((window.event) &&  (window.event.keyCode==13) || e.which==13){var word=this.value.split(',');map.setCenter(new GLatLng(parseFloat(word[0]),parseFloat(word[1])));}" type="text" value="Enter lat,lng"/></p>
    <div id="map" style="width: 705px; height: 511px; top: 0px;"></div></div>
  </body>
</html>


