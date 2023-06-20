package cc.panada.vortex.bottledxp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class BottledXP extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if (!player.isSneaking() || e.getHand() != EquipmentSlot.HAND || e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        final PlayerInventory inv = player.getInventory();
        if (inv.getItemInMainHand().getType() != Material.GLASS_BOTTLE) return;
        if (calculateTotalExperience(player.getLevel()) + Math.round(player.getExp() * player.getExpToLevel()) < 12) return;
        player.giveExp(-12);
        inv.removeItem(new ItemStack(Material.GLASS_BOTTLE));
        if (Arrays.stream(inv.getStorageContents()).noneMatch(is -> is == null || is.getType() == Material.AIR || (is.getType() == Material.EXPERIENCE_BOTTLE && is.getAmount() < 64))) {
            final Location loc = player.getLocation().add(0, 1, 0);
            loc.getWorld().dropItem(loc, new ItemStack(Material.EXPERIENCE_BOTTLE)).setVelocity(loc.getDirection().multiply(0.5));
        } else inv.addItem(new ItemStack(Material.EXPERIENCE_BOTTLE));
    }

    int calculateTotalExperience(int currentLevel) {
        if (currentLevel >= 0 && currentLevel <= 16) return currentLevel * currentLevel + 6 * currentLevel;
        if (currentLevel >= 17 && currentLevel <= 31) return (int) (2.5 * currentLevel * currentLevel - 40.5 * currentLevel + 360);
        return (int) (4.5 * currentLevel * currentLevel - 162.5 * currentLevel + 2220);
    }
}
