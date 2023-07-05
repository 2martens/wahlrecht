package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.Candidate;
import de.twomartens.wahlrecht.model.Constituency;
import de.twomartens.wahlrecht.model.Elected;
import de.twomartens.wahlrecht.model.ElectedResult;
import de.twomartens.wahlrecht.model.Election;
import de.twomartens.wahlrecht.model.Nomination;
import de.twomartens.wahlrecht.model.SeatResult;
import de.twomartens.wahlrecht.model.VotingResult;
import de.twomartens.wahlrecht.model.VotingThreshold;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

class CalculationServiceTest {

  private static final Constituency EIMSBUETTEL_NORD = new Constituency("Eimsbüttel-Nord", 3);
  private static final Constituency EIMSBUETTEL_SUED = new Constituency("Eimsbüttel-Süd", 5);
  private static final Constituency HARO = new Constituency("Rotherbaum/Harvestehude", 4);
  private static final Constituency LOKSTEDT = new Constituency("Lokstedt", 3);
  private static final Constituency NIENDORF = new Constituency("Niendorf", 5);
  private static final Constituency SCHNELSEN = new Constituency("Schnelsen", 3);
  private static final Constituency EIDELSTEDT = new Constituency("Eidelstedt", 4);
  private static final Constituency STELLINGEN = new Constituency("Stellingen", 3);
  private static final Nomination SPD_NIENDORF = buildNomination("SPD-Niendorf", false);
  private static final Nomination CDU_NIENDORF = buildNomination("CDU-Niendorf", false);
  private static final Nomination FDP_NIENDORF = buildNomination("FDP-Niendorf", false);
  private static final Nomination LINKE_NIENDORF = buildNomination("LINKE-Niendorf", false);
  private static final Nomination GRUENE_NIENDORF = buildNomination("GRUENE-Niendorf", false);
  private static final Nomination AFD_NIENDORF = buildNomination("AFD-Niendorf", false);

  private static final Nomination SPD_BEZIRK = buildNomination("SPD-Bezirk", true);
  private static final Nomination CDU_BEZIRK = buildNomination("CDU-Bezirk", true);
  private static final Nomination FDP_BEZIRK = buildNomination("FDP-Bezirk", true);
  private static final Nomination LINKE_BEZIRK = buildNomination("LINKE-Bezirk", true);
  private static final Nomination GRUENE_BEZIRK = buildNomination("GRUENE-Bezirk", true);
  private static final Nomination AFD_BEZIRK = buildNomination("AFD-Bezirk", true);
  private static final Nomination PIRATEN_BEZIRK = buildNomination("Piraten-Bezirk", true);
  public static final String GOTTLIEB_GABOR = "Gottlieb, Gabor";
  public static final String RIEGEL_ANN_KATHRIN = "Riegel, Ann-Kathrin";
  public static final String HOEFLICH_JUTTA = "Höflich, Jutta";
  public static final String DR_LANGHEIN_A_W_HEINRICH = "Dr. Langhein, A.W.Heinrich";
  public static final String PAGELS_MANUELA = "Pagels, Manuela";
  public static final String WIEGMANN_ROLAND = "Wiegmann, Roland";
  public static final String KLEINERT_MIKEY = "Kleinert, Mikey";
  public static final String KUHLMANN_DIETMAR = "Kuhlmann, Dietmar";
  public static final String MARTENS_JIM = "Martens, Jim";
  public static final String DORSCH_SEBASTIAN = "Dorsch, Sebastian";
  public static final String BOHNY_CARL_MARIA = "Bohny, Carl Maria";
  public static final String KRUEGER_KLAUS = "Krüger, Klaus";
  public static final String SCHWANKE_BENJAMIN = "Schwanke, Benjamin";
  public static final String MUELLER_SOENKSEN_BURKHARDT = "Müller-Sönksen, Burkhardt";
  public static final String SCHOEMER_DIRK = "Schömer, Dirk";
  public static final String ZIMMERMANN_ELKE = "Zimmermann, Elke";
  public static final String PILLATZKE_JOERG = "Pillatzke, Jörg";
  public static final String KLEIN_ROBERT = "Klein, Robert";
  public static final String ERK_ARAMAK = "Erk, Aramak";
  public static final String DR_FISCHER_JOST_LEONHARDT = "Dr. Fischer, Jost Leonhardt";
  public static final String KUELL_GABRIELA = "Küll, Gabriela";

  private CalculationService service;

  @BeforeEach
  void setService() {
    service = new CalculationService();
  }

  @Test
  void shouldCalculateConstituencyCorrectly() {
    Collection<Nomination> nominations = List.of(
        SPD_NIENDORF,
        CDU_NIENDORF,
        LINKE_NIENDORF,
        FDP_NIENDORF,
        GRUENE_NIENDORF,
        AFD_NIENDORF
    );
    setUpCandidatesConstituency();
    setUpResultsConstituency();

    ElectedResult elected = service.calculateConstituency(NIENDORF, nominations);

    Assertions.assertThat(elected.electedCandidates())
        .containsKey(SPD_NIENDORF)
        .containsKey(CDU_NIENDORF)
        .containsKey(GRUENE_NIENDORF)
        .doesNotContainKey(LINKE_NIENDORF)
        .doesNotContainKey(AFD_NIENDORF)
        .doesNotContainKey(FDP_NIENDORF);
  }

  @Test
  void shouldCalculateOverallSeatsCorrectly() {
    Collection<Constituency> constituencies = List.of(
        EIMSBUETTEL_NORD, EIMSBUETTEL_SUED, HARO,
        LOKSTEDT, NIENDORF, SCHNELSEN, EIDELSTEDT, STELLINGEN);
    Election election = new Election("Bezirkswahl 2019",
        LocalDate.of(2019, Month.MAY, 26), VotingThreshold.THREE,
        51, constituencies);
    Collection<Nomination> nominations = List.of(
        SPD_BEZIRK,
        CDU_BEZIRK,
        LINKE_BEZIRK,
        FDP_BEZIRK,
        GRUENE_BEZIRK,
        AFD_BEZIRK
    );
    setUpCandidatesOverall();
    setUpResultsOverall();

    SeatResult result = service.calculateOverallSeatDistribution(election, nominations);

    Assertions.assertThat(result.seatsPerNomination())
        .containsEntry(SPD_BEZIRK, 12)
        .containsEntry(CDU_BEZIRK, 9)
        .containsEntry(LINKE_BEZIRK, 5)
        .containsEntry(FDP_BEZIRK, 3)
        .containsEntry(GRUENE_BEZIRK, 19)
        .containsEntry(AFD_BEZIRK, 3);
  }

  @Test
  void shouldCalculateOverallCandidatesCorrectly() {
    setUpCandidatesOverall();
    setUpResultsOverall();
    Map<Nomination, Integer> seatsPerNomination = Map.of(
        SPD_BEZIRK, 2,
        CDU_BEZIRK, 2,
        LINKE_BEZIRK, 3,
        GRUENE_BEZIRK, 8,
        FDP_BEZIRK, 3,
        AFD_BEZIRK, 3
    );
    setUpConstituencyResults();

    ElectedResult result = service.calculateElectedOverallCandidates(seatsPerNomination);

    Assertions.assertThat(result.electedCandidates())
        .anySatisfy((nomination, candidates) -> {
          Assertions.assertThat(nomination).isEqualTo(SPD_BEZIRK);
          Assertions.assertThat(candidates)
              .anyMatch(candidate -> candidate.getName().equals(GOTTLIEB_GABOR)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(RIEGEL_ANN_KATHRIN)
                  && candidate.getElected() == Elected.OVERALL_VOTE_ORDER);
        })
        .anySatisfy((nomination, candidates) -> {
          Assertions.assertThat(nomination).isEqualTo(CDU_BEZIRK);
          Assertions.assertThat(candidates)
              .anyMatch(candidate -> candidate.getName().equals(HOEFLICH_JUTTA)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(DR_LANGHEIN_A_W_HEINRICH)
                  && candidate.getElected() == Elected.OVERALL_VOTE_ORDER);
        })
        .anySatisfy((nomination, candidates) -> {
          Assertions.assertThat(nomination).isEqualTo(LINKE_BEZIRK);
          Assertions.assertThat(candidates)
              .anyMatch(candidate -> candidate.getName().equals(PAGELS_MANUELA)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(WIEGMANN_ROLAND)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(KLEINERT_MIKEY)
                  && candidate.getElected() == Elected.OVERALL_VOTE_ORDER);
        })
        .anySatisfy((nomination, candidates) -> {
          Assertions.assertThat(nomination).isEqualTo(FDP_BEZIRK);
          Assertions.assertThat(candidates)
              .anyMatch(candidate -> candidate.getName().equals(KRUEGER_KLAUS)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(SCHWANKE_BENJAMIN)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(MUELLER_SOENKSEN_BURKHARDT)
                  && candidate.getElected() == Elected.OVERALL_VOTE_ORDER);
        })
        .anySatisfy((nomination, candidates) -> {
          Assertions.assertThat(nomination).isEqualTo(AFD_BEZIRK);
          Assertions.assertThat(candidates)
              .anyMatch(candidate -> candidate.getName().equals(SCHOEMER_DIRK)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(ZIMMERMANN_ELKE)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(PILLATZKE_JOERG)
                  && candidate.getElected() == Elected.OVERALL_VOTE_ORDER);
        })
        .anySatisfy((nomination, candidates) -> {
          Assertions.assertThat(nomination).isEqualTo(GRUENE_BEZIRK);
          Assertions.assertThat(candidates)
              .anyMatch(candidate -> candidate.getName().equals(KUHLMANN_DIETMAR)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(KLEIN_ROBERT)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(ERK_ARAMAK)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(MARTENS_JIM)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(DR_FISCHER_JOST_LEONHARDT)
                  && candidate.getElected() == Elected.OVERALL_NOMINATION_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(KUELL_GABRIELA)
                  && candidate.getElected() == Elected.OVERALL_VOTE_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(BOHNY_CARL_MARIA)
                  && candidate.getElected() == Elected.OVERALL_VOTE_ORDER)
              .anyMatch(candidate -> candidate.getName().equals(DORSCH_SEBASTIAN)
                  && candidate.getElected() == Elected.OVERALL_VOTE_ORDER);
        });
  }

  private void setUpCandidatesConstituency() {
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

  private void setUpResultsConstituency() {
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

  private void setUpCandidatesOverall() {
    SPD_BEZIRK.addCandidates(buildCandidates(
        GOTTLIEB_GABOR, "Meding, Sarah", "Harringa, Nils", "Nendza-Ammar, Charlotte",
        "Rust, Rüdiger", "Köster-Marjanovic, Hannelore", RIEGEL_ANN_KATHRIN, "Rüter, Monika",
        "Heise, Elk", "Fischbach-Pyttel, Carola", "Schirmer, Stephan-Philipp", "Telljohann, Katharina",
        "Röder, Wilfried", "Dr. Matiss, Claudia", "Koßel, Uwe", "Wagner, Saskia", "Bäcker, Guido",
        "Tiben-Thörner, Karin", "Ptach, Stephan", "Steinbiß, Emily", "Berisha, Dafina",
        "Meyer, Simon", "Porr, Ulrike", "Lange, Frederik", "Oliva, Nora", "Drossinakis, Panagiotis",
        "Jacobs, Ruth", "Bernstein, Til", "Kröger, Manuela-Andrea", "Dr. Enzmann, Dirk",
        "Schilling, Alexandra", "Abboud, Hussein", "Petersen, Britt", "Petersen, Johannes",
        "Petersen, Doris", "Frese, Gabor", "Suh-von Bremen, Sun Won", "Steppat, Wolfgang",
        "Hildebrand, Denise"
    ));

    CDU_BEZIRK.addCandidates(buildCandidates(
        HOEFLICH_JUTTA, "Könecke, Christian", "Birnbaum, Andreas", "Möller-Fiedler, Sybille",
        "Kochmann, Franziska", "von Deutsch, Christiane", "Hoffmann, Bernd", "Holst, Christian",
        "Lau, Roman", "Hille, Robert", "Ahrens, Mareike", DR_LANGHEIN_A_W_HEINRICH,
        "Wichmann, Norbert", "Lau, Johanna", "Czernotzky, Birgit", "von Sawilski, Marlies",
        "Dönselmann, Malte",
        "Jarzembowski, Daniel", "Howe, Sönke", "Recknagel, Dennis", "Dieball, Laura",
        "Josefowsky, Ernst Günther", "Wysocki, Michael", "Thomsen, Thomas", "Greshake, Sascha",
        "Thiesen, Marianne",
        "Schüller, Michael", "Jacobs, Jürgen", "Weiler, Johannes", "Howe, Raphaela"
    ));

    LINKE_BEZIRK.addCandidates(buildCandidates(
        PAGELS_MANUELA, WIEGMANN_ROLAND, "Peters, Ralf", "Hoyer, Jonas",
        KLEINERT_MIKEY, "Schulte, Kolja", "Huwald, Kristin", "Dr. Ritter, Sabine",
        "Pirling, David", "Reipschläger, Bernhard", "Arndt, Thomas", "Artus, Holger",
        "WLaab, Helene"
    ));

    GRUENE_BEZIRK.addCandidates(buildCandidates(
        "Kern, Lisa", "Hadji Mir Agha, Ali", "Dr. Putz, Miriam", KUHLMANN_DIETMAR,
        "Dr. Hunter, Lynne", "Schmidt-Tobler, Falk", "Demirhan, Sina", KLEIN_ROBERT,
        ERK_ARAMAK, MARTENS_JIM, "Warnecke, Anne Kathrin", DR_FISCHER_JOST_LEONHARDT,
        "Kost, Cornelia", "Brandt, Christopher", "Schübel, Nina Joana", "Schmidt, Lutz",
        "Wolf, Rita",
        "Alam, Leon", "Schwarzwald, Cristina", "Thies, Nico", "Hasselmann, Annette",
        "Koriath, Jan", KUELL_GABRIELA, "Köhler, Kevin", "Hericks, Susanne",
        BOHNY_CARL_MARIA,
        "Fester, Emilia", "Hofmann, Klaus-Dieter", "Neumann, Bernd", "Braasch, Julian",
        DORSCH_SEBASTIAN, "Hasselmann, Harald"
    ));

    FDP_BEZIRK.addCandidates(buildCandidates(
        KRUEGER_KLAUS, SCHWANKE_BENJAMIN, "Cleven, Martina", "Zielinski, Mathias",
        "Thiele, Camilla Joyce", "Krüger, Hannelore", "Welling, Jörg", "Häffs, Marvin",
        "Korb, Hendrik", "Bahmer, Larissa", "Langbehn, Marian", "Szrubarski, Sebastian",
        "Patzer, Heinrich-Otto", "Sosin, Tatjana", "Damm, Renate", MUELLER_SOENKSEN_BURKHARDT
    ));

    AFD_BEZIRK.addCandidates(buildCandidates(
        SCHOEMER_DIRK, ZIMMERMANN_ELKE, PILLATZKE_JOERG, "Pohl, Christian",
        "Hanssen, Wolfgang", "Lemke, Martin", "Cremer-Thursby, Marc", "Schultz, Thomas",
        "Ziob, Peter"
    ));

    PIRATEN_BEZIRK.addCandidates(buildCandidates(
        "Dr. Siebert-Schütz, Martin", "Mewes, Christoph", "Brümmer, Amadeus"
    ));
  }

  private void setUpResultsOverall() {
    SPD_BEZIRK.setVotingResult(VotingResult.builder()
        .votesOnNomination(84703)
        .votesThroughHealing(985)
        .votesPerPosition(buildVotesPerPosition(
            8782, 3318, 3825, 3519, 2036, 3923, 5427, 2131, 566, 895, 1425, 1924, 819,
            2590, 2190, 3568, 742, 401, 554, 1133, 1268, 580, 254, 1217, 693, 1359, 481,
            650, 230, 1544, 1080, 1879, 400, 353, 525, 469, 196, 759, 872
        ))
        .build());

    CDU_BEZIRK.setVotingResult(VotingResult.builder()
        .votesOnNomination(66245)
        .votesThroughHealing(660)
        .votesPerPosition(buildVotesPerPosition(
            5966, 1982, 1604, 2414, 1322, 2237, 1868, 1872, 1889, 881, 1253, 2921, 598,
            2721, 618, 941, 620, 837, 378, 594, 484, 584, 472, 408, 748, 650, 282,
            315, 1397, 502
        ))
        .build());

    LINKE_BEZIRK.setVotingResult(VotingResult.builder()
        .votesOnNomination(47452)
        .votesThroughHealing(330)
        .votesPerPosition(buildVotesPerPosition(
            4890, 875, 1532, 1293, 2321, 696, 1843, 2212, 1488, 483, 764, 572, 892
        ))
        .build());

    GRUENE_BEZIRK.setVotingResult(VotingResult.builder()
        .votesOnNomination(150842)
        .votesThroughHealing(900)
        .votesPerPosition(buildVotesPerPosition(
            10586, 5629, 12597, 2935, 3378, 735, 5662, 4021, 2065, 2469, 1561, 3557, 2290,
            1411, 2445, 1761, 1252, 1628, 652, 3524, 677, 843, 2375, 500, 1674, 3264, 1569,
            531, 531, 652, 5358, 2129
        ))
        .build());

    FDP_BEZIRK.setVotingResult(VotingResult.builder()
        .votesOnNomination(24435)
        .votesThroughHealing(240)
        .votesPerPosition(buildVotesPerPosition(
            3528, 1964, 1633, 897, 1193, 355, 401, 1012, 496, 1242, 643, 300, 311,
            998, 505, 2181
        ))
        .build());

    AFD_BEZIRK.setVotingResult(VotingResult.builder()
        .votesOnNomination(21625)
        .votesThroughHealing(220)
        .votesPerPosition(buildVotesPerPosition(
            1226, 1950, 3007, 874, 1032, 780, 535, 221, 283
        ))
        .build());

    PIRATEN_BEZIRK.setVotingResult(VotingResult.builder()
        .votesOnNomination(6715)
        .votesThroughHealing(25)
        .votesPerPosition(buildVotesPerPosition(
            871, 1907, 987
        ))
        .build());
  }

  void setUpConstituencyResults() {
    GRUENE_BEZIRK.getCandidate(1).setElected(Elected.CONSTITUENCY);
    GRUENE_BEZIRK.getCandidate(2).setElected(Elected.CONSTITUENCY);
    GRUENE_BEZIRK.getCandidate(3).setElected(Elected.CONSTITUENCY);
    GRUENE_BEZIRK.getCandidate(5).setElected(Elected.CONSTITUENCY);
    GRUENE_BEZIRK.getCandidate(6).setElected(Elected.CONSTITUENCY);
    GRUENE_BEZIRK.getCandidate(7).setElected(Elected.CONSTITUENCY);
    GRUENE_BEZIRK.getCandidate(11).setElected(Elected.CONSTITUENCY);
    GRUENE_BEZIRK.getCandidate(15).setElected(Elected.CONSTITUENCY);
    GRUENE_BEZIRK.getCandidate(16).setElected(Elected.CONSTITUENCY);
    GRUENE_BEZIRK.getCandidate(20).setElected(Elected.CONSTITUENCY);
    GRUENE_BEZIRK.getCandidate(22).setElected(Elected.CONSTITUENCY);
  }

  private static Nomination buildNomination(String name, boolean supportVotesOnNomination) {
    return new Nomination(name, supportVotesOnNomination);
  }

  private static Collection<Candidate> buildCandidates(String... names) {
    return Arrays.stream(names).map(name -> new Candidate(name, "")).toList();
  }

  @NonNull
  private static Map<Integer, Integer> buildVotesPerPosition(@NonNull int... votes) {
    Map<Integer, Integer> votesPerPosition = new HashMap<>();
    for (int i = 0; i < votes.length; i++) {
      int vote = votes[i];
      votesPerPosition.put(i + 1, vote);
    }
    return votesPerPosition;
  }
}