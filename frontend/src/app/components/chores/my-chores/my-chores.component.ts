import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {ChoreService} from "../../../services/chore.service";
import {ToastrService} from "ngx-toastr";
import {ChoresDto} from "../../../dtos/chores";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ChoreConfirmationModalComponent} from "./chore-confirmation-modal/chore-confirmation-modal.component";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";


@Component({
  selector: 'app-my-chores',
  templateUrl: './my-chores.component.html',
  styleUrls: ['./my-chores.component.scss']
})
export class MyChoresComponent {
  chores: ChoresDto[] = [];
  completedChores: ChoresDto[] = [];
  private searchParams: string;
  message: string;

  constructor(private router: Router,
              private choreService: ChoreService,
              private notification: ToastrService,
              private modalService: NgbModal,
              private errorHandler: ErrorHandlerService) {
  }

  showConfirmationModal() {
    const modalRef = this.modalService.open(ChoreConfirmationModalComponent);
    modalRef.componentInstance.choreName = 'Chore Name'; // Pass the chore name or any other data

    modalRef.result.then((result) => {
      if (result) {
        for (let i = 0; i < this.completedChores.length; i++) {
          this.choreService.repeatChore(this.completedChores[i], result.date).subscribe({
            next: (repetedChore) => {
              console.log('This is the repeated chore: ', repetedChore)
              this.router.navigate(['chores', 'all']);
              this.notification.success("Chores completed and points awarded.", "Success");
              this.notification.success("Chores are repeated.", "Success");
            },
            error: (error) => {
              this.errorHandler.handleErrors(error, "chore", 'delete');
            }
          });
        }
      } else {
        this.deleteCompletedChores();
      }
    });
  }

  ngOnInit() {
    this.choreService.getChoresByUser(this.searchParams).subscribe({
      next: res => {
        if (res.length == 0) {
          this.message = 'Good Job! You have completed all of your chores.'
        } else {
          this.chores = res.sort((a: ChoresDto, b: ChoresDto) => {
            return new Date(a.endDate).getTime() - new Date(b.endDate).getTime();
          });
        }
      },
      error: error => {
        this.notification.error("Failed to load chores", 'Error')
      }
    });
  }

  updateChoreComplete(chore: ChoresDto) {
    for (let i = 0; i < this.chores.length; i++) {
      if (this.chores[i] == chore) {
        this.chores[i].completed = !this.chores[i].completed;
        break;
      }
    }
    this.completedChores = this.chores.filter(chore => chore.completed);
  }

  completedChoresIsEmpty() {
    return this.completedChores.length == 0;
  }

  deleteCompletedChores() {
    return this.choreService.deleteChores(this.completedChores).subscribe({
      next: res => {

        for (let i = 0; i < res.length; i++) {
          this.chores = this.chores.filter(chore => chore.id !== res[i].id);
        }

        this.awardPoints();
        this.completedChores = [];
        if (this.chores.length === 0) {
          this.message = 'Good Job! You have completed all of your chores.';
        }
        this.notification.success("Chores completed and points awarded.", "Success");
      },
      error: error => {
        this.errorHandler.handleErrors(error, "chore", 'delete');
      }
    });
  }

  awardPoints() {
    let points = this.completedChores[0].user.points;
    for (let i = 0; i < this.completedChores.length; i++) {
      let curr = this.completedChores[i];
      points += parseInt(curr.points);
    }

    this.choreService.updatePoints(points, this.completedChores[0].user.id).subscribe({
      next: () => {
      },
      error: err => {
        this.notification.error("Points could not be awarded", 'Error');
      }
    });
  }

  navigateToAllChores() {
    this.router.navigate(['/chores/all']);
  }

  navigateToPreference() {
    this.router.navigate(['/chores/preference']);
  }

  navigateToLeaderboard() {
    this.router.navigate(['/chores/leaderboard']);
  }
}
