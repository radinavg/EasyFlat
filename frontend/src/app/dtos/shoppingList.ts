import {ShoppingItemDto} from "./item";

export class ShoppingListDto{
  constructor(
    public id: number,
    public name: string,
    public itemsCount: number
  ) {}
}




