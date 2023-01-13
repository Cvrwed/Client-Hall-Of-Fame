package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandStop extends CommandBase {
   @Override
   public String getCommandName() {
      return "stop";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.stop.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) {
      if (MinecraftServer.getServer().worldServers != null) {
         notifyOperators(sender, this, "commands.stop.start", new Object[0]);
      }

      MinecraftServer.getServer().initiateShutdown();
   }
}
