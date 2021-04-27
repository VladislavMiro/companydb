package sample.model;

import java.sql.Date;

public class Report {
    private final String projectName;
    private final Integer projectCost;
    private final Date dateBegin;
    private final Date dateEnd;
    private final Date dateRealEnd;
    private final Integer profit;

    public Report(String projectName, Integer projectCost, Date dateBegin, Date dateEnd, Date dateRealEnd) {
        this.projectName = projectName;
        this.projectCost = projectCost;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.dateRealEnd = dateRealEnd;
        this.profit = null;
    }

    public Report(String projectName, Integer projectCost, Date dateBegin, Date dateEnd, Integer profit) {
        this.projectName = projectName;
        this.projectCost = projectCost;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.dateRealEnd = null;
        this.profit = profit;
    }

    public String getProjectName() { return projectName; }

    public Integer getProjectCost() { return projectCost; }

    public Date getDateBegin() { return dateBegin; }

    public Date getDateEnd() { return dateEnd; }

    public Date getDateRealEnd() { return dateRealEnd; }

    public Integer getProfit() { return profit; }

}
