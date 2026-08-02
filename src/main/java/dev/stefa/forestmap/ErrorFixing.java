package dev.stefa.forestmap;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ErrorFixing implements CommandLineRunner {
  AdeWfsClient client;

  @Override
  public void run(String... args) throws Exception {
    // [45.029322,8.086852 -> 45.035387,8.098439]
//    WfsPage page = client.getFeatures(new BoundingBox(45.029322, 8.086852, 45.035387, 8.098439));
//    log.info(new String(page.raw()));
  }
}
