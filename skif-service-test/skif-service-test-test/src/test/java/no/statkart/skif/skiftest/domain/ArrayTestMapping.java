package no.statkart.skif.skiftest.domain;

import no.statkart.skif.mapper.Mapping;

/**
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public interface ArrayTestMapping extends Mapping {
    no.statkart.skif.skiftest.domain.array2.G d2w(no.statkart.skif.skiftest.domain.array1.G g);
    no.statkart.skif.skiftest.domain.array1.G w2d(no.statkart.skif.skiftest.domain.array2.G g);
}
