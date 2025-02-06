import {OrderType} from "./orderType";
import {Unit} from "./unit";

export class StorageItem {
  itemId?: string;
  ean?:string;
  generalName?:string;
  productName?:string;
  brand?:string;
  quantityCurrent:number;
  quantityTotal?:number;
  unit?: Unit;
  expireDate?:string;
  description?:string;
  priceInCent?:number;
}

export class ItemSearchDto {
  itemId?: string;
  alwaysInStock:boolean;
  productName?:string;
  fillLevel?:string;
  orderBy: OrderType;
  desc: boolean;
}

export class StorageItemListDto {
  generalName?:string;
  quantityCurrent:number;
  quantityTotal:number;
  storId:string;
  unit: Unit;
}
