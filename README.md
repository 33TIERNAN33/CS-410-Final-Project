# Final Project for CS 410 Databases

* Author: Abdulalim Ciftci
* Author: Tiernan Benner
* Class: CS410 Section #001
* Semester: Fall 2025

## Overview

For this project, we built a Gradebook Management System using Java and MySQL. 
The goal was to create a program where an instructor can manage their classes, add students, creating assignments, and enter grades. 
The most important feature is that the system automatically calculates the final weighted grades (like 40% Homework, 60% Exam) 
using SQL queries, rather than doing the math in Java..

## Reflection

Overall, this project was a really good way to see how Java connects to a real database. 
What worked well for us was deciding to put all the database connection code into a separate file (DB.java). 
This kept our main code clean and made it much easier to focus on the logic without seeing connection strings everywhere. 
However, it was a struggle to get the weighted grade calculations right in SQL. Writing a single query to join four different tables 
and calculate the weights correctly took us a long time and a lot of trial and error.

## Compiling and Using

This section should tell the user how to compile your code.  It is
also appropriate to instruct the user how to use your code. Does your
program require user input? If so, what does your user need to know
about it to use it as quickly as possible?

## Sources used

If you used any sources outside of the lecture notes, class lab files,
or text book you need to list them here. If you looked something up on
stackoverflow.com and fail to cite it in this section it will be
considered plagiarism and be dealt with accordingly. So be safe CITE!

----------
This README template is using Markdown. To preview your README output,
you can copy your file contents to a Markdown editor/previewer such
as [https://stackedit.io/editor](https://stackedit.io/editor).
