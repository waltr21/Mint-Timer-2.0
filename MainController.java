//package temp;

import java.util.ArrayList;
import java.util.Date;

import javafx.application.Application;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import java.text.DecimalFormat;

import javafx.scene.image.Image;
import java.io.FileInputStream;
import javafx.scene.control.Tooltip;

import javafx.geometry.Pos;
import javafx.scene.layout.Region;

//Animations
import javafx.util.Duration;
import javafx.animation.TranslateTransition;


/**
 * Controller class to handle the main screen GUI
 */
public class MainController extends Application implements Initializable{
    @FXML private AnchorPane mainPane;
    @FXML private Label timeLabel, scrambleLabel, avgLabel, bestLabel, toolLabel, emojiLabel, secondsLabel;
    @FXML private JFXListView<Label> listView;
    @FXML private JFXToggleButton secondsToggle, emojiToggle;
	@FXML private JFXButton settingsButton;	


    // @FXML private BorderPane mainPane;

    private long startTime;
    private int prevIndex;
    private Scene scene;
    private boolean running;
    private Rotations rotateEngine;
    private DecimalFormat df;
    private ArrayList<SolveInfo> currentSolves;
    private TranslateTransition animateUp;
    private TranslateTransition animateDown;


    public static void main(String[] args) {
        launch(args);
    }

    public MainController(){
        running = false;
        rotateEngine = new Rotations();
        df = new DecimalFormat("0.000");
        prevIndex = -1;
        currentSolves = new ArrayList<SolveInfo>();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));

        scene = new Scene(root, 670, 670);
        stage.setMinWidth(670);
        stage.setMinHeight(670);

        stage.setTitle("Mint Timer");
        stage.setScene(scene);
        stage.show();
        scene.getRoot().requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        scrambleLabel.setText(rotateEngine.getMoves(3));
        listView.setExpanded(true);
        listView.depthProperty().set(1);
        secondsToggle.setSelected(true);
        emojiToggle.setSelected(true);


        animateDown = new TranslateTransition();
        animateDown.setDuration(Duration.millis(350));
        animateDown.setNode(scrambleLabel);
        animateDown.setToY(150);
        animateDown.setOnFinished(e -> {
            scrambleLabel.setText(rotateEngine.getMoves(3));
            animateUp.play();
        });

        animateUp = new TranslateTransition();
        animateUp.setDuration(Duration.millis(350));
        animateUp.setNode(scrambleLabel);
        animateUp.setToY(0);
    }

    @FXML private void handlePressed(KeyEvent event){
        // System.out.println("Pressed");
        if (event.getCode() == KeyCode.SPACE){
            if (running){
                double tempTime = (System.currentTimeMillis() - startTime) / 1000.000;

                String timeString = df.format(tempTime);
                Label lb = new Label(timeString);

                //Set the width/height of the text area to the same of the cell.
                //This is to avoid having to use cell factories...
                lb.setMinWidth(123);
                lb.setMinHeight(35);
                lb.setAlignment(Pos.CENTER);

                listView.getItems().add(lb);

                timeLabel.setText(timeString);

                currentSolves.add(new SolveInfo(tempTime, scrambleLabel.getText(), new Date()));
                avgLabel.setText("AVG: " + format(getAvg()));
                bestLabel.setText("Best: " + format(getBest()));

                if (secondsToggle.isSelected())
                    displayTimer.stop();

                animateDown.play();

                if (emojiToggle.isSelected())
                    setImages();

                listView.scrollTo(listView.getItems().size() - 1);
            }
        }

    }

    @FXML private void handleRelease(KeyEvent event){
        // System.out.println("Released");
        if (event.getCode() == KeyCode.SPACE){
            if(!running){
                startTime = System.currentTimeMillis();
                running = true;

                if (secondsToggle.isSelected())
                    displayTimer.start();
                else
                    timeLabel.setText(" ");
            }
            else{
                running = false;
            }
        }
    }

	@FXML private void settingsClicked(){
		if(!emojiToggle.isVisible()){
			emojiToggle.setVisible(true);
			secondsToggle.setVisible(true);
			secondsLabel.setVisible(true);
			emojiLabel.setVisible(true);
        	mainPane.requestFocus();
			settingsButton.setTranslateY(120);
			settingsButton.setText("Close");
		}
		else{
			emojiToggle.setVisible(false);
			secondsToggle.setVisible(false);
			secondsLabel.setVisible(false);
			emojiLabel.setVisible(false);
        	mainPane.requestFocus();
			settingsButton.setTranslateY(0);
			settingsButton.setText("Settings");
		}
	}

    @FXML private void listClicked(MouseEvent event){
        // System.out.println(listView.getSelectionModel().getSelectedIndex());
        int index = listView.getSelectionModel().getSelectedIndex();
        mainPane.requestFocus();
        if(event.getButton() == MouseButton.SECONDARY && index >= 0){
            listView.getItems().remove(index);
            currentSolves.remove(index);
            prevIndex = -1;
            toolLabel.setVisible(false);

            avgLabel.setText("AVG: " + format(getAvg()));
            bestLabel.setText("Best: " + format(getBest()));
            if (emojiToggle.isSelected())
                setImages();
        }

        if(event.getButton() == MouseButton.PRIMARY && index >= 0){
            scrambleLabel.setText(currentSolves.get(index).getScramble());
        }

        listView.getSelectionModel().clearSelection();
        // listView.setTooltip(new Tooltip("Testing"));
    }

    @FXML private void listExited(){
        toolLabel.setVisible(false);
    }

    @FXML private void mouseHovered(MouseEvent event){
        // System.out.println("Moved");
        toolLabel.setLayoutX(event.getSceneX() + 15);
        toolLabel.setLayoutY(event.getSceneY());

        boolean notIn = false;
        if (prevIndex == -1){
            notIn = true;
        }
        // toolLabel.setVisible(true);


        if (notIn || !listView.getItems().get(prevIndex).isHover()){
            for (int i = 0; i < listView.getItems().size(); i++){
                Label lb = listView.getItems().get(i);
                if (lb.isHover()){
                    prevIndex = i;
                    String tempScram = splitString(currentSolves.get(i).getScramble());
                    toolLabel.setText(tempScram + "\n(Right click to delete)");
                    toolLabel.setMinWidth(Region.USE_PREF_SIZE);
                    toolLabel.setVisible(true);
                    return;
                }
            }
            toolLabel.setVisible(false);
            prevIndex = -1;
        }

    }

    @FXML private void toggleClicked(){
        mainPane.requestFocus();
    }

    @FXML private void emojiToggleClicked(){
        if(!emojiToggle.isSelected()){
            for (Label lb : listView.getItems()){
                try{
                    lb.setGraphic(null);
                }
                catch(Exception e){
                    System.out.println(e);
                }
            }
        }
        else{
            setImages();
        }
        mainPane.requestFocus();
    }


    AnimationTimer displayTimer = new AnimationTimer() {
        private long timestamp;
        private long time = 0;
        private long fraction = 0;
        private int animatedSeconds = 0;
        private int animatedMinutes = 0;

        @Override
        public void start() {
            timeLabel.setText("0.0");
            timestamp = startTime - fraction;
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
            fraction = System.currentTimeMillis() - timestamp;
            time = 0;
            animatedSeconds = 0;
            animatedMinutes = 0;
        }

        @Override
        public void handle(long now) {
            long newTime = System.currentTimeMillis();

            if (timestamp + 100 <= newTime) {
                long deltaT = (newTime - timestamp) / 100;
                time += deltaT;
                timestamp += 100 * deltaT;
                if (time > 9){
                  time = 0;
                  animatedSeconds++;
                }
                if (animatedSeconds > 59){
                  animatedSeconds = 0;
                  animatedMinutes++;
                }

                if (animatedMinutes > 0){
                  if (animatedSeconds < 10)
                    timeLabel.setText(animatedMinutes + ":" + "0" +
                    animatedSeconds + "." + Long.toString(time));
                  else
                    timeLabel.setText(animatedMinutes + ":" +
                    animatedSeconds + "." + Long.toString(time));
                }
                else
                  timeLabel.setText(animatedSeconds + "." + Long.toString(time));
            }
        }
    };

    private double getAvg(){
        if (currentSolves.size() == 0){
            return 0.0;
        }

        double sum = 0;
        for (SolveInfo si : currentSolves){
            sum += si.getSolveTime();
        }

        return (sum / (currentSolves.size() + 0.0));
    }

    private double getBest(){
        if (currentSolves.size() == 0){
            return 0.0;
        }

        double min = currentSolves.get(0).getSolveTime();
        for (SolveInfo si : currentSolves){
            if (min > si.getSolveTime()){
                min = si.getSolveTime();
            }
        }

        return min;
    }

    private String format(double n){
        return df.format(n);
    }

    private void setImages(){
        double avg = getAvg();
        double best = getBest();
        for (Label lb : listView.getItems()){
            String imageName;
            double labelTime = Double.parseDouble(lb.getText());
            double difference = Math.abs(labelTime - avg);
            double percent = difference / avg;

            if (labelTime == best){
                imageName = "best.png";
            }
            else if (labelTime > avg && percent >= 0.10){
                imageName = "frown.png";
            }
            else if (labelTime < avg && percent >= 0.10){
                imageName = "smile.png";
            }
            else{
                imageName = "neutral.png";
            }

            try{
                lb.setGraphic(new ImageView(new Image(new FileInputStream(System.getProperty("user.dir") + "/Images/" + imageName))));
                // lb.setGraphic(null);

            }
            catch(Exception e){
                System.out.println(e);
            }
        }
    }

    private String splitString(String s){
        String newString = "";
        for (int i = 0; i < s.length(); i++){
            if (i % 50 == 0 && i != 0)
                newString += "\n";
            newString += s.charAt(i);
        }
        return newString;
    }
}
