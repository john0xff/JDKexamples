CREATE database if not exists `sql_training`;

USE `sql_training`;

CREATE TABLE Persons
(
	ID int NOT NULL AUTO_INCREMENT, 
	Name varchar(255),
	City varchar(255),
	MONEY int,
	PRIMARY KEY(ID)
);

insert into sql_training.person( , `someguy`, `London`, `100`);

INSERT INTO `sql_training`.`persons` (`ID` ,`Name` ,`City` ,`MONEY`)VALUES (NULL , 'someguy', 'London', '100');

INSERT INTO `persons` (NULL , 'someguy', 'London', '100');



select top 1 software 
from your_table 
group by software
order by count(*) desc 

SELECT * FROM Customers
WHERE country='USA';

SELECT top 1 country
from Customers
group by country
order by count(*) desc;

SELECT top 1 city
from person
group by city
order by count(*) desc;

SELECT `City` FROM `persons` group by `City` order by count(*) desc

