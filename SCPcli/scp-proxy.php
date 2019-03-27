<?
$m = new Memcached();
$m->addServer('localhost', 11211);

$cache_key = "scp-proxy-".$_GET['article'];

if (!$result = $m->get($cache_key))
{
	$result = @file_get_contents("http://www.scp-wiki.net.nyud.net/".$_GET['article']);

	if ($result)
	{
		$m->set($cache_key, $result, 604800);
	}	
}

$start = strpos($result, "<div id=\"page-content\">");
$end = strpos($result, "<div class=\"page-tags\">");
$length = $end-$start;

$result = substr($result, $start, $length);

$result = str_replace("</p>", "\n", $result);

$result = strip_tags($result);

$result = html_entity_decode($result);
$result = utf8_decode($result);

$end_rating_string = "&#8211;x";
$result = substr($result, strpos($result, $end_rating_string)+strlen($end_rating_string));

echo $result;

?>
