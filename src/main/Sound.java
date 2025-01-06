package main;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

    Clip clip;
    URL soundURL[] = new URL[5];

    // Get all the sound data needed
    public Sound() {
        soundURL[0] = getClass().getResource("/sound/music.wav");
        soundURL[1] = getClass().getResource("/sound/move.wav");
        soundURL[2] = getClass().getResource("/sound/drop.wav");
        soundURL[3] = getClass().getResource("/sound/clear.wav");
    }

    // Set the sound file to play to the one specified
    public void setFile(int index) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[index]);
            clip = AudioSystem.getClip();
            clip.open(ais);

        } catch (Exception e) {

        }
    }

    // Run through the sound file once
    public void play() {
        clip.start();
    }

    // Play a sound file and loop it when it's done
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // Stop the current sound effect (this is only used for music since other sound effects will stop when they're done)
    public void stop() {
        clip.stop();
    }
}
