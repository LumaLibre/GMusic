package dev.geco.gmusic.objects;


import java.util.Timer;

public class SongSettings {
	
	private final Song song;
	
	private Timer timer;
	
	private long position;
	
	private boolean paused = false;
	
	public SongSettings(Song song, Timer timer, long position) {
		
		this.song = song;
		this.timer = timer;
		this.position = position;
		
	}
	
	public Song getSong() { return song; }
	
	public Timer getTimer() { return timer; }
	
	public void setTimer(Timer T) { timer = T; }
	
	public long getPosition() { return position; }
	
	public void setPosition(long P) { position = P; }
	
	public boolean isPaused() { return paused; }
	
	public void setPaused(boolean Paused) { paused = Paused; }
	
}