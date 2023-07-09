package de.twomartens.wahlrecht.controller.v1;

import de.twomartens.wahlrecht.mapper.v1.PartyInElectionMapper;
import de.twomartens.wahlrecht.model.dto.ErrorMessage;
import de.twomartens.wahlrecht.model.dto.v1.PartyInElection;
import de.twomartens.wahlrecht.service.PartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/wahlrecht/v1")
@Tag(name = "Parties", description = "all requests relating to parties")
public class PartyController {

  private final PartyInElectionMapper mapper = Mappers.getMapper(PartyInElectionMapper.class);
  private final PartyService service;

  @Operation(
      summary = "Returns all stored parties for given election name",
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Returns all stored parties",
              content = {@Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = PartyInElection.class)))}
          ),
          @ApiResponse(responseCode = "404",
              description = "No parties found",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ErrorMessage.class)
                  )
              }
          )
      }
  )
  @GetMapping(value = "/parties/by-election-name/{electionName}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Collection<PartyInElection>> getPartiesByElectionName(
      @PathVariable @Parameter(description = "the election name", example = "Bezirkswahl 2019") String electionName) {
    List<PartyInElection> parties = service.getPartiesByElectionName(electionName).stream()
        .map(mapper::mapToExternal)
        .toList();

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(parties);
  }

  @Operation(
      summary = "Stores a new party in an election",
      responses = {
          @ApiResponse(responseCode = "201",
              description = "Party was created"),
          @ApiResponse(responseCode = "200",
              description = "Party was modified"),
          @ApiResponse(responseCode = "401",
              description = "Unauthorized",
              content = {@Content(
                  mediaType = "plain/text")}
          )
      }
  )
  @PutMapping("/party")
  @SecurityRequirement(name = "basicAuth")
  public ResponseEntity<?> putElection(@RequestBody PartyInElection party) {
    boolean createdNew = service.storeParty(mapper.mapToDB(party));
    return createdNew
        ? new ResponseEntity<>(HttpStatus.CREATED)
        : new ResponseEntity<>(HttpStatus.OK);
  }
}
