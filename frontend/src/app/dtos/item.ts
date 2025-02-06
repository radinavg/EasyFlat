import {DigitalStorageDto} from "./digitalStorageDto";
import {IngredientDto} from "./ingredientDto";
import {ShoppingLabelDto} from "./shoppingLabel";
import {ShoppingListDto} from "./shoppingList";
import {OrderType} from "./orderType";
import {Unit} from "./unit";

export class ItemDto {
  itemId?: number;
  ean?: string;
  generalName?: string;
  productName?: string;
  brand?: string;
  quantityCurrent?: number;
  quantityTotal?: number;
  unit?: Unit;
  expireDate?: Date;
  description?: string;
  boughtAt?: string;
  priceInCent?: number;
  alwaysInStock: boolean;
  minimumQuantity?: number;
  addToFiance: boolean;
  ingredients?: [IngredientDto];
  digitalStorage?: DigitalStorageDto;
  alternativeNames?: AlternativeName[];
}

export class ItemFieldSearchDto {
  generalName?: string;
  brand?: string;
  boughtAt?: string;
}


export class ShoppingItemDto extends ItemDto {
  shoppingList?: ShoppingListDto;
  labels?: [ShoppingLabelDto];
  check?: boolean;
}

export class ShoppingItemSearchDto {
  itemId?: string;
  productName?: string;
  label?: string;
}

export class AlternativeName {
  id:number;
  name:string;
}
