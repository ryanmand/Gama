import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAluno, NewAluno } from '../aluno.model';

export type PartialUpdateAluno = Partial<IAluno> & Pick<IAluno, 'id'>;

type RestOf<T extends IAluno | NewAluno> = Omit<T, 'dataNascimento'> & {
  dataNascimento?: string | null;
};

export type RestAluno = RestOf<IAluno>;

export type NewRestAluno = RestOf<NewAluno>;

export type PartialUpdateRestAluno = RestOf<PartialUpdateAluno>;

export type EntityResponseType = HttpResponse<IAluno>;
export type EntityArrayResponseType = HttpResponse<IAluno[]>;

@Injectable({ providedIn: 'root' })
export class AlunoService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/alunos');

  create(aluno: NewAluno): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(aluno);
    return this.http.post<RestAluno>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(aluno: IAluno): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(aluno);
    return this.http
      .put<RestAluno>(`${this.resourceUrl}/${this.getAlunoIdentifier(aluno)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(aluno: PartialUpdateAluno): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(aluno);
    return this.http
      .patch<RestAluno>(`${this.resourceUrl}/${this.getAlunoIdentifier(aluno)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestAluno>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAluno[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAlunoIdentifier(aluno: Pick<IAluno, 'id'>): number {
    return aluno.id;
  }

  compareAluno(o1: Pick<IAluno, 'id'> | null, o2: Pick<IAluno, 'id'> | null): boolean {
    return o1 && o2 ? this.getAlunoIdentifier(o1) === this.getAlunoIdentifier(o2) : o1 === o2;
  }

  addAlunoToCollectionIfMissing<Type extends Pick<IAluno, 'id'>>(
    alunoCollection: Type[],
    ...alunosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const alunos: Type[] = alunosToCheck.filter(isPresent);
    if (alunos.length > 0) {
      const alunoCollectionIdentifiers = alunoCollection.map(alunoItem => this.getAlunoIdentifier(alunoItem));
      const alunosToAdd = alunos.filter(alunoItem => {
        const alunoIdentifier = this.getAlunoIdentifier(alunoItem);
        if (alunoCollectionIdentifiers.includes(alunoIdentifier)) {
          return false;
        }
        alunoCollectionIdentifiers.push(alunoIdentifier);
        return true;
      });
      return [...alunosToAdd, ...alunoCollection];
    }
    return alunoCollection;
  }

  protected convertDateFromClient<T extends IAluno | NewAluno | PartialUpdateAluno>(aluno: T): RestOf<T> {
    return {
      ...aluno,
      dataNascimento: aluno.dataNascimento?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restAluno: RestAluno): IAluno {
    return {
      ...restAluno,
      dataNascimento: restAluno.dataNascimento ? dayjs(restAluno.dataNascimento) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestAluno>): HttpResponse<IAluno> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestAluno[]>): HttpResponse<IAluno[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
