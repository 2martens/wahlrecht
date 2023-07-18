import {createAction, props} from "@ngrx/store";
import {Message} from "../model/message";

export enum ActionTypes {
  AddMessage = '[Messages] Add Message',
  AddMessageFinished = '[Messages] Add Message Finished',
}

export const addMessageAction = createAction(
    ActionTypes.AddMessage,
    props<{message: Message}>()
);

export type AddMessageAction = ReturnType<typeof addMessageAction>;

export const addMessageFinishedAction = createAction(
    ActionTypes.AddMessageFinished
);

export type AddMessageFinishedAction = ReturnType<typeof addMessageFinishedAction>;

export type MessagesActions =
    | AddMessageAction
    | AddMessageFinishedAction;
