
sudo apt-get update
sudo apt-get install apache2
sudo apt-get install mysql-server php5-mysql php5
mysql-password: 1234
sudo apt-get install php5-mysqlnd

CREATE DATABASE uem;
use uem;
create table users(id INT(10) AUTO_INCREMENT PRIMARY KEY, user_name varchar(50), user_email  varchar(100) UNIQUE, user_password varchar(50), user_adminright varchar(10), user_gcmid text);
INSERT INTO users(user_name, user_email, user_password) VALUES ("Shopan Dey", 'shopan222@gmail.com' , '1234' );

create table node_info( id INT(6) AUTO_INCREMENT PRIMARY KEY, name varchar(50), username varchar(100), device_id  varchar(100) UNIQUE, device_key varchar(20), output_data  varchar(200), device_status varchar(200));
INSERT INTO node_info (name,username,device_id,device_key,output_data) VALUES ('Servo', 'shopan222@gmail.com', 'arm_001','1234','90,90,90,90,90');

show tables;
decs table_name;

DROP TABLE name;

create table node_data( id INT(10) AUTO_INCREMENT PRIMARY KEY, device_id  varchar(100), log_time varchar(100), input_data  varchar(200));

INSERT INTO node_data(device_id,log_time,input_data) VALUES ('sensor_001','now','temp');

INSERT INTO node_info (name,username,device_id,device_key,output_data,device_status) VALUES ('RJ0004', 'srishmita@gmail.com', 'bbb1212a','1234','on', 'online');
INSERT INTO node_data(device_id,log_time,input_data) VALUES ('aaa2324c','now','sensor data');

sudo tail -f /var/log/apache2/error.log