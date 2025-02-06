import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {RecipeDetailDto, RecipeSuggestion} from "../dtos/cookingDtos/recipeSuggestion";
import {AuthService} from "./auth.service";
import {RecipeIngredient} from "../dtos/cookingDtos/recipeIngredient";

@Injectable({
  providedIn: 'root'
})
export class CookingService {
  baseUri = environment.backendUrl + '/cooking';
  cookbookUri = this.baseUri + '/cookbook';

  constructor(private httpClient: HttpClient,
              private authService: AuthService) {
  }

  loadRecipes(type: string): Observable<RecipeSuggestion[]> {
    console.log(type + '  service')
    let params = new HttpParams();
    if (type) {
      params = params.append('type', type);
    }
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.get<RecipeSuggestion[]>(this.baseUri, {params, headers},);
  }

  getCookbook(): Observable<RecipeSuggestion[]> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.get<RecipeSuggestion[]>(this.cookbookUri, {headers});
  }

  createCookbookRecipe(recipe: RecipeSuggestion): Observable<RecipeSuggestion> {

    if (recipe.missedIngredients) {
      recipe.missedIngredients.forEach(ingredient => {
        if (ingredient.id != null) {
          ingredient.id = null;
        }
      });
    }
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });

    return this.httpClient.post<RecipeSuggestion>(this.cookbookUri, recipe, {headers});
  }

  updateCookbookRecipe(recipe: RecipeSuggestion): Observable<RecipeSuggestion> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.put<RecipeSuggestion>(this.cookbookUri + '/' + recipe.id, recipe, {headers});
  }

  getCookbookRecipe(id: string): Observable<RecipeSuggestion> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.get<RecipeSuggestion>(this.cookbookUri + '/' + id, {headers});
  }

  deleteCookbookRecipe(id: string): Observable<RecipeSuggestion> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.delete<RecipeSuggestion>(this.cookbookUri + '/' + id, {headers});
  }

  getRecipeDetails(id: string): Observable<RecipeDetailDto> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.get<RecipeDetailDto>(this.baseUri + '/detail/' + id, {headers});
  }

  getMissingIngredients(id: string): Observable<RecipeSuggestion> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.get<RecipeSuggestion>(this.cookbookUri + '/missing/' + id, {headers});
  }

  cookRecipe(recipe: RecipeSuggestion): Observable<RecipeSuggestion> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.put<RecipeSuggestion>(this.baseUri + "/cook", recipe, {headers})
  }

  addToShoppingList(recipe: RecipeSuggestion): Observable<RecipeSuggestion> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.post<RecipeSuggestion>(this.baseUri + '/shopping', recipe, {headers});
  }

  unMatchIngredient(ingredientName: string): Observable<RecipeIngredient> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.put<RecipeIngredient>(this.baseUri + '/unmatchitems', ingredientName, {headers})
  }

}
