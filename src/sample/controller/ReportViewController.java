package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import sample.model.DataBaseManager;
import sample.model.Report;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReportViewController {

    @FXML
    private TableView<Report> tableView;

    @FXML
    private TableColumn<Report, String> name;

    @FXML
    private TableColumn<Report, Integer> price;

    @FXML
    private TableColumn<Report, Date> dateBeg;

    @FXML
    private TableColumn<Report, Date> dateEnd;

    @FXML
    private TableColumn<Report, Date> dateEndReal;

    @FXML
    private TableColumn<Report, Double> profit;

    @FXML
    private Label profitLabel;

    @FXML
    private Button saveButton;

    private ArrayList<Report> reports = new ArrayList<>();

    private Integer profitAt = null;

    @FXML
    private DatePicker dateStartPicker;

    @FXML
    private ComboBox<String> reportType;

    @FXML
    void addButtonAction() {
        switch (reportType.getSelectionModel().getSelectedIndex()) {
            case 0:
                if (!dateStartPicker.getValue().isAfter(LocalDate.now()))
                try {
                    reports = DataBaseManager.getShared().getReportProfitAtTable(Date.valueOf(dateStartPicker.getValue()));
                    profitAt = DataBaseManager.getShared().getReportProfitAt(Date.valueOf(dateStartPicker.getValue()));
                    fillTableView(FXCollections.observableArrayList(reports));
                    if (profitAt != null) profitLabel.setText("Итоги: " + profitAt.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                } else {
                    showAlert("Внимание", "Дата еще не наступила!", Alert.AlertType.INFORMATION);
                }
                break;
            case 1:
                Integer sumProfit = 0;
                try {
                    reports = DataBaseManager.getShared().getReportFutureProfit();
                    fillTableView(FXCollections.observableArrayList(reports));
                    for (Report report : reports) {
                        if (report.getProfit() != null)
                            sumProfit += report.getProfit();
                    }
                    profitLabel.setText("Итоги: " + sumProfit.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        saveButton.setDisable(reports.isEmpty());
    }

    @FXML
    void saveButtonAction() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage window = (Stage) dateStartPicker.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            saveFile(file);
        } else {
            showAlert("Ошибка", "Не удалось сохранить файл!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void initialize() {
        ObservableList<String> reportTypeList = FXCollections.observableArrayList("Полученная прибыль за период", "Предполагаемая прибыль");
        reportType.setItems(reportTypeList);
        reportType.getSelectionModel().select(0);
        dateStartPicker.setValue(LocalDate.now());
        profit.setVisible(false);
        saveButton.setDisable(true);
    }

    @FXML
    void reportTypeSelectAction() {
        switch (reportType.getSelectionModel().getSelectedIndex()) {
            case 0:
                dateStartPicker.setDisable(false);
                profit.setVisible(false);
                dateEndReal.setVisible(true);
                break;
            case 1:
                dateEndReal.setVisible(false);
                profit.setVisible(true);
                dateStartPicker.setDisable(true);
                break;
            default:
                break;
        }
    }

    private void fillTableView(ObservableList<Report> array) {
        name.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        price.setCellValueFactory(new PropertyValueFactory<>("projectCost"));
        dateBeg.setCellValueFactory(new PropertyValueFactory<>("dateBegin"));
        formatDateCell(dateBeg);
        dateEnd.setCellValueFactory(new PropertyValueFactory<>("dateEnd"));
        formatDateCell(dateEnd);
        dateEndReal.setCellValueFactory(new PropertyValueFactory<>("dateRealEnd"));
        formatDateCell(dateEndReal);
        profit.setCellValueFactory(new PropertyValueFactory<>("profit"));
        tableView.setItems(array);
    }

    private void formatDateCell(TableColumn<Report, Date> colum) {
        colum.setCellFactory(column -> {
            TableCell<Report, Date> cell = new TableCell<>() {
                private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(format.format(item));
                    }
                }
            };
            return cell;
        });
    }

    private void showAlert(String title ,String message,  Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void saveFile(File file) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Employees sheet");
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
        int rowNum = 0;
        String[] header;
        if (reportType.getSelectionModel().getSelectedIndex() == 0) {
            header = new String[]{"Название", "Цена", "Дата начала", "Дата сдачи", "Реальная дата сдачи"};
        } else {
            header = new String[]{"Название", "Цена", "Дата начала", "Дата сдачи", "Прибыль"};
        }
        Cell cell;
        Row row;
        row = sheet.createRow(rowNum);
        int cellNum = 0;
        for (String cellData : header) {
            sheet.setColumnWidth(cellNum, 6000);
            cell = row.createCell(cellNum++, CellType.STRING);
            cell.setCellValue(cellData);
            cell.setCellStyle(style);
        }

        rowNum = 1;

        for (Report report : reports) {
            row = sheet.createRow(rowNum);
            cellNum = 0;
            cell = row.createCell(cellNum, CellType.STRING);
            cell.setCellValue(report.getProjectName());
            cell = row.createCell(++cellNum, CellType.NUMERIC);
            cell.setCellValue(report.getProjectCost());
            cell = row.createCell(++cellNum);
            cell.setCellValue(report.getDateBegin());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(++cellNum);
            cell.setCellValue(report.getDateEnd());
            cell.setCellStyle(cellStyle);
            if (reportType.getSelectionModel().getSelectedIndex() == 0) {
                cell = row.createCell(++cellNum);
                cell.setCellValue(report.getDateRealEnd());
                cell.setCellStyle(cellStyle);
            } else {
                cell = row.createCell(++cellNum, CellType.NUMERIC);
                cell.setCellValue(report.getProfit());
            }
            rowNum++;
        }

        row = sheet.createRow(rowNum);
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Итог:");
        cell.setCellStyle(style);
        cell = row.createCell(1, CellType.NUMERIC);
        if (reportType.getSelectionModel().getSelectedIndex() == 0) {
            cell.setCellValue(profitAt);
        } else {
            Integer sum = 0;
            for (Report report : reports)
                sum += report.getProfit();
            cell.setCellValue(sum);
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось сохранить файл!", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

}
