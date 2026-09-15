import java.util.ArrayList;
import java.util.Scanner;

class Student {
    private final String name;
    private final double score;

    public Student(String name, double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() { return name; }
    public double getScore() { return score; }

    public String getGrade() {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        if (score >= 50) return "D";
        return "F";
    }
}

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ArrayList<Student> students = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== STUDENT GRADE TRACKER =====");
            System.out.println("1. Add student");
            System.out.println("2. View all students");
            System.out.println("3. View summary");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addStudent();
                case "2" -> displayStudents();
                case "3" -> displaySummary();
                case "4" -> {
                    System.out.println("Thank you for using Student Grade Tracker.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void addStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter score (0-100): ");
        try {
            double score = Double.parseDouble(scanner.nextLine());
            if (score < 0 || score > 100) {
                System.out.println("Score must be between 0 and 100.");
                return;
            }
            students.add(new Student(name, score));
            System.out.println("Student added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void displayStudents() {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        System.out.println("\n----- Student Report -----");
        System.out.printf("%-5s %-25s %-10s %-8s%n", "No.", "Name", "Score", "Grade");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.printf("%-5d %-25s %-10.2f %-8s%n",
                    i + 1, s.getName(), s.getScore(), s.getGrade());
        }
    }

    private static void displaySummary() {
        if (students.isEmpty()) {
            System.out.println("No data available for summary.");
            return;
        }

        double total = 0;
        double highest = students.get(0).getScore();
        double lowest = students.get(0).getScore();

        for (Student s : students) {
            total += s.getScore();
            highest = Math.max(highest, s.getScore());
            lowest = Math.min(lowest, s.getScore());
        }

        double average = total / students.size();

        System.out.println("\n----- Summary -----");
        System.out.println("Total students : " + students.size());
        System.out.printf("Average score  : %.2f%n", average);
        System.out.printf("Highest score  : %.2f%n", highest);
        System.out.printf("Lowest score   : %.2f%n", lowest);
    }
}
