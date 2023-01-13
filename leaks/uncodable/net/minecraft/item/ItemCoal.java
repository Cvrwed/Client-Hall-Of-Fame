package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;

public class ItemCoal extends Item {
   public ItemCoal() {
      this.setHasSubtypes();
      this.setMaxDamage(0);
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      return stack.getMetadata() == 1 ? "item.charcoal" : "item.coal";
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      subItems.add(new ItemStack(itemIn, 1, 0));
      subItems.add(new ItemStack(itemIn, 1, 1));
   }
}
