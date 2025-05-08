package dev.geco.gmusic.objects;

import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Song {

    private final String fileName;
	private final String id;
	private final String title;
	private final String oAuthor;
	private final String author;
	private final List<String> description;
	private final List<String> categories;
	private Material material;
	private SoundCategory soundCategory;
	
	
	private HashMap<String, String> instruments = new HashMap<String, String>();
	
	private HashMap<String, List<Note>> noteParts = new HashMap<String, List<Note>>();
	
	private List<Note> m = new ArrayList<>();
	
	
	private HashMap<Long, List<NotePart>> co = new HashMap<Long, List<NotePart>>();
	
	private long na = 0;
	
	private long e = 0;


    public Song(File File) {

        YamlConfiguration file = YamlConfiguration.loadConfiguration(File);
		fileName = File.getName();
		
		id = file.getString("Song.Id");
		title = file.getString("Song.Title", id);
		oAuthor = file.getString("Song.OAuthor");
		author = file.getString("Song.Author");
		description = file.getStringList("Song.Description");
		categories = file.getStringList("Song.Categorys");
		String tm = file.getString("Song.Material");
		if(tm != null) {
			try { material = Material.valueOf(tm.toUpperCase()); } catch(IllegalArgumentException e) { }
		}
        List<Material> DISCS = Tag.ITEMS_CREEPER_DROP_MUSIC_DISCS.getValues().parallelStream().toList();
        if(material == null) material = id == null ? DISCS.getFirst() : DISCS.get(id.length() <= DISCS.size() - 1 ? id.length() : id.length() % (DISCS.size() - 1));
		try { soundCategory = SoundCategory.valueOf(file.getString("Song.Category").toUpperCase()); } catch(IllegalArgumentException e) { soundCategory = SoundCategory.RECORDS; }
		
		List<String> il = new ArrayList<>();
		try { for(String l : file.getConfigurationSection("Song.Content.Instruments").getKeys(false)) il.add(l); } catch (Exception e) { }
		for(String l : il) {
			try {
				String s = NoteInstrument.getInstrument(Integer.parseInt(file.getString("Song.Content.Instruments." + l)));
				if(s != null) instruments.put(l, s);
				else throw new NumberFormatException();
			} catch(IllegalArgumentException e) { instruments.put(l, file.getString("Song.Content.Instruments." + l)); }
		}
		
		List<String> pl = new ArrayList<>();
		try { for(String l : file.getConfigurationSection("Song.Content.Parts").getKeys(false)) pl.add(l); } catch (Exception e) { }
		
		for(String l : pl) {
			List<Note> pl1 = new ArrayList<>();
			for(String l1 : file.getStringList("Song.Content.Parts." + l)) pl1.add(new Note(this, l1));
			noteParts.put(l, pl1);
		}
		
		List<String> ml = file.getStringList("Song.Content.Main");
		for(String l : ml) m.add(new Note(this, l));
		
		for(Note n : m) {
			
			if(n.isReference()) {
				
				for(long z = 1; z <= n.getAmount(); z++) {
					
					e += n.getDelay();
					
					for(Note n1 : n.getReference()) {
						
						for(long z1 = 1; z1 <= n1.getAmount(); z1++) {
							
							e += n1.getDelay();
							
							if(co.containsKey(e)) {
								List<NotePart> np = co.get(e);
								np.addAll(n1.getNoteParts());
								co.put(e, np);
							} else co.put(e, n1.getNoteParts());
							
							na += n1.getNoteParts().size();
							
						}
						
					}
					
				}
				
			} else {
				
				for(long z = 1; z <= n.getAmount(); z++) {
					
					e += n.getDelay();
					
					if(co.containsKey(e)) {
						List<NotePart> np = co.get(e);
						np.addAll(n.getNoteParts());
						co.put(e, np);
					} else co.put(e, n.getNoteParts());
					
					na += n.getNoteParts().size();
					
				}
				
			}
			
		}
		
		file = null;
		
	}
	
	
	public String getFileName() { return fileName; }
	
	
	public String getId() { return id; }
	
	public String getTitle() { return title; }
	
	public String getOriginalAuthor() { return oAuthor; }
	
	public String getAuthor() { return author; }
	
	public List<String> getDescription() { return description; }
	
	public List<String> getCategorys() { return categories; }
	
	public Material getMaterial() { return material; }
	
	public SoundCategory getCategories() { return soundCategory; }
	
	
	public HashMap<String, String> getInstruments() { return instruments; }
	
	public HashMap<String, List<Note>> getNoteParts() { return noteParts; }
	
	public List<Note> getMain() { return m; }
	
	
	public HashMap<Long, List<NotePart>> getContent() { return co; }
	
	public long getStepAmount() { return co.size(); }
	
	public long getNoteAmount() { return na; }
	
	public long getLength() { return e; }
	
}