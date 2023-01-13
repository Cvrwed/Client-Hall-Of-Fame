package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class CommandShowSeed extends CommandBase {
   @Override
   public boolean canCommandSenderUseCommand(ICommandSender sender) {
      return MinecraftServer.getServer().isSinglePlayer() || super.canCommandSenderUseCommand(sender);
   }

   @Override
   public String getCommandName() {
      return "seed";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.seed.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) {
      World world = (World)(sender instanceof EntityPlayer ? ((EntityPlayer)sender).worldObj : MinecraftServer.getServer().worldServerForDimension(0));
      sender.addChatMessage(new ChatComponentTranslation("commands.seed.success", world.getSeed()));
   }
}
