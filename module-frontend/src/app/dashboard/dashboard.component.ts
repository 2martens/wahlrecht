import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {ElectionService} from "../elections/election.service";
import {Election} from "../elections/election";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  electionColumns = ['name', 'day', 'totalNumberOfSeats', 'votingThreshold'];
  dataSource = new MatTableDataSource<Election>();
  private subscription: Subscription|null = null;

  constructor(private electionService: ElectionService) {
  }

  ngOnInit() {
    this.subscription = this.electionService.getElections().subscribe(
      (elections) => {
        this.dataSource.data = elections;
      }
    );
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }
}
