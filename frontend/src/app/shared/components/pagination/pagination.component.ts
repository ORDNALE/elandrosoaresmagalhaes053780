import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css'
})
export class PaginationComponent implements OnChanges {
  @Input() page: number = 0;
  @Input() pageCount: number = 0;
  @Input() total: number = 0;
  @Input() pageSize: number = 12;
  @Output() pageChange = new EventEmitter<number>();

  pages: number[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['pageCount'] || changes['page']) {
      this.calculatePages();
    }
  }

  calculatePages(): void {
    const total = this.pageCount;
    const current = this.page;
    const maxPagesToShow = 5;

    let startPage: number, endPage: number;

    if (total <= maxPagesToShow) {
      // less than max pages so show all
      startPage = 0;
      endPage = total - 1;
    } else {
      // more than max pages so calculate start and end pages
      const middle = Math.floor(maxPagesToShow / 2);
      if (current <= middle) {
        startPage = 0;
        endPage = maxPagesToShow - 1;
      } else if (current + middle >= total) {
        startPage = total - maxPagesToShow;
        endPage = total - 1;
      } else {
        startPage = current - middle;
        endPage = current + middle;
      }
    }

    this.pages = Array.from(Array((endPage + 1) - startPage).keys()).map(i => startPage + i);
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.pageCount && page !== this.page) {
      this.pageChange.emit(page);
    }
  }

  getPageClasses(page: number): string {
    const isCurrent = page === this.page;
    const base = 'w-10 h-10 rounded-lg transition-all flex items-center justify-center text-sm font-medium border';

    if (isCurrent) {
      return `${base} bg-gradient-to-br from-orange-500 to-orange-600 border-orange-500 text-white shadow-lg shadow-orange-500/20`;
    }

    return `${base} bg-gray-800/50 border-gray-700 text-gray-400 hover:bg-gray-700 hover:text-white hover:border-gray-600`;
  }

  // Helper for template to avoid prop drilling Math
  get Math() {
    return Math;
  }
}
