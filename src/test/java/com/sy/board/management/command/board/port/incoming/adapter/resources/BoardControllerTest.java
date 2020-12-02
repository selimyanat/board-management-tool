package com.sy.board.management.command.board.port.incoming.adapter.resources;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sy.board.management.command.board.domain.model.Board;
import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.core.command.CommandBus;
import com.sy.board.management.core.domain.model.Error;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@AutoConfigureMockMvc()
@WebMvcTest(controllers = BoardController.class)
public class BoardControllerTest {

  private static Error AN_ERROR = Error.of("Operation cannot be completed");

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private Board board;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CommandBus commandBus;

  @BeforeEach
  public void setUp() {

    when(commandBus.accept(any())).thenReturn(right(board));
  }

  @DisplayName("Create a board returns 204 http code")
  @Test
  public void createBoard_is_successful() throws Exception {

    when(board.getBoardId())
        .thenReturn(BoardId.newBoardId());

    mockMvc.perform(MockMvcRequestBuilders.post(BoardController.CREATE_BOARD_COMMAND_URL)
                                          .contentType(APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(CreateBoardCommandRequest
                                                                     .of("MY_BOARD"))))
           .andDo(print())
           .andExpect(status().isCreated())
           .andExpect(header().string("location", "boards/"+ board.getBoardId().getId()));
  }

  @DisplayName("Create a board returns 400 http code")
  @Test
  public void createBoard_is_erroneous() throws Exception {

    when(commandBus.accept(any()))
        .thenReturn(left(AN_ERROR));

    mockMvc.perform(MockMvcRequestBuilders.post(BoardController.CREATE_BOARD_COMMAND_URL)
                                          .contentType(APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(CreateBoardCommandRequest
                                                                     .of("MY_BOARD"))))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(content().string(AN_ERROR.getMessage()));
  }

  @DisplayName("Update a board returns 204 http code")
  @Test
  public void updateBoardName_is_successful() throws Exception {

    when(commandBus.accept(any()))
        .thenReturn(right(board));

    mockMvc.perform(MockMvcRequestBuilders.put(BoardController.UPDATE_BOARD_NAME_COMMAND_URL, UUID.randomUUID())
                                          .contentType(APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                            UpdateBoadNameCommandRequest.of("NEW_BOARD_NAME"))))
           .andDo(print())
           .andExpect(status().isNoContent());
  }

  @DisplayName("Update a board returns 400 http code")
  @Test
  public void updateBoardName_is_erroneous() throws Exception {

    when(commandBus.accept(any()))
        .thenReturn(left(AN_ERROR));

    mockMvc.perform(MockMvcRequestBuilders.put(BoardController.UPDATE_BOARD_NAME_COMMAND_URL, UUID.randomUUID())
                                          .contentType(APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(
                            UpdateBoadNameCommandRequest.of("NEW_BOARD_NAME"))))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(content().string(AN_ERROR.getMessage()));
  }

  @DisplayName("Archive a board returns 204 http code")
  @Test
  public void archiveBoard_is_successful() throws Exception {

    when(commandBus.accept(any()))
        .thenReturn(right(board));

    mockMvc.perform(MockMvcRequestBuilders.put(BoardController.ARCHIVE_BOARD_COMMAND_URL, UUID.randomUUID())
                                          .contentType(APPLICATION_JSON))
           .andDo(print())
           .andExpect(status().isNoContent());
  }

  @DisplayName("Archive a board returns 400 http code")
  @Test
  public void archiveBoard_is_erroneous() throws Exception {

    when(commandBus.accept(any()))
        .thenReturn(left(AN_ERROR));

    mockMvc.perform(MockMvcRequestBuilders.put(BoardController.ARCHIVE_BOARD_COMMAND_URL, UUID.randomUUID())
                                          .contentType(APPLICATION_JSON))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(content().string(AN_ERROR.getMessage()));
  }
}