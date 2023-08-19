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
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping(value = ["/wahlrecht/v1"])
@Tag(name = "Elections", description = "all requests relating to elections")
class ElectionController(private val service: ElectionService) {
  private val mapper = Mappers.getMapper(ElectionMapper::class.java)

  @SecurityRequirement(name = "oauth2")
  @SecurityRequirement(name = "bearerAuth")
  @GetMapping(value = ["/elections"], produces = [MediaType.APPLICATION_JSON_VALUE])
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
  fun getElections(): ResponseEntity<Collection<Election>> {
    val elections = service.elections.asSequence()
        .map { mapper.mapToExternal(it.value) }
        .toList()
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(elections)
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
      @PathVariable @Parameter(description = "the election name", example = "Bezirkswahl 2019") electionName: String?
  ): ResponseEntity<Election> {
    val election = mapper.mapToExternal(service.elections[electionName]!!)
    return ok()
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
  fun putElection(@RequestBody election: Election?, uriComponentsBuilder: UriComponentsBuilder):
      ResponseEntity<Void> {
    val createdNew = service.storeElection(mapper.mapToDB(election!!))
    return if (createdNew) {
      created(uriComponentsBuilder.path("/elections/{electionName}")
          .buildAndExpand(election.name).toUri())
          .build()
    }
    else ok().build()
  }
}
