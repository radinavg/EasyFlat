import {Component, OnInit} from '@angular/core';
import {EventDto, EventLabel} from "../../../dtos/event";
import {CookbookMode} from "../../cookbook/cookbook-create/cookbook-create.component";
import {NgForm} from "@angular/forms";
import {Observable} from "rxjs";
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../../services/cooking.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {UnitService} from "../../../services/unit.service";
import {EventsService} from "../../../services/events.service";


export enum EventsMode {
  create,
  edit
}

@Component({
  selector: 'app-events-create',
  templateUrl: './events-create.component.html',
  styleUrls: ['./events-create.component.scss']
})
export class EventsCreateComponent implements OnInit {
  event: EventDto = {
    title: '',
    description: '',
    date: new Date(),
    startTime: '',
    endTime: ''
  };
  allDay: boolean = false;

  mode: EventsMode = EventsMode.create;
  selectedLabelColor = '#ffffff';

  constructor(
    private eventService: EventsService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }


  public get submitButtonText(): string {
    switch (this.mode) {
      case EventsMode.create:
        return 'Create';
      case EventsMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  ngOnInit(): void {

    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if (this.mode === EventsMode.edit) {
      this.route.params.subscribe({
        next: params => {
          const id = params.id;
          this.eventService.getEventWithId(id).subscribe({
            next: res => {
              console.log(res)
              this.event = res;
              if(this.event.startTime == '00:00:00' && this.event.endTime == '23:59:00') {
                this.allDay = true;
              }
            },
            error: error => {
              console.error(`Event could not be retrieved from the backend: ${error}`);
              this.router.navigate(['/events']);
              this.notification.error('Event could not be retrieved', "Error");
            }
          })
        },
        error: error => {
          console.error(`Event could not be retrieved using the ID from the URL: ${error}`);
          this.router.navigate(['events']);
          this.notification.error('No event provided for editing', "Error");
        }
      })
    }
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case EventsMode.create:
        return 'created';
      case EventsMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  public get heading(): string {
    switch (this.mode) {
      case EventsMode.create:
        return 'Create Event';
      case EventsMode.edit:
        return 'Edit Event';
      default:
        return '?';
    }
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.event);
    if(this.allDay) {
      this.event.startTime =  '00:00:00';
      this.event.endTime =  '23:59:00';
    }


      let observable: Observable<EventDto>;
      switch (this.mode) {
        case EventsMode.create:
          observable = this.eventService.createEvent(this.event);
          break;
        case EventsMode.edit:
          observable = this.eventService.updateEvent(this.event);
          break;
        default:
          console.error('Unknown EventMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Event ${this.event.title} successfully ${this.modeActionFinished}`, "Success");
          this.router.navigate(['/events']);
        },
        error: error => {
          console.error(error);
          let firstBracket = error.error.indexOf('[');
          let lastBracket = error.error.indexOf(']');
          let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
          let errorDescription = error.error.substring(0, firstBracket);
          errorMessages.forEach(message => {
            this.notification.error(message, errorDescription);
          });
        }
      });


  }

  addLabel(label: string, selectedLabelColor: string): void {
    if (!label || label.length === 0) {
      return;
    }

    const newLabel: EventLabel = {
      labelName: label,
      labelColour: (selectedLabelColor !== '#ffffff' ? selectedLabelColor : '#000000')
    };

    if (!this.event.labels) {
      this.event.labels = [newLabel];
    } else {
      this.event.labels.push(newLabel);
    }
  }

  removeLabel(i: number) {
    if (this.event.labels && this.event.labels.length > i) {
      this.event.labels.splice(i, 1);
    }
  }

}
