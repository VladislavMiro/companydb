package sample.model;

public class Employer {
    private Integer id;
    private String name;
    private String lastName;
    private String partherName;
    private String position;
    private Department department;
    private Integer salary;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPartherName(String partherName) {
        this.partherName = partherName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public void setDepartment(Department department) { this.department = department; }

    //public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPartherName() {
        return partherName;
    }

    public String getPosition() {
        return position;
    }

    public Integer getSalary() {
        return salary;
    }

    public Department getDepartment() { return department; }

    //public Integer getDepartmentId() { return departmentId; }

    public Employer(Integer id, String name, String lastName, String partherName, String position, Integer salary, Department department) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.partherName = partherName;
        this.position = position;
        this.salary = salary;
        this.department = department;
    }

    public Employer(String name, String lastName, String partherName, String position, Integer salary, Department department) {
        this.name = name;
        this.lastName = lastName;
        this.partherName = partherName;
        this.position = position;
        this.salary = salary;
        this.department = department;
    }

}
