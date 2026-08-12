import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css'
})
export class PaginationComponent {
  isEmpty = input<boolean>(false);
  isFirst = input<boolean>(true);
  isLast = input<boolean>(false);
  pageSize = input<number>(0);
  currentPage = input<number>(0);
  numberOfElements = input<number>(0);
  totalElements = input<number>(0);
  totalPages = input<number>(0);

  pageChange = output<number>();

  onPageChange(page: number) {
    this.pageChange.emit(page);
  }
}
