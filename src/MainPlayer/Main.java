package MainPlayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


public class Main extends Application {
    private static MediaPlayer mediaPlayer;

    @Override
    public void start(Stage mainWindow) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("UI.fxml"));
        mainWindow.setTitle("Music Player Pro v.1.0");
        mainWindow.setScene(new Scene(root, 600, 450));
        mainWindow.show();
    }

    static void changeSong(MediaPlayer newMediaPlayer){
        mediaPlayer = newMediaPlayer;
    }

    static void pauseMusic(){
        mediaPlayer.pause();
    }

    static MediaPlayer.Status getMediaStatus(){
        return mediaPlayer.getStatus();
    }

    static MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
