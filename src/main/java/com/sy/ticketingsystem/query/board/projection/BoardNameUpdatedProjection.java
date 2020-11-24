package com.sy.ticketingsystem.query.board.projection;

import com.sy.ticketingsystem.command.board.domain.model.BoardNameUpdated;
import com.sy.ticketingsystem.query.board.port.outgoing.adapter.BoardViewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class BoardNameUpdatedProjection implements Projection<BoardNameUpdated> {

  private final BoardViewRepository boardViewRepository;

  @Override
  public void on(BoardNameUpdated boardNameUpdated) {

    var exist = boardViewRepository.findById(boardNameUpdated.getId());

    exist.ifPresentOrElse(
        boardView -> {
          boardView.setName(boardNameUpdated.getNewName());
          LOG.info("Board view with id {} has been successfully updated with new name {}",
                   boardNameUpdated.getId(),
                   boardNameUpdated.getNewName());
          },
        () -> LOG.error("Failed to update the board view with id {} as it does not exist",
                        boardNameUpdated.getId()));
  }
}
