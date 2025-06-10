<?php
define('LN', "\n");

if ($argc < 3) {
  die(sprintf('usage: php %s <csvFile> <tableName>', $argv[0]) . LN);
}
ini_set('error_log', realpath(dirname(__FILE__) . '/../logs') . '/php.log');

$in = $argv[1];
$table = $argv[2];
$string_fields = isset($argv[3]) && !empty($argv[3]) ? explode(',', $argv[3]) : [];

$out = preg_replace('/\.csv$/', '.sql', $in);
error_log(sprintf('[%s:%d] creating %s', basename(__FILE__), __LINE__, $out));
if (file_exists($out))
  unlink($out);

processCsv($in, $table, $out);
error_log(sprintf('[%s:%d] %s has been created', basename(__FILE__), __LINE__, $out));

function processCsv($csvFile, $table, $out) {
  $records = [];
  if (file_exists($csvFile)) {
    $lineNumber = 0;

    foreach (file($csvFile) as $line) {
      $lineNumber++;
      $values = str_getcsv($line);
      if ($lineNumber == 1) {
        $columns = $values;
      } else {
        if (count($columns) != count($values)) {
          error_log(sprintf('error in %s line #%d: expected nr-of-columns: %d vs actual values: %d',
          $csvFile, $lineNumber, count($columns), count($values)));
          error_log(sprintf('file: %s, id: %s', $values[1], $values[2]));
        } else {
          $sql = sprintf("INSERT INTO %s (`%s`) VALUES (%s);\n", $table, join('`,`', $columns), str_putcsv2($values, ',', '"', $columns));
          file_put_contents($out, $sql, FILE_APPEND);
        }
      }
    }
  } else {
    error_log('file does not exist! ' . $csvFile);
  }
  return $records;
}

function str_putcsv2(array $input, $delimiter = ',', $enclosure = '"', $columns) {
  global $string_fields;

  $a2 = [];
  foreach ($input as $i => $item) {
    if ($columns[$i] != 'recordId' && !in_array($columns[$i], $string_fields) && preg_match('/^\d+(\.\d+)?$/', $item))
      $a2[] = $item;
    else if ($item == 'NULL') 
      $a2[] = $item;
    else
      $a2[] = $enclosure . $item . $enclosure;
  }
  return implode($delimiter, $a2);
}
