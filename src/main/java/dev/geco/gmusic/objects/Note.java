package dev.geco.gmusic.objects;

import java.util.*;

public class Note {
	
	private static final String DELAY = "!";
	private static final String TICKDELAY = "t";
	private static final String AMOUNT = ";";
	private static final String REF = "?";
	private static final String PARTS = "_";
	
	
	private final Song song;
	private long delay = 0;
	private long amount = 1;
	private final List<NotePart> noteParts = new ArrayList<>();
	private List<Note> noteReferences = new ArrayList<>();
	
	
	public Note(Song Song, String NoteString) {
		
		song = Song;
		
		String ns = NoteString;
		
		if(ns.contains(DELAY)) {
			try {
				delay = (ns.contains(TICKDELAY) ? 50 : 1) * Long.parseLong(ns.split(DELAY)[0].replace(TICKDELAY, ""));
				if(delay < 0) delay = 0;
			} catch(NumberFormatException e) { }
			ns = ns.split(DELAY)[1];
		}
		
		if(ns.contains(AMOUNT)) {
			try {
				long r1 = Long.parseLong(ns.split(AMOUNT)[1]);
				if(r1 > 0) amount += r1;
			} catch(NumberFormatException e) { }
			ns = ns.split(AMOUNT)[0];
		}
		
		if(ns.startsWith(REF)) {
			List<Note> p1 = song.getNoteParts().get(ns.replace(REF, ""));
			if(p1 != null) noteReferences = p1;
		} else for(String i : ns.split(PARTS)) noteParts.add(new NotePart(this, i));
		
	}
	
	
	public Song getSong() { return song; }
	
	
	public long getDelay() { return delay; }
	
	public long getAmount() { return amount; }
	
	public List<NotePart> getNoteParts() { return noteParts; }
	
	public List<Note> getReference() { return noteReferences; }
	
	public boolean isReference() { return noteReferences.size() > 0; }
	
}