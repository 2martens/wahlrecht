package de.twomartens.wahlrecht.controller.v1;

import de.twomartens.wahlrecht.mapper.v1.ElectionResultMapper;
import de.twomartens.wahlrecht.model.dto.ErrorMessage;
import de.twomartens.wahlrecht.model.dto.v1.ElectionResult;
import de.twomartens.wahlrecht.service.ElectionResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ElectionResults", description = "all requests relating to election results")
public class ElectionResultController {

  private final ElectionResultMapper mapper = Mappers.getMapper(ElectionResultMapper.class);
  private final ElectionResultService service;

  @Operation(
      summary = "Returns election result for given election name",
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Returns stored election result for given election name",
              content = {@Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ElectionResult.class))}
          ),
          @ApiResponse(responseCode = "404",
              description = "No election result found",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ErrorMessage.class)
                  )
              }
          )
      }
  )
  @GetMapping(value = "/electionResult/by-election-name/{electionName}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ElectionResult> getElectionByName(
      @PathVariable @Parameter(description = "the election name", example = "Bezirkswahl 2019") String electionName) {
    ElectionResult result = mapper.mapToExternal(service.getElectionResult(electionName));
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(result);
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
  @PutMapping("/electionResult")
  @SecurityRequirement(name = "basicAuth")
  public ResponseEntity<?> putElection(@RequestBody ElectionResult electionResult) {
    boolean createdNew = service.storeResult(mapper.mapToDB(electionResult));
    return createdNew
        ? new ResponseEntity<>(HttpStatus.CREATED)
        : new ResponseEntity<>(HttpStatus.OK);
  }
}
