package de.twomartens.wahlrecht.controller.v1

import de.twomartens.wahlrecht.mapper.v1.ElectionResultMapper
import de.twomartens.wahlrecht.model.dto.ErrorMessage
import de.twomartens.wahlrecht.model.dto.v1.ElectionResult
import de.twomartens.wahlrecht.service.ElectionResultService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.mapstruct.factory.Mappers
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import reactor.util.function.Tuples

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = ["/wahlrecht/v1"])
@Tag(name = "ElectionResults", description = "all requests relating to election results")
class ElectionResultController(private val service: ElectionResultService) {
  private val mapper = Mappers.getMapper(ElectionResultMapper::class.java)

  @Operation(
      summary = "Returns election result for given election name",
      responses = [ApiResponse(
          responseCode = "200",
          description = "Returns stored election result for given election name",
          content = [Content(mediaType = "application/json", schema = Schema(implementation = ElectionResult::class))]
      ), ApiResponse(
          responseCode = "404",
          description = "No election result found",
          content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorMessage::class))]
      )]
  )
  @GetMapping(
      value = ["/electionResult/{electionName}"],
      produces = [MediaType.APPLICATION_JSON_VALUE]
  )
  @SecurityRequirement(name = "bearerAuth")
  @SecurityRequirement(name = "oauth2")
  fun getElectionByName(
      @PathVariable @Parameter(description = "the election name", example = "Bezirkswahl 2019") electionName: String
  ): Mono<ResponseEntity<ElectionResult>> {
    return service.getElectionResult(electionName)
        .map {
          mapper.mapToExternal(it)
        }
        .map {
          ResponseEntity.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(it)
        }
  }

  @Operation(
      summary = "Stores a new election",
      responses = [ApiResponse(
          responseCode = "201",
          description = "Election was created"
      ), ApiResponse(responseCode = "200", description = "Election was modified"), ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = [Content(mediaType = "plain/text")]
      )]
  )
  @PutMapping("/electionResult")
  @SecurityRequirement(name = "bearerAuth")
  @SecurityRequirement(name = "oauth2")
  fun putElection(@RequestBody electionResult: ElectionResult,
                  uriComponentsBuilder: UriComponentsBuilder): Mono<ResponseEntity<ElectionResult>> {
    val storedElection = service.storeResult(mapper.mapToDB(electionResult))
    return storedElection
        .map { tuple ->
          Tuples.of(tuple.t1, mapper.mapToExternal(tuple.t2))
        }
        .map { tuple ->
          if (tuple.t1) {
            ResponseEntity.created(uriComponentsBuilder.path("/electionResult/{electionName}")
                .buildAndExpand(tuple.t2.electionName).toUri())
                .contentType(MediaType.APPLICATION_JSON)
                .body(tuple.t2)
          } else {
            ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(tuple.t2)
          }
        }
  }
}
