package dev.geco.gmusic.objects;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import dev.geco.gmusic.api.events.GPluginReloadEvent;
import dev.geco.gmusic.main.GMusicMain;
import dev.geco.gmusic.values.Values;
import org.jetbrains.annotations.Nullable;

//@SuppressWarnings("removal") // TODO: Fix removals
public class MusicGUI {

	private static final NamespacedKey ACTION_KEY = new NamespacedKey(GMusicMain.getInstance(), "action");
	
	private final UUID uuid;
	private final MenuType menuType;
	private final Inventory inventory;
	private final GMusicMain plugin;
	private final Listener listener;
	private String search = "";
	private int menuState = 0;
	private int page;
	private final PlaySettings playSettings;
	
	public MusicGUI(UUID uuid, MenuType Type, GMusicMain GPluginMain) {
		
		this.uuid = uuid;
		
		menuType = Type;
		
		plugin = GPluginMain;
		
		playSettings = plugin.getValues().getPlaySettings().get(this.uuid);
		
		inventory = Bukkit.createInventory(new InventoryHolder() {
			
			@Override
			public Inventory getInventory() {
				return inventory;
			}
			
		}, 6 * 9, plugin.getMManager().getMessage("MusicGUI.title"));
		
		setPage(1);
		
		setDefaultBar();
		
		listener = new Listener() {
			
			@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
			public void ICliE(InventoryClickEvent e) {
				
				if(e.getInventory().equals(inventory)) {
					
					ItemStack is = e.getCurrentItem();
					
					if(is != null && is.hasItemMeta()) {
						
						String z = is.getItemMeta().getPersistentDataContainer().get(ACTION_KEY, PersistentDataType.STRING);
						if (z == null) {
							return;
						}
						
						ClickType f = e.getClick();
						
						if(z.startsWith("!")) setPage(Integer.parseInt(z.replace("!", "")));
						else if(z.startsWith(".")) setOptionsBar();
						else if(z.startsWith("+")) {
							switch(z.replace("+", "")) {
							case "v":
								switch(f) {
								case MIDDLE:
									playSettings.setVolume(plugin.getCManager().P_D_VOLUME);
									break;
								case LEFT:
									playSettings.setVolume(playSettings.getVolume() - Values.VOLUME_STEPS < 0 ? 0 : playSettings.getVolume() - Values.VOLUME_STEPS);
									break;
								case RIGHT:
									playSettings.setVolume(playSettings.getVolume() + Values.VOLUME_STEPS > 100 ? 100 : playSettings.getVolume() + Values.VOLUME_STEPS);
									break;
								default:
									break;
								}
								break;
							case "j":
								playSettings.setPlayOnJoin(f == ClickType.MIDDLE ? plugin.getCManager().P_D_JOIN : !playSettings.isPlayOnJoin());
								break;
							case "s":
								switch(f) {
								case MIDDLE:
									playSettings.setPlayMode(plugin.getCManager().P_D_PLAYMODE);
									break;
								case LEFT:
									playSettings.setPlayMode(playSettings.getPlayMode() - 1 < 0 ? 2 : playSettings.getPlayMode() - 1);
									break;
								case RIGHT:
									playSettings.setPlayMode(playSettings.getPlayMode() + 1 > 2 ? 0 : playSettings.getPlayMode() + 1);
									break;
								default:
									break;
								}
								break;
							case "e":
								playSettings.setShowingParticles(f == ClickType.MIDDLE ? plugin.getCManager().P_D_PARTICLES : !playSettings.isShowingParticles());
								break;
							case "q":
								playSettings.setReverseMode(f == ClickType.MIDDLE ? plugin.getCManager().P_D_REVERSE : !playSettings.isReverseMode());
								break;
							case "r":
								switch(f) {
								case MIDDLE:
									playSettings.setRange(plugin.getCManager().JUKEBOX_RANGE);
									break;
								case LEFT:
									playSettings.setRange(playSettings.getRange() - Values.RANGE_STEPS < 1 ? 1 : playSettings.getRange() - Values.RANGE_STEPS);
									break;
								case SHIFT_LEFT:
									playSettings.setRange(playSettings.getRange() - Values.SHIFT_RANGE_STEPS < 1 ? 1 : playSettings.getRange() - Values.SHIFT_RANGE_STEPS);
									break;
								case RIGHT:
									playSettings.setRange(playSettings.getRange() + Values.RANGE_STEPS > plugin.getCManager().JUKEBOX_MAX_RANGE ? plugin.getCManager().JUKEBOX_MAX_RANGE : playSettings.getRange() + Values.RANGE_STEPS);
									break;
								case SHIFT_RIGHT:
									playSettings.setRange(playSettings.getRange() + Values.SHIFT_RANGE_STEPS > plugin.getCManager().JUKEBOX_MAX_RANGE ? plugin.getCManager().JUKEBOX_MAX_RANGE : playSettings.getRange() + Values.SHIFT_RANGE_STEPS);
									break;
								default:
									break;
								}
								break;
							}
							setOptionsBar();
						} else if(z.startsWith("-")) setDefaultBar();
						else if(z.startsWith("?")) {
							if(f == ClickType.LEFT) {
								Player p = (Player) e.getWhoClicked();
								SearchGUI igui = new SearchGUI(p, e1 -> {
                                    search = e1.getText();
                                    setPage(1);
                                    setDefaultBar();
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() { p.openInventory(inventory); }
                                    }.runTaskLater(plugin, 0);
                                }, plugin);
								igui.openInventory();
							} else if((f == ClickType.RIGHT || f == ClickType.MIDDLE) && !search.equals("")) {
								search = "";
								setPage(1);
								setDefaultBar();
							}
						} else if(z.startsWith(",")) {
							setPlaylistBar();
						} else if(z.startsWith("%")) {
							int cpl = playSettings.getPlayList();
							switch(f) {
							case MIDDLE:
								playSettings.setPlayList(plugin.getCManager().P_D_PLAYLIST);
								break;
							case LEFT:
								playSettings.setPlayList(cpl - 1 < 0 ? 2 : cpl - 1);
								break;
							case RIGHT:
								playSettings.setPlayList(cpl + 1 > 2 ? 0 : cpl + 1);
								break;
							default:
								break;
							}
							if(menuType == MenuType.DEFAULT) {
								Player t = Bukkit.getPlayer(MusicGUI.this.uuid);
								if(playSettings.getPlayList() == 2) plugin.getValues().addRadioPlayer(t);
								else plugin.getValues().removeRadioPlayer(t);
								if(playSettings.getPlayList() == 2) plugin.getSongManager().stopSong(t);
								else if(cpl != 2 && plugin.getSongManager().hasPlayingSong(MusicGUI.this.uuid) && playSettings.getPlayList() == 1 && !playSettings.getFavorites().contains(plugin.getSongManager().getPlayingSong(MusicGUI.this.uuid))) plugin.getSongManager().stopSong(t);
							} else {
								if(playSettings.getPlayList() == 2) {
									playSettings.setReverseMode(false);
									playSettings.setPlayOnJoin(false);
									plugin.getValues().addRadioJukeBox(MusicGUI.this.uuid);
								} else plugin.getValues().removeRadioJukeBox(MusicGUI.this.uuid);
								if(playSettings.getPlayList() == 2 || cpl == 2) plugin.getBoxSongManager().stopBoxSong(uuid);
								else if(plugin.getSongManager().hasPlayingSong(MusicGUI.this.uuid) && playSettings.getPlayList() == 1 && !playSettings.getFavorites().contains(plugin.getSongManager().getPlayingSong(MusicGUI.this.uuid))) plugin.getBoxSongManager().stopBoxSong(uuid);
							}
							setPage(1);
							setPlaylistBar();
						} else if(z.startsWith("=")) {
							String id = z.substring(1);
							Song S = plugin.getValues().getSongs().stream().filter(so -> so.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
							if(f == ClickType.MIDDLE) {
								if(playSettings.getFavorites().contains(S)) playSettings.removeFavoriteSong(S);
								else playSettings.addFavoriteSong(S);
								setPage(page);
							} else {
								if(menuType == MenuType.DEFAULT) plugin.getSongManager().playSong(Bukkit.getPlayer(MusicGUI.this.uuid), S);
								else plugin.getBoxSongManager().playBoxSong(uuid, S);
								setPauseResumeBar();
							}
						} else if(z.startsWith("/")) {
							if(menuType == MenuType.DEFAULT) {
								Player t = Bukkit.getPlayer(MusicGUI.this.uuid);
								switch(z.replace("/", "")) {
								case "s":
									plugin.getSongManager().stopSong(t);
									break;
								case "r":
									plugin.getSongManager().resumeSong(t);
									break;
								case "p":
									plugin.getSongManager().pauseSong(t);
									break;
								case "q":
									plugin.getSongManager().playSong(t, plugin.getSongManager().getRandomSong(MusicGUI.this.uuid));
									break;
								case "l":
									plugin.getSongManager().skipSong(t);
									break;
								default:
									break;
								}
							} else {
								switch(z.replace("/", "")) {
								case "s":
									plugin.getBoxSongManager().stopBoxSong(uuid);
									break;
								case "r":
									plugin.getBoxSongManager().resumeBoxSong(uuid);
									break;
								case "p":
									plugin.getBoxSongManager().pauseBoxSong(uuid);
									break;
								case "q":
									plugin.getBoxSongManager().playBoxSong(uuid, plugin.getSongManager().getRandomSong(MusicGUI.this.uuid));
									break;
								case "l":
									plugin.getBoxSongManager().skipBoxSong(uuid);
									break;
								default:
									break;
								}
							}
							setPauseResumeBar();
						}
						
					}
					
					e.setCancelled(true);
					e.setResult(Result.DENY);
					
				}
				
			}

			@EventHandler (ignoreCancelled = true)
			public void IDraE(InventoryDragEvent e) {
				
				if(e.getInventory().equals(inventory)) e.setCancelled(true);
				
			}
			
			@EventHandler (ignoreCancelled = true)
			public void IOpeE(InventoryOpenEvent e) {
				
				if(e.getInventory().equals(inventory)) setPauseResumeBar();
				
			}
			
			@EventHandler
			public void PDisE(PluginDisableEvent e) {
				
				if(plugin.equals(e.getPlugin())) destroy();
				
			}
			
			@EventHandler
			public void GPluRE(GPluginReloadEvent e) {
				
				if(plugin.equals(e.getPlugin())) destroy();
				
			}
			
		};
		
		plugin.getValues().putMusicGUI(this.uuid, this);
		
		Bukkit.getPluginManager().registerEvents(listener, plugin);
		
	}
	
	public void destroy() {
		
		List<HumanEntity> r = new ArrayList<>();
		r.addAll(inventory.getViewers());
		for(HumanEntity i : r) i.closeInventory();
		
		HandlerList.unregisterAll(listener);
		
	}
	
	public int getMenuState() { return menuState; }
	
	public String getSearch() { return search; }
	
	private void clearBar() { for(int z = 45; z < 52; z++) inventory.setItem(z, null); }
	
	public void setDefaultBar() {
		
		menuState = 0;
		
		clearBar();
		
		ItemStack lp = null;
		
		ItemMeta lpm = null;
		
		if(playSettings.getPlayList() != 2) {
			
			lp = new ItemStack(Material.BARRIER);
			
			lpm = lp.getItemMeta();
			
			lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-stop"));

			setAction(lpm, "/s");
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(46, lp);
			
			if(!plugin.getCManager().G_DISABLE_RANDOM_SONG) {
				
				lp = new ItemStack(Material.ENDER_PEARL);
				
				lpm = lp.getItemMeta();
				
				lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-random"));

				setAction(lpm, "/q");
				lp.setItemMeta(lpm);
				
				inventory.setItem(47, lp);
				
			}
			
			if(!plugin.getCManager().G_DISABLE_SEARCH) {
				
				try { lp = new ItemStack(Material.OAK_SIGN); } catch(NoSuchFieldError e) { lp = new ItemStack(Material.valueOf("SIGN")); }
				
				lpm = lp.getItemMeta();
				
				lpm.setDisplayName(plugin.getMManager().getMessage(search.equals("") ? "MusicGUI.music-search-none" : "MusicGUI.music-search", "%Search%", search));

				setAction(lpm, "?");

				lp.setItemMeta(lpm);
				
				inventory.setItem(51, lp);
				
			}
			
		}
		
		if(!plugin.getCManager().G_DISABLE_PLAYLIST) {
			
			lp = new ItemStack(Material.ENDER_CHEST);
			
			lpm = lp.getItemMeta();
			
			lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-playlist"));

			setAction(lpm, ",");
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(49, lp);
			
		}
		
		if(!plugin.getCManager().G_DISABLE_OPTIONS) {
			
			lp = new ItemStack(Material.HOPPER);
			
			lpm = lp.getItemMeta();
			
			lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-options"));

			setAction(lpm, ".");
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(50, lp);
			
		}
		
		setPauseResumeBar();
		
	}
	
	public void setPauseResumeBar() {
		
		if(menuState != 0 || playSettings.getPlayList() == 2) return;
		
		SongSettings t = plugin.getValues().getSongSettings().get(uuid);
		
		if(t != null) {
			
			ItemStack lp = new ItemStack(Material.END_CRYSTAL);
			
			ItemMeta lpm = lp.getItemMeta();
			
			if(t.isPaused()) {
				
				lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-resume"));

				setAction(lpm, "/r");
				
			} else {
				
				lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-pause"));

				setAction(lpm, "/p");
				
			}
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(45, lp);
			
		} else inventory.setItem(45, null);
		
	}
	
	public void setOptionsBar() {
		
		menuState = 1;
		
		clearBar();
		
		ItemStack lp = new ItemStack(Material.CHEST);
		
		ItemMeta lpm = lp.getItemMeta();
		
		lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-back"));

		setAction(lpm, "-");
		
		lp.setItemMeta(lpm);
		
		inventory.setItem(45, lp);
		
		lp = new ItemStack(Material.MAGMA_CREAM);
		
		lpm = lp.getItemMeta();
		
		lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-options-volume", "%Volume%", "" + playSettings.getVolume()));

		setAction(lpm, "+v");
		
		lp.setItemMeta(lpm);
		
		inventory.setItem(46, lp);
		
		lp = new ItemStack(Material.FIREWORK_ROCKET);
		
		lpm = lp.getItemMeta();
		
		lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-options-particle", "%Particle%", plugin.getMManager().getMessage(playSettings.isShowingParticles() ? "MusicGUI.music-options-true" : "MusicGUI.music-options-false")));

		setAction(lpm, "+e");
		
		lp.setItemMeta(lpm);
		
		inventory.setItem(47, lp);
		
		if(playSettings.getPlayList() != 2) {
			
			lp = new ItemStack(Material.DIAMOND);
			
			lpm = lp.getItemMeta();
			
			lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-options-join", "%Join%", plugin.getMManager().getMessage(playSettings.isPlayOnJoin() ? "MusicGUI.music-options-true" : "MusicGUI.music-options-false")));

			setAction(lpm, "+j");
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(48, lp);
			
			lp = new ItemStack(Material.BLAZE_POWDER);
			
			lpm = lp.getItemMeta();
			
			lpm.setDisplayName(plugin.getMManager().getMessage(playSettings.getPlayMode() == 0 ? "MusicGUI.music-options-playmode-once" : playSettings.getPlayMode() == 1 ? "MusicGUI.music-options-playmode-shuffle" : "MusicGUI.music-options-playmode-repeat"));

			setAction(lpm, "+s");
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(49, lp);
			
			lp = new ItemStack(Material.TOTEM_OF_UNDYING);
			
			lpm = lp.getItemMeta();
			
			lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-options-reverse", "%Reverse%", plugin.getMManager().getMessage(playSettings.isReverseMode() ? "MusicGUI.music-options-true" : "MusicGUI.music-options-false")));

			setAction(lpm, "+q");
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(50, lp);
			
		}
		
		if(menuType == MenuType.JUKEBOX || menuType == MenuType.FULLJUKEBOX) {
			
			lp = new ItemStack(Material.REDSTONE);
			
			lpm = lp.getItemMeta();
			
			lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-options-range", "%Range%", "" + playSettings.getRange()));

			setAction(lpm, "+r");
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(51, lp);
			
		}
		
	}
	
	public void setPlaylistBar() {
		
		menuState = 2;
		
		clearBar();
		
		ItemStack lp = new ItemStack(Material.CHEST);
		
		ItemMeta lpm = lp.getItemMeta();
		
		lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-back"));

		setAction(lpm, "-");
		
		lp.setItemMeta(lpm);
		
		inventory.setItem(45, lp);
		
		if(playSettings.getPlayList() != 2) {
			
			lp = new ItemStack(Material.FEATHER);
			
			lpm = lp.getItemMeta();
			
			lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.music-playlist-skip"));

			setAction(lpm, "/l");
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(47, lp);
			
		}
		
		lp = new ItemStack(Material.NOTE_BLOCK);
		
		lpm = lp.getItemMeta();
		
		lpm.setDisplayName(plugin.getMManager().getMessage(playSettings.getPlayList() == 0 ? "MusicGUI.music-playlist-type-default" : playSettings.getPlayList() == 1 ? "MusicGUI.music-playlist-type-favorites" : "MusicGUI.music-playlist-type-radio"));

		setAction(lpm, "%");
		
		lp.setItemMeta(lpm);
		
		inventory.setItem(49, lp);
		
	}
	
	public void setPage(int Page) {
		
		page = Page;
		
		List<Song> S = new ArrayList<Song>();
		
		if(playSettings.getPlayList() != 2) {
			
			S = playSettings.getPlayList() == 1 ? playSettings.getFavorites() : plugin.getValues().getSongs();
			
			if(!search.equals("")) S = plugin.getSongManager().getSongsBySearch(S, search);
			
		}
		
		if(page > getMaxPageSize(S)) page = getMaxPageSize(S);
		
		for(int z = 0; z < 45; z++) inventory.setItem(z, null);
		
		if(S.size() > 0) {
			
			for(int z = (page - 1) * 45; z < 45 * page && z < S.size(); z++) {
				
				Song s1 = S.get(z);
				
				ItemStack is = new ItemStack(s1.getMaterial());
				
				ItemMeta im = is.getItemMeta();
				
				im.setDisplayName(plugin.getMManager().getMessage("MusicGUI.disc-title", "%Title%", s1.getTitle(), "%Author%", s1.getAuthor().equals("") ? plugin.getMManager().getMessage("MusicGUI.disc-empty-author") : s1.getAuthor(), "%OAuthor%", s1.getOriginalAuthor().equals("") ? plugin.getMManager().getMessage("MusicGUI.disc-empty-oauthor") : s1.getOriginalAuthor()));
				
				List<String> dl = new ArrayList<>();
				
				for(String d : s1.getDescription()) dl.add(plugin.getMManager().getColoredMessage("&6" + d));
				
				if(playSettings.getFavorites().contains(s1)) dl.add(plugin.getMManager().getMessage("MusicGUI.disc-favorite"));
				
				im.setLore(dl);

				setAction(im, "=" + s1.getId());
				
				im.addItemFlags(ItemFlag.values());
				
				is.setItemMeta(im);
				
				inventory.setItem(z % 45, is);
				
			}
			
		}
		
		if(page > 1) {
			
			ItemStack lp = new ItemStack(Material.ARROW);
			
			ItemMeta lpm = lp.getItemMeta();
			
			lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.last-page"));

			setAction(lpm, "!" + (page - 1));
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(52, lp);
			
		} else {
			
			ItemStack lp = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
			
			ItemMeta lpm = lp.getItemMeta();
			
			lpm.setDisplayName(" ");
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(52, lp);
			
		}
		
		if(page < getMaxPageSize(S)) {
			
			ItemStack lp = new ItemStack(Material.ARROW);
			
			ItemMeta lpm = lp.getItemMeta();
			
			lpm.setDisplayName(plugin.getMManager().getMessage("MusicGUI.next-page"));

			setAction(lpm, "!" + (page + 1));
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(53, lp);
			
		} else {
			
			ItemStack lp = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
			
			ItemMeta lpm = lp.getItemMeta();
			
			lpm.setDisplayName(" ");
			
			lp.setItemMeta(lpm);
			
			inventory.setItem(53, lp);
			
		}
		
	}
	
	private int getMaxPageSize(List<Song> S) {
    	
    	int i = S.size();
    	
    	return (i / 45) + (i % 45 == 0 ? 0 : 1);
    	
    }

	@Nullable
	private String getAction(ItemMeta meta) {
		return meta.getPersistentDataContainer().get(ACTION_KEY, PersistentDataType.STRING);
	}

	private void setAction(ItemMeta meta, String action) {
		meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, action);
	}
	
	public UUID getOwner() { return uuid; }
	
	public MenuType getMenuType() { return menuType; }
	
	public PlaySettings getPlaySettings() { return playSettings; }
	
	public Inventory getInventory() { return inventory; }
	
	
	public enum MenuType {
		
		DEFAULT,
		JUKEBOX,
		FULLJUKEBOX;
		
	}
	
}