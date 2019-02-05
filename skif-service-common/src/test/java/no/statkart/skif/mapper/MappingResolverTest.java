package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.Test;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@SuppressWarnings("UnstableApiUsage")
public class MappingResolverTest {
    @SuppressWarnings("unused")
    private Collection<String> stringCollection;
    @SuppressWarnings("unused")
    private List<String> stringList;
    @SuppressWarnings("unused")
    private Function<String, String> stringStringFunction;
    @SuppressWarnings("unused")
    private UnaryOperator<String> stringUnaryOperator;
    @SuppressWarnings("unused")
    private UnaryOperator<?> stringUnaryOperator2;
    @SuppressWarnings("unused")
    private StringOperator stringOperator;

    /**
     * {@code T extends SuperClass} x {@code SubClass} = {@code SubClass}
     */
    @Test
    public void getSubtype_TypeVariable() throws NoSuchMethodException {
        Method method = MappingResolverTest.class.getDeclaredMethod("typeVariable");
        Type genericReturnType = method.getGenericReturnType();
        Type typeVariable = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
        Assertions.assertThat(typeVariable).isInstanceOf(TypeVariable.class);

        TypeToken<?> subtype = MappingResolver.getSubtype(TypeToken.of(typeVariable), Integer.class);
        Assertions.assertThat(subtype.getType()).isEqualTo(Integer.class);
    }

    /**
     * {@code ? extends SuperClass} x {@code SubClass} = {@code SubClass}
     */
    @Test
    public void getSubtype_WildcardType() throws NoSuchMethodException {
        Method method = MappingResolverTest.class.getDeclaredMethod("wildcardType");
        Type genericReturnType = method.getGenericReturnType();
        Type wildcardType = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
        Assertions.assertThat(wildcardType).isInstanceOf(WildcardType.class);

        TypeToken<?> subtype = MappingResolver.getSubtype(TypeToken.of(wildcardType), Integer.class);
        Assertions.assertThat(subtype.getType()).isEqualTo(Integer.class);
    }

    /**
     * {@code ? extends SuperClass} x {@code SubClass} = {@code SubClass}
     */
    @Test
    public void getSubtype_WildcardTypeWithTypeVariable() throws NoSuchMethodException {
        Method method = MappingResolverTest.class.getDeclaredMethod("wildcardTypeOfTypeVariable");
        Type genericReturnType = method.getGenericReturnType();
        Type wildcardType = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
        Assertions.assertThat(wildcardType).isInstanceOf(WildcardType.class);

        TypeToken<?> subtype = MappingResolver.getSubtype(TypeToken.of(wildcardType), Integer.class);
        Assertions.assertThat(subtype.getType()).isEqualTo(Integer.class);
    }

    @Test
    public void getSubtype_primitive() {
        TypeToken<?> typeToken = TypeToken.of(int.class);
        Assertions.assertThat(typeToken.getRawType().isPrimitive()).isTrue();

        TypeToken<?> primitiveSubtype = MappingResolver.getSubtype(typeToken, int.class);
        Assertions.assertThat(primitiveSubtype.getType()).isEqualTo(int.class);
    }

    /**
     * {@code Collection<String>} x {@code List} = {@code List<String>}
     */
    @Test
    public void getSubtype_sameTypeParameters() throws NoSuchFieldException {
        Field stringCollectionField = MappingResolverTest.class.getDeclaredField("stringCollection");
        Field stringListField = MappingResolverTest.class.getDeclaredField("stringList");

        TypeToken<?> typeToken = TypeToken.of(stringCollectionField.getGenericType());
        TypeToken<?> subtype = MappingResolver.getSubtype(typeToken, List.class);
        Assertions.assertThat((TypeToken) subtype).isEqualTo(TypeToken.of(stringListField.getGenericType()));
    }

    /**
     * {@code Function<String, String>} x {@code UnaryOperator} = {@code UnaryOperator<String>} fordi typeparameteren
     * til {@code UnaryOperator} blir bruk til å fylle inn begge typeparameterene til superinterfacet {@code Function}.
     */
    @Test
    public void getSubtype_lessTypeParameters() throws NoSuchFieldException {
        Field stringStringFunctionField = MappingResolverTest.class.getDeclaredField("stringStringFunction");
        Field stringUnaryOperatorField = MappingResolverTest.class.getDeclaredField("stringUnaryOperator");

        TypeToken<?> typeToken = TypeToken.of(stringStringFunctionField.getGenericType());
        TypeToken<?> subtype = MappingResolver.getSubtype(typeToken, UnaryOperator.class);
        Assertions.assertThat((TypeToken) subtype).isEqualTo(TypeToken.of(stringUnaryOperatorField.getGenericType()));
    }

    /**
     * {@code BubbleId<?>} x {@code SomeEnumId} = {@code SomeEnumId}. Typeparameteren til {@code BubbleId} blir låst
     * av {@code SomeEnumId}, som selv ikke har noen typeparametre.
     */
    @Test
    public void getSubtype_toNoTypeParameters() throws NoSuchFieldException {
        Field stringUnaryOperator2Field = MappingResolverTest.class.getDeclaredField("stringUnaryOperator2");
        Field stringOperatorField = MappingResolverTest.class.getDeclaredField("stringOperator");

        TypeToken<?> typeToken = TypeToken.of(stringUnaryOperator2Field.getGenericType());
        TypeToken<?> subtype = MappingResolver.getSubtype(typeToken, StringOperator.class);
        Assertions.assertThat((TypeToken) subtype).isEqualTo(TypeToken.of(stringOperatorField.getGenericType()));
    }

    private static Collection<? extends Number> wildcardType() {
        return null;
    }

    private static <T extends Number> Collection<? extends T> wildcardTypeOfTypeVariable() {
        return null;
    }

    private static <T extends Number> Collection<T> typeVariable() {
        return null;
    }

    private interface StringOperator extends UnaryOperator<String> {

    }
}