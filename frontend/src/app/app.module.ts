import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {
  NgbAlertModule,
  NgbDatepickerModule,
  NgbModule,
  NgbTimepickerConfig,
  NgbTimepickerModule
} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {DigitalStorageComponent} from './components/digital-storage/digital-storage.component';
import {ItemCardComponent} from './components/digital-storage/item-card/item-card.component';
import {ItemCreateEditComponent} from './components/digital-storage/item-create-edit/item-create-edit.component';
import {ItemDetailComponent} from './components/digital-storage/item-detail/item-detail.component';
import {ToastrModule} from "ngx-toastr";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AutocompleteComponent} from './components/utils/autocomplete/autocomplete.component';
import {ItemDetailListComponent} from './components/digital-storage/item-detail-list/item-detail-list.component';
import {RegisterComponent} from './components/register/register.component';
import {AccountComponent} from './components/account/account.component';
import {LoginFlatComponent} from "./components/login-flat/login-flat.component";
import {CreateFlatComponent} from './components/create-flat/create-flat.component';
import {ShoppingListComponent} from './components/shopping-list/shopping-list.component';
import {
  ShoppingItemCreateEditComponent
} from "./components/shopping-list/shopping-item-create-edit/shopping-item-create-edit.component";
import {ColorPickerModule} from "ngx-color-picker";
import {
  ShoppingListCreateComponent
} from './components/shopping-list/shopping-list-create/shopping-list-create.component';
import {CookingComponent} from './components/cooking/cooking.component';
import {RecipeCardComponent} from './components/cooking/recipe-card/recipe-card.component';
import {JsonPipe, NgOptimizedImage} from "@angular/common";
import {CookbookComponent} from './components/cookbook/cookbook.component';
import {CookbookCardComponent} from './components/cookbook/cookbook-card/cookbook-card.component';
import {RecipeDetailComponent} from './components/cooking/recipe-detail/recipe-detail.component';
import {CookbookCreateComponent} from './components/cookbook/cookbook-create/cookbook-create.component';
import {CookbookDetailComponent} from './components/cookbook/cookbook-detail/cookbook-detail.component';
import {CookbookModalComponent} from './components/cookbook/cookbook-modal/cookbook-modal.component';
import {CookingModalComponent} from './components/cooking/cooking-modal/cooking-modal.component';
import {LOAD_WASM, NgxScannerQrcodeModule} from "ngx-scanner-qrcode";
import {ConfirmDeleteDialogComponent} from "./components/utils/confirm-delete-dialog/confirm-delete-dialog.component";
import {ShoppingListsComponent} from './components/shopping-list/shopping-lists/shopping-lists.component';
import {ShoppingListCardComponent} from './components/shopping-list/shopping-list-card/shopping-list-card.component';
import {AdminSelectionModalComponent} from './components/admin-selection-modal/admin-selection-modal.component';
import {SignOutModalComponent} from './components/utils/sign-out-modal/sign-out-modal.component';
import {EventsComponent} from "./components/events/events.component";
import {EventsCreateComponent} from "./components/events/events-create/events-create.component";
import {EventCardComponent} from "./components/events/event-card/event-card.component";
import {MatchingModalComponent} from "./components/cooking/matching-modal/matching-modal.component";
import {
  MatchingModalCookbookComponent
} from "./components/cookbook/matching-modal-cookbook/matching-modal-cookbook.component";
import {FinanceComponent} from './components/finance/finance.component';
import {ExpenseCreateEditComponent} from './components/finance/expense-create-edit/expense-create-edit.component';
import {
  RadioButtonsComponentComponent
} from './components/utils/radio-buttons-component/radio-buttons-component.component';
import {ShowUserForExpenseComponent} from './components/utils/show-user-for-expense/show-user-for-expense.component';
import {ChorePreferenceComponent} from './components/chores/chore-preference/chore-preference.component';
import {AllChoreComponent} from './components/chores/all-chore/all-chore.component';
import {MyChoresComponent} from './components/chores/my-chores/my-chores.component';
import {NewChoreComponent} from './components/chores/new-chore/new-chore.component';
import {ChoreCardComponent} from './components/chores/chore-card/chore-card.component';
import {LeaderboardComponent} from './components/chores/leaderboard/leaderboard.component';
import {MatListModule} from '@angular/material/list';
import {MatCardModule} from '@angular/material/card';
import {ChorePreferenceCardComponent} from './components/chores/chore-preference-card/chore-preference-card.component';
import {
  ChoreConfirmationModalComponent
} from "./components/chores/my-chores/chore-confirmation-modal/chore-confirmation-modal.component";
import {UserDropdownComponent} from './components/utils/user-dropdown/user-dropdown.component';
import {DebitsComponent} from './components/finance/debits/debits.component';
import {ConfirmPayedPackComponent} from './components/utils/confirm-payed-pack/confirm-payed-pack.component';
import {BarchartVerticalComponent} from './components/finance/graphs/barchart-nagative/barchart-vertical.component';
import {NgxEchartsModule} from "ngx-echarts";
import {RadarComponent} from './components/finance/graphs/radar/radar.component';
import {ExpenseDetailComponent} from './components/finance/expense-detail/expense-detail.component';
import {ExpenseOverviewComponent} from './components/finance/expense-overview/expense-overview.component';
import {SortButtonComponent} from './components/utils/sort-button/sort-button.component';
import { CreateExpenseFromShopComponent } from './components/utils/create-expense-from-shop/create-expense-from-shop.component';

LOAD_WASM().subscribe();


@NgModule({
    declarations: [
        AppComponent,
        HeaderComponent,
        FooterComponent,
        HomeComponent,
        LoginComponent,
        MessageComponent,
        DigitalStorageComponent,
        ItemCardComponent,
        ItemCreateEditComponent,
        ItemDetailComponent,
        AutocompleteComponent,
        ItemDetailListComponent,
        RegisterComponent,
        AccountComponent,
        LoginFlatComponent,
        CreateFlatComponent,
        ShoppingListComponent,
        ShoppingItemCreateEditComponent,
        ShoppingListCreateComponent,
        CookingComponent,
        RecipeCardComponent,
        CookbookComponent,
        CookbookCardComponent,
        RecipeDetailComponent,
        CookbookCreateComponent,
        CookbookDetailComponent,
        CookbookModalComponent,
        CookingModalComponent,
        ConfirmDeleteDialogComponent,
        ShoppingListsComponent,
        ShoppingListCardComponent,
        AdminSelectionModalComponent,
        SignOutModalComponent,
        EventsComponent,
        EventsCreateComponent,
        EventCardComponent,
        MatchingModalComponent,
        MatchingModalCookbookComponent,
        FinanceComponent,
        ExpenseCreateEditComponent,
        RadioButtonsComponentComponent,
        ShowUserForExpenseComponent,
        UserDropdownComponent,
        DebitsComponent,
        ConfirmPayedPackComponent,
        BarchartVerticalComponent,
        RadarComponent,
        ExpenseDetailComponent,
        ConfirmDeleteDialogComponent,
        ExpenseOverviewComponent,
        ChorePreferenceComponent,
        AllChoreComponent,
        MyChoresComponent,
        NewChoreComponent,
        ChoreCardComponent,
        LeaderboardComponent,
        ChorePreferenceCardComponent,
        ChoreConfirmationModalComponent,
      SortButtonComponent,
      CreateExpenseFromShopComponent,
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        ReactiveFormsModule,
        HttpClientModule,
        NgbModule,
        FormsModule,
        ToastrModule.forRoot({
          timeOut: 7000,
            positionClass: 'toast-top-right',
            preventDuplicates: true,
        }),
        BrowserAnimationsModule,
        ColorPickerModule,
        NgOptimizedImage,
        NgxScannerQrcodeModule,
        NgxEchartsModule.forRoot({
          /**
           * This will import all modules from echarts.
           * but only when they are called
           */
          echarts: () => import('echarts'), // or import('./path-to-my-custom-echarts')
        }),
        NgbDatepickerModule,
        NgbTimepickerModule,
        NgbAlertModule,
        JsonPipe,
        MatListModule,
        MatCardModule,
      ],
    providers: [
      httpInterceptorProviders,
      NgbTimepickerConfig
    ],
    bootstrap: [AppComponent]
})

export class AppModule {
}
