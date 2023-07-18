import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {StoreModule} from "@ngrx/store";
import {featureStateName, messagesReducers} from "./store";
import {EffectsModule} from "@ngrx/effects";
import {MessagesEffects} from "./store/messages.effects";
import {MatSnackBarModule} from "@angular/material/snack-bar";


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MatSnackBarModule,
    StoreModule.forFeature(featureStateName, messagesReducers),
    EffectsModule.forFeature([MessagesEffects])
  ]
})
export class MessagesModule {
}
