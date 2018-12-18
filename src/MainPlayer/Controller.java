package MainPlayer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class Controller {
//    All the necessary collections and variables to keep track of playlist and current song.
    private ObservableMap<String, String> playlistMap = FXCollections.observableHashMap();
    private ObservableList<String> playlist = FXCollections.observableArrayList();
    private int songIndex = 0;

//    All necessary fxml controls which are updated by code.
    @FXML
    private BorderPane borderPane;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label durationLabel;
    @FXML
    private Slider durationSlider;
    @FXML
    private ListView<String> playlistView = new ListView<String>(playlist);
    @FXML
    private Label titleLabel;


//    All the event handlers for interaction with the UI
    @FXML
    public void playSong(){

        titleLabel.setText(playlist.get(songIndex));

        try {
            chooseSong(playlist.indexOf(playlistView.getSelectionModel().getSelectedItem()));
//            if (Main.getMediaPlayer().getStatus().equals(MediaPlayer.Status.READY) ||
//                Main.getMediaPlayer().getStatus().equals(MediaPlayer.Status.PAUSED) ||
//                Main.getMediaPlayer().getStatus().equals(MediaPlayer.Status.PLAYING)){

            playOrUnpause();

//            }
        } catch (Exception e){
            System.out.println("No song is selected to be played!");
        }
    }

    @FXML
    public void handleNextSong(){
        try {
            songIndex++;
            chooseSong(songIndex);
            resetTimers();
            playOrUnpause();
        } catch (IndexOutOfBoundsException e){
            if(Main.getMediaPlayer() != null) {
                songIndex = -1;
                handleNextSong();
            }
        }
    }

    @FXML
    public void handlePreviousSong(){
        try {
            songIndex--;
            chooseSong(songIndex);
            resetTimers();
            playOrUnpause();
        } catch (IndexOutOfBoundsException e){
            if(Main.getMediaPlayer() != null) {
                songIndex = 1;
                handleNextSong();
            }
        }
    }

//   Change to addSongsToPlaylist, make hashMap get String for short name and String for absolute path.
    @FXML
    public void handleSongAddition(){
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Music","*.mp3")
        );
        chooser.setTitle("Add song to current playlist.");

        File file = chooser.showOpenDialog(borderPane.getScene().getWindow());
        String title = file.getAbsolutePath().replace(".mp3", "").split("\\\\")[5];
        playlistMap.put(title,file.getAbsolutePath());
        playlist.add(title);
        playlistView.setItems(playlist);
        if(Main.getMediaPlayer() == null){
            chooseSong(songIndex);
        }
    }

    @FXML
    public void handleStop(){
        if(Main.getMediaPlayer() != null) {
            resetTimers();
            Main.getMediaPlayer().stop();
        }
    }


    @FXML
    public void handleSliderChange(){
        if(Main.getMediaPlayer() != null) {
            updateDurationLabel();
            Main.getMediaPlayer().seek(Duration.seconds(durationSlider.getValue()));
        } else {
            durationSlider.setValue(0d);
        }
    }

    @FXML
    public void handlePauseButton(){
        if(Main.getMediaPlayer() != null) {
            Main.pauseMusic();
        }
    }

//   All the private methods to breakup functionality into smaller pieces and keep code DRY
    private void chooseSong(int index){
        MediaPlayer oldMediaPlayer = null;
        //Checking if there could be any song playing atm
        if(Main.getMediaPlayer() != null) {
            Main.getMediaPlayer().stop();
            oldMediaPlayer = Main.getMediaPlayer();
        }
        songIndex = index;
        String songTitle = playlist.get(songIndex);
        String songPath = playlistMap.get(songTitle);

        //Changing song, making main's MediaPlayer a new one containing requested song.
        Main.changeSong(new MediaPlayer(new Media(new File(songPath).toURI().toString())));
        //deleting old MediaPlayer for a memory
        if(oldMediaPlayer != null) {
            oldMediaPlayer.dispose();
        }
        setUpMediaPlayer();
        playlistView.getSelectionModel().select(songIndex);
    }

    private void resetTimers(){
        durationSlider.setValue(0d);
        durationLabel.setText("00:00");
    }

    private void setUpMediaPlayer(){
        Main.getMediaPlayer().setOnReady(new Runnable() {
            @Override
            public void run() {
                double duration = Main.getMediaPlayer().getMedia().getDuration().toSeconds();
                durationSlider.setMax(duration);
            }
        });

        Main.getMediaPlayer().setAudioSpectrumInterval(1);


        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Main.getMediaPlayer().setVolume(volumeSlider.getValue());
            }
        });

        Main.getMediaPlayer().setAudioSpectrumListener(new AudioSpectrumListener() {
            @Override
            public void spectrumDataUpdate(double v, double v1, float[] floats, float[] floats1) {
                durationSlider.setValue((Main.getMediaPlayer().getCurrentTime().toSeconds()));
                updateDurationLabel();
            }
        });

        playlistView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    chooseSong(playlistView.getSelectionModel().getSelectedIndex());
                    playSong();
                }
            }
        });

    }

    private void updateDurationLabel(){
        int minutes = (int)(durationSlider.getValue()/60);
        int seconds = (int)((durationSlider.getValue() - minutes*60));
        durationLabel.setText(String.format("%02d:%02d", minutes, seconds));

    }

    private void playOrUnpause(){
        if(Main.getMediaPlayer().getStatus().equals(MediaPlayer.Status.PLAYING)){
            Main.getMediaPlayer().stop();
        }
        Main.getMediaPlayer().play();
    }
}
