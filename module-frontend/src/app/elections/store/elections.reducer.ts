import {Election} from "../model/election";
import {
  loadAllElectionsFinishedAction,
  loadSingleElectionFinishedAction
} from "./elections.actions";
import {createReducer, on} from "@ngrx/store";

export interface ReducerElectionsState {
  items: Election[];
  selectedItem: Election|undefined;
}

export const initialState: ReducerElectionsState = {
  items: [],
  selectedItem: undefined
};

export const electionsReducer = createReducer(
    initialState,
    on(loadAllElectionsFinishedAction, (state,
                                        action) => ({
      ...state,
      items: [...action.payload]
    })),
    on(loadSingleElectionFinishedAction, (state,
                                          action) => ({
      ...state,
      selectedItem: action.payload
    }))
);
