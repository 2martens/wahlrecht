package de.twomartens.wahlrecht.controller.v1

import de.twomartens.wahlrecht.mapper.v1.ElectionMapper
import de.twomartens.wahlrecht.model.dto.v1.Election
import de.twomartens.wahlrecht.service.ElectionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.mapstruct.factory.Mappers
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuples

@RestController
@RequestMapping(value = ["/wahlrecht/v1"])
@Tag(name = "Elections", description = "all requests relating to elections")
class ElectionController(private val service: ElectionService) {
  private val mapper = Mappers.getMapper(ElectionMapper::class.java)

  @SecurityRequirement(name = "oauth2")
  @SecurityRequirement(name = "bearerAuth")
  @GetMapping(value = ["/elections"])
  @Operation(
      summary = "Returns all stored elections",
      responses = [ApiResponse(
          responseCode = "200",
          description = "Returns all stored elections",
          content = [Content(
              mediaType = "application/json",
              array = ArraySchema(
                  schema = Schema(
                      implementation = Election::class
                  )
              )
          )]
      )]
  )
  fun getElections(): Flux<Election> {
    val elections = service.getElections()
        .map {
          mapper.mapToExternal(it)
        }
    return elections
  }

  @Operation(
      summary = "Returns election matching provided name",
      responses = [ApiResponse(
          responseCode = "200",
          description = "Returns matching election",
          content = [Content(mediaType = "application/json", schema = Schema(implementation = Election::class))]
      )]
  )
  @GetMapping(value = ["/elections/{electionName}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @SecurityRequirement(name = "bearerAuth")
  @SecurityRequirement(name = "oauth2")
  fun getElectionByName(
      @PathVariable @Parameter(description = "the election name", example = "Bezirkswahl 2019") electionName: String
  ): ResponseEntity<Mono<Election>> {
    val election = service.findElectionByName(electionName)
        .map { mapper.mapToExternal(it) }
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(election)
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
  @PutMapping("/election")
  @SecurityRequirement(name = "bearerAuth")
  @SecurityRequirement(name = "oauth2")
  fun putElection(@RequestBody election: Election, uriComponentsBuilder: UriComponentsBuilder): Mono<ResponseEntity<Election>> {
    return service.storeElection(mapper.mapToDB(election))
        .map { tuple ->
          Tuples.of(tuple.t1, mapper.mapToExternal(tuple.t2))
        }
        .map { tuple ->
          if (tuple.t1) {
            ResponseEntity.created(uriComponentsBuilder.path("/elections/{electionName}")
                .buildAndExpand(tuple.t2.name).toUri())
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
