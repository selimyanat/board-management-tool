package com.sy.board.management.query.board.port.outgoing.adapter;

import com.sy.board.management.query.board.domain.model.BoardView;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardViewRepository extends PagingAndSortingRepository<BoardView, String>  {

}
