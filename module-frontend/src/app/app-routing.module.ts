import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {PermissionDeniedComponent} from "./permission-denied/permission-denied.component";

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    children: [
      {
        path: 'elections',
        loadChildren: () => import("./elections/elections.module").then(mod => mod.ElectionsModule)
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
