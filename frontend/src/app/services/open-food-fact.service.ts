import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {ItemDto} from "../dtos/item";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class OpenFoodFactService {


  baseUri = environment.backendUrl + '/item';

  constructor(
    private http: HttpClient,
  ) {
  }

  findByEan(ean: string): Observable<ItemDto> {
    return this.http.get<ItemDto>(`${this.baseUri}/ean/${ean}`);
  }

}
