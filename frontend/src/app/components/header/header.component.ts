import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {UserDetail} from "../../dtos/auth-request";
import {ShoppingListService} from "../../services/shopping-list.service";
import {Router} from "@angular/router";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {ShoppingListDto} from "../../dtos/shoppingList";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  constructor(public authService: AuthService, private sharedFlatService: SharedFlatService, private httpClient: HttpClient,
              private router: Router) {
  }

  ngOnInit() {
  }

  isInWg() {
    return this.sharedFlatService.isLoggInWg();
  }

  logoutUser() {
    this.sharedFlatService.changeEventToFalse();
    this.authService.logoutUser();
  }

}
