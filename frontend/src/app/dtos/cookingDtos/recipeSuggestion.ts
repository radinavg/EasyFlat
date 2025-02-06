import {RecipeIngredient} from "./recipeIngredient";
import {CookingSteps} from "./CookingSteps";

export class RecipeSuggestion {
  id?: string;
  title: string;
  summary: string;
  servings: number;
  readyInMinutes: number;
  extendedIngredients: RecipeIngredient[];
  missedIngredients: RecipeIngredient[];
}


export class RecipeDetailDto {
  id: string;
  title: string;
  summary: string;
  servings: number;
  readyInMinutes: number;
  extendedIngredients: RecipeIngredient[];
  missedIngredients: RecipeIngredient[];
  steps: CookingSteps;
}
