package com.example.final_project;

public class Lecture {
    public String courseId;
    public String sectionId;
    public String lectureId;
    public String lectureTitle;

    public Lecture(String courseId, String sectionId, String lectureId, String lectureTitle) {
        this.courseId = courseId;
        this.sectionId = sectionId;
        this.lectureId = lectureId;
        this.lectureTitle = lectureTitle;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getLectureId() {
        return lectureId;
    }

    public String getLectureTitle() {
        return lectureTitle;
    }

}
