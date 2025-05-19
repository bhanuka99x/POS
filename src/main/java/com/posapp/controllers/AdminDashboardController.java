package com.posapp.controllers;

import com.posapp.dbconnection.dbconn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class AdminDashboardController {


    @FXML
    private Button btnadd;

    @FXML
    private Button btncustomers;

    @FXML
    private Button btndelete;

    @FXML
    private Button btnedit;

    @FXML
    private Button btninventory;

    @FXML
    private Button btnlogout;

    @FXML
    private Button btnmenu;

    @FXML
    private Button btnope;

    @FXML
    private Button btnorders;

    @FXML
    private Button btnreports;

    @FXML
    private TableView<UserRow> tblusers;

    @FXML
    private TableColumn<UserRow, Integer> colId;

    @FXML
    private TableColumn<UserRow,String> colUser;

    @FXML
    private TableColumn<UserRow,String> colPass;

    @FXML
    private TableColumn<UserRow,String> colRole;

    @FXML
    private Label txtlbl;

    @FXML
    private TextField txtpassword;

    @FXML
    private TextField txtusername;

    @FXML
    private ComboBox<String> cmbrole;

    private final ObservableList<UserRow> userList = FXCollections.observableArrayList();
    private UserRow selectedUser = null;



    @FXML
    public void initialize(){
        cmbrole.setItems(FXCollections.observableArrayList("admin", "operator"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPass.setCellValueFactory(new PropertyValueFactory<>("password"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        tblusers.setItems(userList);
        loaduser();

        tblusers.setOnMouseClicked(event ->{
            selectedUser = tblusers.getSelectionModel().getSelectedItem();
            txtusername.setText(selectedUser.getUsername());
            txtpassword.setText(selectedUser.getPassword());
            cmbrole.setValue(selectedUser.getRole());

        } );


    }
   private void loaduser(){
        String sql = "SELECT * FROM users";

        try(Connection conn= dbconn.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery(sql)){
            userList.clear();
            while(rs.next()){
                int id = rs.getInt("user_id");
                String username = rs.getString("user_name");
                String password = rs.getString("password_hash");
                String role = rs.getString("role");
               userList.add(new UserRow(id,username, password, role));
            }
            tblusers.setItems(userList);

        }catch (SQLException e){
            e.printStackTrace();
        }
   }

   @FXML
   public void clickadd(ActionEvent event){
        String username = txtusername.getText();
        String password = txtpassword.getText();
        String role = cmbrole.getValue();
        if(username.isEmpty() || password.isEmpty() || role == null) return;{

            String sql = "INSERT INTO users(user_name,password_hash,role) VALUES(?,?,?)";
            try(Connection conn = dbconn.connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1,username);
                stmt.setString(2,password);
                stmt.setString(3,role);
                stmt.executeUpdate();
                loaduser();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
   }

    public void clickedit(ActionEvent event) {
        if(selectedUser == null)return;{
            String username = txtusername.getText();
            String password = txtpassword.getText();
            String role = cmbrole.getValue();

            String sql = "UPDATE users SET user_name = ?,password_hash = ?, role = ? WHERE user_id=?";
            try(Connection conn = dbconn.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1,username);
                stmt.setString(2,password);
                stmt.setString(3,role);
                stmt.setInt(4,selectedUser.getId());
                stmt.executeUpdate();
                loaduser();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void clickdelete(ActionEvent event) {
        if(selectedUser == null)return;{
            String sql = "DELETE FROM users WHERE user_id = ? ";
            try(Connection conn = dbconn.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1,selectedUser.getId());
                stmt.executeUpdate();
                loaduser();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static class UserRow{
        private final  int id;
        private final String username;
        private final String password;
        private final String role;

        public UserRow( int id,String username,String password,String role){
            this.id = id;
            this.username = username;
            this.password = password;
            this.role = role;
        }
        public int getId() {
            return id;
        }


        public String getUsername(){
            return username;
        }
        public String getPassword(){
            return password;
        }
        public String getRole(){
            return role;
        }
    }

    @FXML
    public void clickmenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    public void clickinventory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_inventory.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    public void clickcustomers(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_customers.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    public void clickorders(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_orders.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    public void clickreports(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_report.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    public void clickope(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_operator.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    void clicklogout(ActionEvent event) {

    }
}
