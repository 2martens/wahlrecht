package de.twomartens.wahlrecht.controller.v1;

import de.twomartens.wahlrecht.mapper.v1.ElectionMapper;
import de.twomartens.wahlrecht.model.dto.v1.Election;
import de.twomartens.wahlrecht.service.ElectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/wahlrecht/v1")
@Tag(name = "Elections", description = "all requests relating to elections")
public class ElectionController {

  private final ElectionMapper mapper = Mappers.getMapper(ElectionMapper.class);
  private final ElectionService service;

  @Operation(
      summary = "Returns all stored elections",
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Returns all stored elections",
              content = {@Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Election.class)))}
          )
      }
  )
  @GetMapping(value = "/elections", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Collection<Election>> getElections() {
    List<Election> elections = service.getElections().stream().map(mapper::mapToExternal).toList();
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(elections);
  }

  @Operation(
      summary = "Stores a new election",
      responses = {
          @ApiResponse(responseCode = "201",
              description = "Election was created"),
          @ApiResponse(responseCode = "200",
              description = "Election was modified"),
          @ApiResponse(responseCode = "401",
              description = "Unauthorized",
              content = {@Content(
                  mediaType = "plain/text")}
          )
      }
  )
  @PutMapping("/election")
  @SecurityRequirement(name = "basicAuth")
  public ResponseEntity<?> putElection(@RequestBody Election election) {
    boolean createdNew = service.storeElection(mapper.mapToDB(election));
    return createdNew
        ? new ResponseEntity<>(HttpStatus.CREATED)
        : new ResponseEntity<>(HttpStatus.OK);
  }
}
