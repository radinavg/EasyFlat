import {Injectable} from '@angular/core';
import {ToastrService} from "ngx-toastr";

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {

  constructor(private notification: ToastrService) {
  }

  /**
   * Handle errors from the backend and send appropriate notifications
   *
   * @param error the error provided when subscribing to the request
   * @param entityType the name of the entity e.g. item, expense, event, etc.
   * @param action the action that was supposed to happen (in the past tense) e.g. created, updated, deleted, etc.
   */
  public handleErrors(error: any, entityType: string, action: string): void {

    // 400 - Bad request (Malformed JSON)
    // 403 - Forbidden (No permission to access resource)
    // 404 - Not found (Resource not found)
    // 409 - Conflict (Conflict Exception)
    // 422 - Unprocessable Entity (Validation Exception)
    // 500 - Internal Server Error (When unexpected error occurs in the server)
    // 502 - Bad Gateway (If API could not be reached)

    if (error.status === 400) {
      this.notification.error("There was an error with the request.", "Error");
    } else if (error.status === 403) {
      this.notification.error(`You do not have permission to access to this ${entityType}.`, "Error");
    } else if (error.status === 404) {
      this.notification.error(`The ${entityType} could not be ${action}.`, "Error");
      this.handleErrorMessages(error);
    } else if (error.status === 409) {
      this.handleErrorMessages(error);
    } else if (error.status === 422) {
      this.handleErrorMessages(error);
    } else if (error.status === 500) {
      this.notification.error(`The ${entityType} could not be ${action} due to an issue with the server.`, "Error");
    } else if (error.status === 502) {
      this.notification.error(`There was an issue communicating with an external API.`, "Error");
    } else {
      this.notification.error(`The ${entityType} could not be ${action}.`, "Error");
      this.handleErrorMessages(error);
    }
  }

  private handleErrorMessages(error: any) {
    if(typeof error.error === 'string'){
      let firstBracket = error.error.indexOf('[');
      let lastBracket = error.error.lastIndexOf(']'); // Use lastIndexOf in case there are multiple brackets

      if (firstBracket === -1) {
        if (error.error.length > 0){
          this.notification.error(error.error, "Error");
        } else {
          this.notification.error("An error occurred." , "Error");
        }
      } else {
        let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
        let errorDescription = error.error.substring(0, firstBracket - 2);
        if (!(errorMessages.length === 1 && errorMessages[0] === '')) {
          errorMessages.forEach(message => {
            this.notification.error(message.trim(), errorDescription);
          });
        } else {
          this.notification.error("An error occurred." , "Error");
        }
      }
    } else {
      this.notification.error("An error occurred.", "Error");
    }

  }
}
