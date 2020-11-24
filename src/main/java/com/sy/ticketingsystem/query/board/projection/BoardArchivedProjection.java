package com.sy.ticketingsystem.query.board.projection;

import static io.vavr.control.Option.some;

import com.sy.ticketingsystem.command.board.domain.model.BoardArchived;
import com.sy.ticketingsystem.core.query.projection.Projection;
import com.sy.ticketingsystem.query.board.domain.model.BoardView;
import com.sy.ticketingsystem.query.board.domain.model.BoardView.Status;
import com.sy.ticketingsystem.query.board.port.outgoing.adapter.BoardViewRepository;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class BoardArchivedProjection implements Projection<BoardArchived> {

  private final BoardViewRepository boardViewRepository;

  public void on(BoardArchived boardArchived) {

    var executionContext = new ExecutionContext();
    executionContext.event = boardArchived;

    var boardViewOption = boardViewRepository.findById(boardArchived.getBoardId().getId());

    Option.ofOptional(boardViewOption)
          .onEmpty(() -> LOG.error("Board view with id {} does not exit.",
                                   executionContext.event.getBoardId().getId()))
          .map(boardView -> executionContext.setBoardViews(some(boardView)))
          .map(ctx -> executionContext.boardViews.get().setStatus(Status.ARCHIVED))
          .toTry()
          .map(boardView -> boardViewRepository.save(executionContext.boardViews.get()))
          .onFailure(throwable -> LOG.error("The board view with id {} cannot be "
                                                + "be archived because of the following error ",
                                            executionContext.event.getBoardId().getId(),
                                            throwable))
          .onSuccess(boardView -> LOG.info("Board view with id {} has been successfully archived",
                                           executionContext.event.getBoardId().getId()));

  }

  @Setter
  private static class ExecutionContext {

    BoardArchived event;
    Option<BoardView> boardViews;
  }
}
