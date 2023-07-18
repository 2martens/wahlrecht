import {ElectionsState} from "../elections/store";
import {MessagesState} from "../messages/store";

export interface AppState {
  electionsFeature: ElectionsState;
  messagesFeature: MessagesState;
}
