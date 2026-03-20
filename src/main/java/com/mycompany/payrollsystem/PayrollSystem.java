/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.payrollsystem;

/**
 *
 * @author synen, re-organized version by Mors
 */
// ===== IMPORTS =====
// BufferedReader allows rading text files line by line
import java.io.BufferedReader;
// FileReader allows opening and reading files from the file system or computer
import java.io.FileReader;
// Duration allows calculation difference between two LocalTime objects or two times (login or logout)
import java.time.Duration;
// LocalTime represents a specific time without date -- to be used for logging in and logging out like 8:30
import java.time.LocalTime;
// YearMonth allows to get number of days in a specific month
import java.time.YearMonth;
// DateTimeFormatter allows parsing time strings like "15:00" into LocalTime objects or in simpler terms, it converts text like "15:00" into a usable time object
import java.time.format.DateTimeFormatter;
// Scanner allows user input from keyboard
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

// ===== MAIN CLASS =====
// declare a public class named PayrollSystem
// all program logic exists inside this class

/**
 *
 * @author synen
 */

class Attendance {
    String empNo;
    int month;
    int day;
    int year;
    LocalTime login;
    LocalTime logout;
    
    public Attendance(String empNo, int month, int day, int year, LocalTime login, LocalTime logout) {
        this.empNo = empNo;
        this.month = month;
        this.day = day;
        this.year = year;
        this.login = login;
        this.logout = logout;
    }
}

public class PayrollSystem {
    
    // ==== GLOBAL VARIABLES ====
    
    // file paths used across the application

    /**
     *
     */
    public static final String EMP_FILE = "src\\main\\java\\com\\mycompany\\payrollsystem\\employee_details.csv";

    /**
     *
     */
    public static final String ATT_FILE = "src\\main\\java\\com\\mycompany\\payrollsystem\\attendance_record.csv";
    
    //centralize year value to avoid hardcoding

    /**
     *
     */
    public static final int YEAR = 2024;
    
    // CSV column indices for consistent reference

    /**
     *
     */
    public static final int COL_EMP_NO = 0;

    /**
     *
     */
    public static final int COL_LAST_NAME = 1;

    /**
     *
     */
    public static final int COL_FIRST_NAME = 2;

    /**
     *
     */
    public static final int COL_BIRTHDAY = 3;

    /**
     *
     */
    public static final int COL_HOURLY_RATE = 18;

    /**
     *
     */
    public static final int COL_DATE = 3;

    /**
     *
     */
    public static final int COL_LOGIN = 4;

    /**
     *
     */
    public static final int COL_LOGOUT = 5;
    
    // loads employee file into memory (List of String arrays)
    public static List<String[]> loadEmployees(String EMP_FILE) {
        List<String[]> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(EMP_FILE))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // split csv safely (handles commas inside quotes)
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                employees.add(data);
            }
        } catch (Exception e) {
            System.out.println("Error reading employee file.");
            e.printStackTrace();
        }

        return employees;
    }

    // loads attendance csv file into Attendance objects
    public static List<Attendance> loadAttendance(String ATT_FILE) {
        List<Attendance> records = new ArrayList<>();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

        try (BufferedReader br = new BufferedReader(new FileReader(ATT_FILE))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                String empNo = data[COL_EMP_NO];

                String[] dateParts = data[COL_DATE].split("/");
                int month = Integer.parseInt(dateParts[0]);
                int day = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);

                LocalTime login = LocalTime.parse(data[COL_LOGIN].trim(), timeFormat);
                LocalTime logout = LocalTime.parse(data[COL_LOGOUT].trim(), timeFormat);

                records.add(new Attendance(empNo, month, day, year, login, logout));
            }
        } catch (Exception e) {
            System.out.println("Error loading attendance file.");
            e.printStackTrace();
        }

        return records;
    }
    
    
    // ==== METHODS ====
    // SSS Contribution
    // public -> accessible everywhere
    // static -> can run without creating an object
    // double -> returns decimal value
    // computeSSS -> method name
    // grossSalary -> input parameter

    /**
     *
     * @param grossSalary
     * @return
     */
    public static double computeSSS(double grossSalary) {
        // sss method computes employee SSS contribution based on gross salary (reference: SSS Contribution.csv from MotorPH website)
        // check ranges and return corresponding contribution
        double baseContribution = 135.0; // declaring variable storing minimum SSS contribution
        double increment = 22.5; // declaring amount added per salary bracket increase
        double salaryStep = 500.0; // how much the salary must increase before moving to the next SSS contribution bracket; each bracket covers 500php of salary; declaring salary range per contribution bracket
        double startingSalary = 3250.0; // declaring starting salary where contribution calculation begins
        double maxContribution = 1125.0; // declaring maximum allowed SSS contribution
        
        // if gross salary is below the starting bracket, return minimum contribution value which is baseContribution as declared above
        if (grossSalary < startingSalary)
            return baseContribution; // min contribution
        
        // calculate how many brackets above starting salary
        // grossSalary - startingSalary -> excess salary
        // divide by salaryStep -> number of brackets
        // cast to int removes decimals
        // + 1 ensure correct bracket counting
        int n = (int)((grossSalary - startingSalary)/salaryStep) + 1; // number of brackets
        
        // contribution is base + increment x number of brackets; capped at max contribution
        // Math.min ensure value never exceeds maxContribution
        return Math.min(baseContribution + increment * n, maxContribution);          
    }

    // PhilHealth Contribution (50% employee share)
    // computes philhealth contribution (employee share 50%)
    // based on 3% of monthly basic, capped between 300 and 1800
    // public -> accessible anywhere
    // static -> can run without creating an object
    // double -> returns decimal value
    // grossSalary -> input parameter

    /**
     *
     * @param grossSalary
     * @return
     */
    public static double computePhilHealth(double grossSalary) {
        double premium = grossSalary * 0.03; // compute 3% of monthly basic/gross
        if (grossSalary <= 10000) premium = 300; // check if computed premium is below minimum threshold then force minimum contribution rule
        else if (grossSalary >= 60000) premium = 1800; // check if computed premium exceeds maximum threshold then apply maximum contribution rule
        return premium / 2; // employee only pays 50% of total premium
    }

    // Pag-IBIG Contribution
    // computes employee pag-ibig contribution based on salary
    // 1% if salary <= 1500, otherwise 2%

    /**
     *
     * @param grossSalary
     * @return
     */
    public static double computePagIbig(double grossSalary) {
        double contribution; // declaring varibale that will store contribution value
        if (grossSalary <= 1500)  // check if salary is 1500 or below
            contribution = grossSalary * 0.01; // contribution equals 1% of salary
        else // otherwhise salary is above 1500
            contribution = grossSalary * 0.02; // contribution equals 2% of salary
        return Math.min(contribution, 100); // Math.min ensures contribution never exceeds 100 pesos
    }
    
    // Withholding Tax
    // computes income tax based on taxable income
    // taxable income = gross - sss - philhealth - pag-ibig

    /**
     *
     * @param taxableIncome
     * @return
     */
    public static double computeWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20832)  // if taxable income is within tax free bracket
            return 0; // no tax deduction
        else if (taxableIncome <= 33332) // second income bracket
            return (taxableIncome - 20833) * 0.20; // tax only applies to excess over 20833
        else if (taxableIncome <= 66666) // third income bracket
            return 2500 + ((taxableIncome - 33333) * 0.25); // fixed base tax + 25% of excess
        else if (taxableIncome <= 166666) // fourth income bracket
            return 10833 + ((taxableIncome - 66667) * 0.30); // base tax plus 30% of excess income
        else if (taxableIncome <= 666666) // fifth income bracket
            return 40833.33 + ((taxableIncome - 166667) * 0.32); // base tax plus 32% of excess
        else // highest tax bracket
            return 200833.33 + ((taxableIncome - 666667) * 0.35); // base tax plus 35% of remaining income
    }
    
    // HOURS COMPUTATION
    // calculates how many payable work hours an employee completed

    /**
     *
     * @param login
     * @param logout
     * @return
     */
    public static double computeHours(LocalTime login, LocalTime logout) {
        
        // created a LocalTime object representing 8:10 AM
        LocalTime graceTime = LocalTime.of(8, 10); // grace period allowed until 8:10
        // created LocalTime object representing 5:00 PM
        LocalTime cutoffTime = LocalTime.of(17, 0); // official work end time
        
        // apply 17:00 cut off and check if employee logged out after 5:00 PM
        if (logout.isAfter(cutoffTime)) {
            logout = cutoffTime; // force logout time to become exactly 5:00 PM which prevents overtime counting
        }
        
        // [NEW]: safety validation
        // prevents negative working time if logout < login
        if(logout.isBefore(login)) {
            return 0; // invalid attendance -> no payable hours
        }
        
        // calculate total minutes between login and logout
        // Duration.between finds time difference
        // toMinutes converts result into minutes
        long minutesWorked = Duration.between(login, logout).toMinutes();
        
        // check if employee worked more than 1 hour
        if (minutesWorked > 60) {
            minutesWorked -= 60; // deduct 60 minutes for lunch break
        } else {
            // if work time is too short, employee returns zero payable hours
            minutesWorked = 0;
        }
        
        // grace period rule
        // check if login time is not after grace period
        // meaning employee arrived on or before 8:10 AM
        if (!login.isAfter(graceTime)) {
            return 8.0;
            }
        
        // convert minutes to hours and cap at 8
        return Math.min(minutesWorked / 60.0, 8.0);
    }   
    
    // helper method for displaying payroll information aka Display Payroll Module
    // receives employee info and computed attendance data
    // this method is method chaining where one method orchestrates many smaller methods and this is what you call modular programming

    /**
     *
     * @param empNo
     * @param lastName
     * @param firstName
     * @param birthday
     * @param firstHalf
     * @param secondHalf
     * @param hourlyRate
     * @param monthName
     * @param monthDays
     */
    public static void displayPayroll(
            String empNo, // employee number
            String lastName, // employee last name
            String firstName, // employee first name
            String birthday, // employee birthday
            double firstHalf, // hours worked from day 1-15
            double secondHalf, // hours worked from day 16-end
            double hourlyRate, // employee hourly pay
            String monthName, // name of current month
            int monthDays) { // total days in a month
        
        // compute gross salary for the cutoffs
        // hours worked x hourly rate
        double gross1 = firstHalf * hourlyRate;
        double gross2 = secondHalf * hourlyRate;
        
        // compute total monthly gross salary
        double monthlyGross = gross1 + gross2;
        
        // call deduction methods
        double sss = computeSSS(monthlyGross);
        double phil = computePhilHealth(monthlyGross);
        double pagibig = computePagIbig(monthlyGross);
        double tax = computeWithholdingTax(monthlyGross);
        
        // add all deductions together
        double totalDeductions = sss + phil + pagibig + tax;
        
        // net pay for cutoffs
        double net1 = gross1;
        double net2 = gross2 - totalDeductions; // net pay after deductions
        
        // display employee and payroll information
        System.out.println("\n================ "+ monthName + " ================");
        System.out.println("Employee No.: " + empNo);
        System.out.println("Employee: " + lastName + ", " + firstName);
        System.out.println("Birthday: " + birthday);
        System.out.println("\n===== Cutoff Date: " + monthName + " 1 to 15 ====");
        System.out.println("Total Hours Worked: " + firstHalf);
        System.out.println("Gross Salary: " + gross1);
        System.out.println("Net Salary: " + net1);
        System.out.println("\n==== Cutoff Date: " + monthName + " 16 to " + monthDays + " ====");
        System.out.println("Total Hours Worked: " + secondHalf);
        System.out.println("Gross Salary: " + gross2);
        System.out.println("Deductions");
        System.out.println("    SSS: " + sss);
        System.out.println("    PhilHealth: " + phil);
        System.out.println("    Pag-IBIG: " + pagibig);
        System.out.println("    Tax: " + tax);
        System.out.println("    Total Deductions: " + totalDeductions);
        System.out.println("Net Salary: " + net2);
    }
    
    // helper method tht computes total worked hours
    // for both payroll cutoffs of a specific month

    /**
     *
     * @param empNo
     * @param records
     * @param month
     * @return
     */
    public static double[] computeAttendance(
            String empNo, // employee number to search
            List<Attendance> records, // attendance csv file path
            int month) { // month beeing processed
        
        double firstHalf = 0; // stores total hours from day 1-15
        double secondHalf = 0; // stores total hours from day 16-end
        
        for(Attendance rec : records) {
            if (!rec.empNo.equals(empNo))
                continue;
            if (rec.year != YEAR || rec.month != month)
                continue;
            double hours = computeHours(rec.login, rec.logout);
            
            if (rec.day <= 15) {
                firstHalf += hours;
            } else {
                secondHalf += hours;
            }
        }
        return new double[]{firstHalf, secondHalf};
    }
    
    // helper method for processing payroll for employee
    // for one employee across multiple months
        // finds employee using employee number
    public static String[] findEmployee(List<String[]> employees, String empNo) {
        for (String[] emp : employees) {
            if (emp[COL_EMP_NO].equals(empNo)) {
                return emp;
            }
        }
        return null; // employee not found
    }
    /**
     *
     * @param emp
     * @param EMP_FILE
     * @param records
     */
    // processes payroll for ONE employee
        public static void processPayrollForEmployee(String[] emp, List<Attendance> records) {

            String empNo = emp[COL_EMP_NO];
            String lastName = emp[COL_LAST_NAME];
            String firstName = emp[COL_FIRST_NAME];
            String birthday = emp[COL_BIRTHDAY];
            double hourlyRate = Double.parseDouble(emp[COL_HOURLY_RATE]);

            for (int month = 6; month <= 12; month++) {

                String monthName = switch(month) {
                    case 6 -> "June";
                    case 7 -> "July";
                    case 8 -> "August";
                    case 9 -> "September";
                    case 10 -> "October";
                    case 11 -> "November";
                    case 12 -> "December";
                    default -> "Month "+month;
                };

                int monthDays = YearMonth.of(YEAR, month).lengthOfMonth();

                double[] hours = computeAttendance(empNo, records, month);

                displayPayroll(empNo, lastName, firstName, birthday,
                        hours[0], hours[1], hourlyRate, monthName, monthDays);
            }
        }

    // processes payroll for ALL employees
    public static void processPayrollForAllEmployees(List<String[]> employees, List<Attendance> records) {
        for (String[] emp : employees) {
            processPayrollForEmployee(emp, records);
        }
    }
    
    
    public static void handleEmployeeFlow(Scanner scanner, List<String[]> employees) {

        while(true) {
            System.out.println("\n1. Enter Employee Number");
            System.out.println("\n2. Exit Program");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if(choice == 2) break;

            System.out.print("Enter Employee Number: ");
            String empNo = scanner.nextLine();

            String[] emp = findEmployee(employees, empNo);

            if(emp != null) {
                System.out.println("\n===================================");
                System.out.println("Employee # : " + emp[0]);
                System.out.println("Employee Name : " + emp[1] + ", " + emp[2]);
                System.out.println("Birthday : " + emp[3]);
                System.out.println("===================================");
            } else {
                System.out.println("Employee number does not exist.");
            }
        }
    }    

    public static void handlePayrollFlow(Scanner scanner, List<String[]> employees, List<Attendance> records) {
        while(true) {
            System.out.println("\n1. Process Payroll");
            System.out.println("\n2. Exit Program");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if(choice == 2) break;
            if(choice != 1) {
                System.out.println("Invalid choice. Please input 1 or 2 only.");
                continue;
            }

            System.out.println("\n1. One Employee");
            System.out.println("\n2. All Employees");
            System.out.println("\n3. Exit Program");

            int subChoice = scanner.nextInt();
            scanner.nextLine();

            if(subChoice == 3) break;

            if(subChoice == 1) {
                System.out.print("Enter Employee Number: ");
                String empNo = scanner.nextLine();

                String[] emp = findEmployee(employees, empNo);

                if(emp != null)
                    processPayrollForEmployee(emp, records);
                else
                    System.out.println("Employee number does not exist.");
            }

            if(subChoice == 2) {
                processPayrollForAllEmployees(employees, records);
            }
        }
    }    
    // ==== MAIN PROGRAM ====
    // execution of the Java program starts here

    /**
     *
     * @param args
     */

     private static boolean authenticate(String username, String password) {
        return (username.equals("employee") && password.equals("12345")) ||
               (username.equals("payroll_staff") && password.equals("12345"));
    }
    
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        List<String[]> employees = loadEmployees(EMP_FILE);
        List<Attendance> attendanceRecords = loadAttendance(ATT_FILE);

        System.out.println("==== MOTORPH LOGIN ====");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (authenticate(username, password)) {
            System.out.println("Login Successful!"); // show success message

            // continue to employee menu if employee logs in
            if (username.equals("employee")) {
                handleEmployeeFlow(scanner, employees);
            }
            // continue to payroll menu if payroll staff logs in
            else if (username.equals("payroll_staff")) {
                handlePayrollFlow(scanner, employees, attendanceRecords);
            }
        } else {
            System.out.println("Incorrect username or password"); // show error message
        }
    }
}    
