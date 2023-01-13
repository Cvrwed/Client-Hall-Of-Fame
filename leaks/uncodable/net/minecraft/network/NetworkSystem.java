package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.NetHandlerHandshakeMemory;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
   private static final Logger logger = LogManager.getLogger();
   public static final LazyLoadBase<NioEventLoopGroup> eventLoops = new LazyLoadBase<NioEventLoopGroup>() {
      protected NioEventLoopGroup load() {
         return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<EpollEventLoopGroup> field_181141_b = new LazyLoadBase<EpollEventLoopGroup>() {
      protected EpollEventLoopGroup load() {
         return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<LocalEventLoopGroup> SERVER_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
      protected LocalEventLoopGroup load() {
         return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
      }
   };
   private final MinecraftServer mcServer;
   public volatile boolean isAlive;
   private final List<ChannelFuture> endpoints = Collections.synchronizedList(Lists.newArrayList());
   private final List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());

   public NetworkSystem(MinecraftServer server) {
      this.mcServer = server;
      this.isAlive = true;
   }

   public void addLanEndpoint(InetAddress address, int port) {
      synchronized(this.endpoints) {
         Class<? extends ServerSocketChannel> oclass;
         LazyLoadBase<? extends EventLoopGroup> lazyloadbase;
         if (Epoll.isAvailable() && this.mcServer.func_181035_ah()) {
            oclass = EpollServerSocketChannel.class;
            lazyloadbase = field_181141_b;
            logger.info("Using epoll channel type");
         } else {
            oclass = NioServerSocketChannel.class;
            lazyloadbase = eventLoops;
            logger.info("Using default channel type");
         }

         this.endpoints
            .add(
               ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(oclass))
                     .childHandler(
                        new ChannelInitializer<Channel>() {
                           protected void initChannel(Channel p_initChannel_1_) {
                              try {
                                 p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
                              } catch (ChannelException var3) {
                              }
               
                              p_initChannel_1_.pipeline()
                                 .addLast("timeout", new ReadTimeoutHandler(30))
                                 .addLast("legacy_query", new PingResponseHandler(NetworkSystem.this))
                                 .addLast("splitter", new MessageDeserializer2())
                                 .addLast("decoder", new MessageDeserializer(EnumPacketDirection.SERVERBOUND))
                                 .addLast("prepender", new MessageSerializer2())
                                 .addLast("encoder", new MessageSerializer(EnumPacketDirection.CLIENTBOUND));
                              NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.SERVERBOUND);
                              NetworkSystem.this.networkManagers.add(networkmanager);
                              p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
                              networkmanager.setNetHandler(new NetHandlerHandshakeTCP(NetworkSystem.this.mcServer, networkmanager));
                           }
                        }
                     )
                     .group((EventLoopGroup)lazyloadbase.getValue())
                     .localAddress(address, port))
                  .bind()
                  .syncUninterruptibly()
            );
      }
   }

   public SocketAddress addLocalEndpoint() {
      ChannelFuture channelfuture;
      synchronized(this.endpoints) {
         channelfuture = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(LocalServerChannel.class))
               .childHandler(new ChannelInitializer<Channel>() {
                  protected void initChannel(Channel p_initChannel_1_) {
                     NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.SERVERBOUND);
                     networkmanager.setNetHandler(new NetHandlerHandshakeMemory(NetworkSystem.this.mcServer, networkmanager));
                     NetworkSystem.this.networkManagers.add(networkmanager);
                     p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
                  }
               })
               .group((EventLoopGroup)eventLoops.getValue())
               .localAddress(LocalAddress.ANY))
            .bind()
            .syncUninterruptibly();
         this.endpoints.add(channelfuture);
      }

      return channelfuture.channel().localAddress();
   }

   public void terminateEndpoints() {
      this.isAlive = false;

      for(ChannelFuture channelfuture : this.endpoints) {
         try {
            channelfuture.channel().close().sync();
         } catch (InterruptedException var4) {
            logger.error("Interrupted whilst closing channel");
         }
      }
   }

   public void networkTick() {
      synchronized(this.networkManagers) {
         Iterator<NetworkManager> iterator = this.networkManagers.iterator();

         while(iterator.hasNext()) {
            NetworkManager networkmanager = (NetworkManager)iterator.next();
            if (!networkmanager.hasNoChannel()) {
               if (!networkmanager.isChannelOpen()) {
                  iterator.remove();
                  networkmanager.checkDisconnected();
               } else {
                  try {
                     networkmanager.processReceivedPackets();
                  } catch (Exception var8) {
                     if (networkmanager.isLocalChannel()) {
                        CrashReport crashreport = CrashReport.makeCrashReport(var8, "Ticking memory connection");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Ticking connection");
                        crashreportcategory.addCrashSectionCallable("Connection", networkmanager::toString);
                        throw new ReportedException(crashreport);
                     }

                     logger.warn("Failed to handle packet for " + networkmanager.getRemoteAddress(), var8);
                     ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");
                     networkmanager.sendPacket(
                        new S40PacketDisconnect(chatcomponenttext), p_operationComplete_1_ -> networkmanager.closeChannel(chatcomponenttext)
                     );
                     networkmanager.disableAutoRead();
                  }
               }
            }
         }
      }
   }

   public MinecraftServer getServer() {
      return this.mcServer;
   }
}
