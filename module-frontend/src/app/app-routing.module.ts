import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {ElectionsComponent} from "./elections/elections.component";
import {AppAuthGuard} from "./auth/auth.guard";
import {PermissionDeniedComponent} from "./permission-denied/permission-denied.component";

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    canActivate: [AppAuthGuard],
    children: [
      {
        path: 'elections',
        component: ElectionsComponent,
        data: {
          roles: ['USER']
        },
        canActivate: [AppAuthGuard]
      },
      {
        path: 'permission-denied',
        component: PermissionDeniedComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
