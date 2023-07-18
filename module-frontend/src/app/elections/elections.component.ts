import {Component, OnInit} from '@angular/core';
import {KeycloakService} from "keycloak-angular";
import {Election} from "./model/election";
import {Observable} from "rxjs";
import {Store} from "@ngrx/store";
import {allElections} from "./store";
import {loadAllElectionsAction} from "./store/elections.actions";

@Component({
  selector: 'app-elections',
  templateUrl: './elections.component.html',
  styleUrls: ['./elections.component.scss']
})
export class ElectionsComponent implements OnInit{
  $elections: Observable<Election[]> = this.store.select<Election[]>(allElections());

  constructor(private keycloakService: KeycloakService,
              private store: Store) {
  }

  ngOnInit() {
    this.store.dispatch(loadAllElectionsAction());
  }
}
