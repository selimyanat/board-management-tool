package com.sy.board.management.query.board.projection;

import static io.vavr.control.Option.some;

import com.sy.board.management.query.board.domain.model.BoardView;
import com.sy.board.management.query.board.port.outgoing.adapter.BoardViewRepository;
import com.sy.board.management.command.board.domain.model.BoardNameUpdated;
import com.sy.board.management.core.query.projection.Projection;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class BoardNameUpdatedProjection implements Projection<BoardNameUpdated> {

  private final BoardViewRepository boardViewRepository;

  @Override
  public void on(BoardNameUpdated boardNameUpdated) {

    var executionContext = new ExecutionContext();
    executionContext.event = boardNameUpdated;

    var boardViewOption = boardViewRepository.findById(boardNameUpdated.getBoardId().getId());

    Option.ofOptional(boardViewOption)
          .onEmpty(() -> LOG.error("Board view with id {} does not exit.",
                                   executionContext.event.getBoardId().getId()))
          .map(boardView -> executionContext.setBoardViews(some(boardView)))
          .map(ctx -> executionContext.boardViews.get().setName(executionContext.event.getNewName()))
          .toTry()
          .map(boardView -> boardViewRepository.save(executionContext.boardViews.get()))
          .onFailure(throwable -> LOG.error("The board view with id {} cannot be "
                                                + "be updated with the new name {} "
                                                + "because of the following error ",
                                            executionContext.event.getBoardId().getId(),
                                            executionContext.event.getNewName(),
                                            throwable))
          .onSuccess(boardView -> LOG.info("Board view with id {} has been successfully updated with "
                                               + "new name {}",
                                           executionContext.event.getBoardId().getId(),
                                           executionContext.event.getNewName()));
  }

  @Setter
  private static class ExecutionContext {

    BoardNameUpdated event;
    Option<BoardView> boardViews;
  }
}
