package entity;

import java.io.Serializable;
import java.util.Objects;

public class Grade implements Serializable
{
    private String subject;
    private int semester;
    private int grade;

    public Grade(String subject, int semester, int grade)
    {
        this.subject = subject;
        this.semester = semester;
        this.grade = grade;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public int getSemester()
    {
        return semester;
    }

    public void setSemester(int semester)
    {
        this.semester = semester;
    }

    public int getGrade()
    {
        return grade;
    }

    public void setGrade(int grade)
    {
        this.grade = grade;
    }

    @Override
    public String toString()
    {
        return "entity.Grade{" +
                "subject='" + subject + '\'' +
                ", semester=" + semester +
                ", grade=" + grade +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade1 = (Grade) o;
        return semester == grade1.semester &&
                grade == grade1.grade &&
                Objects.equals(subject, grade1.subject);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(subject, semester, grade);
    }
}
