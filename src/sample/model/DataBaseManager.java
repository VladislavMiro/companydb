package sample.model;

import java.sql.*;
import java.util.ArrayList;

public class DataBaseManager {
    private static DataBaseManager shared = new DataBaseManager();
    private static String url;
    private static String username;
    private static String password;
    private static Connection connection = null;
    private static User user = null;

    public static DataBaseManager getShared() {
        if(shared == null) {
            shared = new DataBaseManager();
        }

        return shared;
    }

    private DataBaseManager() {}

    public void setConfiguration(String url, String username, String password) {
        DataBaseManager.url = url;
        DataBaseManager.username = username;
        DataBaseManager.password = password;
    }

    public User getUser() { return user; }

    public void connect() throws SQLException, ClassNotFoundException {
        System.out.println("Тестирование подключения к базе данных:");
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(url, username, password);
    }

    public void disconnect() throws SQLException { connection.close(); }

    public boolean auth(String login, String password) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("SELECT checkPassword(?,?);");
        statement.setString(1,login);
        statement.setString(2,password);

        try(ResultSet request = statement.executeQuery()) {
            request.next();
            return request.getBoolean(1);
        }
    }

    public void loadUser(String login) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("SELECT u.id, u.login, u.role FROM users u WHERE u.login=?;");
        statement.setString(1, login);

        try (ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            user = new User(resultSet.getInt(1), resultSet.getString(2), UserRole.values()[resultSet.getInt(3)]);
        }

    }

    public ArrayList<Employer> getEmployeers() throws SQLException, ClassNotFoundException {
        connect();
        ArrayList<Employer> array = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("SELECT id, first_name, last_name, pather_name, _position, salary, dep_id, dep_name FROM getEmployees();");
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Department department = null;
                if (resultSet.getInt(7) != Types.NULL)
                    department = new Department(resultSet.getInt(7), resultSet.getString(8));
                array.add(new Employer(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getString(5), resultSet.getInt(6), department));
            }
        }
        return array;
    }

    public void insertEmployee(Employer employer) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("CALL insertEmployee(?, ?, ?, ?, ?, ?);");
        statement.setString(1, employer.getName());
        statement.setString(2, employer.getLastName());
        statement.setString(3, employer.getPartherName());
        if (employer.getPosition() == null || employer.getPosition().equals("")) {
            statement.setNull(4, Types.NULL);
        } else {
            statement.setString(4, employer.getPosition());
        }
        statement.setInt(5, employer.getSalary());
        if (employer.getDepartment() == null) {
            statement.setNull(6, Types.NULL);
        } else {
            statement.setInt(6, employer.getDepartment().getId());
        }
        statement.executeUpdate();
    }

    public void updateEmployee(Employer employer) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("CALL updateEmployee(?, ?, ?, ?, ?, ?, ?);");
        statement.setInt(1, employer.getId());
        statement.setString(2, employer.getName());
        statement.setString(3, employer.getLastName());
        statement.setString(4, employer.getPartherName());
        if (employer.getPosition() == null) {
            statement.setNull(5, Types.NULL);
        } else {
            statement.setString(5, employer.getPosition());
        }
        statement.setInt(6, employer.getSalary());
        if (employer.getDepartment() == null) {
            statement.setNull(7, Types.NULL);
        } else {
            statement.setInt(7, employer.getDepartment().getId());
            System.out.println(employer.getDepartment().getId());
        }
        statement.executeUpdate();
    }

    public void deleteEmployee(Employer employer) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("CALL deleteEmployee(?)");
        statement.setInt(1, employer.getId());
        statement.executeUpdate();
    }

    public ArrayList<Project> getProjects() throws SQLException, ClassNotFoundException {
        ArrayList<Project> array = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("SELECT id, name, cost, department_id, dep_name, date_beg, date_end, date_end_real FROM getProjects();");
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Department department = null;
                if (resultSet.getInt(4) != Types.NULL)
                    department = new Department(resultSet.getInt(4), resultSet.getString(5));
                array.add(new Project(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getInt(3), department, resultSet.getDate(6),
                        resultSet.getDate(7), resultSet.getDate(8)));
            }
        }
        return array;
    }

    public void insertProject(Project project) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("CALL insertProject(?, ?, ?, ?, ?)");
        statement.setString(1, project.getName());
        if (project.getPrice() == null) {
            statement.setNull(2, Types.NULL);
        } else {
            statement.setInt(2, project.getPrice());
        }
        if (project.getDepartment() == null) {
            statement.setNull(3, Types.NULL);
        } else {
            statement.setInt(3, project.getDepartment().getId());
        }
        if (project.getDateBeg() == null) {
            statement.setNull(4, Types.NULL);
        } else {
            statement.setDate(4, project.getDateBeg());
        }
        if (project.getDateEnd() == null) {
            statement.setNull(5, Types.NULL);
        } else {
            statement.setDate(5, project.getDateEnd());
        }
        statement.executeUpdate();
    }

    public void updateProject(Project project) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("CALL updateProject(?, ?, ?, ?, ?, ?, ?)");
        statement.setInt(1, project.getId());
        statement.setString(2, project.getName());
        if (project.getPrice() == null) {
            statement.setNull(3, Types.NULL);
        } else {
            statement.setInt(3, project.getPrice());
        }
        if (project.getDepartment() == null) {
            statement.setNull(4, Types.NULL);
        } else {
            statement.setInt(4, project.getDepartment().getId());
        }
        if (project.getDateBeg() == null) {
            statement.setNull(5, Types.NULL);
        } else {
            statement.setDate(5, project.getDateBeg());
        }
        if (project.getDateEnd() == null) {
            statement.setNull(6, Types.NULL);
        } else {
            statement.setDate(6, project.getDateEnd());
        }
        if (project.getDateEndReal() == null) {
            statement.setNull(7, Types.NULL);
        } else {
            statement.setDate(7, project.getDateEndReal());
        }
        statement.executeUpdate();
    }

    public void deleteProject(Project project) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("CALL deleteProject(?);");
        statement.setInt(1, project.getId());
        statement.executeUpdate();
    }

    public ArrayList<Department> getDepartments() throws SQLException, ClassNotFoundException {
        ArrayList<Department> array = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM getDepartments();");

        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                array.add(new Department(resultSet.getInt(1), resultSet.getString(2)));
            }
        }

        return array;
    }

    public void insertToDepartments(String name) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("CALL insertDepartments(?);");
        statement.setString(1, name);
        statement.executeUpdate();
    }

    public void updateDepartments(Department department) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("CALL updateDepartments(?, ?);");
        statement.setString(1, department.getName());
        statement.setInt(2, department.getId());
        statement.executeUpdate();
    }

    public void deleteFromDepartments(Department department) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = connection.prepareStatement("CALL deleteDepartment(?);");
        statement.setInt(1, department.getId());
        statement.executeUpdate();
    }

    public ArrayList<Report> getReportProfitAtTable(Date date) throws SQLException {
        ArrayList<Report> reportArray = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("SELECT _name, _cost, _date_beg, _date_end, _date_end_real FROM profitAtTable(?);");
        statement.setDate(1, date);
        try(ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reportArray.add(new Report(resultSet.getString(1), resultSet.getInt(2),
                        resultSet.getDate(3), resultSet.getDate(4), resultSet.getDate(5)));
            }
        }

        return reportArray;
    }

    public Integer getReportProfitAt(Date date) throws SQLException {
        Integer profit = null;

        PreparedStatement statement = connection.prepareStatement("SELECT profitat FROM profitAt(?);");
        statement.setDate(1, date);
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                profit = resultSet.getInt(1);
            }
        }

        return profit;
    }

    public ArrayList<Report> getReportFutureProfit() throws SQLException {
        ArrayList<Report> reports = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("SELECT _name, _cost, _date_beg, _date_end, _profit FROM futureProfit();");

        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reports.add(new Report(resultSet.getString(1), resultSet.getInt(2),
                        resultSet.getDate(3), resultSet.getDate(4), resultSet.getInt(5)));
            }
        }

        return reports;
    }

}
