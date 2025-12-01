package com.employeemgmt.ui.fx;

import com.employeemgmt.models.Employee;
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
   Employee home page
*/
public class EmployeeDashboard {

    private final EmployeeService service = new EmployeeService();

    public void start(Stage stage, User user) {

        if (user == null || user.isAdmin()) {
            stage.close();
            new LoginScreen().start(new Stage());
            return;
        }

        Label title = new Label("Welcome, " + user.getUsername());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea view = new TextArea();
        view.setEditable(false);

        Button refresh = new Button("Refresh");
        Button logout = new Button("Logout");

        refresh.setOnAction(e -> {
            SearchResult r = service.searchEmployeeById(user.getEmpid(), user);
            if (!r.isSuccess()) {
                view.setText(r.getMessage());
                return;
            }

            Employee emp = r.getEmployees().get(0);
            String text =
                    "Name: " + emp.getFullName() + "\n" +
                    "Email: " + emp.getEmail() + "\n" +
                    "Salary: " + emp.getFormattedSalary() + "\n" +
                    "Hire Date: " + emp.getHireDate();
            view.setText(text);
        });

        logout.setOnAction(e -> {
            stage.close();
            new LoginScreen().start(new Stage());
        });

        VBox layout = new VBox(12, title, view, refresh, logout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(layout, 500, 420));
        stage.setTitle("Employee Dashboard");
        stage.show();

        refresh.fire();
    }
}
