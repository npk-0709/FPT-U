import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MyFileManager {
    private String fileName; // name of the file to manage

    public MyFileManager(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
    // setter for fileName


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void writeAnEmployeeToFile(Employee employee) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(fileName, true));
            printWriter.println(employee.toString()); // write employee info to file
            printWriter.close(); // close the writer
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage()); // log error message
        }
    }

    public void writeListOfEmployeesToFile(List<Employee> employeeList) {
        if (employeeList == null || employeeList.isEmpty()) {
            System.out.println("Employee list is empty. Nothing to write to file.");
            return;
        }
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(fileName, true));
            for (Employee employee : employeeList) {
                printWriter.println(employee.toString());
            }
            printWriter.close();
        } catch (IOException exception) {
            System.err.println("Error: " + exception.getMessage());
        }
    }
}
