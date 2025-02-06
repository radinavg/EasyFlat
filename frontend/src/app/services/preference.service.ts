import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Preference} from "../dtos/preference";
import {ShoppingItemDto} from "../dtos/item";
import {Observable} from "rxjs";
import {ChoresDto} from "../dtos/chores";
import {SharedFlat} from "../dtos/sharedFlat";

@Injectable({
  providedIn: 'root'
})
export class PreferenceService {
  private choreBaseUri: string = this.globals.backendUri + '/chores/preference'

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  editPreference(preference: Preference) {
    return this.httpClient.put<Preference>(`${this.choreBaseUri}`, preference)
  }

  getLastPreference(): Observable<Preference> {
    return this.httpClient.get<Preference>(this.choreBaseUri);
  }

}
