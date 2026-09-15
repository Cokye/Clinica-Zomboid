import { TestBed } from '@angular/core/testing';
import { Convenio } from './convenio';

describe('Convenio', () => {
  let service: Convenio;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Convenio);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
