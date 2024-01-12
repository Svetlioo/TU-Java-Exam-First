import entity.Grade;
import exception.CredentialsException;
import roles.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Server {
    private static final String USERS_FILENAME = "users.bin";

    private final Object usersLock;

    private ServerSocket server;

    public Server() {
        initAdmins();
        usersLock = new Object();
    }

    /*
    Creates the users file the first time the server is run.
     */
    public void initAdmins() {
        if (new File(USERS_FILENAME).exists())
            return;
        List<User> users = new ArrayList<>();
        users.add(new Admin("admin", "12345"));
        saveUsers(users);
    }

    public void start() {
        try {
            System.out.println("Server listening.");
            server = new ServerSocket(8080);

            while (true) {
                Socket client = server.accept();

                Thread clientThread = new Thread(() ->
                {
                    System.out.println("Accepted client.");
                    try (Scanner sc = new Scanner(client.getInputStream()); PrintStream out = new PrintStream(client.getOutputStream())) {
                        userMenu(sc, out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // For offline testing
//        userMenu(new Scanner(System.in), System.out);
    }

    @SuppressWarnings("unchecked")
    public List<User> loadUsers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(USERS_FILENAME))) {
            return (List<User>) in.readObject();
        } catch (IOException e) {
            if (e instanceof InvalidClassException) {
                throw new RuntimeException("One or more of the roles.User subclasses has likely changed." +
                        " Serializable versions are not supported." +
                        " Recreate the users file.", e);
            }

            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // Should never happen
            throw new RuntimeException(e);
        }

        return null;
    }

    public void saveUsers(List<User> users) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USERS_FILENAME))) {
            out.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerUser(String userName, String password, UserType userType) throws CredentialsException {
        User user = UserFactory.createUser(userName, password, userType);
        synchronized (usersLock) {
            List<User> users = loadUsers();
            users.add(user);
            saveUsers(users);
        }
    }

    private User login(String userName, String password) {
        synchronized (usersLock) {
            for (User user : loadUsers()) {
                if (Objects.equals(user.getUserName(), userName) && Objects.equals(user.getPassword(), password))
                    return user;
            }

            return null;
        }
    }

    private void userMenu(Scanner sc, PrintStream out) {
        while (true) {
            out.println("Login? Y/N");
            String login = sc.nextLine();

            if (!login.equalsIgnoreCase("Y")) {
                out.println("Goodbye.");
                return;
            }

            out.println("Enter username:");
            String userName = sc.nextLine();

            out.println("Enter password:");
            String password = sc.nextLine();

            User user = login(userName, password);

            if (user == null) {
                out.println("Error: Invalid login.");
                continue;
            }

            switch (user.getUserType()) {
                case ADMIN: {
                    adminMenu(sc, out, (Admin) user);
                    break;
                }
                case STUDENT: {
                    studentMenu(sc, out, (Student) user);
                    break;
                }
                case TEACHER: {
                    teacherMenu(sc, out, (Teacher) user);
                    break;
                }
            }
        }
    }

    private void adminMenu(Scanner sc, PrintStream out, Admin admin) {
        out.println("Logged in as admin.");

        out.println("Enter user type to create: (ADMIN | STUDENT | TEACHER)");
        try {
            UserType userType = UserType.valueOf(sc.nextLine().toUpperCase());

            out.println("Enter username:");
            String userName = sc.nextLine();

            out.println("Enter password:");
            String password = sc.nextLine();

            registerUser(userName, password, userType);

            out.println("Success.");
        } catch (IllegalArgumentException e) {
            out.println("Error: Invalid user type.");
        } catch (CredentialsException e) {
            out.println(e.getMessage());
        }
    }

    private void studentMenu(Scanner sc, PrintStream out, Student student) {
        out.println("Logged in as student.");
        List<Grade> sortedGrades = student.getGrades()
                .stream()
                .sorted(Comparator.comparingInt(Grade::getSemester).thenComparing(Grade::getSubject))
                .collect(Collectors.toList());
        out.println(sortedGrades);
    }

    private void teacherMenu(Scanner sc, PrintStream out, Teacher teacher) {
        out.println("Logged in as teacher.");

        out.println("Enter student faculty number:");
        String facultyNumber = sc.nextLine();

        out.println("Enter subject:");
        String subject = sc.nextLine();

        out.println("Enter semester:");
        int semester = Integer.parseInt(sc.nextLine());

        out.println("Enter grade:");
        int gradeValue = Integer.parseInt(sc.nextLine());

        Grade grade = new Grade(subject, semester, gradeValue);

        synchronized (usersLock) {
            List<User> users = loadUsers();
            for (User user : users) {
                if (user.getUserName().equals(facultyNumber) && user instanceof Student) {
                    Student student = (Student) user;

                    student.getGrades().add(grade);

                    saveUsers(users);

                    out.println("Success.");
                    return;
                }
            }

            out.println("No such student.");
        }
    }
}
