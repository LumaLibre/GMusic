package dev.geco.gmusic.objects;

import java.lang.reflect.*;
import java.util.*;

import dev.geco.gmusic.manager.NMSManager;
import io.papermc.paper.math.BlockPosition;
import net.minecraft.network.chat.PlayerChatMessage;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

/*import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;*/

import dev.geco.gmusic.api.events.GPluginReloadEvent;
import dev.geco.gmusic.main.GMusicMain;
import dev.geco.gmusic.manager.NMSManager;

public class SearchGUI {
	
	private final Player p;
	
	private Inventory i;
	
	private GMusicMain GPM;
	
	private Listener l;
	
	private final AnvilClickEventHandler h;
	
	private Class<?> BlockPosition;
    private Class<?> PacketPlayOutOpenWindow;
    private Class<?> ContainerAnvil;
    private Class<?> ChatMessage;
    private Class<?> EntityHuman;
    private Class<?> ContainerAccess;
    private Class<?> Containers;
	
    private boolean useNewVersion = NMSManager.isNewerOrVersion(14);
    
    public interface AnvilClickEventHandler { public void onAnvilClick(AnvilClickEvent e); }
    
    public static class AnvilClickEvent extends Event implements Cancellable {
    	
    	private static final HandlerList HANDLERS = new HandlerList();
    	
    	private int s;
        private ItemStack i;
        private String t;
        
        private boolean cancelled;
        
        public AnvilClickEvent(int Slot, ItemStack Item, String Text) {
        	
            s = Slot;
            i = Item;
            t = Text;
            
        }
        
        public int getSlot() { return s; }
        
        public ItemStack getItemStack() { return i; }
        
        public String getText() { return t; }
        
        public boolean isCancelled() { return cancelled; }
        
        public void setCancelled(boolean cancel) { cancelled = cancel; }
        
        @Override
    	public HandlerList getHandlers() { return HANDLERS; }
    	
    	public static HandlerList getHandlerList() { return HANDLERS; }
        
    }
	
	public SearchGUI(Player P, final AnvilClickEventHandler Handler, GMusicMain GPluginMain) {
		
		p = P;
		
		h = Handler;

		/*
		BlockPosition = NMSManager.getNMSClass("BlockPosition");
        PacketPlayOutOpenWindow = NMSManager.getNMSClass("PacketPlayOutOpenWindow");
        ContainerAnvil = NMSManager.getNMSClass("ContainerAnvil");
        ChatMessage = NMSManager.getNMSClass("ChatMessage");
        EntityHuman = NMSManager.getNMSClass("EntityHuman");
        if(useNewVersion) {
        	ContainerAccess = NMSManager.getNMSClass("ContainerAccess");
        	Containers = NMSManager.getNMSClass("Containers");
        }
		
		GPM = GPluginMain;
		
		l = new Listener() {
			
			@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
			public void ICliE(InventoryClickEvent e) {
				
				if(e.getInventory().equals(i)) {
					
					e.setCancelled(true);
					
					int S = e.getRawSlot();
					
					if(S != 2) return;
					
					ItemStack IS = e.getCurrentItem();
					
					if(IS != null && IS.hasItemMeta() && IS.getItemMeta().getDisplayName() != null) {
						
						AnvilClickEvent ACE = new AnvilClickEvent(2, IS, ChatColor.stripColor(IS.getItemMeta().getDisplayName()));
						
						if(!ACE.isCancelled()) {
							
							h.onAnvilClick(ACE);
							
							e.getWhoClicked().closeInventory();
		                    
		                    HandlerList.unregisterAll(l);
							
						}
						
					}
					
				}
				
			}
			
			@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
            public void PAE(PrepareAnvilEvent e) {
            	
            	if(e.getInventory().equals(i)) {
            		
            		ItemStack IS = e.getResult();
            		
                	if(IS != null && IS.hasItemMeta() && IS.getItemMeta().getDisplayName() != null) {
                		
                		ItemMeta M = IS.getItemMeta();
                		
                		M.setDisplayName(GPM.getMManager().getColoredMessage("&6" + M.getDisplayName()));
                		
                		IS.setItemMeta(M);
                		
                		e.setResult(IS);
                		
                	}
            		
            	}
            	
            }
			
			@EventHandler
            public void ICloE(InventoryCloseEvent e) {
            	
                if(e.getInventory().equals(i)) {
                    
                	if(p.getLevel() > 0) p.setLevel(p.getLevel() - 1);
                	i.clear();
                	destroy();
                	
                }
                
            }
            
            @EventHandler
            public void PQuiE(PlayerQuitEvent e) {
            	
                if(e.getPlayer().equals(p)) {

                	if(i != null) {
						if(p.getLevel() > 0) p.setLevel(p.getLevel() - 1);
						i.clear();
					}
                	destroy();
                	
                }
                
            }
            
            @EventHandler
			public void PDisE(PluginDisableEvent e) {
				
				if(GPM.equals(e.getPlugin())) {

					if(i != null) {
						if(p.getLevel() > 0) p.setLevel(p.getLevel() - 1);
						i.clear();
					}

                	destroy();
                	
				}
				
			}
			
			@EventHandler
			public void GPluRE(GPluginReloadEvent e) {
				
				if(GPM.equals(e.getPlugin())) {

					if(i != null) {
						if(p.getLevel() > 0) p.setLevel(p.getLevel() - 1);
						i.clear();
					}

                	destroy();
					
				}
				
			}
			
		};
		
		GPM.getValues().putInputGUI(p, this);
		
		Bukkit.getPluginManager().registerEvents(l, GPM);
		 */
	}
	
	public void destroy() {

		if(i != null) {
			List<HumanEntity> r = new ArrayList<>();
			r.addAll(i.getViewers());
			for(HumanEntity i : r) i.closeInventory();
		}
		
		HandlerList.unregisterAll(l);
		
	}
	
	public void openInventory() {
		p.sendMessage("Not implemented yet.");
	}
	
}