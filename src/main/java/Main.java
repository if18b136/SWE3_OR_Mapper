import Database.DatabaseConnection;

import Entities.*;
import ORM.Base.Field;
import ORM.Manager;

import ORM.Queries.CreateTableQuery;
import ORM.Queries.InsertQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author Maximilian Rotter
 * @version 0.something
 * Main class that executes program with given arguments
 */
public class Main {
    /**
     * Logger instance for main class.
     */
    static final Logger mainLogger = LogManager.getLogger("Main Logger");

    /**
     * Runnable main class for program start.
     *
     * @param args  Program start input arguments.
     */
    public static void main(String[] args) {
        try{
            Connection db = DatabaseConnection.getInstance().getConnection();
            Scanner scanner = new Scanner(System.in);
            initTables(db);
            mainMenu(scanner,db);
        } catch (Exception e) {
            mainLogger.error(e);
            e.printStackTrace();
        }
    }

    private static void mainMenu(Scanner scanner, Connection db) throws InterruptedException, SQLException {
        printMainMenu();
        while (scanner.hasNext()) {

            var  test = scanner.next();
            if (test.equals("0")) {
                System.out.println("\n\rBye.");
                break;
            }
            switch (test) {
                case "1" -> initTables(db);
                case "2" -> dropTables(db);
                case "3" -> runAll(db);
                case "4" -> runChoice(scanner);
                default -> System.out.println("\n\rNumber has no function associated to it. Try another one:\n\r");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("Choose an option: ");
        System.out.println("1. drop and init example tables");
        System.out.println("2. drop example tables");
        System.out.println("3. run full test example");
        System.out.println("4. run certain test example");
        System.out.println("0. Exit");
    }

    private static void runChoice(Scanner scanner) throws InterruptedException, SQLException {
        System.out.println("Which test do you want to run?");
        System.out.println("1. create custom entity.");
        System.out.println("2. update custom entity.");
        System.out.println("3. create subclass entity.");
        System.out.println("4. caching example.");
        System.out.println("5. 1:n and m:n example.");
        System.out.println("6. table creation example.");
        System.out.println("7. insert query example.");
        System.out.println("8. delete database entry.");
        System.out.println("0. Back.");

        while (scanner.hasNextInt()) {
            int input = scanner.nextInt();
            if (input == 0) {
                System.out.println("\n\rBack to main menu.");
                printMainMenu();
                break;
            }
            switch (input) {
                case 1 -> createCustomEntity();
                case 2 -> updateCustomEntity();
                case 3 -> createSubclassEntity();
                case 4 -> caching();
                case 5 -> relations();
                case 6 -> createTableFromEntity();
                case 7 -> createInsert();
                case 8 -> delete();
                default -> System.out.println("\n\rNumber has no function associated to it. Try another one:\n\r");
            }
        }
    }

    private static void initTables(Connection db) throws SQLException, InterruptedException {
        dropTables(db);
        db.prepareStatement("CREATE TABLE t_person (id int PRIMARY KEY, firstName varchar(50) NOT NULL, lastName varchar(50) NOT NULL, birthDate date NOT NULL)").execute();
        db.prepareStatement("CREATE TABLE t_teacher (id int PRIMARY KEY, salary double NOT NULL, FOREIGN KEY (id) REFERENCES t_person(id))").execute();
        db.prepareStatement("CREATE TABLE t_course (id int PRIMARY KEY, courseName varchar(50) NOT NULL, ects double NOT NULL, teacher int NOT NULL, FOREIGN KEY (teacher) REFERENCES t_teacher(id))").execute();
        db.prepareStatement("CREATE TABLE t_student (id int PRIMARY KEY, FOREIGN KEY (id) REFERENCES t_person(id))").execute();
        db.prepareStatement("CREATE TABLE t_student_course ( t_student_id int, t_course_id int, FOREIGN KEY (t_student_id) REFERENCES t_student(id), FOREIGN KEY (t_course_id) REFERENCES t_course(id))").execute();
        System.out.println("Tables initialized.");
        TimeUnit.SECONDS.sleep(1);
    }

    private static void dropTables(Connection db) throws SQLException, InterruptedException {
        db.prepareStatement("DROP TABLE if exists t_student_course").execute();
        db.prepareStatement("DROP TABLE if exists t_course").execute();
        db.prepareStatement("DROP TABLE if exists t_teacher").execute();
        db.prepareStatement("DROP TABLE if exists t_student").execute();
        db.prepareStatement("DROP TABLE if exists t_person").execute();
        db.prepareStatement("DROP TABLE if exists t_test").execute();
        System.out.println("Tables dropped.");
        TimeUnit.SECONDS.sleep(1);
    }

    private static void runAll(Connection db) throws InterruptedException, SQLException {
        dropTables(db);
        Manager.enableCaching(true);
        System.out.println("\n\rCreate person entity, corresponding table and insert into database.");
        TimeUnit.SECONDS.sleep(5);
        Person person = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.createTable(person);
        Manager.save(person);
        Person getPerson = Manager.get(Person.class, 1);
        System.out.println("Person firstName: " + getPerson.getFirstName());
        Manager.enableCaching(false);

        TimeUnit.SECONDS.sleep(2);
        System.out.println("\n\rChange firstname to Thomas and then upsert.");
        TimeUnit.SECONDS.sleep(5);
        person.setFirstName("Thomas");
        Manager.saveOrUpdate(person);
        getPerson = Manager.get(Person.class, 1);
        System.out.println("Person firstName: " + getPerson.getFirstName());

        TimeUnit.SECONDS.sleep(2);
        System.out.println("\n\rCreate child class entity and store superclass information in it.");
        TimeUnit.SECONDS.sleep(5);
        Teacher teacher = new Teacher(1,2536.0);
        Manager.createTable(teacher);
        Manager.save(teacher);
        Teacher getTeacher = Manager.get(Teacher.class, 1);
        System.out.println("Teacher firstName: " + getTeacher.getFirstName());
        System.out.println("Teacher lastName: " + getTeacher.getLastName());
        System.out.println("Teacher birthdate: " + getTeacher.getBirthDate());
        System.out.println("Teacher salary: " + getTeacher.getSalary());
        System.out.println("Number of listed courses: " + (getTeacher.getTeachingCourses() == null ? 0 : getTeacher.getTeachingCourses().size()));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("\n\rCreate courses and adding a teacher as fk object, then retrieving the course from the database and print out teacher information from course.");
        TimeUnit.SECONDS.sleep(5);
        Course course1 = new Course(1,"Mathematics",3.5,teacher);
        Manager.createTable(course1);
        Manager.save(course1);

        Course getCourse = Manager.get(Course.class, 1);
        getTeacher = getCourse.getTeacher();
        System.out.println("Teacher firstName: " + getTeacher.getFirstName());
        System.out.println("Teacher lastName: " + getTeacher.getLastName());
        System.out.println("Teacher birthdate: " + getTeacher.getBirthDate());
        System.out.println("Teacher salary: " + getTeacher.getSalary());
        TimeUnit.SECONDS.sleep(2);
        System.out.println("\n\rAdd courses to student course list, then retrieve student from database and print course info from student object.");
        TimeUnit.SECONDS.sleep(5);
        Course course2 = new Course(2,"English",1.5,teacher);
        Manager.save(course2);
        Course course3 = new Course(3,"History",2.0,teacher);
        Manager.save(course3);
        Student student = new Student(1);
        student.addCourse(course1);
        student.addCourse(course2);
        student.addCourse(course3);
        Manager.createTable(student);
        Manager.save(student);

        Student getStudent = Manager.get(Student.class, 1);
        List<String> list = getStudent.getBookedCourses();
        for (String str : list) {
            System.out.println("Student course name: " + str);
        }

        TimeUnit.SECONDS.sleep(2);
        System.out.println("\n\rDelete entry.");
        TimeUnit.SECONDS.sleep(5);
        ResultSet res = db.prepareStatement("select count(*) from t_course;").executeQuery();
        while (res.next()) {
            System.out.println("Number of entries before delete: " + res.getInt(1));
        }
        Manager.delete(course3);
        res = db.prepareStatement("select count(*) from t_course;").executeQuery();
        while (res.next()) {
            System.out.println("Number of entries after delete: " + res.getInt(1));
        }
        TimeUnit.SECONDS.sleep(1);
        System.out.println();
        printMainMenu();
    }

    private static void createCustomEntity() throws InterruptedException {
        System.out.println("Create person entity and insert into database.");
        TimeUnit.SECONDS.sleep(2);
        Person person = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.save(person);

        Person getPerson = Manager.get(Person.class, 1);
        System.out.println("Person firstName: " + getPerson.getFirstName());
        System.out.println("Person lastName: " + getPerson.getLastName());
        System.out.println("Person birthdate: " + getPerson.getBirthDate());
    }

    private static void updateCustomEntity() throws InterruptedException {
        System.out.println("Change firstname to Thomas and then upsert.");
        TimeUnit.SECONDS.sleep(2);
        Person person = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.save(person);
        person.setFirstName("Thomas");
        Manager.saveOrUpdate(person);

        Person getPerson = Manager.get(Person.class, 1);
        System.out.println("Person firstName: " + getPerson.getFirstName());
        System.out.println("Person lastName: " + getPerson.getLastName());
        System.out.println("Person birthdate: " + getPerson.getBirthDate());
    }

    private static void createSubclassEntity() throws InterruptedException {
        System.out.println("Create child class entity and store superclass information in it.");
        TimeUnit.SECONDS.sleep(2);
        Person person = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.save(person);

        Teacher teacher = new Teacher(1,2536.0);
        Manager.save(teacher);

        Teacher getTeacher = Manager.get(Teacher.class, 1);
        System.out.println("Teacher firstName: " + getTeacher.getFirstName());
        System.out.println("Teacher lastName: " + getTeacher.getLastName());
        System.out.println("Teacher birthdate: " + getTeacher.getBirthDate());
        System.out.println("Teacher salary: " + getTeacher.getSalary());
        System.out.println("Number of listed courses: " + (getTeacher.getTeachingCourses() == null ? 0 : getTeacher.getTeachingCourses().size()));
    }

    private static void caching() throws InterruptedException {
        System.out.println("Enable caching, store person, disable caching and change person data, then retrieve cached version with correct firstname.");
        TimeUnit.SECONDS.sleep(2);
        Manager.enableCaching(true);
        Person person = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.save(person);
        Manager.enableCaching(false);
        person.setFirstName("Thomas");
        Manager.saveOrUpdate(person);
        Manager.enableCaching(true);
        Person getPerson = Manager.get(Person.class,1);
        System.out.println("Cached firstname for person: " + getPerson.getFirstName());
    }

    private static void relations() throws InterruptedException {
        System.out.println("Create courses and adding a teacher as fk object, then retrieving the course from the database and print out teacher information from course.");
        TimeUnit.SECONDS.sleep(2);
        Person person = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.save(person);
        Teacher teacher = new Teacher(1,2536.0);
        Manager.save(teacher);
        Course course1 = new Course(1,"Mathematics",3.5,teacher);
        Manager.save(course1);

        Course getCourse = Manager.get(Course.class, 1);
        Teacher getTeacher = getCourse.getTeacher();
        System.out.println("Teacher firstName: " + getTeacher.getFirstName());
        System.out.println("Teacher lastName: " + getTeacher.getLastName());
        System.out.println("Teacher birthdate: " + getTeacher.getBirthDate());
        System.out.println("Teacher salary: " + getTeacher.getSalary());
        TimeUnit.SECONDS.sleep(2);
        System.out.println("Add courses to student course list, then retrieve student from database and print course info from student object.");
        TimeUnit.SECONDS.sleep(2);
        Course course2 = new Course(2,"English",1.5,teacher);
        Manager.save(course2);
        Course course3 = new Course(3,"History",2.0,teacher);
        Manager.save(course3);
        Student student = new Student(1);
        student.addCourse(course1);
        student.addCourse(course2);
        student.addCourse(course3);
        Manager.save(student);

        Student getStudent = Manager.get(Student.class, 1);
        List<String> list = getStudent.getBookedCourses();
        for (String str : list) {
            System.out.println("Student course name: " + str);
        }

    }

    private static void createTableFromEntity() {
        CreateTableQuery create = new CreateTableQuery();
        Person person = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        System.out.println(create.buildQuery(Manager.getEntity(person)));
    }

    private static void createInsert() {
        InsertQuery insert = new InsertQuery();
        Person person = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        insert.buildQuery(person,Manager.getEntity(person));
        System.out.println(insert.getStmt().toString());
    }

    private static void delete() throws SQLException {
        Person person = new Person(1,"Timmy","Turner", LocalDate.of(1992,5,21));
        Manager.save(person);
        Connection db = DatabaseConnection.getInstance().getConnection();
        ResultSet res = db.prepareStatement("select count(*) from t_person;").executeQuery();
        while (res.next()) {
            System.out.println("Number of entries before delete: " + res.getInt(1));
        }
        Manager.delete(person);
        res = db.prepareStatement("select count(*) from t_person;").executeQuery();
        while (res.next()) {
            System.out.println("Number of entries after delete: " + res.getInt(1));
        }
    }
}
