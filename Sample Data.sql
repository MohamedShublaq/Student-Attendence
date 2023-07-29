-- Department Table
INSERT INTO department (dept_name, building) VALUES
  ('Comp. Sci.', 'Engineering'),
  ('Mathematics', 'Science'),
  ('Physics', 'Science');
 
-- Instructor Table
INSERT INTO instructor (id, name, salary, gender, password, dept_name) VALUES
  ('2002', 'John Doe', 120000, 'Male', crypt('instructor', gen_salt('bf')), 'Comp. Sci.'),
  ('2003', 'Jane Smith', 95000, 'Female', crypt('instructor', gen_salt('bf')), 'Mathematics'),
  ('2004', 'David Johnson', 80000, 'Male', crypt('instructor', gen_salt('bf')), 'Physics');

-- Student Table
INSERT INTO student (id, name, gender, password, dept_name, city, street) VALUES
  ('220203', 'Sarah Johnso', 'Female', crypt('studen', gen_salt('bf')), 'Comp. Sci.', 'New York', '123 Street'),
  ('120204', 'Michael Brn', 'Male', crypt('studen', gen_salt('bf')), 'Mathematics', 'Los Angeles', '456 Street'),
  ('220204', 'Emily Dis', 'Female', crypt('studen', gen_salt('bf')), 'Physics', 'Chicago', '789 Street');

-- Phone Number Table
INSERT INTO phone_number (id, phone_number) VALUES
  ('220203', '1234567890'),
  ('220204', '2345678901'),
  ('120204', '3456789012');

-- Admin Table
INSERT INTO adminstrator (id, name, salary, password) VALUES
  ('001', 'Admin', 35000, crypt('admin', gen_salt('bf')));

-- TeacherAssistant Table
INSERT INTO teacherassistant (id, name, salary, gender, password, dept_name) VALUES
  ('3', 'Robert Wilson', 15000, 'Male', crypt('tutor', gen_salt('bf')), 'Comp. Sci.'),
  ('4', 'Emma Thompson', 15000, 'Female', crypt('tutor', gen_salt('bf')), 'Mathematics'),
  ('5', 'Daniel Harris', 15000, 'Male', crypt('tutor', gen_salt('bf')), 'Physics');

-- Course Table
INSERT INTO course (course_id, subject, dept_name, book) VALUES
  ('7', 'Introduction to Programming', 'Comp. Sci.', 'Programming Book'),
  ('8', 'Calculus I', 'Mathematics', 'Calculus Book'),
  ('9', 'Mechanics', 'Physics', 'Physics Book');

-- Section Table
INSERT INTO section (course_id, sec_id, semester, year, building, room_number) VALUES
  ('7', '001', 'Fall', 2022, 'Engineering', '101'),
  ('8', '001', 'Spring', 2023, 'Science', '201'),
  ('9', '001', 'Winter', 2023, 'Science', '301');

-- Teaches Table
INSERT INTO teaches (id, course_id, sec_id, semester, year) VALUES
  ('2002', '7', '001', 'Fall', 2022),
  ('2003', '8', '001', 'Spring', 2023),
  ('2004', '9', '001', 'Winter', 2023);

-- Takes Table
INSERT INTO takes (id, course_id, sec_id, semester, year, grade) VALUES
  ('220203', '7', '001', 'Fall', 2022, 'A'),
  ('220204', '8', '001', 'Spring', 2023, 'B+'),
  ('120204', '9', '001', 'Winter', 2023, 'A-');

-- Records Table
INSERT INTO records (id, course_id, sec_id, semester, year,lec_id) VALUES
  ('3', '7', '001', 'Fall', 2022,'3'),
  ('4', '8', '001', 'Spring', 2023,'4'),
  ('5', '9', '001', 'Winter', 2023,'5');

-- Lecture Table
INSERT INTO lecture (lec_id, course_id, sec_id, title, semester, year, building, room_number ) VALUES
  ('3', '7', '001', 'Introduction', 'Fall', 2022, 'Engineering', '101'),
  ('4', '8', '001', 'Calculus', 'Spring', 2023, 'Science', '201' ),
  ('5', '9', '001', 'Mechanics', 'Winter', 2023, 'Science', '301');

-- Attend Table
INSERT INTO attend (id, lec_id, course_id, sec_id, semester, year) VALUES
  ('220203', '3', '7', '001', 'Fall', 2022),
  ('220204', '4', '8', '001', 'Spring', 2023),
  ('120204', '5', '9', '001', 'Winter', 2023);
 ------------------------------------------------------------------------------------------
 --Second Generation
-- Department Table
INSERT INTO department (dept_name, building) VALUES
('Biology', 'Science'),
('Chemistry', 'Science'),
('History', 'Arts');

-- Instructor Table
INSERT INTO instructor (id, name, salary, gender, password, dept_name) VALUES
('2005', 'Karen Wilson', 110000, 'Female', crypt('instructor', gen_salt('bf')), 'Biology'),
('2006', 'Michael Lee', 90000, 'Male', crypt('instructor', gen_salt('bf')), 'Chemistry'),
('2007', 'Emily Davis', 85000, 'Female', crypt('instructor', gen_salt('bf')), 'History');

-- Student Table
INSERT INTO student (id, name, gender, password, dept_name, city, street) VALUES
('220205', 'Jessica Anderson', 'Female', crypt('student', gen_salt('bf')), 'Biology', 'Boston', '456 Avenue'),
('120206', 'Christopher Taylor', 'Male', crypt('student', gen_salt('bf')), 'Chemistry', 'San Francisco', '789 Avenue'),
('220206', 'Olivia Wilson', 'Female', crypt('student', gen_salt('bf')), 'History', 'Seattle', '123 Avenue');

-- Phone Number Table
INSERT INTO phone_number (id, phone_number) VALUES
('220205', '4567890123'),
('220206', '5678901234'),
('120206', '6789012345');

-- Admin Table
INSERT INTO adminstrator (id, name, salary, password) VALUES
('002', 'SuperAdmin', 50000, crypt('admin', gen_salt('bf')));

-- TeacherAssistant Table
INSERT INTO teacherassistant (id, name, salary, gender, password, dept_name) VALUES
('6', 'Andrew Thompson', 15000, 'Male', crypt('tutor', gen_salt('bf')), 'Biology'),
('7', 'Sophia Davis', 15000, 'Female', crypt('tutor', gen_salt('bf')), 'Chemistry'),
('8', 'William Harris', 15000, 'Male', crypt('tutor', gen_salt('bf')), 'History');

-- Course Table
INSERT INTO course (course_id, subject, dept_name, book) VALUES
('10', 'Genetics', 'Biology', 'Genetics'),
('11', 'Organic Chemistry', 'Chemistry', 'Organic Chemistry'),
('12', 'World History', 'History', 'History Book');

-- Section Table
INSERT INTO section (course_id, sec_id, semester, year, building, room_number) VALUES
('10', '001', 'Fall', 2022, 'Science', '101'),
('11', '001', 'Spring', 2023, 'Science', '201'),
('12', '001', 'Winter', 2023, 'Arts', '301');

-- Teaches Table
INSERT INTO teaches (id, course_id, sec_id, semester, year) VALUES
('2005', '10', '001', 'Fall', 2022),
('2006', '11', '001', 'Spring', 2023),
('2007', '12', '001', 'Winter', 2023);

-- Takes Table
INSERT INTO takes (id, course_id, sec_id, semester, year, grade) VALUES
('220205', '10', '001', 'Fall', 2022, 'A-'),
('220206', '11', '001', 'Spring', 2023, 'B'),
('120206', '12', '001', 'Winter', 2023, 'B+');

-- Records Table
INSERT INTO records (id, course_id, sec_id, semester, year, lec_id) VALUES
('7', '11', '001', 'Spring', 2023, '7'),
('8', '12', '001', 'Winter', 2023, '8');

-- Lecture Table
INSERT INTO lecture (lec_id, course_id, sec_id, title, semester, year, building, room_number ) VALUES
('6', '10', '001', 'Genetics', 'Fall', 2022, 'Science', '101'),
('7', '11', '001', 'Organic Chemi', 'Spring', 2023, 'Science', '201'),
('8', '12', '001', 'World Hist', 'Winter', 2023, 'Arts', '301');

-- Attend Table
INSERT INTO attend (id, lec_id, course_id, sec_id, semester, year) VALUES
('220205', '6', '10', '001', 'Fall', 2022),
('220206', '7', '11', '001', 'Spring', 2023),
('120206', '8', '12', '001', 'Winter', 2023);

