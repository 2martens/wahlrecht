package de.twomartens.wahlrecht.controller.v1;

import de.twomartens.wahlrecht.mapper.v1.GreetingMapper;
import de.twomartens.wahlrecht.model.dto.v1.Greeting;
import de.twomartens.wahlrecht.service.GreetingService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/wahlrecht/v1")
@Tag(name = "Greeting Example", description = "all requests relating to greetings")
public class GreetingRestController {

  private final GreetingMapper mapper = Mappers.getMapper(GreetingMapper.class);

  private final GreetingService service;

  @Operation(
      summary = "Returns a greeting message",
      responses = {@ApiResponse(
          responseCode = "200")
      }
  )
  @GetMapping("/greeting")
  public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
    return mapper.map(service.createGreeting(name));
  }

  @Operation(
      summary = "Posts a greeting message to db",
      responses = {@ApiResponse(
          responseCode = "200")
      }
  )
  @PostMapping("/greeting")
  public void postGreeting(@RequestBody Greeting greeting) {
    service.postGreeting(mapper.map(greeting));
  }

  @Hidden
  @GetMapping("/healthCheck")
  public String checkHealth(@RequestParam(value = "message") String message) {
    return message;
  }

}
