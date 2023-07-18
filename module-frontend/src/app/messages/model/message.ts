import {MessageType} from "./message-type";

export interface Message {
  text: string;
  type: MessageType;
}
