import {Injectable} from "@angular/core";
import {ElectionService} from "../election.service";
import {
  ActionTypes,
  loadAllElectionsFinishedAction,
  LoadSingleElectionAction,
  loadSingleElectionFinishedAction
} from "./elections.actions";
import {map, switchMap} from "rxjs";
import {Actions, createEffect, ofType} from "@ngrx/effects";

@Injectable()
export class ElectionsEffects {
  constructor(private actions$: Actions,
              private electionService: ElectionService) {
  }

  loadAllElections$ = createEffect(() => this.actions$.pipe(
      ofType(ActionTypes.LoadAllElections),
      switchMap(() => this.electionService.getElections()
          .pipe(
              map(elections => loadAllElectionsFinishedAction({payload: elections}))
          )
      )
  ));

  loadSingleElection$ = createEffect(() => {
    return this.actions$.pipe(
        ofType(ActionTypes.LoadSingleElection),
        switchMap((action: LoadSingleElectionAction) => this.electionService.selectElection(action.payload)
            .pipe(
                map(election =>
                    loadSingleElectionFinishedAction({payload: election}))
            )
        ),
    )
  });
}
