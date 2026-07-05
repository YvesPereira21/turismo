import { Component, inject, input, OnInit, signal } from '@angular/core';
import { FormArray, FormControl, ReactiveFormsModule } from '@angular/forms';
import { TagService } from '../../services/tag.service';
import { Tag } from '../../../../core/models/tag';

@Component({
  selector: 'app-tag-selector',
  imports: [ReactiveFormsModule],
  templateUrl: './tag-selector.component.html',
  styleUrl: './tag-selector.component.css'
})
export class TagSelectorComponent implements OnInit {
  private tagService = inject(TagService);

  tagsNames = input.required<FormArray>();
  tags = signal<Tag[]>([]);
  isModalOpen = signal<boolean>(false);

  ngOnInit(): void {
    this.loadAllTags();
  }

  loadAllTags() {
    this.tagService.getAllTags().subscribe({
      next: (data) => {
        this.tags.set(data.content);
      },
      error: (erro) => {
        alert('Erro ao buscar tags. Por favor recarregue a página e tente novamente');
      }
    });
  }

  openTagModal() {
    this.isModalOpen.set(true);
  }

  closeTagModal() {
    this.isModalOpen.set(false);
  }

  isTagSelected(tagName: string): boolean {
    const tagList = this.tagsNames();
    return tagList.controls.some(control => control.value === tagName);
  }

  toggleTag(tagName: string) {
    const tagList = this.tagsNames();
    const index = tagList.controls.findIndex(control => control.value === tagName);

    if (index !== -1) {
      tagList.removeAt(index);
    } else {
      tagList.push(new FormControl(tagName));
    }
  }
}
