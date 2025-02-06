import { Injectable } from '@angular/core';
import {Preference} from "../dtos/preference";

@Injectable({
  providedIn: 'root'
})
export class PreferenceStorageService {

  private readonly PREFERENCE_KEY = 'lastPickedPreference';

  constructor() {}

  getLastPickedOptions(): Preference {
    const storedPreference = localStorage.getItem(this.PREFERENCE_KEY);
    return storedPreference ? JSON.parse(storedPreference) : null;
  }

  setLastPickedOptions(preference: Preference): void {
    localStorage.setItem(this.PREFERENCE_KEY, JSON.stringify(preference));
  }
}
