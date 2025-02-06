import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {SharedFlat} from "../../dtos/sharedFlat";
import {UserDetail} from "../../dtos/auth-request";
import {AuthService} from "../../services/auth.service";
import {getTokenAtPosition} from "@angular/compiler-cli/src/ngtsc/util/src/typescript";
import {ToastrService} from "ngx-toastr";
import {ErrorHandlerService} from "../../services/util/error-handler.service";

@Component({
  selector: 'app-login-flat',
  templateUrl: './login-flat.component.html',
  styleUrls: ['./login-flat.component.scss']
})
export class LoginFlatComponent implements OnInit{
  user: UserDetail
  loginForm: UntypedFormGroup;
  submitted = false;
  error = false;
  errorMessage = '';


  constructor(private formBuilder: UntypedFormBuilder, private sharedFlatService: SharedFlatService,private authService: AuthService, private notification: ToastrService, private router: Router,  private errorHandler: ErrorHandlerService) {
    this.loginForm = this.formBuilder.group({
      flatName: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }
  loginWG(): void {
    this.submitted = true;
    if (this.loginForm.valid) {
      const sharedFlat: SharedFlat = new SharedFlat(
        this.loginForm.controls.flatName.value,
        this.loginForm.controls.password.value
      );
      console.log('Try to authenticate shared flat: ' + sharedFlat.name);
      this.sharedFlatService.loginWG(sharedFlat, this.authService.getToken()).subscribe(
        () => {
          console.log('You have successfully logged in!');
          this.changeEventToTrue();
          this.router.navigate(['/']);
          this.notification.success("You have successfully logged in shared flat: " + sharedFlat.name , "Success");
        },
        (error) => {
          this.error = true;
          if (error) {
            this.errorMessage = 'Invalid credentials. Could not log in.';
            this.router.navigate(['/wgLogin']);
            this.errorHandler.handleErrors(error, "shared flat", 'log in');
          }
        }
      );
    } else {
      this.notification.error("Invalid input");
    }
  }

  ngOnInit(): void {
    this.authService.getUser(this.authService.getToken()).subscribe(
      (user) => {
        this.user = user;
      },
      (error) => {
        this.errorHandler.handleErrors(error, "user", 'get');
      }
    );
  }
  changeEventToTrue() {
    return this.sharedFlatService.changeEvent();
  }
}
