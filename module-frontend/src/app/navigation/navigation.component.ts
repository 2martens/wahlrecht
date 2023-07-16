import {Component} from '@angular/core';
import {KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent {
  loggedUserName = '';

  constructor(private keycloakService: KeycloakService) { }

  ngOnInit(): void {
    this.loggedUserName = this.keycloakService.getUsername();
  }

  logout(): void {
    this.keycloakService.logout(window.location.origin);
  }
}
