export class Unit {
  name: string;
  convertFactor?: number;
  subUnit?: Unit
}

export class UnitConvertDto {
  from: Unit;
  to: Unit;
  value: number;
}
