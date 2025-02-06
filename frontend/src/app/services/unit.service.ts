import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {Unit} from "../dtos/unit";

@Injectable({
  providedIn: 'root'
})
export class UnitService {
  baseUri = environment.backendUrl + '/unit';

  constructor(
    private http: HttpClient
  ) {
  }

  findAll(): Observable<Unit[]> {
    return this.http.get<Unit[]>(`${this.baseUri}`);
  }
}
