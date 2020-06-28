package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.blocks.Vein;
import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.cscorelib2.materials.MaterialCollections;
import io.github.thebusybiscuit.cscorelib2.protection.ProtectableAction;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * The {@link PickaxeOfVeinMining} is a powerful tool which allows you to mine an entire vein of ores
 * at once. It even works with the fortune {@link Enchantment}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class PickaxeOfVeinMining extends SimpleSlimefunItem<BlockBreakHandler> {

    private final ItemSetting<Integer> maxBlocks = new ItemSetting<Integer>("max-blocks", 16) {

        @Override
        public boolean validateInput(Integer input) {
            // We do not wanna allow any negative values here
            return super.validateInput(input) && input.intValue() > 0;
        }

    };

    public PickaxeOfVeinMining(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(maxBlocks);
    }

    @Override
    public BlockBreakHandler getItemHandler() {
        return new BlockBreakHandler() {

            @Override
            public boolean isPrivate() {
                return false;
            }

            @Override
            public boolean onBlockBreak(BlockBreakEvent e, ItemStack item, int fortune, List<ItemStack> drops) {
                if (MaterialCollections.getAllOres().contains(e.getBlock().getType()) && isItem(item)) {
                    if (!Slimefun.hasUnlocked(e.getPlayer(), PickaxeOfVeinMining.this, true)) {
                        return true;
                    }

                    List<Block> blocks = Vein.find(e.getBlock(), maxBlocks.getValue(), MaterialCollections.getAllOres());
                    breakBlocks(e.getPlayer(), blocks, fortune);
                    return true;
                }
                else {
                    return false;
                }
            }
        };
    }

    private void breakBlocks(Player p, List<Block> blocks, int fortune) {
        for (Block b : blocks) {
            if (SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(), ProtectableAction.BREAK_BLOCK)) {
                b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());

                for (ItemStack drop : b.getDrops(getItem())) {
                    b.getWorld().dropItemNaturally(b.getLocation(), drop.getType().isBlock() ? drop : new CustomItem(drop, fortune));
                }

                b.setType(Material.AIR);
            }
        }
    }

}
