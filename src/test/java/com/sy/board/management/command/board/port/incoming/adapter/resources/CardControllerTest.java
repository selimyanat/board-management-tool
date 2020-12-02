package com.sy.board.management.command.board.port.incoming.adapter.resources;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sy.board.management.command.board.domain.model.cardlist.Card;
import com.sy.board.management.command.board.domain.model.cardlist.CardId;
import com.sy.board.management.command.board.usecase.CreateCardCommand;
import com.sy.board.management.core.command.CommandBus;
import com.sy.board.management.core.domain.model.Error;
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
@WebMvcTest(controllers = CardController.class)
public class CardControllerTest {

  private static Error AN_ERROR = Error.of("Operation cannot be completed");

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private Card card;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CommandBus commandBus;


  @DisplayName("Create a card returns 204 http code")
  @Test
  public void createCard_is_successful() throws Exception {

    when(commandBus.accept(any()))
        .thenReturn(right(card));
    when(card.getCardId())
        .thenReturn(CardId.newCardId());

    mockMvc.perform(MockMvcRequestBuilders.post(CardController.CARD_COMMAND_ROOT_URL)
                                          .contentType(APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(CreateCardCommand.of(randomUUID(),
                                                                                      "Task Name",
                                                                                      "Task Description")
                        )))
           .andDo(print())
           .andExpect(status().isCreated())
           .andExpect(header().string("location", "cards/"+ card.getCardId().getId()));
  }

  @DisplayName("Create a card returns 400 http code")
  @Test
  public void createCard_is_erroneous() throws Exception {

    when(commandBus.accept(any()))
        .thenReturn(left(AN_ERROR));

    mockMvc.perform(MockMvcRequestBuilders.post(CardController.CARD_COMMAND_ROOT_URL)
                                          .contentType(APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(CreateCardCommand.of(randomUUID(),
                                                                                      "Task Name",
                                                                                      "Task Description")
                        )))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(content().string(AN_ERROR.getMessage()));
  }

}