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
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

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
        value = ["/electionResults/{electionName}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "oauth2")
    fun getElectionResultByElectionName(
        @PathVariable @Parameter(description = "the election name", example = "Bezirkswahl 2019") electionName: String
    ): ResponseEntity<ElectionResult> {
        val result = mapper.mapToExternal(service.getElectionResult(electionName))
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(result)
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
    fun putElection(@RequestBody electionResult: ElectionResult?,
                    uriComponentsBuilder: UriComponentsBuilder): ResponseEntity<Void> {
        val createdNew = service.storeResult(mapper.mapToDB(electionResult!!))
        return if (createdNew) {
          created(uriComponentsBuilder.path("/electionResult/{electionName}")
              .buildAndExpand(electionResult.electionName).toUri())
              .build()
        }
        else ok().build()
    }
}
