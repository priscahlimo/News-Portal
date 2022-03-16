# News-Portal

#### By **Priscah Limo**
## Description
A rest REST API for querying and retrieving scoped news and information.
## Setup/Installation Requirements
* Ensure to have JUnit, intellij, SDK,JDK
* Clone the repository
* Run the project on your machine

## postgres setup

CREATE DATABASE news_portal;

CREATE TABLE users (id serial primary key, name varchar, position varchar, role varchar, department varchar);

CREATE TABLE news (id serial primary key, title varchar, description varchar, type varchar, author varchar);

CREATE TABLE departments (id serial primary key, name varchar, description varchar, totalemployees int);

CREATE TABLE departments_users (id serial primary key, deptid int, userid int);

CREATE TABLE departments_news (id serial primary key, deptid int, newsid int, userid int);

CREATE DATABASE news_portal_test WITH TEMPLATE news_portal;

## Routes

Use the following paths.

/users  -Lists all the users

/departments  -Gets all the departments

/departments/new  -Gets an individual department using id

/departments/:deptId/details -Post a new department

/departments/:deptId/users/new  -Creates new department from specific user

/departments/:deptId/users/:userId/details"  -Users from individual department

/departments/:deptId/users/:userId/news  -Users from individual department

/departments/:deptId/users/:userId/news/new" - A user can post news

/departments/:deptId/news  -Get news from specific department

/users/:userId/news -Get news from specific use news

/news -Get all news news

## Known Bugs
There are no known bugs.
## Technologies Used
Java

Postgres

Spark

Handlebars

CSS

Bootstrap
## Support and contact details
Email : priscah.limo@student.moringaschool.com
### License
*[MIT License]("./LICENSE")

Copyright (c) [2022] [Priscah Limo]