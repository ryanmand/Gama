import dayjs from 'dayjs/esm';

export interface IAluno {
  id: number;
  nome?: string | null;
  email?: string | null;
  dataNascimento?: dayjs.Dayjs | null;
  telefone?: string | null;
}

export type NewAluno = Omit<IAluno, 'id'> & { id: null };
