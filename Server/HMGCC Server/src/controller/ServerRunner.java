package controller;

import java.io.IOException;
import java.sql.SQLException;

import GUIWidgets.LogContainer;
import GUIWidgets.StatusBar;
import GUIWidgets.WindowMenuBar;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Class used to start both the server and GUI.
 * @author Oliver Bowker
 * @version 1.0
 */
public class ServerRunner extends Application {
	
	public Server server;
	
	private Stage window;
	private Scene scene;
	private BorderPane layout;
	private WindowMenuBar menuBar;
	private LogContainer logContainer;
	private StatusBar statusBar;
	
	/**
	 * Main method of program.
	 * @param args = Command line arguments.
	 * @throws SQLException - Thrown due to database access.
	 * @throws IOException - Socket exceptions.
	 */
	public static void main(String[] args) throws SQLException, IOException {
		launch(args);
	}

	
	/**
	 * Function to start the GUI, also the Server itself is started.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setWidth(420);
		primaryStage.setHeight(200);
		primaryStage.setResizable(false);
		
		Label lblTitle = new Label("Sign In to Server Account");
		lblTitle.setFont(Font.font(30));
		TextField txtEmail = new TextField();
		txtEmail.setPrefWidth(380);
		TextField txtPassword = new TextField();
		txtPassword.setPrefWidth(380);
		
		Button btnLogin = new Button("Login");
		btnLogin.setPrefWidth(380);
		
		btnLogin.setOnMouseClicked(e -> {
			try {
				primaryStage.close();
				server = new Server(txtEmail.getText(), txtPassword.getText());
				mainPage();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		VBox mainLayout = new VBox(8);
		HBox inputLayout = new HBox(12);
		inputLayout.getChildren().addAll(txtEmail, txtPassword);
		mainLayout.getChildren().addAll(lblTitle, inputLayout, btnLogin);
		mainLayout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(mainLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	private void mainPage() throws Exception {
		this.window = new Stage();
		this.window.setHeight(700);
		this.window.setWidth(1100);
		this.window.setResizable(false);
		
		this.menuBar = new WindowMenuBar();
		this.logContainer = new LogContainer();
		this.statusBar = new StatusBar();
		this.layout = new BorderPane();
		this.layout.setTop(this.menuBar);
		this.layout.setCenter(this.logContainer);
		this.layout.setBottom(this.statusBar);
		
		this.server.logs = this.logContainer;
		this.server.startServer();
		
		this.scene = new Scene(this.layout);
		this.scene.getStylesheets().add("file:styles.css");
		this.window.setScene(this.scene);
		this.window.show();
	}
	
}
