# Project Name
Students Attendance


## Done By:

- [Ibrahim Omar Shatat](120200262)
- [Mohamed Ahmed Shublaq](120200260)
- [Amro Kamal Kalloub](120200649)

## Project Overview

The project aimed to simplify the process of recording and managing student attendance in an efficient and reliable manner. 
The developers utilized Java and JavaFX to create an intuitive graphical user interface that would serve as the gateway to this attendance management system. They designed a sleek and responsive interface that allowed users to effortlessly navigate through the application's various functionalities.
To ensure data integrity and security, the team integrated a PostgreSQL database into the project. By leveraging the power of a robust database management system, they enabled administrators and teachers to store and retrieve attendance records with ease. The database architecture was carefully designed, incorporating appropriate tables, relationships, and constraints to maintain the integrity of the attendance data.
The Student Attendance project provided different user roles, including administrators and teacher assistants, each with their own set of privileges and responsibilities. Administrators could manage student records, create courses, generate attendance reports, and oversee the overall system administration. Teacher assistants could take attendance, view student details, and mark leaves or absences.

## Included Folders and Files

- `src\`: Main application Folder , it contains the java classes and fxml files for GUI.
- `Project DDL.sql`: This file contains the sql queries to create the DataBase and set roles and constraints, if the database not created yet , it will run by App.
- `Sample Data.sql`: This file contains the sql queries to insert queries to the DataBase (Second File runned by the App).
- `Sample Query.sql`: This file contains the sql queries to check the workflow of DataBase.
- `ERD.png`: An image describes the ER-Digram of our databas.
- `Relational Schema.png` : An image describes the relational schema of our database.
- `ReadMe.txt`: This File describes the files in the project and the usage instruction. 

## Requirements

- Java 20 or higher
- JavaFX library
- apache poi 5.0 (For Excel Files).
- use maven to build project.
- pgcrypto extension. 

## How to Use
1- Download All Requirments and Dependancies.
2- Ensure that you have created the role (Facebook group).
3. Open Intellij IDE and use it to build the project.
4. Load Maven Project to install all requirments.
5. Run the application: `App.main()`
6. The application window will open, and you can start using the GUI features.

## User Senario:
-The application starts, and the start method in the App class is invoked.
-Temporary connection is established to a PostgreSQL database to create a database role.
-The application checks if the "Students Attendance" database already exists.
-If the database not exists, the user is prompted to select a DDL (Data Definition Language) file containing the necessary table creation statements.
-The selected DDL file is executed, creating the required database tables.
-The user is prompted to select an SQL file containing insert statements to populate the tables with initial data.
-The selected SQL file is executed, inserting the data into the database.
-The application sets up the initial scene by loading the "opening.fxml" file.
-The scene is displayed in the application window.

## Drive Link:
https://drive.google.com/file/d/1ORQzPk51TeG5H7iJpXCtdEolnOxVNplK/view?usp=sharing
