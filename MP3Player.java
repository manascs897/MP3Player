import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MP3Player extends JFrame {

    private JButton playButton, pauseButton, resumeButton, stopButton, browseButton;
    private JLabel statusLabel;
    private File mp3File;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private Player player;
    private long pauseLocation;
    private long songTotalLength;

    public MP3Player() {
        super("MP3 Music Player");

        setLayout(new FlowLayout());
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        browseButton = new JButton("Browse MP3");
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        resumeButton = new JButton("Resume");
        stopButton = new JButton("Stop");
        statusLabel = new JLabel("No file loaded.");

        add(browseButton);
        add(playButton);
        add(pauseButton);
        add(resumeButton);
        add(stopButton);
        add(statusLabel);

        // Initially disable all buttons except Browse
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);

        // Button Listeners
        browseButton.addActionListener(e -> chooseFile());
        playButton.addActionListener(e -> playMusic());
        pauseButton.addActionListener(e -> pauseMusic());
        resumeButton.addActionListener(e -> resumeMusic());
        stopButton.addActionListener(e -> stopMusic());
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION) {
            mp3File = fileChooser.getSelectedFile();
            statusLabel.setText("Selected: " + mp3File.getName());
            playButton.setEnabled(true);
            stopButton.setEnabled(true);
        }
    }

    private void playMusic() {
        try {
            fis = new FileInputStream(mp3File);
            bis = new BufferedInputStream(fis);
            player = new Player(bis);
            songTotalLength = fis.available();

            new Thread(() -> {
                try {
                    player.play();
                    statusLabel.setText("Playback completed.");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }).start();

            statusLabel.setText("Playing: " + mp3File.getName());
            pauseButton.setEnabled(true);
        } catch (Exception e) {
            System.out.println("Error playing file: " + e.getMessage());
        }
    }

    private void pauseMusic() {
        try {
            pauseLocation = fis.available();
            player.close();
            statusLabel.setText("Paused");
            resumeButton.setEnabled(true);
        } catch (Exception e) {
            System.out.println("Pause error: " + e.getMessage());
        }
    }

    private void resumeMusic() {
        try {
            fis = new FileInputStream(mp3File);
            bis = new BufferedInputStream(fis);
            player = new Player(bis);
            fis.skip(songTotalLength - pauseLocation);

            new Thread(() -> {
                try {
                    player.play();
                    statusLabel.setText("Resumed: " + mp3File.getName());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            System.out.println("Resume error: " + e.getMessage());
        }
    }

    private void stopMusic() {
        if(player != null) {
            player.close();
            statusLabel.setText("Stopped");
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MP3Player().setVisible(true));
    }
}