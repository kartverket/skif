package no.statkart.skif.mapper;

import com.google.inject.TypeLiteral;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Definerer mapping mellom Domain Objects og Web Service API Objects.
 * <p/>
 * I utgangspunktet trenger man ikke definere noen andre metoder enn de generiske, men custom mappere blir mer
 * kompakte dersom man definerer opp de mappingene de bruker.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public interface Mapping extends MappingBase {

    <T> T d2w(Object source, Class<T> targetClass);

    <T> T w2d(Object source, Class<T> targetClass);


    Object d2w(Object source, Type sourceType, Type targetType);

    Object w2d(Object source, Type sourceType, Type targetType);


    <T> T d2w(Object source, TypeLiteral<T> targetType);

    <T> T w2d(Object source, TypeLiteral<T> targetType);


    String d2w(String source);

    String w2d(String source);


    Integer d2w(Integer source);

    Integer w2d(Integer source);


    Long d2w(Long source);

    Long w2d(Long source);


    Boolean d2w(Boolean source);

    Boolean w2d(Boolean source);


    BigDecimal d2w(BigDecimal source);

    BigDecimal w2d(BigDecimal source);

}
