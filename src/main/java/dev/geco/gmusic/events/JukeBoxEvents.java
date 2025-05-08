package dev.geco.gmusic.events;



import dev.geco.gmusic.manager.MusicManager;
import dev.geco.gmusic.objects.PlaySettings;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import dev.geco.gmusic.main.GMusicMain;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JukeBoxEvents implements Listener {
	
	private final GMusicMain GPM;
	
    public JukeBoxEvents(GMusicMain GPluginMain) { GPM = GPluginMain; }
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void PIntE(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if(!e.isCancelled() || p.hasPermission(GPM.NAME + ".AMusic.UseJukeBox") || p.hasPermission(GPM.NAME + ".AMusic.*")) {
			
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				
				Block b = e.getClickedBlock();
				
				if(b.getType() == Material.JUKEBOX) {
					
					Jukebox JB = (Jukebox) b.getState();
					
					if(b.hasMetadata(GPM.NAME + "_JB")) {
						
						if(!p.isSneaking() || e.getItem() == null) {
							
							p.openInventory(GPM.getValues().getMusicGUIs().get((UUID) b.getMetadata(GPM.NAME + "_JB").getFirst().value()).getInventory());
							
							e.setCancelled(true);
							e.setUseItemInHand(Result.DENY);
							e.setUseInteractedBlock(Result.DENY);
							
						} else if(!e.isBlockInHand()) e.setUseInteractedBlock(Result.DENY);
						
					} else {
                        JB.getRecord();
                        if(JB.getRecord().getType() == Material.AIR && e.getItem() != null && e.getItem().hasItemMeta()) {

                            String ln = e.getItem().getItemMeta().getPersistentDataContainer().get(MusicManager.DISC_KEY, PersistentDataType.STRING);

                            ItemStack I = GPM.getValues().getDiscItems().keySet().stream().filter(d -> {
                                String containerString = d.getItemMeta().getPersistentDataContainer().get(MusicManager.DISC_KEY, PersistentDataType.STRING);
								return ln != null && ln.equals(containerString);
                            }).findFirst().orElse(null);

                            if(I != null) {

                                JB.setRecord(e.getItem());
                                JB.update(false, false);

								Material type = e.getItem().getType();
								Sound s = Registry.SOUNDS.get(new NamespacedKey("minecraft", type.name().toLowerCase()));

                                if(p.getGameMode() != GameMode.CREATIVE) {
                                    ItemStack s1 = e.getItem();
                                    s1.setAmount(s1.getAmount() - 1);
                                    if(e.getHand() == EquipmentSlot.HAND) {
                                        p.getInventory().setItemInMainHand(s1);
                                    } else if(e.getHand() == EquipmentSlot.OFF_HAND) {
                                        p.getInventory().setItemInOffHand(s1);
                                    }
                                }

                                List<Player> cm = new ArrayList<>();
                                for(Entity pcm : b.getWorld().getNearbyEntities(b.getLocation().add(0.5, 0.5, 0.5), 70, 70, 70)) {
                                    if(pcm instanceof Player) cm.add((Player) pcm);
                                }

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        for(Player t : cm) {
                                            t.stopSound(s, SoundCategory.RECORDS);
                                            t.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                                        }
                                    }

                                }.runTaskLaterAsynchronously(GPM, 0);

                                UUID un = UUID.randomUUID();
                                PlaySettings ps = GPM.getPlaySettingsManager().getPlaySettings(un);
                                ps.setPlayMode(0);
                                ps.setPlayList(0);
                                GPM.getValues().putPlaySetting(un, ps);
                                GPM.getValues().putJukeBlock(un, b.getLocation().add(0.5, 0.5, 0.5));
                                GPM.getBoxSongManager().playBoxSong(un, GPM.getValues().getDiscItems().get(I));
                                GPM.getValues().putTempJukeBlock(b, ps);

                                e.setCancelled(true);
                                e.setUseItemInHand(Result.DENY);
                                e.setUseInteractedBlock(Result.DENY);

                            }

                        } else GPM.getJukeBoxManager().removeTempJukebox(b);
                    }
					
				}
				
			}
			
		}
		
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void BPlaE(BlockPlaceEvent e) {
		
		Block block = e.getBlock();
		
		if(block.getType() == Material.JUKEBOX) {
			
			ItemStack itemStack = e.getItemInHand();
			if (!itemStack.hasItemMeta()) {
				return;
			}

			if (itemStack.getItemMeta().getPersistentDataContainer().has(MusicManager.JUKEBOX_KEY)) {
				GPM.getJukeBoxManager().setNewJukebox(block);
			}
			
		}
		
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void BBreE(BlockBreakEvent e) {
		
		Block b = e.getBlock();
		
		if(b.getType() == Material.JUKEBOX) {
			
			if(b.hasMetadata(GPM.NAME + "_JB")) {
				
				b.setType(Material.AIR);
				
				b.getWorld().dropItemNaturally(b.getLocation(), GPM.getMusicManager().getJukeBox());
				
				GPM.getJukeBoxManager().removeJukebox(b);
				
			} else GPM.getJukeBoxManager().removeTempJukebox(b);
			
		}
		
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void BExpE(BlockExplodeEvent e) {
		
		for(Block b : e.blockList()) {
			
			if(b.getType() == Material.JUKEBOX) {
				
				if(b.hasMetadata(GPM.NAME + "_JB")) {
					
					b.setType(Material.AIR);
					
					b.getWorld().dropItemNaturally(b.getLocation(), GPM.getMusicManager().getJukeBox());
					
					GPM.getJukeBoxManager().removeJukebox(b);
					
				} else GPM.getJukeBoxManager().removeTempJukebox(b);
				
			}
			
		}
		
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void EExpE(EntityExplodeEvent e) {
		
		for(Block b : e.blockList()) {
			
			if(b.getType() == Material.JUKEBOX) {
				
				if(b.hasMetadata(GPM.NAME + "_JB")) {
					
					b.setType(Material.AIR);
					
					b.getWorld().dropItemNaturally(b.getLocation(), GPM.getMusicManager().getJukeBox());
					
					GPM.getJukeBoxManager().removeJukebox(b);
					
				} else GPM.getJukeBoxManager().removeTempJukebox(b);
				
			}
			
		}
		
	}
	
}