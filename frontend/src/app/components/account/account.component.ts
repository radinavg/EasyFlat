import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {UserDetail} from '../../dtos/auth-request';
import {Observable} from "rxjs";
import {Router} from "@angular/router";
import {SharedFlat} from "../../dtos/sharedFlat";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {ToastrService} from "ngx-toastr";
import {ShoppingListService} from "../../services/shopping-list.service";
import {NgbModal, NgbModalOptions} from '@ng-bootstrap/ng-bootstrap';
import {AdminSelectionModalComponent} from '../admin-selection-modal/admin-selection-modal.component';
import {LocalNgModuleData} from "@angular/compiler-cli/src/ngtsc/scope";
import {RecipeSuggestion} from "../../dtos/cookingDtos/recipeSuggestion";
import {ItemDto} from "../../dtos/item";

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  @Output() account: EventEmitter<UserDetail> = new EventEmitter<UserDetail>();
  user: UserDetail = {
    id: '',
    firstName: '',
    lastName: '',
    email: '',
    flatName: '',
    password: '',
    admin: false,
    points: 0
  };
  accountForm: FormGroup;
  passwordForm: FormGroup;
  flatForm: FormGroup;
  submitted = false;
  error = false;
  errorMessage = '';
  submittedPassword = false;

  users: UserDetail[];

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder, private shoppingListService: ShoppingListService, private authService: AuthService, private sharedFlatService: SharedFlatService, private router: Router, private notification: ToastrService) {
    this.accountForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required]],
      password: ['', [Validators.minLength(8)]],
      flatName: [''],
      admin: ['']
    });
    this.passwordForm = this.formBuilder.group({
      repeatPassword: ['', [Validators.minLength(8)]],
      newPassword: ['', [Validators.minLength(8)]],
    });
    this.flatForm = this.formBuilder.group({
      flatName: [''],
    });

  }

  openAdminSelectionModal(): void {
    console.log(this.users.length);
    if (this.users.length == 0) {
        this.signOut();
    } else {
      if (this.user.admin) {
        const options: NgbModalOptions = {
          centered: true,
        };

        const modalRef = this.modalService.open(AdminSelectionModalComponent, options);
        modalRef.componentInstance.users = this.users;

        modalRef.result.then((selectedUserId) => {
          // Handle the selected user ID and update admin
          console.log('Selected User ID:', selectedUserId);
          this.authService.setAdmin(selectedUserId).subscribe({
            next: (result) => {
              console.log('Admin set successfully:', result);
            },
            error: (error) => {
              console.error('Error setting admin:', error);
            },
            complete: () => {
              console.log('Admin setting complete');
            }
          });
          console.log('Admin is set')
            this.signOut();
        });
      } else {
          this.signOut();

      }
    }
  }


  ngOnInit(): void {
    // Fetch user data and update form values
    this.authService.getUser(this.authService.getToken()).subscribe(
      (user) => {
        this.user = user;
        console.log('User :', this.user);

        this.accountForm.patchValue({
          firstName: this.user.firstName,
          lastName: this.user.lastName,
          email: this.user.email,
          flatName: this.user.flatName,
          admin: this.user.admin
        });
        this.flatForm.patchValue({
          flatName: this.user.flatName
        })
        // Fetch all other users
        this.authService.getUsers(this.user.id).subscribe({
          next: (users) => {
            console.log(users)
            this.users = users;
          },
          error: (error) => {
            console.error('Error fetching users:', error);
          }
        });
      },
      (error) => {
        console.error('Error fetching user:', error);
      }
    );
  }

  update(): void {
    this.submitted = true;
    const formValue = this.accountForm.value;

      const userDetail: UserDetail = new UserDetail(this.user.id,this.accountForm.controls.firstName.value,this.accountForm.controls.lastName.value,  this.accountForm.controls.email.value, this.user.flatName , this.user.password,this.accountForm.controls.admin.value, this.user.points);
      console.log(userDetail)
      this.authService.update(userDetail).subscribe({
        next: () => {
          this.user = userDetail
          this.notification.success('Successfully updated user: ' + this.user.firstName + ' ' + this.user.lastName)
        },
        error: error => {
          console.log('Could not update due to:');
          console.log(error);
          this.error = true;
          let firstBracket = error.error.indexOf('[');
          let lastBracket = error.error.indexOf(']');
          let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
          let errorDescription = error.error.substring(0, firstBracket);
          errorMessages.forEach(message => {
            this.notification.error(message, errorDescription);
          });
        }
      });
    console.log(formValue)
  }

  vanishError() {
    this.error = false;
  }

  delete() {
    this.authService.delete(this.user).subscribe({
      next: (deletedUser: UserDetail) => {
        console.log('User deleted:', deletedUser);
        this.authService.logoutUser();
        this.router.navigate(['']);
      },
      error: error => {
        console.error(error.message, error);
      }
    });
  }

  signOut() {
    this.authService.signOut(this.accountForm.controls.flatName.value, this.user.id).subscribe({
      next: () => {
        this.router.navigate(['']);
        this.notification.success("You have successfully signed out", "Success");
        this.sharedFlatService.changeEventToFalse();
      },
      error: error => {
        let firstBracket = error.error.indexOf('[');
        let lastBracket = error.error.indexOf(']');
        let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
        let errorDescription = error.error.substring(0, firstBracket);
        errorMessages.forEach(message => {
          this.notification.error(message, errorDescription);
        });
      }
    });
  }

  deleteFlat() {
    this.sharedFlatService.delete(this.user).subscribe({
      next: (deletedFlat: SharedFlat) => {
        console.log('Shared flat deleted from user :', deletedFlat);
        this.router.navigate(['']);
        this.sharedFlatService.changeEventToFalse();
        this.notification.success("Flat " + deletedFlat.name + " is successfully deleted.", "Success");
      },
      error: error => {
        console.error(error.message, error);
        let firstBracket = error.error.indexOf('[');
        let lastBracket = error.error.indexOf(']');
        let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
        let errorDescription = error.error.substring(0, firstBracket);
        errorMessages.forEach(message => {
          this.notification.error(message, errorDescription);
        });
      }
    });

  }

  changePassword() {
    this.submittedPassword = true;
    if (this.passwordForm.controls.repeatPassword.value == this.passwordForm.controls.newPassword.value) {
      this.user.password = this.passwordForm.controls.newPassword.value;
      console.log(this.user);
      this.authService.update(this.user).subscribe({
        next: () => {
          this.notification.success('Successfully updated password for user: ' + this.user.firstName + ' ' + this.user.lastName)
        },
        error: error => {
          let firstBracket = error.error.indexOf('[');
          let lastBracket = error.error.indexOf(']');
          let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
          let errorDescription = error.error.substring(0, firstBracket);
          errorMessages.forEach(message => {
            this.notification.error(message, errorDescription);
          });
        }
      });
    } else {
        this.error = true;
        this.notification.error("Passwords don't match");
        console.error(this.errorMessage);
    }
  }

  getIdFormatForDeleteModal(user:UserDetail): string {
    return `${user.firstName}${user.id.toString()}`.replace(/\s/g, '');
  }

  truncateString(input: string, maxLength: number): string {
    if (input.length <= maxLength) {
      return input;
    }

    const truncated = input.substring(0, maxLength - 3);
    return truncated + '...';
  }

  getIdFormatForDeleteFlatModal(user: UserDetail) {
    return `${user.flatName}-1224`.replace(/[^a-zA-Z0-9]+/g, '');
  }
}

