package roles;

import entity.Grade;
import roles.User;

import java.util.ArrayList;
import java.util.List;

public class Student extends User
{
    private List<Grade> grades;

    public Student(String facultyNumber, String EGN)
    {
        super(facultyNumber, EGN);
        grades = new ArrayList<>();
    }

    @Override
    public UserType getUserType()
    {
        return UserType.STUDENT;
    }

    public List<Grade> getGrades()
    {
        return grades;
    }

    @Override
    public String toString()
    {
        return "roles.Student{" +
                "grades=" + grades +
                "} " + super.toString();
    }
}
