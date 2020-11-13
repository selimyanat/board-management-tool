package com.sy.ticketingsystem.core.application;

import static io.vavr.control.Either.left;
import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;

import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.domain.model.Unit;
import io.vavr.Tuple;
import io.vavr.control.Either;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BaseCommandBusHandler implements CommandBus {

  private Map<Class<? extends Command>, CommandHandler> registry;

  public BaseCommandBusHandler(List<CommandHandler> handlers) {

    registry = handlers.stream()
            .map(commandHandler -> Tuple.of(commandHandler, resolveTypeArguments(commandHandler.getClass(), CommandHandler.class)))
            // Put the command and the commandHandler in the map
            .collect(Collectors.toMap(commandHandlerAndGenericTypes -> (Class<? extends Command>) commandHandlerAndGenericTypes._2[0],
                                      commandHandlerAndGenericTypes -> commandHandlerAndGenericTypes._1));
  }

  @SuppressWarnings("unchecked")
  public Either<Error, Unit> accept(Command command) {

    return registry.getOrDefault(command.getClass(),
                                 anSupportedCommand -> left(Error.of("Command not supported")))
                   .handle(command);
  }


}
