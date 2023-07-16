import {VotingThreshold} from "./voting-threshold";
import {Constituency} from "./constituency";

export interface Election {
  name: string;
  day: string;
  totalNumberOfSeats: number;
  votingThreshold: VotingThreshold;
  constituencies: Constituency[];
}
