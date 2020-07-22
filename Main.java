package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;



public class Main extends Application {
    TextArea screen;
    Socket socket;
    BufferedReader bufferedReader;
    PrintWriter printWriter;


    public void go()
    {
        try {

            socket = new Socket(InetAddress.getLocalHost().getHostName(), 5001);
            InputStreamReader inputStreamReader=new InputStreamReader(socket.getInputStream());
            bufferedReader=new BufferedReader(inputStreamReader);
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println("Client2: hello!");
            printWriter.flush();

            Thread screenThread= new Thread(new IncomingReader());
            screenThread.start();

        }catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Client 2: Error while connecting");
            screen.setText("Error while connecting! :(");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        VBox vBox=new VBox();
        Label screenTitle=new Label("Screen");
        TextField sendingScreen= new TextField();
        screen = new TextArea();
        Button sendingButton = new Button("Send");

        go();

        sendingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                printWriter.println("Client2: "+sendingScreen.getText());
                printWriter.flush();
                sendingScreen.setText("");
            }});



        screen.setEditable(false);

        vBox.getChildren().addAll(screenTitle, screen, sendingScreen, sendingButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);


        primaryStage.setTitle("ChatApp");
        primaryStage.setScene(new Scene(vBox, 300, 275));

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                printWriter.println("Client2: LOG OUT!");
                printWriter.flush();
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }


    public class IncomingReader implements Runnable {

        @Override
        public void run() {
            try {
                String message;
                while ((message = bufferedReader.readLine()) != null) {
                    screen.appendText(message);
                    screen.appendText("\n");
                }
            }catch (IOException e)
            {
                screen.setText("Client 2: Error while reading messages");
                e.printStackTrace();
            }
        }
    }
}
