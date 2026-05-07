package byod.model;

import java.time.LocalDateTime;

public class Student {

    private int id;
    private String studentId;
    private String fullName;
    private String course;
    private String email;
    private LocalDateTime createdAt;

    public Student() {}

    public Student(String studentId, String fullName, String course, String email) {
        this.studentId = studentId;
        this.fullName  = fullName;
        this.course    = course;
        this.email     = email;
    }

    // Getters
    public int           getId()        { return id; }
    public String        getStudentId() { return studentId; }
    public String        getFullName()  { return fullName; }
    public String        getCourse()    { return course; }
    public String        getEmail()     { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id)                       { this.id = id; }
    public void setStudentId(String studentId)      { this.studentId = studentId; }
    public void setFullName(String fullName)        { this.fullName = fullName; }
    public void setCourse(String course)            { this.course = course; }
    public void setEmail(String email)              { this.email = email; }
    public void setCreatedAt(LocalDateTime created) { this.createdAt = created; }

    @Override
    public String toString() {
        return fullName + " (" + studentId + ")";
    }
}
