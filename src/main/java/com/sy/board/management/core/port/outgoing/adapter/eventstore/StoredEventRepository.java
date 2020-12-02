package com.sy.board.management.core.port.outgoing.adapter.eventstore;

import io.vavr.collection.List;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoredEventRepository extends PagingAndSortingRepository<StoredEvent, UUID> {

  List<StoredEvent> findByStreamId(String streamId);


}
