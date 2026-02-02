// API Error Response Structure
export interface ApiError {
  status: number;
  error: string;
  message: string;
  path: string;
  timestamp: string;
}

// Validation Error (for 400 Bad Request)
export interface ValidationError {
  field: string;
  message: string;
}

export interface ValidationErrorResponse extends ApiError {
  errors: ValidationError[];
}

// Error types
export enum ErrorType {
  UNAUTHORIZED = 'UNAUTHORIZED', // 401
  FORBIDDEN = 'FORBIDDEN', // 403
  NOT_FOUND = 'NOT_FOUND', // 404
  VALIDATION = 'VALIDATION', // 400
  SERVER_ERROR = 'SERVER_ERROR', // 500
  NETWORK_ERROR = 'NETWORK_ERROR',
  UNKNOWN = 'UNKNOWN'
}

// Standardized error for application use
export interface AppError {
  type: ErrorType;
  message: string;
  details?: string;
  validationErrors?: ValidationError[];
}
