package com.sy.board.management.command.board.port.incoming.adapter.resources;

import static com.sy.board.management.command.board.port.incoming.adapter.resources.CardListController.CARD_LISTS_COMMAND_ROOT_URL;
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
import com.sy.board.management.command.board.domain.model.cardlist.CardList;
import com.sy.board.management.command.board.domain.model.cardlist.CardListId;
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

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@AutoConfigureMockMvc()
@WebMvcTest(controllers = CardListController.class)
public class CardListControllerTest {

  private static Error AN_ERROR = Error.of("Operation cannot be completed");

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private CardList cardList;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CommandBus commandBus;

  @DisplayName("Create a card list returns 204 http code")
  @Test
  public void createCardList_is_successful() throws Exception {

    when(commandBus.accept(any()))
        .thenReturn(right(cardList));
    when(cardList.getCardListId())
        .thenReturn(CardListId.newCardListId());

    mockMvc.perform(post(CARD_LISTS_COMMAND_ROOT_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CreateCardListRequest.of(randomUUID(), "TODO"))))
           .andDo(print())
           .andExpect(status().isCreated())
           .andExpect(header().string("location", "cardLists/"+ cardList.getCardListId().getId()));
  }

  @DisplayName("Create a card list returns 400 http code")
  @Test
  public void createCardList_is_erroneous() throws Exception {

    when(commandBus.accept(any()))
        .thenReturn(left(AN_ERROR));

    mockMvc.perform(post(CARD_LISTS_COMMAND_ROOT_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CreateCardListRequest.of(randomUUID(), "TODO"))))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(content().string(AN_ERROR.getMessage()));
  }

}