import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {Router} from "@angular/router";
import {SharedFlat} from "../../dtos/sharedFlat";
import {AuthService} from "../../services/auth.service";
import {ToastrService} from "ngx-toastr";
import {ErrorHandlerService} from "../../services/util/error-handler.service";

@Component({
  selector: 'app-create-flat',
  templateUrl: './create-flat.component.html',
  styleUrls: ['./create-flat.component.scss']
})
export class CreateFlatComponent implements OnInit {
  createForm: UntypedFormGroup;
  submitted = false;
  error = false;
  errorMessage = '';


  constructor(private formBuilder: UntypedFormBuilder, private notification: ToastrService, private sharedFlatService: SharedFlatService, private authService: AuthService, private router: Router, private errorHandler: ErrorHandlerService) {
    this.createForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)],],
      repeatPassword: ['', [Validators.minLength(8)],],
    });
  }

  createWG(): void {
    this.submitted = true;
    const sharedFlat: SharedFlat = new SharedFlat(this.createForm.controls.name.value, this.createForm.controls.password.value)
    console.log(sharedFlat);
    if (this.createForm.controls.password.value != this.createForm.controls.repeatPassword.value) {
      this.notification.error("Passwords didn't match")
    } else {
      this.sharedFlatService.createWG(sharedFlat, this.authService.getToken()).subscribe({
        next: () => {
          this.changeEventToTrue();
          this.router.navigate(['/account']);
          this.notification.success('Successfully created shared flat: ' + sharedFlat.name, "Success");
        },
        error: error => {
          this.errorHandler.handleErrors(error, "shared flat", 'create');
        }
      });
    }

  }

  ngOnInit(): void {
  }


  changeEventToTrue() {
    this.sharedFlatService.changeEvent();
  }

}
