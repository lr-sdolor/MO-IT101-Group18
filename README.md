# PayrollSystem
MotorPH Payroll System Phase 1 for Computer Programming 1
[Project Plan](https://docs.google.com/spreadsheets/d/1jUmsr0bWvf2RTDSNxkiFwG7VYSIKXbep9_OckFVlx0w/edit?gid=2134013708#gid=2134013708)

---

## Developers

Synen Dolor
- Lead Developer and Code Reviewer. Responsible for structuring the overall codebase and implementing the main program logic. Integrated the different components of the system, handled file processing, debugging, and ensured that the code followed a consistent structure. Also reviewed and refined the computation logic drafted during the planning phase.

Moriah Edoc
- Participated in drafting the initial logic for the hours computation and deduction rules during the planning stage and contributed ideas for how the calculations should be handled in the program.

John Merick Carryle Tarcena
- Assisted in the initial drafting of the computation and deduction logic and provided input during the early planning and review discussions.

Yves Lauren Marcaida
- Contributed to drafting the computation approach and participated in reviewing the proposed deduction logic during the design phase.

Ma. Diana Salvidar
- Assisted in outlining the hours computation and deduction rules and participated in discussions regarding how these should be applied in the system.

MotorPH Payroll System Development Team

---

# MotorPH Payroll System

A Java-based payroll processing system that allows employees to view employee details and payroll staff to compute and process payroll for one or all employees. The program handles attendance, computes hours worked, and calculates deductions and taxes.

---

## System Overview

The application reads employee and attendance data from CSV files and performs automated payroll computation. Depending on the user role, the system provides access either to employee information viewing or payroll processing functions.

Users are required to log in before accessing the system:

- Employee login (`employee`) allows viewing of employee details.
- Payroll staff login (`payroll_staff`) enables payroll computation and processing.

Default password for both accounts: 12345

---

## Employee Functions

After successful login, employees may enter their employee number to view stored personal information retrieved from the employee database. The system displays:
- Employee Number  
- Employee Name  
- Birthday  

---

## Payroll Processing

Payroll staff are provided with options to process payroll for a single employee or for all employees in the system. Payroll computation covers the period from **June to December 2024**.

The payroll module automatically computes:
- Total hours worked
- Gross salary per cut-off
- Government deductions
- Net salary per cut-off

---

## Attendance Processing

Attendance information is obtained from: attendance_record.csv

Employee working hours are evaluated using the following rules:
- Standard working hours: **8 hours per day**
- Login before **8:10 AM** counts as a full work shift
- One-hour lunch break deduction when applicable
- Maximum credited hours per day: **8 hours**

---

## Payroll Computation Method

Employee salary is calculated using the formula: Hourly Rate = Monthly Salary ÷ 21 Working Days ÷ 8 Hours

Payroll is divided into two cut-off periods:
- **First Cut-Off:** Day 1–15  
- **Second Cut-Off:** Day 16–End of Month  

Mandatory deductions automatically applied include:
- SSS Contribution
- PhilHealth Contribution (Employee Share)
- Pag-IBIG Contribution
- Withholding Tax

---

## CSV File Handling

The system relies on the following files:

- `employee_details.csv` – employee information  
- `attendance_record.csv` – daily attendance records  

CSV handling includes:
- Support for quoted fields
- Skipping invalid or empty rows
- Prevention of processing errors caused by incomplete data

---

## System Requirements

To run the program, the following are required:
- Java 8 or higher
- Maven
- Apache NetBeans IDE

---

## Running the Program

### Using NetBeans
1. Open Apache NetBeans IDE
2. Open the PayrollSystem project
3. Right-click the project
4. Select **Run Project**

### Using Terminal
mvn clean compile
mvn exec:java

---

## Error Handling

The system includes validation mechanisms to handle:
- Invalid employee numbers
- Missing CSV entries
- Incorrect login credentials
- Empty or malformed attendance records

---

## License

This project was developed for academic purposes under Computer Programming 1 coursework requirements.
