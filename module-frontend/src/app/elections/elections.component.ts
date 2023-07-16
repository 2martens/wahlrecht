import {Component, OnInit} from '@angular/core';
import {KeycloakService} from "keycloak-angular";
import {Election} from "./election";
import {ElectionService} from "./election.service";
import {Observable} from "rxjs";

@Component({
  selector: 'app-elections',
  templateUrl: './elections.component.html',
  styleUrls: ['./elections.component.scss']
})
export class ElectionsComponent implements OnInit{
  $elections: Observable<Election[]> = this.electionService.getElections();

  constructor(private keycloakService: KeycloakService,
              private electionService: ElectionService) {
  }

  ngOnInit() {
  }
}
