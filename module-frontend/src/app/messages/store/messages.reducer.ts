import {Message} from "../model/message";
import {createReducer, on} from "@ngrx/store";
import {addMessageAction, addMessageFinishedAction} from "./messages.actions";

export interface ReducerMessagesState {
  displayedMessage: Message|undefined;
}

const initialState: ReducerMessagesState = {
  displayedMessage: undefined
}

export const messagesReducer = createReducer(
    initialState,
    on(addMessageAction, (state,
                                        action) => ({
      ...state,
      displayedMessage: action.message
    })),
    on(addMessageFinishedAction, (state) => ({
      ...state,
      displayedMessage: undefined
    }))
);
