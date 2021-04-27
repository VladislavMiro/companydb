package sample.model;

import java.sql.Date;

public class Project {
    private Integer id;
    private String name;
    private Integer price;
    private Department department;
    private Date dateBeg;
    private Date dateEnd;
    private Date dateEndReal;

    public Project(Integer id, String name, Integer price, Department department, Date dateBeg, Date dateEnd, Date dateEndReal) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.department = department;
        this.dateBeg = dateBeg;
        this.dateEnd = dateEnd;
        this.dateEndReal = dateEndReal;
    }

    public Project(String name, Integer price, Department department, Date dateBeg, Date dateEnd) {
        this.name = name;
        this.price = price;
        this.department = department;
        this.dateBeg = dateBeg;
        this.dateEnd = dateEnd;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepName(Department department) {
        this.department = department;
    }

    public Date getDateBeg() {
        return dateBeg;
    }

    public void setDateBeg(Date dateBeg) {
        this.dateBeg = dateBeg;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Date getDateEndReal() {
        return dateEndReal;
    }

    public void setDateEndReal(Date dateEndReal) {
        this.dateEndReal = dateEndReal;
    }
}
