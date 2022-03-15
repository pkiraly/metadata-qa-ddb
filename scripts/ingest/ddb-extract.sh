DIR=ftp.deutsche-digitale-bibliothek.de/MQ-Feedback-Tool/

echo "file,schema,provider_id,provider_name,set_id,set_name,datum,size" > files.csv
find $DIR \
  | while read filename; do 
      if [[ -f $filename ]]; then
        datum=$(stat $filename | grep Modify | sed -r 's,^.+: (.*)\..*$,\1,')
        size=$(stat $filename | grep -oP 'Size: \K(\d+)')
        path=$(echo $filename | sed "s,$DIR,,") ;
        SCHEMA=$(echo $path | cut -d'/' -f1)
        LEVEL2=$(echo $path | cut -d'/' -f2)
        LEVEL3=$(echo $path | cut -d'/' -f3)
        PROV_ID=$(echo $LEVEL2 | cut -d'_' -f1)
        PROV_NAME=$(echo $LEVEL2 | cut -d'_' -f2)
        SET_ID=$(echo $LEVEL3 | cut -d'_' -f1)
        SET_NAME=$(echo $LEVEL3 | cut -d'_' -f2)
        echo "$path,$SCHEMA,$PROV_ID,$PROV_NAME,$SET_ID,$SET_NAME,$datum,$size" >> files.csv
      fi
    done

sqlite3 ddb.sqlite << EOT
.mode csv
.import files.csv files
EOT

# rm files.csv