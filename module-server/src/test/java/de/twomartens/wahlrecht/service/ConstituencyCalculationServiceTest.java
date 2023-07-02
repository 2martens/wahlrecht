package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.Candidate;
import de.twomartens.wahlrecht.model.Constituency;
import de.twomartens.wahlrecht.model.ElectedResult;
import de.twomartens.wahlrecht.model.Nomination;
import de.twomartens.wahlrecht.model.VotingResult;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConstituencyCalculationServiceTest {
  private static final Nomination SPD_NIENDORF = buildNomination("SPD-Niendorf");
  private static final Nomination CDU_NIENDORF = buildNomination("CDU-Niendorf");
  private static final Nomination FDP_NIENDORF = buildNomination("FDP-Niendorf");
  private static final Nomination LINKE_NIENDORF = buildNomination("LINKE-Niendorf");
  private static final Nomination GRUENE_NIENDORF = buildNomination("GRUENE-Niendorf");
  private static final Nomination AFD_NIENDORF = buildNomination("AFD-Niendorf");

  private ConstituencyCalculationService service;

  @BeforeEach
  void setService() {
    service = new ConstituencyCalculationService();
  }

  @Test
  void shouldCalculateConstituencyCorrectly() {
    Constituency constituency = new Constituency("Niendorf", 5);
    Collection<Nomination> nominations = List.of(
        SPD_NIENDORF,
        CDU_NIENDORF,
        LINKE_NIENDORF,
        FDP_NIENDORF,
        GRUENE_NIENDORF,
        AFD_NIENDORF
    );
    setUpCandidates();
    setUpResults();

    ElectedResult elected = service.calculateConstituency(constituency, nominations);

    Assertions.assertThat(elected.electedCandidates())
        .containsKey(SPD_NIENDORF)
        .containsKey(CDU_NIENDORF)
        .containsKey(GRUENE_NIENDORF)
        .doesNotContainKey(LINKE_NIENDORF)
        .doesNotContainKey(AFD_NIENDORF)
        .doesNotContainKey(FDP_NIENDORF);
  }

  private void setUpCandidates() {
    SPD_NIENDORF.addCandidates(new Candidate("Schwarzarius, Ines", ""));
    SPD_NIENDORF.addCandidates(new Candidate("Urbanski, Torge", ""));
    SPD_NIENDORF.addCandidates(new Candidate("Fischbach-Pyttel, Carola", ""));
    SPD_NIENDORF.addCandidates(new Candidate("Bäcker, Guido", ""));
    SPD_NIENDORF.addCandidates(new Candidate("Porr, Ulrike", ""));
    SPD_NIENDORF.addCandidates(new Candidate("Petersen, Johannes", ""));
    SPD_NIENDORF.addCandidates(new Candidate("Kröger, Manuela-Andrea", ""));
    SPD_NIENDORF.addCandidates(new Candidate("Steppat, Wolfgang", ""));
    SPD_NIENDORF.addCandidates(new Candidate("Canbay, Berfin", ""));
    SPD_NIENDORF.addCandidates(new Candidate("Schumacher, Wolfgang", ""));

    CDU_NIENDORF.addCandidates(new Candidate("Kuhn, Rüdiger", ""));
    CDU_NIENDORF.addCandidates(new Candidate("Ahrens, Mareike", ""));
    CDU_NIENDORF.addCandidates(new Candidate("Wysocki, Michael", ""));
    CDU_NIENDORF.addCandidates(new Candidate("Dönselmann, Malte", ""));
    CDU_NIENDORF.addCandidates(new Candidate("Seehaber, Daniel", ""));

    LINKE_NIENDORF.addCandidates(new Candidate("Dr. Khenkhar, Malik", ""));
    LINKE_NIENDORF.addCandidates(new Candidate("Radom, Claudia", ""));
    LINKE_NIENDORF.addCandidates(new Candidate("Kara, Başak", ""));

    FDP_NIENDORF.addCandidates(new Candidate("Häffs, Marvin", ""));
    FDP_NIENDORF.addCandidates(new Candidate("Bahmer, Larissa", ""));
    FDP_NIENDORF.addCandidates(new Candidate("Patzer, Heinrich-Otto", ""));

    GRUENE_NIENDORF.addCandidates(new Candidate("Demirhan, Sina", ""));
    GRUENE_NIENDORF.addCandidates(new Candidate("Schmidt, Lutz", "Redakteur"));

    AFD_NIENDORF.addCandidates(new Candidate("Hanssen, Wolfgang", ""));
  }

  private void setUpResults() {
    SPD_NIENDORF.setVotingResult(VotingResult.builder()
        .votesOnNomination(0)
        .votesPerPosition(Map.of(
            1, 11882,
            2, 7617,
            3, 1946,
            4, 1743,
            5, 1363,
            6, 762,
            7, 895,
            8, 1598,
            9, 1786,
            10, 781
        ))
        .build());

    CDU_NIENDORF.setVotingResult(VotingResult.builder()
        .votesOnNomination(0)
        .votesPerPosition(Map.of(
            1, 10733,
            2, 4550,
            3, 2310,
            4, 2333,
            5, 2565
        ))
        .build());

    LINKE_NIENDORF.setVotingResult(VotingResult.builder()
        .votesOnNomination(0)
        .votesPerPosition(Map.of(
            1, 3782,
            2, 1584,
            3, 1185
        ))
        .build());

    FDP_NIENDORF.setVotingResult(VotingResult.builder()
        .votesOnNomination(0)
        .votesPerPosition(Map.of(
            1, 2498,
            2, 3200,
            3, 992
        ))
        .build());

    GRUENE_NIENDORF.setVotingResult(VotingResult.builder()
        .votesOnNomination(0)
        .votesPerPosition(Map.of(
            1, 14838,
            2, 12501
        ))
        .build());

    AFD_NIENDORF.setVotingResult(VotingResult.builder()
        .votesOnNomination(0)
        .votesPerPosition(Map.of(
            1, 6616
        ))
        .build());
  }

  private static Nomination buildNomination(String name) {
    return new Nomination(name, false);
  }
}