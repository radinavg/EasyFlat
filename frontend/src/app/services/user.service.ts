import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {UserListDto} from "../dtos/user";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  baseUri = environment.backendUrl + '/user';

  constructor(
    private http: HttpClient
  ) {
  }

  findFlatmates(): Observable<UserListDto[]> {
    return this.http.get<UserListDto[]>(this.baseUri + '/flatmates');
  }
}
