# Wahlrecht (Electoral Law)

This application allows the storage of core election data (elections, parties, nominations, 
candidates, election results) and the calculation of election results. This can be used to
quickly get the list of elected candidates from each party based on a final, incomplete
or fictional election result.

## Motivation

The electoral law for the district and city elections in Hamburg is rather complex on the side
of counting the results. This application takes away the complex part of determining the
elected candidates based on the vote results. This allows, for example, playing around with
what-if scenarios: What if one party gets 100 more votes in one constituency, how would that
change the set of elected candidates?

Another motivation: the official list of vote results per candidate is only available in a PDF
and not in machine-readable format. All vote results for each candidate were extracted manually
from that PDF and are now available in easy-to-consume JSON format.

## Correctness

The correctness of the calculation was tested with the final election results from the 2019
district election. When calculating, the application returns the utilized election numbers which
can be used to compare with the official calculation as documented by the Statistikamt Nord.
Except rounding differences a couple positions after the point, the numbers are equal.

Furthermore, unit tests check those results automatically.

## Data source

The source for the data that will be available on the hosted service is the Statistikamt Nord.
All information can be obtained from there (in German): https://www.statistik-nord.de/wahlen/wahlen-in-hamburg/bezirksversammlungswahlen/2019#c7624