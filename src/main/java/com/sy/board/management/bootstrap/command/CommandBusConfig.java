package com.sy.board.management.bootstrap.command;

import com.sy.board.management.core.command.CommandBus;
import com.sy.board.management.core.command.CommandHandler;
import com.sy.board.management.core.command.TransactionalCommandBusHandler;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

  @Bean
  public CommandBus commandBus(List<CommandHandler> handlers) {

    return new TransactionalCommandBusHandler(handlers);
  }
}
