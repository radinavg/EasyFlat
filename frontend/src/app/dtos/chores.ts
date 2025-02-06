import {UserDetail} from "./auth-request";
import {SharedFlat} from "./sharedFlat";

export class ChoresDto {

  id: number;
  name: string;
  description: string;
  endDate: Date;
  points: string;
  user: UserDetail;
  sharedFlat: SharedFlat;
  completed: boolean;
}

export class ChoreSearchDto {
  userName?: string;
  endDate?: Date;
}
