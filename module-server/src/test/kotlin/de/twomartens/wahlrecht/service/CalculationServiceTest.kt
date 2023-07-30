package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.model.internal.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.time.Month
import kotlin.collections.set

@ExtendWith(MockitoExtension::class)
class CalculationServiceTest {
    private lateinit var service: CalculationService

    @Mock
    private lateinit var nominationService: NominationService

    @Mock
    private lateinit var electionService: ElectionService

    companion object {
        private const val BEZIRKSWAHL = "Bezirkswahl 2019"
        const val GOTTLIEB_GABOR = "Gottlieb, Gabor"
        const val RIEGEL_ANN_KATHRIN = "Riegel, Ann-Kathrin"
        const val HOEFLICH_JUTTA = "Höflich, Jutta"
        const val DR_LANGHEIN_A_W_HEINRICH = "Dr. Langhein, A.W.Heinrich"
        const val PAGELS_MANUELA = "Pagels, Manuela"
        const val WIEGMANN_ROLAND = "Wiegmann, Roland"
        const val KLEINERT_MIKEY = "Kleinert, Mikey"
        const val KUHLMANN_DIETMAR = "Kuhlmann, Dietmar"
        const val MARTENS_JIM = "Martens, Jim"
        const val DORSCH_SEBASTIAN = "Dorsch, Sebastian"
        const val BOHNY_CARL_MARIA = "Bohny, Carl Maria"
        const val KRUEGER_KLAUS = "Krüger, Klaus"
        const val SCHWANKE_BENJAMIN = "Schwanke, Benjamin"
        const val MUELLER_SOENKSEN_BURKHARDT = "Müller-Sönksen, Burkhardt"
        const val SCHOEMER_DIRK = "Schömer, Dirk"
        const val ZIMMERMANN_ELKE = "Zimmermann, Elke"
        const val PILLATZKE_JOERG = "Pillatzke, Jörg"
        const val KLEIN_ROBERT = "Klein, Robert"
        const val ERK_ARAMAK = "Erk, Aramak"
        const val DR_FISCHER_JOST_LEONHARDT = "Dr. Fischer, Jost Leonhardt"
        const val KUELL_GABRIELA = "Küll, Gabriela"
        private val EIMSBUETTEL_NORD = Constituency(BEZIRKSWAHL, 1, "Eimsbüttel-Nord", 3)
        private val EIMSBUETTEL_SUED = Constituency(BEZIRKSWAHL, 2, "Eimsbüttel-Süd", 5)
        private val HARO = Constituency(BEZIRKSWAHL, 3, "Rotherbaum/Harvestehude", 4)
        private val LOKSTEDT = Constituency(BEZIRKSWAHL, 4, "Lokstedt", 3)
        private val NIENDORF = Constituency(BEZIRKSWAHL, 5, "Niendorf", 5)
        private val SCHNELSEN = Constituency(BEZIRKSWAHL, 6, "Schnelsen", 3)
        private val EIDELSTEDT = Constituency(BEZIRKSWAHL, 7, "Eidelstedt", 4)
        private val STELLINGEN = Constituency(BEZIRKSWAHL, 8, "Stellingen", 3)
        private val SPD_NIENDORF: Nomination = buildNomination("SPD", "SPD-Niendorf", false)
        private val SPD_NIENDORF_RESULT: VotingResult = buildVotingResult(
            SPD_NIENDORF, 0, 0,
            11882, 7617, 1946, 1743, 1363, 762, 895, 1598, 1786, 781
        )
        private val CDU_NIENDORF: Nomination = buildNomination("CDU", "CDU-Niendorf", false)
        val CDU_NIENDORF_RESULT: VotingResult = buildVotingResult(
            CDU_NIENDORF, 0, 0, 10733,
            4550, 2310, 2333, 2565
        )
        private val FDP_NIENDORF: Nomination = buildNomination("FDP", "FDP-Niendorf", false)
        val FDP_NIENDORF_RESULT: VotingResult = buildVotingResult(
            FDP_NIENDORF, 0, 0, 2498,
            3200, 992
        )
        private val LINKE_NIENDORF: Nomination =
            buildNomination("LINKE", "LINKE-Niendorf", false)
        val LINKE_NIENDORF_RESULT: VotingResult = buildVotingResult(
            LINKE_NIENDORF, 0, 0,
            3782, 1584, 1185
        )
        private val GRUENE_NIENDORF: Nomination =
            buildNomination("GRUENE", "GRUENE-Niendorf", false)
        val GRUENE_NIENDORF_RESULT: VotingResult = buildVotingResult(
            GRUENE_NIENDORF, 0, 0,
            14838, 12501
        )
        private val AFD_NIENDORF: Nomination = buildNomination("AFD", "AFD-Niendorf", false)
        val AFD_NIENDORF_RESULT: VotingResult = buildVotingResult(AFD_NIENDORF, 0, 0, 6616)
        private val SPD_BEZIRK: Nomination = buildNomination("SPD", "SPD-Bezirk", true)
        val SPD_BEZIRK_RESULT: VotingResult = buildVotingResult(
            SPD_BEZIRK, 84703, 985,
            8782, 3318, 3825, 3519, 2036, 3923, 5427, 2131, 566, 895, 1425, 1924, 819,
            2590, 2190, 3568, 742, 401, 554, 1133, 1268, 580, 254, 1217, 693, 1359, 481,
            650, 230, 1544, 1080, 1879, 400, 353, 525, 469, 196, 759, 872
        )
        private val CDU_BEZIRK: Nomination = buildNomination("CDU", "CDU-Bezirk", true)
        val CDU_BEZIRK_RESULT: VotingResult = buildVotingResult(
            CDU_BEZIRK, 66245, 660,
            5966, 1982, 1604, 2414, 1322, 2237, 1868, 1872, 1889, 881, 1253, 2921, 598,
            2721, 618, 941, 620, 837, 378, 594, 484, 584, 472, 408, 748, 650, 282,
            315, 1397, 502
        )
        private val FDP_BEZIRK: Nomination = buildNomination("FDP", "FDP-Bezirk", true)
        val FDP_BEZIRK_RESULT: VotingResult = buildVotingResult(
            FDP_BEZIRK, 24435, 240,
            3528, 1964, 1633, 897, 1193, 355, 401, 1012, 496, 1242, 643, 300, 311,
            998, 505, 2181
        )
        private val LINKE_BEZIRK: Nomination = buildNomination("LINKE", "LINKE-Bezirk", true)
        val LINKE_BEZIRK_RESULT: VotingResult = buildVotingResult(
            LINKE_BEZIRK, 47452, 330,
            4890, 875, 1532, 1293, 2321, 696, 1843, 2212, 1488, 483, 764, 572, 892
        )
        private val GRUENE_BEZIRK: Nomination = buildNomination("GRUENE", "GRUENE-Bezirk", true)
        val GRUENE_BEZIRK_RESULT: VotingResult = buildVotingResult(
            GRUENE_BEZIRK, 150842,
            900,
            10586, 5629, 12597, 2935, 3378, 735, 5662, 4021, 2065, 2469, 1561, 3557, 2290,
            1411, 2445, 1761, 1252, 1628, 652, 3524, 677, 843, 2375, 500, 1674, 3264, 1569,
            531, 531, 652, 5358, 2129
        )
        private val AFD_BEZIRK: Nomination = buildNomination("AFD", "AFD-Bezirk", true)
        val AFD_BEZIRK_RESULT: VotingResult = buildVotingResult(
            AFD_BEZIRK, 21625, 220,
            1226, 1950, 3007, 874, 1032, 780, 535, 221, 283
        )
        private val PIRATEN_BEZIRK: Nomination =
            buildNomination("PIRATEN", "Piraten-Bezirk", true)
        val PIRATEN_BEZIRK_RESULT: VotingResult = buildVotingResult(
            PIRATEN_BEZIRK, 6715,
            25,
            871, 1907, 987
        )

        private fun setUpCandidatesConstituency() {
            SPD_NIENDORF.addCandidates(Candidate("Schwarzarius, Ines", ""))
            SPD_NIENDORF.addCandidates(Candidate("Urbanski, Torge", ""))
            SPD_NIENDORF.addCandidates(Candidate("Fischbach-Pyttel, Carola", ""))
            SPD_NIENDORF.addCandidates(Candidate("Bäcker, Guido", ""))
            SPD_NIENDORF.addCandidates(Candidate("Porr, Ulrike", ""))
            SPD_NIENDORF.addCandidates(Candidate("Petersen, Johannes", ""))
            SPD_NIENDORF.addCandidates(Candidate("Kröger, Manuela-Andrea", ""))
            SPD_NIENDORF.addCandidates(Candidate("Steppat, Wolfgang", ""))
            SPD_NIENDORF.addCandidates(Candidate("Canbay, Berfin", ""))
            SPD_NIENDORF.addCandidates(Candidate("Schumacher, Wolfgang", ""))
            CDU_NIENDORF.addCandidates(Candidate("Kuhn, Rüdiger", ""))
            CDU_NIENDORF.addCandidates(Candidate("Ahrens, Mareike", ""))
            CDU_NIENDORF.addCandidates(Candidate("Wysocki, Michael", ""))
            CDU_NIENDORF.addCandidates(Candidate("Dönselmann, Malte", ""))
            CDU_NIENDORF.addCandidates(Candidate("Seehaber, Daniel", ""))
            LINKE_NIENDORF.addCandidates(Candidate("Dr. Khenkhar, Malik", ""))
            LINKE_NIENDORF.addCandidates(Candidate("Radom, Claudia", ""))
            LINKE_NIENDORF.addCandidates(Candidate("Kara, Başak", ""))
            FDP_NIENDORF.addCandidates(Candidate("Häffs, Marvin", ""))
            FDP_NIENDORF.addCandidates(Candidate("Bahmer, Larissa", ""))
            FDP_NIENDORF.addCandidates(Candidate("Patzer, Heinrich-Otto", ""))
            GRUENE_NIENDORF.addCandidates(Candidate("Demirhan, Sina", ""))
            GRUENE_NIENDORF.addCandidates(Candidate("Schmidt, Lutz", "Redakteur"))
            AFD_NIENDORF.addCandidates(Candidate("Hanssen, Wolfgang", ""))
        }

        private fun buildVotingResult(
            nomination: Nomination, votesOnNomination: Int,
            votesThroughHealing: Int,
            vararg votes: Int
        ): VotingResult {
            return VotingResult(
                nominationId = nomination.id,
                votesOnNomination = votesOnNomination,
                votesThroughHealing = votesThroughHealing,
                votesPerPosition = buildVotesPerPosition(*votes)
            )
        }

        private fun setUpCandidatesOverall() {
            SPD_BEZIRK.addCandidates(
                buildCandidates(
                    GOTTLIEB_GABOR, "Meding, Sarah", "Harringa, Nils", "Nendza-Ammar, Charlotte",
                    "Rust, Rüdiger", "Köster-Marjanovic, Hannelore", RIEGEL_ANN_KATHRIN, "Rüter, Monika",
                    "Heise, Elk", "Fischbach-Pyttel, Carola", "Schirmer, Stephan-Philipp",
                    "Telljohann, Katharina",
                    "Röder, Wilfried", "Dr. Matiss, Claudia", "Koßel, Uwe", "Wagner, Saskia", "Bäcker, Guido",
                    "Tiben-Thörner, Karin", "Ptach, Stephan", "Steinbiß, Emily", "Berisha, Dafina",
                    "Meyer, Simon", "Porr, Ulrike", "Lange, Frederik", "Oliva, Nora", "Drossinakis, Panagiotis",
                    "Jacobs, Ruth", "Bernstein, Til", "Kröger, Manuela-Andrea", "Dr. Enzmann, Dirk",
                    "Schilling, Alexandra", "Abboud, Hussein", "Petersen, Britt", "Petersen, Johannes",
                    "Petersen, Doris", "Frese, Gabor", "Suh-von Bremen, Sun Won", "Steppat, Wolfgang",
                    "Hildebrand, Denise"
                )
            )
            CDU_BEZIRK.addCandidates(
                buildCandidates(
                    HOEFLICH_JUTTA, "Könecke, Christian", "Birnbaum, Andreas", "Möller-Fiedler, Sybille",
                    "Kochmann, Franziska", "von Deutsch, Christiane", "Hoffmann, Bernd", "Holst, Christian",
                    "Lau, Roman", "Hille, Robert", "Ahrens, Mareike", DR_LANGHEIN_A_W_HEINRICH,
                    "Wichmann, Norbert", "Lau, Johanna", "Czernotzky, Birgit", "von Sawilski, Marlies",
                    "Dönselmann, Malte",
                    "Jarzembowski, Daniel", "Howe, Sönke", "Recknagel, Dennis", "Dieball, Laura",
                    "Josefowsky, Ernst Günther", "Wysocki, Michael", "Thomsen, Thomas", "Greshake, Sascha",
                    "Thiesen, Marianne",
                    "Schüller, Michael", "Jacobs, Jürgen", "Weiler, Johannes", "Howe, Raphaela"
                )
            )
            LINKE_BEZIRK.addCandidates(
                buildCandidates(
                    PAGELS_MANUELA, WIEGMANN_ROLAND, "Peters, Ralf", "Hoyer, Jonas",
                    KLEINERT_MIKEY, "Schulte, Kolja", "Huwald, Kristin", "Dr. Ritter, Sabine",
                    "Pirling, David", "Reipschläger, Bernhard", "Arndt, Thomas", "Artus, Holger",
                    "WLaab, Helene"
                )
            )
            GRUENE_BEZIRK.addCandidates(
                buildCandidates(
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
                )
            )
            FDP_BEZIRK.addCandidates(
                buildCandidates(
                    KRUEGER_KLAUS, SCHWANKE_BENJAMIN, "Cleven, Martina", "Zielinski, Mathias",
                    "Thiele, Camilla Joyce", "Krüger, Hannelore", "Welling, Jörg", "Häffs, Marvin",
                    "Korb, Hendrik", "Bahmer, Larissa", "Langbehn, Marian", "Szrubarski, Sebastian",
                    "Patzer, Heinrich-Otto", "Sosin, Tatjana", "Damm, Renate", MUELLER_SOENKSEN_BURKHARDT
                )
            )
            AFD_BEZIRK.addCandidates(
                buildCandidates(
                    SCHOEMER_DIRK, ZIMMERMANN_ELKE, PILLATZKE_JOERG, "Pohl, Christian",
                    "Hanssen, Wolfgang", "Lemke, Martin", "Cremer-Thursby, Marc", "Schultz, Thomas",
                    "Ziob, Peter"
                )
            )
            PIRATEN_BEZIRK.addCandidates(
                buildCandidates(
                    "Dr. Siebert-Schütz, Martin", "Mewes, Christoph", "Brümmer, Amadeus"
                )
            )
        }

        @JvmStatic
        @BeforeAll
        fun setUp() {
            setUpCandidatesConstituency()
            setUpCandidatesOverall()
        }

        private fun buildNomination(
            partyAbbreviation: String,
            name: String, supportVotesOnNomination: Boolean
        ): Nomination {
            return Nomination(
                NominationId(BEZIRKSWAHL, partyAbbreviation, name),
                supportVotesOnNomination
            )
        }

        private fun buildCandidates(vararg names: String): Collection<Candidate> {
            return names
                .map { Candidate(it, "") }
                .toList()
        }

        private fun buildVotesPerPosition(vararg votes: Int): Map<Int, Int> {
            val result: MutableMap<Int, Int> = mutableMapOf()
            for (index in 1..votes.size) {
                result[index] = votes[index - 1]
            }
            return result.toMap()
        }
    }

    @BeforeEach
    fun setService() {
        service = CalculationService(nominationService, electionService)
    }

    @Test
    fun shouldCalculateConstituencyCorrectly() {
        val votingResults: Collection<VotingResult> = listOf(
            SPD_NIENDORF_RESULT,
            CDU_NIENDORF_RESULT,
            LINKE_NIENDORF_RESULT,
            GRUENE_NIENDORF_RESULT,
            FDP_NIENDORF_RESULT,
            AFD_NIENDORF_RESULT
        )
        Mockito.`when`(nominationService.getNominationInternal(SPD_NIENDORF.id)).thenReturn(SPD_NIENDORF)
        Mockito.`when`(nominationService.getNominationInternal(CDU_NIENDORF.id)).thenReturn(CDU_NIENDORF)
        Mockito.`when`(nominationService.getNominationInternal(GRUENE_NIENDORF.id))
            .thenReturn(GRUENE_NIENDORF)
        val elected: ElectedResult = service.calculateConstituency(NIENDORF, votingResults)
        Assertions.assertThat(elected.electedCandidatesByNomination)
            .containsKey(SPD_NIENDORF.id)
            .containsKey(CDU_NIENDORF.id)
            .containsKey(GRUENE_NIENDORF.id)
            .doesNotContainKey(LINKE_NIENDORF.id)
            .doesNotContainKey(AFD_NIENDORF.id)
            .doesNotContainKey(FDP_NIENDORF.id)
    }

    @Test
    fun shouldCalculateOverallSeatsCorrectly() {
        val constituencies: Collection<Constituency> = listOf(
            EIMSBUETTEL_NORD, EIMSBUETTEL_SUED, HARO,
            LOKSTEDT, NIENDORF, SCHNELSEN, EIDELSTEDT, STELLINGEN
        )
        val election = Election(
            "Bezirkswahl 2019",
            LocalDate.of(2019, Month.MAY, 26), VotingThreshold.THREE,
            51, constituencies
        )
        val votingResults: Collection<VotingResult> = listOf(
            SPD_BEZIRK_RESULT,
            CDU_BEZIRK_RESULT,
            LINKE_BEZIRK_RESULT,
            GRUENE_BEZIRK_RESULT,
            FDP_BEZIRK_RESULT,
            AFD_BEZIRK_RESULT,
            PIRATEN_BEZIRK_RESULT
        )
        val result: SeatResult = service.calculateOverallSeatDistribution(election, votingResults)
        Assertions.assertThat(result.seatsPerNomination)
            .containsEntry(SPD_BEZIRK.id, 12)
            .containsEntry(CDU_BEZIRK.id, 9)
            .containsEntry(LINKE_BEZIRK.id, 5)
            .containsEntry(FDP_BEZIRK.id, 3)
            .containsEntry(GRUENE_BEZIRK.id, 19)
            .containsEntry(AFD_BEZIRK.id, 3)
    }

    @Test
    fun shouldCalculateOverallCandidatesCorrectly() {
        val seatsPerVotingResult = mapOf(
            Pair(SPD_BEZIRK_RESULT, 2),
            Pair(CDU_BEZIRK_RESULT, 2),
            Pair(LINKE_BEZIRK_RESULT, 3),
            Pair(GRUENE_BEZIRK_RESULT, 8),
            Pair(FDP_BEZIRK_RESULT, 3),
            Pair(AFD_BEZIRK_RESULT, 3)
        )
        val electedCandidates = setUpConstituencyResults()
        Mockito.`when`(nominationService.getNominationInternal(SPD_BEZIRK.id)).thenReturn(SPD_BEZIRK)
        Mockito.`when`(nominationService.getNominationInternal(CDU_BEZIRK.id)).thenReturn(CDU_BEZIRK)
        Mockito.`when`(nominationService.getNominationInternal(FDP_BEZIRK.id)).thenReturn(FDP_BEZIRK)
        Mockito.`when`(nominationService.getNominationInternal(GRUENE_BEZIRK.id)).thenReturn(GRUENE_BEZIRK)
        Mockito.`when`(nominationService.getNominationInternal(LINKE_BEZIRK.id)).thenReturn(LINKE_BEZIRK)
        Mockito.`when`(nominationService.getNominationInternal(AFD_BEZIRK.id)).thenReturn(AFD_BEZIRK)
        val result: ElectedResult = service.calculateElectedOverallCandidates(
            seatsPerVotingResult,
            electedCandidates
        )
        Assertions.assertThat(result.electedCandidatesByNomination)
            .anySatisfy { id, candidates ->
                Assertions.assertThat(id).isEqualTo(SPD_BEZIRK.id)
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == GOTTLIEB_GABOR
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == RIEGEL_ANN_KATHRIN
                                && candidate.elected == Elected.OVERALL_VOTE_ORDER)
                    }
            }
            .anySatisfy { id, candidates ->
                Assertions.assertThat(id)
                    .isEqualTo(CDU_BEZIRK.id)
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == HOEFLICH_JUTTA
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == DR_LANGHEIN_A_W_HEINRICH
                                && candidate.elected == Elected.OVERALL_VOTE_ORDER)
                    }
            }
            .anySatisfy { id, candidates ->
                Assertions.assertThat(id)
                    .isEqualTo(LINKE_BEZIRK.id)
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == PAGELS_MANUELA
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == WIEGMANN_ROLAND
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == KLEINERT_MIKEY
                                && candidate.elected == Elected.OVERALL_VOTE_ORDER)
                    }
            }
            .anySatisfy { id, candidates ->
                Assertions.assertThat(id)
                    .isEqualTo(FDP_BEZIRK.id)
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == KRUEGER_KLAUS
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == SCHWANKE_BENJAMIN
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == MUELLER_SOENKSEN_BURKHARDT
                                && candidate.elected == Elected.OVERALL_VOTE_ORDER)
                    }
            }
            .anySatisfy { id, candidates ->
                Assertions.assertThat(id)
                    .isEqualTo(AFD_BEZIRK.id)
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == SCHOEMER_DIRK
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == ZIMMERMANN_ELKE
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == PILLATZKE_JOERG
                                && candidate.elected == Elected.OVERALL_VOTE_ORDER)
                    }
            }
            .anySatisfy { id, candidates ->
                Assertions.assertThat(id)
                    .isEqualTo(GRUENE_BEZIRK.id)
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == KUHLMANN_DIETMAR
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == KLEIN_ROBERT
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == ERK_ARAMAK
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == MARTENS_JIM
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == DR_FISCHER_JOST_LEONHARDT
                                && candidate.elected == Elected.OVERALL_NOMINATION_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == KUELL_GABRIELA
                                && candidate.elected == Elected.OVERALL_VOTE_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == BOHNY_CARL_MARIA
                                && candidate.elected == Elected.OVERALL_VOTE_ORDER)
                    }
                Assertions.assertThat(candidates)
                    .anyMatch { candidate ->
                        (candidate.name == DORSCH_SEBASTIAN
                                && candidate.elected == Elected.OVERALL_VOTE_ORDER)
                    }
            }
    }

    private fun setUpConstituencyResults(): Map<String, Collection<ElectedCandidate>> {
        return mapOf(
            Pair(
                GRUENE_BEZIRK_RESULT.nominationId.partyAbbreviation, listOf(
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(1), Elected.CONSTITUENCY),
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(2), Elected.CONSTITUENCY),
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(3), Elected.CONSTITUENCY),
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(5), Elected.CONSTITUENCY),
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(6), Elected.CONSTITUENCY),
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(7), Elected.CONSTITUENCY),
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(11), Elected.CONSTITUENCY),
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(15), Elected.CONSTITUENCY),
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(16), Elected.CONSTITUENCY),
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(20), Elected.CONSTITUENCY),
                    ElectedCandidate(GRUENE_BEZIRK.getCandidate(22), Elected.CONSTITUENCY)
                )
            )
        )
    }
}