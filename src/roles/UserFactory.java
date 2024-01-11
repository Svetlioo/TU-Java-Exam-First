package roles;

import exception.CredentialsException;
import roles.*;

import java.util.regex.Pattern;

public class UserFactory
{
    private final static Pattern facultyNumberPattern = Pattern.compile("\\d{9}");
    private final static Pattern EGNPattern = Pattern.compile("\\d{10}");
    private final static Pattern emailPattern = Pattern.compile("[a-z]+@tu-sofia.bg");

    public static User createUser(String userName, String password, UserType userType) throws CredentialsException
    {
        switch (userType)
        {
            case ADMIN:
            {
                return new Admin(userName, password);
            }
            case STUDENT:
            {
                if (!facultyNumberPattern.matcher(userName).matches())
                    throw new CredentialsException("Error: Invalid faculty number format.");
                if (!EGNPattern.matcher(password).matches())
                    throw new CredentialsException("Error: Invalid EGN format.");
                return new Student(userName, password);
            }
            case TEACHER:
            {
                if (!emailPattern.matcher(userName).matches())
                    throw new CredentialsException("Error: Invalid email format.");
                if (password.length() < 5)
                    throw new CredentialsException("Error: Password must be at least 5 characters");
                return new Teacher(userName, password);
            }
            default:
                return null;
        }
    }
}
