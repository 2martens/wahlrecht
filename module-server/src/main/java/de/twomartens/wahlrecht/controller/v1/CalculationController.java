package de.twomartens.wahlrecht.controller.v1;

import de.twomartens.wahlrecht.mapper.v1.ElectedCandidatesMapper;
import de.twomartens.wahlrecht.mapper.v1.ElectionResultMapper;
import de.twomartens.wahlrecht.model.dto.v1.ElectedCandidates;
import de.twomartens.wahlrecht.model.dto.v1.ElectionResult;
import de.twomartens.wahlrecht.service.CalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/wahlrecht/v1")
@Tag(name = "Calculations", description = "all requests relating to calculations")
public class CalculationController {

  private final ElectedCandidatesMapper electedCandidatesMapper = Mappers.getMapper(
      ElectedCandidatesMapper.class);
  private final ElectionResultMapper electionResultMapper = Mappers.getMapper(
      ElectionResultMapper.class);
  private final CalculationService service;

  @Operation(
      summary = "Calculates a provided election result",
      description = "This request does not store any result and is idempotent.",
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Returns all elected candidates",
              content = {@Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ElectedCandidates.class))}
          )
      }
  )
  @PostMapping(value = "/calculate", produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirement(name = "bearerAuth")
  @SecurityRequirement(name = "oauth2")
  public ResponseEntity<ElectedCandidates> calculateResult(
      @RequestBody ElectionResult electionResult
  ) {
    ElectedCandidates result = electedCandidatesMapper.mapToExternal(
        service.determineElectedCandidates(electionResultMapper.mapToInternal(electionResult)));
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(result);
  }
}
