import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Election} from "./election";
import {catchError, Observable, of, tap} from "rxjs";
import {MessageService} from "../messages/message.service";

@Injectable({
  providedIn: 'root'
})
export class ElectionService {

  private electionsURL = environment.backendURL + '/elections';

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient,
              private messageService: MessageService) { }

  getElections(): Observable<Election[]> {
    return this.http.get<Election[]>(this.electionsURL)
      .pipe(
        tap(_ => this.log('fetched elections')),
        catchError(this.handleError<Election[]>('getElections', []))
      );
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

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /** Log a HeroService message with the MessageService */
  private log(message: string) {
    this.messageService.add(`ElectionService: ${message}`);
  }
}
