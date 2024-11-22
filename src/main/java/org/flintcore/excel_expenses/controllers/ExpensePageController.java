package org.flintcore.excel_expenses.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import javafx.util.Duration;
import org.flintcore.excel_expenses.expenses.ExpenseBuilderHolder;
import org.flintcore.excel_expenses.managers.routers.ApplicationRouter;
import org.flintcore.excel_expenses.managers.routers.expenses.EExpenseRoute;
import org.flintcore.excel_expenses.models.files.EFileExtension;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.services.excels.XSSFExcelExpenseExportFXService;
import org.flintcore.excel_expenses.services.receipts.ReceiptFileScheduledFXService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;


@Component
public class ExpensePageController implements Initializable {

    public static final int TIME_LONG = 20;
    private final ApplicationRouter appRouter;
    private final XSSFExcelExpenseExportFXService expenseExportService;
    private final ReceiptFileScheduledFXService receiptFileService;

    public AtomicBoolean isExporting;

    public ExpensePageController(
            ApplicationRouter appRouter,
            XSSFExcelExpenseExportFXService expenseExportService,
            ReceiptFileScheduledFXService receiptFileService) {
        this.expenseExportService = expenseExportService;
        this.appRouter = appRouter;
        this.receiptFileService = receiptFileService;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.btnCreateExpense.setOnAction(
                evt -> this.appRouter.navigateTo(EExpenseRoute.CREATE)
        );
    }

    @FXML
    public void requestExportFile() {
        if (Objects.isNull(isExporting)) isExporting = new AtomicBoolean();

        if (!isExporting.compareAndSet(false, true)) return;

        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);

            final PauseTransition pauseTransition = new PauseTransition(Duration.seconds(5D));
            pauseTransition.setOnFinished(evt -> alert.close());

            var receiptList = this.receiptFileService.getDataList();

            try {
                ObservableList<Receipt> receipts = receiptList.get(
                        TIME_LONG, TimeUnit.SECONDS
                );

                FileChooser fileChooser = new FileChooser();

                fileChooser.setTitle("Expense Export File");

                var fileExtension = EFileExtension.XLSX;

                fileChooser.getExtensionFilters().addAll(
                        new ExtensionFilter("2007 Excel files",
                                "*%s".formatted(fileExtension.asDotExtension())
                        )
                );

                Calendar calendar = Calendar.getInstance();

                fileChooser.setInitialDirectory(new File("C:/"));

                fileChooser.setInitialFileName("expenses_%d_%d%s".formatted(
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR),
                        fileExtension.asDotExtension()
                ));

                var selectedFile = fileChooser.showSaveDialog(getCurrentWindows());

                alert.show();

                if (Objects.isNull(selectedFile)) {

                    alert.setAlertType(AlertType.CONFIRMATION);
                    alert.setTitle("Action canceled!");
                    alert.setHeaderText(null);
                    alert.setContentText(null);

                    pauseTransition.play();
                }

                Path selectedFilePath = selectedFile.toPath();

                if (selectedFile.exists()) selectedFile.delete();

                this.expenseExportService.setExpenseFileHolder(
                        new ExpenseBuilderHolder(null, "Expenses Data",
                                "expenses of %d".formatted(calendar.get(Calendar.MONTH)),
                                receipts
                        )
                );

                this.expenseExportService.setNewExpenseLocation(selectedFilePath);

                alert.show();

                alert.contentTextProperty().bind(
                        this.expenseExportService.messageProperty().orElse("")
                );

                this.expenseExportService.setOnFailed(evt -> isExporting.set(false));

                this.expenseExportService.setOnSucceeded(evt -> {
                    // All done
                    isExporting.set(false);

                    alert.contentTextProperty().unbind();
                    alert.setAlertType(AlertType.CONFIRMATION);
                    alert.setHeaderText("Completed!");
                    alert.setContentText("Data exported successfully!");
                });

                this.expenseExportService.restart();
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                alert.setAlertType(AlertType.ERROR);
                alert.setHeaderText("Failed ");

                pauseTransition.play();
            }
        });
    }

    private Window getCurrentWindows() {
        return receiptListView.getScene().getWindow();
    }

    @FXML
    private Button btnCreateExpense;

    @FXML
    private Button btnExportFile;

    @FXML
    private VBox receiptListView;
}
