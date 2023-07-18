import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Election} from "./model/election";
import {catchError, Observable, of} from "rxjs";
import {Store} from "@ngrx/store";
import {electionByName} from "./store";
import {MessagesService} from "../messages/messages.service";
import {MessageType} from "../messages/model/message-type";

@Injectable({
  providedIn: 'root'
})
export class ElectionService {

  private electionsURL = environment.backendURL + '/elections';

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient,
              private store: Store,
              private messageService: MessagesService) { }

  getElections(): Observable<Election[]> {
    return this.http.get<Election[]>(this.electionsURL, this.httpOptions)
      .pipe(
        catchError(this.handleError<Election[]>('getElections', []))
      );
  }

  selectElection(name: string) {
    return this.store.select(electionByName(name));
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      if (error.status == 401) {
        this.log(MessageType.UNAUTHENTICATED);
      }
      if (error.status == 403) {
        this.log(MessageType.UNAUTHORIZED);
      }
      if (error.status == 500) {
        this.log(MessageType.INTERNAL_SERVER_ERROR);
      }

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  private log(type: MessageType) {
    this.messageService.logMessage('ElectionService', type);
  }
}
