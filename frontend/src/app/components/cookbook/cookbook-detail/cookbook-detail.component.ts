import {Component, Input, OnInit} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../../services/cooking.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {RecipeIngredient} from "../../../dtos/cookingDtos/recipeIngredient";
import {MatchingModalComponent} from "../../cooking/matching-modal/matching-modal.component";
import {ItemService} from "../../../services/item.service";
import {MatchingModalCookbookComponent} from "../matching-modal-cookbook/matching-modal-cookbook.component";

@Component({
  selector: 'app-cookbook-detail',
  templateUrl: './cookbook-detail.component.html',
  styleUrls: ['./cookbook-detail.component.scss']
})
export class CookbookDetailComponent implements OnInit {


  @Input() recipe: RecipeSuggestion;

  constructor(public activeModal: NgbActiveModal, private cookingService: CookingService, private notification: ToastrService, private router: Router,
              private route: ActivatedRoute,
              private itemService: ItemService,
              private modalService: NgbModal,) {
  }

  ngOnInit(): void {
    console.log(this.recipe)
    this.cookingService.getCookbookRecipe(this.recipe.id.toString()).subscribe({
      next: res => {
        this.recipe = res;
      },
      error: error => {

      }
    })
  }


  openMatchModal(ingredient: RecipeIngredient) {
    const modalRef = this.modalService.open(MatchingModalCookbookComponent, {size: 'lg'});
    modalRef.componentInstance.ingredient = ingredient;


    modalRef.componentInstance.matchingDone.subscribe(() => {
      this.ngOnInit();
    });
  }

  unMatchIngredient(ingredient: RecipeIngredient) {
    console.log(ingredient);
    if (ingredient.matchedItem && ingredient.realName) {
      const realNameIndex = ingredient.matchedItem.alternativeNames.findIndex(
        (alternativeName) => alternativeName.name === ingredient.realName
      );

      if (realNameIndex !== -1) {
        ingredient.matchedItem.alternativeNames.splice(realNameIndex, 1);
      }
      ingredient.matched = false;
      this.itemService.updateItem(ingredient.matchedItem).subscribe({
        next: () => {
          this.cookingService.unMatchIngredient(ingredient.name).subscribe({
            next: () => {
              this.notification.success(`Ingredient ${ingredient.name} successfully unmatched`, "Success");
              this.ngOnInit();
            },
            error: error => {
              this.notification.error(`Ingredient ${ingredient.name} cannot be  unmatched`, "Error");
            }
          });

        },
      });

    }
  }
}
