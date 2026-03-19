/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.payrollsystem;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

// ================= EMPLOYEE CLASS =================
class Employee {
    String empNo;
    String lastName;
    String firstName;
    String birthday;
    double hourlyRate;

    public Employee(String[] data) {
        this.empNo = data[0];
        this.lastName = data[1];
        this.firstName = data[2];
        this.birthday = data[3];
        this.hourlyRate = Double.parseDouble(data[18]);
    }
}

// ================= ATTENDANCE CLASS =================
class Attendance {
    String empNo;
    int month, day, year;
    LocalTime login, logout;

    public Attendance(String empNo, int month, int day, int year, LocalTime login, LocalTime logout) {
        this.empNo = empNo;
        this.month = month;
        this.day = day;
        this.year = year;
        this.login = login;
        this.logout = logout;
    }
}

// ================= MAIN SYSTEM =================
public class PayrollSystem {

    public static final String EMPLOYEE_FILE_PATH = "src\\main\\java\\com\\mycompany\\payrollsystem\\employee_details.csv";
    public static final String ATTENDANCE_FILE_PATH = "src\\main\\java\\com\\mycompany\\payrollsystem\\attendance_record.csv";
    public static final int YEAR = 2024;

    // ================= FILE LOADING =================
    public static List<Employee> loadEmployees(String filePath) {
        List<Employee> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                list.add(new Employee(data));
            }
        } catch (Exception e) {
            System.out.println("Error loading employees.");
        }
        return list;
    }

    public static List<Attendance> loadAttendance(String filePath) {
        List<Attendance> list = new ArrayList<>();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("H:mm");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                String empNo = data[0];
                String[] date = data[3].split("/");

                list.add(new Attendance(
                        empNo,
                        Integer.parseInt(date[0]),
                        Integer.parseInt(date[1]),
                        Integer.parseInt(date[2]),
                        LocalTime.parse(data[4].trim(), format),
                        LocalTime.parse(data[5].trim(), format)
                ));
            }
        } catch (Exception e) {
            System.out.println("Error loading attendance.");
        }
        return list;
    }

    // ================= COMPUTATIONS =================
    public static double computeSSS(double salary) {
        if (salary < 3250) return 135;
        int n = (int) ((salary - 3250) / 500) + 1;
        return Math.min(135 + (22.5 * n), 1125);
    }

    public static double computePhilHealth(double salary) {
        double premium = salary * 0.03;
        if (salary <= 10000) premium = 300;
        else if (salary >= 60000) premium = 1800;
        return premium / 2;
    }

    public static double computePagIbig(double salary) {
        double rate = (salary <= 1500) ? 0.01 : 0.02;
        return Math.min(salary * rate, 100);
    }

    public static double computeTax(double income) {
        if (income <= 20832) return 0;
        if (income <= 33332) return (income - 20833) * 0.20;
        if (income <= 66666) return 2500 + (income - 33333) * 0.25;
        if (income <= 166666) return 10833 + (income - 66667) * 0.30;
        if (income <= 666666) return 40833.33 + (income - 166667) * 0.32;
        return 200833.33 + (income - 666667) * 0.35;
    }

    public static double computeHours(LocalTime login, LocalTime logout) {
        LocalTime grace = LocalTime.of(8, 10);
        LocalTime cutoff = LocalTime.of(17, 0);

        if (logout.isAfter(cutoff)) logout = cutoff;
        if (logout.isBefore(login)) return 0;

        long minutes = Duration.between(login, logout).toMinutes();
        if (minutes <= 60) return 0;

        minutes -= 60;

        if (!login.isAfter(grace)) return 8.0;
        return Math.min(minutes / 60.0, 8.0);
    }

    // ================= LOGIC =================
    public static double[] computeAttendance(String empNo, List<Attendance> records, int month) {
        double first = 0, second = 0;

        for (Attendance r : records) {
            if (!r.empNo.equals(empNo) || r.year != YEAR || r.month != month) continue;

            double hours = computeHours(r.login, r.logout);
            if (r.day <= 15) first += hours;
            else second += hours;
        }
        return new double[]{first, second};
    }

    public static void displayPayroll(Employee e, double h1, double h2, int month) {
        String monthName = Month.of(month).name();
        int days = YearMonth.of(YEAR, month).lengthOfMonth();

        double g1 = h1 * e.hourlyRate;
        double g2 = h2 * e.hourlyRate;
        double gross = g1 + g2;

        double sss = computeSSS(gross);
        double phil = computePhilHealth(gross);
        double pag = computePagIbig(gross);
        double tax = computeTax(gross);

        double deductions = sss + phil + pag + tax;

        System.out.println("\n===== " + monthName + " =====");
        System.out.printf("Employee: %s, %s%n", e.lastName, e.firstName);

        System.out.printf("1-15 Hours: %.2f | Gross: %.2f%n", h1, g1);
        System.out.printf("16-%d Hours: %.2f | Gross: %.2f%n", days, h2, g2);

        System.out.printf("SSS: %.2f | PhilHealth: %.2f | Pag-IBIG: %.2f | Tax: %.2f%n",
                sss, phil, pag, tax);

        System.out.printf("Net Salary: %.2f%n", (g2 - deductions));
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        List<Employee> employees = loadEmployees(EMPLOYEE_FILE_PATH);
        List<Attendance> records = loadAttendance(ATTENDANCE_FILE_PATH);

        Map<String, Employee> map = new HashMap<>();
        for (Employee e : employees) map.put(e.empNo, e);

        System.out.println("====================================");
System.out.println("      MOTORPH PAYROLL SYSTEM        ");
System.out.println("====================================");
System.out.print("Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        if (user.equals("employee") && pass.equals("12345")) {

    System.out.print("Enter Employee #: ");
    String id = sc.nextLine();

    Employee e = map.get(id);

    if (e == null) {
        System.out.println("Employee not found.");
        return;
    }

    while (true) {
        System.out.println("\n==== EMPLOYEE MENU ====");
        System.out.println("1. View Employee Details");
        System.out.println("2. View Payroll");
        System.out.println("3. Exit");

        System.out.print("Enter choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 3) break;

        switch (choice) {

            case 1:
                System.out.println("\n===== EMPLOYEE DETAILS =====");
                System.out.println("Employee #: " + e.empNo);
                System.out.println("Name: " + e.lastName + ", " + e.firstName);
                System.out.println("Birthday: " + e.birthday);
                System.out.println("Hourly Rate: " + e.hourlyRate);
                break;

            case 2:
                for (int m = 6; m <= 12; m++) {
                    double[] hrs = computeAttendance(e.empNo, records, m);
                    displayPayroll(e, hrs[0], hrs[1], m);
                }
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }

} else {
    System.out.println("Invalid login.");
}
    }
}
