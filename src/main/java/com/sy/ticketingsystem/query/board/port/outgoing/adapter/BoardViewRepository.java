package com.sy.ticketingsystem.query.board.port.outgoing.adapter;

import com.sy.ticketingsystem.query.board.domain.model.BoardView;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardViewRepository extends PagingAndSortingRepository<BoardView, String>  {

}
