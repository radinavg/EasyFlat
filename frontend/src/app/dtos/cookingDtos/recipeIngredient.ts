import {Unit} from "../unit";
import {ItemDto} from "../item";

export class RecipeIngredient {

  id?: number;
  name?: string;
  unit?: string;
  unitEnum?: Unit;
  amount?: number;
  matched: boolean;
  autoMatched: boolean;
  haveWithDifferentUnits: boolean;
  realName: string;
  matchedItem: ItemDto
}
