import {messagesReducer, ReducerMessagesState} from "./messages.reducer";
import {ActionReducerMap, createFeatureSelector} from "@ngrx/store";

export const featureStateName = 'messagesFeature';

export interface MessagesState {
  messages: ReducerMessagesState;
}

export const messagesReducers: ActionReducerMap<MessagesState> = {
  messages: messagesReducer,
};

export const getMessagesFeatureState = createFeatureSelector<MessagesState>(
    featureStateName
);
