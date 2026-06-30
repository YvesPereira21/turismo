export interface WarnCreate {
  name: string;
  description: string;
}

export interface Warn {
  id: string;
  name: string;
  description: string;
  eventDate?: string;
}
