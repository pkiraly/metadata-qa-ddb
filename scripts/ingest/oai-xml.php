<?php
define("LN", "\n");

require_once 'HTTP/Request2.php';

$baseOutputDir = $argv[1];
$set = $argv[2];
echo $set, LN;

$resumptionToken = '';
$doNext = TRUE;

$output_dir = $baseOutputDir . '/' . $set;
if (!file_exists($output_dir))
  mkdir($output_dir, 0777, true);

$time1 = new DateTime("now");
$i = 0;
$count = 0;
while ($doNext) {
  $doNext = TRUE;
  $response = getRespone($set, $resumptionToken);
  $ids = [];
  foreach ($response->xml->ListRecords->record as $record) {
    $rawId = (string) $record->header->identifier;
    $ids[] = str_replace('KBR', '', $rawId);
  }
  $count += count($ids);
  file_put_contents(sprintf("%s/%06d.xml", $output_dir, ++$i), $response->content);

  $resumptionToken = $response->xml->ListRecords->resumptionToken;
  $completeListSize = $resumptionToken['completeListSize'];
  $now = new DateTime("now");
  printf("%s request: %d, total: %d, ingested: %d, resumptionToken: %s\n",
    $now->diff($time1)->format('%d %H:%I:%S'), $i, $completeListSize, $count, $resumptionToken);

  if (is_null($resumptionToken) || empty($resumptionToken) || $resumptionToken == "")
    $doNext = FALSE;
}

$time2 = new DateTime("now");
$interval=  $time1->diff($time2);
echo 'DONE. It took ', $interval->format('%D %H:%I:%S'), LN;

function getRespone($set = '', $resumptionToken = '') {
  global $i;

  if ($resumptionToken == '') {
    $URL = sprintf('https://oai.deutsche-digitale-bibliothek.de/?verb=ListRecords&metadataPrefix=edm&set=%s', $set);
  } else {
    $URL = sprintf("https://oai.deutsche-digitale-bibliothek.de/?verb=ListRecords&resumptionToken=%s", $resumptionToken);
  }

  $trial = 0;

  $response = null;
  do {
    $response = getHttpRespone($URL);
    if ($response->status == 200) {
      $response->xml = simplexml_load_string($response->content) or die("Error: Cannot create object");
    } else {
      printf("%d) trial, i=%d\n", $trial, $i);
      echo 'status: ', $response->status, LN;
      echo 'error message: ', $response->error, LN;
      sleep(3);
      $trial++;
    }
  } while ($trial > 0 && $trial <= 3);
  return $response;
}

function getHttpRespone($url) {
  $request = new HTTP_Request2($url, HTTP_Request2::METHOD_GET);
  $response = (object)[
    'status' => '',
    'error' => '',
    'content' => '',
  ];

  try {
    $httpResponse = $request->send();
    $response->status = $httpResponse->getStatus();
    if ($httpResponse->getStatus() == 200) {
      $response->content = $httpResponse->getBody();
      // file_put_contents($output, $httpResponse->getBody() . LN, FILE_APPEND);
    } else {
      $response->error = sprintf('Unexpected HTTP status: %s %s', $httpResponse->getStatus(), $httpResponse->getReasonPhrase());
    }
  } catch (HTTP_Request2_Exception $e) {
    $response->error = sprintf('Error: %s', $e->getMessage());
  }

  return $response;
}

