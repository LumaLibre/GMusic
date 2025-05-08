package dev.geco.gmusic.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaySettings {
	
	private final UUID uuid;
	private int playList;
	private long volume;
	private boolean playOnJoin;
	private int playMode;
	private boolean showParticles;
	private boolean reverseMode;
	private boolean toggle;
	private long range;
	private String currentSong;
	private final List<Song> favorites;
	
	public PlaySettings(UUID UUID, int PlayList, long Volume, boolean PlayOnJoin, int PlayMode, boolean ShowingParticles, boolean ReverseMode, boolean Toggle, long Range, String CurrentSong, List<Song> Favorites) {
		playList = PlayList;
		uuid = UUID;
		volume = Volume;
		playOnJoin = PlayOnJoin;
		playMode = PlayMode;
		showParticles = ShowingParticles;
		reverseMode = ReverseMode;
		toggle = Toggle;
		range = Range;
		currentSong = CurrentSong;
		favorites = Favorites;
	}
	
	public UUID getUUID() { return uuid; }
	
	public int getPlayList() { return playList; }
	
	public void setPlayList(int PlayList) { playList = PlayList; }
	
	public long getVolume() { return volume; }
	
	public float getFixedVolume() { return (float) (volume * 2) / 100; }
	
	public void setVolume(long Volume) { volume = Volume; }
	
	public boolean isPlayOnJoin() { return playOnJoin; }
	
	public void setPlayOnJoin(boolean PlayOnJoin) { playOnJoin = PlayOnJoin; }
	
	public int getPlayMode() { return playMode; }
	
	public void setPlayMode(int PlayMode) { playMode = PlayMode; }
	
	public boolean isShowingParticles() { return showParticles; }
	
	public void setShowingParticles(boolean ShowingParticles) { showParticles = ShowingParticles; }
	
	public boolean isReverseMode() { return reverseMode; }
	
	public void setReverseMode(boolean ReverseMode) { reverseMode = ReverseMode; }
	
	public boolean isToggleMode() { return toggle; }
	
	public void setToggleMode(boolean ToggleMode) { toggle = ToggleMode; }
	
	public long getRange() { return range; }
	
	public void setRange(long Range) { range = Range; }
	
	public String getCurrentSong() { return currentSong; }
	
	public void setCurrentSong(String CurrentSong) { currentSong = CurrentSong; }
	
	public List<Song> getFavorites() { return favorites; }
	
	public void addFavoriteSong(Song S) { favorites.add(S); }
	
	public void removeFavoriteSong(Song S) { favorites.remove(S); }
	
}