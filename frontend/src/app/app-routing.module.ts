import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {DigitalStorageComponent} from "./components/digital-storage/digital-storage.component";
import {
    ItemCreateEditComponent,
    ItemCreateEditMode
} from "./components/digital-storage/item-create-edit/item-create-edit.component";
import {ItemDetailComponent} from "./components/digital-storage/item-detail/item-detail.component";
import {ItemDetailListComponent} from "./components/digital-storage/item-detail-list/item-detail-list.component";
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {RegisterComponent} from "./components/register/register.component";
import {AccountComponent} from "./components/account/account.component";
import {LoginFlatComponent} from "./components/login-flat/login-flat.component";
import {CreateFlatComponent} from "./components/create-flat/create-flat.component";
import {ShoppingListComponent} from "./components/shopping-list/shopping-list.component";
import {
  ShoppingItemCreateEditComponent, ShoppingItemCreateEditMode
} from "./components/shopping-list/shopping-item-create-edit/shopping-item-create-edit.component";
import {
  ShoppingListCreateComponent
} from "./components/shopping-list/shopping-list-create/shopping-list-create.component";
import {CookingComponent} from "./components/cooking/cooking.component";
import {CookbookComponent} from "./components/cookbook/cookbook.component";
import {CookbookCreateComponent, CookbookMode} from "./components/cookbook/cookbook-create/cookbook-create.component";
import {RecipeDetailComponent} from "./components/cooking/recipe-detail/recipe-detail.component";
import {CookbookDetailComponent} from "./components/cookbook/cookbook-detail/cookbook-detail.component";
import {ShoppingListsComponent} from "./components/shopping-list/shopping-lists/shopping-lists.component";
import {EventsComponent} from "./components/events/events.component";
import {EventsCreateComponent, EventsMode} from "./components/events/events-create/events-create.component";
import {
  ExpenseCreateEditComponent,
  ExpenseCreateEditMode
} from "./components/finance/expense-create-edit/expense-create-edit.component";
import {FinanceComponent} from "./components/finance/finance.component";
import {ExpenseDetailComponent} from "./components/finance/expense-detail/expense-detail.component";
import {ExpenseOverviewComponent} from "./components/finance/expense-overview/expense-overview.component";
import {ChorePreferenceComponent} from "./components/chores/chore-preference/chore-preference.component";
import {AllChoreComponent} from "./components/chores/all-chore/all-chore.component";
import {MyChoresComponent} from "./components/chores/my-chores/my-chores.component";
import {NewChoreComponent} from "./components/chores/new-chore/new-chore.component";
import {LeaderboardComponent} from "./components/chores/leaderboard/leaderboard.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {
    path: 'digital-storage', canActivate: mapToCanActivate([AuthGuard]), children: [
      {path: '', component: DigitalStorageComponent},
      {path: ':name', component: ItemDetailListComponent }
  ]},
  { path: 'item', canActivate: mapToCanActivate([AuthGuard]), children: [
      { path: 'create', component: ItemCreateEditComponent, data: { mode: ItemCreateEditMode.create } },
      { path: ':id/detail', component: ItemDetailComponent },
      { path: ':id/edit', component: ItemCreateEditComponent, data: { mode: ItemCreateEditMode.edit } },
    ]
  },
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {
    path: 'shopping-lists', canActivate: mapToCanActivate([AuthGuard]),children: [
      {path: '', component: ShoppingListsComponent},
      {
        path: 'list', children: [
          {path: 'create', component: ShoppingListCreateComponent}
        ]
      },
      {
        path: 'list/:id', children: [
          {path: '', component: ShoppingListComponent},
          {
            path: 'item', children: [
              {path: 'create', component: ShoppingItemCreateEditComponent, data: {mode: ShoppingItemCreateEditMode.create}},
              {path: ':id/edit', component: ShoppingItemCreateEditComponent, data: {mode: ShoppingItemCreateEditMode.edit}},
            ]
          },
        ]
      },
    ]
  },
  {path: 'register', component: RegisterComponent},
  {path: 'chores', canActivate: mapToCanActivate([AuthGuard]), children: [
      {path: 'preference', component: ChorePreferenceComponent},
      {path: 'all', component: AllChoreComponent},
      {path: 'my', component: MyChoresComponent},
      {path: 'add', component: NewChoreComponent},
      {path: 'leaderboard', component: LeaderboardComponent},
    ]},
  {path: 'account', canActivate: mapToCanActivate([AuthGuard]), component: AccountComponent},
  {path: 'wgLogin', component: LoginFlatComponent},
  {path: 'wgCreate', component: CreateFlatComponent},
  {
    path: 'finance', children: [
            {path: '', component: FinanceComponent},
        ]
    },
    {
      path: 'expense', canActivate: mapToCanActivate([AuthGuard]),children: [
        {path: '', component: ExpenseOverviewComponent},
        {path: 'create', component: ExpenseCreateEditComponent, data: {mode: ExpenseCreateEditMode.create}},
        {path: ':id/edit', component: ExpenseCreateEditComponent, data: {mode: ExpenseCreateEditMode.edit}},
        {path: ':id/detail', component: ExpenseDetailComponent},
      ]
    },

    {
        path: 'cooking', canActivate: mapToCanActivate([AuthGuard]), children: [
        {path: '', component: CookingComponent},
        {path: ':id/detail', component: RecipeDetailComponent}
        ]
    },
    {
        path: 'cookbook', canActivate: mapToCanActivate([AuthGuard]), children: [
        {path: '', component: CookbookComponent},
        {path: 'create', component: CookbookCreateComponent, data: {mode: CookbookMode.create}},
        {path: ':id/edit', component: CookbookCreateComponent, data: {mode: CookbookMode.edit}},
        {path: ':id/detail', component: CookbookDetailComponent}
        ]
    },
  {
    path: 'events', canActivate: mapToCanActivate([AuthGuard]), children: [
      {path: '', component: EventsComponent},
      {path: 'create', component: EventsCreateComponent, data: {mode: EventsMode.create}},
      {path: ':id/edit', component: EventsCreateComponent, data: {mode: EventsMode.edit}}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})

export class AppRoutingModule {
}
