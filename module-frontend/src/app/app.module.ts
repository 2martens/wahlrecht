import {APP_INITIALIZER, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {DashboardComponent} from './dashboard/dashboard.component';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatIconModule} from "@angular/material/icon";
import {MatListModule} from "@angular/material/list";
import {KeycloakAngularModule, KeycloakService} from "keycloak-angular";
import {ElectionsComponent} from './elections/elections.component';
import {PermissionDeniedComponent} from './permission-denied/permission-denied.component';
import {MessagesComponent} from './messages/messages.component';
import {HttpClientModule} from "@angular/common/http";
import {environment} from "../environments/environment";
import {NavigationComponent} from './navigation/navigation.component';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatMenuModule} from "@angular/material/menu";
import {MatTableModule} from "@angular/material/table";

function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: environment.keycloakURL,
        realm: environment.realm,
        clientId: environment.clientId,
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html',
        flow: "standard"
      },
      shouldAddToken: (request) => {
        const { method, url } = request;
        return url.startsWith(environment.backendURL);
      },
      loadUserProfileAtStartUp: true
    });
}

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    ElectionsComponent,
    PermissionDeniedComponent,
    MessagesComponent,
    NavigationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatButtonModule,
    MatGridListModule,
    MatIconModule,
    MatToolbarModule,
    MatListModule,
    KeycloakAngularModule,
    HttpClientModule,
    MatSidenavModule,
    MatMenuModule,
    MatTableModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService],
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
