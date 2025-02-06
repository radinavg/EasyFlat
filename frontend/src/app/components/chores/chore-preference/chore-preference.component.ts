import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {ChoresDto, ChoreSearchDto} from "../../../dtos/chores";
import {Observable} from "rxjs";
import {ShoppingItemDto} from "../../../dtos/item";
import {ItemCreateEditMode} from "../../digital-storage/item-create-edit/item-create-edit.component";
import {ChoreService} from "../../../services/chore.service";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {UnitService} from "../../../services/unit.service";
import {Preference} from "../../../dtos/preference";
import {PreferenceService} from "../../../services/preference.service";
import {PreferenceStorageService} from "../../../services/preference-storage-service";
import {SharedFlat} from "../../../dtos/sharedFlat";
import {ErrorHandlerService} from "../../../services/util/error-handler.service";

@Component({
  selector: 'app-chore-preference',
  templateUrl: './chore-preference.component.html',
  styleUrls: ['./chore-preference.component.scss']
})
export class ChorePreferenceComponent implements OnInit {
  preference: Preference = {
    id: null,
    first: null,
    second: null,
    third: null,
    fourth: null
  };
  chores: ChoresDto[] = [];
  filteredChores: any[][] = [];

  oldPreference: Preference = {
    id: null,
    first: null,
    second: null,
    third: null,
    fourth: null
  };

  private searchParams: ChoreSearchDto = {
    userName: '',
    endDate: null,
  };

  constructor(
    private preferenceService: PreferenceService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private choreService: ChoreService,
    private errorHandler: ErrorHandlerService
  ) {
  }

  filterChores(choreList: any[], selectedChores: any[]): any[] {
    return choreList.filter(chore => !selectedChores.includes(chore));
  }


  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.preference);
    console.log(this.chores);
    if (form.valid) {
      let observable: Observable<Preference>;
      observable = this.preferenceService.editPreference(this.preference);

      observable.subscribe({
        next: data => {
          this.notification.success(`Preferences successfully changed.`, "Success");

          // Fetch the updated preference after editing
          this.preferenceService.getLastPreference().subscribe({
            next: (lastPref: Preference) => {
              if (lastPref) {
                this.oldPreference = lastPref;
              }
            }
          });
          this.router.navigate(['/chores/all']);
        },
        error: error => {
          this.errorHandler.handleErrors(error, "last preference", 'get');
        }
      });
    }
  }


  ngOnInit(): void {
    console.log(this.oldPreference);
    this.preferenceService.getLastPreference().subscribe({
      next: (lastPreference: Preference) => {
        if (lastPreference) {
          this.oldPreference = lastPreference;
        }
        this.choreService.getUnassignedChores().subscribe({
          next: (chores: any[]) => {
            this.chores = chores;
            this.filteredChores[0] = this.chores;
            this.filteredChores[1] = this.chores.slice();
            this.filteredChores[2] = this.chores.slice();
            this.filteredChores[3] = this.chores.slice();
          }
        });
      },
      error: (error: any) => {
        this.choreService.getUnassignedChores().subscribe({
          next: (chores: any[]) => {
            this.chores = chores;
          }
        });
      }
    });
  }


  navigateToAllChores() {
    this.router.navigate(['chores','all']);
  }

  navigateToMyChores() {
    this.router.navigate(['chores','my']);
  }

  navigateToLeaderboard() {
    this.router.navigate(['chores','leaderboard']);

  }
}
