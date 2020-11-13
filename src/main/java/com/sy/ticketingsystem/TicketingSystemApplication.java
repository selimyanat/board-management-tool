package com.sy.ticketingsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TicketingSystemApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(TicketingSystemApplication.class, args);
  }

  @Override
  public void run(String... args)  {

		//repository.findAll().forEach(storedEvent -> System.out.println(storedEvent));

		//repository.findByStreamId("1e6641c1-fd6a-4483-a30f-0d2a08280d7b").forEach(storedEvent ->
    // System.out.println(storedEvent));
//		var storedEvent = StoredEvent.newInstance(UUID.randomUUID().toString(), 1,
//                                              BoardCreated.class.getName(),
//                                              eventSerializer.serialize(BoardCreated.newInstance(BoardId.newBoardId(), "sommeboard")).get()
//    );
//    System.out.println(eventSerializer.deserialize(storedEvent.getPayload(), BoardCreated.class).get());
//
//    repository.save(storedEvent);

  }
}
