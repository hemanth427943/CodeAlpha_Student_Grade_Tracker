import java.util.*;

class Grade {
    private String subject;
    private double score;

    public Grade(String subject, double score) {
        this.subject = subject;
        this.score = score;
    }

    public String getSubject() { return subject; }
    public double getScore() { return score; }

    @Override
    public String toString() {
        return subject + ": " + score;
    }
}

class Student {
    private String name;
    private int studentId;
    private ArrayList<Grade> grades;

    public Student(String name, int studentId) {
        this.name = name;
        this.studentId = studentId;
        this.grades = new ArrayList<>();
    }

    public void addGrade(String subject, double score) {
        if (score >= 0 && score <= 100) {
            grades.add(new Grade(subject, score));
        } else {
            System.out.println("Invalid grade! Please enter a grade between 0 and 100.");
        }
    }

    public double calculateAverage() {
        return grades.stream().mapToDouble(Grade::getScore).average().orElse(0.0);
    }

    public Grade getHighestGradeWithSubject() {
        return grades.stream().max(Comparator.comparingDouble(Grade::getScore)).orElse(null);
    }

    public Grade getLowestGradeWithSubject() {
        return grades.stream().min(Comparator.comparingDouble(Grade::getScore)).orElse(null);
    }

    public String getLetterGrade() {
        double avg = calculateAverage();
        if (avg >= 90) return "A";
        else if (avg >= 80) return "B";
        else if (avg >= 70) return "C";
        else if (avg >= 60) return "D";
        else return "F";
    }

    public String getName() { return name; }
    public int getStudentId() { return studentId; }
    public ArrayList<Grade> getGrades() { return grades; }
    public int getGradeCount() { return grades.size(); }
}

public class StudentGradeTracker {
    private ArrayList<Student> students;
    private Scanner scanner;

    public StudentGradeTracker() {
        students = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("\n========== STUDENT GRADE TRACKER ==========");
        System.out.println("1. Add New Student");
        System.out.println("2. Add Grade to Student");
        System.out.println("3. View Student Details");
        System.out.println("4. Display All Students Summary");
        System.out.println("5. Class Statistics");
        System.out.println("6. Exit");
        System.out.print("Enter your choice (1-6): ");
    }

    private int safeIntInput() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter a number.");
            scanner.nextLine();
            return -1;
        }
    }

    public void addStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty!");
            return;
        }

        System.out.print("Enter student ID: ");
        int id = safeIntInput();
        if (id == -1) return;

        for (Student student : students) {
            if (student.getStudentId() == id) {
                System.out.println("Student ID already exists!");
                return;
            }
        }

        students.add(new Student(name, id));
        System.out.println("Student added successfully!");
    }

    public void addGradeToStudent() {
        if (students.isEmpty()) {
            System.out.println("No students found. Please add students first.");
            return;
        }

        System.out.print("Enter student ID: ");
        int id = safeIntInput();
        if (id == -1) return;

        Student student = findStudentById(id);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        System.out.print("Enter subject name: ");
        String subject = scanner.nextLine();

        System.out.print("Enter grade (0-100): ");
        double grade;
        try {
            grade = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Invalid grade input!");
            scanner.nextLine();
            return;
        }
        scanner.nextLine();

        student.addGrade(subject, grade);
        System.out.println("Grade added successfully!");
    }

    public void viewStudentDetails() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        System.out.print("Enter student ID: ");
        int id = safeIntInput();
        if (id == -1) return;

        Student student = findStudentById(id);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        System.out.println("\nName: " + student.getName());
        System.out.println("ID: " + student.getStudentId());
        System.out.println("Grades: ");
        for (Grade grade : student.getGrades()) {
            System.out.println("  " + grade);
        }
        System.out.printf("Average: %.2f\n", student.calculateAverage());
        Grade highest = student.getHighestGradeWithSubject();
        Grade lowest = student.getLowestGradeWithSubject();
        if (highest != null) {
            System.out.println("Highest: " + highest);
            System.out.println("Lowest: " + lowest);
        }
        System.out.println("Letter Grade: " + student.getLetterGrade());
    }

    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        System.out.println("\n========== ALL STUDENTS SUMMARY ==========");
        System.out.printf("%-5s %-20s %-10s %-10s %-15s %-15s %-5s\n",
                "ID", "Name", "Subjects", "Average", "Highest", "Lowest", "Letter");
        System.out.println("=".repeat(90));

        for (Student student : students) {
            if (student.getGradeCount() > 0) {
                Grade highest = student.getHighestGradeWithSubject();
                Grade lowest = student.getLowestGradeWithSubject();
                System.out.printf("%-5d %-20s %-10d %-10.2f %-15s %-15s %-5s\n",
                        student.getStudentId(),
                        student.getName(),
                        student.getGradeCount(),
                        student.calculateAverage(),
                        highest.getScore() + " (" + highest.getSubject() + ")",
                        lowest.getScore() + " (" + lowest.getSubject() + ")",
                        student.getLetterGrade());
            } else {
                System.out.printf("%-5d %-20s %-10s %-10s %-15s %-15s %-5s\n",
                        student.getStudentId(),
                        student.getName(),
                        "0", "N/A", "N/A", "N/A", "N/A");
            }
        }
        System.out.println("=".repeat(90));
    }

    public void displayClassStatistics() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        int studentsWithGrades = 0;
        double total = 0;
        double highest = 0;
        double lowest = Double.MAX_VALUE;

        for (Student student : students) {
            if (student.getGradeCount() > 0) {
                studentsWithGrades++;
                double avg = student.calculateAverage();
                total += avg;
                if (avg > highest) highest = avg;
                if (avg < lowest) lowest = avg;
            }
        }

        System.out.println("\n========== CLASS STATISTICS ==========");
        System.out.println("Total Students: " + students.size());
        System.out.println("Students with Grades: " + studentsWithGrades);
        if (studentsWithGrades > 0) {
            System.out.printf("Class Average: %.2f\n", total / studentsWithGrades);
            System.out.printf("Highest Student Average: %.2f\n", highest);
            System.out.printf("Lowest Student Average: %.2f\n", lowest);
        } else {
            System.out.println("No grades recorded yet.");
        }
    }

    public Student findStudentById(int id) {
        for (Student student : students) {
            if (student.getStudentId() == id) return student;
        }
        return null;
    }

    public void run() {
        System.out.println("Welcome to Student Grade Tracker!");

        while (true) {
            displayMenu();
            int choice = safeIntInput();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> addGradeToStudent();
                case 3 -> viewStudentDetails();
                case 4 -> displayAllStudents();
                case 5 -> displayClassStatistics();
                case 6 -> {
                    System.out.println("Exiting... Total students: " + students.size());
                    System.out.println("Thanks for using Student Grade Tracker!");
                    return;
                }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        StudentGradeTracker tracker = new StudentGradeTracker();
        tracker.run();
    }
}

