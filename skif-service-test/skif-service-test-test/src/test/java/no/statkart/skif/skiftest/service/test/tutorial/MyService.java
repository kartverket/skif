package no.statkart.skif.skiftest.service.test.tutorial;

import no.statkart.skif.skiftest.service.test.tutorial.domain.A;
import no.statkart.skif.skiftest.service.test.tutorial.domain.B;
import no.statkart.skif.skiftest.service.test.tutorial.domain.C;

interface MyService {
    C myMethod(A a, B b);
}
