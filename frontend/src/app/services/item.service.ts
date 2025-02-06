import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {ItemDto} from "../dtos/item";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  baseUri = environment.backendUrl + '/item';

  constructor(
    private http: HttpClient,
  ) {
  }

  /**
   * Find an item via its ID
   *
   * @param id the of the item that should be retrieved
   * @return an Observable for the item with the given ID
   */
  getById(id: number): Observable<ItemDto> {
    return this.http.get<ItemDto>(`${this.baseUri}/${id}`);
  }

  /**
   * Find all items in the system
   */
  findAll(limit: number): Observable<ItemDto[]> {
    let params = new HttpParams();
    params = params.append('limit', limit);
    return this.http.get<ItemDto[]>(this.baseUri, {params});
  }

  /**
   * Find an item via their {@link generalName}
   *
   * @param generalName the group which an item belongs to
   * @return an Observable for a list of items which have the given {@link generalName}
   */
  findByGeneralName(generalName: string): Observable<ItemDto[]> {
    return this.http.get<ItemDto[]>(`${this.baseUri}/general-name/${generalName}`);
  }

  /**
   * Find an item via its brand
   *
   * @param brand the brand of the item
   * @return an Observable for a list of items made by this brand
   */
  findByBrand(brand: string): Observable<ItemDto[]> {
    let params = new HttpParams();
    params = params.append('brand', brand);
    return this.http.get<ItemDto[]>(`${this.baseUri}/search`, {params});
  }

  /**
   * Find an item via the store it was bought at
   *
   * @param boughtAt the name of the store
   * @return an Observable for a list of items purchased from this store
   */
  findByBoughtAt(boughtAt: string): Observable<ItemDto[]> {
    let params = new HttpParams();
    params = params.append('boughtAt', boughtAt);
    return this.http.get<ItemDto[]>(`${this.baseUri}/search`, {params});
  }

  /**
   * Create an item in the system
   *
   * @param item the item that should be added to the system
   * @return an Observable for the created item
   */
  createItem(item: ItemDto): Observable<ItemDto> {
    return this.http.post<ItemDto>(this.baseUri, item);
  }

  /**
   * Update an item in the system.
   *
   * @param item the data for the item that should be updated
   * @return an Observable for the updated item
   */
  updateItem(item: ItemDto): Observable<ItemDto> {
    return this.http.put<ItemDto>(`${this.baseUri}/${item.itemId}`, item);
  }

  /**
   * Delete an item from the system.
   *
   * @param itemId the id of the item that should be deleted
   * @return an Observable for the deleted item
   */
  deleteItem(itemId: number): Observable<void> {
    return this.http.delete<void>(this.baseUri + '/' + itemId);
  }

  findByName(productName: string, unitName: string): Observable<ItemDto> {
    let params = new HttpParams();
    params = params.append('unitName', unitName);
    return this.http.get<ItemDto>(this.baseUri + '/name/' + productName, {params})
  }

}
