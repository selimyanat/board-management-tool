package com.sy.board.management.core.command;

import static io.vavr.control.Either.left;
import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;

import com.sy.board.management.core.domain.model.Error;
import io.vavr.Tuple;
import io.vavr.control.Either;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;

@Getter
public class TransactionalCommandBusHandler implements CommandBus {

  private Map<Class<? extends Command>, CommandHandler> registry;

  public TransactionalCommandBusHandler(List<CommandHandler> handlers) {

    registry = handlers.stream()
            .map(commandHandler -> Tuple.of(commandHandler, resolveTypeArguments(commandHandler.getClass(), CommandHandler.class)))
            // Put the command and the commandHandler in the map
            .collect(Collectors.toMap(commandHandlerAndGenericTypes -> (Class<? extends Command>) commandHandlerAndGenericTypes._2[0],
                                      commandHandlerAndGenericTypes -> commandHandlerAndGenericTypes._1));
  }

  @SuppressWarnings("unchecked")
  @Transactional
  public <T> Either<Error, T> accept(Command<T> command) {

    return registry.getOrDefault(command.getClass(),
                                 anSupportedCommand -> left(Error.of("Command not supported")))
                   .handle(command);
  }
}
