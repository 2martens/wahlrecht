package de.twomartens.wahlrecht.controller.v1;

import de.twomartens.wahlrecht.mapper.v1.ElectionMapper;
import de.twomartens.wahlrecht.mapper.v1.PartyInElectionMapper;
import de.twomartens.wahlrecht.model.dto.ErrorMessage;
import de.twomartens.wahlrecht.model.dto.v1.Election;
import de.twomartens.wahlrecht.model.dto.v1.PartyInElection;
import de.twomartens.wahlrecht.service.ElectionService;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/wahlrecht/v1")
@Tag(name = "Parties", description = "all requests relating to parties")
public class PartyController {

  private final PartyInElectionMapper mapper = Mappers.getMapper(PartyInElectionMapper.class);
  private final ElectionMapper electionMapper = Mappers.getMapper(ElectionMapper.class);
  private final PartyService service;
  private final ElectionService electionService;

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
              description = "Unauthorized"
          )
      }
  )
  @PutMapping("/party")
  @SecurityRequirement(name = "basicAuth")
  public ResponseEntity<?> putParty(@RequestBody PartyInElection party) {
    boolean createdNew = service.storeParty(mapper.mapToDB(party));
    return createdNew
        ? new ResponseEntity<>(HttpStatus.CREATED)
        : new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/party")
  public String submitCreatePartyForm(@ModelAttribute PartyInElection party, Model model) {
    service.storeParty(mapper.mapToDB(party));
    model.addAttribute("party", party);
    model.addAttribute("isCreate", false);
    return "partyUpdate";
  }

  @GetMapping("/party")
  public String createPartyForm(Model model) {
    model.addAttribute("party", new PartyInElection());
    model.addAttribute("isCreate", true);
    return "partyCreate";
  }

  @PostMapping("/party/{electionName}/{abbreviation}")
  public String submitUpdatePartyForm(
      @PathVariable String electionName,
      @PathVariable String abbreviation,
      @ModelAttribute PartyInElection party,
      Model model) {
    return submitCreatePartyForm(party, model);
  }


  @GetMapping("/party/{electionName}/{abbreviation}")
  public String updatePartyForm(
      @PathVariable String electionName,
      @PathVariable String abbreviation,
      Model model) {
    PartyInElection party = mapper.mapToExternal(
        service.getPartyByElectionNameAndAbbreviation(electionName, abbreviation));
    model.addAttribute("party", party);
    model.addAttribute("isCreate", false);

    return "partyUpdate";
  }

  @ModelAttribute("elections")
  Collection<Election> elections() {
    return electionService.getElections().stream()
        .map(electionMapper::mapToExternal)
        .toList();
  }
}
