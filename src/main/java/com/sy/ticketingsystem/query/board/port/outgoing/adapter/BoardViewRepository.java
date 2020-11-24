package com.sy.ticketingsystem.query.board.port.outgoing.adapter;

import com.sy.ticketingsystem.query.board.port.domain.model.BoardView;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardViewRepository extends PagingAndSortingRepository<BoardView, UUID>  {


}
