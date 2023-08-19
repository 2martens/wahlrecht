package de.twomartens.wahlrecht.controller.v1

import de.twomartens.wahlrecht.mapper.v1.PartyInElectionMapper
import de.twomartens.wahlrecht.model.dto.ErrorMessage
import de.twomartens.wahlrecht.model.dto.v1.PartyInElection
import de.twomartens.wahlrecht.service.PartyService
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
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@Controller
@RequestMapping(value = ["/wahlrecht/v1"])
@Tag(name = "Parties", description = "all requests relating to parties")
class PartyController(private val service: PartyService) {
  private val mapper = Mappers.getMapper(PartyInElectionMapper::class.java)

  @Operation(
      summary = "Returns all stored parties for given election name",
      responses = [ApiResponse(
          responseCode = "200",
          description = "Returns all stored parties",
          content = [Content(
              mediaType = "application/json",
              array = ArraySchema(schema = Schema(implementation = PartyInElection::class))
          )]
      ), ApiResponse(
          responseCode = "404",
          description = "No parties found",
          content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorMessage::class))]
      )]
  )
  @GetMapping(value = ["/parties/{electionName}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @SecurityRequirement(name = "bearerAuth")
  @SecurityRequirement(name = "oauth2")
  fun getPartiesByElectionName(
      @PathVariable @Parameter(description = "the election name", example = "Bezirkswahl 2019") electionName: String
  ): ResponseEntity<Collection<PartyInElection>> {
    val parties = service.getPartiesByElectionName(electionName).stream()
        .map { election: de.twomartens.wahlrecht.model.db.PartyInElection? ->
          mapper.mapToExternal(
              election!!
          )
        }
        .toList()
    return ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(parties)
  }

  @Operation(
      summary = "Returns stored party for given election name and party abbreviation",
      responses = [ApiResponse(
          responseCode = "200",
          description = "Returns stored party",
          content = [Content(
              mediaType = "application/json",
              schema = Schema(implementation = PartyInElection::class)
          )]
      ), ApiResponse(
          responseCode = "404",
          description = "No party found",
          content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorMessage::class))]
      )]
  )
  @GetMapping(value = ["/parties/{electionName}/{abbreviation}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @SecurityRequirement(name = "bearerAuth")
  @SecurityRequirement(name = "oauth2")
  fun getPartyByElectionNameAndAbbreviation(
      @PathVariable @Parameter(description = "the election name", example = "Bezirkswahl 2019") electionName: String,
      @PathVariable @Parameter(description = "the party abbreviation", example = "SPD") abbreviation: String
  ): ResponseEntity<PartyInElection> {
    val party = mapper.mapToExternal(
        service.getPartyByElectionNameAndAbbreviation(electionName, abbreviation)
    )
    return ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(party)
  }

  @Operation(
      summary = "Stores a new party in an election",
      responses = [ApiResponse(
          responseCode = "201",
          description = "Party was created"
      ), ApiResponse(responseCode = "200", description = "Party was modified"), ApiResponse(
          responseCode = "401",
          description = "Unauthorized"
      )]
  )
  @PutMapping("/party")
  @SecurityRequirement(name = "bearerAuth")
  @SecurityRequirement(name = "oauth2")
  fun putParty(@RequestBody party: PartyInElection?,
               uriComponentsBuilder: UriComponentsBuilder): ResponseEntity<Void> {
    val createdNew = service.storeParty(mapper.mapToDB(party!!))
    return if (createdNew) {
      created(uriComponentsBuilder.path("/parties/{electionName}/{abbreviation}")
          .buildAndExpand(party.electionName, party.abbreviation).toUri())
          .build()
    } else ok().build()
  }
}
