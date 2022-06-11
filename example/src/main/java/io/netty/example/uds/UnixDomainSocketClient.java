package io.netty.example.uds;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import java.net.SocketAddress;

public class UnixDomainSocketClient {

  public static void main(String[] args) throws Exception {

    EventLoopGroup workerGroup = new EpollEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();

      b.group(workerGroup)
          .option(ChannelOption.SO_BACKLOG, 4096)
          .channel(EpollServerDomainSocketChannel.class)
          .handler(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel ch) throws Exception {
              ch.pipeline().addLast(new UnixDomainSocketServerHandler());
            }
          });

      SocketAddress address = new DomainSocketAddress("/tmp/netty.sock");
      Channel ch = b.connect(address).sync().channel();

      ch.write("Hello, world!").sync();

      ch.closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
    }
  }

}
