/* delete dataase */
DROP database emp;

CREATE database if not exists `EMP`;
USE `EMP`;

create table Employees
(
	id int not null,
	age int not null,
	first varchar (255),
	last varchar (255)
);

INSERT INTO Employees VALUES (100, 18, 'Zara', 'Ali');
INSERT INTO Employees VALUES (101, 25, 'Mahnaz', 'Fatma');
INSERT INTO Employees VALUES (102, 30, 'Zaid', 'Khan');
INSERT INTO Employees VALUES (103, 28, 'Sumit', 'Mittal');

SELECT id, first, last, age FROM Employees;
SELECT id, first, last, age FROM Employees WHERE id >= 102;
SELECT id FROM employees;
SELECT id FROM employees WHERE id < 102;
