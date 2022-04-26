# metadata-qa-ddb
A metadata quality assessment tool customized for the requirements of the Deutsche Digitale Bibliothek

Log in tinto MySQL, create a database and a dedicated user:

```
CREATE DATABASE ddb;

CREATE USER 'ddbadmin'@'localhost' IDENTIFIED BY '<password>';
GRANT ALL PRIVILEGES ON ddb.* TO 'ddbadmin'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```
