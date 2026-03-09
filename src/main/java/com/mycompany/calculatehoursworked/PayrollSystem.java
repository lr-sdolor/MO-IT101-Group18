/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.calculatehoursworked;

/**
 *
 * @author synen
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

// ===== MAIN CLASS =====
// declare a public class named PayrollSystem
// all program logic exists inside this class
public class PayrollSystem {
    
    // ==== METHODS ====
    // SSS Contribution
    // public -> accessible everywhere
    // static -> can run without creating an object
    // double -> returns decimal value
    // computeSSS -> method name
    // grossSalary -> input parameter
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
    public static double computePhilHealth(double grossSalary) {
        double premium = grossSalary * 0.03; // compute 3% of monthly basic/gross
        if (grossSalary <= 10000) premium = 300; // check if computed premium is below minimum threshold then force minimum contribution rule
        else if (grossSalary >= 60000) premium = 1800; // check if computed premium exceeds maximum threshold then apply maximum contribution rule
        return premium / 2; // employee only pays 50% of total premium
    }

    // Pag-IBIG Contribution
    // computes employee pag-ibig contribution based on salary
    // 1% if salary <= 1500, otherwise 2%
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
        
        // convert minutes into decimal hours
        // example: 420 minutes/60 = 7 hours
        double hours = minutesWorked / 60.0;
        
        // grace period rule
        // check if login time is not after grace period
        // meaning employee arrived on or before 8:10 AM
        if (!login.isAfter(graceTime)) {
            return 8.0;
            }
        
        // return final payable hours, capped at 8
        return Math.min(hours, 8.0);
    }   
    
    // helper method for displaying payroll information aka Display Payroll Module
    // receives employee info and computed attendance data
    // this method is method chaining where one method orchestrates many smaller methods and this is what you call modular programming
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
    public static double[] computeAttendance(
            String empNo, // employee number to search
            String attFile, // attendance csv file path
            int month) { // month beeing processed
        
        double firstHalf = 0; // stores total hours from day 1-15
        double secondHalf = 0; // stores total hours from day 16-end
        
        // formatter used to interpret time text like "8:30"
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
        
        // try-with-resources automatically closes fiel after use
        try(BufferedReader br = new BufferedReader(new FileReader(attFile))) {
            
            br.readLine(); // read first line of csv (header row) and ignore it
            String line; // variable that will store each line from file
            
            // read file continuously until no lines remain
            while((line = br.readLine()) != null) {
                if(line.trim().isEmpty()) continue; // skip empty lines
                
                // split csv values into columns
                // regex prevents breaking commas inside quotes
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                // check if record belongs to selected employee
                if(!data[0].equals(empNo)) continue;
                
                // split data column (example: 08/23/2024)
                String[] dateParts = data[3].split("/");
                int recordMonth = Integer.parseInt(dateParts[0]); // extract month from date
                int day = Integer.parseInt(dateParts[1]); // extract day
                int year = Integer.parseInt(dateParts[2]); // extract year
                if(year!=2024 || recordMonth != month) continue; // ignore records not from 2024 or not matching requested month
                // convert login and logout time string into LocalTime object
                LocalTime login = LocalTime.parse(data[4].trim(), timeFormat);
                LocalTime logout = LocalTime.parse(data[5].trim(), timeFormat);
                
                // check if attendance belongs to first cut off
                double hours = computeHours(login, logout);
                if(day <= 15) {
                    firstHalf += hours; // add hours to first half total
                } else {
                    secondHalf += hours; // otherwise add hours to second half total
                }
            }
        } catch(Exception e) { // catch errors like missing file or bad data
            System.out.println("Error reading attendance file for month " + month); // print readbale error message
            e.printStackTrace(); // print detailed technical error for debugging
        }
        
        // return both cutoff totals as an array
        return new double[] {
            firstHalf, // index 0
            secondHalf // index 1
        };
    }
    
    // helper method for processing payroll for employee
    // for one employee across multiple months
    public static void processPayrollForEmp(String empNo, String empFile, String attFile) {
        // try-with-resources automatically closes the file
        try(BufferedReader br = new BufferedReader(new FileReader(empFile))) {
            br.readLine(); //skip header
            String line; // variable that stores each line read from file
            boolean found = false; // flag used to check if employee exists
            while((line = br.readLine()) != null) { // loop through entire employee file
                if(line.trim().isEmpty()) continue; // skip empty lines
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // split csv row into columns safely because for some reason hourlyRate kept getting returned as "1"; reason being that the filereader parses it incorrectly due to the existing commas in some of the columns/indices in employee_details.csv 
                if(data[0].equals(empNo)) { // check if employee number matches input
                    String lastName = data[1]; // store employee last name
                    String firstName = data[2];// store employee first name
                    String birthday = data[3]; // store birthday
                    double hourlyRate = Double.parseDouble(data[18]); // convert hourly rate text into double value
                    found = true; // mark employee as found
                    
                    // Loop months from June to Recember
                    for(int month = 6; month <= 12; month++) {
                        String monthName = switch(month) { // convert month number into readable name
                            case 6 -> "June";
                            case 7 -> "July";
                            case 8 -> "August";
                            case 9 -> "September";
                            case 10 -> "October";
                            case 11 -> "November";
                            case 12 -> "December";
                            default -> "Month "+month; // fallback safety case
                        };
                        int monthDays = YearMonth.of(2024, month).lengthOfMonth(); // determine number of days in month
                        double[] hours = computeAttendance(empNo, attFile, month); // compute attendance hours
                        displayPayroll(empNo, lastName, firstName, birthday, hours[0], hours[1], hourlyRate, monthName, monthDays); // display payroll using computed data
                    }
                    break; // stop searching once employee is processed
                }
            }
            if(!found) { // if employee was never found in file
                System.out.println("Employee number does not exist."); // inform user employee number is valid
            } 
        } catch(Exception e) { // catch file or parsing errors
                System.out.println("Error reading employee file."); // display readable error message
                e.printStackTrace(); // print technical debugging details/errors
        }
    }
    
    // ==== MAIN PROGRAM ====
    // executio of the Java program starts here
    public static void main(String[] args) {
        // create scanner object to allow keyboard input
        Scanner scanner = new Scanner(System.in);
        
        // display login system title
        System.out.println("==== MOTORPH LOGIN ====");
        System.out.print("Username: "); // ask use to enter username
        String username = scanner.nextLine().trim(); // read username input and remove extra spaces using trim()
        System.out.print("Password: "); // ask user to enter password
        String password = scanner.nextLine().trim(); // read password input and remove extra spaces using trim()
        
        if(username.equals("employee") && password.equals("12345")) { // check if login matches employee account
            System.out.println("Login Successful!"); // login success message
        }
        else if (username.equals("payroll_staff") && password.equals("12345")) { // check if login matches payroll staff account
            System.out.println("Login Sucessful!"); // login success message
        }
        else { // run if username or password is incorrect
            System.out.println("Incorrect username or password"); // deny system access
        }
        
        // path location of employee information csv file
        String empFile = "src\\main\\java\\com\\mycompany\\calculatehoursworked\\employee_details.csv";
        // path location of attendance csv file
        String attFile = "src\\main\\java\\com\\mycompany\\calculatehoursworked\\attendance_record.csv";
        
        // employee flow
        if(username.equals("employee") && password.equals("12345")) { // check if logged-in user is an employee
            while(true) { // infinite loop until user exits
                // show employee menu or options
                System.out.println("\n1. Enter Employee Number");
                System.out.println("\n2. Exit Program");
                int choice = scanner.nextInt(); // read menu choice
                scanner.nextLine(); // consume leftover newline character
                
                if(choice == 2) { // exit program if user selects option 2
                    break;
                }
                
                System.out.print("Enter Employee Number: "); // ask for employee number
                String empNo = scanner.nextLine(); // read employee number
                boolean found = false; // flag to check if employee exists
                
                // opem employee file safely
                try(BufferedReader br = new BufferedReader(new FileReader(empFile))) {
                    br.readLine(); // skip header
                    String line;
                    while((line = br.readLine()) != null) { // continue reading until end of line
                        if(line.trim().isEmpty()) continue; // skip empty lines
                        String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // split csv safely with quoted commas
                        if(data[0].equals(empNo)) { // check if employee number matches output
                            // int COL_EMP_NO = 0;
                            // if(data[COL_EMP_NO].equals(empNo))
                            found = true; // employee exists
                            // display employee information
                            System.out.println("\n===================================");
                            System.out.println("Employee # : " + data[0]);
                            System.out.println("Employee Name : " + data[1] + ", " + data[2]);
                            System.out.println("Birthday : " + data[3]);
                            System.out.println("===================================");
                            break; // stop searching once found
                        }
                    }
                    if(!found) {
                        System.out.println("Employee number does not exist."); // show error if employee is not found
                    } 
                } catch(Exception e) {
                    e.printStackTrace();// print technical error info
                }
            }
        }
        // check if payroll staff logged in
        if(username.equals("payroll_staff") && password.equals("12345")) {
            while(true) {
                System.out.println("\n1. Process Payroll");
                System.out.println("\n2. Exit Program");
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 2) {
                    break;
                } else if (choice != 1) {
                    System.out.println("Invalid choice. Please input 1 or 2 only.");
                    continue;
                }
                
                System.out.println("\n1. One Employee");
                System.out.println("\n2. All Employees");
                System.out.println("\n3. Exit Program");
                int subChoice = scanner.nextInt();
                scanner.nextLine();
                if (subChoice == 3) {
                    break;
                } else if(subChoice != 1 && subChoice != 2) {
                    System.out.println("Invalid choice. Please input 1-3 only.");
                    continue;
                }
                
                if(subChoice == 1) {
                    System.out.print("Enter Employee Number: ");
                    String empNo  = scanner.nextLine();
                    processPayrollForEmp(empNo, empFile, attFile); // call payroll processor method
                }
                
                if(subChoice == 2) {
                    try(BufferedReader br = new BufferedReader(new FileReader(empFile))) {
                        br.readLine(); // skip header
                        String line;
                        while((line = br.readLine()) != null) { // loop all employees
                            if(line.trim().isEmpty()) { // skip blanks
                                continue;
                            }
                            String empNo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")[0]; // extract employee ids
                            processPayrollForEmp(empNo, empFile, attFile); // call payroll processor method to process payroll for each employee
                        }
                    } catch(Exception e) {
                        e.printStackTrace(); // error handling again
                    }
                }
            }
        }
    }
}    