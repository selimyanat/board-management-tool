package com.sy.ticketingsystem.query.board.projection;

import com.sy.ticketingsystem.command.board.domain.model.BoardArchived;
import com.sy.ticketingsystem.query.board.port.domain.model.BoardView.Status;
import com.sy.ticketingsystem.query.board.port.outgoing.adapter.BoardViewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class BoardArchivedProjection implements Projection<BoardArchived> {

  private final BoardViewRepository boardViewRepository;

  public void on(BoardArchived boardArchived) {

    var exist = boardViewRepository.findById(boardArchived.getId());

    exist.ifPresentOrElse(
        boardView -> {
          boardView.setStatus(Status.ARCHIVED);
          LOG.info("Board view with id {} has been successfully archived", boardArchived.getId());
        },
        () -> LOG.error("Failed to archive the board view with id {} as it does not exist",
                        boardArchived.getId()));

  }
}
