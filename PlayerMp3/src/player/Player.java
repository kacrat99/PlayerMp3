package player;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Dimension;
import java.io.*;

/**
 * This is a Music Player
 *
 * @author Kacper Ratajczak
 */

public class Player extends JFrame {
	/**
	 * @see #Player() - No argument constructor which is creating Frame
	 * @see #initWindow() - No argument method initialazing all pannels, action listeners, threads
	 * @see #playSong(String, float, int) - creating new Audio.class object
	 */
	public static final int WIDTH = 700, HEIGHT = 350;
	private static final long serialVersionUID = 1L;
	private JPanel browsePanel, namePanel, buttonPanel;
	private JLabel songName;
	private JButton btnBrowse, btnPlay, btnPause, btnStop, volUp, volDown, backward, forward;
	private String fullSongPath;
	private Audio song;
	private boolean isFirstSong = true;
	private int framePos;
	private float volume;
	public File[] songLib; //library of the songs in que
	public int libraryIndex = 0;

	public Player() {
		/**
		 * PLayer Constructor
		 */
		setTitle("Music Player");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(WIDTH, HEIGHT));

		initWindow();
		volume = -20.0f;

		setVisible(true);
	}

	private void initWindow() {
		/**
		 *
		 *  Method used to initialize pannels, buttons and action listeners
		 *
		 * @see #browsePanel - contains browser button
		 * @see #namePanel - contains current song name
		 * @see #buttonPanel - contains all buttons
		 *
		 *
		 *
		 */

		browsePanel = new JPanel();
		btnBrowse = new JButton("Browse");
		add(browsePanel, BorderLayout.NORTH);
		btnBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(true);
				int rVal = chooser.showOpenDialog(Player.this);

				if (rVal == JFileChooser.APPROVE_OPTION) {
					songLib = chooser.getSelectedFiles();
					String name = songLib[libraryIndex].getName();
					System.out.println(name);
					fullSongPath = songLib[libraryIndex].getPath();
					System.out.println(fullSongPath);

					songName.setText(name);
					btnPause.setEnabled(true);
					btnStop.setEnabled(true);
					volUp.setEnabled(true);
					volDown.setEnabled(true);
					forward.setEnabled(true);
					backward.setEnabled(true);

					if (!isFirstSong) {
						song.close();
						playSong(fullSongPath, volume, 0);
					} else {
						isFirstSong = false;
						playSong(fullSongPath, volume, 0);
					}
				}
			}
		});

		browsePanel.add(btnBrowse);
		browsePanel.setBackground(Color.BLACK);
		namePanel = new JPanel();
		add(namePanel, BorderLayout.CENTER);
		songName = new JLabel("Song name");

		/**
		 * @Thread which is chaning backgroud
		 */
		Thread backgroud = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					while (true) {
						for (int i = 0; i < 64; i++) {
							browsePanel.setBackground(new Color(i, i, i));
							namePanel.setBackground(new Color(i, i, i));
							Thread.sleep(200);
						}
						for (int i = 64; i >= 0; i--) {
							browsePanel.setBackground(new Color(i, i, i));
							namePanel.setBackground(new Color(i, i, i));
							Thread.sleep(200);
						}

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		backgroud.start();

		namePanel.add(songName);
		songName.setForeground(Color.WHITE);

		/**
		 * initialize buttons
		 */
		buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		btnPlay = new JButton(new ImageIcon("res/play.png"));
		btnPause = new JButton(new ImageIcon("res/pause.png"));
		btnStop = new JButton(new ImageIcon("res/stop.png"));
		volUp = new JButton(new ImageIcon("res/volUp.png"));
		volDown = new JButton(new ImageIcon("res/volDown.png"));
		backward = new JButton(new ImageIcon("res/back.png"));
		forward = new JButton(new ImageIcon("res/forward.png"));

		/**
		 * buttons action listeners
		 */
		btnPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				song.playAfterPause(framePos);
				btnPlay.setEnabled(false);
				btnPause.setEnabled(true);
			}
		});

		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				framePos = song.getFramePosition();
				song.stop();
				btnPlay.setEnabled(true);
				btnPause.setEnabled(false);

			}
		});

		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				song.close();
				playSong(songLib[libraryIndex].getPath(), volume, framePos);

				songName.setText(songLib[libraryIndex].getName());
				song.stop();
				btnPlay.setEnabled(false);
				btnPause.setEnabled(true);
			}
		});

		volUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				volume += 3.5f;
				song.changeVolume(volume);
			}
		});

		volDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				volume -= 3.5f;
				song.changeVolume(volume);
			}
		});
		forward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				song.close();
				if (libraryIndex == songLib.length - 1) libraryIndex = 0;
				else libraryIndex++;
				playSong(songLib[libraryIndex].getPath(), volume, framePos);
				songName.setText(songLib[libraryIndex].getName());
			}
		});
		backward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				song.close();
				if (libraryIndex == 0) libraryIndex = songLib.length - 1;
				else libraryIndex -= 1;
				playSong(songLib[libraryIndex].getPath(), volume, framePos);
				songName.setText(songLib[libraryIndex].getName());
			}
		});

		volUp.setEnabled(false);
		volDown.setEnabled(false);
		btnPlay.setEnabled(false);
		btnPause.setEnabled(false);
		btnStop.setEnabled(false);
		forward.setEnabled(false);
		backward.setEnabled(false);

		buttonPanel.add(backward);
		buttonPanel.add(volUp);
		buttonPanel.add(btnPlay);
		buttonPanel.add(btnPause);
		buttonPanel.add(btnStop);
		buttonPanel.add(volDown);
		buttonPanel.add(forward);

	}

	private void playSong(String path, float volume, int framePos) {
		/**
		 * Creates new Audio object
		 *
		 * Catches if file is in good/bad format
		 *
		 *
		 */
		try {
			song = new Audio(path);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Player.this, "zly format pliku", "!!!", JOptionPane.ERROR_MESSAGE);

			songName.setText("Song name");
			volUp.setEnabled(false);
			volDown.setEnabled(false);
			btnPlay.setEnabled(false);
			btnPause.setEnabled(false);
			btnStop.setEnabled(false);
		}

		song.play(volume, framePos);
	}
}
