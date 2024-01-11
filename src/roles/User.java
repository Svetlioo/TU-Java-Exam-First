package roles;

import java.io.Serializable;
import java.util.Objects;

public abstract class User implements Serializable
{
    private String userName;
    private String password;

    public User(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

    public abstract UserType getUserType();

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public String toString()
    {
        return "roles.User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(userName, password);
    }
}
