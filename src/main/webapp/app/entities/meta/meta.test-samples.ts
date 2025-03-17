import { IMeta, NewMeta } from './meta.model';

export const sampleWithRequiredData: IMeta = {
  id: 17187,
  valor: 350,
  area: 'HUMANAS',
};

export const sampleWithPartialData: IMeta = {
  id: 24285,
  valor: 443,
  area: 'NATUREZA',
  descricao: 'seal tightly',
};

export const sampleWithFullData: IMeta = {
  id: 32703,
  valor: 176,
  area: 'LINGUAGENS',
  descricao: 'asset pace too',
};

export const sampleWithNewData: NewMeta = {
  valor: 497,
  area: 'MATEMATICA',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
