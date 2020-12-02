package com.sy.board.management.command.board.usecase;

import com.sy.board.management.command.board.domain.model.Board;
import com.sy.board.management.core.command.Command;
import java.util.UUID;
import lombok.Value;

@Value(staticConstructor = "of")
public class ArchiveBoardCommand implements Command<Board> {

  private UUID boardId;

}
