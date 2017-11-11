CREATE TABLE tblTerms (
id INTEGER PRIMARY KEY,
term VARCHAR (96) NOT NULL UNIQUE);

CREATE TABLE tblDefinitions  (
id INTEGER PRIMARY KEY,
definition VARCHAR (255) NOT NULL,
term_id INTEGER NOT NULL,
CONSTRAINT term_fk FOREIGN KEY (term_id) REFERENCES tblTerms (id));

INSERT INTO tblTerms
VALUES (1, 'java');

INSERT INTO tblDefinitions
VALUES (1, 'Programm language',1);

INSERT INTO tblTerms
VALUES (2, 'ACID' );

INSERT INTO tblDefinitions
VALUES (2, 'The acronym for the four properties guaranteed by transactions:
 atomicity, consistency, isolation, and durability.',2);

INSERT INTO tblTerms
VALUES (3, 'API' );

INSERT INTO tblDefinitions
VALUES (3, 'Application Programming Interface. The specification of how a 
programmer writing an application accesses the behavior and state of classes 
and objects.', 3);

INSERT INTO tblTerms
VALUES (4, 'ASCII');

INSERT INTO tblDefinitions
VALUES (4, 'American Standard Code for Information Interchange. A standard
 assignment of 7-bit numeric codes to characters. See also Unicode.',4);

INSERT INTO tblTerms
VALUES (5, 'AWT');

INSERT INTO tblDefinitions
VALUES (5, 'A collection of graphical user interface (GUI) components that
 were implemented using native-platform versions of the components. 
These components provide that subset of functionality which is common to all native platforms.',5);


INSERT INTO tblTerms
VALUES (6, 'abstract');

INSERT INTO tblDefinitions
VALUES (6, 'A Java keyword used in a class definition to specify that a 
class is not to be instantiated, but rather inherited by other classes. 
An abstract class can have abstract methods that are not implemented in the abstract class,
 but in subclasses.',6);

INSERT INTO tblTerms
VALUES (7, 'abstract class');

INSERT INTO tblDefinitions
VALUES (7, 'A class that contains one or more abstract methods, and therefore can
 never be instantiated. Abstract classes are defined so that other classes can extend
 them and make them concrete by implementing the abstract methods.',7);

INSERT INTO tblTerms
VALUES (8, 'abstract method');

INSERT INTO tblDefinitions
VALUES (8, 'A method that has no implementation.',8);

INSERT INTO tblTerms
VALUES (9, 'access control');

INSERT INTO tblDefinitions
VALUES (9, 'The methods by which interactions with resources are limited to collections
 of users or programs for the purpose of enforcing integrity, confidentiality, or availability 
constraints.',9);

INSERT INTO tblTerms
VALUES (10, 'actual parameter list');

INSERT INTO tblDefinitions
VALUES (10, 'The arguments specified in a particular method call. See also formal parameter list. ',10);

INSERT INTO tblTerms
VALUES (11, 'applet');

INSERT INTO tblDefinitions
VALUES (11, 'A component that typically executes in a Web browser, but can execute in 
a variety of other applications or devices that support the applet programming model.',11);

INSERT INTO tblTerms
VALUES (12, 'argument');

INSERT INTO tblDefinitions
VALUES (12, 'A data item specified in a method call. An argument can be a literal value,
 a variable, or an expression.',12);

INSERT INTO tblTerms
VALUES (13, 'array');

INSERT INTO tblDefinitions
VALUES (13, 'A collection of data items, all of the same type, in which each items position
 is uniquely designated by an integer.',13);

INSERT INTO tblTerms
VALUES (14, 'atomic');

INSERT INTO tblDefinitions
VALUES (14, 'Refers to an operation that is never interrupted or left in an incomplete
 state under any circumstance.',14);

INSERT INTO tblTerms
VALUES (15, 'authentication');

INSERT INTO tblDefinitions
VALUES (15, 'The process by which an entity proves to another entity that it is acting
 on behalf of a specific identity.',15);

INSERT INTO tblTerms
VALUES (16, 'authorization');

INSERT INTO tblDefinitions
VALUES (16, 'See access control.',16);

INSERT INTO tblTerms
VALUES (17, 'autoboxing');

INSERT INTO tblDefinitions
VALUES (17, 'Automatic conversion between reference and primitive types.',17);

INSERT INTO tblTerms
VALUES (18, 'bean');

INSERT INTO tblDefinitions
VALUES (18, 'A reusable software component that conforms to certain design
 and naming conventions. The conventions enable beans to be easily combined to 
create an application using tools that understand the conventions.',18);

INSERT INTO tblTerms
VALUES (19, 'binary operator');

INSERT INTO tblDefinitions
VALUES (19, 'An operator that has two arguments.',19);

INSERT INTO tblTerms
VALUES (20, 'bit');

INSERT INTO tblDefinitions
VALUES (20, 'The smallest unit of information in a computer, with a value of either 0 or 1.',20);

