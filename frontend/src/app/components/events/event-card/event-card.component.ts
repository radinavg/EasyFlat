import {Component, EventEmitter, Input, Output} from '@angular/core';
import * as events from "events";
import {EventDto} from "../../../dtos/event";
import {EventsService} from "../../../services/events.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.component.html',
  styleUrls: ['./event-card.component.scss']
})
export class EventCardComponent {
  @Input() event: EventDto;
  @Output() eventDeleted: EventEmitter<void> = new EventEmitter<void>();

  constructor(
    private eventService: EventsService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  truncateString(input: string, maxLength: number): string {
    if (input.length <= maxLength) {
      return input;
    }

    const truncated = input.substring(0, maxLength - 3);
    return truncated + '...';
  }

  deleteEvent() {
    this.eventService.deleteEvent(this.event.id.toString()).subscribe({
      next: data => {
        this.notification.success(`Event ${this.event.title} successfully deleted`, "Success");
        this.eventDeleted.emit();
      },
      error: error => {
        console.log(error);
      }
    });
  }

  export(id: number) {
    console.log(id)
    this.eventService.exportEvent(id.toString()).subscribe(
      (icsContent: string) => {
        this.downloadICSFile(icsContent);
      },
      (error) => {
        console.error('Error exporting event:', error);
        this.notification.error('Error exporting event ' + this.event.title);
      }
    );
  }

  private downloadICSFile(icsContent: string) {
    const blob = new Blob([icsContent], {type: 'text/calendar'});
    const url = window.URL.createObjectURL(blob);

    const link = document.createElement('a');
    link.href = url;
    const fileName = this.event.title + '.ics';
    link.setAttribute('download', fileName);
    document.body.appendChild(link);

    link.click();
    document.body.removeChild(link);
  }

  formatTime(time: string | null | undefined): string {
    if (time == null) {
      return '';
    }
    if (time.trim() === '') {
      return '';
    }

    const [hours, minutes] = time.split(':');
    return `${hours}:${minutes}`;
  }

  getIdForm(): string {
    return `${this.event.title}${this.event.id.toString()}`.replace(/\s/g, '');
  }

}
