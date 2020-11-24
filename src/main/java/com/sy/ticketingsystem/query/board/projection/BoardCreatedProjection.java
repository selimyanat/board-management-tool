package com.sy.ticketingsystem.query.board.projection;


import com.sy.ticketingsystem.command.board.domain.model.BoardCreated;
import com.sy.ticketingsystem.query.board.port.domain.model.BoardView;
import com.sy.ticketingsystem.query.board.port.domain.model.BoardView.Status;
import com.sy.ticketingsystem.query.board.port.outgoing.adapter.BoardViewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class BoardCreatedProjection implements Projection<BoardCreated> {

  private final BoardViewRepository boardViewRepository;

  public void on(BoardCreated boardCreated) {

    var boardView = new BoardView().setBoardId(boardCreated.getId())
                               .setName(boardCreated.getBoardName())
                               .setStatus(Status.ACTIVE);
    boardViewRepository.save(boardView);
    LOG.info("Board view with id {} successfully created", boardView.getBoardId());
  }


}
