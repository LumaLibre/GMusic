package dev.geco.gmusic.objects;

public class NotePart {
	
	private static final String PARTS = ":";
	
	private static final String VAR = "";
	
	private static final String KEYFLOAT = "#";
	
	private static final String STOP = "-";
	
	
	private final Note note;
	private String sound;
	private String stopSound;
	private boolean variableVolume = false;
	private float volume = 1.0f;
	private float pitch = 1.0f;
	private float distance = 0;
	
	
	public NotePart(Note Note, String NotePartString) {
		
		note = Note;
		
		String[] a = NotePartString.split(PARTS);
		
		if(!a[0].startsWith(STOP)) sound = note.getSong().getInstruments().get(a[0]);
		else stopSound = note.getSong().getInstruments().get(a[0].replace(STOP, ""));
		if(sound == null || stopSound != null) return;
		
		if(a.length == 1 || a[1].equals(VAR)) variableVolume = true;
		else {
			try { volume = Float.parseFloat(a[1]); } catch(NumberFormatException e) { }
		}
		
		if(a.length > 2 && !a[2].equals(VAR)) {
			if(a[2].contains(KEYFLOAT)) pitch = NotePitch.getPitch(Integer.parseInt(a[2].replace(KEYFLOAT, "")));
			else {
				try { pitch = Float.parseFloat(a[2]); } catch(NumberFormatException e) { }
			}
		}
		
		if(a.length > 3) distance = ((Integer.parseInt(a[3]) - 100) / 200f) * 2f;
		
	}
	
	
	public Note getNote() { return note; }
	
	
	public String getSound() { return sound; }
	
	public String getStopSound() { return stopSound; }
	
	public boolean isVariableVolume() { return variableVolume; }
	
	public float getVolume() { return volume; }
	
	public float getPitch() { return pitch; }
	
	public float getDistance() { return distance; }
	
}