package main;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

    Clip clip;
    URL soundURL[] = new URL[5];

    //Constructor will fill the soundURL array with all the different
    //sound effects / music you will want to implement
    public Sound() {
        soundURL[0] = getClass().getResource("/sound/music.wav");
        soundURL[1] = getClass().getResource("/sound/move.wav");
        soundURL[2] = getClass().getResource("/sound/drop.wav");
        soundURL[3] = getClass().getResource("/sound/clear.wav");
    }

    //When we want to play a sound we will set the "Clip" to the
    //sound at the indicated index
    public void setFile(int index) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[index]);
            clip = AudioSystem.getClip();
            clip.open(ais);

        }catch(Exception e) {

        }
    }

    //playing a clip will make it run through the sound file once.
    public void play() {
        clip.start();
    }

    //if you want something to continuously play, like background music
    //you would use this loop method when playing the sound
    //I would recommend you make separate objects for sound effects
    //and music in the GamePanel
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    //in order to stop the music at any point, you will need this method
    //you will not need to run this for a sound effect that plays once.
    public void stop() {
        clip.stop();
    }
}
