import {Component} from '@angular/core';
import {KeycloakService} from "keycloak-angular";
import {from} from "rxjs";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent {
  loggedUserName = '';
  isLoggedIn$ = from(this.keycloakService.isLoggedIn());
  url: string;

  constructor(private keycloakService: KeycloakService,
              private route: ActivatedRoute) {
    this.url = route.snapshot.url.join('');
  }

  ngOnInit(): void {
    this.loggedUserName = this.keycloakService.getUsername();
  }

  login(): void {
    this.keycloakService.login({
      redirectUri: window.location.origin + this.url,
    });
  }

  logout(): void {
    this.keycloakService.logout(window.location.origin);
  }
}
