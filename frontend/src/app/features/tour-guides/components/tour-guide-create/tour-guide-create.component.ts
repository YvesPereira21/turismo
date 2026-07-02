import { Component, inject } from '@angular/core';
import { TourGuideService } from '../../services/tour-guide.service';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { TourGuideCreate } from '../../../../core/models/tour-guide';

@Component({
  selector: 'app-tour-guide-create',
  imports: [ReactiveFormsModule],
  templateUrl: './tour-guide-create.component.html',
  styleUrl: './tour-guide-create.component.css'
})
export class TourGuideCreateComponent {
  private tourGuideService = inject(TourGuideService);
  private formBuilder = inject(FormBuilder);

  isSubmiting: boolean = false;
  tourGuideForm = this.formBuilder.group({
    cadastur: ['', { nonNullable: true, validators: [Validators.required] }],
    type: ['', { nonNullable: true, validators: [Validators.required] }],
    name: ['', { nonNullable: true, validators: [Validators.required] }],
    email: ['', { nonNullable: true, validators: [Validators.required, Validators.email] }],
    password: ['', { nonNullable: true, validators: [Validators.required] }],
    phone: ['']
  })

  onSubmit() {
    if (this.tourGuideForm.invalid) return alert('Por gentileza, insira as informações corretamente');
    this.isSubmiting = true;

    const formValues = this.tourGuideForm.value;
    const tourGuide: TourGuideCreate = {
      cadastur: formValues.cadastur!,
      type: formValues.type!,
      user: {
        name: formValues.name!,
        email: formValues.email!,
        password: formValues.password!,
        phone: formValues.phone!
      }
    }

    this.tourGuideService.createTourGuide(tourGuide).subscribe({
      next: (response) => {
        alert('Conta criada com sucesso!');
        this.clearForm();
      }, error: (erro) => {
        alert('Erro ao criar conta. Por favor, tente novamente');
      }
    })
  }

  clearForm() {
    this.tourGuideForm.reset();
    this.isSubmiting = false;
  }
}
