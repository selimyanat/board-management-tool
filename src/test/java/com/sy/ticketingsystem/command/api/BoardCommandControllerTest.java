package com.sy.ticketingsystem.command.api;

import static com.sy.ticketingsystem.command.api.BoardCommandController.ARCHIVE_BOARD_COMMAND_URL;
import static com.sy.ticketingsystem.command.api.BoardCommandController.CREATE_BOARD_COMMAND_URL;
import static com.sy.ticketingsystem.command.api.BoardCommandController.UPDATE_BOARD_NAME_COMMAND_URL;
import static com.sy.ticketingsystem.core.domain.model.Unit.unit;
import static io.vavr.control.Either.right;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sy.ticketingsystem.core.application.CommandBus;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc()
@WebMvcTest(controllers = BoardCommandController.class)
public class BoardCommandControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CommandBus commandBus;

  @DisplayName("Create a board returns 204 http code")
  @Test
  public void createBoard_is_successful() throws Exception {

    when(commandBus.accept(any())).thenReturn(right(unit()));

    mockMvc.perform(post(CREATE_BOARD_COMMAND_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("MY_BOARD")))
           .andDo(print())
           .andExpect(status().isCreated())
           .andExpect(header().exists("location"));
  }

  @DisplayName("Update a board returns 204 http code")
  @Test
  public void updateBoardName_is_successful() throws Exception {

    when(commandBus.accept(any())).thenReturn(right(unit()));

    mockMvc.perform(put(UPDATE_BOARD_NAME_COMMAND_URL, UUID.randomUUID())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("THE_BOARD")))
           .andDo(print())
           .andExpect(status().isNoContent());
  }

  @DisplayName("Archive a board returns 204 http code")
  @Test
  public void archiveBoar_is_successful() throws Exception {

    when(commandBus.accept(any())).thenReturn(right(unit()));

    mockMvc.perform(put(ARCHIVE_BOARD_COMMAND_URL, UUID.randomUUID())
                        .contentType(APPLICATION_JSON))
           .andDo(print())
           .andExpect(status().isNoContent());
  }
}