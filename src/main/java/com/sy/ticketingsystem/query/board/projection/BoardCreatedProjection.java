package com.sy.ticketingsystem.query.board.projection;


import com.sy.ticketingsystem.command.board.domain.model.BoardCreated;
import com.sy.ticketingsystem.core.query.projection.Projection;
import com.sy.ticketingsystem.query.board.domain.model.BoardView;
import com.sy.ticketingsystem.query.board.domain.model.BoardView.Status;
import com.sy.ticketingsystem.query.board.port.outgoing.adapter.BoardViewRepository;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class BoardCreatedProjection implements Projection<BoardCreated> {

  private final BoardViewRepository boardViewRepository;

  public void on(BoardCreated boardCreated) {

    var boardView = BoardView.of(boardCreated.getBoardId().getId(),
                                 boardCreated.getBoardName(),
                                 Status.ACTIVE);
    Try.of(() -> boardViewRepository.save(boardView))
       .onSuccess(boardView1 -> LOG.info("Board view with id {} and name {} successfully created",
                                         boardCreated.getId(),
                                         boardCreated.getBoardName()))
       .onFailure(throwable -> LOG.error("Fail to create the board view with id {} because of the"
                                             + " following error {}",
                                         boardCreated.getBoardId().getId(),
                                         throwable));
  }


}
