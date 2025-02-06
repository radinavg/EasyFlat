import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ChoreModalService {
  private deleteChoresSource = new Subject<void>();

  deleteChores$ = this.deleteChoresSource.asObservable();

  triggerDeleteChores() {
    this.deleteChoresSource.next();
  }
}
