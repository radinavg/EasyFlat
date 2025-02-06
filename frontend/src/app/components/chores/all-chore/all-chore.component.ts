import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {ChoresDto, ChoreSearchDto} from "../../../dtos/chores";
import {ToastrService} from "ngx-toastr";
import {ChoreService} from "../../../services/chore.service";
import {HttpResponse} from "@angular/common/http";
import {tap} from "rxjs/operators";
import {UserDetail} from "../../../dtos/auth-request";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

@Component({
  selector: 'app-all-chore',
  templateUrl: './all-chore.component.html',
  styleUrls: ['./all-chore.component.scss']
})
export class AllChoreComponent {
  chores: ChoresDto[];
  unassigned: ChoresDto[] = [];
  assigned: ChoresDto[] = [];
  searchParams: ChoreSearchDto = {
    userName: '',
    endDate: null,
  };

  constructor(private router: Router,
              private choreService: ChoreService,
              private notification: ToastrService,
              private errorHandler: ErrorHandlerService
  ) {
  }

  navigateToNewChore() {
    this.router.navigate(['/chores', 'add']);
  }

  ngOnInit() {
    this.loadChores();
  }

  loadChores() {
    this.choreService.getChores(this.searchParams).subscribe({
      next: res => {
        this.chores = res.sort((a: ChoresDto, b: ChoresDto) => {
          return new Date(a.endDate).getTime() - new Date(b.endDate).getTime();
        });
        this.assigned = this.chores.filter(chore => chore.user !== null);
        this.unassigned = this.chores.filter(chore => chore.user === null);
      },
      error: error => {
        this.notification.error("Error loading chores", "Error");
      }
    });
  }

  assignChores() {
    this.choreService.assginChores().subscribe({
      next: res => {
        this.chores = res;
        this.notification.success("Successfully assigned chores")
        this.loadChores();
      },
      error: error => {
        this.notification.error("Created Chores are already assigned ")
      }
    });
  }

  exportPDF() {
    this.choreService.generateChoreListPDF().subscribe((response: HttpResponse<Blob>) => {
      const fileName = 'chores.pdf';

      const blob = new Blob([response.body], {type: 'application/pdf'});
      const downloadLink = document.createElement('a');
      downloadLink.href = window.URL.createObjectURL(blob);
      downloadLink.download = fileName;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    });
  }

  navigateToMyChores() {
    this.router.navigate(['/chores/my']);
  }

  navigateToPreference() {
    this.router.navigate(['/chores/preference']);
  }

  navigateToLeaderboard() {
    this.router.navigate(['/chores/leaderboard']);
  }
}
