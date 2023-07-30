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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

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
    @GetMapping(value = ["/parties/by-election-name/{electionName}"], produces = [MediaType.APPLICATION_JSON_VALUE])
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
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(parties)
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
    fun putParty(@RequestBody party: PartyInElection?): ResponseEntity<*> {
        val createdNew = service.storeParty(mapper.mapToDB(party!!))
        return if (createdNew) ResponseEntity<Any>(HttpStatus.CREATED) else ResponseEntity<Any>(HttpStatus.OK)
    }
}
