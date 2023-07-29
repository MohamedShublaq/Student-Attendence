
--Department Table
	CREATE TABLE department (
	  dept_name VARCHAR(15)  ,
	  building VARCHAR(15),
	  primary key(dept_name)
	);
	
	
	--Instructor Table
	CREATE TABLE instructor (
	  id VARCHAR(15)  ,
	  name VARCHAR(20) ,
	  salary numeric(10,0)  check (salary > 10000),
	  gender		varchar(7)  check (gender in ('Male', 'Female')), 
		password  VARCHAR,
		 dept_name		varchar(15), 
	  primary key (id),
	  foreign key (dept_name) references department (dept_name)
			on delete set null
	);
	--Student Table
	CREATE TABLE student (
	  id VARCHAR(15)  ,
	  name VARCHAR(20) ,
	  gender		varchar(7)  check (gender in ('Male', 'Female')), 
		password  VARCHAR(20),
		 dept_name		varchar(15), 
		 city varchar(15),
		 street varchar(15),
	  primary key (id),
	  foreign key (dept_name) references department (dept_name)
			on delete set null
	);
	--Phone Number Table 
	CREATE TABLE phone_number (
	  id VARCHAR(15)  ,
	  phone_number VARCHAR(15),
	  primary key (id , phone_number),
	  foreign key (id) references student (id)
			on delete cascade
	);
	--Admin Table
	
	CREATE TABLE adminstrator (
	  id VARCHAR(15)  ,
	  name VARCHAR(15) ,
	    primary key (id ),
	  salary numeric(10,0)  check (salary > 30000),
	  password  VARCHAR(20));
	 
	--TeacherAssistant
	 CREATE TABLE teacherassistant (
	 id VARCHAR(15)  ,
	  name VARCHAR(20) ,
	  salary numeric(10,0)  check (salary > 5000),
	  gender		varchar(6)  check (gender in ('Male', 'Female')), 
		password  VARCHAR,
		 dept_name		varchar(15), 
	  primary key (id),
	  foreign key (dept_name) references department (dept_name)
			on delete set null
	);
	
	--Course Table
	create table course
		(course_id		varchar(8), 
		 subject			varchar(50), 
		 dept_name		varchar(15),
		 book		varchar(20),
		 primary key (course_id),
		 foreign key (dept_name) references department (dept_name)
			on delete set null
		);
	--Section Table
	create table section
		(course_id		varchar(8), 
	         sec_id			varchar(8),
		 semester		varchar(6)
			check (semester in ('Fall', 'Winter', 'Spring', 'Summer')), 
		 year			numeric(4,0) check (year > 1701 and year < 2100), 
		 building		varchar(15),
		 room_number		varchar(7),
		 primary key (course_id, sec_id, semester, year),
		 foreign key (course_id) references course (course_id)
			on delete cascade
		);
	--teaches Table
	create table teaches
		(id			varchar(5), 
		 course_id		varchar(8),
		 sec_id			varchar(8), 
		 semester		varchar(6),
		 year			numeric(4,0),
		 primary key (id, course_id, sec_id, semester, year),
		 foreign key (course_id, sec_id, semester, year) references section (course_id, sec_id, semester, year)
			on delete cascade,
		 foreign key (id) references instructor (id)
			on delete cascade
		);
	 --takes Table
	create table takes
		(id			varchar(5), 
		 course_id		varchar(8),
		 sec_id			varchar(8), 
		 semester		varchar(6),
		 year			numeric(4,0),
		 grade		        varchar(2),
		 primary key (id, course_id, sec_id, semester, year),
		 foreign key (course_id, sec_id, semester, year) references section (course_id, sec_id, semester, year)
			on delete cascade,
		 foreign key (id) references student (id)
			on delete cascade
		);
	--lecture Table
	create table lecture	
	(lec_id varchar(8),
	course_id		varchar(8), 
	         sec_id			varchar(8),
	         title varchar(15),
		 semester		varchar(6)
			check (semester in ('Fall', 'Winter', 'Spring', 'Summer')), 
		 year			numeric(4,0) check (year > 1701 and year < 2100), 
		 building		varchar(15),
		 room_number		varchar(7),
		 primary key (lec_id,course_id, sec_id, semester, year),
		foreign key (course_id,sec_id, semester, year) references section (course_id,sec_id, semester, year)
		on delete cascade
		);
	--records Table
	create table records
		(id			varchar(5), 
		 course_id		varchar(8),
		 sec_id			varchar(8), 
		 semester		varchar(6),
		 year			numeric(4,0),
		 lec_id varchar(8),
		 primary key (id, course_id, sec_id, semester, year,lec_id),
		 foreign key (lec_id,course_id, sec_id, semester, year) references lecture (lec_id,course_id, sec_id, semester, year)
			on delete cascade
		);
	
	--attend Table
	create table attend(
	 id VARCHAR(15)  ,
	lec_id varchar(8),
	course_id		varchar(8), 
	         sec_id			varchar(8),
		 semester		varchar(6)
			check (semester in ('Fall', 'Winter', 'Spring', 'Summer')), 
		 year			numeric(4,0) check (year > 1701 and year < 2100), 
	 	 primary key (id,lec_id,course_id, sec_id, semester, year),
			foreign key (course_id,sec_id, semester, year) references section (course_id,sec_id, semester, year)
		on delete cascade,
		 foreign key (id) references student (id)
			on delete cascade
	);
---------------------------------------------------------------------
--Creation of roles 
create user tutor WITH SUPERUSER login password '1234';
create user admin  WITH SUPERUSER password '1234';
create user "auth" WITH SUPERUSER  password '12345';
 CREATE EXTENSION IF NOT EXISTS pgcrypto;