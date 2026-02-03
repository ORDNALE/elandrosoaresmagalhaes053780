export interface PageRequest {
    page: number;
    size: number;
    sort?: string;
}

export interface Paged<T> {
    content: T[];
    page: number;
    size: number;
    total: number;
    pageCount: number;
}
