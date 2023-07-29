--Display the list of students who attended less than 25% of lectures
SELECT s.id, s.name
FROM student s
JOIN takes t ON s.id = t.id
JOIN section sec ON t.course_id = sec.course_id
                AND t.sec_id = sec.sec_id
                AND t.semester = sec.semester
                AND t.year = sec.year
JOIN lecture l ON sec.course_id = l.course_id
               AND sec.sec_id = l.sec_id
               AND sec.semester = l.semester
               AND sec.year = l.year
LEFT JOIN attend a ON s.id = a.id
                  AND l.lec_id = a.lec_id
                  AND l.course_id = a.course_id
                  AND l.sec_id = a.sec_id
                  AND l.semester = a.semester
                  AND l.year = a.year
GROUP BY s.id, s.name, sec.course_id, sec.sec_id, sec.semester, sec.year
HAVING COUNT(a.lec_id) / COUNT(l.lec_id) < 0.25
----------------------------------------------------------------------------------------
--What are the top 10 most attended lectures of all time?
SELECT l.lec_id, l.course_id, l.sec_id, l.title, l.semester, l.year, COUNT(a.id) AS attendance_count
FROM lecture l
LEFT JOIN attend a ON l.lec_id = a.lec_id AND l.course_id = a.course_id
                  AND l.sec_id = a.sec_id AND l.semester = a.semester
                  AND l.year = a.year
GROUP BY l.lec_id, l.course_id, l.sec_id, l.title, l.semester, l.year
ORDER BY attendance_count DESC
LIMIT 10;
--------------------------------------------------------------------------------------------
--For each student who attended more than 80% of all lectures, show the lectures heor she did not attend.
WITH total_lectures AS (
  SELECT l.lec_id, l.course_id, l.sec_id, l.title, l.semester, l.year, COUNT(*) AS total_count
  FROM lecture l
  LEFT JOIN attend a ON l.lec_id = a.lec_id AND l.course_id = a.course_id
                     AND l.sec_id = a.sec_id AND l.semester = a.semester
                     AND l.year = a.year
  GROUP BY l.lec_id, l.course_id, l.sec_id, l.title, l.semester, l.year
), 
attended_lectures AS (
  SELECT s.id, l.lec_id, l.course_id, l.sec_id, l.title, l.semester, l.year, COUNT(*) AS attendance_count
  FROM student s
  JOIN attend a ON s.id = a.id
  JOIN lecture l ON a.lec_id = l.lec_id AND a.course_id = l.course_id
                 AND a.sec_id = l.sec_id AND a.semester = l.semester
                 AND a.year = l.year
  GROUP BY s.id, l.lec_id, l.course_id, l.sec_id, l.title, l.semester, l.year
),
high_attendance_students AS (
  SELECT s.id, s.name
  FROM student s
  JOIN attended_lectures al ON s.id = al.id
  JOIN total_lectures tl ON al.lec_id = tl.lec_id AND al.course_id = tl.course_id
                         AND al.sec_id = tl.sec_id AND al.semester = tl.semester
                         AND al.year = tl.year
  GROUP BY s.id, s.name
  HAVING COUNT(*) / MAX(tl.total_count) > 0.8
)
SELECT has.id, has.name, tl.lec_id, tl.course_id, tl.sec_id, tl.title, tl.semester, tl.year
FROM high_attendance_students has
CROSS JOIN total_lectures tl
LEFT JOIN attended_lectures al ON has.id = al.id
                              AND tl.lec_id = al.lec_id AND tl.course_id = al.course_id
                              AND tl.sec_id = al.sec_id AND tl.semester = al.semester
                              AND tl.year = al.year
WHERE al.id IS NULL;
----------------------------------------------------------------------------------------------------
--Display the students ordered by their ‘commitment’ from the most committed tothe least.
SELECT s.id, s.name, COUNT(a.lec_id) AS total_attended_lectures
FROM student s
LEFT JOIN attend a ON s.id = a.id
GROUP BY s.id, s.name
ORDER BY total_attended_lectures DESC;
---------------------------------------------------------------------------------------------------
--fifth one : What are the lectures that had more students missing that lecture than actually attending it?
-- (if a student joined after that lecture, we should not count him as absent)
SELECT l.lec_id, l.course_id, l.sec_id, l.semester, l.year, l.title
FROM lecture l
LEFT JOIN attend a ON l.lec_id = a.lec_id
GROUP BY l.lec_id, l.course_id, l.sec_id, l.semester, l.year, l.title
HAVING COUNT(a.id) < (SELECT COUNT(*) FROM student) - COUNT(a.id);
----------------------------------------------------------------------------------------------------
--Display the list of students who missed 3 consecutive lectures.
SELECT s.id, s.name
FROM student s
LEFT JOIN attend a ON s.id = a.id
LEFT JOIN lecture l ON a.lec_id = l.lec_id
WHERE NOT EXISTS (
  SELECT 1
  FROM lecture l2
  WHERE l2.lec_id = l.lec_id - 1
    AND l2.course_id = l.course_id
    AND l2.sec_id = l.sec_id
    AND l2.semester = l.semester
    AND l2.year = l.year
  UNION
  SELECT 1
  FROM lecture l3
  WHERE l3.lec_id = l.lec_id - 2
    AND l3.course_id = l.course_id
    AND l3.sec_id = l.sec_id
    AND l3.semester = l.semester
    AND l3.year = l.year
)
GROUP BY s.id, s.name
HAVING COUNT(a.lec_id) >= 3;

