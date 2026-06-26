
public class Employee {

    private String id;
    private String name;
    private String department;
    private double basic_salary;

    public Employee(String id, String name, String department, double basic_salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.basic_salary = basic_salary;
    }

    public Employee() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getBasic_salary() {
        return basic_salary;
    }

    public void setBasic_salary(double basic_salary) {
        this.basic_salary = basic_salary;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Employee{");
        sb.append("id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", department=").append(department);
        sb.append(", basic_salary=").append(basic_salary);
        sb.append('}');
        return sb.toString();
    }



}
