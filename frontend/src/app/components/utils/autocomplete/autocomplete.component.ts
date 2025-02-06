import {Component, Input, OnInit} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {debounceTime, of, Subject, switchMap, tap} from 'rxjs';


/** Component for an autocomplete input, similar to a combo box,
 * that lets the user search a list of options by entering a search text.
 * The using site needs to supply a callback that produces the autocomplete options
 * and a callback that formats the model objects to readable text.
 *
 * @param T the model type. In practice only used as a placeholder and helper for typesafety inside this class.
 */
@Component({
  selector: 'app-autocomplete',
  templateUrl: './autocomplete.component.html',
  styleUrls: ['./autocomplete.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: AutocompleteComponent,
    },
  ]
})
export class AutocompleteComponent<T> implements OnInit, ControlValueAccessor {
  static counter = 0;

  // See documentation of NgClass for comparison
  // <https://angular.io/api/common/NgClass>
  @Input()
  textInputClass: string | string[] | Set<string> | { [klass: string]: any } = [];
  @Input()
  datalistClass: string | string[] | Set<string> | { [klass: string]: any } = [];

  dataListId: string;
  inputText = '';
  checkValueNeedsToMatchSuggestion = true;
  value: T | null = null;
  valueCandidates = new Map<string, T>();
  touched = false;
  disabled = false;
  inputChange = new Subject<string>();

  constructor() {
    const autocompleteId = AutocompleteComponent.counter++;
    this.dataListId = `app-autocomplete-candidates-${autocompleteId}`;
  }

  /** If this is `true`, only inputs that match an option from the suggestion list are considered valid
   * and are assigned to the model variable `value`.
   * Setting this to  `false`, only makes sense, if the type `T` of  the model is `string`.
   */
  @Input()
  set valueNeedsToMatchSuggestion(value: string) {
    this.checkValueNeedsToMatchSuggestion = (value === 'true');
  }

  /** Used to get a list of suggestions.
   * These are displayed in the data list
   * and also used to check if input is valid when `valueNeedsToMatchSuggestion` is `true`.
   */
  @Input()
  suggestions = (input: string) => of([]);

  /** Used to get a readable representation of the model value
   * for use in the text input field
   * and the data list options
   */
  @Input()
  formatModel = (model: T | null) => (model as any).toString();

  /** Placeholder text for the input field */
  @Input()
  placeholder: string = '';

  // Dummy functions for the callback variables, so that we do not need to check,
  // if one was already registered
  onChange = (quantity: any) => {
  };
  onTouched = () => {
  };

  ngOnInit(): void {
    this.inputChange
      .pipe(
        tap(this.checkIfInputMatchesCandidate.bind(this)),
        debounceTime(300),
        switchMap(this.suggestions),
      )
      .subscribe({
        next: this.onRecieveNewCandidates.bind(this),
        error: err => {
          console.error('Error when getting autocompletion list', err);
        },
      });
  }


  public resetInputText(): void {
    if (this.checkValueNeedsToMatchSuggestion) {
      this.inputText = this.formatModel(this.value);
    }
  }

  /* Methods needed for ControlValueAccessor */

  public writeValue(obj: any): void {
    this.setValue(obj as T);
  }

  public registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  public registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  public setDisabledState(disabled: boolean): void {
    this.disabled = disabled;
  }

  private checkIfInputMatchesCandidate(input: string): void {
    this.markAsTouched();
    if (input === '') {
      this.setValue(null);
    } else if (!this.checkValueNeedsToMatchSuggestion) {
      /* Type cast hack. The option `valueNeedsToMatchSuggestion` only makes sense
       * if  the model type parameter `T` actually is meant to be `string` anyway.
       * If it is not, expect hell to break loose here.
      */
      this.setValue(input as T);
    } else {
      const selectedValue = this.valueCandidates.get(input);
      if (selectedValue) {
        this.setValue(selectedValue);
      }
    }
  }

  private setValue(newValue: T | null) {
    this.value = newValue;
    this.inputText = this.formatModel(this.value);
    this.onChange(this.value);
  }

  private onRecieveNewCandidates(result: T[]) {
    this.valueCandidates.clear();
    for (const candidate of result) {
      this.valueCandidates.set(this.formatModel(candidate), candidate);
    }
  }

  private markAsTouched(): void {
    if (!this.touched) {
      this.touched = true;
      this.onTouched();
    }
  }
}
