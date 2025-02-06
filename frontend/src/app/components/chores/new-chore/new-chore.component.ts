import {Component} from '@angular/core';
import {ChoresDto} from "../../../dtos/chores";
import {NgForm} from "@angular/forms";
import {ChoreService} from "../../../services/chore.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

@Component({
  selector: 'app-new-chore',
  templateUrl: './new-chore.component.html',
  styleUrls: ['./new-chore.component.scss']
})
export class NewChoreComponent {
  chore: ChoresDto = {
    id: null,
    name: '',
    description: null,
    endDate: null,
    points: null,
    user: null,
    sharedFlat: null,
    completed: false
  };
  userInput: string = null;
  constructor(
    private choreService: ChoreService,
    private router: Router,
    private notification: ToastrService,
    private errorHandler: ErrorHandlerService
  ) {
  }

  onSubmit() {
    if (this.userInput === null) {
      this.chore.points = '0';
    } else if (this.userInput === '') {
      this.chore.points = 'a';
      this.userInput = null;
    }
    console.log(this.chore)
    this.choreService.createChore(this.chore).subscribe({
      next: data => {
        this.chore = data;
        this.notification.success(`Chore ${this.chore.name} successfully created.`, "Success");
        this.router.navigate(['/chores', 'all']);
      },
      error: error => {
        this.errorHandler.handleErrors(error, "chore", 'create');
      }
    });
  }

  onPointsInputChange(event: any): void {
    this.userInput = event.target.value;
  }
}
