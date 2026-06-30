export enum StateName {
  PARAIBA = 'PARAIBA',
  PERNAMBUCO = 'PERNAMBUCO',
  SERGIPE = 'SERGIPE',
  RIOGRANDEDOSUL = 'RIOGRANDEDOSUL',
  RIODEJANEIRO = 'RIODEJANEIRO',
  SAOPAULO = 'SAOPAULO'
}

export interface StateCreate {
  name: StateName;
}

export interface State {
  stateId: string;
  name: StateName;
}
