import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {ItemDto, ShoppingItemDto, ShoppingItemSearchDto} from "../dtos/item";
import {Observable} from "rxjs";
import {ShoppingListDto} from "../dtos/shoppingList";
import {StorageItem} from "../dtos/storageItem";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class ShoppingListService {

  private baseUri: string = 'http://localhost:8080/api/v1/shopping';

  constructor(private http: HttpClient,
              private authService: AuthService) {
  }

  /**
   * Creates a shopping item in the system
   *
   * @param item the data without ID for the shopping item that should be stored in the system
   * @return an Observable for the stored shopping list in the system
   */
  createItem(item: ShoppingItemDto): Observable<ShoppingItemDto> {
    return this.http.post<ItemDto>(this.baseUri, item);
  }

  /**
   * Find an existing shopping item in the system
   *
   * @param id the id of the shopping item that should already be stored in the system
   * @return an Observable for the existing shopping list in the system
   */
  getById(id: string): Observable<ShoppingItemDto> {
    return this.http.get<ShoppingItemDto>(this.baseUri + '/' + id);
  }

  /**
   * Find existing shopping items in the system
   *
   * @param shopId the id of the shopping list to which the shopping items are connected in the system
   * @param searchParams search parameters consisting of the products' name and their labels' value
   * @return an Observable for the existing shopping items in the system
   */
  getItemsWithShopId(shopId: string, searchParams: ShoppingItemSearchDto):Observable<ShoppingItemDto[]> {
    let params = new HttpParams();
    if (searchParams.productName) {
      params = params.append('productName', searchParams.productName);
    }
    if (searchParams.label) {
      params = params.append('label', searchParams.label);
    }
    return this.http.get<ShoppingItemDto[]>(this.baseUri + "/list-items/" + shopId, {params});
  }

  /**
   * Find an existing shopping list in the system
   *
   * @param shoppingListName the name of the list that should already be stored in the system
   * @return an Observable for the existing shopping list in the system
   */
  getShoppingListByName(shoppingListName: string): Observable<ShoppingListDto> {
    return this.http.get<ShoppingListDto>(this.baseUri + '/list/' + shoppingListName);
  }

  /**
   * Find an existing shopping list in the system
   *
   * @param shoppingListId the id of the list that should already be stored in the system
   * @return an Observable for the existing shopping list in the system
   */
  getShoppingListById(shoppingListId: string): Observable<ShoppingListDto> {
    return this.http.get<ShoppingListDto>(this.baseUri + '/listId/' + shoppingListId);
  }

  createList(list: ShoppingListDto): Observable<ShoppingListDto> {
    return this.http.post<ShoppingListDto>(this.baseUri + "/list-create", list);
  }

  deleteItem(itemId: number): Observable<ShoppingItemDto> {
    return this.http.delete<ShoppingItemDto>(this.baseUri + '/' + itemId);
  }

  deleteList(shopId: string): Observable<ShoppingListDto> {
    return this.http.delete<ShoppingListDto>(this.baseUri + '/delete/' + shopId);
  }

  /**
   * Get all shopping lists filtered by search parameters
   *
   * @param searchParams the search parameters consisting only of one string representing the lists' name
   * @return on Observable for the array of lists, which fulfil the search criteria
   */
  getShoppingLists(searchParams: string): Observable<ShoppingListDto[]> {
    let params = new HttpParams();
    if (searchParams) {
      params = params.append('searchParams', searchParams);
    }
    return this.http.get<ShoppingListDto[]>(this.baseUri + '/lists', {params});
  }

  transferToStorage(shoppingItems: ShoppingItemDto[]): Observable<StorageItem[]> {
    return this.http.post<StorageItem[]>(this.baseUri + '/storage', shoppingItems);
  }

  /**
   * Update an item in the system.
   *
   * @param item the data for the item that should be updated
   * @return an Observable for the updated item
   */
  updateItem(item: ShoppingItemDto): Observable<ShoppingItemDto> {
    return this.http.put<ShoppingItemDto>(`${this.baseUri}/${item.itemId}`, item);
  }

  deleteItems(items: ShoppingItemDto[]) {
    console.log(items)
    const itemIds = items.map(item => item.itemId);
    return this.http.delete<ShoppingItemDto[]>(this.baseUri + '/delete', {params: {itemIds: itemIds.join(',')}});
  }

}

