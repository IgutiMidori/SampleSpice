CREATE DATABASE IF NOT EXISTS spice_ec_db;
SHOW WARNINGS;

SELECT host, user FROM mysql.user;
CREATE USER 'spice_dml_user'@'%' IDENTIFIED BY 'P@ssw0rd';
SHOW GRANTS FOR 'spice_dml_user'@'%';
GRANT SELECT, UPDATE, INSERT, DELETE ON spice_ec_db.* TO 'spice_dml_user'@'%';