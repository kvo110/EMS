package com.employeemgmt.ui.fx;

import com.employeemgmt.models.User;
import com.employeemgmt.services.EmployeeService;
import com.employeemgmt.services.EmployeeService.SearchResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
   Lets admin:
   - raise salaries for ALL employees
   - raise salaries for employees in a SALARY RANGE
*/
public class SalaryToolsScreen {

    private final EmployeeService service = new EmployeeService();

    public void start(Stage stage, User admin) {

        Label title = new Label("💰 Salary Tools");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField percent = new TextField();
        percent.setPromptText("Raise % (ex: 5)");

        TextField min = new TextField();
        min.setPromptText("Min Salary (optional)");

        TextField max = new TextField();
        max.setPromptText("Max Salary (optional)");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: red;");

        Button apply = new Button("Apply Raise");
        Button back = new Button("Back");

        apply.setOnAction(e -> {
            try {
                double p = Double.parseDouble(percent.getText().trim());

                double low = min.getText().isBlank() ? 0 : Double.parseDouble(min.getText());
                double high = max.getText().isBlank() ? 999999999 : Double.parseDouble(max.getText());

                SearchResult result = service.updateSalaryRange(p, low, high, admin);

                msg.setStyle("-fx-text-fill: green;");
                msg.setText(result.getMessage());

            } catch (Exception ex) {
                msg.setText("Please enter valid numbers.");
            }
        });

        back.setOnAction(e -> {
            stage.close();
            new AdminDashboard().start(new Stage(), admin);
        });

        VBox layout = new VBox(12, title, percent, min, max, apply, back, msg);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(layout, 420, 350));
        stage.setTitle("Salary Tools");
        stage.show();
    }
}
